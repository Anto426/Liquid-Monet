package com.anto426.liquidmonet.components.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.resolveLiquidGlassBackdrop
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.shapes.RoundedRectangle

/**
 * LiquidCard - Liquid Glass Card with Snell optical refraction and organic gelatin physics.
 */
@Composable
fun LiquidCard(
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    shape: Shape = RoundedRectangle(24.dp),
    containerColor: Color? = null,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    contentPadding: androidx.compose.ui.unit.Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    interactiveGelatin: Boolean = (onClick != null),
    highlightColor: Color = Color.Unspecified,
    content: @Composable BoxScope.() -> Unit
) {
    val isInteractive = interactiveGelatin && (onClick != null)
    val interactiveHighlight = rememberLiquidControlHighlight()
    val colorScheme = MaterialTheme.colorScheme
    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdrop, backdropState)
    val surfaceBackdrop = rememberLayerBackdrop()
    val effectiveHighlightColor = if (highlightColor != Color.Unspecified) highlightColor else (containerColor ?: colorScheme.primary)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = LiquidGlassRole.Surface,
                containerColor = containerColor,
                layerBlock = if (isInteractive) {
                    liquidControlLayerBlock(true, interactiveHighlight)
                } else {
                    null
                },
                exportedBackdrop = surfaceBackdrop
            )
            .then(
                if (isInteractive) {
                    Modifier.liquidControlPressFeedback(
                        enabled = true,
                        interactiveHighlight = interactiveHighlight,
                        drawHighlightOverlay = true,
                        highlightColor = effectiveHighlightColor
                    )
                } else Modifier
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(contentPadding)
    ) {
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalLiquidGlassContentBackdrop provides surfaceBackdrop
        ) {
            content()
        }
    }
}
