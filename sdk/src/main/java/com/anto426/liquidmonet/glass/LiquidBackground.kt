package com.anto426.liquidmonet.glass

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.theme.monet.LiquidMonetSeed
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Visual effects supported by LiquidBackground.
 */
enum class LiquidBackgroundEffect {
    /** Organic floating multi-chromatic aurora borealis with wave interference */
    Aurora,
    /** 4-corner multi-chromatic mesh gradient with focal glow */
    MeshGlow,
    /** Orbiting harmonic light spheres with breathing pulsation */
    OrbitalPulse,
    /** Next.js / Vercel-inspired iconic top apex spotlight beam with illuminated cyber grid */
    RadiantBeam
}

/**
 * LiquidBackground - High-performance dynamic Material 3 background synthesizer.
 *
 * Mixes Material 3 colorScheme tones (Primary, Secondary, Tertiary, Containers)
 * with multi-lobe fluid dynamics, Next.js radiant apex spotlighting, and smooth aperiodic motion.
 */
@Composable
fun LiquidBackground(
    modifier: Modifier = Modifier,
    effect: LiquidBackgroundEffect = LiquidBackgroundEffect.Aurora,
    isDark: Boolean = MaterialTheme.colorScheme.surface.luminance() < 0.5f,
    monetSeed: LiquidMonetSeed? = null,
    primaryOverride: Color? = null,
    secondaryOverride: Color? = null,
    tertiaryOverride: Color? = null,
    accentOverride: Color? = null,
    speedFactor: Float = 1.0f,
    intensity: Float = 1.0f,
    showVignette: Boolean = true
) {
    val performance = LocalLiquidGlassPerformance.current
    val normalizedSpeed = if (speedFactor.isFinite()) {
        speedFactor.coerceIn(0.05f, 10f)
    } else {
        1f
    }
    val normalizedIntensity = if (intensity.isFinite()) intensity.coerceIn(0f, 1f) else 1f
    // HIGH is intentionally a two-dimensional background: it keeps the main glow but drops
    // secondary lobes/grid work. The full multi-lobe composition is reserved for ULTRA.
    val drawDetailedBackground = performance.renderDetailedBackground
    val drawBackgroundMotion = performance.animateBackground

    // 1. Resolve colors from MonetSeed, overrides, or active Material 3 ColorScheme
    val rawPrimary = primaryOverride
        ?: monetSeed?.let { if (isDark) it.darkPrimary else it.lightPrimary }
        ?: MaterialTheme.colorScheme.primary

    val rawSecondary = secondaryOverride
        ?: monetSeed?.let { if (isDark) it.darkSecondary else it.lightSecondary }
        ?: MaterialTheme.colorScheme.secondary

    val rawTertiary = tertiaryOverride
        ?: monetSeed?.let { if (isDark) it.darkTertiary else it.lightTertiary }
        ?: MaterialTheme.colorScheme.tertiary

    val rawAccent = accentOverride
        ?: monetSeed?.let { if (isDark) it.darkPrimary else it.lightPrimary }
        ?: MaterialTheme.colorScheme.primaryContainer

    // 2. Smooth animated color transitions when theme changes
    val primary by animateColorAsState(targetValue = rawPrimary, animationSpec = tween(850), label = "bgPrimary")
    val secondary by animateColorAsState(targetValue = rawSecondary, animationSpec = tween(850), label = "bgSecondary")
    val tertiary by animateColorAsState(targetValue = rawTertiary, animationSpec = tween(850), label = "bgTertiary")
    val accent by animateColorAsState(targetValue = rawAccent, animationSpec = tween(850), label = "bgAccent")

    // 3. Aperiodic oscillators for natural, slow, serene, non-repeating organic motion
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidBackgroundMotion")

    val t1 by if (drawBackgroundMotion) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween((24000 / normalizedSpeed).toInt(), easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "t1"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    val t2 by if (drawDetailedBackground) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween((34000 / normalizedSpeed).toInt(), easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "t2"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    val t3 by if (drawDetailedBackground) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween((46000 / normalizedSpeed).toInt(), easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "t3"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    // 4. Base background gradient (Deep pitch black for Vercel/Next aesthetic)
    val baseBackgroundBrush = remember(isDark) {
        Brush.verticalGradient(
            if (isDark) {
                listOf(
                    Color(0xFF020306),
                    Color(0xFF07080F),
                    Color(0xFF030408)
                )
            } else {
                listOf(
                    Color(0xFFF7F8FC),
                    Color(0xFFEDEBF5),
                    Color(0xFFF3F5FA)
                )
            }
        )
    }

    val alphaMultiplier = if (isDark) normalizedIntensity else normalizedIntensity * 0.70f

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(baseBackgroundBrush)
    ) {
        val w = size.width
        val h = size.height

        when (effect) {
            LiquidBackgroundEffect.RadiantBeam -> {
                // NEXT.JS / VERCEL SIGNATURE RADIANT SPOTLIGHT & SUB-PIXEL GRID
                val apexX = w * (0.50f + 0.05f * sin(t1 * PI.toFloat() * 2f))
                val apexY = -h * 0.04f

                // 1. Next.js Illuminated Cyber Grid (with radial mask falloff)
                val gridStep = 32.dp.toPx()
                val baseGridAlpha = if (isDark) 0.08f else 0.06f
                val maxMaskDist = w * 0.90f

                if (drawDetailedBackground) {
                    var curX = 0f
                    while (curX <= w) {
                        var curY = 0f
                        while (curY <= h) {
                            val dx = curX - apexX
                            val dy = curY - apexY
                            val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                            val mask = (1f - dist / maxMaskDist).coerceIn(0f, 1f)
                            if (mask > 0.01f) {
                                val alpha = baseGridAlpha * mask * mask
                                val lineColor = if (isDark) Color.White.copy(alpha = alpha) else Color.Black.copy(alpha = alpha)
                                drawLine(color = lineColor, start = Offset(curX, curY), end = Offset((curX + gridStep).coerceAtMost(w), curY), strokeWidth = 1f)
                                drawLine(color = lineColor, start = Offset(curX, curY), end = Offset(curX, (curY + gridStep).coerceAtMost(h)), strokeWidth = 1f)
                            }
                            curY += gridStep
                        }
                        curX += gridStep
                    }
                }

                // 2. Next.js Conic Light Rays (Radial Ray Burst from Apex)
                val rayRotation = t1 * 15f
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            primary.copy(alpha = 0.12f * alphaMultiplier),
                            Color.Transparent,
                            secondary.copy(alpha = 0.10f * alphaMultiplier),
                            Color.Transparent,
                            tertiary.copy(alpha = 0.08f * alphaMultiplier),
                            Color.Transparent,
                            primary.copy(alpha = 0.12f * alphaMultiplier),
                            Color.Transparent
                        ),
                        center = Offset(apexX, apexY)
                    ),
                    center = Offset(apexX, apexY),
                    radius = w * 1.2f
                )

                // 3. Primary Apex Spotlight Bloom (Massive Gaussian Dispersion)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primary.copy(alpha = 0.45f * alphaMultiplier),
                            primary.copy(alpha = 0.15f * alphaMultiplier),
                            Color.Transparent
                        ),
                        center = Offset(apexX, apexY),
                        radius = w * 1.05f
                    ),
                    center = Offset(apexX, apexY),
                    radius = w * 1.05f
                )

                // 4. Secondary Chromatic Flare (Right-offset ambient glow)
                val flareX = w * (0.62f - 0.08f * cos(t2 * PI.toFloat() * 2f))
                if (drawDetailedBackground) drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            secondary.copy(alpha = 0.28f * alphaMultiplier),
                            secondary.copy(alpha = 0.06f * alphaMultiplier),
                            Color.Transparent
                        ),
                        center = Offset(flareX, apexY + h * 0.08f),
                        radius = w * 0.80f
                    ),
                    center = Offset(flareX, apexY + h * 0.08f),
                    radius = w * 0.80f
                )

                // 5. White-Hot Intense Apex Core Glint
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (isDark) 0.38f * alphaMultiplier else 0.18f),
                            primary.copy(alpha = 0.22f * alphaMultiplier),
                            Color.Transparent
                        ),
                        center = Offset(apexX, 0f),
                        radius = w * 0.32f
                    ),
                    center = Offset(apexX, 0f),
                    radius = w * 0.32f
                )

                // 6. Subtle Ambient Horizon Glow at Bottom
                val horizonY = h * (0.90f + 0.03f * sin(t3 * PI.toFloat() * 2f))
                if (drawDetailedBackground) drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            tertiary.copy(alpha = 0.20f * alphaMultiplier),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.5f, horizonY),
                        radius = w * 0.65f
                    ),
                    center = Offset(w * 0.5f, horizonY),
                    radius = w * 0.65f
                )
            }

            LiquidBackgroundEffect.Aurora -> {
                // Multi-harmonic Fluid Aurora Lobe 1 (Top-Right Primary)
                val p1X = w * (0.78f + 0.14f * sin(t1 * PI.toFloat() * 2f))
                val p1Y = h * (0.16f + 0.10f * cos(t2 * PI.toFloat() * 2f))
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primary.copy(alpha = 0.38f * alphaMultiplier),
                            primary.copy(alpha = 0.14f * alphaMultiplier),
                            Color.Transparent
                        ),
                        center = Offset(p1X, p1Y),
                        radius = w * 0.85f
                    ),
                    center = Offset(p1X, p1Y),
                    radius = w * 0.85f
                )

                // Aurora Lobe 2 (Mid-Left Secondary)
                val p2X = w * (0.18f + 0.12f * cos(t3 * PI.toFloat() * 2f))
                val p2Y = h * (0.48f + 0.12f * sin(t1 * PI.toFloat() * 2f))
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            secondary.copy(alpha = 0.32f * alphaMultiplier),
                            secondary.copy(alpha = 0.10f * alphaMultiplier),
                            Color.Transparent
                        ),
                        center = Offset(p2X, p2Y),
                        radius = w * 0.78f
                    ),
                    center = Offset(p2X, p2Y),
                    radius = w * 0.78f
                )

                // Aurora Lobe 3 (Bottom-Right Tertiary)
                val p3X = w * (0.68f - 0.14f * sin(t2 * PI.toFloat() * 2f))
                val p3Y = h * (0.80f - 0.08f * cos(t1 * PI.toFloat() * 2f))
                if (drawDetailedBackground) drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            tertiary.copy(alpha = 0.28f * alphaMultiplier),
                            tertiary.copy(alpha = 0.08f * alphaMultiplier),
                            Color.Transparent
                        ),
                        center = Offset(p3X, p3Y),
                        radius = w * 0.72f
                    ),
                    center = Offset(p3X, p3Y),
                    radius = w * 0.72f
                )

                // Aurora Lobe 4 (Focal Accent Center-Bottom)
                val p4X = w * (0.35f + 0.10f * sin(t3 * PI.toFloat() * 2f))
                val p4Y = h * (0.72f + 0.08f * cos(t3 * PI.toFloat() * 2f))
                if (drawDetailedBackground) drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accent.copy(alpha = 0.22f * alphaMultiplier),
                            Color.Transparent
                        ),
                        center = Offset(p4X, p4Y),
                        radius = w * 0.60f
                    ),
                    center = Offset(p4X, p4Y),
                    radius = w * 0.60f
                )
            }

            LiquidBackgroundEffect.MeshGlow -> {
                // 4-Quadrant Chromatic Mesh Blending
                // Top-Left (Primary)
                val tl = Offset(w * (0.15f + 0.08f * cos(t1 * PI.toFloat() * 2f)), h * (0.15f + 0.08f * sin(t2 * PI.toFloat() * 2f)))
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(primary.copy(alpha = 0.32f * alphaMultiplier), Color.Transparent),
                        center = tl,
                        radius = w * 0.80f
                    ),
                    center = tl,
                    radius = w * 0.80f
                )

                // Top-Right (Secondary)
                val tr = Offset(w * (0.85f - 0.08f * sin(t2 * PI.toFloat() * 2f)), h * (0.20f + 0.08f * cos(t1 * PI.toFloat() * 2f)))
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(secondary.copy(alpha = 0.30f * alphaMultiplier), Color.Transparent),
                        center = tr,
                        radius = w * 0.75f
                    ),
                    center = tr,
                    radius = w * 0.75f
                )

                // Bottom-Left (Tertiary)
                val bl = Offset(w * (0.20f + 0.06f * sin(t3 * PI.toFloat() * 2f)), h * (0.85f - 0.06f * cos(t2 * PI.toFloat() * 2f)))
                if (drawDetailedBackground) drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(tertiary.copy(alpha = 0.30f * alphaMultiplier), Color.Transparent),
                        center = bl,
                        radius = w * 0.75f
                    ),
                    center = bl,
                    radius = w * 0.75f
                )

                // Bottom-Right (Accent)
                val br = Offset(w * (0.80f - 0.06f * cos(t1 * PI.toFloat() * 2f)), h * (0.80f - 0.06f * sin(t3 * PI.toFloat() * 2f)))
                if (drawDetailedBackground) drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.28f * alphaMultiplier), Color.Transparent),
                        center = br,
                        radius = w * 0.80f
                    ),
                    center = br,
                    radius = w * 0.80f
                )
            }

            LiquidBackgroundEffect.OrbitalPulse -> {
                val cx = w * 0.5f
                val cy = h * 0.5f
                val orbitRadius = w * 0.35f

                // Orbiting Planet 1 (Primary)
                val angle1 = t1 * PI.toFloat() * 2f
                val o1X = cx + orbitRadius * cos(angle1)
                val o1Y = cy + orbitRadius * 0.7f * sin(angle1)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(primary.copy(alpha = 0.38f * alphaMultiplier), Color.Transparent),
                        center = Offset(o1X, o1Y),
                        radius = w * 0.65f
                    ),
                    center = Offset(o1X, o1Y),
                    radius = w * 0.65f
                )

                // Orbiting Planet 2 (Secondary)
                val angle2 = angle1 + (PI.toFloat() * 2f / 3f)
                val o2X = cx + orbitRadius * cos(angle2)
                val o2Y = cy + orbitRadius * 0.7f * sin(angle2)
                if (drawDetailedBackground) drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(secondary.copy(alpha = 0.34f * alphaMultiplier), Color.Transparent),
                        center = Offset(o2X, o2Y),
                        radius = w * 0.60f
                    ),
                    center = Offset(o2X, o2Y),
                    radius = w * 0.60f
                )

                // Orbiting Planet 3 (Tertiary)
                val angle3 = angle1 + (PI.toFloat() * 4f / 3f)
                val o3X = cx + orbitRadius * cos(angle3)
                val o3Y = cy + orbitRadius * 0.7f * sin(angle3)
                if (drawDetailedBackground) drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(tertiary.copy(alpha = 0.32f * alphaMultiplier), Color.Transparent),
                        center = Offset(o3X, o3Y),
                        radius = w * 0.60f
                    ),
                    center = Offset(o3X, o3Y),
                    radius = w * 0.60f
                )

                // Center Pulsing Sun Core
                val pulseScale = 1f + 0.15f * sin(t2 * PI.toFloat() * 2f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(accent.copy(alpha = 0.25f * alphaMultiplier), Color.Transparent),
                        center = Offset(cx, cy),
                        radius = w * 0.45f * pulseScale
                    ),
                    center = Offset(cx, cy),
                    radius = w * 0.45f * pulseScale
                )
            }
        }

        // 5. Cinematic Vignette (Subtle edge contrast enhancement)
        if (showVignette) {
            val vignetteColor = if (isDark) Color.Black else Color(0xFF1E1E24)
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        vignetteColor.copy(alpha = if (isDark) 0.40f else 0.08f)
                    ),
                    center = Offset(w * 0.5f, h * 0.5f),
                    radius = w * 0.95f
                )
            )
        }
    }
}
