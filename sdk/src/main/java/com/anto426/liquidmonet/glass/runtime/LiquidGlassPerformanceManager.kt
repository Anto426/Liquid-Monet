package com.anto426.liquidmonet.glass.runtime

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import androidx.annotation.FloatRange
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat

/**
 * Observes device capability and transient system pressure, then exposes a Compose [State].
 *
 * The manager retains only [Context.getApplicationContext] and owns every callback it registers.
 * Call [start] when the state becomes visible and [close] when its lifecycle ends. `LiquidMonetTheme`
 * does this automatically.
 */
@Stable
class LiquidGlassPerformanceManager(
    context: Context,
    @FloatRange(from = 0.0, to = 1.0)
    liquidIntensity: Float = 1f
) : AutoCloseable {

    private val applicationContext = context.applicationContext
    private val activityManager = applicationContext.getSystemService(ActivityManager::class.java)
    private val powerManager = applicationContext.getSystemService(PowerManager::class.java)
    private val mainHandler = Handler(Looper.getMainLooper())
    private val deviceProfile = readDeviceProfile()
    // Hardware capabilities are process-stable for this manager. Cache the choice so a refresh
    // can never accidentally select a different visual profile during the current session.
    private val selectedQualityTier = selectQualityTier(deviceProfile)
    private val thermalMonitor = createThermalMonitor(powerManager)

    private var normalizedLiquidIntensity = normalizeIntensity(liquidIntensity)
    private var observing = false
    private var componentCallbacksRegistered = false
    private var powerReceiverRegistered = false

    private val mutableState = mutableStateOf(readState())

    /** The hardware-selected glass profile plus current pressure diagnostics. */
    val state: State<LiquidGlassPerformanceState>
        get() = mutableState

    private val refreshRunnable = Runnable {
        if (observing) updateStateNow()
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    private val componentCallbacks = object : ComponentCallbacks2 {
        override fun onConfigurationChanged(newConfig: Configuration) = requestRefresh()

        override fun onLowMemory() = requestRefresh()

        override fun onTrimMemory(level: Int) = requestRefresh()
    }

    private val powerSaveReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
                requestRefresh()
            }
        }
    }

    /** Starts system observation. Calling this more than once is safe. */
    fun start() {
        runOnMain {
            if (observing) return@runOnMain
            observing = true

            applicationContext.registerComponentCallbacks(componentCallbacks)
            componentCallbacksRegistered = true

            ContextCompat.registerReceiver(
                applicationContext,
                powerSaveReceiver,
                IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            powerReceiverRegistered = true

            thermalMonitor?.start(::requestRefresh)
            updateStateNow()
        }
    }

    /**
     * Changes the global liquid strength. Non-finite input falls back to `1f`; all other values
     * are clamped to `0f..1f`.
     */
    fun setLiquidIntensity(@FloatRange(from = 0.0, to = 1.0) value: Float) {
        val normalized = normalizeIntensity(value)
        runOnMain {
            if (normalizedLiquidIntensity == normalized) return@runOnMain
            normalizedLiquidIntensity = normalized
            updateStateNow()
        }
    }

    /** Re-reads memory, battery-saver and thermal pressure immediately on the main thread. */
    fun refresh() {
        runOnMain(::updateStateNow)
    }

    /** Removes every callback registered by [start]. Calling this more than once is safe. */
    override fun close() {
        runOnMain {
            if (!observing) return@runOnMain
            observing = false
            mainHandler.removeCallbacks(refreshRunnable)
            thermalMonitor?.close()

            if (powerReceiverRegistered) {
                applicationContext.unregisterReceiver(powerSaveReceiver)
                powerReceiverRegistered = false
            }
            if (componentCallbacksRegistered) {
                applicationContext.unregisterComponentCallbacks(componentCallbacks)
                componentCallbacksRegistered = false
            }
        }
    }

    private fun requestRefresh() {
        if (!observing) return
        mainHandler.removeCallbacks(refreshRunnable)
        mainHandler.post(refreshRunnable)
    }

    private fun runOnMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            mainHandler.post(block)
        }
    }

    private fun updateStateNow() {
        mutableState.value = readState()
    }

    private fun readDeviceProfile(): LiquidGlassDeviceProfile {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)
        return LiquidGlassDeviceProfile(
            sdkInt = Build.VERSION.SDK_INT,
            supportsRenderEffect = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
            supportsRuntimeShader = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU,
            isLowRamDevice = activityManager?.isLowRamDevice ?: true,
            totalMemoryBytes = memoryInfo.totalMem,
            appMemoryClassMb = activityManager?.memoryClass ?: 0,
            cpuCoreCount = Runtime.getRuntime().availableProcessors().coerceAtLeast(1),
            is64Bit = Build.SUPPORTED_64_BIT_ABIS.isNotEmpty()
        )
    }

    private fun readState(): LiquidGlassPerformanceState {
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memoryInfo)
        val availableMemoryBytes = memoryInfo.availMem
        val memoryPressureHigh = memoryInfo.lowMemory ||
            deviceProfile.totalMemoryBytes > 0L &&
            availableMemoryBytes.toDouble() / deviceProfile.totalMemoryBytes.toDouble() < 0.12
        val powerSaveMode = powerManager?.isPowerSaveMode ?: false
        val thermalStatus = thermalMonitor?.currentStatus() ?: LiquidGlassThermalStatus.UNKNOWN

        // The visual profile is selected from stable hardware capabilities only. Thermal and
        // memory values remain available as diagnostics, but must not make the UI switch profile
        // while the device is running.
        val baseScales = scalesFor(selectedQualityTier)
        val intensity = normalizedLiquidIntensity

        return LiquidGlassPerformanceState(
            device = deviceProfile,
            qualityTier = selectedQualityTier,
            thermalStatus = thermalStatus,
            isPowerSaveMode = powerSaveMode,
            isMemoryPressureHigh = memoryPressureHigh,
            availableMemoryBytes = availableMemoryBytes,
            liquidIntensity = intensity,
            blurScale = if (deviceProfile.supportsRenderEffect) baseScales.blur * intensity else 0f,
            refractionScale = if (deviceProfile.supportsRuntimeShader) baseScales.refraction * intensity else 0f,
            // Optical intensity must not change interaction timing. A user lowering glass
            // strength should not make every navigation and control animation snap faster.
            motionScale = baseScales.motion,
            chromaticAberrationScale = if (deviceProfile.supportsRuntimeShader) {
                baseScales.chromaticAberration * intensity
            } else {
                0f
            }
        )
    }

    private data class PerformanceScales(
        val blur: Float,
        val refraction: Float,
        val motion: Float,
        val chromaticAberration: Float
    )

    private companion object {
        private const val GIB = 1024L * 1024L * 1024L
        private fun normalizeIntensity(value: Float): Float =
            if (value.isFinite()) value.coerceIn(0f, 1f) else 1f

        private fun selectQualityTier(device: LiquidGlassDeviceProfile): LiquidGlassQualityTier = when {
            !device.supportsRenderEffect ||
                device.isLowRamDevice ||
                device.cpuCoreCount <= 2 ||
                device.totalMemoryBytes in 1L until 2L * GIB -> LiquidGlassQualityTier.MINIMAL

            device.supportsRuntimeShader &&
                device.is64Bit &&
                device.cpuCoreCount >= 8 &&
                device.totalMemoryBytes >= 8 * GIB &&
                device.appMemoryClassMb >= 256 -> LiquidGlassQualityTier.ULTRA

            device.supportsRuntimeShader &&
                device.cpuCoreCount >= 4 &&
                device.totalMemoryBytes >= 4 * GIB -> LiquidGlassQualityTier.HIGH

            else -> LiquidGlassQualityTier.BALANCED
        }

        private fun scalesFor(tier: LiquidGlassQualityTier): PerformanceScales = when (tier) {
            LiquidGlassQualityTier.MINIMAL -> PerformanceScales(
                blur = 0.4f,
                refraction = 0.2f,
                motion = 0.5f,
                chromaticAberration = 0f
            )

            LiquidGlassQualityTier.BALANCED -> PerformanceScales(
                blur = 0.7f,
                refraction = 0.55f,
                motion = 0.75f,
                chromaticAberration = 0.35f
            )

            LiquidGlassQualityTier.HIGH -> PerformanceScales(
                blur = 0.9f,
                refraction = 0.85f,
                motion = 0.9f,
                chromaticAberration = 0.75f
            )

            LiquidGlassQualityTier.ULTRA -> PerformanceScales(
                blur = 1f,
                refraction = 1f,
                motion = 1f,
                chromaticAberration = 1f
            )
        }

        private fun createThermalMonitor(powerManager: PowerManager?): ThermalMonitor? =
            if (powerManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ThermalMonitorApi29(powerManager)
            } else {
                null
            }
    }
}

private interface ThermalMonitor {
    fun start(onChanged: () -> Unit)
    fun currentStatus(): LiquidGlassThermalStatus
    fun close()
}

/** Keeps API 29 types out of the manager's class signature on Android 7-9. */
@RequiresApi(Build.VERSION_CODES.Q)
private class ThermalMonitorApi29(
    private val powerManager: PowerManager
) : ThermalMonitor {
    private var onChanged: (() -> Unit)? = null
    private var registered = false
    private val listener = PowerManager.OnThermalStatusChangedListener {
        onChanged?.invoke()
    }

    override fun start(onChanged: () -> Unit) {
        if (registered) return
        this.onChanged = onChanged
        powerManager.addThermalStatusListener(listener)
        registered = true
    }

    override fun currentStatus(): LiquidGlassThermalStatus = when (powerManager.currentThermalStatus) {
        PowerManager.THERMAL_STATUS_NONE -> LiquidGlassThermalStatus.NONE
        PowerManager.THERMAL_STATUS_LIGHT -> LiquidGlassThermalStatus.LIGHT
        PowerManager.THERMAL_STATUS_MODERATE -> LiquidGlassThermalStatus.MODERATE
        PowerManager.THERMAL_STATUS_SEVERE -> LiquidGlassThermalStatus.SEVERE
        PowerManager.THERMAL_STATUS_CRITICAL -> LiquidGlassThermalStatus.CRITICAL
        PowerManager.THERMAL_STATUS_EMERGENCY -> LiquidGlassThermalStatus.EMERGENCY
        PowerManager.THERMAL_STATUS_SHUTDOWN -> LiquidGlassThermalStatus.SHUTDOWN
        else -> LiquidGlassThermalStatus.UNKNOWN
    }

    override fun close() {
        if (!registered) return
        powerManager.removeThermalStatusListener(listener)
        registered = false
        onChanged = null
    }
}
