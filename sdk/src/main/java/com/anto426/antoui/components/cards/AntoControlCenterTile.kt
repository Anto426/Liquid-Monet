package com.anto426.antoui.components.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle

/**
 * AntoControlCenterTile - Interactive Quick Settings Tile with Radiant Monet Illumination,
 * Smooth Fluid Toggle Transitions, and Button Bounce Physics.
 */
@Composable
fun AntoControlCenterTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val interactiveHighlight = rememberAntoControlHighlight()
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary

    val shape = RoundedRectangle(22.dp)
    val iconShape = Capsule()

    // Smooth color interpolations between ON and OFF states
    val animatedContainerColor by animateColorAsState(
        targetValue = if (active) primaryColor.copy(alpha = 0.36f) else Color.White.copy(alpha = 0.08f),
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "tileContainerColor"
    )

    val animatedIconBgColor by animateColorAsState(
        targetValue = if (active) primaryColor else Color.White.copy(alpha = 0.16f),
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "tileIconBgColor"
    )

    val animatedIconTint by animateColorAsState(
        targetValue = if (active) Color.White else Color.White.copy(alpha = 0.88f),
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "tileIconTint"
    )

    val animatedSubtitleColor by animateColorAsState(
        targetValue = if (active) primaryColor else Color.White.copy(alpha = 0.65f),
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "tileSubtitleColor"
    )

    // Lively scale pop on toggle state change
    val animatedIconScale by animateFloatAsState(
        targetValue = if (active) 1.06f else 1.0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 450f),
        label = "tileIconScale"
    )

    val hostContentBackdrop = com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        backdrop != emptyBackdrop() -> backdrop
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> emptyBackdrop()
    }

    Box(
        modifier = modifier
            .height(74.dp)
            .fillMaxWidth()
            .antoLiquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = AntoGlassRole.Control,
                containerColor = animatedContainerColor,
                layerBlock = antoControlLayerBlock(enabled, interactiveHighlight)
            )
            .clickable(
                interactionSource = null,
                indication = null,
                role = Role.Button,
                enabled = enabled,
                onClick = onClick
            )
            .antoControlPressFeedback(enabled, interactiveHighlight)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Dedicated Optical Liquid Glass Icon Bubble Pod with Gelatin Squeeze
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .graphicsLayer {
                        scaleX = animatedIconScale
                        scaleY = animatedIconScale
                    }
                    .antoLiquidGlass(
                        backdrop = effectiveBackdrop,
                        shape = iconShape,
                        role = AntoGlassRole.Navigation,
                        containerColor = animatedIconBgColor,
                        layerBlock = antoControlLayerBlock(enabled, interactiveHighlight)
                    )
                    .border(
                        width = 1.dp,
                        color = if (active) Color.White.copy(alpha = 0.40f) else Color.White.copy(alpha = 0.18f),
                        shape = iconShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = animatedIconTint,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column {
                BasicText(
                    text = title,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                BasicText(
                    text = subtitle,
                    style = TextStyle(
                        color = animatedSubtitleColor,
                        fontSize = 12.5.sp,
                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
                    )
                )
            }
        }
    }
}
