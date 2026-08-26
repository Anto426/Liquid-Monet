package com.anto426.liquidmonet.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.components.internal.LiquidControlDefaults
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule

/**
 * LiquidIconButton - Dedicated Optical Liquid Glass Icon Button Component.
 */
@Composable
fun LiquidIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    size: Dp = 40.dp,
    iconSize: Dp = 24.dp,
    shape: Shape = Capsule(),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val interactiveHighlight = rememberLiquidControlHighlight()
    val colorScheme = MaterialTheme.colorScheme
    val contentColor by animateColorAsState(
        targetValue = if (enabled) {
            colorScheme.onSurface
        } else {
            colorScheme.onSurface.copy(alpha = LiquidControlDefaults.disabledContentAlpha)
        },
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "iconButtonContentColor"
    )
    val containerColor by animateColorAsState(
        targetValue = colorScheme.primary.copy(alpha = if (enabled) 0.08f else 0.03f),
        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        label = "iconButtonContainerColor"
    )

    Box(
        modifier = modifier
            .size(size)
            .liquidGlass(
                backdrop = backdropState,
                shape = shape,
                role = LiquidGlassRole.Control,
                containerColor = containerColor,
                layerBlock = liquidControlLayerBlock(enabled, interactiveHighlight)
            )
            .clickable(
                interactionSource = null,
                indication = null,
                role = Role.Button,
                enabled = enabled,
                onClick = onClick
            )
            .liquidControlPressFeedback(enabled, interactiveHighlight),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(iconSize)
        )
    }
}
