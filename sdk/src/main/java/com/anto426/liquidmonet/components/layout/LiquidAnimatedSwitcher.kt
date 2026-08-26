package com.anto426.liquidmonet.components.layout

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import com.anto426.liquidmonet.motion.liquidFadeThrough
import com.anto426.liquidmonet.motion.liquidSharedAxisHorizontal
import com.anto426.liquidmonet.motion.liquidSharedAxisVertical

/** Motion styles supported by [LiquidAnimatedSwitcher]. */
enum class LiquidSwitcherTransition {
    /** Horizontal shared-axis motion whose direction follows [LiquidAnimatedSwitcher.isForward]. */
    DirectionalHorizontal,

    /** Vertical shared-axis motion whose direction follows [LiquidAnimatedSwitcher.isForward]. */
    DirectionalVertical,

    /** A compact scale-and-fade transition for content at the same hierarchy level. */
    ScaleFade
}

/**
 * Changes arbitrary content with the same adaptive motion used by Liquid Monet navigation.
 *
 * [isForward] is optional for naturally ordered states: numbers and mutually comparable values
 * are inferred automatically. Unordered states default to forward motion, and can provide their
 * own resolver when semantic order matters.
 */
@Composable
fun <T> LiquidAnimatedSwitcher(
    targetState: T,
    modifier: Modifier = Modifier,
    transition: LiquidSwitcherTransition = LiquidSwitcherTransition.ScaleFade,
    isForward: ((initialState: T, targetState: T) -> Boolean)? = null,
    contentAlignment: Alignment = Alignment.Center,
    label: String = "LiquidAnimatedSwitcher",
    content: @Composable AnimatedContentScope.(targetState: T) -> Unit
) {
    val performance = LocalLiquidGlassPerformance.current

    AnimatedContent(
        targetState = targetState,
        modifier = modifier,
        transitionSpec = {
            val forward = isForward?.invoke(initialState, targetState)
                ?: inferForwardMotion(initialState, targetState)

            when (transition) {
                LiquidSwitcherTransition.DirectionalHorizontal -> {
                    liquidSharedAxisHorizontal(forward = forward, performance = performance)
                }

                LiquidSwitcherTransition.DirectionalVertical -> {
                    liquidSharedAxisVertical(upward = forward, performance = performance)
                }

                LiquidSwitcherTransition.ScaleFade -> liquidFadeThrough(performance)
            }
        },
        contentAlignment = contentAlignment,
        label = label,
        content = content
    )
}

private fun <T> inferForwardMotion(initialState: T, targetState: T): Boolean {
    if (initialState is Number && targetState is Number) {
        return targetState.toDouble() >= initialState.toDouble()
    }

    if (initialState is Comparable<*> && targetState is Comparable<*>) {
        return runCatching {
            @Suppress("UNCHECKED_CAST")
            (targetState as Comparable<Any?>).compareTo(initialState) >= 0
        }.getOrDefault(true)
    }

    return true
}
