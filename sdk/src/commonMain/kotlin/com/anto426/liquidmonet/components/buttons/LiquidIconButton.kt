package com.anto426.liquidmonet.components.buttons

import androidx.compose.animation.animateColorAsState
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
import com.anto426.liquidmonet.components.internal.LiquidInputNormalization
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.resolveLiquidGlassBackdrop
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import com.anto426.liquidmonet.motion.LiquidMotion
import com.anto426.liquidmonet.theme.LiquidGlassTheme
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
    backdropState: Backdrop = emptyBackdrop()
) {
    LiquidInputNormalization.positive(size, "LiquidIconButton size")
    LiquidInputNormalization.positive(iconSize, "LiquidIconButton iconSize")
    val performance = LocalLiquidGlassPerformance.current
    val interactiveHighlight = rememberLiquidControlHighlight()
    val colorScheme = MaterialTheme.colorScheme
    val glassColors = LiquidGlassTheme.colors
    val contentColor by animateColorAsState(
        targetValue = if (enabled) {
            glassColors.content
        } else {
            glassColors.disabledContent
        },
        animationSpec = LiquidMotion.tween(performance, 200, LiquidMotion.FastOutSlow),
        label = "iconButtonContentColor"
    )
    val containerColor by animateColorAsState(
        targetValue = if (enabled) glassColors.neutralContainer
        else glassColors.neutralContainer.copy(alpha = glassColors.neutralContainer.alpha * 0.45f),
        animationSpec = LiquidMotion.tween(performance, 200, LiquidMotion.FastOutSlow),
        label = "iconButtonContainerColor"
    )

    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdropState)

    Box(
        modifier = modifier
            .size(size)
            .liquidGlass(
                backdrop = effectiveBackdrop,
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
            .liquidControlPressFeedback(enabled, interactiveHighlight, shape = shape),
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
