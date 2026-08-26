package com.anto426.liquidmonet.components.inputs

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.anto426.liquidmonet.icons.LiquidIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop

/**
 * LiquidPasswordTextField - Declared First-Class Liquid Glass Password Input.
 * Features lock icon, dot masking, and show/hide password toggle.
 */
@Composable
fun LiquidPasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = "Password",
    placeholder: String = label ?: "Inserisci password...",
    enabled: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    LiquidTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        leadingIcon = LiquidIcons.Lock,
        trailingIcon = if (isPasswordVisible) LiquidIcons.VisibilityOff else LiquidIcons.Visibility,
        onTrailingIconClick = { isPasswordVisible = !isPasswordVisible },
        enabled = enabled,
        singleLine = true,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        keyboardActions = keyboardActions,
        backdropState = backdropState
    )
}
