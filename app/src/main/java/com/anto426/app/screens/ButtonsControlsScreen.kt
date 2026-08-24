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
import com.anto426.antoui.components.buttons.AntoButton
import com.anto426.antoui.components.buttons.AntoButtonVariant
import com.anto426.antoui.components.cards.AntoCard
import com.anto426.antoui.components.selection.AntoChip
import com.anto426.antoui.components.selection.AntoFilterChip
import com.anto426.antoui.components.buttons.AntoFloatingActionButton
import com.anto426.antoui.components.display.AntoHorizontalDivider
import com.anto426.antoui.components.buttons.AntoIconButton
import com.anto426.antoui.components.navigation.AntoLiquidTabRow
import com.anto426.antoui.components.selection.AntoRatingBar
import com.anto426.antoui.components.navigation.AntoTabData
import com.anto426.antoui.icons.AntoIcons
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
        AntoLiquidTabRow(
            tabs = listOf(
                AntoTabData("Panoramica", AntoIcons.Home),
                AntoTabData("Attività", AntoIcons.Refresh, badge = "3"),
                AntoTabData("Preferiti", AntoIcons.Star)
            ),
            selectedIndex = liquidTabIndex,
            onTabSelected = { liquidTabIndex = it },
            backdropState = backdropState
        )

        // Matrice Pulsanti AntoButton
        SectionTitle("Matrice Pulsanti (10 Varianti in Vetro)")
        AntoCard(backdropState = backdropState) {
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
                    AntoButton(
                        text = "Primary",
                        onClick = { },
                        variant = AntoButtonVariant.Primary,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                    AntoButton(
                        text = "Secondary",
                        onClick = { },
                        variant = AntoButtonVariant.Secondary,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AntoButton(
                        text = "Tonal",
                        onClick = { },
                        variant = AntoButtonVariant.Tonal,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                    AntoButton(
                        text = "Glass Pure",
                        onClick = { },
                        variant = AntoButtonVariant.Glass,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AntoButton(
                        text = "Outlined",
                        onClick = { },
                        variant = AntoButtonVariant.Outlined,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                    AntoButton(
                        text = "Text",
                        onClick = { },
                        variant = AntoButtonVariant.Text,
                        backdropState = backdropState,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Icon Buttons & FAB
        SectionTitle("Pulsanti Icona & Azione Flottante (FAB)")
        AntoCard(backdropState = backdropState) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AntoIconButton(
                    icon = AntoIcons.PlayArrow,
                    onClick = { },
                    size = 46.dp,
                    iconSize = 22.dp,
                    backdropState = backdropState
                )
                AntoIconButton(
                    icon = AntoIcons.Star,
                    onClick = { },
                    size = 46.dp,
                    iconSize = 22.dp,
                    backdropState = backdropState
                )
                AntoIconButton(
                    icon = AntoIcons.Share,
                    onClick = { },
                    size = 46.dp,
                    iconSize = 22.dp,
                    backdropState = backdropState
                )
                AntoIconButton(
                    icon = AntoIcons.Settings,
                    onClick = { },
                    size = 46.dp,
                    iconSize = 22.dp,
                    backdropState = backdropState
                )
                AntoFloatingActionButton(
                    onClick = { },
                    size = 46.dp,
                    backdropState = backdropState
                ) {
                    Icon(
                        imageVector = AntoIcons.Add,
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
            AntoChip(
                label = "Monet M3",
                onClick = { },
                selected = true,
                backdropState = backdropState
            )
            AntoChip(
                label = "Vetro Snell",
                onClick = { },
                onCloseClick = { },
                backdropState = backdropState
            )
            AntoChip(
                label = "Liquid 2.0",
                onClick = { },
                onCloseClick = { },
                backdropState = backdropState
            )
            AntoChip(
                label = "AGSL Shaders",
                onClick = { },
                backdropState = backdropState
            )
        }

        // Rating Bar
        SectionTitle("Valutazione a Stelle (Rating Bar)")
        AntoCard(backdropState = backdropState) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AntoRatingBar(
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
