package com.anto426.liquidmonet.motion

import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.anto426.liquidmonet.glass.runtime.LiquidGlassMotionSpecs
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collect

@Immutable
internal data class LiquidPredictiveBackState(
    val progress: Float,
    val edgeDirection: Float
)

/** Bridges Android's native back progress into the SDK motion system. */
@Composable
internal fun rememberLiquidPredictiveBackState(
    onPredictiveBack: (() -> Unit)?
): LiquidPredictiveBackState {
    val performance = LocalLiquidGlassPerformance.current
    val latestOnBack by rememberUpdatedState(onPredictiveBack)
    var progress by remember { mutableFloatStateOf(0f) }
    var edgeDirection by remember { mutableFloatStateOf(1f) }

    PredictiveBackHandler(enabled = onPredictiveBack != null) { progressFlow ->
        try {
            progressFlow.collect { event ->
                progress = event.progress.coerceIn(0f, 1f)
                edgeDirection = when (event.swipeEdge) {
                    BackEventCompat.EDGE_RIGHT -> -1f
                    else -> 1f
                }
            }
            latestOnBack?.invoke()
            progress = 0f
        } catch (_: CancellationException) {
            Animatable(progress).animateTo(
                targetValue = 0f,
                animationSpec = LiquidGlassMotionSpecs.spring(
                    performance = performance,
                    dampingRatio = 0.84f,
                    stiffness = 480f
                )
            ) { progress = value }
        }
    }

    return LiquidPredictiveBackState(progress, edgeDirection)
}
