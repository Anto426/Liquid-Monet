package com.anto426.antoui.components.cards

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.anto426.antoui.glass.runtime.AntoGlassPreset
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.RoundedRectangle

/**
 * AntoCard - Liquid Glass Card with Snell optical refraction and organic gelatin physics.
 */
@Composable
fun AntoCard(
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    isGlass: Boolean = true,
    shape: Shape = RoundedRectangle(24.dp),
    blurRadius: Dp = 14.dp,
    refractionHeight: Dp = 18.dp,
    refractionAmount: Dp = 32.dp,
    containerColor: Color? = null,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    contentPadding: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    interactiveGelatin: Boolean = (onClick != null),
    highlightColor: Color = Color.Unspecified,
    content: @Composable BoxScope.() -> Unit
) {
    val isInteractive = interactiveGelatin && (onClick != null)
    val interactiveHighlight = rememberAntoControlHighlight()
    val colorScheme = MaterialTheme.colorScheme
    val hostContentBackdrop = com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        backdrop != emptyBackdrop() -> backdrop
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> emptyBackdrop()
    }
    val effectiveHighlightColor = if (highlightColor != Color.Unspecified) highlightColor else (containerColor ?: colorScheme.primary)

    if (isGlass) {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .antoLiquidGlass(
                        backdrop = effectiveBackdrop,
                        shape = shape,
                        role = AntoGlassRole.Surface,
                        containerColor = containerColor,
                        preset = AntoGlassPreset(
                            blurRadius = blurRadius,
                            refractionHeight = refractionHeight,
                            refractionAmount = refractionAmount,
                            chromaticAberration = 0.18f
                        ),
                        layerBlock = if (isInteractive) antoControlLayerBlock(true, interactiveHighlight) else null
                    )
                    .then(
                        if (isInteractive) {
                            Modifier.antoControlPressFeedback(
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
                    .padding(contentPadding),
                content = content
            )
        }
    } else {
        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(containerColor ?: MaterialTheme.colorScheme.surface)
                    .then(
                        if (onClick != null) {
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onClick
                            )
                        } else Modifier
                    )
                    .padding(contentPadding),
                content = content
            )
        }
    }
}
