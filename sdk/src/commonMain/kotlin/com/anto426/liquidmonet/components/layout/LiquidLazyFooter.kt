package com.anto426.liquidmonet.components.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.theme.LiquidGlassTheme
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.zIndex
import com.anto426.liquidmonet.components.internal.LiquidHapticCue
import com.anto426.liquidmonet.components.internal.performLiquidHaptic
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.motion.LiquidMotion
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import com.anto426.liquidmonet.icons.LiquidIcons
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.RoundedRectangle

/** Scroll axis of the lazy container hosting [LiquidLazyFooter]. */
enum class LiquidLazyFooterOrientation {
    Vertical,
    Horizontal
}

/** Visible state rendered by [LiquidLazyFooter]. */
enum class LiquidLazyFooterState {
    Hidden,
    Loading,
    End,
    Error
}

/**
 * One reusable endpoint for both `LazyColumn` and `LazyRow`.
 *
 * Place it inside a lazy `item { ... }` and match [orientation] to the list scroll axis. The root
 * is the only glass layer: progress, status and retry content do not create nested glass panels.
 * Backdrop sampling is obtained from the nearest Liquid glass scene and is intentionally absent
 * from the public API.
 */
@Composable
fun LiquidLazyFooter(
    state: LiquidLazyFooterState,
    modifier: Modifier = Modifier,
    orientation: LiquidLazyFooterOrientation = LiquidLazyFooterOrientation.Vertical,
    loadingLabel: String? = null,
    endLabel: String? = null,
    errorLabel: String? = null,
    retryLabel: String? = null,
    onRetry: (() -> Unit)? = null,
    shape: Shape = RoundedRectangle(20.dp)
) {
    val performance = LocalLiquidGlassPerformance.current
    val hapticFeedback = LocalHapticFeedback.current
    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
    val visible = state != LiquidLazyFooterState.Hidden
    var lastVisibleState by remember {
        mutableStateOf(
            state.takeUnless { it == LiquidLazyFooterState.Hidden }
                ?: LiquidLazyFooterState.Loading
        )
    }
    val renderedState = state.takeUnless { it == LiquidLazyFooterState.Hidden }
        ?: lastVisibleState
    val liquidImpulse = remember { Animatable(0f) }
    val transformOrigin = when (orientation) {
        LiquidLazyFooterOrientation.Vertical -> TransformOrigin(0.5f, 1f)
        LiquidLazyFooterOrientation.Horizontal -> TransformOrigin(
            pivotFractionX = if (isLtr) 1f else 0f,
            pivotFractionY = 0.5f
        )
    }

    LaunchedEffect(state) {
        if (state == LiquidLazyFooterState.Hidden) {
            liquidImpulse.snapTo(0f)
        } else {
            lastVisibleState = state
            // A single damped impulse makes Loading/End/Error changes feel liquid without
            // running a permanent animation while an idle footer is on screen.
            liquidImpulse.snapTo(1f)
            liquidImpulse.animateTo(
                targetValue = 0f,
                animationSpec = LiquidMotion.spring(
                    performance = performance,
                    dampingRatio = 0.48f,
                    stiffness = 380f
                )
            )
        }
    }

    val enterTransition = fadeIn(LiquidMotion.tween(performance, 190)) +
        scaleIn(
            animationSpec = LiquidMotion.spring(
                performance = performance,
                dampingRatio = 0.56f,
                stiffness = 340f
            ),
            initialScale = 0.90f,
            transformOrigin = transformOrigin
        ) + when (orientation) {
            LiquidLazyFooterOrientation.Vertical -> expandVertically(
                animationSpec = LiquidMotion.spring(
                    performance = performance,
                    dampingRatio = 0.62f,
                    stiffness = 360f
                ),
                // The footer is the final lazy item: keep its outer edge anchored so spring
                // overshoot travels back over the preceding item instead of outside the viewport.
                expandFrom = Alignment.Bottom,
                clip = false
            )
            LiquidLazyFooterOrientation.Horizontal -> expandHorizontally(
                animationSpec = LiquidMotion.spring(
                    performance = performance,
                    dampingRatio = 0.62f,
                    stiffness = 360f
                ),
                expandFrom = Alignment.End,
                clip = false
            )
        }
    val exitTransition = fadeOut(LiquidMotion.tween(performance, 140)) +
        scaleOut(
            animationSpec = LiquidMotion.spring(
                performance = performance,
                dampingRatio = 0.80f,
                stiffness = 440f
            ),
            targetScale = 0.92f,
            transformOrigin = transformOrigin
        ) + when (orientation) {
            LiquidLazyFooterOrientation.Vertical -> shrinkVertically(
                animationSpec = LiquidMotion.tween(performance, 170),
                shrinkTowards = Alignment.Bottom,
                clip = false
            )
            LiquidLazyFooterOrientation.Horizontal -> shrinkHorizontally(
                animationSpec = LiquidMotion.tween(performance, 170),
                shrinkTowards = Alignment.End,
                clip = false
            )
        }

    AnimatedVisibility(
        visible = visible,
        // zIndex is applied to the lazy item root so the under-damped edge can draw over the
        // neighbouring item. The drawing gutter lives inside AnimatedVisibility: Hidden must
        // collapse to zero instead of leaving a phantom 12 dp footer behind.
        modifier = modifier
            .zIndex(2f),
        enter = enterTransition,
        exit = exitTransition
    ) {
        val orientationModifier = when (orientation) {
            LiquidLazyFooterOrientation.Vertical -> Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)

            LiquidLazyFooterOrientation.Horizontal -> Modifier
                .fillMaxHeight()
                .widthIn(min = 156.dp, max = 240.dp)
        }

        Box(
            modifier = Modifier
                .zIndex(2f)
                .padding(LiquidLazyDefaults.OverflowPadding),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = orientationModifier
                    .graphicsLayer {
                        val impulse = liquidImpulse.value
                        this.transformOrigin = transformOrigin
                        clip = false
                        when (orientation) {
                            LiquidLazyFooterOrientation.Vertical -> {
                                scaleX = 1f + impulse * 0.045f
                                scaleY = 1f - impulse * 0.10f
                            }

                            LiquidLazyFooterOrientation.Horizontal -> {
                                scaleX = 1f - impulse * 0.10f
                                scaleY = 1f + impulse * 0.045f
                            }
                        }
                    }
                    .liquidGlass(
                        backdrop = emptyBackdrop(),
                        shape = shape,
                        role = LiquidGlassRole.Surface
                    )
                    .then(
                        if (state == LiquidLazyFooterState.Error && onRetry != null) {
                            Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                role = Role.Button
                            ) {
                                hapticFeedback.performLiquidHaptic(LiquidHapticCue.Action)
                                onRetry()
                            }
                        } else {
                            Modifier
                        }
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                LiquidAnimatedSwitcher(
                    targetState = renderedState,
                    transition = LiquidSwitcherTransition.LiquidMorph,
                    label = "LiquidLazyFooterState"
                ) { currentState ->
                    LiquidLazyFooterContent(
                        state = currentState,
                        loadingLabel = loadingLabel,
                        endLabel = endLabel,
                        errorLabel = errorLabel,
                        retryLabel = retryLabel,
                        onRetry = onRetry
                    )
                }
            }
        }
    }
}

@Composable
private fun LiquidLazyFooterContent(
    state: LiquidLazyFooterState,
    loadingLabel: String?,
    endLabel: String?,
    errorLabel: String?,
    retryLabel: String?,
    onRetry: (() -> Unit)?
) {
    val colorScheme = MaterialTheme.colorScheme
    val stateLabel = when (state) {
        LiquidLazyFooterState.Hidden -> null
        LiquidLazyFooterState.Loading -> loadingLabel
        LiquidLazyFooterState.End -> endLabel
        LiquidLazyFooterState.Error -> errorLabel
    }

    Row(
        modifier = Modifier.then(
            if (stateLabel != null) Modifier.semantics { stateDescription = stateLabel }
            else Modifier
        ),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (state) {
            LiquidLazyFooterState.Hidden -> Unit

            LiquidLazyFooterState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = colorScheme.primary,
                    strokeWidth = 2.5.dp
                )
            }

            LiquidLazyFooterState.End -> {
                Icon(
                    imageVector = LiquidIcons.Check,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            LiquidLazyFooterState.Error -> {
                Icon(
                    imageVector = LiquidIcons.Warning,
                    contentDescription = null,
                    tint = colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        if (stateLabel != null) {
            Text(
                text = stateLabel,
                color = if (state == LiquidLazyFooterState.Error) {
                    colorScheme.error
                } else {
                    LiquidGlassTheme.colors.secondaryContent
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (state == LiquidLazyFooterState.Error && retryLabel != null && onRetry != null) {
            Text(
                text = retryLabel,
                color = colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
