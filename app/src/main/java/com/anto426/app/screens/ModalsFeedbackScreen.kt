package com.anto426.app.screens

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
import com.anto426.liquidmonet.components.buttons.LiquidButton
import com.anto426.liquidmonet.components.buttons.LiquidButtonSize
import com.anto426.liquidmonet.components.buttons.LiquidButtonVariant
import com.anto426.liquidmonet.components.cards.LiquidCard
import com.anto426.liquidmonet.components.feedback.LiquidDialog
import com.anto426.liquidmonet.components.menu.LiquidDropdownMenu
import com.anto426.liquidmonet.components.menu.LiquidMenuItem
import com.anto426.liquidmonet.components.display.LiquidHorizontalDivider
import com.anto426.liquidmonet.components.display.LiquidSectionHeader
import com.anto426.liquidmonet.components.navigation.LiquidNavigationItem
import com.anto426.liquidmonet.components.navigation.LiquidTabBar
import com.anto426.liquidmonet.components.feedback.LiquidLoading
import com.anto426.liquidmonet.components.feedback.LiquidLoadingStyle
import com.anto426.liquidmonet.components.cards.LiquidPreferenceItem
import com.anto426.liquidmonet.components.feedback.LiquidSheet
import com.anto426.liquidmonet.components.feedback.LiquidToastState
import com.anto426.liquidmonet.components.feedback.LiquidToastType
import com.anto426.liquidmonet.glass.overlay.LiquidGlassDropdownPlacement
import com.anto426.liquidmonet.glass.overlay.liquidGlassOverlayAnchor
import com.anto426.liquidmonet.glass.overlay.rememberLiquidGlassOverlayAnchorState
import com.anto426.liquidmonet.icons.LiquidIcons
import com.kyant.backdrop.Backdrop

@Composable
fun ModalsFeedbackScreen(
    toastState: LiquidToastState,
    backdropState: Backdrop,
    modifier: Modifier = Modifier
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var isSheetOpen by remember { mutableStateOf(false) }
    var isMenuOpen by remember { mutableStateOf(false) }

    var menuVerticalDir by remember { mutableIntStateOf(0) }
    var menuHorizAlign by remember { mutableIntStateOf(0) }
    val selectedPlacement = when (menuVerticalDir) {
        0 -> when (menuHorizAlign) {
            0 -> LiquidGlassDropdownPlacement.BelowEnd
            1 -> LiquidGlassDropdownPlacement.BelowCenter
            else -> LiquidGlassDropdownPlacement.BelowStart
        }
        1 -> when (menuHorizAlign) {
            0 -> LiquidGlassDropdownPlacement.AboveEnd
            1 -> LiquidGlassDropdownPlacement.AboveCenter
            else -> LiquidGlassDropdownPlacement.AboveStart
        }
        else -> LiquidGlassDropdownPlacement.Auto
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Finestre di Dialogo & Bottom Sheet
        LiquidSectionHeader("Finestre Modali & Bottom Sheet")
        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Finestre in Vetro Ottico con Rifrazione Snell",
                    style = MaterialTheme.typography.labelMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LiquidButton(
                        text = "Apri Dialog",
                        onClick = { isDialogOpen = true },
                        variant = LiquidButtonVariant.Primary,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                    LiquidButton(
                        text = "Apri Sheet",
                        onClick = { isSheetOpen = true },
                        variant = LiquidButtonVariant.Secondary,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Dropdown Menu con Direzione & Allineamento
        LiquidSectionHeader("Menu a Tendina (Dropdown con Ottica Liquida)")
        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Posizionamento Verticale",
                    style = MaterialTheme.typography.labelMedium
                )
                LiquidTabBar(
                    items = listOf(
                        LiquidNavigationItem("Sotto"),
                        LiquidNavigationItem("Sopra"),
                        LiquidNavigationItem("Auto")
                    ),
                    selectedIndex = menuVerticalDir,
                    onTabSelected = { menuVerticalDir = it },
                    backdropState = backdropState
                )

                if (menuVerticalDir != 2) {
                    Text(
                        text = "Allineamento Orizzontale",
                        style = MaterialTheme.typography.labelMedium
                    )
                    LiquidTabBar(
                        items = listOf(
                            LiquidNavigationItem("Destra"),
                            LiquidNavigationItem("Centro"),
                            LiquidNavigationItem("Sinistra")
                        ),
                        selectedIndex = menuHorizAlign,
                        onTabSelected = { menuHorizAlign = it },
                        backdropState = backdropState
                    )
                }

                LiquidHorizontalDivider()

                val menuAnchorState = rememberLiquidGlassOverlayAnchorState()
                Box(modifier = Modifier.fillMaxWidth()) {
                    LiquidButton(
                        text = "Apri Dropdown Menu",
                        onClick = { isMenuOpen = !isMenuOpen },
                        variant = LiquidButtonVariant.Tonal,
                        backdropState = backdropState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlassOverlayAnchor(menuAnchorState)
                    )

                    LiquidDropdownMenu(
                        expanded = isMenuOpen,
                        onDismissRequest = { isMenuOpen = false },
                        anchorState = menuAnchorState,
                        placement = selectedPlacement,
                        offset = DpOffset(0.dp, 6.dp),
                        backdropState = backdropState
                    ) {
                        LiquidMenuItem(
                            text = "Condividi Elemento",
                            icon = LiquidIcons.Share,
                            onClick = {
                                isMenuOpen = false
                                toastState.show("Condivisione avviata!", type = LiquidToastType.Info)
                            }
                        )
                        LiquidMenuItem(
                            text = "Salva nei Preferiti",
                            icon = LiquidIcons.Star,
                            onClick = {
                                isMenuOpen = false
                                toastState.show("Aggiunto ai preferiti!", type = LiquidToastType.Success)
                            }
                        )
                        LiquidMenuItem(
                            text = "Elimina",
                            icon = LiquidIcons.Delete,
                            onClick = {
                                isMenuOpen = false
                                toastState.show("Elemento rimosso", type = LiquidToastType.Error)
                            }
                        )
                    }
                }
            }
        }

        // Notifiche Toast in Puro Cristallo
        LiquidSectionHeader("Notifiche Toast in Cristallo Rifrattivo")
        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Tocca per attivare i Toast in puro cristallo ottico:",
                    style = MaterialTheme.typography.bodySmall
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LiquidButton(
                        text = "Successo",
                        onClick = {
                            toastState.show(
                                message = "Operazione completata!",
                                subtitle = "I dati sono stati sincronizzati su cloud",
                                type = LiquidToastType.Success
                            )
                        },
                        variant = LiquidButtonVariant.Primary,
                        size = LiquidButtonSize.Small,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                    LiquidButton(
                        text = "Informazione",
                        onClick = {
                            toastState.show(
                                message = "Aggiornamento disponibile",
                                subtitle = "Versione Liquid Monet 2.0.0 scaricata",
                                type = LiquidToastType.Info
                            )
                        },
                        variant = LiquidButtonVariant.Secondary,
                        size = LiquidButtonSize.Small,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LiquidButton(
                        text = "Avviso",
                        onClick = {
                            toastState.show(
                                message = "Batteria in esaurimento",
                                subtitle = "Meno del 15% di carica residua",
                                type = LiquidToastType.Warning
                            )
                        },
                        variant = LiquidButtonVariant.Tonal,
                        size = LiquidButtonSize.Small,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                    LiquidButton(
                        text = "Errore",
                        onClick = {
                            toastState.show(
                                message = "Errore di connessione",
                                subtitle = "Impossibile contattare il server remoto",
                                type = LiquidToastType.Error
                            )
                        },
                        variant = LiquidButtonVariant.Outlined,
                        size = LiquidButtonSize.Small,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Caricamento & Indicatori di Avanzamento
        LiquidSectionHeader("Animazioni di Caricamento & Shimmer")
        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                LiquidLoading(
                    style = LiquidLoadingStyle.Linear,
                    progress = 0.68f,
                    message = "Avanzamento ondulatorio fluido (68%)",
                    backdropState = backdropState
                )

                LiquidHorizontalDivider()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Spinner Circolare Monet", style = MaterialTheme.typography.bodyMedium)
                    LiquidLoading(style = LiquidLoadingStyle.Circular, backdropState = backdropState)
                }

                LiquidHorizontalDivider()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Gocce di Cristallo Liquide", style = MaterialTheme.typography.bodyMedium)
                    LiquidLoading(style = LiquidLoadingStyle.Dots, backdropState = backdropState)
                }

                LiquidHorizontalDivider()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Impulso Radiale Prismatico", style = MaterialTheme.typography.bodyMedium)
                    LiquidLoading(style = LiquidLoadingStyle.Pulse, backdropState = backdropState)
                }

                LiquidHorizontalDivider()

                Text("Scheletro Shimmer in Vetro", style = MaterialTheme.typography.bodyMedium)
                LiquidLoading(
                    style = LiquidLoadingStyle.Shimmer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    backdropState = backdropState
                )
            }
        }
    }

    // Modal Dialogs & Sheets
    if (isDialogOpen) {
        LiquidDialog(
            onDismissRequest = { isDialogOpen = false },
            title = "Conferma Operazione",
            text = "Vuoi applicare e salvare le nuove impostazioni di sistema?",
            backdropState = backdropState,
            confirmButton = {
                LiquidButton(
                    text = "Conferma",
                    onClick = {
                        isDialogOpen = false
                        toastState.show("Impostazioni salvate!", type = LiquidToastType.Success)
                    },
                    variant = LiquidButtonVariant.Primary,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            dismissButton = {
                LiquidButton(
                    text = "Annulla",
                    onClick = { isDialogOpen = false },
                    variant = LiquidButtonVariant.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }

    if (isSheetOpen) {
        LiquidSheet(
            onDismissRequest = { isSheetOpen = false },
            title = "Informazioni di Sistema",
            subtitle = "Dettagli runtime grafico e architettura UI.",
            backdropState = backdropState
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                LiquidPreferenceItem(
                    title = "Accelerazione Grafica",
                    subtitle = "AGSL Skia Hardware Pipeline",
                    icon = LiquidIcons.Info
                )
                LiquidPreferenceItem(
                    title = "Design System",
                    subtitle = "Material 3 Expressive & Monet",
                    icon = LiquidIcons.Star
                )
                LiquidButton(
                    text = "Chiudi",
                    onClick = { isSheetOpen = false },
                    variant = LiquidButtonVariant.Primary,
                    backdropState = backdropState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
