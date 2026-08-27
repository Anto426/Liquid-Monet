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
import com.anto426.liquidmonet.components.cards.LiquidAccordionItem
import com.anto426.liquidmonet.components.display.LiquidAvatar
import com.anto426.liquidmonet.components.display.LiquidAvatarGroup
import com.anto426.liquidmonet.components.display.LiquidAvatarPresence
import com.anto426.liquidmonet.components.navigation.LiquidBreadcrumbItem
import com.anto426.liquidmonet.components.navigation.LiquidBreadcrumbs
import com.anto426.liquidmonet.components.cards.LiquidCard
import com.anto426.liquidmonet.components.display.LiquidEmptyState
import com.anto426.liquidmonet.components.display.LiquidSectionHeader
import com.anto426.liquidmonet.components.navigation.LiquidPageIndicator
import com.anto426.liquidmonet.components.navigation.LiquidPagination
import com.anto426.liquidmonet.components.cards.LiquidPreferenceItem
import com.anto426.liquidmonet.components.display.LiquidSwipeToDismissBox
import com.anto426.liquidmonet.components.feedback.LiquidToastState
import com.anto426.liquidmonet.components.feedback.LiquidToastType
import com.anto426.liquidmonet.icons.LiquidIcons
import com.kyant.backdrop.Backdrop

@Composable
fun NavigationGesturesScreen(
    toastState: LiquidToastState,
    backdropState: Backdrop,
    modifier: Modifier = Modifier
) {
    var accordion1Open by remember { mutableStateOf(false) }
    var accordion2Open by remember { mutableStateOf(true) }
    var paginationPage by remember { mutableIntStateOf(2) }
    var pagerIndicatorPage by remember { mutableIntStateOf(1) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Breadcrumbs Trail
        LiquidSectionHeader("Percorso & Briciole di Pane (Breadcrumbs)")
        LiquidBreadcrumbs(
            items = listOf(
                LiquidBreadcrumbItem("Home") { toastState.show("Navigato a Home", type = LiquidToastType.Info) },
                LiquidBreadcrumbItem("Impostazioni") { toastState.show("Navigato a Impostazioni", type = LiquidToastType.Info) },
                LiquidBreadcrumbItem("Sicurezza & Accesso") { toastState.show("Navigato a Sicurezza", type = LiquidToastType.Info) },
                LiquidBreadcrumbItem("Attuale")
            ),
            backdropState = backdropState
        )

        // Accordion Expandable Panels
        LiquidSectionHeader("Pannelli Espandibili (Accordion)")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LiquidAccordionItem(
                title = "Architettura Snell & AGSL Shaders",
                subtitle = "Dettagli sulla riflessione e rifrazione della luce",
                leadingIcon = LiquidIcons.Info,
                isExpanded = accordion1Open,
                onExpandedChange = { accordion1Open = it },
                backdropState = backdropState
            ) {
                Text(
                    text = "Lo shader AGSL campiona il backdrop calcolando la dispersione cromatica e le ombre interne per simulare un vetro reale a 60/120 fps.",
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
                    text = "Ogni componente adatta la luminosità del vetro e la specular highlight al tema di sistema attivo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.80f)
                )
            }
        }

        // Avatar & Presence
        LiquidSectionHeader("Avatar & Badge Presenza Liquid Glass")
        LiquidCard(backdropState = backdropState) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LiquidAvatar(initials = "AG", presence = LiquidAvatarPresence.Online, backdropState = backdropState)
                    LiquidAvatar(initials = "LM", presence = LiquidAvatarPresence.Busy, backdropState = backdropState)
                    LiquidAvatar(presence = LiquidAvatarPresence.Away, backdropState = backdropState)
                }

                LiquidAvatarGroup(
                    avatars = listOf("AL", "MK", "ST", "DV", "RK", "PX"),
                    maxDisplay = 3,
                    backdropState = backdropState
                )
            }
        }

        // Paginazione & Pager Indicator
        LiquidSectionHeader("Paginazione & Indicatori Pager")
        LiquidCard(backdropState = backdropState) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                LiquidPageIndicator(
                    pageCount = 5,
                    currentPage = pagerIndicatorPage,
                    onPageSelected = { pagerIndicatorPage = it },
                    backdropState = backdropState,
                    modifier = Modifier.fillMaxWidth()
                )
                LiquidPagination(
                    currentPage = paginationPage,
                    totalPages = 5,
                    onPageChange = { paginationPage = it },
                    backdropState = backdropState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Swipe to Action
        LiquidSectionHeader("Gesto Swipe to Dismiss / Action")
        LiquidSwipeToDismissBox(
            onDismissLeft = {
                toastState.show("Elemento archiviato!", type = LiquidToastType.Info)
            },
            onDismissRight = {
                toastState.show("Aggiunto ai preferiti!", type = LiquidToastType.Success)
            },
            backdropState = backdropState
        ) {
            LiquidPreferenceItem(
                title = "Scorri a destra o sinistra",
                subtitle = "Trascina per visualizzare le azioni rapide in vetro",
                icon = LiquidIcons.Share,
                backdropState = backdropState
            )
        }

        // Empty State Placeholder
        LiquidSectionHeader("Stato Vuoto (Empty State)")
        LiquidEmptyState(
            title = "Nessun Nuovo Elemento",
            description = "Tutte le notifiche e le attività sono state completate con successo.",
            icon = LiquidIcons.Check,
            actionButtonText = "Ricarica Dati",
            onActionClick = {
                toastState.show("Dati aggiornati con successo!", type = LiquidToastType.Success)
            },
            backdropState = backdropState
        )
    }
}
