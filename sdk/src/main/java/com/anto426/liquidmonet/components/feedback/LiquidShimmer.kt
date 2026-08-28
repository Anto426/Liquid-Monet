package com.anto426.liquidmonet.components.feedback

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.theme.LiquidGlassTheme
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.RoundedRectangle

/**
 * LiquidShimmerBox - Loading Skeleton Placeholder in Liquid Glass with Sweeping Refraction Wave.
 */
@Composable
fun LiquidShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedRectangle(16.dp),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val highlightColor = LiquidGlassTheme.colors.neutralContainer

    val infiniteTransition = rememberInfiniteTransition(label = "glassShimmer")
    val translateAnim by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslation"
    )

    Box(
        modifier = modifier
            .liquidGlass(
                backdrop = backdropState,
                shape = shape,
                role = LiquidGlassRole.Surface
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        highlightColor,
                        Color.Transparent
                    ),
                    start = Offset(translateAnim * 400f, 0f),
                    end = Offset((translateAnim + 1f) * 400f, 400f)
                )
            )
    )
}
