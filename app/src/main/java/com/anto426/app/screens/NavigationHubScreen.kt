package com.anto426.app.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
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
import com.anto426.liquidmonet.components.display.LiquidSectionHeader
import com.anto426.liquidmonet.components.layout.LiquidAnimatedSwitcher
import com.anto426.liquidmonet.components.layout.LiquidLazyFooter
import com.anto426.liquidmonet.components.layout.LiquidLazyFooterOrientation
import com.anto426.liquidmonet.components.layout.LiquidLazyFooterState
import com.anto426.liquidmonet.components.layout.LiquidSwitcherTransition
import com.anto426.liquidmonet.components.navigation.LiquidBreadcrumbItem
import com.anto426.liquidmonet.components.navigation.LiquidBreadcrumbs
import com.anto426.liquidmonet.components.cards.LiquidCard
import com.anto426.liquidmonet.components.selection.LiquidChip
import com.anto426.liquidmonet.components.selection.LiquidChipSelectionGroup
import com.anto426.liquidmonet.components.display.LiquidEmptyState
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
    var selectedChips by remember { mutableStateOf(setOf("Monet M3", "AGSL Shaders")) }
    var paginationPage by remember { mutableIntStateOf(2) }
    var pagerIndicatorPage by remember { mutableIntStateOf(1) }
    var switcherPage by remember { mutableIntStateOf(0) }
    var switcherForward by remember { mutableStateOf(true) }
    var switcherTransition by remember {
        mutableStateOf(LiquidSwitcherTransition.DirectionalHorizontal)
    }
    var footerState by remember { mutableStateOf(LiquidLazyFooterState.Loading) }

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
                        LiquidSectionHeader(
                            title = "Percorso Breadcrumbs",
                            subtitle = "Mostra posizione e livelli attraversabili dentro una gerarchia."
                        )
                        LiquidBreadcrumbs(
                            items = listOf(
                                LiquidBreadcrumbItem("Home") { toastState.show("Navigato a Home", type = LiquidToastType.Info) },
                                LiquidBreadcrumbItem("Impostazioni") { toastState.show("Navigato a Impostazioni", type = LiquidToastType.Info) },
                                LiquidBreadcrumbItem("Sicurezza & Accesso") { toastState.show("Navigato a Sicurezza", type = LiquidToastType.Info) },
                                LiquidBreadcrumbItem("Attuale")
                            ),
                            backdropState = backdropState
                        )

                        LiquidSectionHeader(
                            title = "Indicatori Pager & Paginazione",
                            subtitle = "Rendi evidente la pagina corrente e consenti il salto diretto."
                        )
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

                        LiquidSectionHeader(
                            title = "Cambio Contenuto Animato",
                            subtitle = "Passa tra viste con continuità, direzione e inerzia liquide."
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            listOf(
                                "Orizzontale" to LiquidSwitcherTransition.DirectionalHorizontal,
                                "Verticale" to LiquidSwitcherTransition.DirectionalVertical,
                                "Morfosi liquida" to LiquidSwitcherTransition.LiquidMorph
                            ).forEach { (label, transition) ->
                                LiquidChip(
                                    label = label,
                                    selected = switcherTransition == transition,
                                    onClick = { switcherTransition = transition }
                                )
                            }
                        }

                        LiquidCard(backdropState = backdropState) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                LiquidAnimatedSwitcher(
                                    targetState = switcherPage,
                                    transition = switcherTransition,
                                    isForward = { _, _ -> switcherForward },
                                    onSwipeForward = {
                                        switcherForward = true
                                        switcherPage = (switcherPage + 1) % 3
                                    },
                                    onSwipeBackward = {
                                        switcherForward = false
                                        switcherPage = (switcherPage + 2) % 3
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 92.dp),
                                    label = "switcherDemo"
                                ) { page ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = when (page) {
                                                0 -> LiquidIcons.Home
                                                1 -> LiquidIcons.Star
                                                else -> LiquidIcons.Settings
                                            },
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = when (page) {
                                                0 -> "Panoramica Monet"
                                                1 -> "Preferiti in vetro"
                                                else -> "Impostazioni ottiche"
                                            },
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Text(
                                    text = "Scorri direttamente a destra o sinistra",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
                                )
                            }
                        }

                        LiquidSectionHeader(
                            title = "Selezione Multipla Chip",
                            subtitle = "Seleziona singoli filtri oppure applicali e rimuovili tutti insieme."
                        )
                        LiquidChipSelectionGroup(
                            items = listOf("Monet M3", "Vetro Snell", "Liquid 2.0", "AGSL Shaders"),
                            selectedItems = selectedChips,
                            onSelectionChange = { selectedChips = it },
                            label = { it },
                            leadingIcon = {
                                when (it) {
                                    "Monet M3" -> LiquidIcons.Home
                                    "Vetro Snell" -> LiquidIcons.Star
                                    "Liquid 2.0" -> LiquidIcons.Refresh
                                    else -> LiquidIcons.Settings
                                }
                            },
                            badge = {
                                when (it) {
                                    "Monet M3" -> "24"
                                    "Vetro Snell" -> "8"
                                    "Liquid 2.0" -> "12"
                                    else -> "5"
                                }
                            },
                            allLabel = "Tutti",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                1 -> {
                    // Sotto-Schermata 2: Accordion Espandibili
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        LiquidSectionHeader(
                            title = "Pannelli a Fisarmonica (Accordion)",
                            subtitle = "Rivela informazioni secondarie mantenendo compatta la schermata."
                        )
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
                        LiquidSectionHeader(
                            title = "Gesto Swipe to Dismiss / Action",
                            subtitle = "Scorri l'elemento per scoprire azioni contestuali e completarle."
                        )
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

                        LiquidSectionHeader(
                            title = "Avatar & Badge Presenza",
                            subtitle = "Identità, stato e gruppi rimangono leggibili anche in poco spazio."
                        )
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

                        LiquidSectionHeader(
                            title = "Stato Vuoto (Empty State)",
                            subtitle = "Spiega l'assenza di contenuti e propone il prossimo passo."
                        )
                        LiquidEmptyState(
                            title = "Nessun Nuovo Elemento",
                            description = "Tutte le notifiche e le attività sono state completate con successo.",
                            icon = LiquidIcons.Check,
                            actionButtonText = "Ricarica Dati",
                            onActionClick = { toastState.show("Dati aggiornati!", type = LiquidToastType.Success) },
                            backdropState = backdropState
                        )

                        LiquidSectionHeader(
                            title = "Footer Lazy Verticale & Orizzontale",
                            subtitle = "Conclude LazyColumn e LazyRow con stati liquidi, rimbalzo e azione di recupero."
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            listOf(
                                "Nascosto" to LiquidLazyFooterState.Hidden,
                                "Caricamento" to LiquidLazyFooterState.Loading,
                                "Fine" to LiquidLazyFooterState.End,
                                "Errore" to LiquidLazyFooterState.Error
                            ).forEach { (label, state) ->
                                LiquidChip(
                                    label = label,
                                    selected = footerState == state,
                                    onClick = { footerState = state }
                                )
                            }
                        }

                        Text(
                            text = "LazyColumn: scorri il riquadro fino al footer",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                        )
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                count = 5,
                                key = { index -> "vertical-footer-demo-$index" }
                            ) { index ->
                                LiquidCard(
                                    backdropState = backdropState,
                                    contentPadding = 12.dp
                                ) {
                                    Text(
                                        text = "Elemento verticale ${index + 1}",
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            item(key = "vertical-liquid-footer") {
                                LiquidLazyFooter(
                                    state = footerState,
                                    orientation = LiquidLazyFooterOrientation.Vertical,
                                    loadingLabel = "Caricamento elementi…",
                                    endLabel = "Hai raggiunto la fine",
                                    errorLabel = "Caricamento non riuscito",
                                    retryLabel = "Riprova",
                                    onRetry = { footerState = LiquidLazyFooterState.Loading }
                                )
                            }
                        }

                        Text(
                            text = "LazyRow: scorri orizzontalmente fino al footer",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                        )
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(112.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                count = 4,
                                key = { index -> "horizontal-footer-demo-$index" }
                            ) { index ->
                                LiquidCard(
                                    modifier = Modifier.width(164.dp),
                                    backdropState = backdropState,
                                    contentPadding = 12.dp
                                ) {
                                    Text(
                                        text = "Elemento orizzontale ${index + 1}",
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            item(key = "horizontal-liquid-footer") {
                                LiquidLazyFooter(
                                    state = footerState,
                                    orientation = LiquidLazyFooterOrientation.Horizontal,
                                    loadingLabel = "Caricamento…",
                                    endLabel = "Fine elenco",
                                    errorLabel = "Errore",
                                    retryLabel = "Riprova",
                                    onRetry = { footerState = LiquidLazyFooterState.Loading },
                                    modifier = Modifier.height(112.dp)
                                )
                            }
                        }
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

                        LiquidSectionHeader(
                            title = "Connettività & Notifiche",
                            subtitle = "Gestisci servizi e avvisi con la gerarchia delle impostazioni Android."
                        )
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

                        LiquidSectionHeader(
                            title = "Luminosità & Volume",
                            subtitle = "Regola i livelli principali e leggine subito il valore corrente."
                        )
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
