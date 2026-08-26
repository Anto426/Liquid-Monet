package com.anto426.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.components.cards.LiquidCard
import com.anto426.liquidmonet.components.selection.LiquidCheckbox
import com.anto426.liquidmonet.components.pickers.LiquidColorPicker
import com.anto426.liquidmonet.components.pickers.LiquidDatePickerDialog
import com.anto426.liquidmonet.components.pickers.LiquidDatePickerField
import com.anto426.liquidmonet.components.selection.LiquidSelect
import com.anto426.liquidmonet.components.display.LiquidHorizontalDivider
import com.anto426.liquidmonet.components.inputs.LiquidOtpInput
import com.anto426.liquidmonet.components.selection.LiquidRadioButton
import com.anto426.liquidmonet.components.selection.LiquidRangeSlider
import com.anto426.liquidmonet.components.inputs.LiquidSearchBar
import com.anto426.liquidmonet.components.selection.LiquidSlider
import com.anto426.liquidmonet.components.selection.LiquidStepper
import com.anto426.liquidmonet.components.selection.LiquidSwitch
import com.anto426.liquidmonet.components.inputs.LiquidTextField
import com.anto426.liquidmonet.components.inputs.LiquidTextFieldType
import com.anto426.liquidmonet.components.pickers.LiquidTimePickerDialog
import com.anto426.liquidmonet.components.pickers.LiquidTimePickerField
import com.anto426.liquidmonet.icons.LiquidIcons
import com.kyant.backdrop.Backdrop
import java.util.Calendar

@Composable
fun InputsScreen(
    backdropState: Backdrop,
    modifier: Modifier = Modifier
) {
    var searchVal by remember { mutableStateOf("") }
    var textVal by remember { mutableStateOf("Design Liquid Glass") }
    var passwordVal by remember { mutableStateOf("Segreto123!") }
    var emailVal by remember { mutableStateOf("anto@liquidglass.io") }
    var phoneVal by remember { mutableStateOf("+39 345 123 4567") }
    var numberVal by remember { mutableStateOf("42.50") }
    var notesVal by remember { mutableStateOf("Interfaccia realizzata con ottica rifrattiva Snell AGSL e design system unificato.") }

    var stepperVal by remember { mutableIntStateOf(4) }
    var otpCode by remember { mutableStateOf("4268") }
    var selectedDropdownOption by remember { mutableStateOf("Opzione Zaffiro") }

    // Date & Time pickers
    var isDatePickerOpen by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Calendar?>(Calendar.getInstance()) }
    var isTimePickerOpen by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableIntStateOf(14) }
    var selectedMinute by remember { mutableIntStateOf(30) }

    // Selection & Sliders
    var switchVal by remember { mutableStateOf(true) }
    var checkboxVal by remember { mutableStateOf(true) }
    var radioVal by remember { mutableIntStateOf(0) }
    var sliderVal by remember { mutableStateOf(0.60f) }
    var rangeSliderVal by remember { mutableStateOf(0.20f..0.80f) }
    var colorPickerVal by remember { mutableStateOf(Color(0xFF2979FF)) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Barra di Ricerca
        SectionTitle("Barra di Ricerca in Vetro")
        LiquidSearchBar(
            query = searchVal,
            onQueryChange = { searchVal = it },
            backdropState = backdropState
        )

        // Campi Data & Ora
        SectionTitle("Selettori Data & Ora Liquid Glass")
        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LiquidDatePickerField(
                        selectedDate = selectedDate,
                        onClick = { isDatePickerOpen = true },
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                    LiquidTimePickerField(
                        selectedHour = selectedHour,
                        selectedMinute = selectedMinute,
                        onClick = { isTimePickerOpen = true },
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Campi di Testo & Password
        SectionTitle("Campi di Testo, Password & Specializzati")
        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LiquidTextField(
                    value = textVal,
                    onValueChange = { textVal = it },
                    label = "Nome Utente",
                    leadingIcon = LiquidIcons.AccountCircle,
                    backdropState = backdropState
                )

                LiquidTextField(
                    value = passwordVal,
                    onValueChange = { passwordVal = it },
                    type = LiquidTextFieldType.Password,
                    label = "Password Sicura",
                    backdropState = backdropState
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LiquidTextField(
                        value = emailVal,
                        onValueChange = { emailVal = it },
                        type = LiquidTextFieldType.Email,
                        label = "Email",
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                    LiquidTextField(
                        value = phoneVal,
                        onValueChange = { phoneVal = it },
                        type = LiquidTextFieldType.Phone,
                        label = "Telefono",
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                }

                LiquidTextField(
                    value = numberVal,
                    onValueChange = { numberVal = it },
                    type = LiquidTextFieldType.Number,
                    label = "Importo Numerico",
                    backdropState = backdropState
                )

                LiquidSelect(
                    items = listOf("Opzione Zaffiro", "Opzione Smeraldo", "Opzione Tramonto", "Opzione Violetto"),
                    selectedItem = selectedDropdownOption,
                    onItemSelected = { selectedDropdownOption = it },
                    label = "Tema di Sistema",
                    leadingIcon = LiquidIcons.Star,
                    backdropState = backdropState
                )

                LiquidTextField(
                    value = notesVal,
                    onValueChange = { notesVal = it },
                    type = LiquidTextFieldType.TextArea,
                    placeholder = "Inserisci note o descrizioni...",
                    maxLength = 200,
                    backdropState = backdropState
                )
            }
        }

        // Stepper & OTP Code
        SectionTitle("Contatori Numerici & Codice OTP")
        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                LiquidStepper(
                    value = stepperVal,
                    onValueChange = { stepperVal = it },
                    label = "Quantità Elementi",
                    unit = "pz",
                    minValue = 1,
                    maxValue = 20,
                    backdropState = backdropState
                )

                LiquidHorizontalDivider()

                Text("Codice di Verifica OTP (4 Cifre)", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.70f))
                LiquidOtpInput(
                    otpValue = otpCode,
                    onOtpChange = { otpCode = it },
                    length = 4,
                    backdropState = backdropState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Controlli Booleani & Selezione
        SectionTitle("Interruttori, Checkbox & Radio")
        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Interruttore Liquid Switch", color = Color.White)
                    LiquidSwitch(
                        checked = switchVal,
                        onCheckedChange = { switchVal = it },
                        backdropState = backdropState
                    )
                }

                LiquidHorizontalDivider()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sincronizzazione Automatica", color = Color.White)
                    LiquidCheckbox(
                        checked = checkboxVal,
                        onCheckedChange = { checkboxVal = it },
                        backdropState = backdropState
                    )
                }

                LiquidHorizontalDivider()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Opzione Radio 1", color = Color.White)
                    LiquidRadioButton(
                        selected = radioVal == 0,
                        onClick = { radioVal = 0 },
                        backdropState = backdropState
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Opzione Radio 2", color = Color.White)
                    LiquidRadioButton(
                        selected = radioVal == 1,
                        onClick = { radioVal = 1 },
                        backdropState = backdropState
                    )
                }
            }
        }

        // Sliders & Color Picker
        SectionTitle("Slider, Range Slider & Selettore Colore")
        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Slider Singolo: ${(sliderVal * 100).toInt()}%",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                LiquidSlider(
                    value = sliderVal,
                    onValueChange = { sliderVal = it },
                    valueRange = 0f..1f,
                    backdropState = backdropState
                )

                LiquidHorizontalDivider()

                Text(
                    text = "Range Slider: ${(rangeSliderVal.start * 100).toInt()}€ - ${(rangeSliderVal.endInclusive * 100).toInt()}€",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                LiquidRangeSlider(
                    value = rangeSliderVal,
                    onValueChange = { rangeSliderVal = it },
                    backdropState = backdropState
                )
            }
        }

        LiquidColorPicker(
            selectedColor = colorPickerVal,
            onColorSelected = { colorPickerVal = it },
            backdropState = backdropState
        )
    }

    // Modal Pickers Dialogs
    LiquidDatePickerDialog(
        isOpen = isDatePickerOpen,
        onDismissRequest = { isDatePickerOpen = false },
        onDateSelected = { selectedDate = it },
        initialDate = selectedDate ?: Calendar.getInstance(),
        backdropState = backdropState
    )

    LiquidTimePickerDialog(
        isOpen = isTimePickerOpen,
        onDismissRequest = { isTimePickerOpen = false },
        onTimeSelected = { h, m ->
            selectedHour = h
            selectedMinute = m
        },
        initialHour = selectedHour,
        initialMinute = selectedMinute,
        backdropState = backdropState
    )
}
