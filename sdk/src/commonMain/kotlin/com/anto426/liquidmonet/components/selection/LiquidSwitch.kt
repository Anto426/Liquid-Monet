package com.anto426.liquidmonet.components.selection

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.anto426.liquidmonet.components.internal.LiquidGlassToggle
import com.anto426.liquidmonet.glass.resolveLiquidGlassBackdrop

@Composable
fun LiquidSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backdropState: Backdrop = emptyBackdrop()
) {
    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdropState)
    LiquidGlassToggle(
        checked = checked,
        onCheckedChange = onCheckedChange,
        backdrop = effectiveBackdrop,
        modifier = modifier,
        enabled = enabled
    )
}
