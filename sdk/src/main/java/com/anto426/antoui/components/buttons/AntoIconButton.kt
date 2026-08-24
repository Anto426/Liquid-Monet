package com.anto426.antoui.components.buttons

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule

/**
 * AntoIconButton - Dedicated Optical Liquid Glass Icon Button Component.
 */
@Composable
fun AntoIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    enabled: Boolean = true,
    size: Dp = 38.dp,
    iconSize: Dp = 18.dp,
    shape: Shape = Capsule(),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val interactiveHighlight = rememberAntoControlHighlight()
    val contentColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .size(size)
            .antoLiquidGlass(
                backdrop = backdropState,
                shape = shape,
                role = AntoGlassRole.Control,
                layerBlock = antoControlLayerBlock(enabled, interactiveHighlight)
            )
            .clickable(
                interactionSource = null,
                indication = null,
                role = Role.Button,
                enabled = enabled,
                onClick = onClick
            )
            .antoControlPressFeedback(enabled, interactiveHighlight),
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
