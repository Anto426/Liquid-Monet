package com.anto426.liquidmonet.glass.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.anto426.liquidmonet.glass.LiquidGlassZIndex

/**
 * Scene-local portal for modal glass.
 *
 * Dialogs and sheets registered here are drawn outside the layer recorded by
 * `LiquidGlassScene`. They can therefore sample the complete base UI without recording
 * themselves back into the same backdrop.
 */
@Stable
internal class LiquidGlassModalOverlayState {
    private val entries = mutableStateListOf<LiquidGlassModalEntry>()

    val activeEntry: LiquidGlassModalEntry?
        get() = entries.lastOrNull()

    fun show(
        key: Any,
        content: @Composable () -> Unit
    ) {
        val existingIndex = entries.indexOfFirst { it.key == key }
        val entry = LiquidGlassModalEntry(key = key, content = content)
        if (existingIndex >= 0) {
            entries[existingIndex] = entry
        } else {
            entries += entry
        }
    }

    fun remove(key: Any) {
        entries.removeAll { it.key == key }
    }
}

internal val LocalLiquidGlassModalOverlayState =
    staticCompositionLocalOf<LiquidGlassModalOverlayState?> { null }

@Composable
internal fun rememberLiquidGlassModalOverlayState(): LiquidGlassModalOverlayState =
    remember { LiquidGlassModalOverlayState() }

@Composable
internal fun BoxScope.LiquidGlassModalOverlayHost(state: LiquidGlassModalOverlayState) {
    val entry = state.activeEntry ?: return
    key(entry.key) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .zIndex(LiquidGlassZIndex.Modal)
        ) {
            entry.content()
        }
    }
}

internal data class LiquidGlassModalEntry(
    val key: Any,
    val content: @Composable () -> Unit
)
