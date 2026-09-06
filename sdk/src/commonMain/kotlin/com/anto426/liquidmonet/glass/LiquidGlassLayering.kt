package com.anto426.liquidmonet.glass

import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex

/**
 * Explicit local layers for hosts and custom controls that know their direct Compose parent.
 *
 * The stable glass renderer never changes stacking order by semantic role. A renderer cannot know
 * whether it is crossing an `AnimatedContent`, clipped parent or portal boundary.
 */
internal object LiquidGlassZIndex {
    const val Interactive = 1f
    const val TopBar = 2f
    const val Toast = 100f
    const val Menu = 1_000_000f
    const val Modal = 2_000_000f
}

internal fun Modifier.liquidInteractiveZIndex(enabled: Boolean = true): Modifier =
    if (enabled) zIndex(LiquidGlassZIndex.Interactive) else this

internal fun Modifier.liquidTopBarZIndex(): Modifier =
    zIndex(LiquidGlassZIndex.TopBar)
