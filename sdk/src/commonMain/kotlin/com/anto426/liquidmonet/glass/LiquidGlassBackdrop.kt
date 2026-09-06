package com.anto426.liquidmonet.glass

import androidx.compose.runtime.Composable
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.emptyBackdrop

/** Compatibility name for APIs that historically exposed their backdrop as state. */
typealias LiquidGlassBackdropState = Backdrop

/** Controls whether scene or explicitly supplied content is sampled first. */
enum class LiquidGlassBackdropPolicy {
    /** Prefer the backdrop installed by [LiquidGlassScene]. */
    SceneFirst,

    /** Prefer a local [LayerBackdrop] recorded by a custom component. */
    ExplicitFirst
}

/** Resolves the backdrop through one shared precedence rule for every SDK component. */
@Composable
internal fun resolveLiquidGlassBackdrop(
    backdrop: Backdrop = emptyBackdrop(),
    policy: LiquidGlassBackdropPolicy = LiquidGlassBackdropPolicy.SceneFirst
): Backdrop {
    val sceneBackdrop = LocalLiquidGlassContentBackdrop.current
    val explicitBackdrop = backdrop.takeIf { it != emptyBackdrop() }
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
