package com.anto426.antoui.components.display

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.lerp
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop
import com.anto426.antoui.icons.AntoIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.math.sign

/**
 * AntoSwipeToDismissBox - Optical Liquid Glass Swipe-to-Action Container.
 * Features fluid 120 FPS viscous drag physics, 3D perspective tilt, elastic action pods, and dynamic chromatic halo bloom.
 */
@Composable
fun AntoSwipeToDismissBox(
    onDismissLeft: (() -> Unit)? = null, // Swiping left reveals right action (e.g. Delete)
    onDismissRight: (() -> Unit)? = null, // Swiping right reveals left action (e.g. Favorite)
    modifier: Modifier = Modifier,
    leftActionIcon: ImageVector = AntoIcons.Star,
    leftActionColor: Color = MaterialTheme.colorScheme.primary,
    rightActionIcon: ImageVector = AntoIcons.Delete,
    rightActionColor: Color = Color(0xFFFF453A),
    threshold: Dp = 95.dp,
    maxDrag: Dp = 150.dp,
    shape: Shape = RoundedRectangle(20.dp),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    content: @Composable () -> Unit
) {
    val hostContentBackdrop = LocalAntoGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> backdrop
    }

    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val offsetAnim = remember { Animatable(0f) }

    val thresholdPx = with(density) { threshold.toPx() }
    val maxDragPx = with(density) { maxDrag.toPx() }

    val currentOffset = offsetAnim.value
    val dragFraction = if (thresholdPx > 0) (abs(currentOffset) / thresholdPx).fastCoerceIn(0f, 1.6f) else 0f
    val isPastThreshold = abs(currentOffset) >= thresholdPx

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Optical Liquid Action Underlayer with True Viscous Metaball Extrusion & Pinch-Off
        if (abs(currentOffset) > 1.5f) {
            val isSwipingRight = currentOffset > 0 // reveals left action
            val actionColor = if (isSwipingRight) leftActionColor else rightActionColor
            val actionIcon = if (isSwipingRight) leftActionIcon else rightActionIcon
            val liquidPath = remember { androidx.compose.ui.graphics.Path() }
            val specularPath = remember { androidx.compose.ui.graphics.Path() }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
                    .drawBehind {
                        val w = size.width
                        val h = size.height
                        val centerY = h / 2f
                        val podRadius = 26.dp.toPx()
                        val podCenterX = if (isSwipingRight) {
                            (32.dp.toPx() + (abs(currentOffset) * 0.42f)).coerceAtMost(w * 0.45f)
                        } else {
                            (w - 32.dp.toPx() - (abs(currentOffset) * 0.42f)).coerceAtLeast(w * 0.55f)
                        }
                        val glowCenter = androidx.compose.ui.geometry.Offset(podCenterX, centerY)

                        // 1. Fluid Ambient Chromatic Glow Bloom & Shockwave
                        val glowRadius = h * 1.8f * dragFraction.coerceAtMost(1.4f)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    actionColor.copy(alpha = if (isPastThreshold) 0.55f else 0.32f * dragFraction.coerceAtMost(1f)),
                                    actionColor.copy(alpha = 0.10f * dragFraction.coerceAtMost(1f)),
                                    actionColor.copy(alpha = 0f)
                                ),
                                center = glowCenter,
                                radius = glowRadius
                            ),
                            radius = glowRadius,
                            center = glowCenter
                        )

                        // Concentric Liquid Ripple Shockwave upon crossing threshold
                        if (isPastThreshold) {
                            val rippleProgress = ((dragFraction - 1f) / 0.6f).coerceIn(0f, 1f)
                            drawCircle(
                                color = actionColor.copy(alpha = 0.60f * (1f - rippleProgress)),
                                radius = podRadius + (28.dp.toPx() * rippleProgress),
                                center = glowCenter,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = (2.8f * (1f - rippleProgress)).dp.toPx())
                            )
                        }

                        // 2. Organic Viscous Liquid Metaball Bridge with Pinch-Off Detachment
                        val isPinchedOff = dragFraction >= 1.05f
                        liquidPath.reset()

                        if (!isPinchedOff) {
                            val neckWidth = (h * 0.46f * (1f - dragFraction * 0.82f)).coerceAtLeast(6.dp.toPx())
                            if (isSwipingRight) {
                                val edgeX = 0f
                                liquidPath.moveTo(edgeX, centerY - neckWidth)
                                liquidPath.cubicTo(
                                    edgeX + podCenterX * 0.40f, centerY - neckWidth * 0.85f,
                                    podCenterX - podRadius * 0.88f, centerY - podRadius,
                                    podCenterX, centerY - podRadius
                                )
                                liquidPath.arcTo(
                                    rect = androidx.compose.ui.geometry.Rect(
                                        podCenterX - podRadius, centerY - podRadius,
                                        podCenterX + podRadius, centerY + podRadius
                                    ),
                                    startAngleDegrees = -90f,
                                    sweepAngleDegrees = 180f,
                                    forceMoveTo = false
                                )
                                liquidPath.cubicTo(
                                    podCenterX - podRadius * 0.88f, centerY + podRadius,
                                    edgeX + podCenterX * 0.40f, centerY + neckWidth * 0.85f,
                                    edgeX, centerY + neckWidth
                                )
                                liquidPath.close()
                            } else {
                                val edgeX = w
                                liquidPath.moveTo(edgeX, centerY - neckWidth)
                                liquidPath.cubicTo(
                                    edgeX - (w - podCenterX) * 0.40f, centerY - neckWidth * 0.85f,
                                    podCenterX + podRadius * 0.88f, centerY - podRadius,
                                    podCenterX, centerY - podRadius
                                )
                                liquidPath.arcTo(
                                    rect = androidx.compose.ui.geometry.Rect(
                                        podCenterX - podRadius, centerY - podRadius,
                                        podCenterX + podRadius, centerY + podRadius
                                    ),
                                    startAngleDegrees = -90f,
                                    sweepAngleDegrees = -180f,
                                    forceMoveTo = false
                                )
                                liquidPath.cubicTo(
                                    podCenterX + podRadius * 0.88f, centerY + podRadius,
                                    edgeX - (w - podCenterX) * 0.40f, centerY + neckWidth * 0.85f,
                                    edgeX, centerY + neckWidth
                                )
                                liquidPath.close()
                            }

                            // Fill viscous liquid body
                            val liquidBrush = Brush.horizontalGradient(
                                colors = if (isSwipingRight) {
                                    listOf(
                                        actionColor.copy(alpha = 0.38f),
                                        actionColor.copy(alpha = 0.78f)
                                    )
                                } else {
                                    listOf(
                                        actionColor.copy(alpha = 0.78f),
                                        actionColor.copy(alpha = 0.38f)
                                    )
                                },
                                startX = if (isSwipingRight) 0f else podCenterX - podRadius,
                                endX = if (isSwipingRight) podCenterX + podRadius else w
                            )
                            drawPath(liquidPath, liquidBrush)
                        }

                        // 3. Specular Caustic Reflection Arc on the Droplet
                        specularPath.reset()
                        val arcRadius = podRadius * 0.80f
                        specularPath.addArc(
                            oval = androidx.compose.ui.geometry.Rect(
                                podCenterX - arcRadius, centerY - arcRadius - 3.dp.toPx(),
                                podCenterX + arcRadius, centerY + arcRadius - 3.dp.toPx()
                            ),
                            startAngleDegrees = if (isSwipingRight) 190f else -10f,
                            sweepAngleDegrees = if (isSwipingRight) 130f else -130f
                        )
                        drawPath(
                            path = specularPath,
                            color = Color.White.copy(alpha = if (isPastThreshold) 0.85f else 0.45f * dragFraction.coerceAtMost(1f)),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.6.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        )
                    }
                    .padding(horizontal = 20.dp),
                contentAlignment = if (isSwipingRight) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                // Free Floating Optical Liquid Glass Sphere with Snell Refraction
                val basePodScale = lerp(0.65f, if (isPastThreshold) 1.28f else 1.08f, dragFraction.fastCoerceIn(0f, 1f))
                val podAlpha = lerp(0.35f, 1f, (dragFraction / 0.35f).fastCoerceIn(0f, 1f))

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .graphicsLayer {
                            scaleX = basePodScale
                            scaleY = basePodScale
                            alpha = podAlpha
                        }
                        .antoLiquidGlass(
                            backdrop = effectiveBackdrop,
                            shape = Capsule(),
                            role = AntoGlassRole.Navigation,
                            containerColor = if (isPastThreshold) actionColor.copy(alpha = 0.92f) else actionColor.copy(alpha = 0.30f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(26.dp)
                            .graphicsLayer {
                                rotationZ = if (isSwipingRight) (1f - dragFraction) * -28f else (1f - dragFraction) * 28f
                                scaleX = if (isPastThreshold) 1.15f else 1.0f
                                scaleY = if (isPastThreshold) 1.15f else 1.0f
                            }
                    )
                }
            }
        }

        // Foreground Content Card with Fluid Squeeze and 3D Perspective Tilt
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(currentOffset.roundToInt(), 0) }
                .graphicsLayer {
                    // Physical viscous fluid squeeze and 3D tilt
                    val tiltZ = (currentOffset / with(density) { 36.dp.toPx() }).coerceIn(-3.8f, 3.8f)
                    val tiltY = (currentOffset / with(density) { 60.dp.toPx() }).coerceIn(-6.0f, 6.0f)
                    rotationZ = tiltZ
                    rotationY = tiltY
                    scaleX = 1f + 0.022f * dragFraction.coerceAtMost(1f)
                    scaleY = 1f - 0.018f * dragFraction.coerceAtMost(1f)
                    cameraDistance = 14f
                }
                .pointerInput(onDismissLeft, onDismissRight, thresholdPx, maxDragPx) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                if (currentOffset < -thresholdPx && onDismissLeft != null) {
                                    onDismissLeft()
                                } else if (currentOffset > thresholdPx && onDismissRight != null) {
                                    onDismissRight()
                                }
                                offsetAnim.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = 0.60f,
                                        stiffness = 300f
                                    )
                                )
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                offsetAnim.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = 0.60f,
                                        stiffness = 300f
                                    )
                                )
                            }
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            coroutineScope.launch {
                                val allowedAmount = when {
                                    dragAmount > 0 && onDismissRight == null && offsetAnim.value >= 0 -> dragAmount * 0.10f
                                    dragAmount < 0 && onDismissLeft == null && offsetAnim.value <= 0 -> dragAmount * 0.10f
                                    else -> dragAmount
                                }
                                val newRaw = offsetAnim.value + allowedAmount
                                // Continuous asymptotic viscous rubber-banding resistance
                                val clamped = if (abs(newRaw) > thresholdPx) {
                                    val excess = abs(newRaw) - thresholdPx
                                    val damped = (maxDragPx - thresholdPx) * (1f - exp(-excess / (maxDragPx * 0.75f)))
                                    newRaw.sign * (thresholdPx + damped)
                                } else {
                                    newRaw
                                }
                                offsetAnim.snapTo(clamped)
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}
