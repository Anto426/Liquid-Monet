package com.anto426.liquidmonet.components.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.resolveLiquidGlassBackdrop
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle

/**
 * LiquidControlCenterTile - Interactive Quick Settings Tile with Radiant Monet Illumination,
 * Smooth Fluid Toggle Transitions and reference-aligned, stable tile geometry.
 */
@Composable
fun LiquidControlCenterTile(
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
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary
    val activeContentColor = colorScheme.onPrimary
    val inactiveContentColor = colorScheme.onSurface

    val shape = RoundedRectangle(22.dp)
    val iconShape = Capsule()

    // Smooth color interpolations between ON and OFF states
    val animatedContainerColor by animateColorAsState(
        targetValue = if (active) primaryColor.copy(alpha = 0.36f)
        else inactiveContentColor.copy(alpha = 0.08f),
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "tileContainerColor"
    )

    val animatedIconBgColor by animateColorAsState(
        targetValue = if (active) primaryColor else inactiveContentColor.copy(alpha = 0.14f),
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "tileIconBgColor"
    )

    val animatedIconTint by animateColorAsState(
        targetValue = if (active) activeContentColor else inactiveContentColor.copy(alpha = 0.88f),
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "tileIconTint"
    )

    val animatedSubtitleColor by animateColorAsState(
        targetValue = if (active) activeContentColor.copy(alpha = 0.78f)
        else inactiveContentColor.copy(alpha = 0.65f),
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "tileSubtitleColor"
    )

    // Control Center tiles in the reference do not use the generic directional gelatin bounce.
    // Keep only a restrained state transition so nested tile and icon transforms never multiply.
    val animatedIconScale by animateFloatAsState(
        targetValue = if (active) 1.04f else 1.0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "tileIconScale"
    )

    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdrop, backdropState)
    val interactionSource = remember { MutableInteractionSource() }
    val interactiveHighlight = rememberLiquidControlHighlight()

    Box(
        modifier = modifier
            .height(74.dp)
            .fillMaxWidth()
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = LiquidGlassRole.Control,
                containerColor = animatedContainerColor,
                layerBlock = liquidControlLayerBlock(enabled, interactiveHighlight)
            )
            .toggleable(
                value = active,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Switch,
                enabled = enabled,
                onValueChange = { onClick() }
            )
            .liquidControlPressFeedback(enabled, interactiveHighlight)
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
                    // The tile is the optical material. The icon pod is only a chromatic tint;
                    // refracting the same backdrop a second time creates a heavy double-lens halo.
                    .background(animatedIconBgColor, iconShape),
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
                        color = if (active) activeContentColor else inactiveContentColor,
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
