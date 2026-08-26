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
import com.anto426.liquidmonet.theme.monet.blend
import com.kyant.backdrop.catalog.utils.InteractiveHighlight
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
    val automaticTint = when (variant) {
        LiquidButtonVariant.Primary -> colorScheme.primary.copy(alpha = 0.36f)
        LiquidButtonVariant.Secondary -> colorScheme.secondary.copy(alpha = 0.24f)
        LiquidButtonVariant.Tonal -> colorScheme.tertiary.copy(alpha = 0.24f)
        LiquidButtonVariant.Outlined -> colorScheme.primary.copy(alpha = 0.10f)
        LiquidButtonVariant.Glass, LiquidButtonVariant.Text -> Color.Unspecified
    }
    val automaticContent = when (variant) {
        LiquidButtonVariant.Primary -> Color.White
        else -> colorScheme.onSurface
    }

    return LiquidControlColors(
        tint = if (tint.isSpecified) tint else automaticTint,
        content = if (enabled) {
            automaticContent
        } else {
            automaticContent.copy(alpha = LiquidControlDefaults.disabledContentAlpha)
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
    interactiveHighlight: InteractiveHighlight
): (GraphicsLayerScope.() -> Unit)? = if (enabled) {
    {
        val width = size.width.coerceAtLeast(1f)
        val height = size.height.coerceAtLeast(1f)
        val minDim = size.minDimension.coerceAtLeast(1f)
        val maxDim = size.maxDimension.coerceAtLeast(1f)

        val progress = interactiveHighlight.pressProgress
        val baseScale = 1f + (LiquidControlDefaults.pressedScale - 1f) * progress

        val initialDerivative = 0.07f
        val offset = interactiveHighlight.offset
        translationX = minDim * tanh(initialDerivative * offset.x / minDim)
        translationY = minDim * tanh(initialDerivative * offset.y / minDim)

        // Fluid non-linear elastic stretching in all directions (left, right, top, bottom, diagonal)
        val maxDragScale = 0.12f * progress
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
    drawHighlightOverlay: Boolean = true,
    highlightColor: Color = Color.Unspecified
): Modifier = if (enabled) {
    if (drawHighlightOverlay) {
        this
            .then(interactiveHighlight.modifier(highlightColor))
            .then(interactiveHighlight.gestureModifier)
    } else {
        this.then(interactiveHighlight.gestureModifier)
    }
} else {
    this
}
