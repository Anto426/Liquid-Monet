package com.anto426.liquidmonet.motion

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.navigationevent.NavigationEvent.Companion.EDGE_RIGHT
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import kotlinx.coroutines.launch

@Immutable
internal data class LiquidPredictiveBackState(
    val progress: Float,
    val edgeDirection: Float,
)

/**
 * KMP navigation-event bridge shared by Liquid navigation, dialogs and sheets.
 *
 * Live progress is exposed without a time-based filter so visuals stay attached to the finger.
 * Only a cancelled gesture is settled back to zero with Liquid motion.
 */
@Composable
internal fun rememberLiquidPredictiveBackState(
    onPredictiveBack: (() -> Unit)?,
    enabled: Boolean = onPredictiveBack != null,
): LiquidPredictiveBackState {
    val performance = LocalLiquidGlassPerformance.current
    val latestOnBack by rememberUpdatedState(onPredictiveBack)
    val coroutineScope = rememberCoroutineScope()
    val settledProgress = remember { Animatable(0f) }
    val navigationState =
        rememberNavigationEventState(
            currentInfo = NavigationEventInfo.None,
            backInfo = listOf(NavigationEventInfo.None),
        )
    val inProgress =
        navigationState.transitionState as? NavigationEventTransitionState.InProgress
    val latestEvent = inProgress?.latestEvent
    val gestureProgress = latestEvent?.progress?.coerceIn(0f, 1f)

    LaunchedEffect(gestureProgress) {
        if (gestureProgress != null) settledProgress.snapTo(gestureProgress)
    }

    NavigationBackHandler(
        state = navigationState,
        isBackEnabled = enabled && onPredictiveBack != null,
        onBackCancelled = {
            coroutineScope.launch {
                settledProgress.animateTo(
                    targetValue = 0f,
                    animationSpec =
                        LiquidMotion.spring(
                            performance = performance,
                            dampingRatio = 0.84f,
                            stiffness = 480f,
                        ),
                )
            }
        },
        onBackCompleted = {
            latestOnBack?.invoke()
            coroutineScope.launch { settledProgress.snapTo(0f) }
        },
    )

    return LiquidPredictiveBackState(
        progress = gestureProgress ?: settledProgress.value,
        edgeDirection = if (latestEvent?.swipeEdge == EDGE_RIGHT) -1f else 1f,
    )
}
