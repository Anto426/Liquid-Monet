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
import com.anto426.antoui.components.buttons.AntoButton
import com.anto426.antoui.components.buttons.AntoButtonVariant
import com.anto426.antoui.components.cards.AntoCard
import com.anto426.antoui.components.selection.AntoCheckbox
import com.anto426.antoui.components.pickers.AntoColorPicker
import com.anto426.antoui.components.pickers.AntoDatePickerDialog
import com.anto426.antoui.components.pickers.AntoDatePickerField
import com.anto426.antoui.components.selection.AntoDropdownSelect
import com.anto426.antoui.components.inputs.AntoEmailTextField
import com.anto426.antoui.components.buttons.AntoFloatingActionButton
import com.anto426.antoui.components.display.AntoHorizontalDivider
import com.anto426.antoui.components.buttons.AntoIconButton
import com.anto426.antoui.components.navigation.AntoLiquidTabRow
import com.anto426.antoui.components.inputs.AntoNumberTextField
import com.anto426.antoui.components.inputs.AntoOtpInput
import com.anto426.antoui.components.inputs.AntoPasswordTextField
import com.anto426.antoui.components.inputs.AntoPhoneTextField
import com.anto426.antoui.components.selection.AntoRadioButton
import com.anto426.antoui.components.selection.AntoRangeSlider
import com.anto426.antoui.components.selection.AntoRatingBar
import com.anto426.antoui.components.inputs.AntoSearchBar
import com.anto426.antoui.motion.AntoAnimatedNavContent
import com.anto426.antoui.motion.AntoNavTransition
import com.anto426.antoui.components.selection.AntoSlider
import com.anto426.antoui.components.selection.AntoStepper
import com.anto426.antoui.components.selection.AntoSwitch
import com.anto426.antoui.components.inputs.AntoTextArea
import com.anto426.antoui.components.inputs.AntoTextField
import com.anto426.antoui.components.pickers.AntoTimePickerDialog
import com.anto426.antoui.components.pickers.AntoTimePickerField
import com.anto426.antoui.icons.AntoIcons
import com.kyant.backdrop.Backdrop
import java.util.Calendar

@Composable
fun ControlsInputHubScreen(
    backdropState: Backdrop,
    modifier: Modifier = Modifier
) {
    var currentSubTab by remember { mutableIntStateOf(0) }
    val subTabs = listOf("Pulsanti", "Form", "Data & Colore", "Slider & Toggle")

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
        AntoLiquidTabRow(
            items = subTabs,
            selectedIndex = currentSubTab,
            onTabSelected = { currentSubTab = it },
            backdropState = backdropState
        )

        AntoAnimatedNavContent(
            targetState = currentSubTab,
            transition = AntoNavTransition.AutoDirectional,
            label = "controlsSubTabTransition"
        ) { tab ->
            when (tab) {
                0 -> {
                    // Sotto-Schermata 1: Pulsanti & Azioni
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Matrice Pulsanti in Vetro Liquido")
                        AntoCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Varianti Vetro Liquido", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.70f))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    AntoButton(text = "Primary", onClick = { }, variant = AntoButtonVariant.Primary, backdropState = backdropState, modifier = Modifier.weight(1f))
                                    AntoButton(text = "Secondary", onClick = { }, variant = AntoButtonVariant.Secondary, backdropState = backdropState, modifier = Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    AntoButton(text = "Tonal", onClick = { }, variant = AntoButtonVariant.Tonal, backdropState = backdropState, modifier = Modifier.weight(1f))
                                    AntoButton(text = "Glass Pure", onClick = { }, variant = AntoButtonVariant.Glass, backdropState = backdropState, modifier = Modifier.weight(1f))
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    AntoButton(text = "Outlined", onClick = { }, variant = AntoButtonVariant.Outlined, backdropState = backdropState, modifier = Modifier.weight(1f))
                                    AntoButton(text = "Text", onClick = { }, variant = AntoButtonVariant.Text, backdropState = backdropState, modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        SectionTitle("Pulsanti Icona & Azione Flottante (FAB)")
                        AntoCard(backdropState = backdropState) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AntoIconButton(icon = AntoIcons.PlayArrow, onClick = { }, size = 46.dp, iconSize = 22.dp, backdropState = backdropState)
                                AntoIconButton(icon = AntoIcons.Star, onClick = { }, size = 46.dp, iconSize = 22.dp, backdropState = backdropState)
                                AntoIconButton(icon = AntoIcons.Share, onClick = { }, size = 46.dp, iconSize = 22.dp, backdropState = backdropState)
                                AntoIconButton(icon = AntoIcons.Settings, onClick = { }, size = 46.dp, iconSize = 22.dp, backdropState = backdropState)
                                AntoFloatingActionButton(onClick = { }, size = 46.dp, backdropState = backdropState) {
                                    Icon(imageVector = AntoIcons.Add, contentDescription = "Nuovo", tint = Color.White, modifier = Modifier.size(22.dp))
                                }
                            }
                        }

                        SectionTitle("Valutazione a Stelle")
                        AntoCard(backdropState = backdropState) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AntoRatingBar(rating = starRating, onRatingChanged = { starRating = it }, backdropState = backdropState)
                                Text(text = "$starRating stelle su 5", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.70f))
                            }
                        }
                    }
                }

                1 -> {
                    // Sotto-Schermata 2: Campi & Form
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Barra di Ricerca")
                        AntoSearchBar(query = searchVal, onQueryChange = { searchVal = it }, backdropState = backdropState)

                        SectionTitle("Campi di Testo & Specializzati")
                        AntoCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                AntoTextField(
                                    value = textVal,
                                    onValueChange = { textVal = it },
                                    label = "Nome Completo",
                                    leadingIcon = AntoIcons.AccountCircle,
                                    backdropState = backdropState
                                )

                                AntoPasswordTextField(
                                    value = passwordVal,
                                    onValueChange = { passwordVal = it },
                                    label = "Password Sicura",
                                    backdropState = backdropState
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    AntoEmailTextField(value = emailVal, onValueChange = { emailVal = it }, label = "Email", backdropState = backdropState, modifier = Modifier.weight(1f))
                                    AntoPhoneTextField(value = phoneVal, onValueChange = { phoneVal = it }, label = "Telefono", backdropState = backdropState, modifier = Modifier.weight(1f))
                                }

                                AntoNumberTextField(value = numberVal, onValueChange = { numberVal = it }, label = "Importo Numerico", backdropState = backdropState)

                                AntoTextArea(value = notesVal, onValueChange = { notesVal = it }, placeholder = "Inserisci note o descrizioni...", maxLength = 200, backdropState = backdropState)
                            }
                        }
                    }
                }

                2 -> {
                    // Sotto-Schermata 3: Data, Ora & Colore
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Selettori di Data & Ora")
                        AntoCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    AntoDatePickerField(selectedDate = selectedDate, onClick = { isDatePickerOpen = true }, backdropState = backdropState, modifier = Modifier.weight(1f))
                                    AntoTimePickerField(selectedHour = selectedHour, selectedMinute = selectedMinute, onClick = { isTimePickerOpen = true }, backdropState = backdropState, modifier = Modifier.weight(1f))
                                }
                                AntoDropdownSelect(
                                    items = listOf("Opzione Zaffiro", "Opzione Smeraldo", "Opzione Tramonto", "Opzione Violetto"),
                                    selectedItem = selectedDropdownOption,
                                    onItemSelected = { selectedDropdownOption = it },
                                    label = "Tema Dropdown",
                                    leadingIcon = AntoIcons.Star,
                                    backdropState = backdropState
                                )
                            }
                        }

                        SectionTitle("Selettore Spettro Cromatico")
                        AntoColorPicker(selectedColor = colorPickerVal, onColorSelected = { colorPickerVal = it }, backdropState = backdropState)
                    }
                }

                else -> {
                    // Sotto-Schermata 4: Slider & Toggle
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Contatori Numerici & Codice OTP")
                        AntoCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                AntoStepper(value = stepperVal, onValueChange = { stepperVal = it }, label = "Quantità Elementi", unit = "pz", minValue = 1, maxValue = 20, backdropState = backdropState)
                                AntoHorizontalDivider()
                                Text("Codice di Verifica OTP (4 Cifre)", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.70f))
                                AntoOtpInput(otpValue = otpCode, onOtpChange = { otpCode = it }, length = 4, backdropState = backdropState, modifier = Modifier.fillMaxWidth())
                            }
                        }

                        SectionTitle("Controlli Booleani")
                        AntoCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Interruttore Liquid Switch", color = Color.White)
                                    AntoSwitch(checked = switchVal, onCheckedChange = { switchVal = it }, backdropState = backdropState)
                                }
                                AntoHorizontalDivider()
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Checkbox Sincronizzazione", color = Color.White)
                                    AntoCheckbox(checked = checkboxVal, onCheckedChange = { checkboxVal = it }, backdropState = backdropState)
                                }
                                AntoHorizontalDivider()
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Opzione Radio 1", color = Color.White)
                                    AntoRadioButton(selected = radioVal == 0, onClick = { radioVal = 0 }, backdropState = backdropState)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Opzione Radio 2", color = Color.White)
                                    AntoRadioButton(selected = radioVal == 1, onClick = { radioVal = 1 }, backdropState = backdropState)
                                }
                            }
                        }

                        SectionTitle("Slider & Range Slider")
                        AntoCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text("Slider Singolo: ${(sliderVal * 100).toInt()}%", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                AntoSlider(value = sliderVal, onValueChange = { sliderVal = it }, valueRange = 0f..1f, backdropState = backdropState)
                                AntoHorizontalDivider()
                                Text("Range Slider: ${(rangeSliderVal.start * 100).toInt()}€ - ${(rangeSliderVal.endInclusive * 100).toInt()}€", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                AntoRangeSlider(value = rangeSliderVal, onValueChange = { rangeSliderVal = it }, backdropState = backdropState)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Pickers Dialogs
    AntoDatePickerDialog(
        isOpen = isDatePickerOpen,
        onDismissRequest = { isDatePickerOpen = false },
        onDateSelected = { selectedDate = it },
        initialDate = selectedDate ?: Calendar.getInstance(),
        backdropState = backdropState
    )

    AntoTimePickerDialog(
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
