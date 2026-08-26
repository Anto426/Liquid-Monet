package com.anto426.liquidmonet.components.pickers

import androidx.compose.runtime.Composable
import com.anto426.liquidmonet.components.feedback.LiquidDialog
import com.anto426.liquidmonet.components.feedback.LiquidDialogActionButton
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
            LiquidDialogActionButton(
                text = "Conferma",
                isPrimary = true,
                onClick = {
                    onTimeSelected(state.hour, state.minute)
                    onDismissRequest()
                }
            )
        },
        dismissButton = {
            LiquidDialogActionButton(
                text = "Annulla",
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
