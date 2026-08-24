package com.anto426.antoui.components.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule

/**
 * AntoFloatingActionButton - Pure Crystal Liquid Glass Floating Action Button.
 * Uses the exact same 1:1 optical liquid glass pipeline as AntoDialog & AntoSheet
 * with Snell lens refraction, chromatic dispersion, specular highlights, and 3D shadows.
 */
@Composable
fun AntoFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    visible: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    enabled: Boolean = true,
    containerColor: Color? = null,
    shape: Shape = Capsule(),
    content: @Composable () -> Unit
) {
    val interactiveHighlight = rememberAntoControlHighlight()
    val contentColor = MaterialTheme.colorScheme.onSurface
    val hostContentBackdrop = com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> backdrop
    }

    val fabOffsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 160.dp,
        animationSpec = spring(
            dampingRatio = 0.85f,
            stiffness = 400f
        ),
        label = "fabOffsetY"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = fabOffsetY.toPx()
            }
            .size(size)
            .antoLiquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = AntoGlassRole.Navigation,
                containerColor = containerColor,
                layerBlock = antoControlLayerBlock(enabled && visible, interactiveHighlight)
            )
            .clickable(
                interactionSource = null,
                indication = null,
                role = Role.Button,
                enabled = enabled && visible,
                onClick = onClick
            )
            .antoControlPressFeedback(enabled && visible, interactiveHighlight),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalContentColor provides (if (enabled) contentColor else contentColor.copy(alpha = 0.38f))
        ) {
            content()
        }
    }
}

/**
 * AntoExtendedFloatingActionButton - Extended Crystal Liquid Glass FAB with icon and text.
 */
@Composable
fun AntoExtendedFloatingActionButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    expanded: Boolean = true,
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    containerColor: Color? = null,
    shape: Shape = Capsule()
) {
    val interactiveHighlight = rememberAntoControlHighlight()
    val contentColor = MaterialTheme.colorScheme.onSurface
    val hostContentBackdrop = com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        backdrop != emptyBackdrop() -> backdrop
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> emptyBackdrop()
    }

    val fabOffsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 160.dp,
        animationSpec = spring(
            dampingRatio = 0.85f,
            stiffness = 400f
        ),
        label = "extendedFabOffsetY"
    )

    Row(
        modifier = modifier
            .graphicsLayer {
                translationY = fabOffsetY.toPx()
            }
            .height(56.dp)
            .defaultMinSize(minWidth = 56.dp)
            .antoLiquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = AntoGlassRole.Navigation,
                containerColor = containerColor,
                layerBlock = antoControlLayerBlock(enabled && visible, interactiveHighlight)
            )
            .clickable(
                interactionSource = null,
                indication = null,
                role = Role.Button,
                enabled = enabled && visible,
                onClick = onClick
            )
            .antoControlPressFeedback(enabled && visible, interactiveHighlight)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(
            LocalContentColor provides (if (enabled) contentColor else contentColor.copy(alpha = 0.38f))
        ) {
            icon()
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                text()
            }
        }
    }
}
