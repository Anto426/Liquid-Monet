package com.anto426.antoui.glass

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.anto426.antoui.glass.overlay.AntoGlassModalOverlayHost
import com.anto426.antoui.glass.overlay.AntoGlassOverlayHost
import com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop
import com.anto426.antoui.glass.overlay.LocalAntoGlassModalOverlayState
import com.anto426.antoui.glass.overlay.LocalAntoGlassOverlayState
import com.anto426.antoui.glass.overlay.rememberAntoGlassModalOverlayState
import com.anto426.antoui.glass.overlay.rememberAntoGlassOverlayState
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

@Composable
fun AntoGlassScene(
    modifier: Modifier = Modifier,
    background: @Composable BoxScope.() -> Unit = {},
    bottomBar: (@Composable BoxScope.(backdrop: Backdrop) -> Unit)? = null,
    content: @Composable BoxScope.(backdrop: LayerBackdrop) -> Unit
) {
    val backgroundBackdrop = rememberLayerBackdrop()
    val contentBackdrop = rememberLayerBackdrop()
    val overlayState = rememberAntoGlassOverlayState()
    val modalOverlayState = rememberAntoGlassModalOverlayState()

    CompositionLocalProvider(
        LocalAntoGlassOverlayState provides overlayState,
        LocalAntoGlassModalOverlayState provides modalOverlayState,
        LocalAntoGlassContentBackdrop provides contentBackdrop
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().layerBackdrop(contentBackdrop)) {
                Box(modifier = Modifier.fillMaxSize().layerBackdrop(backgroundBackdrop)) {
                    background()
                }
                content(backgroundBackdrop)
            }
            bottomBar?.invoke(this, contentBackdrop)
            AntoGlassOverlayHost(overlayState)
            AntoGlassModalOverlayHost(modalOverlayState)
        }
    }
}
