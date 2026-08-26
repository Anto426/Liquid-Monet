package com.anto426.liquidmonet.components.buttons

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
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule

/**
 * LiquidFloatingActionButton - Pure Crystal Liquid Glass Floating Action Button.
 * Uses the exact same 1:1 optical liquid glass pipeline as LiquidDialog & LiquidSheet
 * with Snell lens refraction, chromatic dispersion, specular highlights, and 3D shadows.
 */
@Composable
fun LiquidFloatingActionButton(
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
    val interactiveHighlight = rememberLiquidControlHighlight()
    val contentColor = MaterialTheme.colorScheme.onSurface
    val hostContentBackdrop = com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop.current
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
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = LiquidGlassRole.Navigation,
                containerColor = containerColor,
                layerBlock = liquidControlLayerBlock(enabled && visible, interactiveHighlight)
            )
            .clickable(
                interactionSource = null,
                indication = null,
                role = Role.Button,
                enabled = enabled && visible,
                onClick = onClick
            )
            .liquidControlPressFeedback(enabled && visible, interactiveHighlight),
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
 * LiquidExtendedFloatingActionButton - Extended Crystal Liquid Glass FAB with icon and text.
 */
@Composable
fun LiquidExtendedFloatingActionButton(
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
    val interactiveHighlight = rememberLiquidControlHighlight()
    val contentColor = MaterialTheme.colorScheme.onSurface
    val hostContentBackdrop = com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop.current
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
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = LiquidGlassRole.Navigation,
                containerColor = containerColor,
                layerBlock = liquidControlLayerBlock(enabled && visible, interactiveHighlight)
            )
            .clickable(
                interactionSource = null,
                indication = null,
                role = Role.Button,
                enabled = enabled && visible,
                onClick = onClick
            )
            .liquidControlPressFeedback(enabled && visible, interactiveHighlight)
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
