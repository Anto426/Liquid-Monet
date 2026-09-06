package com.anto426.liquidmonet.glass.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import com.kyant.backdrop.RuntimeShaderPrewarm
import kotlinx.coroutines.withTimeoutOrNull

@Composable
internal actual fun rememberLiquidGlassPerformanceState(
    liquidIntensity: Float,
    maximumQuality: LiquidGlassQualityTier,
    reduceMotion: Boolean
): State<LiquidGlassPerformanceState?> = produceState(null, liquidIntensity, maximumQuality, reduceMotion) {
    // Programs are prepared once on Default; recreation only awaits the same process-owned job.
    // This compiles Skia source, not a Metal pipeline or a device performance score.
    withTimeoutOrNull(1_000L) { RuntimeShaderPrewarm.prepare() }
    value = LiquidGlassPerformanceState.Fallback.copy(
        liquidIntensity = if (liquidIntensity.isFinite()) liquidIntensity.coerceIn(0f, 1f) else 1f,
        // iOS has a Skia backend, but no calibrated device probe yet. Retain its existing
        // conservative profile until that backend can be measured on an Apple device.
        qualityTier = LiquidGlassQualityTier.MINIMAL,
        motionScale = if (reduceMotion) 0f else LiquidGlassPerformanceState.Fallback.motionScale
    )
}
