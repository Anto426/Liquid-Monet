package com.anto426.liquidmonet.components.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.components.internal.LiquidControlDefaults
import com.anto426.liquidmonet.components.internal.liquidButtonColors
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.anto426.liquidmonet.components.feedback.LiquidCircularProgressIndicator
import com.kyant.backdrop.catalog.components.LiquidGlassButton
import com.kyant.shapes.Capsule

/**
 * LiquidButton / LiquidButton - Single Unified Liquid Glass Button Component.
 *
 * Fully configurable through its styling parameters:
 * @param variant Visual style ([LiquidButtonVariant.Primary], [LiquidButtonVariant.Secondary],
 *                [LiquidButtonVariant.Tonal], [LiquidButtonVariant.Glass],
 *                [LiquidButtonVariant.Outlined], [LiquidButtonVariant.Text]).
 * @param size Button dimension scale ([LiquidButtonSize.Small], [LiquidButtonSize.Medium], [LiquidButtonSize.Large]).
 * @param enabled Whether the button is enabled and interactive.
 * @param isLoading When true, replaces content with a smooth progress indicator.
 * @param leadingIcon Optional leading icon composable.
 * @param trailingIcon Optional trailing icon composable.
 * @param shape Corner shape (defaults to Capsule).
 * @param tint Custom color tint override (harmonized with Monet by default).
 * @param backdropState Optical glass backdrop reference.
 */
@Composable
fun LiquidButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    variant: LiquidButtonVariant = LiquidButtonVariant.Primary,
    size: LiquidButtonSize = LiquidButtonSize.Medium,
    shape: Shape = LiquidControlDefaults.shape,
    tint: Color = Color.Unspecified,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    val isInteractive = enabled && !isLoading
    val colors = liquidButtonColors(variant, tint, isInteractive)

    val animatedContentColor by animateColorAsState(
        targetValue = colors.content,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "buttonContentColor"
    )

    val animatedTint by animateColorAsState(
        targetValue = colors.tint,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "buttonTint"
    )

    val height: Dp = when (size) {
        LiquidButtonSize.Small -> 36.dp
        LiquidButtonSize.Medium -> 48.dp
        LiquidButtonSize.Large -> 56.dp
    }

    val contentPadding: PaddingValues = when (size) {
        LiquidButtonSize.Small -> PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        LiquidButtonSize.Medium -> PaddingValues(horizontal = 18.dp, vertical = 10.dp)
        LiquidButtonSize.Large -> PaddingValues(horizontal = 24.dp, vertical = 14.dp)
    }

    val textStyle = when (size) {
        LiquidButtonSize.Small -> MaterialTheme.typography.labelMedium
        LiquidButtonSize.Medium -> MaterialTheme.typography.labelLarge
        LiquidButtonSize.Large -> MaterialTheme.typography.titleSmall
    }

    val border: BorderStroke? = if (variant == LiquidButtonVariant.Outlined) {
        BorderStroke(
            1.dp,
            animatedContentColor.copy(alpha = if (enabled) 0.35f else 0.15f)
        )
    } else null

    LiquidGlassButton(
        onClick = onClick,
        backdrop = backdropState,
        modifier = modifier,
        isInteractive = isInteractive,
        shape = shape,
        height = height,
        contentPadding = contentPadding,
        border = border,
        tint = animatedTint
    ) {
        CompositionLocalProvider(LocalContentColor provides animatedContentColor) {
            ProvideTextStyle(value = textStyle) {
                AnimatedContent(
                    targetState = isLoading,
                    transitionSpec = {
                        (scaleIn(spring(dampingRatio = 0.72f, stiffness = 420f), initialScale = 0.65f) + fadeIn(tween(180)))
                            .togetherWith(scaleOut(spring(dampingRatio = 0.85f, stiffness = 480f), targetScale = 0.65f) + fadeOut(tween(140)))
                    },
                    label = "buttonLoadingTransition"
                ) { loading ->
                    if (loading) {
                        val spinnerSize = if (size == LiquidButtonSize.Small) 16.dp else 20.dp
                        LiquidCircularProgressIndicator(
                            modifier = Modifier.size(spinnerSize),
                            indicatorSize = spinnerSize,
                            strokeWidth = if (size == LiquidButtonSize.Small) 2.2.dp else 2.8.dp,
                            progressColor = animatedContentColor,
                            progress = null
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (leadingIcon != null) {
                                leadingIcon()
                            }
                            content()
                            if (trailingIcon != null) {
                                trailingIcon()
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Text-based LiquidButton overload.
 */
@Composable
fun LiquidButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    variant: LiquidButtonVariant = LiquidButtonVariant.Primary,
    size: LiquidButtonSize = LiquidButtonSize.Medium,
    shape: Shape = LiquidControlDefaults.shape,
    tint: Color = Color.Unspecified,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    LiquidButton(
        onClick = onClick,
        modifier = modifier,
        backdrop = backdrop,
        backdropState = backdropState,
        enabled = enabled,
        isLoading = isLoading,
        variant = variant,
        size = size,
        shape = shape,
        tint = tint,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
    ) {
        Text(text = text)
    }
}
