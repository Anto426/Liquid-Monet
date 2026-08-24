package com.anto426.app.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anto426.antoui.components.cards.AntoAccordionItem
import com.anto426.antoui.components.display.AntoAvatar
import com.anto426.antoui.components.display.AntoAvatarGroup
import com.anto426.antoui.components.display.AntoAvatarPresence
import com.anto426.antoui.components.navigation.AntoBreadcrumbItem
import com.anto426.antoui.components.navigation.AntoBreadcrumbs
import com.anto426.antoui.components.cards.AntoCard
import com.anto426.antoui.components.selection.AntoChip
import com.anto426.antoui.components.display.AntoEmptyState
import com.anto426.antoui.components.selection.AntoFilterChip
import com.anto426.antoui.components.display.AntoHorizontalDivider
import com.anto426.antoui.components.navigation.AntoLiquidTabRow
import com.anto426.antoui.components.navigation.AntoPageIndicator
import com.anto426.antoui.components.navigation.AntoPagination
import com.anto426.antoui.components.cards.AntoPreferenceItem
import com.anto426.antoui.components.selection.AntoSlider
import com.anto426.antoui.components.display.AntoSwipeToDismissBox
import com.anto426.antoui.components.selection.AntoSwitch
import com.anto426.antoui.components.feedback.AntoToastState
import com.anto426.antoui.components.feedback.AntoToastType
import com.anto426.antoui.icons.AntoIcons
import com.anto426.antoui.motion.AntoAnimatedNavContent
import com.anto426.antoui.motion.AntoNavTransition
import com.kyant.backdrop.Backdrop

@Composable
fun NavigationHubScreen(
    toastState: AntoToastState,
    backdropState: Backdrop,
    modifier: Modifier = Modifier
) {
    var currentSubTab by remember { mutableIntStateOf(0) }
    val subTabs = listOf("Paginazione", "Accordion", "Gesti & Avatar", "Impostazioni")

    // Pager & chip states
    var selectedChip by remember { mutableStateOf("Tutti") }
    var paginationPage by remember { mutableIntStateOf(2) }
    var pagerIndicatorPage by remember { mutableIntStateOf(1) }

    // Accordion
    var accordion1Open by remember { mutableStateOf(false) }
    var accordion2Open by remember { mutableStateOf(true) }

    // Settings
    var wifiState by remember { mutableStateOf(true) }
    var notificationsState by remember { mutableStateOf(true) }
    var darkGlassState by remember { mutableStateOf(true) }
    var brightnessVal by remember { mutableFloatStateOf(0.75f) }
    var volumeVal by remember { mutableFloatStateOf(0.60f) }

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
            label = "navigationSubTabTransition"
        ) { tab ->
            when (tab) {
                0 -> {
                    // Sotto-Schermata 1: Nav, Paginazione & Chip
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Percorso Breadcrumbs")
                        AntoBreadcrumbs(
                            items = listOf(
                                AntoBreadcrumbItem("Home") { toastState.show("Navigato a Home", type = AntoToastType.Info) },
                                AntoBreadcrumbItem("Impostazioni") { toastState.show("Navigato a Impostazioni", type = AntoToastType.Info) },
                                AntoBreadcrumbItem("Sicurezza & Accesso") { toastState.show("Navigato a Sicurezza", type = AntoToastType.Info) },
                                AntoBreadcrumbItem("Attuale")
                            ),
                            backdropState = backdropState
                        )

                        SectionTitle("Indicatori Pager & Paginazione")
                        AntoCard(backdropState = backdropState) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text("Indicatore a Goccia Fluida (Worm Droplet)", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.70f))
                                AntoPageIndicator(
                                    pageCount = 5,
                                    currentPage = pagerIndicatorPage,
                                    onPageSelected = { pagerIndicatorPage = it },
                                    backdropState = backdropState,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                AntoHorizontalDivider()
                                Text("Paginazione Numerata in Vetro", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.70f))
                                AntoPagination(
                                    currentPage = paginationPage,
                                    totalPages = 5,
                                    onPageChange = { paginationPage = it },
                                    backdropState = backdropState,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        SectionTitle("Filtri & Chip Interattivi")
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                        ) {
                            listOf(
                                Triple("Tutti", AntoIcons.Home, "24"),
                                Triple("Preferiti", AntoIcons.Star, "8"),
                                Triple("Recenti", AntoIcons.Refresh, "12"),
                                Triple("Audio", AntoIcons.Settings, "5")
                            ).forEach { (label, icon, count) ->
                                AntoFilterChip(
                                    selected = selectedChip == label,
                                    onSelectedChange = { selectedChip = label },
                                    label = label,
                                    leadingIcon = icon,
                                    badge = count,
                                    backdropState = backdropState
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            AntoChip(label = "Monet M3", onClick = { }, selected = true, backdropState = backdropState)
                            AntoChip(label = "Vetro Snell", onClick = { }, onCloseClick = { }, backdropState = backdropState)
                            AntoChip(label = "Liquid 2.0", onClick = { }, onCloseClick = { }, backdropState = backdropState)
                            AntoChip(label = "AGSL Shaders", onClick = { }, backdropState = backdropState)
                        }
                    }
                }

                1 -> {
                    // Sotto-Schermata 2: Accordion Espandibili
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Pannelli a Fisarmonica (Accordion)")
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            AntoAccordionItem(
                                title = "Architettura Snell & AGSL Shaders",
                                subtitle = "Riflessione e rifrazione della luce a 120 FPS",
                                leadingIcon = AntoIcons.Info,
                                isExpanded = accordion1Open,
                                onExpandedChange = { accordion1Open = it },
                                backdropState = backdropState
                            ) {
                                Text(
                                    text = "Lo shader AGSL campiona il backdrop calcolando la dispersione cromatica e le ombre interne per simulare un vetro reale senza gravare sulla CPU.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.80f)
                                )
                            }

                            AntoAccordionItem(
                                title = "Monet Dynamic Chromatic Tint",
                                subtitle = "Armonizzazione colore in tempo reale",
                                leadingIcon = AntoIcons.Star,
                                isExpanded = accordion2Open,
                                onExpandedChange = { accordion2Open = it },
                                backdropState = backdropState
                            ) {
                                Text(
                                    text = "Ogni componente adatta la trasparenza del vetro e la specular highlight al tema di sistema Material 3 attivo.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.80f)
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // Sotto-Schermata 3: Gesti, Avatar & Empty State
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Gesto Swipe to Dismiss / Action")
                        AntoSwipeToDismissBox(
                            onDismissLeft = { toastState.show("Elemento archiviato!", type = AntoToastType.Info) },
                            onDismissRight = { toastState.show("Aggiunto ai preferiti!", type = AntoToastType.Success) },
                            backdropState = backdropState
                        ) {
                            AntoPreferenceItem(
                                title = "Scorri a destra o sinistra",
                                subtitle = "Trascina per visualizzare le azioni in vetro",
                                icon = AntoIcons.Share,
                                backdropState = backdropState
                            )
                        }

                        SectionTitle("Avatar & Badge Presenza")
                        AntoCard(backdropState = backdropState) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    AntoAvatar(initials = "AG", presence = AntoAvatarPresence.Online, backdropState = backdropState)
                                    AntoAvatar(initials = "LM", presence = AntoAvatarPresence.Busy, backdropState = backdropState)
                                    AntoAvatar(presence = AntoAvatarPresence.Away, backdropState = backdropState)
                                }
                                AntoAvatarGroup(avatars = listOf("AL", "MK", "ST", "DV", "RK", "PX"), maxDisplay = 3, backdropState = backdropState)
                            }
                        }

                        SectionTitle("Stato Vuoto (Empty State)")
                        AntoEmptyState(
                            title = "Nessun Nuovo Elemento",
                            description = "Tutte le notifiche e le attività sono state completate con successo.",
                            icon = AntoIcons.Check,
                            actionButtonText = "Ricarica Dati",
                            onActionClick = { toastState.show("Dati aggiornati!", type = AntoToastType.Success) },
                            backdropState = backdropState
                        )
                    }
                }

                else -> {
                    // Sotto-Schermata 4: Impostazioni & Profilo
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        AntoCard(backdropState = backdropState) {
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                AntoAvatar(initials = "AU", presence = AntoAvatarPresence.Online, size = 54.dp, backdropState = backdropState)
                                Column {
                                    Text(text = "Anto Developer", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                    Text(text = "anto@liquidui.com • Pro Tier", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.70f))
                                }
                            }
                        }

                        SectionTitle("Connettività & Notifiche")
                        AntoCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                AntoPreferenceItem(
                                    title = "Rete Wi-Fi",
                                    subtitle = if (wifiState) "Connesso a Liquid-5G" else "Disattivato",
                                    icon = AntoIcons.Phone,
                                    backdropState = backdropState,
                                    trailingContent = {
                                        AntoSwitch(checked = wifiState, onCheckedChange = { wifiState = it }, backdropState = backdropState)
                                    }
                                )
                                AntoHorizontalDivider()
                                AntoPreferenceItem(
                                    title = "Notifiche di Sistema",
                                    subtitle = if (notificationsState) "Attive con suoni aptici" else "Silenziate",
                                    icon = AntoIcons.Notifications,
                                    backdropState = backdropState,
                                    trailingContent = {
                                        AntoSwitch(checked = notificationsState, onCheckedChange = { notificationsState = it }, backdropState = backdropState)
                                    }
                                )
                                AntoHorizontalDivider()
                                AntoPreferenceItem(
                                    title = "Modalità Vetro Scuro",
                                    subtitle = if (darkGlassState) "Contrasto e riflessi profondi" else "Vetro chiaro",
                                    icon = AntoIcons.Star,
                                    backdropState = backdropState,
                                    trailingContent = {
                                        AntoSwitch(checked = darkGlassState, onCheckedChange = { darkGlassState = it }, backdropState = backdropState)
                                    }
                                )
                            }
                        }

                        SectionTitle("Luminosità & Volume")
                        AntoCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Luminosità Schermo", color = Color.White)
                                    Text("${(brightnessVal * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                AntoSlider(value = brightnessVal, onValueChange = { brightnessVal = it }, valueRange = 0f..1f, backdropState = backdropState)
                                AntoHorizontalDivider()
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Volume Audio", color = Color.White)
                                    Text("${(volumeVal * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                AntoSlider(value = volumeVal, onValueChange = { volumeVal = it }, valueRange = 0f..1f, backdropState = backdropState)
                            }
                        }
                    }
                }
            }
        }
    }
}
