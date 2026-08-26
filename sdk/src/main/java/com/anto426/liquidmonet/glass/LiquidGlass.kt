package com.anto426.liquidmonet.glass

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPreset
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.shapes.RoundedRectangle

/**
 * LiquidGlass - Core Translucent Liquid Glass Container Primitive.
 * Neutral crystal glass surface without Monet color contamination.
 */
@Composable
fun LiquidGlass(
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    shape: Shape = RoundedRectangle(24.dp),
    blurRadius: Dp = 14.dp,
    refractionHeight: Dp = 18.dp,
    refractionAmount: Dp = 32.dp,
    containerColor: Color? = null,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    content: @Composable BoxScope.() -> Unit
) {
    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdrop, backdropState)
    val surfaceBackdrop = rememberLayerBackdrop()
    Box(
        modifier = modifier.liquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = LiquidGlassRole.Surface,
                containerColor = containerColor,
                preset = LiquidGlassPreset(
                    blurRadius = blurRadius,
                    refractionHeight = refractionHeight,
                    refractionAmount = refractionAmount,
                    chromaticAberration = 0.18f
                ),
                exportedBackdrop = surfaceBackdrop
            )
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop provides surfaceBackdrop
        ) {
            content()
        }
    }
}
