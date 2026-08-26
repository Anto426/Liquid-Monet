package com.anto426.liquidmonet.components.display

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop
import com.anto426.liquidmonet.icons.LiquidIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule

enum class LiquidAvatarPresence {
    None,
    Online,
    Away,
    Busy,
    Offline
}

/**
 * LiquidAvatar - Optical Liquid Glass Avatar Component with Tactile Bounce Dynamics.
 */
@Composable
fun LiquidAvatar(
    modifier: Modifier = Modifier,
    initials: String? = null,
    icon: ImageVector? = if (initials == null) LiquidIcons.AccountCircle else null,
    presence: LiquidAvatarPresence = LiquidAvatarPresence.None,
    size: Dp = 44.dp,
    containerColor: Color? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val hostContentBackdrop = LocalLiquidGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> backdrop
    }

    val interactiveHighlight = rememberLiquidControlHighlight()
    val colorScheme = MaterialTheme.colorScheme
    val presenceColor = when (presence) {
        LiquidAvatarPresence.Online -> Color(0xFF34C759)
        LiquidAvatarPresence.Away -> Color(0xFFFF9500)
        LiquidAvatarPresence.Busy -> Color(0xFFFF3B30)
        LiquidAvatarPresence.Offline -> Color(0xFF8E8E93)
        LiquidAvatarPresence.None -> Color.Transparent
    }

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer(liquidControlLayerBlock(enabled, interactiveHighlight) ?: {})
            .liquidControlPressFeedback(
                enabled = enabled,
                interactiveHighlight = interactiveHighlight,
                drawHighlightOverlay = false
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = enabled,
                        role = Role.Button,
                        onClick = onClick
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // Glass Avatar Body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(1.5.dp, Color.White.copy(alpha = 0.30f), CircleShape)
                .liquidGlass(
                    backdrop = effectiveBackdrop,
                    shape = Capsule(),
                    role = LiquidGlassRole.Navigation,
                    containerColor = containerColor ?: colorScheme.primary.copy(alpha = 0.20f)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (initials != null) {
                Text(
                    text = initials.take(2).uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = (size.value * 0.38f).sp
                    ),
                    color = colorScheme.onSurface
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colorScheme.onSurface,
                    modifier = Modifier.size(size * 0.55f)
                )
            }
        }

        // Presence Indicator Dot floating in the bottom corner unclipped
        if (presence != LiquidAvatarPresence.None) {
            val badgeSize = (size.value * 0.30f).coerceAtLeast(10f).dp
            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
                    .clip(CircleShape)
                    .background(presenceColor)
                    .border(2.dp, Color.White, CircleShape)
            )
        }
    }
}

/**
 * LiquidAvatarGroup - Overlapping Stack of Liquid Glass Avatars with Tactile Interactions.
 */
@Composable
fun LiquidAvatarGroup(
    avatars: List<String>, // List of initials
    modifier: Modifier = Modifier,
    maxDisplay: Int = 4,
    size: Dp = 40.dp,
    onAvatarClick: ((Int) -> Unit)? = null,
    onOverflowClick: (() -> Unit)? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val hostContentBackdrop = LocalLiquidGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> backdrop
    }

    val displayList = avatars.take(maxDisplay)
    val overflowCount = (avatars.size - maxDisplay).coerceAtLeast(0)
    val overlapSpacing = -(size * 0.28f)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(overlapSpacing)
    ) {
        displayList.forEachIndexed { index, initials ->
            LiquidAvatar(
                initials = initials,
                size = size,
                onClick = { onAvatarClick?.invoke(index) },
                backdropState = effectiveBackdrop
            )
        }

        if (overflowCount > 0) {
            val overflowHighlight = rememberLiquidControlHighlight()

            Box(
                modifier = Modifier
                    .size(size)
                    .graphicsLayer(liquidControlLayerBlock(true, overflowHighlight) ?: {})
                    .liquidControlPressFeedback(
                        enabled = true,
                        interactiveHighlight = overflowHighlight,
                        drawHighlightOverlay = false
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        onClick = { onOverflowClick?.invoke() }
                    )
                    .border(1.5.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                    .liquidGlass(
                        backdrop = effectiveBackdrop,
                        shape = Capsule(),
                        role = LiquidGlassRole.Navigation,
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+$overflowCount",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = (size.value * 0.34f).sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}
