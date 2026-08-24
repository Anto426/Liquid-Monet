package com.anto426.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anto426.antoui.components.display.AntoAvatar
import com.anto426.antoui.components.display.AntoAvatarPresence
import com.anto426.antoui.components.cards.AntoCard
import com.anto426.antoui.components.display.AntoHorizontalDivider
import com.anto426.antoui.components.cards.AntoPreferenceItem
import com.anto426.antoui.components.selection.AntoSlider
import com.anto426.antoui.components.selection.AntoSwitch
import com.anto426.antoui.icons.AntoIcons
import com.kyant.backdrop.Backdrop

@Composable
fun SettingsScreen(
    backdropState: Backdrop,
    modifier: Modifier = Modifier
) {
    var wifiState by remember { mutableStateOf(true) }
    var notificationsState by remember { mutableStateOf(true) }
    var darkGlassState by remember { mutableStateOf(true) }
    var brightnessVal by remember { mutableFloatStateOf(0.75f) }
    var volumeVal by remember { mutableFloatStateOf(0.60f) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Card
        AntoCard(backdropState = backdropState) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AntoAvatar(
                    initials = "AU",
                    presence = AntoAvatarPresence.Online,
                    size = 54.dp,
                    backdropState = backdropState
                )

                Column {
                    Text(
                        text = "Anto Developer",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "anto@liquidui.com • Pro Tier",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.70f)
                    )
                }
            }
        }

        // Connettività & Notifiche
        SectionTitle("Connettività & Notifiche")
        AntoCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AntoPreferenceItem(
                    title = "Rete Wi-Fi",
                    subtitle = if (wifiState) "Connesso a Liquid-5G" else "Disattivato",
                    icon = AntoIcons.Phone,
                    trailingContent = {
                        AntoSwitch(
                            checked = wifiState,
                            onCheckedChange = { wifiState = it },
                            backdropState = backdropState
                        )
                    }
                )

                AntoHorizontalDivider()

                AntoPreferenceItem(
                    title = "Notifiche di Sistema",
                    subtitle = if (notificationsState) "Attive con suoni aptici" else "Silenziate",
                    icon = AntoIcons.Notifications,
                    trailingContent = {
                        AntoSwitch(
                            checked = notificationsState,
                            onCheckedChange = { notificationsState = it },
                            backdropState = backdropState
                        )
                    }
                )

                AntoHorizontalDivider()

                AntoPreferenceItem(
                    title = "Modalità Vetro Scuro",
                    subtitle = if (darkGlassState) "Contrasto e riflessi profondi" else "Vetro chiaro",
                    icon = AntoIcons.Star,
                    trailingContent = {
                        AntoSwitch(
                            checked = darkGlassState,
                            onCheckedChange = { darkGlassState = it },
                            backdropState = backdropState
                        )
                    }
                )
            }
        }

        // Audio & Luminosità
        SectionTitle("Luminosità & Volume Audio")
        AntoCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Luminosità Schermo", color = Color.White)
                    Text("${(brightnessVal * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                AntoSlider(
                    value = brightnessVal,
                    onValueChange = { brightnessVal = it },
                    valueRange = 0f..1f,
                    backdropState = backdropState
                )

                AntoHorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Volume Audio", color = Color.White)
                    Text("${(volumeVal * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                AntoSlider(
                    value = volumeVal,
                    onValueChange = { volumeVal = it },
                    valueRange = 0f..1f,
                    backdropState = backdropState
                )
            }
        }

        // Info SDK & Diagnostica
        SectionTitle("Informazioni SDK & Rendering")
        AntoCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AntoPreferenceItem(
                    title = "Runtime Grafico",
                    subtitle = "AGSL Skia Hardware Acceleration",
                    icon = AntoIcons.Info
                )
                AntoPreferenceItem(
                    title = "Versione AntoUI",
                    subtitle = "2.0.0 Expressive Liquid Glass Edition",
                    icon = AntoIcons.Check
                )
            }
        }
    }
}
