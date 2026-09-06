package com.anto426.liquidmonet.glass.runtime

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

internal data class LiquidGlassDiagnostics(
    val availableMemoryBytes: Long = 0L,
    val memoryPressureHigh: Boolean = false,
    val powerSaveMode: Boolean = false,
    val thermalStatus: LiquidGlassThermalStatus = LiquidGlassThermalStatus.UNKNOWN
)

/** Owns platform observation only. No Compose state, rendering, benchmarking or persistence. */
internal class LiquidGlassDiagnosticsMonitor(
    private val context: Context,
    private val device: LiquidGlassDeviceProfile
) {
    private val refreshes = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    fun refresh() { refreshes.tryEmit(Unit) }

    // flowOn covers service lookup, callback registration/removal, and every diagnostic read.
    // OS callbacks only enqueue a conflated signal; they never perform a synchronous read.
    fun observe(): Flow<LiquidGlassDiagnostics> = callbackFlow {
        val power = context.getSystemService(PowerManager::class.java)
        val thermal = if (Build.VERSION.SDK_INT >= 29 && power != null) ThermalMonitorApi29(power) else null
        @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
        val callbacks = object : ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) { trySend(Unit) }
            override fun onLowMemory() { trySend(Unit) }
            override fun onTrimMemory(level: Int) { trySend(Unit) }
        }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) { trySend(Unit) }
        }
        val componentsRegistered = runCatching { context.registerComponentCallbacks(callbacks) }.isSuccess
        val receiverRegistered = runCatching {
            ContextCompat.registerReceiver(context, receiver,
                IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED), ContextCompat.RECEIVER_NOT_EXPORTED)
        }.isSuccess
        runCatching { thermal?.start { trySend(Unit) } }
        val requested = launch { refreshes.collect { trySend(Unit) } }
        // Callback ownership ends with collection, including cancellation during theme disposal.
        trySend(Unit)
        awaitClose {
            requested.cancel()
            runCatching { thermal?.close() }
            if (receiverRegistered) runCatching { context.unregisterReceiver(receiver) }
            if (componentsRegistered) runCatching { context.unregisterComponentCallbacks(callbacks) }
        }
    }.conflate().mapNotNull {
        try {
            val memory = ActivityManager.MemoryInfo()
            context.getSystemService(ActivityManager::class.java)?.getMemoryInfo(memory)
            val power = context.getSystemService(PowerManager::class.java)
            LiquidGlassDiagnostics(
                availableMemoryBytes = memory.availMem,
                memoryPressureHigh = memory.lowMemory || device.totalMemoryBytes > 0L &&
                    memory.availMem.toDouble() / device.totalMemoryBytes < 0.12,
                powerSaveMode = power?.isPowerSaveMode ?: false,
                thermalStatus = if (Build.VERSION.SDK_INT >= 29 && power != null) {
                    ThermalMonitorApi29.status(power.currentThermalStatus)
                } else LiquidGlassThermalStatus.UNKNOWN
            )
        } catch (_: Exception) { null }
    }.flowOn(Dispatchers.IO)
}

/** Keeps API 29 types out of the observer's class signature on Android 7-9. */
@RequiresApi(29)
private class ThermalMonitorApi29(private val power: PowerManager) {
    private var listener: PowerManager.OnThermalStatusChangedListener? = null

    fun start(onChanged: () -> Unit) {
        val callback = PowerManager.OnThermalStatusChangedListener { onChanged() }
        power.addThermalStatusListener(callback)
        listener = callback
    }

    fun close() {
        listener?.let(power::removeThermalStatusListener)
        listener = null
    }

    companion object {
        fun status(value: Int): LiquidGlassThermalStatus = when (value) {
            PowerManager.THERMAL_STATUS_NONE -> LiquidGlassThermalStatus.NONE
            PowerManager.THERMAL_STATUS_LIGHT -> LiquidGlassThermalStatus.LIGHT
            PowerManager.THERMAL_STATUS_MODERATE -> LiquidGlassThermalStatus.MODERATE
            PowerManager.THERMAL_STATUS_SEVERE -> LiquidGlassThermalStatus.SEVERE
            PowerManager.THERMAL_STATUS_CRITICAL -> LiquidGlassThermalStatus.CRITICAL
            PowerManager.THERMAL_STATUS_EMERGENCY -> LiquidGlassThermalStatus.EMERGENCY
            PowerManager.THERMAL_STATUS_SHUTDOWN -> LiquidGlassThermalStatus.SHUTDOWN
            else -> LiquidGlassThermalStatus.UNKNOWN
        }
    }
}
