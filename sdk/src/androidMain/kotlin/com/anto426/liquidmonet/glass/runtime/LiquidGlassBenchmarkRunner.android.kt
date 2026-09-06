package com.anto426.liquidmonet.glass.runtime

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Runs independent CPU and render probes within capability limits. Owns no UI or files. */
internal object LiquidGlassBenchmarkRunner {
    @androidx.annotation.WorkerThread
    suspend fun measure(context: Context, device: LiquidGlassDeviceProfile): LiquidGlassCalibration {
        if (LiquidGlassCalibrationPolicy.capabilityCeiling(device) == LiquidGlassQualityTier.MINIMAL) {
            return fallback(LiquidGlassCalibrationSource.CAPABILITY_LIMIT)
        }
        return try {
            val memory = ActivityManager.MemoryInfo()
            context.getSystemService(ActivityManager::class.java)?.getMemoryInfo(memory)
            val power = context.getSystemService(PowerManager::class.java)
            val hot = Build.VERSION.SDK_INT >= 29 && (power?.currentThermalStatus ?: 0) >= PowerManager.THERMAL_STATUS_MODERATE
            if (memory.lowMemory || memory.availMem < 128L * 1024 * 1024 || power?.isPowerSaveMode == true || hot) {
                return fallback(LiquidGlassCalibrationSource.CONSTRAINED_START)
            }
            val cpu = withContext(Dispatchers.Default) { LiquidGlassCpuProbe.measure() }
            val ceiling = LiquidGlassCalibrationPolicy.cpuCeiling(device, cpu.first, cpu.second)
            if (ceiling == LiquidGlassQualityTier.MINIMAL || Build.VERSION.SDK_INT < 31) {
                fallback(LiquidGlassCalibrationSource.MEASURED).copy(
                    cpuWorkP90Nanos = cpu.first, memoryCopyP90Nanos = cpu.second
                )
            } else {
                LiquidGlassRenderProbe.measure(device, ceiling).copy(
                    cpuWorkP90Nanos = cpu.first, memoryCopyP90Nanos = cpu.second
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w("LiquidGlassCalibration", "Calibration unavailable; retaining conservative profile", e)
            fallback(LiquidGlassCalibrationSource.TEST_UNAVAILABLE)
        } catch (e: LinkageError) {
            Log.w("LiquidGlassCalibration", "Graphics backend unavailable", e)
            fallback(LiquidGlassCalibrationSource.TEST_UNAVAILABLE)
        } catch (_: OutOfMemoryError) {
            fallback(LiquidGlassCalibrationSource.TEST_UNAVAILABLE)
        }
    }

    private fun fallback(source: LiquidGlassCalibrationSource) =
        LiquidGlassCalibration(LiquidGlassQualityTier.MINIMAL, source)

}
