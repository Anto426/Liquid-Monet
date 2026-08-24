package com.anto426.antoui.components.selection

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.icons.AntoIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop

/**
 * AntoRatingBar - Minimal, sleek Material 3 Star Rating Selector.
 * Features clean organic spring bounce on selection and Material Theme colors.
 */
@Composable
fun AntoRatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: Dp = 28.dp,
    activeColor: Color = Color.Unspecified,
    inactiveColor: Color = Color.Unspecified,
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val colorScheme = MaterialTheme.colorScheme
    val resolvedActiveColor = if (activeColor.isSpecified) activeColor else colorScheme.primary
    val resolvedInactiveColor = if (inactiveColor.isSpecified) inactiveColor else colorScheme.onSurface.copy(alpha = 0.22f)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isFilled = i <= rating
            val starHighlight = rememberAntoControlHighlight()

            val animatedColor by animateColorAsState(
                targetValue = if (isFilled) resolvedActiveColor else resolvedInactiveColor,
                animationSpec = tween(durationMillis = 200),
                label = "starColor_$i"
            )

            val animatedScale by animateFloatAsState(
                targetValue = if (isFilled) 1.05f else 0.95f,
                animationSpec = spring(
                    dampingRatio = 0.60f,
                    stiffness = 500f
                ),
                label = "starScale_$i"
            )

            Box(
                modifier = Modifier
                    .size(starSize + 8.dp)
                    .graphicsLayer(antoControlLayerBlock(enabled, starHighlight) ?: {})
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                    }
                    .antoControlPressFeedback(
                        enabled = enabled,
                        interactiveHighlight = starHighlight,
                        drawHighlightOverlay = false
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        enabled = enabled,
                        onClick = { onRatingChanged(i) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AntoIcons.Star,
                    contentDescription = "Valutazione $i su $maxStars",
                    tint = animatedColor,
                    modifier = Modifier.size(starSize)
                )
            }
        }
    }
}
