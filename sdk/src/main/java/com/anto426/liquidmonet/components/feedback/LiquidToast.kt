package com.anto426.liquidmonet.components.feedback

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.icons.LiquidIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.math.sign

/**
 * Toast notification semantic type.
 */
enum class LiquidToastType {
    Success,
    Info,
    Warning,
    Error,
    Neutral
}

/**
 * Immutable data payload for LiquidToast.
 */
@Immutable
data class LiquidToastData(
    val id: Long,
    val message: String,
    val subtitle: String? = null,
    val type: LiquidToastType = LiquidToastType.Info,
    val icon: ImageVector? = null,
    val durationMillis: Long = 2800L
)

/**
 * State holder for imperatively showing liquid glass toast notifications.
 */
@Stable
class LiquidToastState(private val scope: CoroutineScope) {
    var currentToast by mutableStateOf<LiquidToastData?>(null)
        private set

    private var dismissJob: Job? = null
    private var nextId = 0L

    fun show(
        message: String,
        subtitle: String? = null,
        type: LiquidToastType = LiquidToastType.Info,
        icon: ImageVector? = null,
        durationMillis: Long = 2800L
    ) {
        dismissJob?.cancel()
        val data = LiquidToastData(
            id = ++nextId,
            message = message,
            subtitle = subtitle,
            type = type,
            icon = icon,
            durationMillis = durationMillis
        )
        currentToast = data
        dismissJob = scope.launch {
            delay(durationMillis)
            if (currentToast?.id == data.id) {
                currentToast = null
            }
        }
    }

    fun dismiss() {
        dismissJob?.cancel()
        currentToast = null
    }
}

/**
 * Remembers an [LiquidToastState] tied to the current composition scope.
 */
@Composable
fun rememberLiquidToastState(): LiquidToastState {
    val scope = rememberCoroutineScope()
    return remember(scope) { LiquidToastState(scope) }
}

/**
 * LiquidToastHost - Overlay container that renders active [LiquidToast] notifications with camera punch-hole dewdrop physics.
 */
@Composable
fun LiquidToastHost(
    state: LiquidToastState,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopCenter,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val toast = state.currentToast

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        contentAlignment = alignment
    ) {
        AnimatedContent(
            targetState = toast,
            transitionSpec = {
                val transform = when {
                    initialState == null && targetState != null -> {
                        // First entry: dewdrop emergence from camera punch-hole
                        fadeIn(tween(120, easing = LinearOutSlowInEasing))
                            .togetherWith(fadeOut(tween(80)))
                    }
                    initialState != null && targetState == null -> {
                        // Dismissal: dewdrop retracts cleanly back up into the camera hole
                        fadeIn(tween(80))
                            .togetherWith(
                                slideOutVertically(
                                    animationSpec = spring(dampingRatio = 0.82f, stiffness = 440f),
                                    targetOffsetY = { -it * 2 }
                                ) + scaleOut(
                                    animationSpec = spring(dampingRatio = 0.82f, stiffness = 440f),
                                    targetScale = 0.15f
                                ) + fadeOut(tween(140, easing = FastOutSlowInEasing))
                            )
                    }
                    else -> {
                        // Consecutive replacement: smooth fluid morph
                        fadeIn(tween(160))
                            .togetherWith(
                                scaleOut(
                                    animationSpec = spring(dampingRatio = 0.85f, stiffness = 450f),
                                    targetScale = 0.70f
                                ) + fadeOut(tween(120))
                            )
                    }
                }
                transform.using(SizeTransform(clip = false))
            },
            label = "liquidToastTransition"
        ) { currentToastItem ->
            if (currentToastItem != null) {
                LiquidToast(
                    data = currentToastItem,
                    onDismiss = { state.dismiss() },
                    backdropState = backdropState
                )
            }
        }
    }
}

/**
 * LiquidToast - Radiant Optical Liquid Glass Toast Notification Pill.
 * Emerges directly from the top camera punch-hole as a falling dewdrop (goccia di rugiada)
 * that lands and blossoms laterally across the glass surface with morning dew physics.
 */
@Composable
fun LiquidToast(
    data: LiquidToastData,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val offsetAnim = remember { Animatable(0f) }
    val dewdropSpring = remember(data.id) { Animatable(0f) }
    val colorScheme = MaterialTheme.colorScheme
    val toastHighlight = rememberLiquidControlHighlight()

    LaunchedEffect(data.id) {
        dewdropSpring.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = 0.58f,
                stiffness = 270f
            )
        )
    }

    val accentColor = when (data.type) {
        LiquidToastType.Success -> Color(0xFF10B981)
        LiquidToastType.Info -> colorScheme.primary
        LiquidToastType.Warning -> Color(0xFFF59E0B)
        LiquidToastType.Error -> Color(0xFFEF4444)
        LiquidToastType.Neutral -> Color.White.copy(alpha = 0.85f)
    }

    val iconVector: ImageVector = data.icon ?: when (data.type) {
        LiquidToastType.Success -> LiquidIcons.Check
        LiquidToastType.Info -> LiquidIcons.Info
        LiquidToastType.Warning -> LiquidIcons.Warning
        LiquidToastType.Error -> LiquidIcons.Close
        LiquidToastType.Neutral -> LiquidIcons.Star
    }

    val shape = Capsule()
    val hostContentBackdrop = com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> backdrop
    }

    val currentOffset = offsetAnim.value
    val dismissThresholdPx = with(density) { 90.dp.toPx() }

    val progress = dewdropSpring.value

    // Camera punch-hole dewdrop trajectory:
    // Starts exactly at punch-hole height (-56.dp) as a tiny bead (scaleX: 0.16),
    // drips down stretching vertically (scaleY: 1.25), then blossoms laterally into full capsule!
    val fallOffsetY = lerp(with(density) { -58.dp.toPx() }, 0f, progress)
    val dewdropScaleX = lerp(0.16f, 1f, progress)
    val dewdropScaleY = if (progress < 0.65f) {
        lerp(0.40f, 1.22f, progress / 0.65f)
    } else {
        lerp(1.22f, 1.0f, (progress - 0.65f) / 0.35f)
    }

    Box(
        modifier = modifier
            .offset { IntOffset(currentOffset.roundToInt(), fallOffsetY.roundToInt()) }
            .graphicsLayer {
                scaleX = dewdropScaleX
                scaleY = dewdropScaleY
                // 3D perspective tilt and subtle fade on drag
                rotationZ = (currentOffset / with(density) { 24.dp.toPx() }).coerceIn(-4.5f, 4.5f)
                alpha = (1f - (abs(currentOffset) / with(density) { 260.dp.toPx() }).coerceIn(0f, 0.6f)) * (progress / 0.25f).coerceIn(0f, 1f)
                cameraDistance = 16f
            }
            .pointerInput(dismissThresholdPx) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            if (abs(offsetAnim.value) > dismissThresholdPx) {
                                onDismiss?.invoke()
                            } else {
                                offsetAnim.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = 0.68f,
                                        stiffness = 380f
                                    )
                                )
                            }
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            offsetAnim.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(
                                    dampingRatio = 0.68f,
                                    stiffness = 380f
                                )
                            )
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            val newRaw = offsetAnim.value + dragAmount
                            val damped = if (abs(newRaw) > dismissThresholdPx) {
                                val excess = abs(newRaw) - dismissThresholdPx
                                val dampedExcess = 60.dp.toPx() * (1f - exp(-excess / (80.dp.toPx())))
                                newRaw.sign * (dismissThresholdPx + dampedExcess)
                            } else {
                                newRaw
                            }
                            offsetAnim.snapTo(damped)
                        }
                    }
                )
            }
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = LiquidGlassRole.Navigation,
                layerBlock = liquidControlLayerBlock(true, toastHighlight)
            )
            .liquidControlPressFeedback(
                enabled = true,
                interactiveHighlight = toastHighlight,
                drawHighlightOverlay = false
            )
            .clickable(
                interactionSource = null,
                indication = null,
                role = Role.Button,
                onClick = { onDismiss?.invoke() }
            )
            .padding(start = 10.dp, top = 9.dp, end = 20.dp, bottom = 9.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Dedicated Optical Liquid Glass Icon Bubble Pod with Gelatin Squeeze
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .liquidGlass(
                        backdrop = effectiveBackdrop,
                        shape = Capsule(),
                        role = LiquidGlassRole.Navigation,
                        containerColor = accentColor.copy(alpha = 0.18f),
                        layerBlock = liquidControlLayerBlock(true, toastHighlight)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Message and Subtitle Column (blossoms smoothly as the dewdrop expands laterally)
            Column(
                modifier = Modifier.graphicsLayer {
                    alpha = ((progress - 0.35f) / 0.65f).coerceIn(0f, 1f)
                    scaleX = ((progress - 0.25f) / 0.75f).coerceIn(0.4f, 1f)
                },
                verticalArrangement = Arrangement.Center
            ) {
                BasicText(
                    text = data.message,
                    style = TextStyle(
                        color = colorScheme.onSurface,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                if (data.subtitle != null) {
                    BasicText(
                        text = data.subtitle,
                        style = TextStyle(
                            color = colorScheme.onSurface.copy(alpha = 0.70f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}
