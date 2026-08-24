package com.anto426.antoui.components.selection

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.components.menu.AntoGlassDropdownMenu
import com.anto426.antoui.glass.AntoBackgroundEffect
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.anto426.antoui.glass.overlay.antoGlassOverlayAnchor
import com.anto426.antoui.glass.overlay.rememberAntoGlassOverlayAnchorState
import com.anto426.antoui.icons.AntoIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle

/**
 * AntoGlassSelectionItem - Dedicated Menu Item for Selection (Single/Multi Select).
 * Provides clear visual feedback:
 * - Active Monet primary tinted container
 * - Interactive touch / drag spotlight illumination (no white flash)
 * - Checkmark indicator on the trailing edge
 * - Title and optional descriptive subtitle
 */
@Composable
fun AntoGlassSelectionItem(
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
                imageVector = AntoIcons.Check,
                contentDescription = "Selezionato",
                tint = colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * AntoGlassSelectPicker - Compact Liquid Glass Selection Picker Anchor + Dropdown.
 * Displays the currently selected value and opens an optical liquid selection menu when tapped.
 */
@Composable
fun <T> AntoGlassSelectPicker(
    label: String,
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    itemSubtitle: ((T) -> String?)? = null,
    itemIcon: ((T) -> ImageVector?)? = null,
    shape: Shape = RoundedRectangle(18.dp),
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    var expanded by remember { mutableStateOf(false) }
    val anchorState = rememberAntoGlassOverlayAnchorState()
    val interactiveHighlight = rememberAntoControlHighlight()
    val colorScheme = MaterialTheme.colorScheme

    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(220),
        label = "chevronRotation"
    )

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .antoGlassOverlayAnchor(anchorState)
                .antoLiquidGlass(
                    backdrop = backdropState,
                    shape = shape,
                    role = AntoGlassRole.Control,
                    containerColor = if (expanded) colorScheme.primary.copy(alpha = 0.08f) else null,
                    layerBlock = antoControlLayerBlock(enabled, interactiveHighlight)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                    enabled = enabled,
                    onClick = { expanded = !expanded }
                )
                .antoControlPressFeedback(enabled, interactiveHighlight)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                val currentIcon = itemIcon?.invoke(selectedItem)
                if (currentIcon != null) {
                    Icon(
                        imageVector = currentIcon,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSurface.copy(alpha = 0.60f),
                        fontSize = 11.sp
                    )
                    Text(
                        text = itemLabel(selectedItem),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                }
            }

            Icon(
                imageVector = AntoIcons.KeyboardArrowDown,
                contentDescription = if (expanded) "Chiudi" else "Apri selezione",
                tint = colorScheme.onSurface.copy(alpha = 0.60f),
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer { rotationZ = chevronRotation }
            )
        }

        AntoGlassDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            anchorState = anchorState,
            offset = DpOffset(0.dp, 6.dp),
            backdropState = backdropState
        ) {
            items.forEach { item ->
                AntoGlassSelectionItem(
                    title = itemLabel(item),
                    subtitle = itemSubtitle?.invoke(item),
                    icon = itemIcon?.invoke(item),
                    selected = item == selectedItem,
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * AntoBackgroundSelector - Dedicated Liquid Glass Background Effect Selector.
 */
@Composable
fun AntoBackgroundSelector(
    selectedEffect: AntoBackgroundEffect,
    onEffectSelected: (AntoBackgroundEffect) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val effects = listOf(
        Triple(AntoBackgroundEffect.RadiantBeam, "Radiant Beam", "Spotlight con griglia sub-pixel ad alta precisione"),
        Triple(AntoBackgroundEffect.Aurora, "Cosmic Aurora", "Onde fluide boreali a luminescenza dinamica"),
        Triple(AntoBackgroundEffect.MeshGlow, "Mesh Glow", "Sfumature organiche e gradienti diffusi"),
        Triple(AntoBackgroundEffect.OrbitalPulse, "Orbital Pulse", "Anelli concentrici ad espansione armonica")
    )

    AntoGlassSelectPicker(
        label = "Effetto di Sfondo Dinamico",
        items = effects.map { it.first },
        selectedItem = selectedEffect,
        onItemSelected = onEffectSelected,
        itemLabel = { effect -> effects.firstOrNull { it.first == effect }?.second ?: effect.name },
        itemSubtitle = { effect -> effects.firstOrNull { it.first == effect }?.third },
        itemIcon = { effect ->
            when (effect) {
                AntoBackgroundEffect.RadiantBeam -> AntoIcons.Refresh
                AntoBackgroundEffect.Aurora -> AntoIcons.Star
                AntoBackgroundEffect.MeshGlow -> AntoIcons.Info
                AntoBackgroundEffect.OrbitalPulse -> AntoIcons.Settings
            }
        },
        modifier = modifier,
        backdropState = backdropState
    )
}
