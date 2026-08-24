package com.anto426.antoui.glass.runtime

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring as composeSpring
import androidx.compose.animation.core.tween as composeTween
import kotlin.math.roundToInt

/** Shared motion factory which applies the adaptive liquid-motion scale consistently. */
object AntoGlassMotionSpecs {
    const val FastDurationMillis = 180
    const val StandardDurationMillis = 320
    const val SlowDurationMillis = 480

    const val DefaultDampingRatio = 0.78f
    const val DefaultStiffness = 420f

    fun durationMillis(
        performance: AntoGlassPerformanceState,
        durationMillis: Int = StandardDurationMillis
    ): Int = (durationMillis.coerceAtLeast(0) * performance.motionScale)
        .roundToInt()

    fun <T> tween(
        performance: AntoGlassPerformanceState,
        durationMillis: Int = StandardDurationMillis,
        easing: Easing = FastOutSlowInEasing
    ): FiniteAnimationSpec<T> {
        val scaledDuration = AntoGlassMotionSpecs.durationMillis(performance, durationMillis)
        return if (scaledDuration == 0) {
            snap()
        } else {
            composeTween(durationMillis = scaledDuration, easing = easing)
        }
    }

    fun <T> spring(
        performance: AntoGlassPerformanceState,
        dampingRatio: Float = DefaultDampingRatio,
        stiffness: Float = DefaultStiffness
    ): FiniteAnimationSpec<T> {
        if (performance.motionScale <= 0f) return snap()

        val speedScale = performance.motionScale.coerceIn(0.25f, 1f)
        return composeSpring(
            dampingRatio = dampingRatio.coerceAtLeast(0.01f),
            stiffness = (stiffness.coerceAtLeast(Spring.StiffnessVeryLow) /
                (speedScale * speedScale)).coerceAtMost(Spring.StiffnessHigh)
        )
    }
}
