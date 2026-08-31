package com.anto426.liquidmonet.motion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import com.anto426.liquidmonet.glass.runtime.LiquidGlassMotionSpecs
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPerformanceState
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance

/**
 * Expressive navigation and screen transition presets for Liquid Monet and Liquid Monet SDK.
 */
enum class LiquidNavTransition {
    /**
     * Material 3 Expressive Shared Axis Horizontal.
     * Slides horizontally with subtle parallax scaling (0.94f -> 1.0f).
     */
    SharedAxisHorizontal,

    /**
     * Shared Axis Vertical for hierarchy drill-down and modal flows.
     */
    SharedAxisVertical,

    /**
     * Material 3 Expressive Fade Through.
     * Ideal for persistent bottom navigation bars and root tabs.
     */
    FadeThrough,

    /**
     * Expressive Elevation Scale & Zoom Morph.
     */
    ElevationScale,

    /**
     * Organic Liquid Elastic Spring Transition.
     */
    LiquidSpring,

    /**
     * Automatically calculates forward/backward slide based on numeric comparison.
     */
    AutoDirectional
}

/**
 * LiquidAnimatedNavContent - Expressive hardware-accelerated container for screen transitions.
 * Automatically adapts motion duration and spring physics to device tier and refresh rate.
 */
@Composable
fun <T> LiquidAnimatedNavContent(
    targetState: T,
    modifier: Modifier = Modifier,
    transition: LiquidNavTransition = LiquidNavTransition.AutoDirectional,
    contentAlignment: Alignment = Alignment.TopStart,
    label: String = "LiquidAnimatedNavContent",
    onPredictiveBack: (() -> Unit)? = null,
    content: @Composable AnimatedContentScope.(targetState: T) -> Unit
) {
    val performance = LocalLiquidGlassPerformance.current
    val predictiveBack = rememberLiquidPredictiveBackState(onPredictiveBack)

    AnimatedContent(
        targetState = targetState,
        modifier = modifier.graphicsLayer {
            val progress = predictiveBack.progress
            transformOrigin = TransformOrigin.Center
            translationX = size.width * 0.16f * progress * predictiveBack.edgeDirection
            scaleX = 1f - 0.025f * progress
            scaleY = 1f - 0.025f * progress
            alpha = 1f - 0.08f * progress
        },
        transitionSpec = {
            when (transition) {
                LiquidNavTransition.AutoDirectional -> {
                    val isForward = if (initialState is Number && targetState is Number) {
                        (targetState as Number).toDouble() >= (initialState as Number).toDouble()
                    } else if (initialState is Comparable<*> && targetState is Comparable<*>) {
                        @Suppress("UNCHECKED_CAST")
                        (targetState as Comparable<Any>) >= (initialState as Comparable<Any>)
                    } else {
                        true
                    }
                    liquidSharedAxisHorizontal(isForward, performance)
                }
                LiquidNavTransition.SharedAxisHorizontal -> {
                    liquidSharedAxisHorizontal(true, performance)
                }
                LiquidNavTransition.SharedAxisVertical -> {
                    liquidSharedAxisVertical(true, performance)
                }
                LiquidNavTransition.FadeThrough -> {
                    liquidFadeThrough(performance)
                }
                LiquidNavTransition.ElevationScale -> {
                    liquidElevationScale(true, performance)
                }
                LiquidNavTransition.LiquidSpring -> {
                    liquidLiquidSpring(true, performance)
                }
            }
        },
        contentAlignment = contentAlignment,
        label = label,
        content = content
    )
}

/**
 * Shared Axis Horizontal Transition Spec with unclipped fluid bounds and organic parallax depth.
 */
fun <S> AnimatedContentTransitionScope<S>.liquidSharedAxisHorizontal(
    forward: Boolean,
    performance: LiquidGlassPerformanceState
): ContentTransform {
    val duration = LiquidGlassMotionSpecs.durationMillis(performance, 340)

    val enter: EnterTransition = slideInHorizontally(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.74f,
            stiffness = 320f
        ),
        initialOffsetX = { if (forward) (it * 0.18f).toInt() else -(it * 0.18f).toInt() }
    ) + fadeIn(
        animationSpec = tween(durationMillis = (duration * 0.70f).toInt(), easing = LinearOutSlowInEasing)
    ) + scaleIn(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.78f,
            stiffness = 340f
        ),
        initialScale = 0.94f
    )

    val exit: ExitTransition = slideOutHorizontally(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.82f,
            stiffness = 360f
        ),
        targetOffsetX = { if (forward) -(it * 0.14f).toInt() else (it * 0.14f).toInt() }
    ) + fadeOut(
        animationSpec = tween(durationMillis = (duration * 0.45f).toInt(), easing = FastOutLinearInEasing)
    ) + scaleOut(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.86f,
            stiffness = 380f
        ),
        targetScale = 0.97f
    )

    return (enter togetherWith exit).using(SizeTransform(clip = false))
}

/**
 * Seekable horizontal back transition for navigation hosts that expose predictive-back progress.
 *
 * Unlike [liquidSharedAxisHorizontal], this preset deliberately uses linear timing: the host seeks
 * the transition from the user's gesture progress, so springs and delayed fades would make the
 * screen lag behind the finger. The host remains responsible for consuming or cancelling Back.
 *
 * @param fromRightEdge true when the gesture starts from the right display edge (RTL direction).
 */
fun <S> AnimatedContentTransitionScope<S>.liquidPredictiveBackHorizontal(
    fromRightEdge: Boolean,
    performance: LiquidGlassPerformanceState,
): ContentTransform {
    val duration = LiquidGlassMotionSpecs.durationMillis(performance, 420)
    val direction = if (fromRightEdge) -1 else 1

    val enter =
        slideInHorizontally(
            animationSpec = tween(durationMillis = duration, easing = LinearEasing),
            initialOffsetX = { width -> -(width * 0.18f * direction).toInt() },
        ) +
            fadeIn(
                animationSpec = tween(durationMillis = duration, easing = LinearEasing),
            ) +
            scaleIn(
                animationSpec = tween(durationMillis = duration, easing = LinearEasing),
                initialScale = 0.96f,
            )

    val exit =
        slideOutHorizontally(
            animationSpec = tween(durationMillis = duration, easing = LinearEasing),
            targetOffsetX = { width -> (width * 0.16f * direction).toInt() },
        ) +
            fadeOut(
                animationSpec = tween(durationMillis = duration, easing = LinearEasing),
                targetAlpha = 0.88f,
            ) +
            scaleOut(
                animationSpec = tween(durationMillis = duration, easing = LinearEasing),
                targetScale = 0.985f,
            )

    return (enter togetherWith exit).using(SizeTransform(clip = false))
}

/**
 * Shared Axis Vertical Transition Spec.
 */
fun <S> AnimatedContentTransitionScope<S>.liquidSharedAxisVertical(
    upward: Boolean,
    performance: LiquidGlassPerformanceState
): ContentTransform {
    val duration = LiquidGlassMotionSpecs.durationMillis(performance, 320)

    val enter: EnterTransition = slideInVertically(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.74f,
            stiffness = 320f
        ),
        initialOffsetY = { if (upward) (it * 0.16f).toInt() else -(it * 0.16f).toInt() }
    ) + fadeIn(
        animationSpec = tween(durationMillis = (duration * 0.68f).toInt(), easing = LinearOutSlowInEasing)
    ) + scaleIn(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.78f,
            stiffness = 340f
        ),
        initialScale = 0.95f
    )

    val exit: ExitTransition = slideOutVertically(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.82f,
            stiffness = 360f
        ),
        targetOffsetY = { if (upward) -(it * 0.12f).toInt() else (it * 0.12f).toInt() }
    ) + fadeOut(
        animationSpec = tween(durationMillis = (duration * 0.45f).toInt(), easing = FastOutLinearInEasing)
    ) + scaleOut(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.86f,
            stiffness = 380f
        ),
        targetScale = 0.97f
    )

    return (enter togetherWith exit).using(SizeTransform(clip = false))
}

/**
 * Material 3 Fade Through Transition Spec with unclipped morphology.
 */
fun <S> AnimatedContentTransitionScope<S>.liquidFadeThrough(
    performance: LiquidGlassPerformanceState
): ContentTransform {
    val duration = LiquidGlassMotionSpecs.durationMillis(performance, 280)

    val enter: EnterTransition = fadeIn(
        animationSpec = tween(durationMillis = (duration * 0.72f).toInt(), delayMillis = (duration * 0.28f).toInt(), easing = LinearOutSlowInEasing)
    ) + scaleIn(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.78f,
            stiffness = 340f
        ),
        initialScale = 0.93f
    )

    val exit: ExitTransition = fadeOut(
        animationSpec = tween(durationMillis = (duration * 0.38f).toInt(), easing = FastOutLinearInEasing)
    ) + scaleOut(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.82f,
            stiffness = 360f
        ),
        targetScale = 0.97f
    )

    return (enter togetherWith exit).using(SizeTransform(clip = false))
}

/**
 * Elevation Scale / Zoom Morph Transition Spec.
 */
fun <S> AnimatedContentTransitionScope<S>.liquidElevationScale(
    forward: Boolean,
    performance: LiquidGlassPerformanceState
): ContentTransform {
    val duration = LiquidGlassMotionSpecs.durationMillis(performance, 320)

    val enter: EnterTransition = fadeIn(
        animationSpec = tween(durationMillis = (duration * 0.75f).toInt(), easing = LinearOutSlowInEasing)
    ) + scaleIn(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.76f,
            stiffness = 340f
        ),
        initialScale = if (forward) 0.92f else 1.05f
    )

    val exit: ExitTransition = fadeOut(
        animationSpec = tween(durationMillis = (duration * 0.42f).toInt(), easing = FastOutLinearInEasing)
    ) + scaleOut(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.82f,
            stiffness = 360f
        ),
        targetScale = if (forward) 1.05f else 0.92f
    )

    return (enter togetherWith exit).using(SizeTransform(clip = false))
}

/**
 * Liquid Elastic Spring Transition Spec with visceral fluid elongation and spring overshoot.
 */
fun <S> AnimatedContentTransitionScope<S>.liquidLiquidSpring(
    forward: Boolean,
    performance: LiquidGlassPerformanceState
): ContentTransform {
    val enter: EnterTransition = slideInHorizontally(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.65f,
            stiffness = 280f
        ),
        initialOffsetX = { if (forward) (it * 0.22f).toInt() else -(it * 0.22f).toInt() }
    ) + fadeIn(
        animationSpec = LiquidGlassMotionSpecs.tween(performance, 200)
    ) + scaleIn(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.68f,
            stiffness = 290f
        ),
        initialScale = 0.92f
    )

    val exit: ExitTransition = slideOutHorizontally(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.78f,
            stiffness = 340f
        ),
        targetOffsetX = { if (forward) -(it * 0.16f).toInt() else (it * 0.16f).toInt() }
    ) + fadeOut(
        animationSpec = LiquidGlassMotionSpecs.tween(performance, 140)
    ) + scaleOut(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.82f,
            stiffness = 360f
        ),
        targetScale = 0.96f
    )

    return (enter togetherWith exit).using(SizeTransform(clip = false))
}
