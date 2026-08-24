package com.anto426.antoui.glass.overlay

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.anto426.antoui.glass.runtime.AntoGlassMotionSpecs
import com.anto426.antoui.glass.runtime.AntoGlassPerformanceState
import com.anto426.antoui.glass.runtime.LocalAntoGlassPerformance
import com.kyant.backdrop.Backdrop
import kotlin.math.roundToInt

/**
 * Coordinates a single glass overlay rendered inside [AntoGlassScene].
 *
 * Rendering in the scene, instead of in a platform popup window, keeps the overlay in the same
 * graphics tree as the recorded Backdrop layer. Consumers normally interact with this state
 * through the menu components rather than calling it directly.
 */
@Stable
class AntoGlassOverlayState internal constructor() {

    internal var entry: AntoGlassOverlayEntry? by mutableStateOf(null)
        private set

    internal fun show(
        key: Any,
        anchorBoundsInWindow: Rect,
        placement: AntoGlassOverlayPlacement,
        offset: DpOffset,
        onDismissRequest: () -> Unit,
        content: @Composable (dismiss: () -> Unit) -> Unit
    ) {
        val current = entry
        if (current != null && current.key != key && current.visible) {
            current.onDismissRequest()
        }
        entry = AntoGlassOverlayEntry(
            key = key,
            anchorBoundsInWindow = anchorBoundsInWindow,
            placement = placement,
            offset = offset,
            visible = true,
            onDismissRequest = onDismissRequest,
            content = content
        )
    }

    internal fun updateAnchor(key: Any, anchorBoundsInWindow: Rect) {
        val current = entry ?: return
        if (current.key == key && current.anchorBoundsInWindow != anchorBoundsInWindow) {
            entry = current.copy(anchorBoundsInWindow = anchorBoundsInWindow)
        }
    }

    internal fun dismiss(key: Any? = null, notifyOnDismiss: Boolean = true) {
        val current = entry ?: return
        if (key != null && current.key != key) return
        if (!current.visible) return

        entry = current.copy(visible = false)
        if (notifyOnDismiss) current.onDismissRequest()
    }

    internal fun removeImmediately(key: Any) {
        if (entry?.key == key) entry = null
    }

    internal fun removeAfterExit(key: Any) {
        val current = entry ?: return
        if (current.key == key && !current.visible) entry = null
    }
}

/** Bounds holder used to anchor a hosted overlay to a real laid-out component. */
@Stable
class AntoGlassOverlayAnchorState internal constructor() {

    var boundsInWindow: Rect? by mutableStateOf(null)
        internal set
}

@Composable
fun rememberAntoGlassOverlayAnchorState(): AntoGlassOverlayAnchorState =
    remember { AntoGlassOverlayAnchorState() }

/** Captures the final window-space bounds used by [AntoGlassScene]'s overlay host. */
fun Modifier.antoGlassOverlayAnchor(state: AntoGlassOverlayAnchorState): Modifier =
    onGloballyPositioned { coordinates ->
        state.boundsInWindow = coordinates.boundsInWindow()
    }

/** Direction and alignment options for glass dropdown menus and overlays. */
enum class AntoGlassDropdownPlacement {
    /** Appears below the trigger, aligned to its trailing edge (right in LTR). Default. */
    BelowEnd,

    /** Appears below the trigger, aligned to its leading edge (left in LTR). */
    BelowStart,

    /** Appears below the trigger, centered horizontally. */
    BelowCenter,

    /** Appears above the trigger, aligned to its trailing edge. */
    AboveEnd,

    /** Appears above the trigger, aligned to its leading edge. */
    AboveStart,

    /** Appears above the trigger, centered horizontally. */
    AboveCenter,

    /** Legacy anchor placement overlaying the anchor top edge. */
    AnchorTopEnd,

    /** Automatically determines whether to open above or below depending on available space. */
    Auto
}

typealias AntoGlassOverlayPlacement = AntoGlassDropdownPlacement

internal val LocalAntoGlassOverlayState =
    staticCompositionLocalOf<AntoGlassOverlayState?> { null }

/** Backdrop containing the fully composed base scene, excluding hosted overlays themselves. */
internal val LocalAntoGlassContentBackdrop =
    staticCompositionLocalOf<Backdrop?> { null }

@Composable
internal fun rememberAntoGlassOverlayState(): AntoGlassOverlayState =
    remember { AntoGlassOverlayState() }

@Composable
internal fun BoxScope.AntoGlassOverlayHost(state: AntoGlassOverlayState) {
    val entry = state.entry ?: return
    val performance = LocalAntoGlassPerformance.current
    var hostBoundsInWindow by remember { mutableStateOf(Rect.Zero) }
    val visibility = remember(entry.key) {
        MutableTransitionState(false).apply { targetState = entry.visible }
    }

    LaunchedEffect(entry.visible) {
        visibility.targetState = entry.visible
    }
    LaunchedEffect(visibility.isIdle, visibility.currentState) {
        if (visibility.isIdle && !visibility.currentState && !visibility.targetState) {
            state.removeAfterExit(entry.key)
        }
    }

    BackHandler(enabled = entry.visible) {
        state.dismiss(entry.key)
    }

    Box(
        modifier = Modifier
            .matchParentSize()
            .zIndex(1_000_000f)
            .onGloballyPositioned { coordinates ->
                hostBoundsInWindow = coordinates.boundsInWindow()
            }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = null,
                    indication = null,
                    enabled = entry.visible,
                    onClick = { state.dismiss(entry.key) }
                )
                .clearAndSetSemantics { }
        )

        AntoGlassOverlayPositioner(
            anchorBoundsInWindow = entry.anchorBoundsInWindow,
            hostBoundsInWindow = hostBoundsInWindow,
            placement = entry.placement,
            offset = entry.offset,
            modifier = Modifier.matchParentSize()
        ) { alignment, transformOrigin ->
            val anchorSize = IntSize(
                width = entry.anchorBoundsInWindow.width.roundToInt().coerceAtLeast(1),
                height = entry.anchorBoundsInWindow.height.roundToInt().coerceAtLeast(1)
            )
            AnimatedVisibility(
                visibleState = visibility,
                enter = glassOverlayEnterTransition(anchorSize, performance, alignment, transformOrigin),
                exit = glassOverlayExitTransition(anchorSize, performance, alignment, transformOrigin)
            ) {
                entry.content { state.dismiss(entry.key) }
            }
        }
    }
}

@Composable
private fun AntoGlassOverlayPositioner(
    anchorBoundsInWindow: Rect,
    hostBoundsInWindow: Rect,
    placement: AntoGlassDropdownPlacement,
    offset: DpOffset,
    modifier: Modifier = Modifier,
    content: @Composable (alignment: Alignment, transformOrigin: TransformOrigin) -> Unit
) {
    var animationAlignment by remember { mutableStateOf(Alignment.TopEnd) }
    var animationOrigin by remember { mutableStateOf(TransformOrigin(0.92f, 0.04f)) }

    Layout(
        modifier = modifier,
        content = {
            content(animationAlignment, animationOrigin)
        }
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val margin = 12.dp.roundToPx()
        val maxOverlayWidth = (width - margin * 2).coerceAtLeast(0)
        val maxOverlayHeight = (height - margin * 2).coerceAtLeast(0)
        val placeable = measurables.singleOrNull()?.measure(
            constraints.copy(
                minWidth = 0,
                minHeight = 0,
                maxWidth = maxOverlayWidth,
                maxHeight = maxOverlayHeight
            )
        )

        layout(width, height) {
            if (placeable == null) return@layout

            val anchorLeft = anchorBoundsInWindow.left - hostBoundsInWindow.left
            val anchorTop = anchorBoundsInWindow.top - hostBoundsInWindow.top
            val anchorRight = anchorBoundsInWindow.right - hostBoundsInWindow.left
            val anchorBottom = anchorBoundsInWindow.bottom - hostBoundsInWindow.top
            val anchorCenterX = (anchorLeft + anchorRight) / 2f
            val offsetX = offset.x.roundToPx()
            val offsetY = offset.y.roundToPx()

            val minX = margin
            val maxX = (width - margin - placeable.width).coerceAtLeast(minX)

            val preferredX = when (placement) {
                AntoGlassDropdownPlacement.BelowStart,
                AntoGlassDropdownPlacement.AboveStart -> (anchorLeft + offsetX).roundToInt()
                AntoGlassDropdownPlacement.BelowCenter,
                AntoGlassDropdownPlacement.AboveCenter -> (anchorCenterX - placeable.width / 2f + offsetX).roundToInt()
                AntoGlassDropdownPlacement.BelowEnd,
                AntoGlassDropdownPlacement.AboveEnd,
                AntoGlassDropdownPlacement.AnchorTopEnd,
                AntoGlassDropdownPlacement.Auto -> (anchorRight - placeable.width + offsetX).roundToInt()
            }
            val x = preferredX.coerceIn(minX, maxX)

            val belowY = (anchorBottom + offsetY).roundToInt()
            val aboveY = (anchorTop - offsetY - placeable.height).roundToInt()
            val minY = margin
            val maxY = (height - margin - placeable.height).coerceAtLeast(minY)

            val isAbove = when (placement) {
                AntoGlassDropdownPlacement.AboveStart,
                AntoGlassDropdownPlacement.AboveCenter,
                AntoGlassDropdownPlacement.AboveEnd -> true
                AntoGlassDropdownPlacement.BelowStart,
                AntoGlassDropdownPlacement.BelowCenter,
                AntoGlassDropdownPlacement.BelowEnd,
                AntoGlassDropdownPlacement.AnchorTopEnd -> false
                AntoGlassDropdownPlacement.Auto -> (belowY > maxY && aboveY >= minY)
            }

            val preferredY = when (placement) {
                AntoGlassDropdownPlacement.AnchorTopEnd -> (anchorTop + offsetY).roundToInt()
                else -> if (isAbove) aboveY else belowY
            }
            val y = preferredY.coerceIn(minY, maxY)

            val targetAlignment = when {
                isAbove && (placement == AntoGlassDropdownPlacement.AboveStart) -> Alignment.BottomStart
                isAbove && (placement == AntoGlassDropdownPlacement.AboveCenter) -> Alignment.BottomCenter
                isAbove -> Alignment.BottomEnd
                !isAbove && (placement == AntoGlassDropdownPlacement.BelowStart) -> Alignment.TopStart
                !isAbove && (placement == AntoGlassDropdownPlacement.BelowCenter) -> Alignment.TopCenter
                else -> Alignment.TopEnd
            }

            val targetOrigin = when (targetAlignment) {
                Alignment.TopEnd -> TransformOrigin(0.92f, 0.04f)
                Alignment.TopStart -> TransformOrigin(0.08f, 0.04f)
                Alignment.TopCenter -> TransformOrigin(0.50f, 0.04f)
                Alignment.BottomEnd -> TransformOrigin(0.92f, 0.96f)
                Alignment.BottomStart -> TransformOrigin(0.08f, 0.96f)
                Alignment.BottomCenter -> TransformOrigin(0.50f, 0.96f)
                else -> TransformOrigin(0.92f, 0.04f)
            }

            if (animationAlignment != targetAlignment || animationOrigin != targetOrigin) {
                animationAlignment = targetAlignment
                animationOrigin = targetOrigin
            }

            placeable.place(x, y)
        }
    }
}

private fun glassOverlayEnterTransition(
    anchorSize: IntSize,
    performance: AntoGlassPerformanceState,
    alignment: Alignment = Alignment.TopEnd,
    transformOrigin: TransformOrigin = TransformOrigin(0.92f, 0.04f)
): EnterTransition =
    expandIn(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.60f,
            stiffness = 75f
        ),
        expandFrom = alignment,
        clip = false,
        initialSize = { fullSize ->
            IntSize(
                width = anchorSize.width.coerceAtMost(fullSize.width),
                height = anchorSize.height.coerceAtMost(fullSize.height)
            )
        }
    ) + scaleIn(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.58f,
            stiffness = 68f
        ),
        initialScale = 0.02f,
        transformOrigin = transformOrigin
    ) + fadeIn(
        animationSpec = AntoGlassMotionSpecs.tween(
            performance = performance,
            durationMillis = 500
        )
    )

private fun glassOverlayExitTransition(
    anchorSize: IntSize,
    performance: AntoGlassPerformanceState,
    alignment: Alignment = Alignment.TopEnd,
    transformOrigin: TransformOrigin = TransformOrigin(0.92f, 0.04f)
): ExitTransition =
    shrinkOut(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.82f,
            stiffness = 110f
        ),
        shrinkTowards = alignment,
        clip = false,
        targetSize = { fullSize ->
            IntSize(
                width = anchorSize.width.coerceAtMost(fullSize.width),
                height = anchorSize.height.coerceAtMost(fullSize.height)
            )
        }
    ) + scaleOut(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.82f,
            stiffness = 110f
        ),
        targetScale = 0.02f,
        transformOrigin = transformOrigin
    ) + fadeOut(
        animationSpec = AntoGlassMotionSpecs.tween(
            performance = performance,
            durationMillis = 360
        )
    )

internal data class AntoGlassOverlayEntry(
    val key: Any,
    val anchorBoundsInWindow: Rect,
    val placement: AntoGlassOverlayPlacement,
    val offset: DpOffset,
    val visible: Boolean,
    val onDismissRequest: () -> Unit,
    val content: @Composable (dismiss: () -> Unit) -> Unit
)
