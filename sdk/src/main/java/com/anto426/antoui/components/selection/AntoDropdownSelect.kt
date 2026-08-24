package com.anto426.antoui.components.selection

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.anto426.antoui.glass.overlay.antoGlassOverlayAnchor
import com.anto426.antoui.glass.overlay.rememberAntoGlassOverlayAnchorState
import com.anto426.antoui.icons.AntoIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.RoundedRectangle

/**
 * AntoDropdownSelect - Optical Liquid Glass Select Picker Field with Dropdown Selection Menu.
 */
@Composable
fun <T> AntoDropdownSelect(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Seleziona",
    itemLabel: (T) -> String = { it.toString() },
    itemSubtitle: ((T) -> String?)? = null,
    itemIcon: ((T) -> ImageVector)? = null,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    var isExpanded by remember { mutableStateOf(false) }
    val anchorState = rememberAntoGlassOverlayAnchorState()
    val interactiveHighlight = rememberAntoControlHighlight()
    val colorScheme = MaterialTheme.colorScheme

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(220),
        label = "dropdownChevron"
    )

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .antoGlassOverlayAnchor(anchorState)
                .antoLiquidGlass(
                    backdrop = backdropState,
                    shape = RoundedRectangle(18.dp),
                    role = AntoGlassRole.Control,
                    containerColor = if (isExpanded) colorScheme.primary.copy(alpha = 0.08f) else null,
                    layerBlock = antoControlLayerBlock(enabled, interactiveHighlight)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                    enabled = enabled,
                    onClick = { isExpanded = !isExpanded }
                )
                .antoControlPressFeedback(enabled, interactiveHighlight)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = label,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    if (selectedItem != null) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurface.copy(alpha = 0.65f),
                            fontSize = 11.sp
                        )
                        Text(
                            text = itemLabel(selectedItem),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                    } else {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurface.copy(alpha = 0.45f)
                        )
                    }
                }
            }

            Icon(
                imageVector = AntoIcons.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Chiudi" else "Espandi",
                tint = colorScheme.onSurface.copy(alpha = 0.50f),
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer { rotationZ = chevronRotation }
            )
        }

        AntoGlassDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
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
                        isExpanded = false
                    }
                )
            }
        }
    }
}
