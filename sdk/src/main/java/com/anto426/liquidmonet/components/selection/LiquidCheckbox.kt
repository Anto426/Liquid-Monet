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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.kyant.shapes.RoundedRectangle

/**
 * LiquidCheckbox - Liquid Glass Checkbox with Snell Lensing, Vibrant Monet Transitions, and Tactile Bounce.
 */
@Composable
fun LiquidCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val interactiveHighlight = rememberLiquidControlHighlight()
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary

    val shape = RoundedRectangle(8.dp)

    val animatedContainerColor by animateColorAsState(
        targetValue = if (checked) primaryColor else colorScheme.onSurface.copy(alpha = 0.08f),
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "checkboxContainerColor"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (checked) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 500f),
        label = "checkboxScalePop"
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
                if (onCheckedChange != null) {
                    Modifier
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            role = Role.Checkbox,
                            enabled = enabled,
                            onClick = { onCheckedChange(!checked) }
                        )
                        .liquidControlPressFeedback(enabled, interactiveHighlight)
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = scaleIn(spring(dampingRatio = 0.60f, stiffness = 500f)) + fadeIn(tween(160)),
            exit = scaleOut(spring(dampingRatio = 0.85f)) + fadeOut(tween(120))
        ) {
            Canvas(modifier = Modifier.size(14.dp)) {
                val w = size.width
                val h = size.height
                val checkPath = Path().apply {
                    moveTo(w * 0.15f, h * 0.52f)
                    lineTo(w * 0.42f, h * 0.80f)
                    lineTo(w * 0.88f, h * 0.22f)
                }
                drawPath(
                    path = checkPath,
                    color = colorScheme.onPrimary,
                    style = Stroke(
                        width = 2.4.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}
