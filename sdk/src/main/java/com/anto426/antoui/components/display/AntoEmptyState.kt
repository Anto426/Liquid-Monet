package com.anto426.antoui.components.display

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.antoui.components.buttons.AntoButton
import com.anto426.antoui.components.buttons.AntoButtonVariant
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop
import com.anto426.antoui.icons.AntoIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle

/**
 * AntoEmptyState - Optical Liquid Glass Empty State Placeholder.
 * Fully customizable for title, description, custom icon, colors, actions, and custom content.
 */
@Composable
fun AntoEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageVector? = AntoIcons.Info,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    iconContainerColor: Color? = null,
    iconSize: Dp = 32.dp,
    iconBubbleSize: Dp = 68.dp,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null,
    secondaryActionButtonText: String? = null,
    onSecondaryActionClick: (() -> Unit)? = null,
    onIconClick: (() -> Unit)? = null,
    shape: Shape = RoundedRectangle(24.dp),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    customIcon: (@Composable () -> Unit)? = null,
    extraContent: (@Composable () -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val hostContentBackdrop = LocalAntoGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> backdrop
    }

    val iconHighlight = rememberAntoControlHighlight()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .antoLiquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = AntoGlassRole.Control
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Clean Optical Liquid Glass Icon Bubble
            if (customIcon != null) {
                customIcon()
                Spacer(modifier = Modifier.height(14.dp))
            } else if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(iconBubbleSize)
                        .graphicsLayer(antoControlLayerBlock(true, iconHighlight) ?: {})
                        .antoControlPressFeedback(
                            enabled = true,
                            interactiveHighlight = iconHighlight,
                            drawHighlightOverlay = false
                        )
                        .then(
                            if (onIconClick != null) {
                                Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    role = Role.Button,
                                    onClick = onIconClick
                                )
                            } else Modifier
                        )
                        .antoLiquidGlass(
                            backdrop = effectiveBackdrop,
                            shape = Capsule(),
                            role = AntoGlassRole.Navigation,
                            containerColor = iconContainerColor ?: iconColor.copy(alpha = 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(iconSize)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            // Subtitle / Description
            if (description != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                    color = colorScheme.onSurface.copy(alpha = 0.70f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            // Extra Content Slot
            if (extraContent != null) {
                Spacer(modifier = Modifier.height(16.dp))
                extraContent()
            }

            // Actions Row
            if ((actionButtonText != null && onActionClick != null) || (secondaryActionButtonText != null && onSecondaryActionClick != null)) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (secondaryActionButtonText != null && onSecondaryActionClick != null) {
                        AntoButton(
                            text = secondaryActionButtonText,
                            onClick = onSecondaryActionClick,
                            variant = AntoButtonVariant.Secondary,
                            backdropState = effectiveBackdrop
                        )
                    }

                    if (actionButtonText != null && onActionClick != null) {
                        AntoButton(
                            text = actionButtonText,
                            onClick = onActionClick,
                            variant = AntoButtonVariant.Primary,
                            backdropState = effectiveBackdrop
                        )
                    }
                }
            }
        }
    }
}
