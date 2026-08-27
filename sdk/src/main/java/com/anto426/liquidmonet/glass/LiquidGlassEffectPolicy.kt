package com.anto426.liquidmonet.glass

import androidx.compose.runtime.Immutable
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPerformanceState
import com.anto426.liquidmonet.glass.runtime.LiquidGlassQualityTier
import com.anto426.liquidmonet.glass.runtime.LiquidGlassThermalStatus
import com.anto426.liquidmonet.glass.runtime.LiquidGlassTokens
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPresets
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * One global rendering decision shared by every Liquid Glass renderer.
 *
 * Components provide only their semantic [role]. They do not choose individual quality levels;
 * the active theme profile decides which effects are safe for that role.
 */
@Immutable
internal data class LiquidGlassEffectPolicy(
    val blur: Boolean,
    val refraction: Boolean,
    val chromaticAberration: Boolean,
    val highlight: Boolean,
    val shadow: Boolean,
    val innerShadow: Boolean,
    val toggleRefractionHeightScale: Float = 0.5f,
    val toggleRefractionAmountScale: Float = 10f / 14f,
    val navigationDropletStartStrength: Float = 0.28f,
    val navigationDropletEndStrength: Float = 0.52f,
    val interactiveHighlightWidth: Dp = 1.2.dp,
    val interactiveHighlightBlurRadius: Dp = 2.dp,
    val toggleHighlightScale: Float = 1.5f
)

/** Resolves the one effect budget used by both public surfaces and internal renderers. */
internal fun LiquidGlassPerformanceState.effectPolicy(
    role: LiquidGlassRole,
    interactive: Boolean = false
): LiquidGlassEffectPolicy {
    val tier = qualityTier
    val canRenderEffects = tier != LiquidGlassQualityTier.MINIMAL
    val runtimeConstrained = isPowerSaveMode ||
        isMemoryPressureHigh ||
        thermalStatus == LiquidGlassThermalStatus.MODERATE ||
        thermalStatus == LiquidGlassThermalStatus.SEVERE ||
        thermalStatus == LiquidGlassThermalStatus.CRITICAL ||
        thermalStatus == LiquidGlassThermalStatus.EMERGENCY ||
        thermalStatus == LiquidGlassThermalStatus.SHUTDOWN
    val isLargeSurface = role == LiquidGlassRole.Surface ||
        role == LiquidGlassRole.Dialog ||
        role == LiquidGlassRole.Sheet ||
        role == LiquidGlassRole.Menu
    val isNavigationInteraction = role == LiquidGlassRole.Navigation && interactive
    val isCompactFunctionalSurface = role == LiquidGlassRole.Control ||
        role == LiquidGlassRole.Navigation ||
        role == LiquidGlassRole.TopBar
    val canUseInteractiveLens = !runtimeConstrained &&
        refractionScale > 0f &&
        (tier == LiquidGlassQualityTier.BALANCED ||
            tier == LiquidGlassQualityTier.HIGH ||
            tier == LiquidGlassQualityTier.ULTRA)
    val canUseStaticCompactLens = !runtimeConstrained &&
        refractionScale > 0f &&
        (tier == LiquidGlassQualityTier.HIGH || tier == LiquidGlassQualityTier.ULTRA)
    val canUseLens = canRenderEffects && (
        isLargeSurface ||
            (isCompactFunctionalSurface && canUseStaticCompactLens) ||
            (interactive && canUseInteractiveLens)
        )

    return LiquidGlassEffectPolicy(
        blur = canRenderEffects && (role != LiquidGlassRole.Control || interactive),
        refraction = canUseLens,
        chromaticAberration = !runtimeConstrained &&
            tier == LiquidGlassQualityTier.ULTRA && canUseLens,
        highlight = canRenderEffects,
        shadow = canRenderEffects && (isLargeSurface || isNavigationInteraction),
        innerShadow = !runtimeConstrained && tier == LiquidGlassQualityTier.ULTRA &&
            (isLargeSurface || isNavigationInteraction)
    )
}

/** Resolves optical dimensions from the same role policy used by the surface renderer. */
internal fun LiquidGlassPerformanceState.effectTokens(
    role: LiquidGlassRole,
    interactive: Boolean = false
): LiquidGlassTokens {
    val preset = if (interactive) {
        LiquidGlassPresets.Interactive
    } else {
        LiquidGlassStyleManager.resolve(role).preset
    }
    return preset.resolve(this)
}

internal val LiquidGlassPerformanceState.animateBackground: Boolean
    get() = qualityTier != LiquidGlassQualityTier.MINIMAL &&
        !isPowerSaveMode &&
        !isMemoryPressureHigh &&
        thermalStatus != LiquidGlassThermalStatus.MODERATE &&
        thermalStatus != LiquidGlassThermalStatus.SEVERE &&
        thermalStatus != LiquidGlassThermalStatus.CRITICAL &&
        thermalStatus != LiquidGlassThermalStatus.EMERGENCY &&
        thermalStatus != LiquidGlassThermalStatus.SHUTDOWN

internal val LiquidGlassPerformanceState.renderDetailedBackground: Boolean
    get() = qualityTier == LiquidGlassQualityTier.ULTRA && animateBackground
