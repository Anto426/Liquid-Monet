package com.anto426.liquidmonet.components.internal

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import com.anto426.liquidmonet.components.buttons.LiquidButtonVariant
import com.anto426.liquidmonet.theme.LiquidGlassTheme
import com.anto426.liquidmonet.theme.monet.blend
import com.kyant.shapes.Capsule
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tanh

/** Shared visual and interaction rules for compact Liquid Monet controls. */
internal object LiquidControlDefaults {
    val shape: Shape = Capsule()

    const val pressedScale: Float = 0.96f
    const val disabledContentAlpha: Float = 0.38f
    const val tintedContainerAlpha: Float = 0.06f
    const val inactiveContainerAlpha: Float = 0.08f
    const val accentContainerAlpha: Float = 0.22f
    const val selectedContainerAlpha: Float = 0.28f
    const val activeTrackAlpha: Float = 0.52f
    const val focusIndicatorAlpha: Float = 0.40f
}

@Immutable
internal data class LiquidControlColors(
    val tint: Color,
    val content: Color
)

@Composable
internal fun liquidButtonColors(
    variant: LiquidButtonVariant,
    tint: Color,
    enabled: Boolean
): LiquidControlColors {
    val colorScheme = MaterialTheme.colorScheme
    val glassColors = LiquidGlassTheme.colors
    val automaticTint = when (variant) {
        LiquidButtonVariant.Primary -> glassColors.accentContainer
        LiquidButtonVariant.Secondary -> colorScheme.secondary.copy(alpha = glassColors.accentContainer.alpha * 0.72f)
        LiquidButtonVariant.Tonal -> colorScheme.tertiary.copy(alpha = glassColors.accentContainer.alpha * 0.80f)
        LiquidButtonVariant.Outlined -> colorScheme.primary.copy(alpha = 0.10f)
        LiquidButtonVariant.Glass, LiquidButtonVariant.Text -> Color.Unspecified
    }
    // Every variant remains translucent, so an opaque Material `onPrimary` pairing would not
    // guarantee contrast against the sampled backdrop beneath the glass.
    val automaticContent = glassColors.content

    return LiquidControlColors(
        tint = if (tint.isSpecified) tint else automaticTint,
        content = if (enabled) {
            automaticContent
        } else {
            glassColors.disabledContent
        }
    )
}

@Composable
internal fun rememberLiquidControlHighlight(): InteractiveHighlight {
    val animationScope = rememberCoroutineScope()
    return remember(animationScope) {
        InteractiveHighlight(animationScope = animationScope)
    }
}

internal fun liquidControlLayerBlock(
    enabled: Boolean,
    interactiveHighlight: InteractiveHighlight,
    stretchFactor: Float = 1f,
    translationFactor: Float = 1f
): (GraphicsLayerScope.() -> Unit)? = if (enabled) {
    {
        val width = size.width.coerceAtLeast(1f)
        val height = size.height.coerceAtLeast(1f)
        val minDim = size.minDimension.coerceAtLeast(1f)
        val maxDim = size.maxDimension.coerceAtLeast(1f)

        val progress = interactiveHighlight.pressProgress
        val baseScale = 1f + (LiquidControlDefaults.pressedScale - 1f) * progress

        val initialDerivative = 0.07f * translationFactor
        val offset = interactiveHighlight.offset
        translationX = minDim * translationFactor * tanh(initialDerivative * offset.x / minDim)
        translationY = minDim * translationFactor * tanh(initialDerivative * offset.y / minDim)

        // Fluid non-linear elastic stretching in all directions (left, right, top, bottom, diagonal)
        val maxDragScale = 0.12f * progress * stretchFactor
        val offsetAngle = atan2(offset.y, offset.x)
        val aspectX = (width / height).coerceIn(0.5f, 2.0f)
        val aspectY = (height / width).coerceIn(0.5f, 2.0f)

        val horizontalStretch =
            maxDragScale * abs(cos(offsetAngle) * offset.x / maxDim) * aspectX
        val verticalStretch =
            maxDragScale * abs(sin(offsetAngle) * offset.y / maxDim) * aspectY

        scaleX = baseScale + horizontalStretch
        scaleY = baseScale + verticalStretch
    }
} else {
    null
}

internal fun Modifier.liquidControlPressFeedback(
    enabled: Boolean,
    interactiveHighlight: InteractiveHighlight,
    shape: Shape? = null,
    drawHighlightOverlay: Boolean = true,
    highlightColor: Color = Color.Unspecified
): Modifier = if (enabled) {
    val elevatedModifier = this.liquidInteractiveZIndex()
    if (drawHighlightOverlay) {
        elevatedModifier
            .then(interactiveHighlight.modifier(highlightColor, shape))
            .then(interactiveHighlight.gestureModifier)
    } else {
        elevatedModifier.then(interactiveHighlight.gestureModifier)
    }
} else {
    this
}
