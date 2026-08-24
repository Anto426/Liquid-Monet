package com.anto426.antoui.components.selection

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
import androidx.compose.runtime.rememberUpdatedState
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
 * AntoRangeSlider - Optical Liquid Glass Dual-Thumb Range Slider.
 * Features a sleek track with two floating optical liquid glass lens thumbs that elastically squash and stretch on drag.
 */
@Composable
fun AntoRangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    tint: Color = Color.Unspecified,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    trackHeight: Dp = 6.dp,
    thumbSize: Dp = 26.dp
) {
    AntoRangeSlider(
        value = { value },
        onValueChange = onValueChange,
        modifier = modifier,
        valueRange = valueRange,
        steps = steps,
        enabled = enabled,
        tint = tint,
        backdrop = backdrop,
        backdropState = backdropState,
        trackHeight = trackHeight,
        thumbSize = thumbSize
    )
}

@Composable
fun AntoRangeSlider(
    value: () -> ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    tint: Color = Color.Unspecified,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
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
        backdropState != emptyBackdrop() -> backdropState
        backdrop != emptyBackdrop() -> backdrop
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> emptyBackdrop()
    }
    val trackBackdrop = rememberLayerBackdrop()

    val currentRangeState by rememberUpdatedState(value())
    val onValueChangeState by rememberUpdatedState(onValueChange)

    val visibilityThreshold = ((valueRange.endInclusive - valueRange.start) / 1000f).coerceAtLeast(0.0001f)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val trackWidth = constraints.maxWidth.toFloat().coerceAtLeast(1f)
        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        val animationScope = rememberCoroutineScope()

        val startDragAnimation = remember(animationScope) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = currentRangeState.start,
                valueRange = valueRange,
                visibilityThreshold = visibilityThreshold,
                initialScale = 1f,
                pressedScale = 1.45f,
                onDragStarted = { },
                onDragStopped = { },
                onDrag = { _, _ -> }
            )
        }

        val endDragAnimation = remember(animationScope) {
            DampedDragAnimation(
                animationScope = animationScope,
                initialValue = currentRangeState.endInclusive,
                valueRange = valueRange,
                visibilityThreshold = visibilityThreshold,
                initialScale = 1f,
                pressedScale = 1.45f,
                onDragStarted = { },
                onDragStopped = { },
                onDrag = { _, _ -> }
            )
        }

        LaunchedEffect(startDragAnimation) {
            snapshotFlow { value().start }
                .collectLatest { curStart ->
                    if (startDragAnimation.targetValue != curStart) {
                        startDragAnimation.updateValue(curStart)
                    }
                }
        }

        LaunchedEffect(endDragAnimation) {
            snapshotFlow { value().endInclusive }
                .collectLatest { curEnd ->
                    if (endDragAnimation.targetValue != curEnd) {
                        endDragAnimation.updateValue(curEnd)
                    }
                }
        }

        var activeThumb by remember { mutableStateOf<Int?>(null) } // 0: start, 1: end

        val selectThumb: (Float) -> Int = { posX ->
            val startX = trackWidth * startDragAnimation.progress
            val endX = trackWidth * endDragAnimation.progress
            if (kotlin.math.abs(posX - startX) <= kotlin.math.abs(posX - endX)) 0 else 1
        }

        val updateFromPosition: (Float, Int) -> Unit = { posX, thumb ->
            val rangeSpan = valueRange.endInclusive - valueRange.start
            val fraction = (posX / trackWidth).fastCoerceIn(0f, 1f)
            val targetVal = if (isLtr) valueRange.start + fraction * rangeSpan
                            else valueRange.endInclusive - fraction * rangeSpan
            val curRange = currentRangeState
            if (thumb == 0) {
                val clamped = targetVal.coerceIn(valueRange.start, curRange.endInclusive)
                startDragAnimation.updateValue(clamped)
                onValueChangeState(clamped..curRange.endInclusive)
            } else {
                val clamped = targetVal.coerceIn(curRange.start, valueRange.endInclusive)
                endDragAnimation.updateValue(clamped)
                onValueChangeState(curRange.start..clamped)
            }
        }

        val animateFromPosition: (Float, Int) -> Unit = { posX, thumb ->
            val rangeSpan = valueRange.endInclusive - valueRange.start
            val fraction = (posX / trackWidth).fastCoerceIn(0f, 1f)
            val targetVal = if (isLtr) valueRange.start + fraction * rangeSpan
                            else valueRange.endInclusive - fraction * rangeSpan
            val curRange = currentRangeState
            if (thumb == 0) {
                val clamped = targetVal.coerceIn(valueRange.start, curRange.endInclusive)
                startDragAnimation.animateToValue(clamped)
                onValueChangeState(clamped..curRange.endInclusive)
            } else {
                val clamped = targetVal.coerceIn(curRange.start, valueRange.endInclusive)
                endDragAnimation.animateToValue(clamped)
                onValueChangeState(curRange.start..clamped)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .pointerInput(enabled, trackWidth, valueRange, isLtr) {
                    if (!enabled) return@pointerInput
                    detectTapGestures(
                        onPress = { offset ->
                            val thumb = selectThumb(offset.x)
                            activeThumb = thumb
                            val anim = if (thumb == 0) startDragAnimation else endDragAnimation
                            anim.press()
                            animateFromPosition(offset.x, thumb)
                            tryAwaitRelease()
                            anim.release()
                            activeThumb = null
                        },
                        onTap = { offset ->
                            val thumb = selectThumb(offset.x)
                            animateFromPosition(offset.x, thumb)
                        }
                    )
                }
                .pointerInput(enabled, trackWidth, valueRange, isLtr) {
                    if (!enabled) return@pointerInput
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            val thumb = selectThumb(offset.x)
                            activeThumb = thumb
                            val anim = if (thumb == 0) startDragAnimation else endDragAnimation
                            anim.press()
                            updateFromPosition(offset.x, thumb)
                        },
                        onDragEnd = {
                            startDragAnimation.release()
                            endDragAnimation.release()
                            activeThumb = null
                        },
                        onDragCancel = {
                            startDragAnimation.release()
                            endDragAnimation.release()
                            activeThumb = null
                        },
                        onHorizontalDrag = { change, _ ->
                            change.consume()
                            activeThumb?.let { thumb ->
                                updateFromPosition(change.position.x, thumb)
                            }
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
                // Inactive Track Background
                Box(
                    Modifier
                        .clip(Capsule())
                        .background(trackColor)
                        .height(trackHeight)
                        .fillMaxWidth()
                )

                // Active Range Track Fill
                Box(
                    Modifier
                        .clip(Capsule())
                        .background(accentColor)
                        .height(trackHeight)
                        .layout { measurable, constraints ->
                            val startProg = startDragAnimation.progress.coerceIn(0f, 1f)
                            val endProg = endDragAnimation.progress.coerceIn(0f, 1f)
                            val minProg = minOf(startProg, endProg)
                            val maxProg = maxOf(startProg, endProg)
                            val startX = (constraints.maxWidth * minProg).fastRoundToInt()
                            val endX = (constraints.maxWidth * maxProg).fastRoundToInt()
                            val width = (endX - startX).coerceAtLeast(0)
                            val placeable = measurable.measure(
                                constraints.copy(minWidth = width, maxWidth = width)
                            )
                            layout(constraints.maxWidth, placeable.height) {
                                placeable.place(startX, 0)
                            }
                        }
                )
            }

            // Start Floating Optical Lens Thumb
            Box(
                Modifier
                    .graphicsLayer {
                        translationX =
                            (-size.width / 2f + trackWidth * startDragAnimation.progress)
                                .fastCoerceIn(-size.width / 4f, trackWidth - size.width * 3f / 4f) * if (isLtr) 1f else -1f
                    }
                    .drawBackdrop(
                        backdrop = rememberCombinedBackdrop(
                            effectiveBackdrop,
                            rememberBackdrop(trackBackdrop) { drawBackdrop ->
                                val progress = startDragAnimation.pressProgress
                                val scaleX = lerp(0.88f, 1f, progress)
                                val scaleY = lerp(0.88f, 1f, progress)
                                scale(scaleX, scaleY) {
                                    drawBackdrop()
                                }
                            }
                        ),
                        shape = { Capsule() },
                        effects = {
                            val progress = startDragAnimation.pressProgress
                            blur((8f + 4f * progress).dp.toPx())
                            lens(
                                (8f + 6f * progress).dp.toPx(),
                                (14f + 8f * progress).dp.toPx(),
                                chromaticAberration = true
                            )
                        },
                        highlight = {
                            val progress = startDragAnimation.pressProgress
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
                            val progress = startDragAnimation.pressProgress
                            InnerShadow(
                                radius = 3.dp,
                                alpha = 0.35f + 0.35f * progress
                            )
                        },
                        layerBlock = {
                            scaleX = startDragAnimation.scaleX
                            scaleY = startDragAnimation.scaleY
                            val velocity = startDragAnimation.velocity / 10f
                            scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                            scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                        },
                        onDrawSurface = {
                            val progress = startDragAnimation.pressProgress
                            val surfaceAlpha = if (isLightTheme) 0.18f - 0.08f * progress else 0.09f - 0.04f * progress
                            drawRect(Color.White.copy(alpha = surfaceAlpha))
                        }
                    )
                    .size(width = thumbSize * 1.46f, height = thumbSize * 0.92f)
            )

            // End Floating Optical Lens Thumb
            Box(
                Modifier
                    .graphicsLayer {
                        translationX =
                            (-size.width / 2f + trackWidth * endDragAnimation.progress)
                                .fastCoerceIn(-size.width / 4f, trackWidth - size.width * 3f / 4f) * if (isLtr) 1f else -1f
                    }
                    .drawBackdrop(
                        backdrop = rememberCombinedBackdrop(
                            effectiveBackdrop,
                            rememberBackdrop(trackBackdrop) { drawBackdrop ->
                                val progress = endDragAnimation.pressProgress
                                val scaleX = lerp(0.88f, 1f, progress)
                                val scaleY = lerp(0.88f, 1f, progress)
                                scale(scaleX, scaleY) {
                                    drawBackdrop()
                                }
                            }
                        ),
                        shape = { Capsule() },
                        effects = {
                            val progress = endDragAnimation.pressProgress
                            blur((8f + 4f * progress).dp.toPx())
                            lens(
                                (8f + 6f * progress).dp.toPx(),
                                (14f + 8f * progress).dp.toPx(),
                                chromaticAberration = true
                            )
                        },
                        highlight = {
                            val progress = endDragAnimation.pressProgress
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
                            val progress = endDragAnimation.pressProgress
                            InnerShadow(
                                radius = 3.dp,
                                alpha = 0.35f + 0.35f * progress
                            )
                        },
                        layerBlock = {
                            scaleX = endDragAnimation.scaleX
                            scaleY = endDragAnimation.scaleY
                            val velocity = endDragAnimation.velocity / 10f
                            scaleX /= 1f - (velocity * 0.75f).fastCoerceIn(-0.2f, 0.2f)
                            scaleY *= 1f - (velocity * 0.25f).fastCoerceIn(-0.2f, 0.2f)
                        },
                        onDrawSurface = {
                            val progress = endDragAnimation.pressProgress
                            val surfaceAlpha = if (isLightTheme) 0.18f - 0.08f * progress else 0.09f - 0.04f * progress
                            drawRect(Color.White.copy(alpha = surfaceAlpha))
                        }
                    )
                    .size(width = thumbSize * 1.46f, height = thumbSize * 0.92f)
            )
        }
    }
}
