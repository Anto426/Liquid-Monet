package com.anto426.antoui.components.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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

data class AntoNavItemData(
    val label: String,
    val route: String = "",
    val icon: ImageVector? = null,
    val badge: String? = null
) {
    constructor(icon: ImageVector, label: String) : this(label = label, route = "", icon = icon, badge = null)
    constructor(icon: ImageVector, label: String, badge: String?) : this(label = label, route = "", icon = icon, badge = badge)
}

/**
 * AntoFluidNavigationBar - Liquid Glass Bottom Navigation Bar with smart adaptive layout.
 * Dynamically calibrates icon size, font size, badge placement, and text truncation based on element count.
 */
@Composable
fun AntoFluidNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    items: List<AntoNavItemData>,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val effectiveBackdrop = if (backdropState != emptyBackdrop()) backdropState else backdrop
    val isLightTheme = !isSystemInDarkTheme()
    val contentColor = if (isLightTheme) Color.Black.copy(alpha = 0.80f) else Color.White.copy(alpha = 0.80f)

    val navBarOffsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 120.dp,
        animationSpec = spring(
            dampingRatio = 0.85f,
            stiffness = 400f
        ),
        label = "navBarOffsetY"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = navBarOffsetY.toPx()
            }
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        AntoGlassBottomTabs(
            selectedTabIndex = { selectedIndex },
            onTabSelected = onItemSelected,
            backdrop = effectiveBackdrop,
            tabsCount = items.size.coerceAtLeast(1)
        ) {
            for (index in items.indices) {
                val item = items[index]
                AntoGlassBottomTab(
                    onClick = { onItemSelected(index) }
                ) {
                    AdaptiveNavItem(
                        item = item,
                        itemsCount = items.size,
                        contentColor = contentColor
                    )
                }
            }
        }
    }
}

/**
 * Smart adaptive navigation item for bottom bars.
 */
@Composable
private fun AdaptiveNavItem(
    item: AntoNavItemData,
    itemsCount: Int,
    contentColor: Color
) {
    val count = itemsCount.coerceAtLeast(1)
    val hasIcon = item.icon != null
    val hasBadge = !item.badge.isNullOrEmpty()

    val iconSize = when {
        count <= 3 -> 22.dp
        count == 4 -> 20.dp
        else -> 18.dp
    }

    val fontSize = when {
        !hasIcon -> if (count <= 3) 13.5.sp else 12.sp
        count <= 3 -> 11.5.sp
        count == 4 -> 11.sp
        else -> 10.sp
    }

    val spacing = when {
        count <= 3 -> 2.dp
        count == 4 -> 2.dp
        else -> 1.5.dp
    }

    val horizontalPadding = when {
        count <= 3 -> 6.dp
        count == 4 -> 4.dp
        else -> 2.dp
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterVertically)
    ) {
        if (hasIcon) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = item.icon!!,
                    contentDescription = item.label,
                    modifier = Modifier.size(iconSize),
                    tint = contentColor
                )
                if (hasBadge) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 6.dp, y = (-3).dp)
                            .clip(Capsule())
                            .background(contentColor.copy(alpha = 0.20f))
                            .padding(horizontal = 4.dp, vertical = 1.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.badge!!,
                            color = contentColor,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        if (item.label.isNotEmpty()) {
            Text(
                text = item.label,
                color = contentColor,
                fontSize = fontSize,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AntoNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    tabsCount: Int,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    content: @Composable RowScope.() -> Unit
) {
    val effectiveBackdrop = if (backdropState != emptyBackdrop()) backdropState else backdrop

    val navBarOffsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 120.dp,
        animationSpec = spring(
            dampingRatio = 0.85f,
            stiffness = 400f
        ),
        label = "navBarOffsetY"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = navBarOffsetY.toPx()
            }
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        AntoGlassBottomTabs(
            selectedTabIndex = { selectedIndex },
            onTabSelected = onItemSelected,
            backdrop = effectiveBackdrop,
            tabsCount = tabsCount,
            content = content
        )
    }
}

@Composable
fun RowScope.AntoNavigationItem(
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
