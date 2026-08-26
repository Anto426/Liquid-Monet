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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.components.internal.LiquidControlDefaults
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.anto426.liquidmonet.components.feedback.LiquidCircularProgressIndicator
import com.kyant.backdrop.catalog.components.LiquidGlassButton

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
    text: String? = null,
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
    content: @Composable RowScope.() -> Unit = {
        if (text != null) Text(text)
    }
) {
    val isInteractive = enabled && !isLoading
    val colorScheme = MaterialTheme.colorScheme
    val targetTint = when {
        tint.isSpecified -> tint.copy(alpha = tint.alpha.coerceAtMost(0.28f))
        variant == LiquidButtonVariant.Primary -> colorScheme.primary.copy(alpha = 0.22f)
        variant == LiquidButtonVariant.Secondary -> colorScheme.secondary.copy(alpha = 0.14f)
        variant == LiquidButtonVariant.Tonal -> colorScheme.tertiary.copy(alpha = 0.16f)
        else -> Color.Transparent
    }
    val targetContentColor = when (variant) {
        LiquidButtonVariant.Outlined,
        LiquidButtonVariant.Text -> colorScheme.primary
        LiquidButtonVariant.Primary,
        LiquidButtonVariant.Secondary,
        LiquidButtonVariant.Tonal,
        LiquidButtonVariant.Glass -> colorScheme.onSurface
    }.let { contentColor ->
        if (enabled) contentColor else contentColor.copy(alpha = LiquidControlDefaults.disabledContentAlpha)
    }

    val animatedContentColor by animateColorAsState(
        targetValue = targetContentColor,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "buttonContentColor"
    )

    val animatedTint by animateColorAsState(
        targetValue = targetTint,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "buttonTint"
    )

    val height: Dp = when (size) {
        LiquidButtonSize.Small -> 40.dp
        LiquidButtonSize.Medium -> 48.dp
        LiquidButtonSize.Large -> 56.dp
    }

    val contentPadding: PaddingValues = when (size) {
        LiquidButtonSize.Small -> PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        LiquidButtonSize.Medium -> PaddingValues(horizontal = 20.dp, vertical = 10.dp)
        LiquidButtonSize.Large -> PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    }

    val textStyle = when (size) {
        LiquidButtonSize.Small -> MaterialTheme.typography.labelMedium
        LiquidButtonSize.Medium -> MaterialTheme.typography.labelLarge
        LiquidButtonSize.Large -> MaterialTheme.typography.titleSmall
    }

    val border: BorderStroke? = if (variant == LiquidButtonVariant.Outlined) {
        BorderStroke(
            1.dp,
            colorScheme.primary.copy(alpha = if (enabled) 0.48f else 0.18f)
        )
    } else null

    LiquidGlassButton(
        onClick = onClick,
        backdrop = backdropState,
        modifier = modifier.defaultMinSize(minWidth = 64.dp),
        isInteractive = isInteractive,
        enabled = enabled,
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
