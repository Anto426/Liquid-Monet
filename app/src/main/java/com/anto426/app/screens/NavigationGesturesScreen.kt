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
import com.anto426.antoui.components.cards.AntoAccordionItem
import com.anto426.antoui.components.display.AntoAvatar
import com.anto426.antoui.components.display.AntoAvatarGroup
import com.anto426.antoui.components.display.AntoAvatarPresence
import com.anto426.antoui.components.navigation.AntoBreadcrumbItem
import com.anto426.antoui.components.navigation.AntoBreadcrumbs
import com.anto426.antoui.components.cards.AntoCard
import com.anto426.antoui.components.display.AntoEmptyState
import com.anto426.antoui.components.navigation.AntoPageIndicator
import com.anto426.antoui.components.navigation.AntoPagination
import com.anto426.antoui.components.cards.AntoPreferenceItem
import com.anto426.antoui.components.display.AntoSwipeToDismissBox
import com.anto426.antoui.components.feedback.AntoToastState
import com.anto426.antoui.components.feedback.AntoToastType
import com.anto426.antoui.icons.AntoIcons
import com.kyant.backdrop.Backdrop

@Composable
fun NavigationGesturesScreen(
    toastState: AntoToastState,
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
        SectionTitle("Percorso & Briciole di Pane (Breadcrumbs)")
        AntoBreadcrumbs(
            items = listOf(
                AntoBreadcrumbItem("Home") { toastState.show("Navigato a Home", type = AntoToastType.Info) },
                AntoBreadcrumbItem("Impostazioni") { toastState.show("Navigato a Impostazioni", type = AntoToastType.Info) },
                AntoBreadcrumbItem("Sicurezza & Accesso") { toastState.show("Navigato a Sicurezza", type = AntoToastType.Info) },
                AntoBreadcrumbItem("Attuale")
            ),
            backdropState = backdropState
        )

        // Accordion Expandable Panels
        SectionTitle("Pannelli Espandibili (Accordion)")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AntoAccordionItem(
                title = "Architettura Snell & AGSL Shaders",
                subtitle = "Dettagli sulla riflessione e rifrazione della luce",
                leadingIcon = AntoIcons.Info,
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

            AntoAccordionItem(
                title = "Monet Dynamic Chromatic Tint",
                subtitle = "Armonizzazione colore in tempo reale",
                leadingIcon = AntoIcons.Star,
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
        SectionTitle("Avatar & Badge Presenza Liquid Glass")
        AntoCard(backdropState = backdropState) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AntoAvatar(initials = "AG", presence = AntoAvatarPresence.Online, backdropState = backdropState)
                    AntoAvatar(initials = "LM", presence = AntoAvatarPresence.Busy, backdropState = backdropState)
                    AntoAvatar(presence = AntoAvatarPresence.Away, backdropState = backdropState)
                }

                AntoAvatarGroup(
                    avatars = listOf("AL", "MK", "ST", "DV", "RK", "PX"),
                    maxDisplay = 3,
                    backdropState = backdropState
                )
            }
        }

        // Paginazione & Pager Indicator
        SectionTitle("Paginazione & Indicatori Pager")
        AntoCard(backdropState = backdropState) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AntoPageIndicator(
                    pageCount = 5,
                    currentPage = pagerIndicatorPage,
                    onPageSelected = { pagerIndicatorPage = it },
                    backdropState = backdropState,
                    modifier = Modifier.fillMaxWidth()
                )
                AntoPagination(
                    currentPage = paginationPage,
                    totalPages = 5,
                    onPageChange = { paginationPage = it },
                    backdropState = backdropState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Swipe to Action
        SectionTitle("Gesto Swipe to Dismiss / Action")
        AntoSwipeToDismissBox(
            onDismissLeft = {
                toastState.show("Elemento archiviato!", type = AntoToastType.Info)
            },
            onDismissRight = {
                toastState.show("Aggiunto ai preferiti!", type = AntoToastType.Success)
            },
            backdropState = backdropState
        ) {
            AntoPreferenceItem(
                title = "Scorri a destra o sinistra",
                subtitle = "Trascina per visualizzare le azioni rapide in vetro",
                icon = AntoIcons.Share,
                backdropState = backdropState
            )
        }

        // Empty State Placeholder
        SectionTitle("Stato Vuoto (Empty State)")
        AntoEmptyState(
            title = "Nessun Nuovo Elemento",
            description = "Tutte le notifiche e le attività sono state completate con successo.",
            icon = AntoIcons.Check,
            actionButtonText = "Ricarica Dati",
            onActionClick = {
                toastState.show("Dati aggiornati con successo!", type = AntoToastType.Success)
            },
            backdropState = backdropState
        )
    }
}
