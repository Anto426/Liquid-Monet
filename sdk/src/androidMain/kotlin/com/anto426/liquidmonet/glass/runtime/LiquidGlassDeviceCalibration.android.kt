package com.anto426.liquidmonet.glass.runtime

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

internal data class LiquidGlassCalibratedDevice(
    val device: LiquidGlassDeviceProfile,
    val calibration: LiquidGlassCalibration
)

/** One calibration per process and installation, shared by all themes/windows. Never uses the main thread. */
internal object LiquidGlassDeviceCalibration {
    private val mutex = Mutex()
    private val workerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var loading: Deferred<LiquidGlassCalibratedDevice>? = null

    suspend fun load(context: Context): LiquidGlassCalibratedDevice {
        val request = mutex.withLock {
            loading ?: workerScope.async { loadDevice(context.applicationContext) }.also { loading = it }
        }
        // Cancelling a theme (rotation, recreation, another window) must not interrupt the
        // process-owned calibration or mistake it for a failed test on the next composition.
        return request.await()
    }

    private suspend fun loadDevice(context: Context): LiquidGlassCalibratedDevice =
        withContext(Dispatchers.IO) {
            val device = try { LiquidGlassHardwareProbe.read(context) } catch (_: Exception) {
                LiquidGlassPerformanceState.Fallback.device
            }
            val key = LiquidGlassHardwareProbe.key(device)
            val store = LiquidGlassCalibrationStore(context)
            val saved = store.read(key)
            val calibration = when {
                saved?.calibration != null -> saved.calibration
                saved != null -> fallback(LiquidGlassCalibrationSource.INTERRUPTED)
                else -> {
                    // Commit before native rendering: after a driver crash or process kill the
                    // next launch uses a saved conservative result instead of crashing in a loop.
                    if (!store.save(LiquidGlassCalibrationRecord(key, null))) {
                        fallback(LiquidGlassCalibrationSource.TEST_UNAVAILABLE)
                    } else {
                        val worker = workerScope.async {
                            LiquidGlassBenchmarkRunner.measure(context, device)
                        }
                        val measured = withTimeoutOrNull(2_500L) { worker.await() }
                        if (measured == null) worker.cancel()
                        // Native driver calls cannot always be interrupted. Their eventual result
                        // is discarded: only this coordinator writes the permanent profile.
                        measured ?: fallback(LiquidGlassCalibrationSource.TEST_UNAVAILABLE)
                    }
                }
            }
            if (saved?.calibration == null) store.save(LiquidGlassCalibrationRecord(key, calibration))
            Log.i("LiquidGlassCalibration", "${if (saved?.calibration != null) "Loaded" else "Saved"} profile: " +
                "${calibration.qualityTier}, ${calibration.source}; CPU=${calibration.cpuWorkP90Nanos}ns, " +
                "memory=${calibration.memoryCopyP90Nanos}ns, render=${calibration.renderP90Nanos}ns")
            val calibrated = LiquidGlassCalibratedDevice(device, calibration)
            // Shader preparation is per process, separate from the permanent hardware score.
            // Waiting is bounded even if a native driver ignores coroutine cancellation.
            val warmup = workerScope.async { LiquidGlassShaderWarmup.prepare(context, calibrated) }
            if (withTimeoutOrNull(1_500L) { warmup.await(); true } != true) warmup.cancel()
            calibrated
        }

    private fun fallback(source: LiquidGlassCalibrationSource) =
        LiquidGlassCalibration(LiquidGlassQualityTier.MINIMAL, source)
}
