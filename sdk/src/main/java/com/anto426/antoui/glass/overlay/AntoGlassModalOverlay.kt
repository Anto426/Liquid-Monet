package com.anto426.antoui.glass.overlay

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

/**
 * Scene-local portal for modal glass.
 *
 * Dialogs and sheets registered here are drawn outside the layer recorded by
 * `AntoGlassScene`. They can therefore sample the complete base UI without recording
 * themselves back into the same backdrop.
 */
@Stable
internal class AntoGlassModalOverlayState {
    private val entries = mutableStateListOf<AntoGlassModalEntry>()

    val activeEntry: AntoGlassModalEntry?
        get() = entries.lastOrNull()

    fun show(
        key: Any,
        content: @Composable () -> Unit
    ) {
        val existingIndex = entries.indexOfFirst { it.key == key }
        val entry = AntoGlassModalEntry(key = key, content = content)
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

internal val LocalAntoGlassModalOverlayState =
    staticCompositionLocalOf<AntoGlassModalOverlayState?> { null }

@Composable
internal fun rememberAntoGlassModalOverlayState(): AntoGlassModalOverlayState =
    remember { AntoGlassModalOverlayState() }

@Composable
internal fun BoxScope.AntoGlassModalOverlayHost(state: AntoGlassModalOverlayState) {
    val entry = state.activeEntry ?: return
    key(entry.key) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .zIndex(2_000_000f)
        ) {
            entry.content()
        }
    }
}

internal data class AntoGlassModalEntry(
    val key: Any,
    val content: @Composable () -> Unit
)
