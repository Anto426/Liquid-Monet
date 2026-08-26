package com.anto426.antoui.components.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
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
import com.anto426.antoui.components.inputs.AntoSearchBar
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.LocalAntoGlassTopBarScrollBehavior
import com.anto426.antoui.glass.antoLiquidGlass
import com.anto426.antoui.icons.AntoIcons
import com.anto426.antoui.components.menu.AntoGlassMorphingAction
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.luminance
import com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow

data class AntoTopBarAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit = {},
    val subItems: List<AntoTopBarAction> = emptyList(),
    val iconRotation: Float = 0f,
    val selected: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AntoExpressiveTopBar(
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
    actionItems: List<AntoTopBarAction> = emptyList(),
    isSearchActive: Boolean = false,
    searchQuery: String = "",
    onQueryChange: (String) -> Unit = {},
    onSearchActiveChange: ((Boolean) -> Unit)? = null,
    searchPlaceholder: String = "Cerca...",
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val isLightSurface = colorScheme.surface.luminance() > 0.5f
    val performance = com.anto426.antoui.glass.runtime.LocalAntoGlassPerformance.current
    val liquidStrength = performance.liquidIntensity.coerceIn(0f, 1f)
    val hostContentBackdrop = LocalAntoGlassContentBackdrop.current
    val effectiveBackdrop: Backdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        backdrop != emptyBackdrop() -> backdrop
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> emptyBackdrop()
    }
    val effectiveScrollBehavior = scrollBehavior ?: LocalAntoGlassTopBarScrollBehavior.current

    val collapsedFraction = effectiveScrollBehavior?.state?.collapsedFraction ?: 0f
    val isScrolled = (effectiveScrollBehavior?.state?.contentOffset ?: 0f) < -1f || collapsedFraction > 0.01f
    val animatedScrolledAlpha by animateFloatAsState(
        targetValue = if (isScrolled) 1f else 0f,
        animationSpec = tween(durationMillis = 260),
        label = "topBarRefractionAlpha"
    )

    val defaultAlpha = if (isLightSurface) 0.26f else 0.20f
    val surfaceColor = if (effectiveBackdrop == emptyBackdrop()) {
        colorScheme.surfaceContainerHigh.copy(alpha = if (isLightSurface) 0.88f else 0.82f)
    } else {
        colorScheme.surface.copy(alpha = defaultAlpha)
    }
    val subtleMonetTint = colorScheme.primary.copy(
        alpha = if (isLightSurface) 0.07f * (0.4f + 0.6f * liquidStrength)
        else 0.05f * (0.4f + 0.6f * liquidStrength)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(surfaceColor)
    ) {
        LargeTopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Column {
                    AntoTopBarTitle(
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
                    AntoBackButton(
                        onClick = onNavigationClick,
                        backdropState = backdropState
                    )
                } else {
                    navigationIcon()
                }
            },
            actions = {
                actionItems.forEachIndexed { index, action ->
                    AntoGlassMorphingAction(
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
                AntoSearchBar(
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

@Composable
private fun AntoTopBarTitle(
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
fun AntoBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backdropState: Backdrop = emptyBackdrop(),
    contentDescription: String? = null
) {
    val contentColor = MaterialTheme.colorScheme.onSurface
    val shape = Capsule()
    val interactiveHighlight = rememberAntoControlHighlight()

    Box(
        modifier = modifier
            .padding(start = 12.dp, end = 4.dp)
            .size(40.dp)
            .antoLiquidGlass(
                backdrop = backdropState,
                shape = shape,
                role = AntoGlassRole.Navigation,
                layerBlock = antoControlLayerBlock(true, interactiveHighlight)
            )
            .antoControlPressFeedback(
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
            imageVector = AntoIcons.ArrowBack,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun AntoTopBarIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconRotation: Float = 0f,
    backdropState: Backdrop = emptyBackdrop()
) {
    val contentColor = MaterialTheme.colorScheme.onSurface
    val shape = Capsule()
    val interactiveHighlight = rememberAntoControlHighlight()

    Box(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .size(40.dp)
            .antoLiquidGlass(
                backdrop = backdropState,
                shape = shape,
                role = AntoGlassRole.Navigation,
                layerBlock = antoControlLayerBlock(true, interactiveHighlight)
            )
            .antoControlPressFeedback(
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
