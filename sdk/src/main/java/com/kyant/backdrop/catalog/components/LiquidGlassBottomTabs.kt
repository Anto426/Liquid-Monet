package com.kyant.backdrop.catalog.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.LiquidGlassStyleManager
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.resolveLiquidGlassBackdrop
import com.anto426.liquidmonet.glass.runtime.LiquidGlassMotionSpecs
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPresets
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.catalog.utils.DampedDragAnimation
import com.kyant.backdrop.catalog.utils.InteractiveHighlight
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.shapes.Capsule
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

/** Internal renderer kept aligned with the AndroidLiquidGlass reference motion model. */
@Composable
internal fun LiquidGlassBottomTabs(
    selectedTabIndex: () -> Int,
    onTabSelected: (index: Int) -> Unit,
    backdrop: Backdrop,
    tabsCount: Int,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val isLightTheme = !isSystemInDarkTheme()
    val accentColor = MaterialTheme.colorScheme.primary
    val navigationStyle = LiquidGlassStyleManager.resolve(LiquidGlassRole.Navigation)
    val containerColor = MaterialTheme.colorScheme.surface.copy(
        alpha = if (isLightTheme) {
            navigationStyle.lightSurfaceAlpha
        } else {
            navigationStyle.darkSurfaceAlpha
        }
    )
    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdrop)
    val performance = LocalLiquidGlassPerformance.current
    val navigationTokens = LiquidGlassPresets.Navigation.resolve(performance)
    val dropletTokens = LiquidGlassPresets.Interactive.resolve(performance)
    val currentPerformance by rememberUpdatedState(performance)
    val currentSelectedTabIndex by rememberUpdatedState(selectedTabIndex)
    val currentOnTabSelected by rememberUpdatedState(onTabSelected)

    val tabsBackdrop = rememberLayerBackdrop()

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        val density = LocalDensity.current
        val safeTabsCount = tabsCount.coerceAtLeast(1)
        val maxIndex = safeTabsCount - 1
        val tabWidth = with(density) {
            (constraints.maxWidth.toFloat() - 8.dp.toPx()).coerceAtLeast(1f) / safeTabsCount
        }

        val offsetAnimation = remember { Animatable(0f) }
        val panelOffset by remember(density, constraints.maxWidth) {
            derivedStateOf {
                val width = constraints.maxWidth.toFloat().coerceAtLeast(1f)
                val fraction = (offsetAnimation.value / width).fastCoerceIn(-1f, 1f)
                with(density) {
                    4.dp.toPx() * fraction.sign * EaseOut.transform(abs(fraction))
                }
            }
        }

        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        val currentTabWidth by rememberUpdatedState(tabWidth)
        val currentIsLtr by rememberUpdatedState(isLtr)
        val currentPanelOffset by rememberUpdatedState(panelOffset)
        val animationScope = rememberCoroutineScope()
        var currentIndex by remember(safeTabsCount) {
            mutableIntStateOf(selectedTabIndex().coerceIn(0, maxIndex))
        }
        val dampedDragAnimation = remember(animationScope, safeTabsCount) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = selectedTabIndex().coerceIn(0, maxIndex).toFloat(),
                valueRange = 0f..maxIndex.toFloat(),
                visibilityThreshold = 0.001f,
                initialScale = 1f,
                pressedScale = 78f / 56f,
                onDragStarted = {},
                onDragStopped = {
                    currentIndex = targetValue.fastRoundToInt().coerceIn(0, maxIndex)
                    animationScope.launch {
                        offsetAnimation.animateTo(
                            targetValue = 0f,
                            animationSpec = LiquidGlassMotionSpecs.spring(
                                performance = currentPerformance,
                                dampingRatio = 1f,
                                stiffness = 300f
                            )
                        )
                    }
                },
                onDrag = { _, dragAmount ->
                    updateValue(
                        (targetValue +
                            dragAmount.x / currentTabWidth * if (currentIsLtr) 1f else -1f)
                            .fastCoerceIn(0f, maxIndex.toFloat())
                    )
                    animationScope.launch {
                        offsetAnimation.snapTo(offsetAnimation.value + dragAmount.x)
                    }
                }
            )
        }

        LaunchedEffect(dampedDragAnimation) {
            snapshotFlow { currentSelectedTabIndex() }
                .collectLatest { index -> currentIndex = index.coerceIn(0, maxIndex) }
        }
        LaunchedEffect(dampedDragAnimation) {
            snapshotFlow { currentIndex }
                .drop(1)
                .collectLatest { index ->
                    dampedDragAnimation.animateToValue(index.toFloat())
                    currentOnTabSelected(index)
                }
        }

        val interactiveHighlight = remember(animationScope, dampedDragAnimation) {
            InteractiveHighlight(
                animationScope = animationScope,
                position = { size, _ ->
                    Offset(
                        x = if (currentIsLtr) {
                            (dampedDragAnimation.value + 0.5f) * currentTabWidth + currentPanelOffset
                        } else {
                            size.width -
                                (dampedDragAnimation.value + 0.5f) * currentTabWidth +
                                currentPanelOffset
                        },
                        y = size.height / 2f
                    )
                }
            )
        }
        val dropletInteractionProgress by remember(dampedDragAnimation) {
            derivedStateOf {
                val displacementProgress = abs(
                    dampedDragAnimation.targetValue - dampedDragAnimation.value
                ).fastCoerceIn(0f, 1f)
                val velocityProgress =
                    (abs(dampedDragAnimation.velocity) / 4f).fastCoerceIn(0f, 1f)
                maxOf(
                    dampedDragAnimation.pressProgress,
                    displacementProgress,
                    velocityProgress
                ).fastCoerceIn(0f, 1f)
            }
        }

        Row(
            modifier = Modifier
                .graphicsLayer { translationX = panelOffset }
                .liquidGlass(
                    backdrop = effectiveBackdrop,
                    shape = Capsule(),
                    role = LiquidGlassRole.Navigation,
                    containerColor = containerColor,
                    preset = LiquidGlassPresets.Navigation,
                    layerBlock = {
                        val progress = dampedDragAnimation.pressProgress
                        val scale = if (size.width > 0f) {
                            lerp(1f, 1f + 16.dp.toPx() / size.width, progress)
                        } else {
                            1f
                        }
                        scaleX = scale
                        scaleY = scale
                    }
                )
                .then(interactiveHighlight.modifier)
                .height(64.dp)
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )

        // This off-screen render is the reference's color-mask pass for the moving droplet.
        // It is not interactive or exposed to accessibility.
        CompositionLocalProvider(
            LocalLiquidBottomTabScale provides {
                lerp(1f, 1.2f, dampedDragAnimation.pressProgress)
            }
        ) {
            Row(
                modifier = Modifier
                    .clearAndSetSemantics {}
                    .alpha(0f)
                    .layerBackdrop(tabsBackdrop)
                    .graphicsLayer { translationX = panelOffset }
                    .drawBackdrop(
                        backdrop = effectiveBackdrop,
                        shape = { Capsule() },
                        effects = {
                            val progress = dampedDragAnimation.pressProgress
                            vibrancy()
                            if (navigationTokens.blurRadius > 0.dp) {
                                blur(navigationTokens.blurRadius.toPx())
                            }
                            val refractionHeight = navigationTokens.refractionHeight * progress
                            val refractionAmount = navigationTokens.refractionAmount * progress
                            if (
                                size.isSpecified &&
                                size.minDimension > 0f &&
                                refractionHeight > 0.dp &&
                                refractionAmount > 0.dp
                            ) {
                                lens(
                                    refractionHeight.toPx(),
                                    refractionAmount.toPx(),
                                    chromaticAberration =
                                        navigationTokens.chromaticAberration >= 0.08f
                                )
                            }
                        },
                        highlight = {
                            Highlight.Default.copy(alpha = dampedDragAnimation.pressProgress)
                        },
                        onDrawSurface = { drawRect(containerColor) }
                    )
                    .then(interactiveHighlight.modifier)
                    .height(56.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .graphicsLayer(colorFilter = ColorFilter.tint(accentColor)),
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .graphicsLayer {
                    translationX = if (isLtr) {
                        dampedDragAnimation.value * tabWidth + panelOffset
                    } else {
                        size.width - (dampedDragAnimation.value + 1f) * tabWidth + panelOffset
                    }
                }
                .then(interactiveHighlight.gestureModifier)
                .then(dampedDragAnimation.modifier)
                .drawBackdrop(
                    backdrop = rememberCombinedBackdrop(effectiveBackdrop, tabsBackdrop),
                    shape = { Capsule() },
                    effects = {
                        val refractionStrength = lerp(
                            start = 0.28f,
                            stop = 0.52f,
                            fraction = dropletInteractionProgress
                        )
                        val refractionHeight = dropletTokens.refractionHeight * refractionStrength
                        val refractionAmount = dropletTokens.refractionAmount * refractionStrength
                        if (
                            size.isSpecified &&
                            size.minDimension > 0f &&
                            refractionHeight > 0.dp &&
                            refractionAmount > 0.dp
                        ) {
                            lens(
                                refractionHeight.toPx(),
                                refractionAmount.toPx(),
                                chromaticAberration = dropletTokens.chromaticAberration >= 0.08f
                            )
                        }
                    },
                    highlight = {
                        Highlight.Default.copy(alpha = dampedDragAnimation.pressProgress)
                    },
                    shadow = { Shadow(alpha = dampedDragAnimation.pressProgress) },
                    innerShadow = {
                        val progress = dampedDragAnimation.pressProgress
                        InnerShadow(radius = 8.dp * progress, alpha = progress)
                    },
                    layerBlock = {
                        scaleX = dampedDragAnimation.scaleX
                        scaleY = dampedDragAnimation.scaleY
                        val velocity = dampedDragAnimation.velocity / 10f
                        scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                        scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                    },
                    onDrawSurface = {
                        val liquidStrength = performance.liquidIntensity.coerceIn(0f, 1f)
                        val panelTintAlpha = (if (isLightTheme) 0.07f else 0.05f) *
                            (0.4f + 0.6f * liquidStrength)
                        val dropletTintAlpha = lerp(
                            start = panelTintAlpha,
                            stop = (panelTintAlpha * 1.25f).coerceAtMost(0.09f),
                            fraction = dropletInteractionProgress
                        )
                        drawRect(accentColor.copy(alpha = dropletTintAlpha))
                    }
                )
                .height(56.dp)
                .fillMaxWidth(1f / safeTabsCount)
        )
    }
}
