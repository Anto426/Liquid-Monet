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
import com.anto426.liquidmonet.components.selection.LiquidSlider
import com.anto426.liquidmonet.components.cards.LiquidStatusCard
import com.anto426.liquidmonet.components.cards.LiquidStatusType
import com.anto426.liquidmonet.icons.LiquidIcons
import com.kyant.backdrop.Backdrop
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
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
    val musicCurrentTime = "${elapsedSeconds / 60}:${(elapsedSeconds % 60).toString().padStart(2, '0')}"

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Glass Intensity
        LiquidSectionHeader("Ottica Vetro Dinamica")
        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Intensità Vetro Liquido", style = MaterialTheme.typography.bodyMedium)
                    Text("${(sliderVal * 100).toInt()}%", fontWeight = FontWeight.Bold)
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            LiquidControlCenterTile(
                title = "Rete Wi-Fi",
                subtitle = if (wifiActive) "5 GHz" else "Spento",
                icon = LiquidIcons.Phone,
                active = wifiActive,
                onClick = { wifiActive = !wifiActive },
                backdropState = backdropState,
                modifier = Modifier.weight(1f)
            )
            LiquidControlCenterTile(
                title = "Bluetooth",
                subtitle = if (bluetoothActive) "Connesso" else "Spento",
                icon = LiquidIcons.Settings,
                active = bluetoothActive,
                onClick = { bluetoothActive = !bluetoothActive },
                backdropState = backdropState,
                modifier = Modifier.weight(1f)
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
