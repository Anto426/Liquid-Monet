package com.anto426.liquidmonet.components.selection

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.liquidmonet.glass.LiquidBackgroundEffect
import com.anto426.liquidmonet.icons.LiquidIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule

/**
 * LiquidMenuSelectionItem - Dedicated Menu Item for Selection (Single/Multi Select).
 * Provides clear visual feedback:
 * - Active Monet primary tinted container
 * - Interactive touch / drag spotlight illumination (no white flash)
 * - Checkmark indicator on the trailing edge
 * - Title and optional descriptive subtitle
 */
@Composable
fun LiquidMenuSelectionItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val colorScheme = MaterialTheme.colorScheme

    val animatedAlpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.40f,
        animationSpec = tween(180),
        label = "selectionItemAlpha"
    )

    val itemScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(150),
        label = "selectionItemScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isPressed -> colorScheme.primary.copy(alpha = if (selected) 0.32f else 0.24f)
            isHovered -> colorScheme.primary.copy(alpha = if (selected) 0.24f else 0.16f)
            selected -> colorScheme.primary.copy(alpha = 0.16f)
            else -> Color.Transparent
        },
        animationSpec = tween(180),
        label = "selectionItemBg"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isPressed -> colorScheme.primary.copy(alpha = 0.50f)
            isHovered -> colorScheme.primary.copy(alpha = 0.25f)
            selected -> colorScheme.primary.copy(alpha = 0.20f)
            else -> Color.Transparent
        },
        animationSpec = tween(180),
        label = "selectionItemBorder"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(14.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .graphicsLayer {
                scaleX = itemScale
                scaleY = itemScale
                alpha = animatedAlpha
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isPressed || isHovered || selected) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.80f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isPressed || selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isPressed || isHovered || selected) colorScheme.primary else colorScheme.onSurface,
                fontSize = 14.5.sp
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isPressed || isHovered) colorScheme.primary.copy(alpha = 0.80f) else colorScheme.onSurface.copy(alpha = 0.60f),
                    fontSize = 11.5.sp
                )
            }
        }

        if (selected) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = LiquidIcons.Check,
                contentDescription = "Selezionato",
                tint = colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * LiquidBackgroundSelector - Dedicated Liquid Glass Background Effect Selector.
 */
@Composable
fun LiquidBackgroundSelector(
    selectedEffect: LiquidBackgroundEffect,
    onEffectSelected: (LiquidBackgroundEffect) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val effects = listOf(
        Triple(LiquidBackgroundEffect.RadiantBeam, "Radiant Beam", "Spotlight con griglia sub-pixel ad alta precisione"),
        Triple(LiquidBackgroundEffect.Aurora, "Cosmic Aurora", "Onde fluide boreali a luminescenza dinamica"),
        Triple(LiquidBackgroundEffect.MeshGlow, "Mesh Glow", "Sfumature organiche e gradienti diffusi"),
        Triple(LiquidBackgroundEffect.OrbitalPulse, "Orbital Pulse", "Anelli concentrici ad espansione armonica")
    )

    LiquidSelect(
        items = effects.map { it.first },
        selectedItem = selectedEffect,
        onItemSelected = onEffectSelected,
        label = "Effetto di Sfondo Dinamico",
        itemLabel = { effect -> effects.firstOrNull { it.first == effect }?.second ?: effect.name },
        itemSubtitle = { effect -> effects.firstOrNull { it.first == effect }?.third },
        itemIcon = { effect ->
            when (effect) {
                LiquidBackgroundEffect.RadiantBeam -> LiquidIcons.Refresh
                LiquidBackgroundEffect.Aurora -> LiquidIcons.Star
                LiquidBackgroundEffect.MeshGlow -> LiquidIcons.Info
                LiquidBackgroundEffect.OrbitalPulse -> LiquidIcons.Settings
            }
        },
        modifier = modifier,
        backdropState = backdropState
    )
}
