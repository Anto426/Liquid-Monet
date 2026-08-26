package com.anto426.liquidmonet.components.pickers

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.anto426.liquidmonet.components.buttons.LiquidButton
import com.anto426.liquidmonet.components.buttons.LiquidButtonVariant
import com.anto426.liquidmonet.components.feedback.LiquidDialog
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
            LiquidButton(
                text = "Conferma",
                variant = LiquidButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    state.selectedDate?.let { onDateSelected(it) }
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
        LiquidDatePicker(
            state = state,
            backdropState = backdropState
        )
    }
}
