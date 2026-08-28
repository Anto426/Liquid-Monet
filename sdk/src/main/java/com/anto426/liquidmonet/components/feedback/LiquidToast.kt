package com.anto426.liquidmonet.components.feedback

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.anto426.liquidmonet.components.internal.LiquidGlassZIndex
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.resolveLiquidGlassBackdrop
import com.anto426.liquidmonet.icons.LiquidIcons
import com.anto426.liquidmonet.theme.LiquidGlassTheme
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle
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
            // Toasts are scene overlays, not navigation content. Keep them above top bars,
            // floating controls and every ordinary interactive z-index.
            .zIndex(LiquidGlassZIndex.Toast)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        contentAlignment = alignment
    ) {
        AnimatedContent(
            targetState = toast,
            transitionSpec = {
                (
                    slideInVertically(
                        animationSpec = spring(dampingRatio = 0.82f, stiffness = 420f),
                        initialOffsetY = { -it / 2 }
                    ) + scaleIn(
                        animationSpec = spring(dampingRatio = 0.84f, stiffness = 440f),
                        initialScale = 0.96f
                    ) + fadeIn(tween(150))
                ).togetherWith(
                    slideOutVertically(
                        animationSpec = spring(dampingRatio = 0.90f, stiffness = 500f),
                        targetOffsetY = { -it / 3 }
                    ) + scaleOut(
                        animationSpec = spring(dampingRatio = 0.90f, stiffness = 500f),
                        targetScale = 0.98f
                    ) + fadeOut(tween(120))
                ).using(SizeTransform(clip = false))
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
 * LiquidToast - compact Material-proportioned Liquid Glass notification.
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
    val offsetAnim = remember(data.id) { Animatable(0f) }
    val colorScheme = MaterialTheme.colorScheme
    val glassColors = LiquidGlassTheme.colors
    val toastHighlight = rememberLiquidControlHighlight()

    val accentColor = when (data.type) {
        LiquidToastType.Success -> glassColors.success
        LiquidToastType.Info -> colorScheme.primary
        LiquidToastType.Warning -> glassColors.warning
        LiquidToastType.Error -> glassColors.error
        LiquidToastType.Neutral -> glassColors.secondaryContent
    }

    val iconVector: ImageVector = data.icon ?: when (data.type) {
        LiquidToastType.Success -> LiquidIcons.Check
        LiquidToastType.Info -> LiquidIcons.Info
        LiquidToastType.Warning -> LiquidIcons.Warning
        LiquidToastType.Error -> LiquidIcons.Close
        LiquidToastType.Neutral -> LiquidIcons.Star
    }

    val shape = RoundedRectangle(20.dp)
    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdrop, backdropState)

    val currentOffset = offsetAnim.value
    val dismissThresholdPx = with(density) { 90.dp.toPx() }

    Box(
        modifier = modifier
            .offset { IntOffset(currentOffset.roundToInt(), 0) }
            .graphicsLayer {
                rotationZ = (currentOffset / with(density) { 32.dp.toPx() }).coerceIn(-3f, 3f)
                alpha = 1f - (abs(currentOffset) / with(density) { 280.dp.toPx() }).coerceIn(0f, 0.55f)
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
                containerColor = accentColor.copy(alpha = glassColors.neutralContainer.alpha),
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
            .defaultMinSize(minHeight = 56.dp)
            .padding(start = 12.dp, top = 10.dp, end = 16.dp, bottom = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    // The toast shell already owns the refractive pass. The icon capsule is a
                    // restrained tint, avoiding a recursive-looking double lens.
                    .background(
                        accentColor.copy(
                            alpha = (glassColors.neutralContainer.alpha * 1.8f).coerceAtMost(1f)
                        ),
                        Capsule()
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = data.message,
                    style = MaterialTheme.typography.labelLarge,
                    color = glassColors.content
                )
                if (data.subtitle != null) {
                    Text(
                        text = data.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = glassColors.secondaryContent
                    )
                }
            }
        }
    }
}
