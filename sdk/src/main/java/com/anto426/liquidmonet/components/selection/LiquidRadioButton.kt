package com.anto426.liquidmonet.components.selection

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.runtime.LiquidGlassMotionSpecs
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule

/**
 * LiquidRadioButton - Liquid Glass Radio Button with Animated Concentric Dot.
 */
@Composable
fun LiquidRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val interactiveHighlight = rememberLiquidControlHighlight()
    val hapticFeedback = LocalHapticFeedback.current
    val performance = LocalLiquidGlassPerformance.current
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary

    val shape = Capsule()

    val animatedContainerColor by animateColorAsState(
        targetValue = if (selected) primaryColor.copy(alpha = 0.32f) else Color.White.copy(alpha = 0.08f),
        animationSpec = LiquidGlassMotionSpecs.tween(performance, 200),
        label = "radioContainerColor"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (selected) 1.04f else 1.0f,
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.54f,
            stiffness = 460f
        ),
        label = "radioScalePop"
    )
    val dotProgress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.52f,
            stiffness = 520f
        ),
        label = "radioLiquidDot"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .size(24.dp)
            .liquidGlass(
                backdrop = backdropState,
                shape = shape,
                role = LiquidGlassRole.Control,
                containerColor = animatedContainerColor,
                layerBlock = liquidControlLayerBlock(enabled, interactiveHighlight)
            )
            .then(
                if (onClick != null) {
                    Modifier
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            role = Role.RadioButton,
                            enabled = enabled,
                            onClick = {
                                hapticFeedback.performHapticFeedback(
                                    HapticFeedbackType.TextHandleMove
                                )
                                onClick()
                            }
                        )
                        .liquidControlPressFeedback(enabled, interactiveHighlight)
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .graphicsLayer {
                    alpha = dotProgress.coerceIn(0f, 1f)
                    // Slightly asymmetric scaling makes selection feel like a settling droplet.
                    scaleX = (0.2f + 0.8f * dotProgress).coerceAtLeast(0f)
                    scaleY = (0.1f + 0.9f * dotProgress).coerceAtLeast(0f)
                }
                .background(Color.White.copy(alpha = 0.94f), CircleShape)
        )
    }
}
