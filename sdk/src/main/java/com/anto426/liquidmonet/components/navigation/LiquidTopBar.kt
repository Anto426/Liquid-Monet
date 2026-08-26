package com.anto426.liquidmonet.components.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.statusBarsPadding
import com.anto426.liquidmonet.components.inputs.LiquidSearchBar
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.LocalLiquidGlassTopBarScrollBehavior
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.resolveLiquidGlassBackdrop
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop
import com.anto426.liquidmonet.icons.LiquidIcons
import com.anto426.liquidmonet.components.menu.LiquidMorphingAction
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.shapes.Capsule

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.RectangleShape

data class LiquidTopBarAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit = {},
    val subItems: List<LiquidTopBarAction> = emptyList(),
    val iconRotation: Float = 0f,
    val selected: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    statusBadge: String? = null,
    compactTitle: Boolean = false,
    showNavigationIcon: Boolean = false,
    onNavigationClick: (() -> Unit)? = null,
    navigationIcon: @Composable () -> Unit = {},
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actionItems: List<LiquidTopBarAction> = emptyList(),
    isSearchActive: Boolean = false,
    searchQuery: String = "",
    onQueryChange: (String) -> Unit = {},
    onSearchActiveChange: ((Boolean) -> Unit)? = null,
    searchPlaceholder: String = "Cerca...",
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdrop, backdropState)
    val surfaceBackdrop = rememberLayerBackdrop()
    val effectiveScrollBehavior = scrollBehavior ?: LocalLiquidGlassTopBarScrollBehavior.current

    val collapsedFraction = effectiveScrollBehavior?.state?.collapsedFraction ?: 0f
    val isScrolled = (effectiveScrollBehavior?.state?.contentOffset ?: 0f) < -1f || collapsedFraction > 0.01f
    val animatedScrolledAlpha by animateFloatAsState(
        targetValue = if (isScrolled) 1f else 0f,
        animationSpec = tween(durationMillis = 260),
        label = "topBarRefractionAlpha"
    )

    val topBarTintAlpha = lerp(0.05f, 0.12f, animatedScrolledAlpha)
    val surfaceBrush = Brush.verticalGradient(
        colorStops = arrayOf(
            0f to colorScheme.surface.copy(alpha = topBarTintAlpha),
            0.36f to colorScheme.surface.copy(alpha = topBarTintAlpha * 0.72f),
            0.72f to colorScheme.surface.copy(alpha = topBarTintAlpha * 0.18f),
            1f to Color.Transparent
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = RectangleShape,
                role = LiquidGlassRole.TopBar,
                containerColor = Color.Transparent,
                exportedBackdrop = surfaceBackdrop
            )
            .background(surfaceBrush)
    ) {
        CompositionLocalProvider(LocalLiquidGlassContentBackdrop provides surfaceBackdrop) {
            LargeTopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Column {
                    LiquidTopBarTitle(
                        title = title,
                        statusBadge = statusBadge,
                        compactTitle = compactTitle,
                        collapsedFraction = collapsedFraction
                    )
                    if (!subtitle.isNullOrBlank()) {
                        val subtitleAlpha = (1f - (collapsedFraction * 2.5f)).coerceIn(0f, 1f)
                        if (subtitleAlpha > 0.01f) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.graphicsLayer { alpha = subtitleAlpha }
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                if (showNavigationIcon && onNavigationClick != null) {
                    LiquidBackButton(
                        onClick = onNavigationClick,
                        backdropState = backdropState
                    )
                } else {
                    navigationIcon()
                }
            },
            actions = {
                actionItems.forEachIndexed { index, action ->
                    LiquidMorphingAction(
                        action = action,
                        isLastItem = index == actionItems.lastIndex,
                        backdropState = backdropState
                    )
                }
                actions?.invoke(this)
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            ),
            scrollBehavior = effectiveScrollBehavior
            )

            // Expandable Liquid Glass Search Bar (Appears below the TopBar smoothly without hiding the TopBar!)
            AnimatedVisibility(
            visible = isSearchActive,
            enter = expandVertically(
                animationSpec = spring(
                    dampingRatio = 0.75f,
                    stiffness = 380f
                )
            ) + fadeIn(tween(220)),
            exit = shrinkVertically(
                animationSpec = spring(
                    dampingRatio = 0.85f,
                    stiffness = 450f
                )
            ) + fadeOut(tween(180))
            ) {
                Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                ) {
                    LiquidSearchBar(
                    query = searchQuery,
                    onQueryChange = onQueryChange,
                    placeholderText = searchPlaceholder,
                    backdropState = backdropState,
                    modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun LiquidTopBarTitle(
    title: String,
    statusBadge: String?,
    compactTitle: Boolean,
    collapsedFraction: Float
) {
    val expandedSize = if (compactTitle) 22f else 28f
    val collapsedSize = 18f
    val currentSize = lerp(expandedSize, collapsedSize, collapsedFraction)
    val badgeAlpha = (1f - (collapsedFraction * 2.5f)).coerceIn(0f, 1f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = currentSize.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (!statusBadge.isNullOrBlank() && badgeAlpha > 0.05f) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .graphicsLayer { alpha = badgeAlpha }
                    .clip(Capsule())
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = statusBadge,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun LiquidBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backdropState: Backdrop = emptyBackdrop(),
    contentDescription: String? = null
) {
    val contentColor = MaterialTheme.colorScheme.onSurface
    val shape = Capsule()
    val interactiveHighlight = rememberLiquidControlHighlight()

    Box(
        modifier = modifier
            .padding(start = 12.dp, end = 4.dp)
            .size(40.dp)
            .liquidGlass(
                backdrop = backdropState,
                shape = shape,
                role = LiquidGlassRole.Navigation,
                layerBlock = liquidControlLayerBlock(true, interactiveHighlight)
            )
            .liquidControlPressFeedback(
                enabled = true,
                interactiveHighlight = interactiveHighlight
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = LiquidIcons.ArrowBack,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun LiquidTopBarIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconRotation: Float = 0f,
    backdropState: Backdrop = emptyBackdrop()
) {
    val contentColor = MaterialTheme.colorScheme.onSurface
    val shape = Capsule()
    val interactiveHighlight = rememberLiquidControlHighlight()

    Box(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .size(40.dp)
            .liquidGlass(
                backdrop = backdropState,
                shape = shape,
                role = LiquidGlassRole.Navigation,
                layerBlock = liquidControlLayerBlock(true, interactiveHighlight)
            )
            .liquidControlPressFeedback(
                enabled = true,
                interactiveHighlight = interactiveHighlight
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier
                .size(20.dp)
                .rotate(iconRotation)
        )
    }
}
