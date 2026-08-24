package com.anto426.antoui.motion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutLinearInEasing
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
import com.anto426.antoui.glass.runtime.AntoGlassMotionSpecs
import com.anto426.antoui.glass.runtime.AntoGlassPerformanceState
import com.anto426.antoui.glass.runtime.LocalAntoGlassPerformance

/**
 * Expressive navigation and screen transition presets for AntoUI and Liquid Monet SDK.
 */
enum class AntoNavTransition {
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
 * AntoAnimatedNavContent - Expressive hardware-accelerated container for screen transitions.
 * Automatically adapts motion duration and spring physics to device tier and refresh rate.
 */
@Composable
fun <T> AntoAnimatedNavContent(
    targetState: T,
    modifier: Modifier = Modifier,
    transition: AntoNavTransition = AntoNavTransition.AutoDirectional,
    contentAlignment: Alignment = Alignment.TopStart,
    label: String = "AntoAnimatedNavContent",
    content: @Composable AnimatedContentScope.(targetState: T) -> Unit
) {
    val performance = LocalAntoGlassPerformance.current

    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = {
            when (transition) {
                AntoNavTransition.AutoDirectional -> {
                    val isForward = if (initialState is Number && targetState is Number) {
                        (targetState as Number).toDouble() >= (initialState as Number).toDouble()
                    } else if (initialState is Comparable<*> && targetState is Comparable<*>) {
                        @Suppress("UNCHECKED_CAST")
                        (targetState as Comparable<Any>) >= (initialState as Comparable<Any>)
                    } else {
                        true
                    }
                    antoSharedAxisHorizontal(isForward, performance)
                }
                AntoNavTransition.SharedAxisHorizontal -> {
                    antoSharedAxisHorizontal(true, performance)
                }
                AntoNavTransition.SharedAxisVertical -> {
                    antoSharedAxisVertical(true, performance)
                }
                AntoNavTransition.FadeThrough -> {
                    antoFadeThrough(performance)
                }
                AntoNavTransition.ElevationScale -> {
                    antoElevationScale(true, performance)
                }
                AntoNavTransition.LiquidSpring -> {
                    antoLiquidSpring(true, performance)
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
fun <S> AnimatedContentTransitionScope<S>.antoSharedAxisHorizontal(
    forward: Boolean,
    performance: AntoGlassPerformanceState
): ContentTransform {
    val duration = AntoGlassMotionSpecs.durationMillis(performance, 340)

    val enter: EnterTransition = slideInHorizontally(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.74f,
            stiffness = 320f
        ),
        initialOffsetX = { if (forward) (it * 0.18f).toInt() else -(it * 0.18f).toInt() }
    ) + fadeIn(
        animationSpec = tween(durationMillis = (duration * 0.70f).toInt(), easing = LinearOutSlowInEasing)
    ) + scaleIn(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.78f,
            stiffness = 340f
        ),
        initialScale = 0.94f
    )

    val exit: ExitTransition = slideOutHorizontally(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.82f,
            stiffness = 360f
        ),
        targetOffsetX = { if (forward) -(it * 0.14f).toInt() else (it * 0.14f).toInt() }
    ) + fadeOut(
        animationSpec = tween(durationMillis = (duration * 0.45f).toInt(), easing = FastOutLinearInEasing)
    ) + scaleOut(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.86f,
            stiffness = 380f
        ),
        targetScale = 0.97f
    )

    return (enter togetherWith exit).using(SizeTransform(clip = false))
}

/**
 * Shared Axis Vertical Transition Spec.
 */
fun <S> AnimatedContentTransitionScope<S>.antoSharedAxisVertical(
    upward: Boolean,
    performance: AntoGlassPerformanceState
): ContentTransform {
    val duration = AntoGlassMotionSpecs.durationMillis(performance, 320)

    val enter: EnterTransition = slideInVertically(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.74f,
            stiffness = 320f
        ),
        initialOffsetY = { if (upward) (it * 0.16f).toInt() else -(it * 0.16f).toInt() }
    ) + fadeIn(
        animationSpec = tween(durationMillis = (duration * 0.68f).toInt(), easing = LinearOutSlowInEasing)
    ) + scaleIn(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.78f,
            stiffness = 340f
        ),
        initialScale = 0.95f
    )

    val exit: ExitTransition = slideOutVertically(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.82f,
            stiffness = 360f
        ),
        targetOffsetY = { if (upward) -(it * 0.12f).toInt() else (it * 0.12f).toInt() }
    ) + fadeOut(
        animationSpec = tween(durationMillis = (duration * 0.45f).toInt(), easing = FastOutLinearInEasing)
    ) + scaleOut(
        animationSpec = AntoGlassMotionSpecs.spring(
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
fun <S> AnimatedContentTransitionScope<S>.antoFadeThrough(
    performance: AntoGlassPerformanceState
): ContentTransform {
    val duration = AntoGlassMotionSpecs.durationMillis(performance, 280)

    val enter: EnterTransition = fadeIn(
        animationSpec = tween(durationMillis = (duration * 0.72f).toInt(), delayMillis = (duration * 0.28f).toInt(), easing = LinearOutSlowInEasing)
    ) + scaleIn(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.78f,
            stiffness = 340f
        ),
        initialScale = 0.93f
    )

    val exit: ExitTransition = fadeOut(
        animationSpec = tween(durationMillis = (duration * 0.38f).toInt(), easing = FastOutLinearInEasing)
    ) + scaleOut(
        animationSpec = AntoGlassMotionSpecs.spring(
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
fun <S> AnimatedContentTransitionScope<S>.antoElevationScale(
    forward: Boolean,
    performance: AntoGlassPerformanceState
): ContentTransform {
    val duration = AntoGlassMotionSpecs.durationMillis(performance, 320)

    val enter: EnterTransition = fadeIn(
        animationSpec = tween(durationMillis = (duration * 0.75f).toInt(), easing = LinearOutSlowInEasing)
    ) + scaleIn(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.76f,
            stiffness = 340f
        ),
        initialScale = if (forward) 0.92f else 1.05f
    )

    val exit: ExitTransition = fadeOut(
        animationSpec = tween(durationMillis = (duration * 0.42f).toInt(), easing = FastOutLinearInEasing)
    ) + scaleOut(
        animationSpec = AntoGlassMotionSpecs.spring(
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
fun <S> AnimatedContentTransitionScope<S>.antoLiquidSpring(
    forward: Boolean,
    performance: AntoGlassPerformanceState
): ContentTransform {
    val enter: EnterTransition = slideInHorizontally(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.65f,
            stiffness = 280f
        ),
        initialOffsetX = { if (forward) (it * 0.22f).toInt() else -(it * 0.22f).toInt() }
    ) + fadeIn(
        animationSpec = AntoGlassMotionSpecs.tween(performance, 200)
    ) + scaleIn(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.68f,
            stiffness = 290f
        ),
        initialScale = 0.92f
    )

    val exit: ExitTransition = slideOutHorizontally(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.78f,
            stiffness = 340f
        ),
        targetOffsetX = { if (forward) -(it * 0.16f).toInt() else (it * 0.16f).toInt() }
    ) + fadeOut(
        animationSpec = AntoGlassMotionSpecs.tween(performance, 140)
    ) + scaleOut(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.82f,
            stiffness = 360f
        ),
        targetScale = 0.96f
    )

    return (enter togetherWith exit).using(SizeTransform(clip = false))
}
