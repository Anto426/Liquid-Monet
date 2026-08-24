package com.anto426.antoui.components.selection

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.catalog.components.AntoGlassSlider

/**
 * AntoSlider - Authentic Liquid Glass Floating Thumb Slider.
 * Features a sleek track with an optical liquid glass floating thumb that elastically squashes and stretches on drag.
 */
@Composable
fun AntoSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    trackHeight: Dp = 6.dp,
    thumbSize: Dp = 26.dp,
    tint: Color = Color.Unspecified,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val effectiveBackdrop = if (backdropState != emptyBackdrop()) backdropState else backdrop
    val visibilityThreshold = ((valueRange.endInclusive - valueRange.start) / 1000f).coerceAtLeast(0.0001f)

    AntoGlassSlider(
        value = { value },
        onValueChange = onValueChange,
        valueRange = valueRange,
        visibilityThreshold = visibilityThreshold,
        backdrop = effectiveBackdrop,
        modifier = modifier,
        enabled = enabled,
        tint = tint,
        trackHeight = trackHeight,
        thumbSize = thumbSize
    )
}

@Composable
fun AntoSlider(
    value: () -> Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    trackHeight: Dp = 6.dp,
    thumbSize: Dp = 26.dp,
    tint: Color = Color.Unspecified,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val effectiveBackdrop = if (backdropState != emptyBackdrop()) backdropState else backdrop
    val visibilityThreshold = ((valueRange.endInclusive - valueRange.start) / 1000f).coerceAtLeast(0.0001f)

    AntoGlassSlider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        visibilityThreshold = visibilityThreshold,
        backdrop = effectiveBackdrop,
        modifier = modifier,
        enabled = enabled,
        tint = tint,
        trackHeight = trackHeight,
        thumbSize = thumbSize
    )
}

