package com.anto426.liquidmonet.components.selection

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.anto426.liquidmonet.glass.LiquidBackgroundEffect
import com.anto426.liquidmonet.icons.LiquidIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop

/** Selects one of the Liquid Monet scene background effects through the canonical select menu. */
@Composable
fun LiquidBackgroundSelector(
    selectedEffect: LiquidBackgroundEffect,
    onEffectSelected: (LiquidBackgroundEffect) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val effects = listOf(
        Triple(
            LiquidBackgroundEffect.RadiantBeam,
            "Radiant Beam",
            "Spotlight con griglia sub-pixel ad alta precisione"
        ),
        Triple(
            LiquidBackgroundEffect.Aurora,
            "Cosmic Aurora",
            "Onde fluide boreali a luminescenza dinamica"
        ),
        Triple(
            LiquidBackgroundEffect.MeshGlow,
            "Mesh Glow",
            "Sfumature organiche e gradienti diffusi"
        ),
        Triple(
            LiquidBackgroundEffect.OrbitalPulse,
            "Orbital Pulse",
            "Anelli concentrici ad espansione armonica"
        )
    )

    LiquidSelect(
        items = effects.map { it.first },
        selectedItem = selectedEffect,
        onItemSelected = onEffectSelected,
        label = "Effetto di Sfondo Dinamico",
        itemLabel = { effect -> effects.first { it.first == effect }.second },
        itemSubtitle = { effect -> effects.first { it.first == effect }.third },
        itemIcon = { effect ->
            when (effect) {
                LiquidBackgroundEffect.RadiantBeam -> LiquidIcons.Refresh
                LiquidBackgroundEffect.Aurora -> LiquidIcons.Star
                LiquidBackgroundEffect.MeshGlow -> LiquidIcons.Info
                LiquidBackgroundEffect.OrbitalPulse -> LiquidIcons.Settings
            }
        },
        modifier = modifier,
        backdropState = backdropState
    )
}
