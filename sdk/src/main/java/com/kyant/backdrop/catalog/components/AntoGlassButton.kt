package com.kyant.backdrop.catalog.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anto426.antoui.components.internal.AntoControlDefaults
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.anto426.antoui.glass.runtime.AntoGlassPreset
import com.kyant.backdrop.Backdrop

/**
 * AntoGlassButton - Radiant Optical Liquid Glass Button with Animated Interactive States.
 * Balances vibrant saturated Monet colors with Snell lens refraction,
 * chromatic prismatic dispersion, and ambient specular highlights.
 */
@Composable
fun AntoGlassButton(
    onClick: () -> Unit,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    isInteractive: Boolean = true,
    shape: Shape = AntoControlDefaults.shape,
    height: Dp = 48.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    border: BorderStroke? = null,
    tint: Color = Color.Unspecified,
    surfaceColor: Color = Color.Unspecified,
    content: @Composable RowScope.() -> Unit
) {
    val interactiveHighlight = rememberAntoControlHighlight()

    val targetContainerColor = when {
        surfaceColor.isSpecified -> surfaceColor
        tint.isSpecified -> tint
        else -> null
    }

    val animatedContainerColor = if (targetContainerColor != null) {
        animateColorAsState(
            targetValue = if (isInteractive) targetContainerColor else targetContainerColor.copy(alpha = targetContainerColor.alpha * 0.45f),
            animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
            label = "buttonContainerColor"
        ).value
    } else null

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isInteractive) 1f else 0.50f,
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
        label = "buttonAlpha"
    )

    val glassModifier = modifier
        .antoLiquidGlass(
            backdrop = backdrop,
            shape = shape,
            role = AntoGlassRole.Control,
            containerColor = animatedContainerColor,
            layerBlock = antoControlLayerBlock(isInteractive, interactiveHighlight)
        )
        .let { if (border != null) it.border(border, shape) else it }
        .clickable(
            interactionSource = null,
            indication = null,
            role = Role.Button,
            enabled = isInteractive,
            onClick = onClick
        )
        .antoControlPressFeedback(isInteractive, interactiveHighlight)
        .height(height)
        .padding(contentPadding)
        .graphicsLayer { alpha = animatedAlpha }

    Row(
        modifier = glassModifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
