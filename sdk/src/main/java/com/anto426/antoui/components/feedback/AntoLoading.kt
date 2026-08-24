package com.anto426.antoui.components.feedback

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.RoundedRectangle
import kotlin.math.PI
import kotlin.math.sin

/**
 * Visual style variant for unified AntoLoading.
 */
enum class AntoLoadingStyle {
    /** Sleek 360° Monet sweep gradient ring spinner. */
    Circular,
    /** Sinusoidal wavy free-floating liquid progress line. */
    Linear,
    /** 3 pulsating liquid glass crystal droplets. */
    Dots,
    /** Breathing luminous crystal orb with expanding radiant ripples. */
    Pulse,
    /** Optical liquid glass skeleton shimmer placeholder. */
    Shimmer,
    /** Full modal liquid glass dialog overlay with spinner and message. */
    Overlay
}

/**
 * Dimension scale for unified AntoLoading.
 */
enum class AntoLoadingSize {
    Small,
    Medium,
    Large
}

/**
 * AntoLoading / LiquidLoading - Single Unified Loading & Progress Component.
 *
 * Configurable with a single consistent API:
 * @param style Visual loading style ([AntoLoadingStyle.Circular], [AntoLoadingStyle.Linear],
 *              [AntoLoadingStyle.Dots], [AntoLoadingStyle.Pulse], [AntoLoadingStyle.Shimmer],
 *              [AntoLoadingStyle.Overlay]).
 * @param progress When non-null (0.0f..1.0f), renders determinate progress; when null, runs infinite indeterminate animation.
 * @param size Dimension scale ([AntoLoadingSize.Small], [AntoLoadingSize.Medium], [AntoLoadingSize.Large]).
 * @param message Optional text message displayed alongside/beneath the indicator.
 * @param tint Custom color tint (harmonized with Monet theme by default).
 * @param backdropState Optical glass backdrop reference.
 */
@Composable
fun AntoLoading(
    modifier: Modifier = Modifier,
    style: AntoLoadingStyle = AntoLoadingStyle.Circular,
    progress: Float? = null,
    size: AntoLoadingSize = AntoLoadingSize.Medium,
    message: String? = null,
    tint: Color = Color.Unspecified,
    shape: Shape = RoundedRectangle(16.dp),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = if (tint.isSpecified) tint else colorScheme.primary
    val secondaryColor = colorScheme.tertiary

    when (style) {
        AntoLoadingStyle.Circular -> {
            val indicatorSize: Dp = when (size) {
                AntoLoadingSize.Small -> 20.dp
                AntoLoadingSize.Medium -> 36.dp
                AntoLoadingSize.Large -> 52.dp
            }
            val strokeWidth: Dp = when (size) {
                AntoLoadingSize.Small -> 2.5.dp
                AntoLoadingSize.Medium -> 3.5.dp
                AntoLoadingSize.Large -> 4.8.dp
            }

            if (message != null) {
                Row(
                    modifier = modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AntoCircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier.size(indicatorSize),
                        indicatorSize = indicatorSize,
                        strokeWidth = strokeWidth,
                        progressColor = primaryColor,
                        backdropState = backdropState
                    )
                    BasicText(
                        text = message,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = if (size == AntoLoadingSize.Small) 13.sp else 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            } else {
                AntoCircularProgressIndicator(
                    progress = progress,
                    modifier = modifier.size(indicatorSize),
                    indicatorSize = indicatorSize,
                    strokeWidth = strokeWidth,
                    progressColor = primaryColor,
                    backdropState = backdropState
                )
            }
        }

        AntoLoadingStyle.Linear -> {
            val heightDp = when (size) {
                AntoLoadingSize.Small -> 12.dp
                AntoLoadingSize.Medium -> 18.dp
                AntoLoadingSize.Large -> 24.dp
            }
            Column(modifier = modifier.fillMaxWidth()) {
                if (message != null) {
                    BasicText(
                        text = message,
                        style = TextStyle(
                            color = Color.White.copy(alpha = 0.88f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
                AntoLinearProgressIndicator(
                    progress = progress,
                    height = heightDp,
                    progressColor = primaryColor,
                    backdropState = backdropState
                )
            }
        }

        AntoLoadingStyle.Dots -> {
            AntoLoadingDots(
                modifier = modifier,
                loadingSize = size,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                message = message
            )
        }

        AntoLoadingStyle.Pulse -> {
            AntoLoadingPulse(
                modifier = modifier,
                loadingSize = size,
                primaryColor = primaryColor,
                message = message,
                backdropState = backdropState
            )
        }

        AntoLoadingStyle.Shimmer -> {
            AntoShimmerBox(
                modifier = modifier,
                shape = shape,
                backdropState = backdropState
            )
        }

        AntoLoadingStyle.Overlay -> {
            AntoLoadingOverlay(
                message = message ?: "Caricamento in corso...",
                primaryColor = primaryColor,
                backdropState = backdropState
            )
        }
    }
}

/**
 * 3 Pulsating liquid glass crystal droplets.
 */
@Composable
private fun AntoLoadingDots(
    modifier: Modifier = Modifier,
    loadingSize: AntoLoadingSize,
    primaryColor: Color,
    secondaryColor: Color,
    message: String?
) {
    val dotDiameter: Dp = when (loadingSize) {
        AntoLoadingSize.Small -> 6.dp
        AntoLoadingSize.Medium -> 10.dp
        AntoLoadingSize.Large -> 14.dp
    }
    val spacing: Dp = when (loadingSize) {
        AntoLoadingSize.Small -> 4.dp
        AntoLoadingSize.Medium -> 6.dp
        AntoLoadingSize.Large -> 8.dp
    }

    val infiniteTransition = rememberInfiniteTransition(label = "DotsBounceAnim")
    val dot1Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, delayMillis = 0, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, delayMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, delayMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Canvas(
            modifier = Modifier.size(
                width = dotDiameter * 3 + spacing * 2,
                height = dotDiameter * 2.2f
            )
        ) {
            val r = dotDiameter.toPx() / 2f
            val yBase = this.size.height / 2f
            val maxBounce = dotDiameter.toPx() * 0.45f

            // Dot 1
            drawCircle(
                color = primaryColor.copy(alpha = 0.45f + 0.55f * dot1Offset),
                radius = r * (0.85f + 0.25f * dot1Offset),
                center = Offset(r, yBase - maxBounce * dot1Offset)
            )
            // Dot 2
            drawCircle(
                color = primaryColor.copy(alpha = 0.45f + 0.55f * dot2Offset),
                radius = r * (0.85f + 0.25f * dot2Offset),
                center = Offset(r * 3 + spacing.toPx(), yBase - maxBounce * dot2Offset)
            )
            // Dot 3
            drawCircle(
                color = secondaryColor.copy(alpha = 0.45f + 0.55f * dot3Offset),
                radius = r * (0.85f + 0.25f * dot3Offset),
                center = Offset(r * 5 + spacing.toPx() * 2, yBase - maxBounce * dot3Offset)
            )
        }
        if (message != null) {
            BasicText(
                text = message,
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

/**
 * Breathing luminous crystal orb with expanding radiant ripples.
 */
@Composable
private fun AntoLoadingPulse(
    modifier: Modifier = Modifier,
    loadingSize: AntoLoadingSize,
    primaryColor: Color,
    message: String?,
    backdropState: Backdrop
) {
    val totalSize: Dp = when (loadingSize) {
        AntoLoadingSize.Small -> 32.dp
        AntoLoadingSize.Medium -> 48.dp
        AntoLoadingSize.Large -> 64.dp
    }

    val infiniteTransition = rememberInfiniteTransition(label = "PulseAnim")
    val pulseProg by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseProg"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Canvas(modifier = Modifier.size(totalSize)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val maxR = this.size.minDimension / 2f
            val coreR = maxR * 0.40f

            // Outer expanding ripple
            drawCircle(
                color = primaryColor.copy(alpha = (1f - pulseProg) * 0.35f),
                radius = coreR + (maxR - coreR) * pulseProg,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Radiant glowing core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        primaryColor,
                        primaryColor.copy(alpha = 0.40f)
                    ),
                    center = center,
                    radius = coreR
                ),
                radius = coreR * (0.92f + 0.12f * sin(pulseProg * 2 * PI.toFloat())),
                center = center
            )
        }
        if (message != null) {
            BasicText(
                text = message,
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

/**
 * Full modal liquid glass dialog overlay.
 */
@Composable
private fun AntoLoadingOverlay(
    message: String,
    primaryColor: Color,
    backdropState: Backdrop
) {
    Box(
        modifier = Modifier
            .antoLiquidGlass(
                backdrop = backdropState,
                shape = RoundedRectangle(24.dp),
                role = AntoGlassRole.Dialog,
                containerColor = Color.White.copy(alpha = 0.08f)
            )
            .padding(horizontal = 28.dp, vertical = 22.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AntoCircularProgressIndicator(
                progress = null,
                modifier = Modifier.size(38.dp),
                progressColor = primaryColor,
                backdropState = backdropState
            )
            BasicText(
                text = message,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}
