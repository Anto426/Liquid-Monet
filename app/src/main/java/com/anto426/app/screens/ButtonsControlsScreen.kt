package com.anto426.app.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
import com.anto426.liquidmonet.components.selection.LiquidChip
import com.anto426.liquidmonet.components.buttons.LiquidFloatingActionButton
import com.anto426.liquidmonet.components.display.LiquidHorizontalDivider
import com.anto426.liquidmonet.components.buttons.LiquidIconButton
import com.anto426.liquidmonet.components.navigation.LiquidNavigationItem
import com.anto426.liquidmonet.components.navigation.LiquidTabBar
import com.anto426.liquidmonet.components.selection.LiquidRatingBar
import com.anto426.liquidmonet.icons.LiquidIcons
import com.kyant.backdrop.Backdrop

@Composable
fun ButtonsControlsScreen(
    backdropState: Backdrop,
    modifier: Modifier = Modifier
) {
    var selectedChip by remember { mutableStateOf("Tutti") }
    var liquidTabIndex by remember { mutableIntStateOf(0) }
    var starRating by remember { mutableIntStateOf(4) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Liquid Glass Tabs (Goccia Scorrevole Fluida)
        SectionTitle("Liquid Tabs con Goccia Ottica Scorrevole")
        LiquidTabBar(
            items = listOf(
                LiquidNavigationItem(label = "Panoramica", icon = LiquidIcons.Home),
                LiquidNavigationItem(label = "Attività", icon = LiquidIcons.Refresh, badge = "3"),
                LiquidNavigationItem(label = "Preferiti", icon = LiquidIcons.Star)
            ),
            selectedIndex = liquidTabIndex,
            onTabSelected = { liquidTabIndex = it },
            backdropState = backdropState
        )

        // Matrice Pulsanti LiquidButton
        SectionTitle("Matrice Pulsanti (10 Varianti in Vetro)")
        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Prominenti & Vetro Puro",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.70f)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LiquidButton(
                        text = "Primary",
                        onClick = { },
                        variant = LiquidButtonVariant.Primary,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                    LiquidButton(
                        text = "Secondary",
                        onClick = { },
                        variant = LiquidButtonVariant.Secondary,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LiquidButton(
                        text = "Tonal",
                        onClick = { },
                        variant = LiquidButtonVariant.Tonal,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                    LiquidButton(
                        text = "Glass Pure",
                        onClick = { },
                        variant = LiquidButtonVariant.Glass,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LiquidButton(
                        text = "Outlined",
                        onClick = { },
                        variant = LiquidButtonVariant.Outlined,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                    LiquidButton(
                        text = "Text",
                        onClick = { },
                        variant = LiquidButtonVariant.Text,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Icon Buttons & FAB
        SectionTitle("Pulsanti Icona & Azione Flottante (FAB)")
        LiquidCard(backdropState = backdropState) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LiquidIconButton(
                    icon = LiquidIcons.PlayArrow,
                    onClick = { },
                    size = 46.dp,
                    iconSize = 22.dp,
                    backdropState = backdropState
                )
                LiquidIconButton(
                    icon = LiquidIcons.Star,
                    onClick = { },
                    size = 46.dp,
                    iconSize = 22.dp,
                    backdropState = backdropState
                )
                LiquidIconButton(
                    icon = LiquidIcons.Share,
                    onClick = { },
                    size = 46.dp,
                    iconSize = 22.dp,
                    backdropState = backdropState
                )
                LiquidIconButton(
                    icon = LiquidIcons.Settings,
                    onClick = { },
                    size = 46.dp,
                    iconSize = 22.dp,
                    backdropState = backdropState
                )
                LiquidFloatingActionButton(
                    onClick = { },
                    size = 46.dp,
                    backdropState = backdropState
                ) {
                    Icon(
                        imageVector = LiquidIcons.Add,
                        contentDescription = "Nuovo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Filter Chips & Tags
        SectionTitle("Filtri & Chip Interattivi")
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            listOf(
                Triple("Tutti", LiquidIcons.Home, "24"),
                Triple("Preferiti", LiquidIcons.Star, "8"),
                Triple("Recenti", LiquidIcons.Refresh, "12"),
                Triple("Audio", LiquidIcons.Settings, "5")
            ).forEach { (label, icon, count) ->
                LiquidChip(
                    selected = selectedChip == label,
                    onClick = { selectedChip = label },
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
            LiquidChip(
                label = "Monet M3",
                onClick = { },
                selected = true,
                backdropState = backdropState
            )
            LiquidChip(
                label = "Vetro Snell",
                onClick = { },
                onCloseClick = { },
                backdropState = backdropState
            )
            LiquidChip(
                label = "Liquid 2.0",
                onClick = { },
                onCloseClick = { },
                backdropState = backdropState
            )
            LiquidChip(
                label = "AGSL Shaders",
                onClick = { },
                backdropState = backdropState
            )
        }

        // Rating Bar
        SectionTitle("Valutazione a Stelle (Rating Bar)")
        LiquidCard(backdropState = backdropState) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LiquidRatingBar(
                    rating = starRating,
                    onRatingChanged = { starRating = it },
                    backdropState = backdropState
                )
                Text(
                    text = "$starRating stelle su 5 selezionate",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.70f)
                )
            }
        }
    }
}
