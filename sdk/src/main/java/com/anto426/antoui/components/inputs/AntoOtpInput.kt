package com.anto426.antoui.components.inputs

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.RoundedRectangle

/**
 * AntoOtpInput - Radiant Optical Liquid Glass OTP / PIN Verification Cells Component.
 *
 * Features:
 * - Direct seamless touch focus across the entire cell strip via decorationBox.
 * - Solid text state synchronization with cursor pinning for smooth typing and backspace.
 * - Auto-completion callback ([onComplete]) triggered upon filling all slots.
 * - Saturated Monet accent glow and Snell refraction per cell.
 */
@Composable
fun AntoOtpInput(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 4,
    obscureText: Boolean = false,
    enabled: Boolean = true,
    cellWidth: Dp = 48.dp,
    cellHeight: Dp = 56.dp,
    spacing: Dp = 10.dp,
    onComplete: ((String) -> Unit)? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val colorScheme = MaterialTheme.colorScheme
    val otpHighlight = rememberAntoControlHighlight()

    var isFocused by remember { mutableStateOf(false) }

    // Synchronize external String with internal TextFieldValue maintaining cursor at end
    var textFieldValue by remember(otpValue) {
        mutableStateOf(
            TextFieldValue(
                text = otpValue,
                selection = TextRange(otpValue.length)
            )
        )
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            val filteredText = newValue.text.filter { it.isDigit() }.take(length)
            textFieldValue = newValue.copy(
                text = filteredText,
                selection = TextRange(filteredText.length)
            )
            if (filteredText != otpValue) {
                onOtpChange(filteredText)
                if (filteredText.length == length) {
                    onComplete?.invoke(filteredText)
                }
            }
        },
        modifier = modifier
            .graphicsLayer(antoControlLayerBlock(enabled, otpHighlight) ?: {})
            .antoControlPressFeedback(
                enabled = enabled,
                interactiveHighlight = otpHighlight,
                drawHighlightOverlay = false
            )
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                if (otpValue.length == length) {
                    onComplete?.invoke(otpValue)
                }
            }
        ),
        cursorBrush = SolidColor(Color.Transparent),
        enabled = enabled,
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until length) {
                    val rawChar = otpValue.getOrNull(i)?.toString() ?: ""
                    val displayChar = if (obscureText && rawChar.isNotEmpty()) "•" else rawChar
                    val isCurrent = isFocused && (otpValue.length == i || (i == length - 1 && otpValue.length == length))
                    val isFilled = i < otpValue.length

                    val borderColor by animateColorAsState(
                        targetValue = when {
                            isCurrent -> colorScheme.primary
                            isFilled -> colorScheme.primary.copy(alpha = 0.35f)
                            else -> Color.White.copy(alpha = 0.12f)
                        },
                        animationSpec = tween(200),
                        label = "otpBorder_$i"
                    )

                    val containerColor = when {
                        isCurrent -> colorScheme.primary.copy(alpha = 0.18f)
                        isFilled -> colorScheme.primary.copy(alpha = 0.08f)
                        else -> null
                    }

                    Box(
                        modifier = Modifier
                            .size(width = cellWidth, height = cellHeight)
                            .antoLiquidGlass(
                                backdrop = backdropState,
                                shape = RoundedRectangle(16.dp),
                                role = AntoGlassRole.Control,
                                containerColor = containerColor
                            )
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = borderColor,
                                shape = RoundedRectangle(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayChar,
                            style = TextStyle(
                                color = if (isFilled) colorScheme.primary else colorScheme.onSurface,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
    )
}
