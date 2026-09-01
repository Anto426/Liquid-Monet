package com.anto426.liquidmonet.components.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.LocalLiquidGlassContainer
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop
import com.anto426.liquidmonet.glass.resolveLiquidGlassBackdrop
import com.anto426.liquidmonet.theme.LiquidGlassDefaults
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.shapes.RoundedRectangle

/**
 * LiquidCard - Liquid Glass Card with Snell optical refraction.
 *
 * Supports gelatin elastic deformation and white spotlight glow when interactive
 * (enabled automatically when [onClick] is provided or when [interactiveGelatin] is true).
 * Non-interactive container cards stay rock-solid to allow inner touch gestures like chart scrubbing.
 */
@Composable
fun LiquidCard(
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    shape: Shape = RoundedRectangle(24.dp),
    containerColor: Color? = null,
    contentColor: Color = Color.Unspecified,
    contentPadding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    interactiveGelatin: Boolean = (onClick != null),
    drawHighlightOverlay: Boolean = true,
    highlightColor: Color = Color.Unspecified,
    content: @Composable BoxScope.() -> Unit
) {
    val isInteractive = interactiveGelatin || (onClick != null)
    val interactiveHighlight = rememberLiquidControlHighlight()
    val colorScheme = MaterialTheme.colorScheme
    val resolvedContentColor = if (contentColor.isSpecified) {
        contentColor
    } else {
        LiquidGlassDefaults.contentColorFor(containerColor, colorScheme)
    }
    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdrop, backdropState)
    val surfaceBackdrop = rememberLayerBackdrop()
    val cardLayerBlock = liquidControlLayerBlock(isInteractive, interactiveHighlight)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isInteractive && cardLayerBlock != null) {
                    Modifier.graphicsLayer(cardLayerBlock)
                } else {
                    Modifier
                }
            )
            .then(
                if (isInteractive) {
                    Modifier.liquidControlPressFeedback(
                        enabled = true,
                        interactiveHighlight = interactiveHighlight,
                        shape = shape,
                        drawHighlightOverlay = drawHighlightOverlay,
                        highlightColor = highlightColor
                    )
                } else Modifier
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = null,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        // Optical glass surface
        Box(
            modifier = Modifier
                .matchParentSize()
                .liquidGlass(
                    backdrop = effectiveBackdrop,
                    shape = shape,
                    role = LiquidGlassRole.Surface,
                    containerColor = containerColor,
                    exportedBackdrop = surfaceBackdrop
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            CompositionLocalProvider(
                LocalContentColor provides resolvedContentColor,
                LocalLiquidGlassContentBackdrop provides surfaceBackdrop,
                LocalLiquidGlassContainer provides true
            ) {
                content()
            }
        }
    }
}
