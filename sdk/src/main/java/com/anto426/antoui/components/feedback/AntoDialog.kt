package com.anto426.antoui.components.feedback

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
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

@Composable
fun AntoDialog(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    confirmButton: (@Composable () -> Unit)? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    scrimColor: Color? = null,
    containerColor: Color? = null,
    accentColor: Color? = null,
    content: @Composable () -> Unit
) {
    val modalOverlayState = LocalAntoGlassModalOverlayState.current
    val effectiveBackdrop = LocalAntoGlassContentBackdrop.current ?: backdropState
    val hostedContent: @Composable () -> Unit = {
        AntoDialogLayer(
            onDismissRequest = onDismissRequest,
            title = title,
            modifier = modifier,
            backdropState = effectiveBackdrop,
            confirmButton = confirmButton,
            dismissButton = dismissButton,
            scrimColor = scrimColor,
            containerColor = containerColor,
            accentColor = accentColor,
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

/**
 * In-scene modal dialog. Its panel is clear optical glass: it never paints a default color over
 * the recorded scene, so Monet comes through the refraction instead of tinting the container.
 */
@Composable
fun AntoDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    confirmButton: (@Composable () -> Unit)? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    scrimColor: Color? = null,
    containerColor: Color? = null,
    accentColor: Color? = null
) {
    AntoDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        modifier = modifier,
        backdrop = backdrop,
        backdropState = backdropState,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        scrimColor = scrimColor,
        containerColor = containerColor,
        accentColor = accentColor
    ) {
        val contentColor = MaterialTheme.colorScheme.onSurface
        BasicText(
            text = text,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            style = TextStyle(
                color = contentColor.copy(alpha = 0.78f),
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            ),
            maxLines = 6
        )
    }
}

@Composable
private fun AntoDialogLayer(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    backdropState: Backdrop,
    confirmButton: (@Composable () -> Unit)? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    scrimColor: Color? = null,
    containerColor: Color? = null,
    accentColor: Color? = null,
    content: @Composable () -> Unit
) {
    val performance = LocalAntoGlassPerformance.current
    val effectiveBackdrop = LocalAntoGlassContentBackdrop.current ?: backdropState
    val colorScheme = MaterialTheme.colorScheme
    val isLightSurface = colorScheme.surface.luminance() > 0.5f
    val defaultDim = Color.Black.copy(alpha = if (isLightSurface) 0.22f else 0.32f)
    val dimColor = scrimColor ?: defaultDim

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

    val animatedScale by animateFloatAsState(
        targetValue = 1f - predictiveBackProgress * 0.10f,
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.85f,
            stiffness = 400f
        ),
        label = "predictiveDialogScale"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(AntoGlassMotionSpecs.tween(performance, 180)),
        exit = fadeOut(AntoGlassMotionSpecs.tween(performance, 140))
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
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = animatedScale
                        scaleY = animatedScale
                        translationY = (predictiveBackProgress * 12.dp.toPx())
                        alpha = 1f - predictiveBackProgress * 0.35f
                    }
                    .animateEnterExit(
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = 0.66f,
                                stiffness = 340f
                            ),
                            initialScale = 0.84f
                        ) + slideInVertically(
                            animationSpec = spring(
                                dampingRatio = 0.72f,
                                stiffness = 360f
                            ),
                            initialOffsetY = { it / 9 }
                        ) + fadeIn(
                            animationSpec = tween(
                                durationMillis = 240,
                                easing = LinearOutSlowInEasing
                            )
                        ),
                        exit = scaleOut(
                            animationSpec = spring(
                                dampingRatio = 0.88f,
                                stiffness = 460f
                            ),
                            targetScale = 0.88f
                        ) + slideOutVertically(
                            animationSpec = spring(
                                dampingRatio = 0.88f,
                                stiffness = 460f
                            ),
                            targetOffsetY = { it / 14 }
                        ) + fadeOut(
                            animationSpec = tween(
                                durationMillis = 160,
                                easing = FastOutSlowInEasing
                            )
                        )
                    )
            ) {
                AntoDialogContent(
                    title = title,
                    backdrop = effectiveBackdrop,
                    modifier = modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                    containerColor = containerColor,
                    accentColor = accentColor,
                    confirmButton = confirmButton,
                    dismissButton = dismissButton,
                    content = content
                )
            }
        }
    }
}

/** Clear liquid-glass dialog panel shared by hosted and standalone dialogs. */
@Composable
fun AntoDialogContent(
    title: String,
    backdrop: Backdrop,
    modifier: Modifier = Modifier,
    containerColor: Color? = null,
    accentColor: Color? = null,
    confirmButton: (@Composable () -> Unit)? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val contentColor = MaterialTheme.colorScheme.onSurface
    val shape = remember { RoundedRectangle(32.dp) }

    Column(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .widthIn(min = 280.dp, max = 360.dp)
            .fillMaxWidth()
            .antoLiquidGlass(
                backdrop = backdrop,
                shape = shape,
                role = AntoGlassRole.Dialog,
                containerColor = containerColor
            )
            .padding(horizontal = 20.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Centered Title
        BasicText(
            text = title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            style = TextStyle(
                color = contentColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        )

        // Centered Body Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        // Action Buttons Row
        if (dismissButton != null || confirmButton != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (dismissButton != null) {
                    Box(modifier = Modifier.weight(1f)) { dismissButton() }
                }
                if (confirmButton != null) {
                    Box(modifier = Modifier.weight(1f)) { confirmButton() }
                }
            }
        }
    }
}

/** Monet-aware dialog action with tactile bounce; the dialog panel itself remains untinted. */
@Composable
fun AntoDialogActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false,
    accentColor: Color? = null,
    enabled: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme
    val effectiveAccent = accentColor ?: colorScheme.primary
    val buttonHighlight = rememberAntoControlHighlight()

    val backgroundColor = if (isPrimary) {
        effectiveAccent
    } else {
        colorScheme.onSurface.copy(alpha = 0.10f)
    }
    val textColor = when {
        !isPrimary -> colorScheme.onSurface
        accentColor == null -> colorScheme.onPrimary
        else -> colorScheme.surface
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .graphicsLayer(antoControlLayerBlock(enabled, buttonHighlight) ?: {})
            .antoControlPressFeedback(
                enabled = enabled,
                interactiveHighlight = buttonHighlight,
                drawHighlightOverlay = false
            )
            .clip(Capsule())
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicText(
            text = text,
            style = TextStyle(
                color = textColor,
                fontSize = 15.sp,
                fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium
            )
        )
    }
}
