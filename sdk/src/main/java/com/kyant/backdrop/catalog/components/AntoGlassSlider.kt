package com.kyant.backdrop.catalog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastRoundToInt
import androidx.compose.ui.util.lerp
import com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberBackdrop
import com.kyant.backdrop.backdrops.rememberCombinedBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.catalog.utils.DampedDragAnimation
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.InnerShadow
import com.kyant.backdrop.shadow.Shadow
import com.kyant.shapes.Capsule
import kotlinx.coroutines.flow.collectLatest

/**
 * AntoGlassSlider - Liquid Glass Floating Thumb Slider.
 * Features a sleek track with an optical liquid glass floating thumb that elastically squashes and stretches on drag.
 */
@Composable
fun AntoGlassSlider(
    value: () -> Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    visibilityThreshold: Float,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    tint: Color = Color.Unspecified,
    trackHeight: Dp = 6.dp,
    thumbSize: Dp = 26.dp
) {
    val isLightTheme = !isSystemInDarkTheme()
    val accentColor = if (tint.isSpecified) tint else MaterialTheme.colorScheme.primary
    val trackColor =
        if (isLightTheme) Color(0xFF787878).copy(0.2f)
        else Color(0xFF787880).copy(0.36f)

    val hostContentBackdrop = LocalAntoGlassContentBackdrop.current
    val effectiveBackdrop: Backdrop = when {
        backdrop != emptyBackdrop() -> backdrop
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> emptyBackdrop()
    }
    val trackBackdrop = rememberLayerBackdrop()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val trackWidth = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        val animationScope = rememberCoroutineScope()

        val dampedDragAnimation = remember(animationScope) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = value(),
                valueRange = valueRange,
                visibilityThreshold = visibilityThreshold,
                initialScale = 1f,
                pressedScale = 1.45f,
                onDragStarted = { },
                onDragStopped = { },
                onDrag = { _, _ -> }
            )
        }

        LaunchedEffect(dampedDragAnimation) {
            snapshotFlow { value() }
                .collectLatest { curVal ->
                    if (dampedDragAnimation.targetValue != curVal) {
                        dampedDragAnimation.updateValue(curVal)
                    }
                }
        }

        val updateFromPosition: (Float) -> Unit = { posX ->
            val rangeSpan = valueRange.endInclusive - valueRange.start
            val fraction = (posX / trackWidth).fastCoerceIn(0f, 1f)
            val targetVal = if (isLtr) valueRange.start + fraction * rangeSpan
                            else valueRange.endInclusive - fraction * rangeSpan
            dampedDragAnimation.updateValue(targetVal)
            onValueChange(targetVal)
        }

        val animateFromPosition: (Float) -> Unit = { posX ->
            val rangeSpan = valueRange.endInclusive - valueRange.start
            val fraction = (posX / trackWidth).fastCoerceIn(0f, 1f)
            val targetVal = if (isLtr) valueRange.start + fraction * rangeSpan
                            else valueRange.endInclusive - fraction * rangeSpan
            dampedDragAnimation.animateToValue(targetVal)
            onValueChange(targetVal)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .pointerInput(enabled, trackWidth, valueRange, isLtr) {
                    if (!enabled) return@pointerInput
                    detectTapGestures(
                        onPress = { offset ->
                            dampedDragAnimation.press()
                            animateFromPosition(offset.x)
                            tryAwaitRelease()
                            dampedDragAnimation.release()
                        },
                        onTap = { offset ->
                            animateFromPosition(offset.x)
                        }
                    )
                }
                .pointerInput(enabled, trackWidth, valueRange, isLtr) {
                    if (!enabled) return@pointerInput
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            dampedDragAnimation.press()
                            updateFromPosition(offset.x)
                        },
                        onDragEnd = {
                            dampedDragAnimation.release()
                        },
                        onDragCancel = {
                            dampedDragAnimation.release()
                        },
                        onHorizontalDrag = { change, _ ->
                            change.consume()
                            updateFromPosition(change.position.x)
                        }
                    )
                },
            contentAlignment = Alignment.CenterStart
        ) {
            // Track Layer
            Box(
                Modifier
                    .fillMaxWidth()
                    .layerBackdrop(trackBackdrop)
            ) {
                Box(
                    Modifier
                        .clip(Capsule())
                        .background(trackColor)
                        .height(trackHeight)
                        .fillMaxWidth()
                )

                Box(
                    Modifier
                        .clip(Capsule())
                        .background(accentColor)
                        .height(trackHeight)
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            val width = (constraints.maxWidth * dampedDragAnimation.progress).fastRoundToInt()
                            layout(width, placeable.height) {
                                placeable.place(0, 0)
                            }
                        }
                )
            }

            // Floating Optical Lens Thumb with Snell Refraction & Specular Highlights
            Box(
                Modifier
                    .graphicsLayer {
                        translationX =
                            (-size.width / 2f + trackWidth * dampedDragAnimation.progress)
                                .fastCoerceIn(-size.width / 4f, trackWidth - size.width * 3f / 4f) * if (isLtr) 1f else -1f
                    }
                    .drawBackdrop(
                        backdrop = rememberCombinedBackdrop(
                            effectiveBackdrop,
                            rememberBackdrop(trackBackdrop) { drawBackdrop ->
                                val progress = dampedDragAnimation.pressProgress
                                val scaleX = lerp(0.88f, 1f, progress)
                                val scaleY = lerp(0.88f, 1f, progress)
                                scale(scaleX, scaleY) {
                                    drawBackdrop()
                                }
                            }
                        ),
                        shape = { Capsule() },
                        effects = {
                            val progress = dampedDragAnimation.pressProgress
                            blur((8f + 4f * progress).dp.toPx())
                            lens(
                                (8f + 6f * progress).dp.toPx(),
                                (14f + 8f * progress).dp.toPx(),
                                chromaticAberration = true
                            )
                        },
                        highlight = {
                            val progress = dampedDragAnimation.pressProgress
                            Highlight.Ambient.copy(
                                width = 1.2.dp,
                                blurRadius = 2.dp,
                                alpha = 0.65f + 0.35f * progress
                            )
                        },
                        shadow = {
                            Shadow(
                                radius = 6.dp,
                                color = Color.Black.copy(alpha = 0.12f)
                            )
                        },
                        innerShadow = {
                            val progress = dampedDragAnimation.pressProgress
                            InnerShadow(
                                radius = 3.dp,
                                alpha = 0.35f + 0.35f * progress
                            )
                        },
                        layerBlock = {
                            scaleX = dampedDragAnimation.scaleX
                            scaleY = dampedDragAnimation.scaleY
                            val velocity = dampedDragAnimation.velocity / 10f
                            scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                            scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                        },
                        onDrawSurface = {
                            val progress = dampedDragAnimation.pressProgress
                            val surfaceAlpha = if (isLightTheme) 0.18f - 0.08f * progress else 0.09f - 0.04f * progress
                            drawRect(Color.White.copy(alpha = surfaceAlpha))
                        }
                    )
                    .size(width = thumbSize * 1.46f, height = thumbSize * 0.92f)
            )
        }
    }
}

