package com.anto426.antoui.components.feedback

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
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.RoundedRectangle

/**
 * AntoShimmerBox - Loading Skeleton Placeholder in Liquid Glass with Sweeping Refraction Wave.
 */
@Composable
fun AntoShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedRectangle(16.dp),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val highlightColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)

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
            .antoLiquidGlass(
                backdrop = backdropState,
                shape = shape,
                role = AntoGlassRole.Surface
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
