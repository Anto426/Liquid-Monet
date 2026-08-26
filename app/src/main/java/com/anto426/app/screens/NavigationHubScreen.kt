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
import com.anto426.liquidmonet.components.cards.LiquidAccordionItem
import com.anto426.liquidmonet.components.display.LiquidAvatar
import com.anto426.liquidmonet.components.display.LiquidAvatarGroup
import com.anto426.liquidmonet.components.display.LiquidAvatarPresence
import com.anto426.liquidmonet.components.navigation.LiquidBreadcrumbItem
import com.anto426.liquidmonet.components.navigation.LiquidBreadcrumbs
import com.anto426.liquidmonet.components.cards.LiquidCard
import com.anto426.liquidmonet.components.selection.LiquidChip
import com.anto426.liquidmonet.components.display.LiquidEmptyState
import com.anto426.liquidmonet.components.selection.LiquidFilterChip
import com.anto426.liquidmonet.components.display.LiquidHorizontalDivider
import com.anto426.liquidmonet.components.navigation.LiquidNavigationItem
import com.anto426.liquidmonet.components.navigation.LiquidTabBar
import com.anto426.liquidmonet.components.navigation.LiquidPageIndicator
import com.anto426.liquidmonet.components.navigation.LiquidPagination
import com.anto426.liquidmonet.components.cards.LiquidPreferenceItem
import com.anto426.liquidmonet.components.selection.LiquidSlider
import com.anto426.liquidmonet.components.display.LiquidSwipeToDismissBox
import com.anto426.liquidmonet.components.selection.LiquidSwitch
import com.anto426.liquidmonet.components.feedback.LiquidToastState
import com.anto426.liquidmonet.components.feedback.LiquidToastType
import com.anto426.liquidmonet.icons.LiquidIcons
import com.anto426.liquidmonet.motion.LiquidAnimatedNavContent
import com.anto426.liquidmonet.motion.LiquidNavTransition
import com.kyant.backdrop.Backdrop

@Composable
fun NavigationHubScreen(
    toastState: LiquidToastState,
    backdropState: Backdrop,
    modifier: Modifier = Modifier
) {
    var currentSubTab by remember { mutableIntStateOf(0) }
    val subTabs = listOf(
        LiquidNavigationItem("Paginazione"),
        LiquidNavigationItem("Accordion"),
        LiquidNavigationItem("Gesti & Avatar"),
        LiquidNavigationItem("Impostazioni")
    )

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
        LiquidTabBar(
            items = subTabs,
            selectedIndex = currentSubTab,
            onTabSelected = { currentSubTab = it },
            backdropState = backdropState
        )

        LiquidAnimatedNavContent(
            targetState = currentSubTab,
            transition = LiquidNavTransition.AutoDirectional,
            label = "navigationSubTabTransition"
        ) { tab ->
            when (tab) {
                0 -> {
                    // Sotto-Schermata 1: Nav, Paginazione & Chip
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Percorso Breadcrumbs")
                        LiquidBreadcrumbs(
                            items = listOf(
                                LiquidBreadcrumbItem("Home") { toastState.show("Navigato a Home", type = LiquidToastType.Info) },
                                LiquidBreadcrumbItem("Impostazioni") { toastState.show("Navigato a Impostazioni", type = LiquidToastType.Info) },
                                LiquidBreadcrumbItem("Sicurezza & Accesso") { toastState.show("Navigato a Sicurezza", type = LiquidToastType.Info) },
                                LiquidBreadcrumbItem("Attuale")
                            ),
                            backdropState = backdropState
                        )

                        SectionTitle("Indicatori Pager & Paginazione")
                        LiquidCard(backdropState = backdropState) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text("Indicatore a Goccia Fluida (Worm Droplet)", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.70f))
                                LiquidPageIndicator(
                                    pageCount = 5,
                                    currentPage = pagerIndicatorPage,
                                    onPageSelected = { pagerIndicatorPage = it },
                                    backdropState = backdropState,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                LiquidHorizontalDivider()
                                Text("Paginazione Numerata in Vetro", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.70f))
                                LiquidPagination(
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
                                Triple("Tutti", LiquidIcons.Home, "24"),
                                Triple("Preferiti", LiquidIcons.Star, "8"),
                                Triple("Recenti", LiquidIcons.Refresh, "12"),
                                Triple("Audio", LiquidIcons.Settings, "5")
                            ).forEach { (label, icon, count) ->
                                LiquidFilterChip(
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
                            LiquidChip(label = "Monet M3", onClick = { }, selected = true, backdropState = backdropState)
                            LiquidChip(label = "Vetro Snell", onClick = { }, onCloseClick = { }, backdropState = backdropState)
                            LiquidChip(label = "Liquid 2.0", onClick = { }, onCloseClick = { }, backdropState = backdropState)
                            LiquidChip(label = "AGSL Shaders", onClick = { }, backdropState = backdropState)
                        }
                    }
                }

                1 -> {
                    // Sotto-Schermata 2: Accordion Espandibili
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Pannelli a Fisarmonica (Accordion)")
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            LiquidAccordionItem(
                                title = "Architettura Snell & AGSL Shaders",
                                subtitle = "Riflessione e rifrazione della luce a 120 FPS",
                                leadingIcon = LiquidIcons.Info,
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

                            LiquidAccordionItem(
                                title = "Monet Dynamic Chromatic Tint",
                                subtitle = "Armonizzazione colore in tempo reale",
                                leadingIcon = LiquidIcons.Star,
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
                        LiquidSwipeToDismissBox(
                            onDismissLeft = { toastState.show("Elemento archiviato!", type = LiquidToastType.Info) },
                            onDismissRight = { toastState.show("Aggiunto ai preferiti!", type = LiquidToastType.Success) },
                            backdropState = backdropState
                        ) {
                            LiquidPreferenceItem(
                                title = "Scorri a destra o sinistra",
                                subtitle = "Trascina per visualizzare le azioni in vetro",
                                icon = LiquidIcons.Share,
                                backdropState = backdropState
                            )
                        }

                        SectionTitle("Avatar & Badge Presenza")
                        LiquidCard(backdropState = backdropState) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    LiquidAvatar(initials = "AG", presence = LiquidAvatarPresence.Online, backdropState = backdropState)
                                    LiquidAvatar(initials = "LM", presence = LiquidAvatarPresence.Busy, backdropState = backdropState)
                                    LiquidAvatar(presence = LiquidAvatarPresence.Away, backdropState = backdropState)
                                }
                                LiquidAvatarGroup(avatars = listOf("AL", "MK", "ST", "DV", "RK", "PX"), maxDisplay = 3, backdropState = backdropState)
                            }
                        }

                        SectionTitle("Stato Vuoto (Empty State)")
                        LiquidEmptyState(
                            title = "Nessun Nuovo Elemento",
                            description = "Tutte le notifiche e le attività sono state completate con successo.",
                            icon = LiquidIcons.Check,
                            actionButtonText = "Ricarica Dati",
                            onActionClick = { toastState.show("Dati aggiornati!", type = LiquidToastType.Success) },
                            backdropState = backdropState
                        )
                    }
                }

                else -> {
                    // Sotto-Schermata 4: Impostazioni & Profilo
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        LiquidCard(backdropState = backdropState) {
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                LiquidAvatar(initials = "AU", presence = LiquidAvatarPresence.Online, size = 54.dp, backdropState = backdropState)
                                Column {
                                    Text(text = "Anto Developer", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                    Text(text = "anto@liquidui.com • Pro Tier", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.70f))
                                }
                            }
                        }

                        SectionTitle("Connettività & Notifiche")
                        LiquidCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                LiquidPreferenceItem(
                                    title = "Rete Wi-Fi",
                                    subtitle = if (wifiState) "Connesso a Liquid-5G" else "Disattivato",
                                    icon = LiquidIcons.Phone,
                                    backdropState = backdropState,
                                    trailingContent = {
                                        LiquidSwitch(checked = wifiState, onCheckedChange = { wifiState = it }, backdropState = backdropState)
                                    }
                                )
                                LiquidHorizontalDivider()
                                LiquidPreferenceItem(
                                    title = "Notifiche di Sistema",
                                    subtitle = if (notificationsState) "Attive con suoni aptici" else "Silenziate",
                                    icon = LiquidIcons.Notifications,
                                    backdropState = backdropState,
                                    trailingContent = {
                                        LiquidSwitch(checked = notificationsState, onCheckedChange = { notificationsState = it }, backdropState = backdropState)
                                    }
                                )
                                LiquidHorizontalDivider()
                                LiquidPreferenceItem(
                                    title = "Modalità Vetro Scuro",
                                    subtitle = if (darkGlassState) "Contrasto e riflessi profondi" else "Vetro chiaro",
                                    icon = LiquidIcons.Star,
                                    backdropState = backdropState,
                                    trailingContent = {
                                        LiquidSwitch(checked = darkGlassState, onCheckedChange = { darkGlassState = it }, backdropState = backdropState)
                                    }
                                )
                            }
                        }

                        SectionTitle("Luminosità & Volume")
                        LiquidCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Luminosità Schermo", color = Color.White)
                                    Text("${(brightnessVal * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                LiquidSlider(value = brightnessVal, onValueChange = { brightnessVal = it }, valueRange = 0f..1f, backdropState = backdropState)
                                LiquidHorizontalDivider()
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Volume Audio", color = Color.White)
                                    Text("${(volumeVal * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                LiquidSlider(value = volumeVal, onValueChange = { volumeVal = it }, valueRange = 0f..1f, backdropState = backdropState)
                            }
                        }
                    }
                }
            }
        }
    }
}
