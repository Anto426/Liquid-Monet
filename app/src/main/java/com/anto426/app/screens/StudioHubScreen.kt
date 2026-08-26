package com.anto426.app.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import com.anto426.liquidmonet.components.navigation.LiquidNavigationItem
import com.anto426.liquidmonet.components.navigation.LiquidTabBar
import com.anto426.liquidmonet.components.cards.LiquidMediaController
import com.anto426.liquidmonet.components.pickers.LiquidMonetPaletteSelector
import com.anto426.liquidmonet.components.selection.LiquidBackgroundSelector
import com.anto426.liquidmonet.components.selection.LiquidSlider
import com.anto426.liquidmonet.components.cards.LiquidStatusCard
import com.anto426.liquidmonet.components.cards.LiquidStatusType
import com.anto426.liquidmonet.glass.LiquidBackgroundEffect
import com.anto426.liquidmonet.motion.LiquidAnimatedNavContent
import com.anto426.liquidmonet.motion.LiquidNavTransition
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

    var isPlayingMusic by remember { mutableStateOf(true) }
    var progressVal by remember { mutableFloatStateOf(0.35f) }
    var wifiActive by remember { mutableStateOf(true) }
    var bluetoothActive by remember { mutableStateOf(false) }
    var airplaneActive by remember { mutableStateOf(false) }
    var torchActive by remember { mutableStateOf(false) }

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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sub-Navigation Liquid Tabs
        LiquidTabBar(
            items = subTabs,
            selectedIndex = currentSubTab,
            onTabSelected = { currentSubTab = it },
            backdropState = backdropState
        )

        LiquidAnimatedNavContent(
            targetState = currentSubTab,
            transition = LiquidNavTransition.AutoDirectional,
            label = "studioSubTabTransition"
        ) { tab ->
            when (tab) {
                0 -> {
                    // Sotto-Schermata 1: Panoramica & Monet
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Personalizzazione Cromatica Monet")
                        LiquidCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text(
                                    text = "Armonie Cromatiche di Sistema",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                LiquidMonetPaletteSelector(
                                    seeds = listOf(
                                        "Sapphire" to LiquidMonetPresets.Sapphire,
                                        "Emerald" to LiquidMonetPresets.Emerald,
                                        "Sunset" to LiquidMonetPresets.Sunset,
                                        "Violet" to LiquidMonetPresets.Violet
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
                                    Text("Intensità Vetro Rifrattivo", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
                                    Text("${(sliderVal * 100).toInt()}%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
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
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                LiquidBackgroundSelector(
                                    selectedEffect = selectedEffect,
                                    onEffectSelected = onSelectEffect,
                                    backdropState = backdropState
                                )
                            }
                        }

                        SectionTitle("Stato dei Servizi")
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
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Player Multimediale Liquid Glass")
                        LiquidMediaController(
                            title = "Cosmic Aurora",
                            artist = "Electronic Soundscape • Lossless",
                            isPlaying = isPlayingMusic,
                            progress = progressVal,
                            currentTime = musicCurrentTime,
                            totalTime = "3:45",
                            onPlayPauseClick = { isPlayingMusic = !isPlayingMusic },
                            onPreviousClick = { progressVal = 0.0f },
                            onNextClick = { progressVal = (progressVal + 0.25f).coerceAtMost(0.95f) },
                            backdropState = backdropState
                        )

                        LiquidCard(backdropState = backdropState) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Dettagli Traccia & Audio Engine",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Codec: FLAC 24-bit / 96 kHz • Uscita: Audio Spaziale Prismatico con riverbero in vetro liquido.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }
                        }
                    }
                }

                else -> {
                    // Sotto-Schermata 3: Control Center
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        SectionTitle("Control Center Rapido")
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
}
