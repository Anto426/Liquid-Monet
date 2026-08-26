package com.anto426.liquidmonet.glass

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import com.anto426.liquidmonet.glass.overlay.LiquidGlassModalOverlayHost
import com.anto426.liquidmonet.glass.overlay.LiquidGlassOverlayHost
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassModalOverlayState
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassOverlayState
import com.anto426.liquidmonet.glass.overlay.rememberLiquidGlassModalOverlayState
import com.anto426.liquidmonet.glass.overlay.rememberLiquidGlassOverlayState
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

internal val LocalLiquidGlassTopBarScrollBehavior =
    compositionLocalOf<TopAppBarScrollBehavior?> { null }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidGlassScene(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    background: @Composable BoxScope.() -> Unit = {},
    topBar: (@Composable BoxScope.(backdrop: Backdrop) -> Unit)? = null,
    bottomBar: (@Composable BoxScope.(backdrop: Backdrop) -> Unit)? = null,
    overlay: (@Composable BoxScope.(backdrop: Backdrop) -> Unit)? = null,
    content: @Composable BoxScope.(backdrop: LayerBackdrop) -> Unit
) {
    val backgroundBackdrop = rememberLayerBackdrop()
    val contentBackdrop = rememberLayerBackdrop()
    val overlayState = rememberLiquidGlassOverlayState()
    val modalOverlayState = rememberLiquidGlassModalOverlayState()
    CompositionLocalProvider(
        LocalLiquidGlassOverlayState provides overlayState,
        LocalLiquidGlassModalOverlayState provides modalOverlayState,
        LocalLiquidGlassContentBackdrop provides backgroundBackdrop,
        LocalLiquidGlassTopBarScrollBehavior provides scrollBehavior
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .then(
                    if (scrollBehavior != null) {
                        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                    } else {
                        Modifier
                    }
                )
        ) {
            Box(modifier = Modifier.fillMaxSize().layerBackdrop(contentBackdrop)) {
                Box(modifier = Modifier.fillMaxSize().layerBackdrop(backgroundBackdrop)) {
                    background()
                }
                content(backgroundBackdrop)
            }
            CompositionLocalProvider(LocalLiquidGlassContentBackdrop provides contentBackdrop) {
                topBar?.invoke(this@Box, contentBackdrop)
                bottomBar?.invoke(this@Box, contentBackdrop)
                overlay?.invoke(this@Box, contentBackdrop)
                LiquidGlassOverlayHost(overlayState)
                LiquidGlassModalOverlayHost(modalOverlayState)
            }
        }
    }
}
