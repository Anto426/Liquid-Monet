package com.anto426.liquidmonet.glass

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
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPerformanceState
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPreset
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPresets
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow

/** Semantic roles used to keep all liquid-glass surfaces visually consistent. */
enum class LiquidGlassRole {
    Control,
    Surface,
    Dialog,
    Sheet,
    Menu,
    TopBar,
    Navigation
}

/** Unscaled optical values for one glass role. */
@Immutable
data class LiquidGlassSurfaceStyle(
    val preset: LiquidGlassPreset,
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
object LiquidGlassStyleManager {
    private val control = LiquidGlassSurfaceStyle(
        preset = LiquidGlassPresets.Standard,
        lightSurfaceAlpha = 0.20f,
        darkSurfaceAlpha = 0.15f,
        lightBrightness = 0.06f,
        saturation = 1.22f,
        highlightAlpha = 0.48f,
        shadowRadius = 10.dp,
        shadowAlpha = 0.11f,
        innerShadowRadius = 1.dp,
        innerShadowAlpha = 0.10f
    )

    private val surface = LiquidGlassSurfaceStyle(
        preset = LiquidGlassPresets.Navigation,
        lightSurfaceAlpha = 0.20f,
        darkSurfaceAlpha = 0.16f,
        lightBrightness = 0.10f,
        saturation = 1.35f,
        highlightAlpha = 0.58f,
        shadowRadius = 16.dp,
        shadowAlpha = 0.15f,
        innerShadowRadius = 2.dp,
        innerShadowAlpha = 0.12f
    )

    private val dialog = LiquidGlassSurfaceStyle(
        preset = LiquidGlassPresets.Navigation,
        lightSurfaceAlpha = 0.30f,
        darkSurfaceAlpha = 0.26f,
        lightBrightness = 0.14f,
        saturation = 1.40f,
        highlightAlpha = 0.72f,
        shadowRadius = 28.dp,
        shadowAlpha = 0.26f,
        innerShadowRadius = 2.dp,
        innerShadowAlpha = 0.18f
    )

    private val sheet = LiquidGlassSurfaceStyle(
        preset = LiquidGlassPresets.Navigation,
        lightSurfaceAlpha = 0.30f,
        darkSurfaceAlpha = 0.26f,
        lightBrightness = 0.12f,
        saturation = 1.38f,
        highlightAlpha = 0.68f,
        shadowRadius = 24.dp,
        shadowAlpha = 0.22f,
        innerShadowRadius = 2.dp,
        innerShadowAlpha = 0.16f
    )

    private val menu = LiquidGlassSurfaceStyle(
        preset = LiquidGlassPresets.Navigation,
        lightSurfaceAlpha = 0.20f,
        darkSurfaceAlpha = 0.16f,
        lightBrightness = 0.12f,
        saturation = 1.45f,
        highlightAlpha = 0.65f,
        shadowRadius = 20.dp,
        shadowAlpha = 0.18f,
        innerShadowRadius = 2.dp,
        innerShadowAlpha = 0.15f
    )

    private val navigation = LiquidGlassSurfaceStyle(
        preset = LiquidGlassPresets.Navigation,
        lightSurfaceAlpha = 0.20f,
        darkSurfaceAlpha = 0.16f,
        lightBrightness = 0.10f,
        saturation = 1.35f,
        highlightAlpha = 0.62f,
        shadowRadius = 18.dp,
        shadowAlpha = 0.18f,
        innerShadowRadius = 2.dp,
        innerShadowAlpha = 0.14f
    )

    private val topBar = LiquidGlassSurfaceStyle(
        preset = LiquidGlassPresets.Navigation,
        lightSurfaceAlpha = 0.20f,
        darkSurfaceAlpha = 0.16f,
        lightBrightness = 0.08f,
        saturation = 1.30f,
        highlightAlpha = 0f,
        shadowRadius = 0.dp,
        shadowAlpha = 0f,
        innerShadowRadius = 0.dp,
        innerShadowAlpha = 0f
    )

    fun resolve(role: LiquidGlassRole): LiquidGlassSurfaceStyle = when (role) {
        LiquidGlassRole.Control -> control
        LiquidGlassRole.Surface -> surface
        LiquidGlassRole.Dialog -> dialog
        LiquidGlassRole.Sheet -> sheet
        LiquidGlassRole.Menu -> menu
        LiquidGlassRole.TopBar -> topBar
        LiquidGlassRole.Navigation -> navigation
    }
}

/**
 * Resolves the backdrop once for every SDK component.
 *
 * Inside [LiquidGlassScene], the scene-installed backdrop wins because it represents the surface
 * that is actually behind the component in its current layer (base content, card, or overlay).
 * Explicit values remain the fallback for standalone use outside a scene.
 */
@Composable
internal fun resolveLiquidGlassBackdrop(
    backdrop: Backdrop,
    backdropState: Backdrop = backdrop
): Backdrop {
    val sceneBackdrop = LocalLiquidGlassContentBackdrop.current
    return when {
        sceneBackdrop != null && sceneBackdrop != emptyBackdrop() -> sceneBackdrop
        backdropState != emptyBackdrop() -> backdropState
        backdrop != emptyBackdrop() -> backdrop
        else -> emptyBackdrop()
    }
}

/**
 * Applies the shared optical pipeline and clips content with the exact same [shape].
 *
 * Automatically harmonizes optical transparency, Monet chromatic refraction, and fallback
 * acrylic frosted glass when rendered outside [LiquidGlassScene].
 */
@Composable
fun Modifier.liquidGlass(
    backdrop: Backdrop,
    shape: Shape,
    role: LiquidGlassRole = LiquidGlassRole.Surface,
    containerColor: Color? = null,
    preset: LiquidGlassPreset? = null,
    performance: LiquidGlassPerformanceState = LocalLiquidGlassPerformance.current,
    layerBlock: (GraphicsLayerScope.() -> Unit)? = null,
    exportedBackdrop: LayerBackdrop? = null
): Modifier {
    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdrop)
    val colorScheme = MaterialTheme.colorScheme
    val isLightSurface = colorScheme.surface.luminance() > 0.5f
    val style = LiquidGlassStyleManager.resolve(role)
    val tokens = (preset ?: style.preset).resolve(performance)
    val defaultAlpha = if (isLightSurface) style.lightSurfaceAlpha else style.darkSurfaceAlpha
    val liquidStrength = performance.liquidIntensity.coerceIn(0f, 1f)

    // Ultra-subtle & crystal-clear Monet chromatic glass infusion (5% in dark, 7% in light)
    val subtleMonetTint = colorScheme.primary.copy(
        alpha = if (isLightSurface) 0.07f * (0.4f + 0.6f * liquidStrength)
                else 0.05f * (0.4f + 0.6f * liquidStrength)
    )

    val surfaceColor = containerColor ?: colorScheme.surface.copy(alpha = defaultAlpha)

    return drawBackdrop(
        backdrop = effectiveBackdrop,
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
        highlight = if (style.highlightAlpha > 0f) {
            {
                Highlight.Plain.copy(
                    alpha = style.highlightAlpha * (0.45f + 0.55f * liquidStrength)
                )
            }
        } else {
            null
        },
        shadow = if (style.shadowRadius > 0.dp && style.shadowAlpha > 0f) {
            {
                Shadow(
                    radius = style.shadowRadius,
                    color = Color.Black.copy(alpha = style.shadowAlpha)
                )
            }
        } else {
            null
        },
        innerShadow = if (style.innerShadowRadius > 0.dp && style.innerShadowAlpha > 0f) {
            {
                InnerShadow(
                    radius = style.innerShadowRadius,
                    alpha = style.innerShadowAlpha * (0.55f + 0.45f * liquidStrength)
                )
            }
        } else {
            null
        },
        layerBlock = layerBlock,
        exportedBackdrop = exportedBackdrop,
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
