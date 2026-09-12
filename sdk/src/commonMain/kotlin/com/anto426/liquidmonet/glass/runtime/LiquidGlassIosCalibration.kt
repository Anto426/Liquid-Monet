package com.anto426.liquidmonet.glass.runtime

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

/** Owns only SDK files; implementations must bound reads and atomically replace records. */
internal interface LiquidGlassIosCalibrationStore {
    fun read(key: String): LiquidGlassCalibrationRecord?
    fun save(record: LiquidGlassCalibrationRecord): Boolean
    fun beginWarmup(signature: String): Boolean
    fun finishWarmup()
}

/**
 * One operation per process, independent of theme cancellation. Workers never write records:
 * a native GPU call may outlive the deadline, but its late result cannot replace the fallback.
 */
internal class LiquidGlassIosCalibration(
    private val scope: CoroutineScope,
    private val store: LiquidGlassIosCalibrationStore,
    private val key: String,
    private val capabilityTier: LiquidGlassQualityTier,
    private val measure: suspend () -> LiquidGlassCalibration,
    private val timeoutMillis: Long = 4_000L
) {
    private val mutex = Mutex()
    private var loading: Deferred<LiquidGlassCalibration>? = null

    suspend fun load(): LiquidGlassCalibration {
        val operation = mutex.withLock {
            loading ?: scope.async { loadOnce() }.also { loading = it }
        }
        return operation.await()
    }

    private suspend fun loadOnce(): LiquidGlassCalibration {
        val saved = store.read(key)
        saved?.calibration?.let { return it }
        val result = when {
            // A pending record means the previous process died in native code. Do not repeat it.
            saved != null -> estimate()
            !store.save(LiquidGlassCalibrationRecord(key, null)) -> estimate()
            else -> {
                val worker = scope.async {
                    try { measure() }
                    catch (error: CancellationException) { throw error }
                    catch (_: Exception) { estimate() }
                }
                val measured = withTimeoutOrNull(timeoutMillis) { worker.await() }
                if (measured == null) worker.cancel()
                measured?.takeIf(::validMeasurement) ?: estimate()
            }
        }
        store.save(LiquidGlassCalibrationRecord(key, result))
        return result
    }

    private fun validMeasurement(result: LiquidGlassCalibration): Boolean =
        result.source == LiquidGlassCalibrationSource.MEASURED &&
            result.qualityTier <= capabilityTier &&
            result.cpuWorkP90Nanos > 0 && result.memoryCopyP90Nanos > 0 &&
            result.renderP90Nanos > 0 && result.renderSampleCount >= 5 &&
            result.renderWidthPixels > 0 && result.renderHeightPixels > 0

    private fun estimate() = LiquidGlassCalibration(capabilityTier, LiquidGlassCalibrationSource.CAPABILITY_ESTIMATE)
}

/** Version only the iOS workload/backend; Android's persisted profiles are unaffected. */
internal fun liquidGlassIosDeviceKey(
    machine: String,
    gpu: String,
    device: LiquidGlassDeviceProfile,
    simulator: Boolean,
    workloadVersion: Int = 1
): String {
    // FNV-1a is a stable local identity checksum, not a security primitive or a user identifier.
    val identity = listOf(
        "ios-skia-metal", workloadVersion, simulator, machine, gpu, device.totalMemoryBytes,
        device.cpuCoreCount, device.supportsRenderEffect, device.supportsRuntimeShader,
        minOf(device.displayWidthPixels, device.displayHeightPixels),
        maxOf(device.displayWidthPixels, device.displayHeightPixels),
        device.displayRefreshRateHz, device.displayDensity
    ).joinToString("|")
    var hash = 0xcbf29ce484222325uL
    for (byte in identity.encodeToByteArray()) hash = (hash xor byte.toUByte().toULong()) * 0x100000001b3uL
    return hash.toString(16)
}
