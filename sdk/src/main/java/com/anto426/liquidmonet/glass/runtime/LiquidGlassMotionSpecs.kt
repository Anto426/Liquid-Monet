package com.anto426.liquidmonet.glass.runtime

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring as composeSpring
import androidx.compose.animation.core.tween as composeTween
import kotlin.math.roundToInt

/** Shared motion factory which applies the adaptive liquid-motion scale consistently. */
object LiquidGlassMotionSpecs {
    const val FastDurationMillis = 180
    const val StandardDurationMillis = 320
    const val SpatialDurationMillis = 380
    const val SlowDurationMillis = 480

    const val DefaultDampingRatio = 0.78f
    const val DefaultStiffness = 420f

    const val SpatialDampingRatio = 0.82f
    const val SpatialStiffness = 360f

    const val BouncyDampingRatio = 0.70f
    const val BouncyStiffness = 300f

    const val SnappyDampingRatio = 0.86f
    const val SnappyStiffness = 460f

    /** Material 3 Expressive & iOS 18 fluid motion easing curves */
    val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.70f, 0.10f, 1.0f)
    val EmphasizedAccelerate: Easing = CubicBezierEasing(0.30f, 0.00f, 0.80f, 0.15f)
    val Emphasized: Easing = CubicBezierEasing(0.20f, 0.00f, 0.00f, 1.0f)
    val StandardDecelerate: Easing = CubicBezierEasing(0.00f, 0.00f, 0.20f, 1.0f)

    fun durationMillis(
        performance: LiquidGlassPerformanceState,
        durationMillis: Int = StandardDurationMillis
    ): Int = (durationMillis.coerceAtLeast(0) * performance.motionScale)
        .roundToInt()

    fun <T> tween(
        performance: LiquidGlassPerformanceState,
        durationMillis: Int = StandardDurationMillis,
        easing: Easing = FastOutSlowInEasing
    ): FiniteAnimationSpec<T> {
        val scaledDuration = LiquidGlassMotionSpecs.durationMillis(performance, durationMillis)
        return if (scaledDuration == 0) {
            snap()
        } else {
            composeTween(durationMillis = scaledDuration, easing = easing)
        }
    }

    fun <T> spring(
        performance: LiquidGlassPerformanceState,
        dampingRatio: Float = DefaultDampingRatio,
        stiffness: Float = DefaultStiffness
    ): FiniteAnimationSpec<T> {
        if (performance.motionScale <= 0f) return snap()

        // Preserve the reference spring character. Lower tiers receive only a modest speed-up;
        // the previous squared scaling made ordinary BALANCED devices look nearly unanimated.
        val speedScale = performance.motionScale.coerceIn(0.75f, 1f)
        return composeSpring(
            dampingRatio = dampingRatio.coerceAtLeast(0.01f),
            stiffness = (stiffness.coerceAtLeast(Spring.StiffnessVeryLow) / speedScale)
                .coerceAtMost(Spring.StiffnessHigh)
        )
    }
}
