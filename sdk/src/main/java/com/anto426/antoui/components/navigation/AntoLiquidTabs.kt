package com.anto426.antoui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.catalog.components.AntoGlassBottomTab
import com.kyant.backdrop.catalog.components.AntoGlassBottomTabs
import com.kyant.shapes.Capsule

/**
 * Data item representing an AntoTab.
 */
data class AntoTabData(
    val label: String,
    val icon: ImageVector? = null,
    val badge: String? = null
)

/**
 * AntoLiquidTabRow - Original Optical Liquid Glass Tab Row with sliding droplet indicator.
 */
@Composable
fun AntoLiquidTabRow(
    selectedTabIndex: () -> Int,
    onTabSelected: (Int) -> Unit,
    tabsCount: Int,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    content: @Composable RowScope.() -> Unit
) {
    val hostContentBackdrop = com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop.current
    val effectiveBackdrop = if (backdropState != emptyBackdrop()) backdropState else (hostContentBackdrop ?: backdrop)

    AntoGlassBottomTabs(
        selectedTabIndex = selectedTabIndex,
        onTabSelected = onTabSelected,
        backdrop = effectiveBackdrop,
        tabsCount = tabsCount.coerceAtLeast(1),
        modifier = modifier,
        content = content
    )
}

/**
 * AntoLiquidTabRow with AntoTabData list.
 * Automatically adapts typography, spacing, icon sizing, and layout based on element count.
 */
@Composable
fun AntoLiquidTabRow(
    tabs: List<AntoTabData>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val isLightTheme = !isSystemInDarkTheme()
    val contentColor = if (isLightTheme) Color.Black.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.85f)

    AntoLiquidTabRow(
        selectedTabIndex = { selectedIndex },
        onTabSelected = onTabSelected,
        tabsCount = tabs.size,
        modifier = modifier,
        backdropState = backdropState
    ) {
        tabs.forEachIndexed { index, tab ->
            AntoGlassBottomTab(onClick = { onTabSelected(index) }) {
                AdaptiveTabItem(
                    tab = tab,
                    tabsCount = tabs.size,
                    contentColor = contentColor
                )
            }
        }
    }
}

/**
 * AntoLiquidTabRow with simple string items.
 * Intelligently scales font size, padding, and text truncation based on element count.
 */
@JvmName("AntoLiquidTabRowStrings")
@Composable
fun AntoLiquidTabRow(
    items: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val isLightTheme = !isSystemInDarkTheme()
    val contentColor = if (isLightTheme) Color.Black.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.85f)

    AntoLiquidTabRow(
        selectedTabIndex = { selectedIndex },
        onTabSelected = onTabSelected,
        tabsCount = items.size,
        modifier = modifier,
        backdropState = backdropState
    ) {
        items.forEachIndexed { index, label ->
            AntoGlassBottomTab(onClick = { onTabSelected(index) }) {
                AdaptiveStringTabItem(
                    label = label,
                    tabsCount = items.size,
                    contentColor = contentColor
                )
            }
        }
    }
}

/**
 * Smart adaptive tab item that dynamically chooses font size, icon size, spacing,
 * and layout (horizontal vs compact vertical) depending on tabs count and text length.
 */
@Composable
private fun AdaptiveTabItem(
    tab: AntoTabData,
    tabsCount: Int,
    contentColor: Color
) {
    val count = tabsCount.coerceAtLeast(1)
    val hasIcon = tab.icon != null
    val hasBadge = !tab.badge.isNullOrEmpty()
    val isLongText = tab.label.length > 7

    val fontSize = when {
        count <= 2 -> 14.5.sp
        count == 3 -> 13.5.sp
        count == 4 -> if (hasIcon || isLongText) 12.sp else 13.sp
        else -> if (hasIcon) 11.sp else 12.sp
    }

    val iconSize = when {
        count <= 2 -> 18.dp
        count == 3 -> 17.dp
        count == 4 -> 15.dp
        else -> 14.dp
    }

    val spacing = when {
        count <= 2 -> 7.dp
        count == 3 -> 5.dp
        count == 4 -> 4.dp
        else -> 3.dp
    }

    val horizontalPadding = when {
        count <= 2 -> 8.dp
        count == 3 -> 6.dp
        count == 4 -> 4.dp
        else -> 2.dp
    }

    val useVerticalLayout = count >= 4 && hasIcon && isLongText

    if (useVerticalLayout) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = tab.icon!!,
                contentDescription = tab.label,
                tint = contentColor,
                modifier = Modifier.size(iconSize)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterHorizontally)
            ) {
                Text(
                    text = tab.label,
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = fontSize,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                if (hasBadge) {
                    CompactBadge(badge = tab.badge!!, contentColor = contentColor, count = count)
                }
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally)
        ) {
            if (hasIcon) {
                Icon(
                    imageVector = tab.icon!!,
                    contentDescription = tab.label,
                    tint = contentColor,
                    modifier = Modifier.size(iconSize)
                )
            }
            Text(
                text = tab.label,
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = fontSize,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            if (hasBadge) {
                CompactBadge(badge = tab.badge!!, contentColor = contentColor, count = count)
            }
        }
    }
}

/**
 * Smart adaptive string tab item scaling font size and padding dynamically.
 */
@Composable
private fun AdaptiveStringTabItem(
    label: String,
    tabsCount: Int,
    contentColor: Color
) {
    val count = tabsCount.coerceAtLeast(1)
    val fontSize = when {
        count <= 2 -> 14.5.sp
        count == 3 -> 13.5.sp
        count == 4 -> 12.5.sp
        count == 5 -> 12.sp
        else -> 11.sp
    }

    val horizontalPadding = when {
        count <= 2 -> 8.dp
        count == 3 -> 6.dp
        count == 4 -> 4.dp
        else -> 2.dp
    }

    Text(
        text = label,
        color = contentColor,
        fontWeight = FontWeight.SemiBold,
        fontSize = fontSize,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = horizontalPadding)
    )
}

/**
 * Compact high-contrast badge adapted to available space.
 */
@Composable
private fun CompactBadge(
    badge: String,
    contentColor: Color,
    count: Int
) {
    val hPad = if (count <= 3) 6.dp else 4.dp
    val vPad = if (count <= 3) 2.dp else 1.dp
    val fSize = if (count <= 3) 10.sp else 9.sp

    Box(
        modifier = Modifier
            .clip(Capsule())
            .background(contentColor.copy(alpha = 0.15f))
            .padding(horizontal = hPad, vertical = vPad),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = badge,
            color = contentColor,
            fontSize = fSize,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false
        )
    }
}

/**
 * Individual tab item scope receiver.
 */
@Composable
fun RowScope.AntoLiquidTab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    AntoGlassBottomTab(
        onClick = onClick,
        modifier = modifier,
        content = content
    )
}
