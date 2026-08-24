package com.anto426.antoui.components.inputs

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop

/**
 * AntoNumberTextField - Dedicated Liquid Glass Number Input Component.
 */
@Composable
fun AntoNumberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = "Valore Numerico",
    placeholder: String = label ?: "0.00",
    enabled: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    AntoTextField(
        value = value,
        onValueChange = { input ->
            if (input.isEmpty() || input.all { it.isDigit() || it == '.' || it == ',' }) {
                onValueChange(input)
            }
        },
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        keyboardActions = keyboardActions,
        backdropState = backdropState
    )
}
