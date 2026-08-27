package com.anto426.liquidmonet.glass

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPreset
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.shapes.RoundedRectangle

/**
 * Groups several liquid controls behind one optical surface.
 *
 * The container owns the expensive backdrop sampling. Children keep their own tint, clipping,
 * press feedback and accessibility semantics, but skip redundant blur/refraction passes while
 * hosted inside this container.
 */
@Composable
fun LiquidGlassContainer(
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    shape: Shape = RoundedRectangle(24.dp),
    role: LiquidGlassRole = LiquidGlassRole.Surface,
    containerColor: Color? = null,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    preset: LiquidGlassPreset? = null,
    contentAlignment: Alignment = Alignment.TopStart,
    backdropPolicy: LiquidGlassBackdropPolicy = LiquidGlassBackdropPolicy.SceneFirst,
    content: @Composable BoxScope.() -> Unit
) {
    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdrop, backdropState, backdropPolicy)
    val surfaceBackdrop = rememberLayerBackdrop()

    Box(
        // Keep the host itself unclipped. The optical surface below still clips to [shape], but
        // a pressed or dragged child is free to squash beyond the container and draw over its
        // siblings instead of being cut at the panel edge.
        modifier = modifier.zIndex(role.layerZIndex()),
        contentAlignment = contentAlignment
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .liquidGlass(
                    backdrop = effectiveBackdrop,
                    shape = shape,
                    role = role,
                    containerColor = containerColor,
                    preset = preset,
                    exportedBackdrop = surfaceBackdrop,
                    backdropPolicy = backdropPolicy
                )
        )

        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalLiquidGlassContentBackdrop provides surfaceBackdrop,
            LocalLiquidGlassContainer provides true
        ) {
            content()
        }
    }
}

/** True while descendants are already covered by a shared liquid-glass surface. */
internal val LocalLiquidGlassContainer = staticCompositionLocalOf { false }
