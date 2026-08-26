package com.anto426.liquidmonet.components.selection

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.catalog.components.LiquidGlassToggle

@Composable
fun LiquidSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    LiquidGlassToggle(
        checked = checked,
        onCheckedChange = onCheckedChange,
        backdrop = backdropState,
        modifier = modifier,
        enabled = enabled
    )
}
