package com.anto426.liquidmonet.glass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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

/** Shared stacking order for optical layers. It applies locally within each Compose parent. */
internal fun LiquidGlassRole.layerZIndex(): Float = when (this) {
    LiquidGlassRole.Surface -> 0f
    // Controls and navigation items must keep their parent's natural order. Raising every item
    // globally breaks AnimatedContent and can place old/new destinations over each other.
    LiquidGlassRole.TopBar,
    LiquidGlassRole.Control,
    LiquidGlassRole.Navigation -> 0f
    LiquidGlassRole.Menu -> 10f
    LiquidGlassRole.Dialog,
    LiquidGlassRole.Sheet -> 20f
}

/**
 * Selects which backdrop wins when a custom glass component is hosted inside a scene.
 *
 * [SceneFirst] preserves the automatic SDK behavior. [ExplicitFirst] is intended for custom
 * components that record a local [LayerBackdrop] and need to refract that exact layer.
 */
enum class LiquidGlassBackdropPolicy {
    SceneFirst,
    ExplicitFirst
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
        preset = LiquidGlassPresets.Subtle,
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
        lightSurfaceAlpha = 0.12f,
        darkSurfaceAlpha = 0.22f,
        lightBrightness = 0.14f,
        saturation = 1.40f,
        highlightAlpha = 0.72f,
        shadowRadius = 28.dp,
        shadowAlpha = 0.26f,
        innerShadowRadius = 2.dp,
        innerShadowAlpha = 0.18f
    )

    // Dialog and sheet use the same modal optics; their placement is the only visual difference.
    private val sheet = dialog.copy()

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
    backdropState: Backdrop = backdrop,
    policy: LiquidGlassBackdropPolicy = LiquidGlassBackdropPolicy.SceneFirst
): Backdrop {
    val sceneBackdrop = LocalLiquidGlassContentBackdrop.current
    val explicitBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        backdrop != emptyBackdrop() -> backdrop
        else -> null
    }
    return when (policy) {
        LiquidGlassBackdropPolicy.SceneFirst -> when {
            sceneBackdrop != null && sceneBackdrop != emptyBackdrop() -> sceneBackdrop
            explicitBackdrop != null -> explicitBackdrop
            else -> emptyBackdrop()
        }
        LiquidGlassBackdropPolicy.ExplicitFirst -> when {
            explicitBackdrop != null -> explicitBackdrop
            sceneBackdrop != null && sceneBackdrop != emptyBackdrop() -> sceneBackdrop
            else -> emptyBackdrop()
        }
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
    exportedBackdrop: LayerBackdrop? = null,
    backdropPolicy: LiquidGlassBackdropPolicy = LiquidGlassBackdropPolicy.SceneFirst
): Modifier {
    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdrop, policy = backdropPolicy)
    val colorScheme = MaterialTheme.colorScheme
    val isLightSurface = colorScheme.surface.luminance() > 0.5f
    val style = LiquidGlassStyleManager.resolve(role)
    val tokens = (preset ?: style.preset).resolve(performance)
    val defaultAlpha = if (isLightSurface) style.lightSurfaceAlpha else style.darkSurfaceAlpha
    val liquidStrength = performance.liquidIntensity.coerceIn(0f, 1f)

    // Keep the material optically neutral. Monet is only an ambient reflection here; explicit
    // selected/prominent controls provide their own tint and must not have it added twice.
    val subtleMonetTint = colorScheme.primary.copy(
        alpha = if (isLightSurface) 0.03f * (0.35f + 0.65f * liquidStrength)
                else 0.02f * (0.35f + 0.65f * liquidStrength)
    )

    val isModalSurface = role == LiquidGlassRole.Dialog || role == LiquidGlassRole.Sheet
    val defaultSurfaceColor = when {
        // Modal glass is intentionally a little darker than navigation without becoming opaque.
        // In light themes a restrained neutral veil darkens the refracted scene; in dark themes
        // the existing surface tone already provides that contribution.
        isModalSurface && isLightSurface -> Color.Black.copy(alpha = defaultAlpha)
        else -> colorScheme.surface.copy(alpha = defaultAlpha)
    }
    val surfaceColor = containerColor ?: defaultSurfaceColor

    // A container already paid for the optical backdrop. Keep descendants visually alive, but
    // turn their glass into a cheap clipped tint so a row of controls does not multiply the same
    // full-frame blur/lens work.
    if (LocalLiquidGlassContainer.current) {
        val groupedSurfaceColor = containerColor ?: Color.Transparent
        return this
            .then(if (layerBlock != null) Modifier.graphicsLayer(layerBlock) else Modifier)
            .clip(shape)
            .zIndex(role.layerZIndex())
            .drawWithContent {
                if (groupedSurfaceColor.alpha > 0f) {
                    drawRect(groupedSurfaceColor)
                }
                drawContent()
            }
    }

    // Every renderer consumes one global policy. The component contributes only its semantic
    // role; it cannot silently select a different quality profile for itself.
    val effectPolicy = performance.effectPolicy(
        role = role,
        interactive = layerBlock != null
    )
    val useBlur = effectPolicy.blur && tokens.blurRadius > 0.dp
    val useRefraction = effectPolicy.refraction &&
        tokens.refractionHeight > 0.dp && tokens.refractionAmount > 0.dp
    val useChromaticAberration = effectPolicy.chromaticAberration &&
        tokens.chromaticAberration >= 0.08f
    val useHighlight = effectPolicy.highlight && style.highlightAlpha > 0f
    val useShadow = effectPolicy.shadow && style.shadowRadius > 0.dp && style.shadowAlpha > 0f
    val useInnerShadow = effectPolicy.innerShadow &&
        style.innerShadowRadius > 0.dp && style.innerShadowAlpha > 0f

    return drawBackdrop(
        backdrop = effectiveBackdrop,
        shape = { shape },
        effects = {
            // Like a physical lens, a larger piece of glass reads as a thicker material. Scale
            // the optical displacement from the rendered geometry instead of assigning every
            // capsule, toolbar and sheet the same apparent thickness.
            val compactReference = 48.dp.toPx()
            val largeReference = 240.dp.toPx()
            val sizeProgress = if (size.isSpecified) {
                ((size.minDimension - compactReference) / (largeReference - compactReference))
                    .coerceIn(0f, 1f)
            } else {
                0f
            }
            val opticalThickness = when (role) {
                LiquidGlassRole.Control,
                LiquidGlassRole.Navigation -> 0.84f + 0.16f * sizeProgress
                LiquidGlassRole.TopBar -> 0.92f + 0.16f * sizeProgress
                LiquidGlassRole.Surface,
                LiquidGlassRole.Menu,
                LiquidGlassRole.Dialog,
                LiquidGlassRole.Sheet -> 0.96f + 0.22f * sizeProgress
            }

            colorControls(
                brightness = if (isLightSurface) style.lightBrightness * liquidStrength else 0f,
                saturation = 1f + (style.saturation - 1f) * liquidStrength
            )
            if (useBlur) {
                blur(tokens.blurRadius.toPx() * opticalThickness)
            }
            if (
                useRefraction &&
                size.isSpecified &&
                size.minDimension > 0f &&
                tokens.refractionHeight > 0.dp &&
                tokens.refractionAmount > 0.dp
            ) {
                lens(
                    refractionHeight = tokens.refractionHeight.toPx() * opticalThickness,
                    refractionAmount = tokens.refractionAmount.toPx() * opticalThickness,
                    depthEffect = true,
                    chromaticAberration = useChromaticAberration
                )
            }
        },
        highlight = if (useHighlight) {
            {
                Highlight.Plain.copy(
                    alpha = style.highlightAlpha * (0.45f + 0.55f * liquidStrength)
                )
            }
        } else {
            null
        },
        shadow = if (useShadow) {
            {
                Shadow(
                    radius = style.shadowRadius,
                    color = Color.Black.copy(alpha = style.shadowAlpha)
                )
            }
        } else {
            null
        },
        innerShadow = if (useInnerShadow) {
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
            // Every Liquid Monet surface receives the same restrained chromatic veil. Explicit
            // neutral/custom surface colors may tune opacity, but must not silently disable Monet.
            if (role != LiquidGlassRole.TopBar && subtleMonetTint.alpha > 0f) {
                drawRect(subtleMonetTint)
            }
        }
    ).zIndex(role.layerZIndex())
}
