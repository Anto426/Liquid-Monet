package com.anto426.antoui.glass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anto426.antoui.glass.runtime.AntoGlassPerformanceState
import com.anto426.antoui.glass.runtime.AntoGlassPreset
import com.anto426.antoui.glass.runtime.LocalAntoGlassPerformance
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow

/** Semantic roles used to keep all liquid-glass surfaces visually consistent. */
enum class AntoGlassRole {
    Control,
    Surface,
    Dialog,
    Sheet,
    Menu,
    Navigation
}

/** Unscaled optical values for one glass role. */
@Immutable
data class AntoGlassSurfaceStyle(
    val preset: AntoGlassPreset,
    val lightSurfaceAlpha: Float,
    val darkSurfaceAlpha: Float,
    val lightBrightness: Float,
    val saturation: Float,
    val highlightAlpha: Float,
    val shadowRadius: Dp,
    val shadowAlpha: Float,
    val innerShadowRadius: Dp,
    val innerShadowAlpha: Float
)

/** Single source of truth for the SDK's optical surface presets. */
object AntoGlassStyleManager {
    private val menu = AntoGlassSurfaceStyle(
        preset = AntoGlassPreset(14.dp, 18.dp, 32.dp, 0.18f),
        lightSurfaceAlpha = 0.26f,
        darkSurfaceAlpha = 0.20f,
        lightBrightness = 0.12f,
        saturation = 1.45f,
        highlightAlpha = 0.65f,
        shadowRadius = 20.dp,
        shadowAlpha = 0.18f,
        innerShadowRadius = 2.dp,
        innerShadowAlpha = 0.15f
    )

    fun resolve(role: AntoGlassRole, isLightSurface: Boolean): AntoGlassSurfaceStyle = menu
}

/**
 * Applies the shared optical pipeline and clips content with the exact same [shape].
 *
 * Automatically harmonizes optical transparency, Monet chromatic refraction, and fallback
 * acrylic frosted glass when rendered outside [AntoGlassScene].
 */
@Composable
fun Modifier.antoLiquidGlass(
    backdrop: Backdrop,
    shape: Shape,
    role: AntoGlassRole = AntoGlassRole.Surface,
    containerColor: Color? = null,
    preset: AntoGlassPreset? = null,
    performance: AntoGlassPerformanceState = LocalAntoGlassPerformance.current,
    layerBlock: (GraphicsLayerScope.() -> Unit)? = null
): Modifier {
    val colorScheme = MaterialTheme.colorScheme
    val isLightSurface = colorScheme.surface.luminance() > 0.5f
    val style = AntoGlassStyleManager.resolve(role, isLightSurface)
    val tokens = (preset ?: style.preset).resolve(performance)
    val defaultAlpha = if (isLightSurface) style.lightSurfaceAlpha else style.darkSurfaceAlpha
    val liquidStrength = performance.liquidIntensity.coerceIn(0f, 1f)

    // Ultra-subtle & crystal-clear Monet chromatic glass infusion (5% in dark, 7% in light)
    val subtleMonetTint = colorScheme.primary.copy(
        alpha = if (isLightSurface) 0.07f * (0.4f + 0.6f * liquidStrength)
                else 0.05f * (0.4f + 0.6f * liquidStrength)
    )

    val surfaceColor = if (containerColor != null) {
        containerColor
    } else if (backdrop == emptyBackdrop()) {
        colorScheme.surfaceContainerHigh.copy(alpha = if (isLightSurface) 0.88f else 0.82f)
    } else {
        colorScheme.surface.copy(alpha = defaultAlpha)
    }

    return drawBackdrop(
        backdrop = backdrop,
        shape = { shape },
        effects = {
            colorControls(
                brightness = if (isLightSurface) style.lightBrightness * liquidStrength else 0f,
                saturation = 1f + (style.saturation - 1f) * liquidStrength
            )
            if (tokens.blurRadius > 0.dp) {
                blur(tokens.blurRadius.toPx())
            }
            if (
                size.isSpecified &&
                size.minDimension > 0f &&
                tokens.refractionHeight > 0.dp &&
                tokens.refractionAmount > 0.dp
            ) {
                lens(
                    refractionHeight = tokens.refractionHeight.toPx(),
                    refractionAmount = tokens.refractionAmount.toPx(),
                    depthEffect = true,
                    chromaticAberration = tokens.chromaticAberration >= 0.08f
                )
            }
        },
        highlight = {
            Highlight.Plain.copy(
                alpha = style.highlightAlpha * (0.45f + 0.55f * liquidStrength)
            )
        },
        shadow = {
            Shadow(
                radius = style.shadowRadius,
                color = Color.Black.copy(alpha = style.shadowAlpha)
            )
        },
        innerShadow = {
            InnerShadow(
                radius = style.innerShadowRadius,
                alpha = style.innerShadowAlpha * (0.55f + 0.45f * liquidStrength)
            )
        },
        layerBlock = layerBlock,
        onDrawSurface = {
            if (surfaceColor.alpha > 0f) {
                drawRect(surfaceColor)
            }
            if (containerColor == null && subtleMonetTint.alpha > 0f) {
                drawRect(subtleMonetTint)
            }
        }
    )
}
