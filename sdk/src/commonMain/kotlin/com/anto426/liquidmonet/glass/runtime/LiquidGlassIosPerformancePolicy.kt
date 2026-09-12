package com.anto426.liquidmonet.glass.runtime

/**
 * Initial iOS sampling budget inferred from stable capabilities, not a measured calibration.
 * Kept platform-free so the policy can be regression-tested without an Apple test runner.
 */
internal object LiquidGlassIosPerformancePolicy {
    private const val GiB = 1024L * 1024L * 1024L

    fun qualityTier(device: LiquidGlassDeviceProfile, appleGpuFamily: Int): LiquidGlassQualityTier {
        // iOS has no Android app heap class. A zero appMemoryClassMb is unknown, not low RAM.
        val memoryCeiling = when {
            device.isLowRamDevice || device.totalMemoryBytes < 2L * GiB -> LiquidGlassQualityTier.MINIMAL
            device.totalMemoryBytes < 4L * GiB -> LiquidGlassQualityTier.BALANCED
            device.totalMemoryBytes < 7L * GiB -> LiquidGlassQualityTier.HIGH
            else -> LiquidGlassQualityTier.ULTRA
        }
        val cpuCeiling = when {
            device.cpuCoreCount <= 2 -> LiquidGlassQualityTier.BALANCED
            device.cpuCoreCount <= 4 || !device.is64Bit -> LiquidGlassQualityTier.HIGH
            else -> LiquidGlassQualityTier.ULTRA
        }
        // Feature families are supporting ceilings, not GPU timings. New Apple GPUs retain
        // support for older families, so they do not need a phone-model allowlist update.
        val gpuCeiling = when {
            !device.supportsRenderEffect -> LiquidGlassQualityTier.MINIMAL
            !device.supportsRuntimeShader -> LiquidGlassQualityTier.BALANCED
            appleGpuFamily >= 7 -> LiquidGlassQualityTier.ULTRA
            appleGpuFamily >= 4 -> LiquidGlassQualityTier.HIGH
            else -> LiquidGlassQualityTier.BALANCED
        }
        val familyCeiling = device.processorFamily?.effectiveCpuQualityCeiling ?: LiquidGlassQualityTier.ULTRA
        return minOf(minOf(memoryCeiling, cpuCeiling, gpuCeiling), familyCeiling)
    }
}
