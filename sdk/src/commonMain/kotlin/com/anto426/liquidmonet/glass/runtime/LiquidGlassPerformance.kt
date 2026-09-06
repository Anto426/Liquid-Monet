package com.anto426.liquidmonet.glass.runtime

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/** Rendering quality calibrated once and retained for this device. */
enum class LiquidGlassQualityTier {
    MINIMAL,
    BALANCED,
    HIGH,
    ULTRA
}

/** Platform-independent view of Android's thermal status. */
enum class LiquidGlassThermalStatus {
    UNKNOWN,
    NONE,
    LIGHT,
    MODERATE,
    SEVERE,
    CRITICAL,
    EMERGENCY,
    SHUTDOWN
}

/** Stable hardware capabilities which do not normally change during a process lifetime. */
@Immutable
data class LiquidGlassDeviceProfile(
    val sdkInt: Int,
    val supportsRenderEffect: Boolean,
    val supportsRuntimeShader: Boolean,
    val isLowRamDevice: Boolean,
    val totalMemoryBytes: Long,
    val appMemoryClassMb: Int,
    val cpuCoreCount: Int,
    val is64Bit: Boolean,
    /** Per-core maximum clock, when exposed by the OS; missing clocks are not treated as zero. */
    val cpuMaxFrequenciesKhz: List<Long> = emptyList(),
    val displayWidthPixels: Int = 0,
    val displayHeightPixels: Int = 0,
    val displayRefreshRateHz: Float = 60f,
    val displayDensity: Float = 1f,
    val socModel: String = ""
)

/**
 * Calibrated values consumed by liquid-glass components. Pressure fields are diagnostics;
 * they do not change the selected appearance during a session or on subsequent launches.
 *
 * Every scale is normalized to `0f..1f`. A zero refraction or chromatic-aberration
 * scale is also the explicit signal that the device cannot run the required AGSL shader.
 */
@Immutable
data class LiquidGlassPerformanceState(
    val device: LiquidGlassDeviceProfile,
    val qualityTier: LiquidGlassQualityTier,
    val thermalStatus: LiquidGlassThermalStatus,
    val isPowerSaveMode: Boolean,
    val isMemoryPressureHigh: Boolean,
    val availableMemoryBytes: Long,
    val liquidIntensity: Float,
    val blurScale: Float,
    val refractionScale: Float,
    val motionScale: Float,
    val chromaticAberrationScale: Float,
    val calibration: LiquidGlassCalibration? = null,
    /** Requested material fidelity; device speed changes sampling resolution, not this value. */
    val opticalQualityTier: LiquidGlassQualityTier? = null,
    val renderResolutionScale: Float = 1f
) {
    companion object {
        /** Safe degradation used when a component is rendered outside [com.anto426.liquidmonet.theme.LiquidMonetTheme]. */
        val Fallback = LiquidGlassPerformanceState(
            device = LiquidGlassDeviceProfile(
                sdkInt = 24,
                supportsRenderEffect = false,
                supportsRuntimeShader = false,
                isLowRamDevice = true,
                totalMemoryBytes = 0L,
                appMemoryClassMb = 0,
                cpuCoreCount = 1,
                is64Bit = false
            ),
            qualityTier = LiquidGlassQualityTier.MINIMAL,
            thermalStatus = LiquidGlassThermalStatus.UNKNOWN,
            isPowerSaveMode = false,
            isMemoryPressureHigh = false,
            availableMemoryBytes = 0L,
            liquidIntensity = 1f,
            blurScale = 0f,
            refractionScale = 0f,
            motionScale = 1f,
            chromaticAberrationScale = 0f
        )
    }
}

/** Current adaptive liquid-glass state, installed automatically by `LiquidMonetTheme`. */
val LocalLiquidGlassPerformance = staticCompositionLocalOf {
    LiquidGlassPerformanceState.Fallback
}
