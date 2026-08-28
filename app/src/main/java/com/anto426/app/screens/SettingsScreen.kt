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
import com.anto426.liquidmonet.components.display.LiquidAvatar
import com.anto426.liquidmonet.components.display.LiquidAvatarPresence
import com.anto426.liquidmonet.components.cards.LiquidCard
import com.anto426.liquidmonet.components.display.LiquidHorizontalDivider
import com.anto426.liquidmonet.components.display.LiquidSectionHeader
import com.anto426.liquidmonet.components.cards.LiquidPreferenceItem
import com.anto426.liquidmonet.components.selection.LiquidSlider
import com.anto426.liquidmonet.components.selection.LiquidSwitch
import com.anto426.liquidmonet.icons.LiquidIcons
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
        LiquidCard(backdropState = backdropState) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LiquidAvatar(
                    initials = "AU",
                    presence = LiquidAvatarPresence.Online,
                    size = 54.dp,
                    backdropState = backdropState
                )

                Column {
                    Text(
                        text = "Anto Developer",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "anto@liquidui.com • Pro Tier",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Connettività & Notifiche
        LiquidSectionHeader(
            title = "Connettività & Notifiche",
            subtitle = "Configura reti, avvisi e aspetto del vetro di sistema."
        )
        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LiquidPreferenceItem(
                    title = "Rete Wi-Fi",
                    subtitle = if (wifiState) "Connesso a Liquid-5G" else "Disattivato",
                    icon = LiquidIcons.Phone,
                    trailingContent = {
                        LiquidSwitch(
                            checked = wifiState,
                            onCheckedChange = { wifiState = it },
                            backdropState = backdropState
                        )
                    }
                )

                LiquidHorizontalDivider()

                LiquidPreferenceItem(
                    title = "Notifiche di Sistema",
                    subtitle = if (notificationsState) "Attive con suoni aptici" else "Silenziate",
                    icon = LiquidIcons.Notifications,
                    trailingContent = {
                        LiquidSwitch(
                            checked = notificationsState,
                            onCheckedChange = { notificationsState = it },
                            backdropState = backdropState
                        )
                    }
                )

                LiquidHorizontalDivider()

                LiquidPreferenceItem(
                    title = "Modalità Vetro Scuro",
                    subtitle = if (darkGlassState) "Contrasto e riflessi profondi" else "Vetro chiaro",
                    icon = LiquidIcons.Star,
                    trailingContent = {
                        LiquidSwitch(
                            checked = darkGlassState,
                            onCheckedChange = { darkGlassState = it },
                            backdropState = backdropState
                        )
                    }
                )
            }
        }

        // Audio & Luminosità
        LiquidSectionHeader(
            title = "Luminosità & Volume Audio",
            subtitle = "Regola i livelli principali del dispositivo."
        )
        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Luminosità Schermo")
                    Text("${(brightnessVal * 100).toInt()}%", fontWeight = FontWeight.Bold)
                }
                LiquidSlider(
                    value = brightnessVal,
                    onValueChange = { brightnessVal = it },
                    valueRange = 0f..1f,
                    backdropState = backdropState
                )

                LiquidHorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Volume Audio")
                    Text("${(volumeVal * 100).toInt()}%", fontWeight = FontWeight.Bold)
                }
                LiquidSlider(
                    value = volumeVal,
                    onValueChange = { volumeVal = it },
                    valueRange = 0f..1f,
                    backdropState = backdropState
                )
            }
        }

        // Info SDK & Diagnostica
        LiquidSectionHeader(
            title = "Informazioni SDK & Rendering",
            subtitle = "Versione, pipeline grafica e capacità attive."
        )
        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LiquidPreferenceItem(
                    title = "Runtime Grafico",
                    subtitle = "AGSL Skia Hardware Acceleration",
                    icon = LiquidIcons.Info
                )
                LiquidPreferenceItem(
                    title = "Versione Liquid Monet",
                    subtitle = "2.0.0 Expressive Liquid Glass Edition",
                    icon = LiquidIcons.Check
                )
            }
        }
    }
}
