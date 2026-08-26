package com.anto426.liquidmonet.components.selection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.semantics.Role
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
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary

    val shape = Capsule()

    val animatedContainerColor by animateColorAsState(
        targetValue = if (selected) primaryColor.copy(alpha = 0.32f) else Color.White.copy(alpha = 0.08f),
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "radioContainerColor"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (selected) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 500f),
        label = "radioScalePop"
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
                            onClick = onClick
                        )
                        .liquidControlPressFeedback(enabled, interactiveHighlight)
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = selected,
            enter = scaleIn(spring(dampingRatio = 0.60f, stiffness = 500f)) + fadeIn(tween(160)),
            exit = scaleOut(spring(dampingRatio = 0.85f)) + fadeOut(tween(120))
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .liquidGlass(
                        backdrop = backdropState,
                        shape = CircleShape,
                        role = LiquidGlassRole.Control,
                        containerColor = primaryColor
                    )
            )
        }
    }
}
