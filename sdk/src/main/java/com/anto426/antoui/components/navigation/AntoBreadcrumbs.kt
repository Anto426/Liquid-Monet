package com.anto426.antoui.components.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
 * Data item representing a step in breadcrumbs.
 */
data class AntoBreadcrumbItem(
    val label: String,
    val onClick: (() -> Unit)? = null
)

/**
 * AntoBreadcrumbs - Optical Liquid Glass Navigation Breadcrumbs Trail.
 * Minimalist, elegant scrollable glass trail with fluid tactile bounce on clickable items.
 */
@Composable
fun AntoBreadcrumbs(
    items: List<AntoBreadcrumbItem>,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val colorScheme = MaterialTheme.colorScheme
    val hostContentBackdrop = com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> backdrop
    }

    Row(
        modifier = modifier
            .antoLiquidGlass(
                backdrop = effectiveBackdrop,
                shape = Capsule(),
                role = AntoGlassRole.Navigation
            )
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            val isLast = index == items.size - 1

            AntoBreadcrumbItemView(
                item = item,
                isLast = isLast
            )

            if (!isLast) {
                Icon(
                    imageVector = AntoIcons.ChevronRight,
                    contentDescription = null,
                    tint = colorScheme.onSurface.copy(alpha = 0.40f),
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

@Composable
private fun AntoBreadcrumbItemView(
    item: AntoBreadcrumbItem,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val isClickable = item.onClick != null && !isLast
    val highlight = rememberAntoControlHighlight()

    Box(
        modifier = modifier
            .graphicsLayer(antoControlLayerBlock(isClickable, highlight) ?: {})
            .antoControlPressFeedback(
                enabled = isClickable,
                interactiveHighlight = highlight,
                drawHighlightOverlay = false
            )
            .then(
                if (isClickable) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        onClick = { item.onClick?.invoke() }
                    )
                } else Modifier
            )
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isLast) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.5.sp
            ),
            color = if (isLast) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.72f)
        )
    }
}
