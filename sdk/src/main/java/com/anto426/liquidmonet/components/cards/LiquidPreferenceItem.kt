package com.anto426.liquidmonet.components.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule

/**
 * LiquidPreferenceGroup - Liquid Glass Settings Group Container.
 */
@Composable
fun LiquidPreferenceGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (!title.isNullOrBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 12.dp, bottom = 6.dp, top = 8.dp)
            )
        }
        LiquidCard(
            modifier = Modifier.fillMaxWidth(),
            backdropState = backdropState
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * LiquidPreferenceItem - Modern Liquid Glass Setting Item Row with touch feedback.
 */
@Composable
fun LiquidPreferenceItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    trailingContent: (@Composable RowScope.() -> Unit)? = null
) {
    val isInteractive = onClick != null
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val interactiveHighlight = rememberLiquidControlHighlight()
    val iconHighlight = rememberLiquidControlHighlight()
    val colorScheme = MaterialTheme.colorScheme

    val hostContentBackdrop = LocalLiquidGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        backdrop != emptyBackdrop() -> backdrop
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> emptyBackdrop()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(liquidControlLayerBlock(isInteractive, interactiveHighlight) ?: {})
            .liquidControlPressFeedback(
                enabled = isInteractive,
                interactiveHighlight = interactiveHighlight,
                drawHighlightOverlay = false
            )
            .then(
                if (isInteractive) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingContent != null) {
            leadingContent()
            Spacer(modifier = Modifier.width(12.dp))
        } else if (icon != null) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .graphicsLayer(liquidControlLayerBlock(true, iconHighlight) ?: {})
                    .liquidControlPressFeedback(
                        enabled = true,
                        interactiveHighlight = iconHighlight,
                        drawHighlightOverlay = false
                    )
                    .liquidGlass(
                        backdrop = effectiveBackdrop,
                        shape = Capsule(),
                        role = LiquidGlassRole.Navigation,
                        containerColor = colorScheme.primary.copy(alpha = if (isPressed && isInteractive) 0.22f else 0.14f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isPressed && onClick != null) FontWeight.SemiBold else FontWeight.Medium,
                color = colorScheme.onSurface
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }
        }

        if (trailingContent != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailingContent()
        }
    }
}
