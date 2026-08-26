package com.anto426.liquidmonet.components.pickers

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.anto426.liquidmonet.components.buttons.LiquidButton
import com.anto426.liquidmonet.components.buttons.LiquidButtonVariant
import com.anto426.liquidmonet.components.feedback.LiquidDialog
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop

/**
 * LiquidTimePickerDialog - Dedicated Optical Liquid Glass Time Picker Modal Dialog Component.
 */
@Composable
fun LiquidTimePickerDialog(
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    initialHour: Int = 12,
    initialMinute: Int = 0,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    if (!isOpen) return

    val state = rememberLiquidTimePickerState(initialHour, initialMinute)

    LiquidDialog(
        onDismissRequest = onDismissRequest,
        title = "Seleziona Ora",
        backdropState = backdropState,
        confirmButton = {
            LiquidButton(
                text = "Conferma",
                variant = LiquidButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    onTimeSelected(state.hour, state.minute)
                    onDismissRequest()
                }
            )
        },
        dismissButton = {
            LiquidButton(
                text = "Annulla",
                variant = LiquidButtonVariant.Secondary,
                modifier = Modifier.fillMaxWidth(),
                onClick = onDismissRequest
            )
        }
    ) {
        LiquidTimePicker(
            state = state,
            backdropState = backdropState
        )
    }
}
