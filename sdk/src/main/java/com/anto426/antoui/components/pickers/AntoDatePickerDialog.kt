package com.anto426.antoui.components.pickers

import androidx.compose.runtime.Composable
import com.anto426.antoui.components.feedback.AntoDialog
import com.anto426.antoui.components.feedback.AntoDialogActionButton
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import java.util.Calendar

/**
 * AntoDatePickerDialog - Dedicated Optical Liquid Glass Modal Calendar Dialog Component.
 */
@Composable
fun AntoDatePickerDialog(
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    onDateSelected: (Calendar) -> Unit,
    initialDate: Calendar = Calendar.getInstance(),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    if (!isOpen) return

    val state = rememberAntoDatePickerState(initialDate)

    AntoDialog(
        onDismissRequest = onDismissRequest,
        title = "Seleziona Data",
        backdropState = backdropState,
        confirmButton = {
            AntoDialogActionButton(
                text = "Conferma",
                isPrimary = true,
                onClick = {
                    state.selectedDate?.let { onDateSelected(it) }
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
        AntoDatePicker(
            state = state,
            backdropState = backdropState
        )
    }
}
