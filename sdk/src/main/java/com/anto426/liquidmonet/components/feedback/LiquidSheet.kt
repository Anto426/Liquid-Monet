package com.anto426.liquidmonet.components.feedback

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassModalOverlayState
import com.anto426.liquidmonet.glass.runtime.LiquidGlassMotionSpecs
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

/** Floating bottom sheet with refractive glass and a restrained modal tone. */
@Composable
fun LiquidSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    containerColor: Color? = null,
    content: @Composable () -> Unit
) {
    val modalOverlayState = LocalLiquidGlassModalOverlayState.current
    val effectiveBackdrop = LocalLiquidGlassContentBackdrop.current ?: backdropState
    val hostedContent: @Composable () -> Unit = {
        LiquidSheetLayer(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            title = title,
            subtitle = subtitle,
            backdropState = effectiveBackdrop,
            containerColor = containerColor,
            content = content
        )
    }

    if (modalOverlayState == null) {
        hostedContent()
        return
    }

    val overlayKey = remember { Any() }
    val latestHostedContent by rememberUpdatedState(hostedContent)
    DisposableEffect(modalOverlayState, overlayKey) {
        modalOverlayState.show(overlayKey) { latestHostedContent() }
        onDispose { modalOverlayState.remove(overlayKey) }
    }
}

@Composable
private fun LiquidSheetLayer(
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    title: String?,
    subtitle: String?,
    backdropState: Backdrop,
    containerColor: Color? = null,
    content: @Composable () -> Unit
) {
    val performance = LocalLiquidGlassPerformance.current
    val effectiveBackdrop = LocalLiquidGlassContentBackdrop.current ?: backdropState
    val colorScheme = MaterialTheme.colorScheme
    val isLightSurface = colorScheme.surface.luminance() > 0.5f
    val contentColor = colorScheme.onSurface
    val dimColor = Color.Black.copy(alpha = if (isLightSurface) 0.22f else 0.32f)
    val shape = remember { RoundedRectangle(32.dp) }
    val surfaceBackdrop = rememberLayerBackdrop()
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current
    val dismissDistancePx = with(density) { 120.dp.toPx() }
    val dismissVelocityPx = with(density) { 800.dp.toPx() }
    val upwardResistanceLimitPx = with(density) { 24.dp.toPx() }

    var isVisible by remember { mutableStateOf(false) }
    var isDismissing by remember { mutableStateOf(false) }
    var dismissThresholdReached by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val coroutineScope = rememberCoroutineScope()
    val animateDismiss: () -> Unit = dismiss@{
        if (isDismissing) return@dismiss
        isDismissing = true
        isVisible = false
        coroutineScope.launch {
            delay(LiquidGlassMotionSpecs.durationMillis(performance, 180).toLong())
            onDismissRequest()
        }
    }

    var predictiveBackProgress by remember { mutableFloatStateOf(0f) }
    PredictiveBackHandler(enabled = isVisible) { progressFlow ->
        try {
            progressFlow.collect { backEvent -> predictiveBackProgress = backEvent.progress }
            animateDismiss()
        } catch (_: CancellationException) {
            predictiveBackProgress = 0f
        }
    }

    val dragOffsetY = remember { Animatable(0f) }
    val animatedScale by animateFloatAsState(
        targetValue = 1f - predictiveBackProgress * 0.08f,
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.85f,
            stiffness = 400f
        ),
        label = "predictiveSheetScale"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(LiquidGlassMotionSpecs.tween(performance, 180)),
        exit = fadeOut(LiquidGlassMotionSpecs.tween(performance, 160))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    dimColor.copy(alpha = dimColor.alpha * (1f - predictiveBackProgress * 0.3f))
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = animateDismiss
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        val predictiveOffset = predictiveBackProgress * 120.dp.toPx()
                        val dragProgress = (dragOffsetY.value / dismissDistancePx)
                            .coerceIn(-0.20f, 1.20f)
                        val deformation = abs(dragProgress)
                        translationY = dragOffsetY.value + predictiveOffset
                        transformOrigin = TransformOrigin(0.5f, 1f)
                        scaleX = animatedScale * (1f + deformation * 0.025f)
                        scaleY = animatedScale * (1f - dragProgress.coerceAtLeast(0f) * 0.018f)
                        alpha = 1f - predictiveBackProgress * 0.25f
                    }
                    .animateEnterExit(
                        enter = slideInVertically(
                            animationSpec = LiquidGlassMotionSpecs.spring(
                                performance = performance,
                                dampingRatio = 0.72f,
                                stiffness = 360f
                            ),
                            initialOffsetY = { it }
                        ) + fadeIn(LiquidGlassMotionSpecs.tween(performance, 150)),
                        exit = slideOutVertically(
                            animationSpec = LiquidGlassMotionSpecs.spring(
                                performance = performance,
                                dampingRatio = 0.95f,
                                stiffness = 450f
                            ),
                            targetOffsetY = { it }
                        ) + fadeOut(LiquidGlassMotionSpecs.tween(performance, 150))
                    )
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                val rawOffset = dragOffsetY.value + delta
                                val resistedOffset = when {
                                    rawOffset < 0f -> (rawOffset * 0.12f)
                                        .coerceAtLeast(-upwardResistanceLimitPx)
                                    rawOffset > dismissDistancePx -> dismissDistancePx +
                                        (rawOffset - dismissDistancePx) * 0.28f
                                    else -> rawOffset
                                }
                                val reached = resistedOffset >= dismissDistancePx
                                if (reached && !dismissThresholdReached) {
                                    hapticFeedback.performHapticFeedback(
                                        HapticFeedbackType.TextHandleMove
                                    )
                                }
                                dismissThresholdReached = reached
                                coroutineScope.launch { dragOffsetY.snapTo(resistedOffset) }
                            },
                            onDragStopped = { velocity ->
                                if (dragOffsetY.value > dismissDistancePx || velocity > dismissVelocityPx) {
                                    animateDismiss()
                                } else {
                                    dismissThresholdReached = false
                                    coroutineScope.launch {
                                        dragOffsetY.animateTo(
                                            targetValue = 0f,
                                            animationSpec = LiquidGlassMotionSpecs.spring(
                                                performance = performance,
                                                dampingRatio = 0.76f,
                                                stiffness = 420f
                                            )
                                        )
                                    }
                                }
                            }
                        )
                        .liquidGlass(
                            backdrop = effectiveBackdrop,
                            shape = shape,
                            role = LiquidGlassRole.Sheet,
                            containerColor = containerColor,
                            exportedBackdrop = surfaceBackdrop
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    CompositionLocalProvider(LocalLiquidGlassContentBackdrop provides surfaceBackdrop) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 12.dp)
                            .width(40.dp)
                            .height(5.dp)
                            .liquidGlass(
                                backdrop = surfaceBackdrop,
                                shape = Capsule(),
                                role = LiquidGlassRole.Control,
                                containerColor = contentColor.copy(alpha = 0.16f)
                            )
                    )

                    if (!title.isNullOrBlank()) {
                        BasicText(
                            text = title,
                            style = TextStyle(
                                color = contentColor,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    if (!subtitle.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        BasicText(
                            text = subtitle,
                            style = TextStyle(
                                color = contentColor.copy(alpha = 0.78f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    if (!title.isNullOrBlank() || !subtitle.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    content()
                    }
                }
            }
        }
    }
}
