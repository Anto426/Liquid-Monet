package com.anto426.app.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
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
import com.anto426.liquidmonet.components.buttons.LiquidButton
import com.anto426.liquidmonet.components.buttons.LiquidButtonVariant
import com.anto426.liquidmonet.components.cards.LiquidCard
import com.anto426.liquidmonet.components.selection.LiquidCheckbox
import com.anto426.liquidmonet.components.pickers.LiquidColorPicker
import com.anto426.liquidmonet.components.pickers.LiquidDatePickerDialog
import com.anto426.liquidmonet.components.pickers.LiquidDatePickerField
import com.anto426.liquidmonet.components.selection.LiquidSelect
import com.anto426.liquidmonet.components.buttons.LiquidFloatingActionButton
import com.anto426.liquidmonet.components.display.LiquidHorizontalDivider
import com.anto426.liquidmonet.components.display.LiquidSectionHeader
import com.anto426.liquidmonet.components.buttons.LiquidIconButton
import com.anto426.liquidmonet.components.navigation.LiquidNavigationItem
import com.anto426.liquidmonet.components.navigation.LiquidTabBar
import com.anto426.liquidmonet.components.inputs.LiquidOtpInput
import com.anto426.liquidmonet.components.selection.LiquidRadioButton
import com.anto426.liquidmonet.components.selection.LiquidRangeSlider
import com.anto426.liquidmonet.components.selection.LiquidRatingBar
import com.anto426.liquidmonet.components.inputs.LiquidSearchBar
import com.anto426.liquidmonet.motion.LiquidAnimatedNavContent
import com.anto426.liquidmonet.motion.LiquidNavTransition
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
fun ControlsInputHubScreen(
    backdropState: Backdrop,
    modifier: Modifier = Modifier
) {
    var currentSubTab by remember { mutableIntStateOf(0) }
    val subTabs = listOf(
        LiquidNavigationItem("Pulsanti"),
        LiquidNavigationItem("Form"),
        LiquidNavigationItem("Data & Colore"),
        LiquidNavigationItem("Slider & Toggle")
    )

    // Form states
    var searchVal by remember { mutableStateOf("") }
    var textVal by remember { mutableStateOf("Design Liquid Glass") }
    var passwordVal by remember { mutableStateOf("Segreto123!") }
    var emailVal by remember { mutableStateOf("anto@liquidglass.io") }
    var phoneVal by remember { mutableStateOf("+39 345 123 4567") }
    var numberVal by remember { mutableStateOf("42.50") }
    var notesVal by remember { mutableStateOf("Interfaccia realizzata con ottica rifrattiva Snell AGSL e design system unificato.") }
    var selectedDropdownOption by remember { mutableStateOf("Opzione Zaffiro") }

    // Stepper & OTP
    var stepperVal by remember { mutableIntStateOf(4) }
    var otpCode by remember { mutableStateOf("4268") }
    var starRating by remember { mutableIntStateOf(4) }

    // Date & Time pickers
    var isDatePickerOpen by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Calendar?>(Calendar.getInstance()) }
    var isTimePickerOpen by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableIntStateOf(14) }
    var selectedMinute by remember { mutableIntStateOf(30) }

    // Toggles & Sliders
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
        // Sub-Navigation Liquid Tabs
        LiquidTabBar(
            items = subTabs,
            selectedIndex = currentSubTab,
            onTabSelected = { currentSubTab = it },
            backdropState = backdropState
        )

        LiquidAnimatedNavContent(
            targetState = currentSubTab,
            transition = LiquidNavTransition.AutoDirectional,
            label = "controlsSubTabTransition"
        ) { tab ->
            when (tab) {
                0 -> {
                    // Sotto-Schermata 1: Pulsanti & Azioni
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        LiquidSectionHeader(
                            title = "Matrice Pulsanti in Vetro Liquido",
                            subtitle = "Confronta gerarchie e varianti senza rinunciare alla risposta elastica."
                        )
                        LiquidCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Varianti Vetro Liquido", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.70f))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    LiquidButton(text = "Primary", onClick = { }, variant = LiquidButtonVariant.Primary, backdropState = backdropState, modifier = Modifier.weight(1f))
                                    LiquidButton(text = "Secondary", onClick = { }, variant = LiquidButtonVariant.Secondary, backdropState = backdropState, modifier = Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    LiquidButton(text = "Tonal", onClick = { }, variant = LiquidButtonVariant.Tonal, backdropState = backdropState, modifier = Modifier.weight(1f))
                                    LiquidButton(text = "Glass Pure", onClick = { }, variant = LiquidButtonVariant.Glass, backdropState = backdropState, modifier = Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    LiquidButton(text = "Outlined", onClick = { }, variant = LiquidButtonVariant.Outlined, backdropState = backdropState, modifier = Modifier.weight(1f))
                                    LiquidButton(text = "Text", onClick = { }, variant = LiquidButtonVariant.Text, backdropState = backdropState, modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        LiquidSectionHeader(
                            title = "Pulsanti Icona & Azione Flottante (FAB)",
                            subtitle = "Azioni rapide, compatte e accessibili anche senza etichetta visibile."
                        )
                        LiquidCard(backdropState = backdropState) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LiquidIconButton(icon = LiquidIcons.PlayArrow, onClick = { }, size = 46.dp, iconSize = 22.dp, backdropState = backdropState)
                                LiquidIconButton(icon = LiquidIcons.Star, onClick = { }, size = 46.dp, iconSize = 22.dp, backdropState = backdropState)
                                LiquidIconButton(icon = LiquidIcons.Share, onClick = { }, size = 46.dp, iconSize = 22.dp, backdropState = backdropState)
                                LiquidIconButton(icon = LiquidIcons.Settings, onClick = { }, size = 46.dp, iconSize = 22.dp, backdropState = backdropState)
                                LiquidFloatingActionButton(onClick = { }, size = 46.dp, backdropState = backdropState) {
                                    Icon(imageVector = LiquidIcons.Add, contentDescription = "Nuovo", tint = Color.White, modifier = Modifier.size(22.dp))
                                }
                            }
                        }

                        LiquidSectionHeader(
                            title = "Valutazione a Stelle",
                            subtitle = "Seleziona un punteggio con feedback visivo e aptico."
                        )
                        LiquidCard(backdropState = backdropState) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                LiquidRatingBar(rating = starRating, onRatingChanged = { starRating = it }, backdropState = backdropState)
                                Text(text = "$starRating stelle su 5", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.70f))
                            }
                        }
                    }
                }

                1 -> {
                    // Sotto-Schermata 2: Campi & Form
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        LiquidSectionHeader(
                            title = "Barra di Ricerca",
                            subtitle = "Filtra i contenuti con un campo liquido dedicato alla ricerca."
                        )
                        LiquidSearchBar(query = searchVal, onQueryChange = { searchVal = it }, backdropState = backdropState)

                        LiquidSectionHeader(
                            title = "Campi di Testo & Specializzati",
                            subtitle = "Input testuali, password e formattazioni specifiche in un'unica famiglia."
                        )
                        LiquidCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                LiquidTextField(
                                    value = textVal,
                                    onValueChange = { textVal = it },
                                    label = "Nome Completo",
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

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    LiquidTextField(value = emailVal, onValueChange = { emailVal = it }, type = LiquidTextFieldType.Email, label = "Email", backdropState = backdropState, modifier = Modifier.weight(1f))
                                    LiquidTextField(value = phoneVal, onValueChange = { phoneVal = it }, type = LiquidTextFieldType.Phone, label = "Telefono", backdropState = backdropState, modifier = Modifier.weight(1f))
                                }

                                LiquidTextField(value = numberVal, onValueChange = { numberVal = it }, type = LiquidTextFieldType.Number, label = "Importo Numerico", backdropState = backdropState)

                                LiquidTextField(value = notesVal, onValueChange = { notesVal = it }, type = LiquidTextFieldType.TextArea, placeholder = "Inserisci note o descrizioni...", maxLength = 200, backdropState = backdropState)
                            }
                        }
                    }
                }

                2 -> {
                    // Sotto-Schermata 3: Data, Ora & Colore
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        LiquidSectionHeader(
                            title = "Selettori di Data & Ora",
                            subtitle = "Scegli valori temporali e opzioni da controlli coerenti con il tema."
                        )
                        LiquidCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    LiquidDatePickerField(selectedDate = selectedDate, onClick = { isDatePickerOpen = true }, backdropState = backdropState, modifier = Modifier.weight(1f))
                                    LiquidTimePickerField(selectedHour = selectedHour, selectedMinute = selectedMinute, onClick = { isTimePickerOpen = true }, backdropState = backdropState, modifier = Modifier.weight(1f))
                                }
                                LiquidSelect(
                                    items = listOf("Opzione Zaffiro", "Opzione Smeraldo", "Opzione Tramonto", "Opzione Violetto"),
                                    selectedItem = selectedDropdownOption,
                                    onItemSelected = { selectedDropdownOption = it },
                                    label = "Tema Dropdown",
                                    leadingIcon = LiquidIcons.Star,
                                    backdropState = backdropState
                                )
                            }
                        }

                        LiquidSectionHeader(
                            title = "Selettore Spettro Cromatico",
                            subtitle = "Esplora lo spettro e restituisci il colore scelto in tempo reale."
                        )
                        LiquidColorPicker(selectedColor = colorPickerVal, onColorSelected = { colorPickerVal = it }, backdropState = backdropState)
                    }
                }

                else -> {
                    // Sotto-Schermata 4: Slider & Toggle
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        LiquidSectionHeader(
                            title = "Contatori Numerici & Codice OTP",
                            subtitle = "Inserimenti brevi con vincoli, avanzamento e stato sempre leggibili."
                        )
                        LiquidCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                LiquidStepper(value = stepperVal, onValueChange = { stepperVal = it }, label = "Quantità Elementi", unit = "pz", minValue = 1, maxValue = 20, backdropState = backdropState)
                                LiquidHorizontalDivider()
                                Text("Codice di Verifica OTP (4 Cifre)", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.70f))
                                LiquidOtpInput(otpValue = otpCode, onOtpChange = { otpCode = it }, length = 4, backdropState = backdropState, modifier = Modifier.fillMaxWidth())
                            }
                        }

                        LiquidSectionHeader(
                            title = "Controlli Booleani",
                            subtitle = "Switch, checkbox e radio per stati esclusivi o indipendenti."
                        )
                        LiquidCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Interruttore Liquid Switch", color = Color.White)
                                    LiquidSwitch(checked = switchVal, onCheckedChange = { switchVal = it }, backdropState = backdropState)
                                }
                                LiquidHorizontalDivider()
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Checkbox Sincronizzazione", color = Color.White)
                                    LiquidCheckbox(checked = checkboxVal, onCheckedChange = { checkboxVal = it }, backdropState = backdropState)
                                }
                                LiquidHorizontalDivider()
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Opzione Radio 1", color = Color.White)
                                    LiquidRadioButton(selected = radioVal == 0, onClick = { radioVal = 0 }, backdropState = backdropState)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Opzione Radio 2", color = Color.White)
                                    LiquidRadioButton(selected = radioVal == 1, onClick = { radioVal = 1 }, backdropState = backdropState)
                                }
                            }
                        }

                        LiquidSectionHeader(
                            title = "Slider & Range Slider",
                            subtitle = "Regola un valore o un intervallo seguendo la traccia colorata."
                        )
                        LiquidCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text("Slider Singolo: ${(sliderVal * 100).toInt()}%", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                LiquidSlider(value = sliderVal, onValueChange = { sliderVal = it }, valueRange = 0f..1f, backdropState = backdropState)
                                LiquidHorizontalDivider()
                                Text("Range Slider: ${(rangeSliderVal.start * 100).toInt()}€ - ${(rangeSliderVal.endInclusive * 100).toInt()}€", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                LiquidRangeSlider(value = rangeSliderVal, onValueChange = { rangeSliderVal = it }, backdropState = backdropState)
                            }
                        }
                    }
                }
            }
        }
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
