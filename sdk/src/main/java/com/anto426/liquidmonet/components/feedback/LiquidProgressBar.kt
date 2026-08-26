package com.anto426.liquidmonet.components.feedback

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.progressSemantics
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPresets
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * LiquidLinearProgressIndicator - Wavy Optical Liquid Glass Linear Progress Bar.
 * Features undulating sinusoidal liquid waves, radiant Monet gradient shimmer,
 * and glowing leading droplet without any surrounding bounding box container.
 */
@Composable
fun LiquidLinearProgressIndicator(
    progress: Float? = null,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    trackColor: Color? = null,
    progressColor: Color? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val colorScheme = MaterialTheme.colorScheme
    val activePrimary = progressColor ?: colorScheme.primary
    val activeSecondary = colorScheme.tertiary
    val highlightColor = colorScheme.onSurface
    val defaultTrack = trackColor ?: colorScheme.onSurface.copy(alpha = 0.18f)
    val normalizedProgress = progress?.coerceIn(0f, 1f)
    val effectiveBackdrop = if (backdropState != emptyBackdrop()) backdropState else backdrop
    val semanticsModifier = if (normalizedProgress != null) {
        modifier.progressSemantics(normalizedProgress)
    } else {
        modifier.progressSemantics()
    }
    val trackModifier = semanticsModifier.liquidGlass(
        backdrop = effectiveBackdrop,
        shape = RoundedRectangle(height / 2f),
        role = LiquidGlassRole.Control,
        preset = LiquidGlassPresets.Subtle,
        containerColor = Color.Transparent
    )

    val infiniteTransition = rememberInfiniteTransition(label = "LinearWavyProgressAnimation")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerOffset"
    )

    val indeterminatePulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "IndeterminatePulse"
    )

    if (normalizedProgress != null) {
        // Determinate Wavy Liquid Progress
        val animatedProgress by animateFloatAsState(
            targetValue = normalizedProgress,
            animationSpec = spring(dampingRatio = 0.78f, stiffness = 380f),
            label = "determinateProgressAnim"
        )

        Canvas(
            modifier = trackModifier
                .fillMaxWidth()
                .height(height)
        ) {
            val canvasWidth = this.size.width
            val canvasHeight = this.size.height
            val centerY = canvasHeight / 2f
            val strokePx = (canvasHeight * 0.45f).coerceIn(2.dp.toPx(), 4.dp.toPx())
            val amplitude = ((canvasHeight - strokePx) * 0.36f).coerceAtLeast(0f)
            val wavelength = (canvasHeight * 5f).coerceAtLeast(28.dp.toPx())

            // 1. Draw Full Wavy Background Track
            val trackPath = Path()
            val step = 3f
            var isFirst = true
            var x = 0f
            while (x <= canvasWidth) {
                val y = centerY + amplitude * sin(2 * PI * (x / wavelength) + wavePhase).toFloat()
                if (isFirst) {
                    trackPath.moveTo(x, y)
                    isFirst = false
                } else {
                    trackPath.lineTo(x, y)
                }
                x += step
            }
            drawPath(
                path = trackPath,
                color = defaultTrack,
                style = Stroke(width = strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // 2. Draw Active Wavy Progress Line
            val activeTargetX = canvasWidth * animatedProgress
            if (activeTargetX > 1f) {
                val activePath = Path()
                isFirst = true
                x = 0f
                var lastX = 0f
                var lastY = centerY
                while (x <= activeTargetX) {
                    val y = centerY + amplitude * sin(2 * PI * (x / wavelength) + wavePhase).toFloat()
                    if (isFirst) {
                        activePath.moveTo(x, y)
                        isFirst = false
                    } else {
                        activePath.lineTo(x, y)
                    }
                    lastX = x
                    lastY = y
                    x += step
                }

                drawPath(
                    path = activePath,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            activePrimary,
                            activeSecondary,
                            activePrimary
                        ),
                        startX = -100f + 200f * shimmerOffset,
                        endX = canvasWidth + 200f * shimmerOffset
                    ),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Glowing Meniscus Droplet at the leading tip of the wave
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            highlightColor.copy(alpha = 0.90f),
                            activePrimary.copy(alpha = 0.45f),
                            Color.Transparent
                        ),
                        center = Offset(lastX, lastY),
                        radius = strokePx * 1.8f
                    ),
                    radius = strokePx * 1.8f,
                    center = Offset(lastX, lastY)
                )
            }
        }
    } else {
        // Indeterminate Wavy Traveling Liquid Wave
        Canvas(
            modifier = trackModifier
                .fillMaxWidth()
                .height(height)
        ) {
            val canvasWidth = this.size.width
            val canvasHeight = this.size.height
            val centerY = canvasHeight / 2f
            val strokePx = (canvasHeight * 0.45f).coerceIn(2.dp.toPx(), 4.dp.toPx())
            val amplitude = ((canvasHeight - strokePx) * 0.36f).coerceAtLeast(0f)
            val wavelength = (canvasHeight * 5f).coerceAtLeast(28.dp.toPx())

            // Background Wavy Track
            val trackPath = Path()
            val step = 3f
            var isFirst = true
            var x = 0f
            while (x <= canvasWidth) {
                val y = centerY + amplitude * sin(2 * PI * (x / wavelength) + wavePhase).toFloat()
                if (isFirst) {
                    trackPath.moveTo(x, y)
                    isFirst = false
                } else {
                    trackPath.lineTo(x, y)
                }
                x += step
            }
            drawPath(
                path = trackPath,
                color = defaultTrack,
                style = Stroke(width = strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Traveling Liquid Wave Segment
            val waveSegmentWidth = canvasWidth * (0.32f + 0.12f * indeterminatePulse)
            val startX = (canvasWidth + waveSegmentWidth) * shimmerOffset - waveSegmentWidth
            val endX = startX + waveSegmentWidth

            val activeWavePath = Path()
            isFirst = true
            x = startX.coerceAtLeast(0f)
            var lastX = x
            var lastY = centerY
            while (x <= endX.coerceAtMost(canvasWidth)) {
                val y = centerY + amplitude * sin(2 * PI * (x / wavelength) + wavePhase).toFloat()
                if (isFirst) {
                    activeWavePath.moveTo(x, y)
                    isFirst = false
                } else {
                    activeWavePath.lineTo(x, y)
                }
                lastX = x
                lastY = y
                x += step
            }

            if (!isFirst) {
                drawPath(
                    path = activeWavePath,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            activePrimary.copy(alpha = 0.75f),
                            activeSecondary,
                            activePrimary,
                            Color.Transparent
                        ),
                        startX = startX,
                        endX = endX
                    ),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Glowing Head Droplet
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            highlightColor.copy(alpha = 0.82f),
                            activePrimary.copy(alpha = 0f)
                        ),
                        center = Offset(lastX, lastY),
                        radius = strokePx * 1.8f
                    ),
                    radius = strokePx * 1.8f,
                    center = Offset(lastX, lastY)
                )
            }
        }
    }
}

/**
 * LiquidCircularProgressIndicator - Sleek Optical Liquid Glass Circular Progress Spinner.
 * Features pure geometric circular arcs, radiant Monet sweep gradients, and smooth spring physics.
 */
@Composable
fun LiquidCircularProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null,
    indicatorSize: Dp = 40.dp,
    strokeWidth: Dp = 4.dp,
    trackColor: Color? = null,
    progressColor: Color? = null,
    color: Color? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val colorScheme = MaterialTheme.colorScheme
    val activePrimary = color ?: progressColor ?: colorScheme.primary
    val activeSecondary = colorScheme.tertiary
    val highlightColor = colorScheme.onSurface
    val defaultTrack = trackColor ?: colorScheme.onSurface.copy(alpha = 0.14f)
    val normalizedProgress = progress?.coerceIn(0f, 1f)
    val effectiveBackdrop = if (backdropState != emptyBackdrop()) backdropState else backdrop
    val semanticsModifier = if (normalizedProgress != null) {
        modifier.progressSemantics(normalizedProgress)
    } else {
        modifier.progressSemantics()
    }
    val trackModifier = semanticsModifier.liquidGlass(
        backdrop = effectiveBackdrop,
        shape = Capsule(),
        role = LiquidGlassRole.Control,
        preset = LiquidGlassPresets.Subtle,
        containerColor = Color.Transparent
    )

    val infiniteTransition = rememberInfiniteTransition(label = "CircularProgressAnimation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 40f,
        targetValue = 280f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SweepAngle"
    )

    if (normalizedProgress != null) {
        // Determinate Circular Progress Arc
        val animatedProgress by animateFloatAsState(
            targetValue = normalizedProgress,
            animationSpec = spring(dampingRatio = 0.78f, stiffness = 380f),
            label = "determinateCircularAnim"
        )

        Canvas(
            modifier = trackModifier.defaultMinSize(minWidth = indicatorSize, minHeight = indicatorSize)
        ) {
            val strokePx = strokeWidth.toPx()
            val canvasWidth = this.size.width
            val canvasHeight = this.size.height
            val arcSize = Size(canvasWidth - strokePx, canvasHeight - strokePx)
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)

            // Background Track
            drawArc(
                color = defaultTrack,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Active Progress Arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        activePrimary,
                        activeSecondary,
                        activePrimary
                    ),
                    center = Offset(canvasWidth / 2f, canvasHeight / 2f)
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
    } else {
        // Indeterminate Liquid Spinner
        Canvas(
            modifier = trackModifier.defaultMinSize(minWidth = indicatorSize, minHeight = indicatorSize)
        ) {
            val strokePx = strokeWidth.toPx()
            val canvasWidth = this.size.width
            val canvasHeight = this.size.height
            val arcSize = Size(canvasWidth - strokePx, canvasHeight - strokePx)
            val topLeft = Offset(strokePx / 2f, strokePx / 2f)

            // Background Track
            drawArc(
                color = defaultTrack,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Radiant Liquid Spinning Arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        activePrimary.copy(alpha = 0.10f),
                        activePrimary,
                        activeSecondary,
                        highlightColor
                    ),
                    center = Offset(canvasWidth / 2f, canvasHeight / 2f)
                ),
                startAngle = rotation,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
    }
}
