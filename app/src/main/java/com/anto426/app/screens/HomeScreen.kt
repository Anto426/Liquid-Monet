package com.anto426.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.sp
import com.anto426.antoui.components.cards.AntoCard
import com.anto426.antoui.components.cards.AntoControlCenterTile
import com.anto426.antoui.components.display.AntoHorizontalDivider
import com.anto426.antoui.components.cards.AntoMediaController
import com.anto426.antoui.components.pickers.AntoMonetPaletteSelector
import com.anto426.antoui.components.selection.AntoSlider
import com.anto426.antoui.components.cards.AntoStatusCard
import com.anto426.antoui.components.cards.AntoStatusType
import com.anto426.antoui.icons.AntoIcons
import com.anto426.antoui.theme.monet.AntoMonetPresets
import com.kyant.backdrop.Backdrop
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    selectedPresetIndex: Int,
    onSelectPreset: (Int) -> Unit,
    sliderVal: Float,
    onSliderChange: (Float) -> Unit,
    backdropState: Backdrop,
    modifier: Modifier = Modifier
) {
    var isPlayingMusic by remember { mutableStateOf(true) }
    var progressVal by remember { mutableFloatStateOf(0.35f) }
    var wifiActive by remember { mutableStateOf(true) }
    var bluetoothActive by remember { mutableStateOf(false) }

    LaunchedEffect(isPlayingMusic) {
        if (isPlayingMusic) {
            while (true) {
                delay(60L)
                progressVal = if (progressVal >= 1f) 0f else progressVal + 0.0025f
            }
        }
    }

    val elapsedSeconds = (progressVal * 225).toInt()
    val musicCurrentTime = String.format(Locale.getDefault(), "%d:%02d", elapsedSeconds / 60, elapsedSeconds % 60)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Monet Palette & Glass Intensity
        SectionTitle("Personalizzazione & Ottica Vetro")
        AntoCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Armonie Cromatiche Monet",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                AntoMonetPaletteSelector(
                    seeds = listOf(
                        "Sapphire" to AntoMonetPresets.Sapphire,
                        "Emerald" to AntoMonetPresets.Emerald,
                        "Sunset" to AntoMonetPresets.Sunset,
                        "Violet" to AntoMonetPresets.Violet
                    ),
                    selectedIndex = selectedPresetIndex,
                    onSelectIndex = onSelectPreset,
                    backdropState = backdropState
                )

                AntoHorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Intensità Vetro Liquido", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
                    Text("${(sliderVal * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                AntoSlider(
                    value = sliderVal,
                    onValueChange = onSliderChange,
                    valueRange = 0f..1f,
                    backdropState = backdropState
                )
            }
        }

        // Media Player
        SectionTitle("Player Multimediale")
        AntoMediaController(
            title = "Cosmic Aurora",
            artist = "Electronic Soundscape",
            isPlaying = isPlayingMusic,
            progress = progressVal,
            currentTime = musicCurrentTime,
            totalTime = "3:45",
            onPlayPauseClick = { isPlayingMusic = !isPlayingMusic },
            onPreviousClick = { progressVal = 0.0f },
            onNextClick = { progressVal = (progressVal + 0.25f).coerceAtMost(0.95f) },
            backdropState = backdropState
        )

        // Control Center
        SectionTitle("Control Center Rapido")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AntoControlCenterTile(
                title = "Rete Wi-Fi",
                subtitle = if (wifiActive) "Connesso • 5 GHz" else "Disattivato",
                icon = AntoIcons.Phone,
                active = wifiActive,
                onClick = { wifiActive = !wifiActive },
                backdropState = backdropState
            )
            AntoControlCenterTile(
                title = "Bluetooth",
                subtitle = if (bluetoothActive) "Dispositivi connessi" else "Non attivo",
                icon = AntoIcons.Settings,
                active = bluetoothActive,
                onClick = { bluetoothActive = !bluetoothActive },
                backdropState = backdropState
            )
        }

        // Status Cards
        SectionTitle("Stato di Sistema")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AntoStatusCard(
                title = "Sistema Operativo",
                description = "Pipeline grafica AGSL e rendering attivi a 120 FPS.",
                statusType = AntoStatusType.Success,
                backdropState = backdropState
            )
            AntoStatusCard(
                title = "Spazio in Esaurimento",
                description = "Rimangono meno di 2 GB di spazio disponibile su dispositivo.",
                statusType = AntoStatusType.Warning,
                backdropState = backdropState
            )
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.90f),
        modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
    )
}
