package com.anto426.liquidmonet.components.buttons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.components.internal.LiquidControlDefaults
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.RoundedRectangle

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
    expanded: Boolean = true,
    label: (@Composable () -> Unit)? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    enabled: Boolean = true,
    containerColor: Color? = null,
    shape: Shape = RoundedRectangle(16.dp),
    content: @Composable () -> Unit
) {
    val interactiveHighlight = rememberLiquidControlHighlight()
    val colorScheme = MaterialTheme.colorScheme
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
    val fabVisibility by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.86f, stiffness = 500f),
        label = "fabVisibility"
    )
    val horizontalPadding by animateDpAsState(
        targetValue = if (label != null && expanded) 20.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = 420f),
        label = "fabHorizontalPadding"
    )
    val resolvedContainerColor = containerColor ?: colorScheme.primaryContainer.copy(alpha = 0.18f)
    val animatedContainerColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (enabled) {
            resolvedContainerColor
        } else {
            resolvedContainerColor.copy(alpha = resolvedContainerColor.alpha * 0.45f)
        },
        label = "fabContainerColor"
    )
    val animatedContentColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (enabled) {
            colorScheme.onSurface
        } else {
            colorScheme.onSurface.copy(alpha = LiquidControlDefaults.disabledContentAlpha)
        },
        label = "fabContentColor"
    )

    Row(
        modifier = modifier
            .graphicsLayer {
                translationY = fabOffsetY.toPx()
                alpha = fabVisibility
                scaleX = 0.84f + 0.16f * fabVisibility
                scaleY = 0.84f + 0.16f * fabVisibility
            }
            .height(size)
            .defaultMinSize(minWidth = size)
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = LiquidGlassRole.Navigation,
                containerColor = animatedContainerColor,
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
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides animatedContentColor) {
            ProvideTextStyle(MaterialTheme.typography.labelLarge) {
                content()
                if (label != null) {
                    AnimatedVisibility(
                        visible = expanded,
                        enter = fadeIn() + expandHorizontally(expandFrom = Alignment.Start),
                        exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
                    ) {
                        label()
                    }
                }
            }
        }
    }
}
