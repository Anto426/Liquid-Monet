package com.anto426.liquidmonet.glass.runtime

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Unscaled design values for a family of liquid-glass surfaces. */
@Immutable
data class LiquidGlassPreset(
    val blurRadius: Dp,
    val refractionHeight: Dp,
    val refractionAmount: Dp,
    val chromaticAberration: Float
) {
    /** Resolves this visual preset against current device and user intensity. */
    fun resolve(performance: LiquidGlassPerformanceState): LiquidGlassTokens = LiquidGlassTokens(
        blurRadius = blurRadius * performance.blurScale,
        refractionHeight = refractionHeight * performance.refractionScale,
        refractionAmount = refractionAmount * performance.refractionScale,
        chromaticAberration = chromaticAberration * performance.chromaticAberrationScale,
        motionScale = performance.motionScale,
        qualityTier = performance.qualityTier
    )
}

/** Final values which components can consume without repeating adaptation logic. */
@Immutable
data class LiquidGlassTokens(
    val blurRadius: Dp,
    val refractionHeight: Dp,
    val refractionAmount: Dp,
    val chromaticAberration: Float,
    val motionScale: Float,
    val qualityTier: LiquidGlassQualityTier
)

/** Shared visual presets; [Standard] preserves the SDK's existing glass defaults at full quality. */
object LiquidGlassPresets {
    val Subtle = LiquidGlassPreset(
        blurRadius = 2.dp,
        refractionHeight = 10.dp,
        refractionAmount = 16.dp,
        chromaticAberration = 0.08f
    )

    val Standard = LiquidGlassPreset(
        blurRadius = 14.dp,
        refractionHeight = 18.dp,
        refractionAmount = 32.dp,
        chromaticAberration = 0.18f
    )

    /** Reference-aligned navigation panel optics (8dp blur, symmetric 24dp lens). */
    val Navigation = LiquidGlassPreset(
        blurRadius = 8.dp,
        refractionHeight = 24.dp,
        refractionAmount = 24.dp,
        chromaticAberration = 0.14f
    )

    /** Compact moving lens used by thumbs and the navigation selection droplet. */
    val Interactive = LiquidGlassPreset(
        blurRadius = 8.dp,
        refractionHeight = 10.dp,
        refractionAmount = 14.dp,
        chromaticAberration = 0.18f
    )

    val Immersive = LiquidGlassPreset(
        blurRadius = 8.dp,
        refractionHeight = 24.dp,
        refractionAmount = 48.dp,
        chromaticAberration = 0.24f
    )
}
