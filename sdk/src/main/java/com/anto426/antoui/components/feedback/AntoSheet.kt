package com.anto426.antoui.components.feedback

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop
import com.anto426.antoui.glass.overlay.LocalAntoGlassModalOverlayState
import com.anto426.antoui.glass.runtime.AntoGlassMotionSpecs
import com.anto426.antoui.glass.runtime.LocalAntoGlassPerformance
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Floating bottom sheet rendered as clear optical glass. No default surface color is painted:
 * the visible material comes from the scene's blur, refraction, highlight and edge shadows.
 */
@Composable
fun AntoSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    containerColor: Color? = null,
    content: @Composable () -> Unit
) {
    val modalOverlayState = LocalAntoGlassModalOverlayState.current
    val effectiveBackdrop = LocalAntoGlassContentBackdrop.current ?: backdropState
    val hostedContent: @Composable () -> Unit = {
        AntoSheetLayer(
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
private fun AntoSheetLayer(
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    title: String?,
    subtitle: String?,
    backdropState: Backdrop,
    containerColor: Color? = null,
    content: @Composable () -> Unit
) {
    val performance = LocalAntoGlassPerformance.current
    val effectiveBackdrop = LocalAntoGlassContentBackdrop.current ?: backdropState
    val colorScheme = MaterialTheme.colorScheme
    val isLightSurface = colorScheme.surface.luminance() > 0.5f
    val contentColor = colorScheme.onSurface
    val dimColor = Color.Black.copy(alpha = if (isLightSurface) 0.22f else 0.32f)
    val shape = remember { RoundedRectangle(32.dp) }
    val density = LocalDensity.current
    val dismissDistancePx = with(density) { 120.dp.toPx() }
    val dismissVelocityPx = with(density) { 800.dp.toPx() }

    var isVisible by remember { mutableStateOf(false) }
    var isDismissing by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val coroutineScope = rememberCoroutineScope()
    val animateDismiss: () -> Unit = dismiss@{
        if (isDismissing) return@dismiss
        isDismissing = true
        isVisible = false
        coroutineScope.launch {
            delay(AntoGlassMotionSpecs.durationMillis(performance, 180).toLong())
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
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.85f,
            stiffness = 400f
        ),
        label = "predictiveSheetScale"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(AntoGlassMotionSpecs.tween(performance, 180)),
        exit = fadeOut(AntoGlassMotionSpecs.tween(performance, 160))
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
                        translationY = dragOffsetY.value + predictiveOffset
                        scaleX = animatedScale
                        scaleY = animatedScale
                        alpha = 1f - predictiveBackProgress * 0.25f
                    }
                    .animateEnterExit(
                        enter = slideInVertically(
                            animationSpec = AntoGlassMotionSpecs.spring(
                                performance = performance,
                                dampingRatio = 0.84f,
                                stiffness = 400f
                            ),
                            initialOffsetY = { it }
                        ) + fadeIn(AntoGlassMotionSpecs.tween(performance, 150)),
                        exit = slideOutVertically(
                            animationSpec = AntoGlassMotionSpecs.spring(
                                performance = performance,
                                dampingRatio = 0.95f,
                                stiffness = 450f
                            ),
                            targetOffsetY = { it }
                        ) + fadeOut(AntoGlassMotionSpecs.tween(performance, 150))
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
                                val newOffset = (dragOffsetY.value + delta).coerceAtLeast(0f)
                                coroutineScope.launch { dragOffsetY.snapTo(newOffset) }
                            },
                            onDragStopped = { velocity ->
                                if (dragOffsetY.value > dismissDistancePx || velocity > dismissVelocityPx) {
                                    animateDismiss()
                                } else {
                                    coroutineScope.launch {
                                        dragOffsetY.animateTo(
                                            targetValue = 0f,
                                            animationSpec = AntoGlassMotionSpecs.spring(
                                                performance = performance,
                                                dampingRatio = 0.76f,
                                                stiffness = 420f
                                            )
                                        )
                                    }
                                }
                            }
                        )
                        .antoLiquidGlass(
                            backdrop = effectiveBackdrop,
                            shape = shape,
                            role = AntoGlassRole.Sheet,
                            containerColor = containerColor
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 12.dp)
                            .width(40.dp)
                            .height(5.dp)
                            .clip(Capsule())
                            .background(contentColor.copy(alpha = 0.22f))
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
