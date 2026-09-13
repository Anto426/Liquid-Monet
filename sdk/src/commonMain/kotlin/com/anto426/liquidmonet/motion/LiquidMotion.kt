package com.anto426.liquidmonet.motion

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring as composeSpring
import androidx.compose.animation.core.tween as composeTween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.graphics.TransformOrigin
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPerformanceState
import kotlin.math.roundToInt

/**
 * LiquidMotion - Centralized Motion & Physics Engine for the Liquid Monet SDK.
 *
 * Consolidates all timing constants, spring damping/stiffness presets, easing curves,
 * adaptive performance-scaled animations, and standard Compose transitions into a single source of truth.
 */
object LiquidMotion {

    // =========================================================================
    // Standard Durations (milliseconds)
    // =========================================================================
    const val FastDurationMillis: Int = 180
    const val StandardDurationMillis: Int = 320
    const val SpatialDurationMillis: Int = 380
    const val SlowDurationMillis: Int = 480

    // Backward-compatibility aliases
    const val Fast: Int = FastDurationMillis
    const val Standard: Int = StandardDurationMillis
    const val Spatial: Int = SpatialDurationMillis
    const val Slow: Int = SlowDurationMillis

    // =========================================================================
    // Canonical Spring Physics Constants
    // =========================================================================

    /** Quick, responsive feedback for buttons, toggles, chips, icons, and tooltips. */
    const val SnappyDampingRatio: Float = 0.86f
    const val SnappyStiffness: Float = 480f

    /** Balanced fluid physics for general controls, selection states, inputs, and sliders. */
    const val InteractiveDampingRatio: Float = 0.78f
    const val InteractiveStiffness: Float = 420f

    /** Default fallback matching Interactive physics. */
    const val DefaultDampingRatio: Float = InteractiveDampingRatio
    const val DefaultStiffness: Float = InteractiveStiffness

    /** Spatial depth movement for sheets, modal dialogs, and large container expansions. */
    const val SpatialDampingRatio: Float = 0.82f
    const val SpatialStiffness: Float = 360f

    /** Playful, organic liquid bounce for charts, jelly droplet indicators, and pagination pills. */
    const val FluidDampingRatio: Float = 0.72f
    const val FluidStiffness: Float = 280f
    const val BouncyDampingRatio: Float = FluidDampingRatio
    const val BouncyStiffness: Float = FluidStiffness

    /** Tactile spring with visible elastic bounce and overshoot for dropdown menus and popovers. */
    const val MenuBounceDampingRatio: Float = 0.52f
    const val MenuBounceStiffness: Float = 320f

    /** High-stiffness tactile spring applied immediately upon touch down. */
    const val PressDampingRatio: Float = 0.86f
    const val PressStiffness: Float = 700f

    /** Smooth elastic spring applied when touch is released, producing a fluid gelatin bounce. */
    const val ReleaseDampingRatio: Float = 0.58f
    const val ReleaseStiffness: Float = 360f

    // =========================================================================
    // Easing Curves (Material 3 Expressive & iOS 18 fluid mechanics)
    // =========================================================================
    val EmphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.70f, 0.10f, 1.0f)
    val EmphasizedAccelerate: Easing = CubicBezierEasing(0.30f, 0.00f, 0.80f, 0.15f)
    val Emphasized: Easing = CubicBezierEasing(0.20f, 0.00f, 0.00f, 1.0f)
    val StandardDecelerate: Easing = CubicBezierEasing(0.00f, 0.00f, 0.20f, 1.0f)
    val FastOutSlow: Easing = FastOutSlowInEasing
    val Linear: Easing = LinearEasing

    // =========================================================================
    // Adaptive Duration Scaling
    // =========================================================================
    fun durationMillis(
        performance: LiquidGlassPerformanceState,
        durationMillis: Int = StandardDurationMillis
    ): Int = (durationMillis.coerceAtLeast(0) * performance.motionScale).roundToInt()

    // =========================================================================
    // Universal Spring Builders
    // =========================================================================

    /** Standard unscaled Compose spring using Liquid constants. */
    fun <T> spring(
        dampingRatio: Float = DefaultDampingRatio,
        stiffness: Float = DefaultStiffness,
        visibilityThreshold: T? = null
    ): SpringSpec<T> = composeSpring(
        dampingRatio = dampingRatio.coerceAtLeast(0.01f),
        stiffness = stiffness.coerceAtLeast(Spring.StiffnessVeryLow),
        visibilityThreshold = visibilityThreshold
    )

    /** Adaptive performance-scaled Compose spring. */
    fun <T> spring(
        performance: LiquidGlassPerformanceState,
        dampingRatio: Float = DefaultDampingRatio,
        stiffness: Float = DefaultStiffness,
        visibilityThreshold: T? = null
    ): FiniteAnimationSpec<T> {
        if (performance.motionScale <= 0f) return snap()

        val speedScale = performance.motionScale.coerceIn(0.75f, 1f)
        return composeSpring(
            dampingRatio = dampingRatio.coerceAtLeast(0.01f),
            stiffness = (stiffness.coerceAtLeast(Spring.StiffnessVeryLow) / speedScale)
                .coerceAtMost(Spring.StiffnessHigh),
            visibilityThreshold = visibilityThreshold
        )
    }

    /** Snappy spring shortcut (buttons, toggles, chips). */
    fun <T> snappySpring(
        performance: LiquidGlassPerformanceState? = null,
        visibilityThreshold: T? = null
    ): FiniteAnimationSpec<T> = if (performance != null) {
        spring(performance, SnappyDampingRatio, SnappyStiffness, visibilityThreshold)
    } else {
        spring(SnappyDampingRatio, SnappyStiffness, visibilityThreshold)
    }

    /** Interactive standard spring shortcut (controls, sliders, steppers). */
    fun <T> interactiveSpring(
        performance: LiquidGlassPerformanceState? = null,
        visibilityThreshold: T? = null
    ): FiniteAnimationSpec<T> = if (performance != null) {
        spring(performance, InteractiveDampingRatio, InteractiveStiffness, visibilityThreshold)
    } else {
        spring(InteractiveDampingRatio, InteractiveStiffness, visibilityThreshold)
    }

    /** Spatial spring shortcut (sheets, dialogs, toasts, snackbars). */
    fun <T> spatialSpring(
        performance: LiquidGlassPerformanceState? = null,
        visibilityThreshold: T? = null
    ): FiniteAnimationSpec<T> = if (performance != null) {
        spring(performance, SpatialDampingRatio, SpatialStiffness, visibilityThreshold)
    } else {
        spring(SpatialDampingRatio, SpatialStiffness, visibilityThreshold)
    }

    /** Fluid / bouncy spring shortcut (charts, indicators, tabs). */
    fun <T> fluidSpring(
        performance: LiquidGlassPerformanceState? = null,
        visibilityThreshold: T? = null
    ): FiniteAnimationSpec<T> = if (performance != null) {
        spring(performance, FluidDampingRatio, FluidStiffness, visibilityThreshold)
    } else {
        spring(FluidDampingRatio, FluidStiffness, visibilityThreshold)
    }

    /** Tactile press feedback spring. */
    fun pressSpring(visibilityThreshold: Float = 0.001f): SpringSpec<Float> =
        spring(dampingRatio = PressDampingRatio, stiffness = PressStiffness, visibilityThreshold = visibilityThreshold)

    /** Tactile release feedback spring. */
    fun releaseSpring(visibilityThreshold: Float = 0.001f): SpringSpec<Float> =
        spring(dampingRatio = ReleaseDampingRatio, stiffness = ReleaseStiffness, visibilityThreshold = visibilityThreshold)

    // =========================================================================
    // Universal Tween Builders
    // =========================================================================

    fun <T> tween(
        durationMillis: Int = StandardDurationMillis,
        easing: Easing = FastOutSlowInEasing
    ): FiniteAnimationSpec<T> = composeTween(durationMillis = durationMillis, easing = easing)

    fun <T> tween(
        performance: LiquidGlassPerformanceState,
        durationMillis: Int = StandardDurationMillis,
        easing: Easing = FastOutSlowInEasing
    ): FiniteAnimationSpec<T> {
        val scaledDuration = durationMillis(performance, durationMillis)
        return if (scaledDuration == 0) {
            snap()
        } else {
            composeTween(durationMillis = scaledDuration, easing = easing)
        }
    }

    // =========================================================================
    // Centralized Compose Transition Presets (Enter / Exit)
    // =========================================================================

    private fun <T> transitionSpring(
        performance: LiquidGlassPerformanceState?,
        dampingRatio: Float,
        stiffness: Float
    ): FiniteAnimationSpec<T> = if (performance != null) {
        spring(performance, dampingRatio, stiffness)
    } else {
        spring(dampingRatio, stiffness)
    }

    private fun <T> transitionTween(
        performance: LiquidGlassPerformanceState?,
        durationMillis: Int,
        easing: Easing = FastOutSlowInEasing
    ): FiniteAnimationSpec<T> = if (performance != null) {
        tween(performance, durationMillis, easing)
    } else {
        tween(durationMillis, easing)
    }

    /** Canonical Toast entrance transition: upward slide with scale and fade. */
    fun toastEnter(performance: LiquidGlassPerformanceState? = null): EnterTransition = slideInVertically(
        animationSpec = transitionSpring(performance, dampingRatio = 0.82f, stiffness = 420f),
        initialOffsetY = { -it / 2 }
    ) + scaleIn(
        animationSpec = transitionSpring(performance, dampingRatio = 0.84f, stiffness = 440f),
        initialScale = 0.90f
    ) + fadeIn(
        animationSpec = transitionTween(performance, FastDurationMillis, EmphasizedDecelerate)
    )

    /** Canonical Toast exit transition. */
    fun toastExit(performance: LiquidGlassPerformanceState? = null): ExitTransition = slideOutVertically(
        animationSpec = transitionSpring(performance, dampingRatio = 0.90f, stiffness = 500f),
        targetOffsetY = { -it / 3 }
    ) + scaleOut(
        animationSpec = transitionSpring(performance, dampingRatio = 0.90f, stiffness = 500f),
        targetScale = 0.95f
    ) + fadeOut(
        animationSpec = transitionTween(performance, FastDurationMillis, EmphasizedAccelerate)
    )

    /** Canonical Snackbar entrance transition: upward slide with scale and fade. */
    fun snackbarEnter(performance: LiquidGlassPerformanceState? = null): EnterTransition = slideInVertically(
        animationSpec = transitionSpring(performance, dampingRatio = 0.84f, stiffness = 420f),
        initialOffsetY = { it / 2 }
    ) + scaleIn(
        animationSpec = transitionSpring(performance, dampingRatio = 0.86f, stiffness = 440f),
        initialScale = 0.96f
    ) + fadeIn(animationSpec = transitionTween(performance, FastDurationMillis))

    /** Canonical Snackbar exit transition. */
    fun snackbarExit(performance: LiquidGlassPerformanceState? = null): ExitTransition = slideOutVertically(
        animationSpec = transitionSpring(performance, dampingRatio = 0.92f, stiffness = 500f),
        targetOffsetY = { it / 3 }
    ) + scaleOut(
        animationSpec = transitionSpring(performance, dampingRatio = 0.92f, stiffness = 500f),
        targetScale = 0.98f
    ) + fadeOut(animationSpec = transitionTween(performance, FastDurationMillis))

    /** Canonical Dialog entrance transition: smooth scale pop with fade. */
    fun dialogEnter(performance: LiquidGlassPerformanceState? = null): EnterTransition = scaleIn(
        animationSpec = transitionSpring(performance, SpatialDampingRatio, SpatialStiffness),
        initialScale = 0.92f
    ) + fadeIn(
        animationSpec = transitionTween(performance, StandardDurationMillis, EmphasizedDecelerate)
    )

    /** Canonical Dialog exit transition. */
    fun dialogExit(performance: LiquidGlassPerformanceState? = null): ExitTransition = scaleOut(
        animationSpec = transitionSpring(performance, SnappyDampingRatio, SnappyStiffness),
        targetScale = 0.96f
    ) + fadeOut(
        animationSpec = transitionTween(performance, FastDurationMillis, EmphasizedAccelerate)
    )

    /** Canonical Tooltip entrance transition. */
    fun tooltipEnter(performance: LiquidGlassPerformanceState? = null): EnterTransition = scaleIn(
        animationSpec = transitionSpring(performance, dampingRatio = 0.75f, stiffness = 500f)
    ) + fadeIn(animationSpec = transitionTween(performance, FastDurationMillis))

    /** Canonical Tooltip exit transition. */
    fun tooltipExit(performance: LiquidGlassPerformanceState? = null): ExitTransition = scaleOut(
        animationSpec = transitionSpring(performance, dampingRatio = 0.85f, stiffness = 500f)
    ) + fadeOut(animationSpec = transitionTween(performance, FastDurationMillis))

    /** Scale pop entrance transition. */
    fun popEnter(
        performance: LiquidGlassPerformanceState? = null
    ): EnterTransition = scaleIn(
        animationSpec = snappySpring(performance),
        initialScale = 0.88f
    ) + fadeIn(
        animationSpec = if (performance != null) {
            tween(performance, FastDurationMillis)
        } else {
            tween(FastDurationMillis)
        }
    )

    /** Scale pop exit transition. */
    fun popExit(
        performance: LiquidGlassPerformanceState? = null
    ): ExitTransition = scaleOut(
        animationSpec = snappySpring(performance),
        targetScale = 0.88f
    ) + fadeOut(
        animationSpec = if (performance != null) {
            tween(performance, FastDurationMillis)
        } else {
            tween(FastDurationMillis)
        }
    )

    /** Slide up & fade enter. */
    fun slideUpFadeEnter(
        performance: LiquidGlassPerformanceState? = null,
        initialOffsetY: (fullHeight: Int) -> Int = { it / 2 }
    ): EnterTransition = slideInVertically(
        animationSpec = spatialSpring(performance),
        initialOffsetY = initialOffsetY
    ) + fadeIn(animationSpec = transitionTween(performance, FastDurationMillis))

    /** Slide down & fade exit. */
    fun slideDownFadeExit(
        performance: LiquidGlassPerformanceState? = null,
        targetOffsetY: (fullHeight: Int) -> Int = { it / 2 }
    ): ExitTransition = slideOutVertically(
        animationSpec = spatialSpring(performance),
        targetOffsetY = targetOffsetY
    ) + fadeOut(animationSpec = transitionTween(performance, FastDurationMillis))

    /**
     * Bouncy spring specifically tuned for dropdown menus, popovers, and contextual action bubbles.
     * Features an organic overshoot and elastic settling.
     */
    fun <T> menuBounceSpring(
        performance: LiquidGlassPerformanceState? = null,
        visibilityThreshold: T? = null
    ): FiniteAnimationSpec<T> = if (performance != null) {
        spring(performance, MenuBounceDampingRatio, MenuBounceStiffness, visibilityThreshold)
    } else {
        spring(MenuBounceDampingRatio, MenuBounceStiffness, visibilityThreshold)
    }

    /**
     * Canonical Dropdown Menu entrance transition with a pronounced, organic liquid bounce effect.
     * Scales rapidly from 70% with a spring overshoot and bouncy slide before settling gracefully.
     */
    fun menuEnter(
        performance: LiquidGlassPerformanceState? = null,
        transformOrigin: TransformOrigin = TransformOrigin(0.92f, 0.04f),
        isAbove: Boolean = false
    ): EnterTransition {
        val slideOffset = if (isAbove) 16 else -16
        return scaleIn(
            animationSpec = menuBounceSpring(performance),
            initialScale = 0.84f,
            transformOrigin = transformOrigin
        ) + slideInVertically(
            animationSpec = menuBounceSpring(performance),
            initialOffsetY = { (slideOffset * (it / 100).coerceAtLeast(1)).coerceIn(-24, 24) }
        ) + fadeIn(
            animationSpec = transitionTween(performance, FastDurationMillis, EmphasizedDecelerate)
        )
    }

    /**
     * Canonical Dropdown Menu exit transition. Smoothly collapses with snappy dissipation.
     */
    fun menuExit(
        performance: LiquidGlassPerformanceState? = null,
        transformOrigin: TransformOrigin = TransformOrigin(0.92f, 0.04f),
        isAbove: Boolean = false
    ): ExitTransition {
        val slideOffset = if (isAbove) 16 else -16
        return scaleOut(
            animationSpec = if (performance != null) {
                spring(performance, SnappyDampingRatio, SnappyStiffness)
            } else {
                spring(SnappyDampingRatio, SnappyStiffness)
            },
            targetScale = 0.80f,
            transformOrigin = transformOrigin
        ) + slideOutVertically(
            animationSpec = transitionTween(performance, 140, EmphasizedAccelerate),
            targetOffsetY = { (slideOffset * (it / 100).coerceAtLeast(1)).coerceIn(-28, 28) }
        ) + fadeOut(
            animationSpec = transitionTween(performance, 140, EmphasizedAccelerate)
        )
    }
}
