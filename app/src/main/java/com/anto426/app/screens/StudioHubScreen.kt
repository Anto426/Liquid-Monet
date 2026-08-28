package com.anto426.app.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.sp
import com.anto426.liquidmonet.components.cards.LiquidCard
import com.anto426.liquidmonet.components.cards.LiquidControlCenterTile
import com.anto426.liquidmonet.components.display.LiquidHorizontalDivider
import com.anto426.liquidmonet.components.display.LiquidSectionHeader
import com.anto426.liquidmonet.components.navigation.LiquidNavigationItem
import com.anto426.liquidmonet.components.navigation.LiquidTabBar
import com.anto426.liquidmonet.components.cards.LiquidMediaController
import com.anto426.liquidmonet.components.pickers.LiquidPaletteOption
import com.anto426.liquidmonet.components.pickers.LiquidPaletteSelector
import com.anto426.liquidmonet.components.selection.LiquidBackgroundSelector
import com.anto426.liquidmonet.components.selection.LiquidSlider
import com.anto426.liquidmonet.components.cards.LiquidStatusCard
import com.anto426.liquidmonet.components.cards.LiquidStatusType
import com.anto426.liquidmonet.glass.LiquidBackgroundEffect
import com.anto426.liquidmonet.icons.LiquidIcons
import com.anto426.liquidmonet.theme.monet.LiquidMonetPresets
import com.kyant.backdrop.Backdrop
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun StudioHubScreen(
    selectedPresetIndex: Int,
    onSelectPreset: (Int) -> Unit,
    sliderVal: Float,
    onSliderChange: (Float) -> Unit,
    backdropState: Backdrop,
    modifier: Modifier = Modifier,
    selectedEffect: LiquidBackgroundEffect = LiquidBackgroundEffect.RadiantBeam,
    onSelectEffect: (LiquidBackgroundEffect) -> Unit = {}
) {
    var currentSubTab by remember { mutableIntStateOf(0) }
    val subTabs = listOf(
        LiquidNavigationItem("Panoramica"),
        LiquidNavigationItem("Media Player"),
        LiquidNavigationItem("Control Center")
    )

    var wifiActive by remember { mutableStateOf(true) }
    var bluetoothActive by remember { mutableStateOf(false) }
    var airplaneActive by remember { mutableStateOf(false) }
    var torchActive by remember { mutableStateOf(false) }

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

        when (currentSubTab) {
                0 -> {
                    // Sotto-Schermata 1: Panoramica & Monet
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        LiquidSectionHeader(
                            title = "Personalizzazione Cromatica Monet",
                            subtitle = "Scegli palette dinamica, sfondo e intensità dell'ottica di vetro."
                        )
                        LiquidCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text(
                                    text = "Armonie Cromatiche di Sistema",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
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
                                    Text("Intensità Vetro Rifrattivo", style = MaterialTheme.typography.bodyMedium)
                                    Text("${(sliderVal * 100).toInt()}%", fontWeight = FontWeight.Bold)
                                }
                                LiquidSlider(
                                    value = sliderVal,
                                    onValueChange = onSliderChange,
                                    valueRange = 0f..1f,
                                    backdropState = backdropState
                                )

                                LiquidHorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                Text(
                                    text = "Sfondo Ottico Dinamico",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                )
                                LiquidBackgroundSelector(
                                    selectedEffect = selectedEffect,
                                    onEffectSelected = onSelectEffect,
                                    backdropState = backdropState
                                )
                            }
                        }

                        LiquidSectionHeader(
                            title = "Stato dei Servizi",
                            subtitle = "Controlla in un colpo d'occhio rendering, sincronizzazione e risorse."
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            LiquidStatusCard(
                                title = "Pipeline Grafica",
                                description = "Shaders AGSL e accelerazione hardware attivi a 120 FPS.",
                                statusType = LiquidStatusType.Success,
                                backdropState = backdropState
                            )
                            LiquidStatusCard(
                                title = "Cache Shader",
                                description = "Ottimizzazione rendering completata con successo.",
                                statusType = LiquidStatusType.Info,
                                backdropState = backdropState
                            )
                        }
                    }
                }

                1 -> {
                    // Sotto-Schermata 2: Media Player
                    StudioMediaPlayerDemo(backdropState = backdropState)
                }

                else -> {
                    // Sotto-Schermata 3: Control Center
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        LiquidSectionHeader(
                            title = "Control Center Rapido",
                            subtitle = "Attiva le funzioni principali con controlli compatti e risposta immediata."
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            LiquidControlCenterTile(
                                title = "Rete Wi-Fi",
                                subtitle = if (wifiActive) "Connesso • Liquid-5G" else "Disattivato",
                                icon = LiquidIcons.Phone,
                                active = wifiActive,
                                onClick = { wifiActive = !wifiActive },
                                backdropState = backdropState
                            )
                            LiquidControlCenterTile(
                                title = "Bluetooth",
                                subtitle = if (bluetoothActive) "Dispositivi accoppiati" else "Non attivo",
                                icon = LiquidIcons.Settings,
                                active = bluetoothActive,
                                onClick = { bluetoothActive = !bluetoothActive },
                                backdropState = backdropState
                            )
                            LiquidControlCenterTile(
                                title = "Modalità Aereo",
                                subtitle = if (airplaneActive) "Tutte le radio disattivate" else "Connessioni attive",
                                icon = LiquidIcons.Info,
                                active = airplaneActive,
                                onClick = { airplaneActive = !airplaneActive },
                                backdropState = backdropState
                            )
                            LiquidControlCenterTile(
                                title = "Torcia Prismatica",
                                subtitle = if (torchActive) "Luce attiva al 100%" else "Spenta",
                                icon = LiquidIcons.Star,
                                active = torchActive,
                                onClick = { torchActive = !torchActive },
                                backdropState = backdropState
                            )
                        }
                    }
                }
        }
    }
}

@Composable
private fun StudioMediaPlayerDemo(backdropState: Backdrop) {
    var isPlayingMusic by remember { mutableStateOf(true) }
    val progressState = remember { mutableFloatStateOf(0.35f) }
    val currentTimeState = remember { mutableStateOf(formatMediaTime(progressState.floatValue)) }

    // Keep the original smooth media-player update cadence for the demo.
    LaunchedEffect(isPlayingMusic) {
        if (isPlayingMusic) {
            while (true) {
                delay(60L)
                progressState.floatValue =
                    if (progressState.floatValue >= 1f) 0f else progressState.floatValue + 0.0025f
            }
        }
    }

    LaunchedEffect(isPlayingMusic) {
        if (isPlayingMusic) {
            while (true) {
                delay(1000L)
                currentTimeState.value = formatMediaTime(progressState.floatValue)
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        LiquidSectionHeader(
            title = "Player Multimediale Liquid Glass",
            subtitle = "Riproduzione, avanzamento e controlli riuniti in una superficie rifrattiva."
        )
        LiquidMediaController(
            title = "Cosmic Aurora",
            artist = "Electronic Soundscape • Lossless",
            isPlaying = isPlayingMusic,
            progress = 0.35f,
            currentTime = "1:18",
            totalTime = "3:45",
            onPlayPauseClick = { isPlayingMusic = !isPlayingMusic },
            onPreviousClick = {
                progressState.floatValue = 0.0f
                currentTimeState.value = formatMediaTime(0.0f)
            },
            onNextClick = {
                val nextProgress = (progressState.floatValue + 0.25f).coerceAtMost(0.95f)
                progressState.floatValue = nextProgress
                currentTimeState.value = formatMediaTime(nextProgress)
            },
            progressState = progressState,
            currentTimeState = currentTimeState,
            backdropState = backdropState
        )

        LiquidCard(backdropState = backdropState) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Dettagli Traccia & Audio Engine",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )
                Text(
                    text = "Codec: FLAC 24-bit / 96 kHz • Uscita: Audio Spaziale Prismatico con riverbero in vetro liquido.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

private fun formatMediaTime(progress: Float): String {
    val elapsedSeconds = (progress * 225).toInt()
    return String.format(
        Locale.getDefault(),
        "%d:%02d",
        elapsedSeconds / 60,
        elapsedSeconds % 60
    )
}
