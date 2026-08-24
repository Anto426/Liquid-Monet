package com.anto426.app.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.anto426.antoui.components.buttons.AntoButton
import com.anto426.antoui.components.buttons.AntoButtonSize
import com.anto426.antoui.components.buttons.AntoButtonVariant
import com.anto426.antoui.components.cards.AntoCard
import com.anto426.antoui.components.feedback.AntoDialog
import com.anto426.antoui.components.feedback.AntoDialogActionButton
import com.anto426.antoui.components.menu.AntoGlassDropdownMenu
import com.anto426.antoui.components.menu.AntoGlassMenuItem
import com.anto426.antoui.components.display.AntoHorizontalDivider
import com.anto426.antoui.components.navigation.AntoLiquidTabRow
import com.anto426.antoui.components.feedback.AntoLoading
import com.anto426.antoui.components.feedback.AntoLoadingStyle
import com.anto426.antoui.components.cards.AntoPreferenceItem
import com.anto426.antoui.components.feedback.AntoSheet
import com.anto426.antoui.components.feedback.AntoToastState
import com.anto426.antoui.components.feedback.AntoToastType
import com.anto426.antoui.glass.overlay.AntoGlassDropdownPlacement
import com.anto426.antoui.glass.overlay.antoGlassOverlayAnchor
import com.anto426.antoui.glass.overlay.rememberAntoGlassOverlayAnchorState
import com.anto426.antoui.icons.AntoIcons
import com.anto426.antoui.motion.AntoAnimatedNavContent
import com.anto426.antoui.motion.AntoNavTransition
import com.kyant.backdrop.Backdrop

@Composable
fun ModalsFeedbackHubScreen(
    toastState: AntoToastState,
    backdropState: Backdrop,
    modifier: Modifier = Modifier
) {
    var currentSubTab by remember { mutableIntStateOf(0) }
    val subTabs = listOf("Dialog & Sheet", "Menu", "Toast", "Caricamento")

    var isDialogOpen by remember { mutableStateOf(false) }
    var isSheetOpen by remember { mutableStateOf(false) }
    var isMenuOpen by remember { mutableStateOf(false) }

    var menuVerticalDir by remember { mutableIntStateOf(0) }
    var menuHorizAlign by remember { mutableIntStateOf(0) }
    val selectedPlacement = when (menuVerticalDir) {
        0 -> when (menuHorizAlign) {
            0 -> AntoGlassDropdownPlacement.BelowEnd
            1 -> AntoGlassDropdownPlacement.BelowCenter
            else -> AntoGlassDropdownPlacement.BelowStart
        }
        1 -> when (menuHorizAlign) {
            0 -> AntoGlassDropdownPlacement.AboveEnd
            1 -> AntoGlassDropdownPlacement.AboveCenter
            else -> AntoGlassDropdownPlacement.AboveStart
        }
        else -> AntoGlassDropdownPlacement.Auto
    }

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
            label = "modalsSubTabTransition"
        ) { tab ->
            when (tab) {
                0 -> {
                    // Sotto-Schermata 1: Dialog & Sheet
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Finestre Modali & Bottom Sheet")
                        AntoCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Finestre in Vetro Ottico con Rifrazione Snell", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.70f))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                    AntoButton(text = "Apri Dialog", onClick = { isDialogOpen = true }, variant = AntoButtonVariant.Primary, backdropState = backdropState, modifier = Modifier.weight(1f))
                                    AntoButton(text = "Apri Sheet", onClick = { isSheetOpen = true }, variant = AntoButtonVariant.Secondary, backdropState = backdropState, modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Sotto-Schermata 2: Dropdown Menu
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Menu a Tendina (Dropdown)")
                        AntoCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Direzione Verticale", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.70f))
                                AntoLiquidTabRow(
                                    items = listOf("Sotto", "Sopra", "Auto"),
                                    selectedIndex = menuVerticalDir,
                                    onTabSelected = { menuVerticalDir = it },
                                    backdropState = backdropState
                                )

                                if (menuVerticalDir != 2) {
                                    Text("Allineamento Orizzontale", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.70f))
                                    AntoLiquidTabRow(
                                        items = listOf("Destra", "Centro", "Sinistra"),
                                        selectedIndex = menuHorizAlign,
                                        onTabSelected = { menuHorizAlign = it },
                                        backdropState = backdropState
                                    )
                                }

                                AntoHorizontalDivider()

                                val menuAnchorState = rememberAntoGlassOverlayAnchorState()
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    AntoButton(
                                        text = "Apri Dropdown Menu",
                                        onClick = { isMenuOpen = !isMenuOpen },
                                        variant = AntoButtonVariant.Tonal,
                                        backdropState = backdropState,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .antoGlassOverlayAnchor(menuAnchorState)
                                    )

                                    AntoGlassDropdownMenu(
                                        expanded = isMenuOpen,
                                        onDismissRequest = { isMenuOpen = false },
                                        anchorState = menuAnchorState,
                                        placement = selectedPlacement,
                                        offset = DpOffset(0.dp, 6.dp),
                                        backdropState = backdropState
                                    ) {
                                        AntoGlassMenuItem(text = "Condividi Elemento", icon = AntoIcons.Share, onClick = { isMenuOpen = false; toastState.show("Condivisione avviata!", type = AntoToastType.Info) })
                                        AntoGlassMenuItem(text = "Salva nei Preferiti", icon = AntoIcons.Star, onClick = { isMenuOpen = false; toastState.show("Aggiunto ai preferiti!", type = AntoToastType.Success) })
                                        AntoGlassMenuItem(text = "Elimina", icon = AntoIcons.Delete, onClick = { isMenuOpen = false; toastState.show("Elemento rimosso", type = AntoToastType.Error) })
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Sotto-Schermata 3: Notifiche Toast
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Notifiche Toast in Cristallo Rifrattivo")
                        AntoCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Tocca un pulsante per mostrare un Toast in puro vetro liquido:", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.70f), style = MaterialTheme.typography.bodySmall)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    AntoButton(
                                        text = "Successo",
                                        onClick = { toastState.show("Operazione completata!", "I dati sono stati sincronizzati su cloud", AntoToastType.Success) },
                                        variant = AntoButtonVariant.Primary,
                                        size = AntoButtonSize.Small,
                                        backdropState = backdropState,
                                        modifier = Modifier.weight(1f)
                                    )
                                    AntoButton(
                                        text = "Informazione",
                                        onClick = { toastState.show("Aggiornamento pronto", "Versione AntoUI 2.0.0 scaricata", AntoToastType.Info) },
                                        variant = AntoButtonVariant.Secondary,
                                        size = AntoButtonSize.Small,
                                        backdropState = backdropState,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    AntoButton(
                                        text = "Avviso",
                                        onClick = { toastState.show("Batteria scarica", "Meno del 15% di carica residua", AntoToastType.Warning) },
                                        variant = AntoButtonVariant.Tonal,
                                        size = AntoButtonSize.Small,
                                        backdropState = backdropState,
                                        modifier = Modifier.weight(1f)
                                    )
                                    AntoButton(
                                        text = "Errore",
                                        onClick = { toastState.show("Errore di rete", "Impossibile contattare il server remoto", AntoToastType.Error) },
                                        variant = AntoButtonVariant.Outlined,
                                        size = AntoButtonSize.Small,
                                        backdropState = backdropState,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {
                    // Sotto-Schermata 4: Caricamento & Shimmer
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Indicatori di Avanzamento & Skeleton")
                        AntoCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                AntoLoading(style = AntoLoadingStyle.Linear, progress = 0.68f, message = "Avanzamento ondulatorio fluido (68%)", backdropState = backdropState)
                                AntoHorizontalDivider()
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Spinner Circolare Monet", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                    AntoLoading(style = AntoLoadingStyle.Circular, backdropState = backdropState)
                                }
                                AntoHorizontalDivider()
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Gocce di Cristallo Liquide", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                    AntoLoading(style = AntoLoadingStyle.Dots, backdropState = backdropState)
                                }
                                AntoHorizontalDivider()
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Impulso Radiale Prismatico", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                    AntoLoading(style = AntoLoadingStyle.Pulse, backdropState = backdropState)
                                }
                                AntoHorizontalDivider()
                                Text("Scheletro Shimmer in Vetro", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                AntoLoading(style = AntoLoadingStyle.Shimmer, modifier = Modifier.fillMaxWidth().height(36.dp), backdropState = backdropState)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialogs & Sheets
    if (isDialogOpen) {
        AntoDialog(
            onDismissRequest = { isDialogOpen = false },
            title = "Conferma Operazione",
            text = "Vuoi applicare e salvare le nuove impostazioni di sistema?",
            backdropState = backdropState,
            confirmButton = {
                AntoDialogActionButton(
                    text = "Conferma",
                    onClick = {
                        isDialogOpen = false
                        toastState.show("Impostazioni salvate!", type = AntoToastType.Success)
                    },
                    isPrimary = true
                )
            },
            dismissButton = {
                AntoDialogActionButton(
                    text = "Annulla",
                    onClick = { isDialogOpen = false }
                )
            }
        )
    }

    if (isSheetOpen) {
        AntoSheet(
            onDismissRequest = { isSheetOpen = false },
            title = "Informazioni di Sistema",
            subtitle = "Dettagli runtime grafico e architettura UI.",
            backdropState = backdropState
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                AntoPreferenceItem(title = "Accelerazione Grafica", subtitle = "AGSL Skia Hardware Pipeline", icon = AntoIcons.Info)
                AntoPreferenceItem(title = "Design System", subtitle = "Material 3 Expressive & Monet", icon = AntoIcons.Star)
                AntoButton(text = "Chiudi", onClick = { isSheetOpen = false }, variant = AntoButtonVariant.Primary, backdropState = backdropState, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
