package com.anto426.liquidmonet.components.internal

import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex

/**
 * Local stacking levels for liquid components.
 *
 * Interactive glass is kept above passive containers in its immediate Compose parent. Keeping
 * this value small and local avoids changing the ordering of whole AnimatedContent destinations.
 */
internal object LiquidGlassZIndex {
    const val Interactive = 1f
    const val TopBar = 2f
    const val Toast = 100f
}

internal fun Modifier.liquidInteractiveZIndex(enabled: Boolean = true): Modifier =
    if (enabled) zIndex(LiquidGlassZIndex.Interactive) else this

internal fun Modifier.liquidTopBarZIndex(): Modifier =
    zIndex(LiquidGlassZIndex.TopBar)
