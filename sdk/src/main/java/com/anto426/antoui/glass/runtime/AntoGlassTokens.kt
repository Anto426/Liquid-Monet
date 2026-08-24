package com.anto426.antoui.glass.runtime

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Unscaled design values for a family of liquid-glass surfaces. */
@Immutable
data class AntoGlassPreset(
    val blurRadius: Dp,
    val refractionHeight: Dp,
    val refractionAmount: Dp,
    val chromaticAberration: Float
) {
    /** Resolves this visual preset against current device and user intensity. */
    fun resolve(performance: AntoGlassPerformanceState): AntoGlassTokens = AntoGlassTokens(
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
data class AntoGlassTokens(
    val blurRadius: Dp,
    val refractionHeight: Dp,
    val refractionAmount: Dp,
    val chromaticAberration: Float,
    val motionScale: Float,
    val qualityTier: AntoGlassQualityTier
)

/** Shared visual presets; [Standard] preserves the SDK's existing glass defaults at full quality. */
object AntoGlassPresets {
    val Subtle = AntoGlassPreset(
        blurRadius = 2.dp,
        refractionHeight = 10.dp,
        refractionAmount = 16.dp,
        chromaticAberration = 0.08f
    )

    val Standard = AntoGlassPreset(
        blurRadius = 14.dp,
        refractionHeight = 18.dp,
        refractionAmount = 32.dp,
        chromaticAberration = 0.18f
    )

    val Immersive = AntoGlassPreset(
        blurRadius = 8.dp,
        refractionHeight = 24.dp,
        refractionAmount = 48.dp,
        chromaticAberration = 0.24f
    )
}

/** Standard adaptive tokens, installed automatically by `AntoUITheme`. */
val LocalAntoGlassTokens = staticCompositionLocalOf {
    AntoGlassPresets.Standard.resolve(AntoGlassPerformanceState.Fallback)
}
