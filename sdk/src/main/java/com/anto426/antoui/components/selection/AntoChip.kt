package com.anto426.antoui.components.selection

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.antoui.components.buttons.AntoButtonVariant
import com.anto426.antoui.components.internal.AntoControlDefaults
import com.anto426.antoui.components.internal.antoButtonColors
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.anto426.antoui.icons.AntoIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule

/**
 * AntoChip - Radiant Optical Liquid Glass Chip for filters, tags, suggestions, and categories.
 * Features vibrant Monet illumination, dynamic scale spring transitions, and lively liquid bounce.
 */
@Composable
fun AntoChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    badge: String? = null,
    onCloseClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    shape: Shape = Capsule(),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    tint: Color = Color.Unspecified
) {
    val interactiveHighlight = rememberAntoControlHighlight()
    val closeHighlight = rememberAntoControlHighlight()
    val colorScheme = MaterialTheme.colorScheme

    val hostContentBackdrop = com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> backdrop
    }

    val selectedColors = antoButtonColors(AntoButtonVariant.Primary, tint, enabled)
    val activeMonetColor = if (selectedColors.tint.isSpecified) selectedColors.tint else colorScheme.primary

    val targetContentColor = if (selected) {
        Color.White
    } else if (enabled) {
        Color.White.copy(alpha = 0.88f)
    } else {
        Color.White.copy(alpha = 0.38f)
    }

    val animatedContentColor by animateColorAsState(
        targetValue = targetContentColor,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "chipContentColor"
    )

    // Vibrant Radiant Monet Glow & Fill
    val targetContainerColor = if (selected) activeMonetColor.copy(alpha = 0.52f) else Color.White.copy(alpha = 0.08f)
    val animatedContainerColor by animateColorAsState(
        targetValue = targetContainerColor,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "chipContainerColor"
    )

    Row(
        modifier = modifier
            .height(38.dp)
            .graphicsLayer(antoControlLayerBlock(enabled, interactiveHighlight) ?: {})
            .antoControlPressFeedback(
                enabled = enabled,
                interactiveHighlight = interactiveHighlight,
                drawHighlightOverlay = false
            )
            .antoLiquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = AntoGlassRole.Navigation,
                containerColor = animatedContainerColor
            )
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                enabled = enabled,
                onClick = onClick
            )
            .padding(
                start = if (leadingIcon != null) 12.dp else 14.dp,
                end = if (onCloseClick != null || trailingIcon != null || badge != null) 8.dp else 14.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(LocalContentColor provides animatedContentColor) {
            // Leading Icon
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = animatedContentColor.copy(alpha = if (selected) 1f else 0.90f),
                    modifier = Modifier.size(16.dp)
                )
            }

            BasicText(
                text = label,
                style = TextStyle(
                    color = animatedContentColor,
                    fontSize = 13.5.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                )
            )

            // Radiant Count / Badge Bubble
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .antoLiquidGlass(
                            backdrop = effectiveBackdrop,
                            shape = Capsule(),
                            role = AntoGlassRole.Navigation,
                            containerColor = if (selected) activeMonetColor.copy(alpha = 0.75f) else Color.White.copy(alpha = 0.16f)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicText(
                        text = badge,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Close Action with Tactile Bounce
            if (onCloseClick != null) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(Capsule())
                        .graphicsLayer(antoControlLayerBlock(enabled, closeHighlight) ?: {})
                        .antoControlPressFeedback(
                            enabled = enabled,
                            interactiveHighlight = closeHighlight,
                            drawHighlightOverlay = false
                        )
                        .clickable(
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            onClick = onCloseClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AntoIcons.Close,
                        contentDescription = "Rimuovi",
                        tint = animatedContentColor.copy(alpha = 0.75f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            } else if (trailingIcon != null) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = animatedContentColor.copy(alpha = 0.90f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * AntoFilterChip - Filter variation of AntoChip with toggle selection and count badge.
 */
@Composable
fun AntoFilterChip(
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    badge: String? = null,
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    tint: Color = Color.Unspecified
) {
    AntoChip(
        label = label,
        onClick = { onSelectedChange(!selected) },
        modifier = modifier,
        selected = selected,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        badge = badge,
        enabled = enabled,
        tint = tint,
        backdrop = backdrop,
        backdropState = backdropState
    )
}
