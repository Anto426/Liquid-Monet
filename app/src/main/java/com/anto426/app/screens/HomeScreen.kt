package com.anto426.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.components.cards.LiquidCard
import com.anto426.liquidmonet.components.cards.LiquidControlCenterTile
import com.anto426.liquidmonet.components.display.LiquidHorizontalDivider
import com.anto426.liquidmonet.components.display.LiquidSectionHeader
import com.anto426.liquidmonet.components.cards.LiquidMediaController
import com.anto426.liquidmonet.components.pickers.LiquidPaletteOption
import com.anto426.liquidmonet.components.pickers.LiquidPaletteSelector
import com.anto426.liquidmonet.components.selection.LiquidSlider
import com.anto426.liquidmonet.components.cards.LiquidStatusCard
import com.anto426.liquidmonet.components.cards.LiquidStatusType
import com.anto426.liquidmonet.icons.LiquidIcons
import com.anto426.liquidmonet.theme.monet.LiquidMonetPresets
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
        LiquidSectionHeader("Personalizzazione & Ottica Vetro")
        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Armonie Cromatiche Monet",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                LiquidPaletteSelector(
                    options = listOf(
                        LiquidPaletteOption("Sapphire", LiquidMonetPresets.Sapphire.lightPrimary),
                        LiquidPaletteOption("Emerald", LiquidMonetPresets.Emerald.lightPrimary),
                        LiquidPaletteOption("Sunset", LiquidMonetPresets.Sunset.lightPrimary),
                        LiquidPaletteOption("Violet", LiquidMonetPresets.Violet.lightPrimary)
                    ),
                    selectedIndex = selectedPresetIndex,
                    onSelectIndex = onSelectPreset,
                    backdropState = backdropState
                )

                LiquidHorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Intensità Vetro Liquido", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
                    Text("${(sliderVal * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                LiquidSlider(
                    value = sliderVal,
                    onValueChange = onSliderChange,
                    valueRange = 0f..1f,
                    backdropState = backdropState
                )
            }
        }

        // Media Player
        LiquidSectionHeader("Player Multimediale")
        LiquidMediaController(
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
        LiquidSectionHeader("Control Center Rapido")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            LiquidControlCenterTile(
                title = "Rete Wi-Fi",
                subtitle = if (wifiActive) "Connesso • 5 GHz" else "Disattivato",
                icon = LiquidIcons.Phone,
                active = wifiActive,
                onClick = { wifiActive = !wifiActive },
                backdropState = backdropState
            )
            LiquidControlCenterTile(
                title = "Bluetooth",
                subtitle = if (bluetoothActive) "Dispositivi connessi" else "Non attivo",
                icon = LiquidIcons.Settings,
                active = bluetoothActive,
                onClick = { bluetoothActive = !bluetoothActive },
                backdropState = backdropState
            )
        }

        // Status Cards
        LiquidSectionHeader("Stato di Sistema")
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            LiquidStatusCard(
                title = "Sistema Operativo",
                description = "Pipeline grafica AGSL e rendering attivi a 120 FPS.",
                statusType = LiquidStatusType.Success,
                backdropState = backdropState
            )
            LiquidStatusCard(
                title = "Spazio in Esaurimento",
                description = "Rimangono meno di 2 GB di spazio disponibile su dispositivo.",
                statusType = LiquidStatusType.Warning,
                backdropState = backdropState
            )
        }
    }
}
