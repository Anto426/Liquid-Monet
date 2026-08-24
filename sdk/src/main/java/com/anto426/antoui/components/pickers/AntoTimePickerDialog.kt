package com.anto426.antoui.components.pickers

import androidx.compose.runtime.Composable
import com.anto426.antoui.components.feedback.AntoDialog
import com.anto426.antoui.components.feedback.AntoDialogActionButton
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop

/**
 * AntoTimePickerDialog - Dedicated Optical Liquid Glass Time Picker Modal Dialog Component.
 */
@Composable
fun AntoTimePickerDialog(
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    initialHour: Int = 12,
    initialMinute: Int = 0,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    if (!isOpen) return

    val state = rememberAntoTimePickerState(initialHour, initialMinute)

    AntoDialog(
        onDismissRequest = onDismissRequest,
        title = "Seleziona Ora",
        backdropState = backdropState,
        confirmButton = {
            AntoDialogActionButton(
                text = "Conferma",
                isPrimary = true,
                onClick = {
                    onTimeSelected(state.hour, state.minute)
                    onDismissRequest()
                }
            )
        },
        dismissButton = {
            AntoDialogActionButton(
                text = "Annulla",
                onClick = onDismissRequest
            )
        }
    ) {
        AntoTimePicker(
            state = state,
            backdropState = backdropState
        )
    }
}
