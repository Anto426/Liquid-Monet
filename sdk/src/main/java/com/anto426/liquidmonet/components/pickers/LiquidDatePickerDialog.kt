package com.anto426.liquidmonet.components.pickers

import androidx.compose.runtime.Composable
import com.anto426.liquidmonet.components.feedback.LiquidDialog
import com.anto426.liquidmonet.components.feedback.LiquidDialogActionButton
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import java.util.Calendar

/**
 * LiquidDatePickerDialog - Dedicated Optical Liquid Glass Modal Calendar Dialog Component.
 */
@Composable
fun LiquidDatePickerDialog(
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    onDateSelected: (Calendar) -> Unit,
    initialDate: Calendar = Calendar.getInstance(),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    if (!isOpen) return

    val state = rememberLiquidDatePickerState(initialDate)

    LiquidDialog(
        onDismissRequest = onDismissRequest,
        title = "Seleziona Data",
        backdropState = backdropState,
        confirmButton = {
            LiquidDialogActionButton(
                text = "Conferma",
                isPrimary = true,
                onClick = {
                    state.selectedDate?.let { onDateSelected(it) }
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
        LiquidDatePicker(
            state = state,
            backdropState = backdropState
        )
    }
}
