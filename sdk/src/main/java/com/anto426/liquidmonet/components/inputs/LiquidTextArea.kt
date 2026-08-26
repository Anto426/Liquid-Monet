package com.anto426.liquidmonet.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.RoundedRectangle

/**
 * LiquidTextArea - Multi-line Optical Liquid Glass Text Area for notes, comments, and descriptions.
 */
@Composable
fun LiquidTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Scrivi qui...",
    minHeight: Dp = 110.dp,
    maxLines: Int = 8,
    maxLength: Int? = null,
    enabled: Boolean = true,
    shape: Shape = RoundedRectangle(20.dp),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val colorScheme = MaterialTheme.colorScheme
    val contentColor = colorScheme.onSurface
    val primaryColor = colorScheme.primary
    val placeholderColor = contentColor.copy(alpha = 0.45f)

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isFocused) primaryColor.copy(alpha = 0.55f) else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "textAreaBorderColor"
    )

    val effectiveBackdrop = if (backdropState != emptyBackdrop()) backdropState else backdrop
    val inputHighlight = rememberLiquidControlHighlight()

    BasicTextField(
        value = value,
        onValueChange = {
            if (maxLength == null || it.length <= maxLength) {
                onValueChange(it)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .graphicsLayer(liquidControlLayerBlock(enabled, inputHighlight) ?: {})
            .liquidControlPressFeedback(
                enabled = enabled,
                interactiveHighlight = inputHighlight,
                drawHighlightOverlay = false
            )
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = LiquidGlassRole.Control
            )
            .border(width = 1.2.dp, color = animatedBorderColor, shape = shape),
        enabled = enabled,
        textStyle = TextStyle(
            color = contentColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal
        ),
        cursorBrush = SolidColor(primaryColor),
        singleLine = false,
        maxLines = maxLines,
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                color = placeholderColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                    innerTextField()
                }

                if (maxLength != null) {
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "${value.length}/$maxLength",
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.50f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    )
}
