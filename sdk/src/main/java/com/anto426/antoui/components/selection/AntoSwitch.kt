package com.anto426.antoui.components.selection

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.catalog.components.AntoGlassToggle

@Composable
fun AntoSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    AntoGlassToggle(
        selected = { checked },
        onSelect = onCheckedChange,
        backdrop = backdropState,
        modifier = modifier
    )
}
