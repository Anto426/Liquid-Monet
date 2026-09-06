package com.anto426.liquidmonet.glass.internal

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.anto426.liquidmonet.glass.runtime.LiquidGlassDynamicHighlight
import com.anto426.liquidmonet.glass.runtime.LiquidGlassDynamicPreset
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPerformanceState
import com.anto426.liquidmonet.glass.runtime.effectPolicy
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow

/**
 * Canonical renderer for glass whose optics change continuously during an interaction.
 *
 * This is intentionally separate from the public `liquidGlass` modifier: public surfaces have a
 * stable role and style, while thumbs, droplets and masks interpolate their optical response in
 * the draw phase. Both paths still consume the same performance policy.
 */
internal fun Modifier.liquidGlassDynamic(
    backdrop: Backdrop,
    shape: Shape,
    preset: LiquidGlassDynamicPreset,
    performance: LiquidGlassPerformanceState,
    effectProgress: () -> Float,
    decorationProgress: () -> Float = effectProgress,
    layerBlock: (GraphicsLayerScope.() -> Unit)? = null,
    onDrawBackdrop: DrawScope.(drawBackdrop: DrawScope.() -> Unit) -> Unit = { it() },
    onDrawSurface: (DrawScope.() -> Unit)? = null
): Modifier {
    val policy = performance.effectPolicy(role = preset.role, interactive = true)
    val tokens = preset.optics.resolve(performance)

    return drawBackdrop(
        backdrop = backdrop,
        resolutionScale = performance.renderResolutionScale,
        shape = { shape },
        effects = {
            val progress = effectProgress().coerceIn(0f, 1f)
            if (preset.vibrancy && policy.blur) {
                vibrancy()
            }

            val blurStrength = lerp(preset.blurAtRest, preset.blurWhenActive, progress)
                .coerceAtLeast(0f)
            val blurRadius = tokens.blurRadius * blurStrength
            if (policy.blur && blurRadius > 0.dp) {
                blur(blurRadius.toPx())
            }

            val refractionStrength = lerp(
                preset.refractionAtRest,
                preset.refractionWhenActive,
                progress
            ).coerceAtLeast(0f)
            val refractionHeight = tokens.refractionHeight *
                preset.refractionHeightScale * refractionStrength
            val refractionAmount = tokens.refractionAmount *
                preset.refractionAmountScale * refractionStrength
            if (
                policy.refraction &&
                size.isSpecified &&
                size.minDimension > 0f &&
                refractionHeight > 0.dp &&
                refractionAmount > 0.dp
            ) {
                lens(
                    refractionHeight = refractionHeight.toPx(),
                    refractionAmount = refractionAmount.toPx(),
                    depthEffect = preset.depthEffect,
                    chromaticAberration = policy.chromaticAberration &&
                        tokens.chromaticAberration >= 0.08f
                )
            }
        },
        highlight = if (policy.highlight && preset.highlight != LiquidGlassDynamicHighlight.None) {
            {
                val progress = decorationProgress().coerceIn(0f, 1f)
                val alpha = lerp(
                    preset.highlightAtRest,
                    preset.highlightWhenActive,
                    progress
                ).coerceIn(0f, 1f)
                when (preset.highlight) {
                    LiquidGlassDynamicHighlight.None -> null
                    LiquidGlassDynamicHighlight.Ambient -> Highlight.Ambient.copy(
                        width = preset.highlightWidth,
                        blurRadius = preset.highlightBlurRadius,
                        alpha = alpha
                    )
                    LiquidGlassDynamicHighlight.Directional -> Highlight.Default.copy(
                        width = preset.highlightWidth,
                        blurRadius = preset.highlightBlurRadius,
                        alpha = alpha
                    )
                }
            }
        } else {
            null
        },
        shadow = if (
            policy.shadow && preset.shadowRadius > 0.dp && preset.shadowColorAlpha > 0f
        ) {
            {
                val progress = decorationProgress().coerceIn(0f, 1f)
                Shadow(
                    radius = preset.shadowRadius,
                    color = Color.Black.copy(alpha = preset.shadowColorAlpha),
                    alpha = lerp(
                        preset.shadowAtRest,
                        preset.shadowWhenActive,
                        progress
                    ).coerceIn(0f, 1f)
                )
            }
        } else {
            null
        },
        innerShadow = if (policy.innerShadow && preset.innerShadowRadius > 0.dp) {
            {
                val progress = decorationProgress().coerceIn(0f, 1f)
                val alpha = lerp(
                    preset.innerShadowAtRest,
                    preset.innerShadowWhenActive,
                    progress
                ).coerceIn(0f, 1f)
                InnerShadow(
                    radius = preset.innerShadowRadius * alpha,
                    alpha = alpha
                )
            }
        } else {
            null
        },
        layerBlock = layerBlock,
        onDrawBackdrop = onDrawBackdrop,
        onDrawSurface = onDrawSurface
    )
}
