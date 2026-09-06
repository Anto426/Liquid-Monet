package com.anto426.liquidmonet.components.menu

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.components.navigation.LiquidTopBarAction
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.resolveLiquidGlassBackdrop
import com.anto426.liquidmonet.glass.overlay.LiquidGlassDropdownPlacement
import com.anto426.liquidmonet.glass.overlay.liquidGlassOverlayAnchor
import com.anto426.liquidmonet.glass.overlay.rememberLiquidGlassOverlayAnchorState
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import com.anto426.liquidmonet.motion.LiquidMotion
import com.kyant.backdrop.Backdrop
import com.kyant.shapes.Capsule

/** A top-bar action using the same dropdown lifecycle and interaction as every other menu. */
@Composable
fun LiquidMorphingAction(
    action: LiquidTopBarAction,
    isLastItem: Boolean,
    backdropState: Backdrop,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val anchorState = rememberLiquidGlassOverlayAnchorState()
    val performance = LocalLiquidGlassPerformance.current
    val anchorShape = remember { Capsule() }
    val interactiveHighlight = rememberLiquidControlHighlight()
    val isMorphing = expanded && action.subItems.isNotEmpty()
    val buttonAlpha by animateFloatAsState(
        targetValue = if (isMorphing) 0f else 1f,
        animationSpec = LiquidMotion.tween(performance, 280),
        label = "buttonAlpha"
    )
    val buttonScale by animateFloatAsState(
        targetValue = if (isMorphing) 0.94f else 1f,
        animationSpec = LiquidMotion.menuBounceSpring(performance),
        label = "buttonScale"
    )
    val iconRotation by animateFloatAsState(
        targetValue = action.iconRotation + if (expanded) 90f else 0f,
        animationSpec = LiquidMotion.spring(performance, dampingRatio = 0.58f, stiffness = 300f),
        label = "iconRotation"
    )

    Box(modifier = modifier.padding(start = 4.dp, end = if (isLastItem) 12.dp else 0.dp)) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .liquidGlassOverlayAnchor(anchorState)
                .graphicsLayer {
                    alpha = buttonAlpha
                    scaleX = buttonScale
                    scaleY = buttonScale
                }
                .liquidGlass(
                    backdrop = resolveLiquidGlassBackdrop(backdropState),
                    shape = anchorShape,
                    role = LiquidGlassRole.Control,
                    layerBlock = liquidControlLayerBlock(true, interactiveHighlight, stretchFactor = 0.35f, translationFactor = 0.35f)
                )
                .liquidControlPressFeedback(true, interactiveHighlight, shape = anchorShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (action.subItems.isEmpty()) action.onClick() else expanded = !expanded
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp).graphicsLayer { rotationZ = iconRotation }
            )
        }
        LiquidDropdownMenu(
            expanded = isMorphing,
            onDismissRequest = { expanded = false },
            modifier = Modifier.widthIn(min = 190.dp, max = 260.dp),
            anchorState = anchorState,
            placement = LiquidGlassDropdownPlacement.AnchorTopEnd,
            offset = DpOffset.Zero,
            backdropState = backdropState
        ) {
            action.subItems.forEach { item ->
                LiquidMenuItem(
                    text = item.label,
                    icon = item.icon,
                    selected = item.selected,
                    onClick = {
                        expanded = false
                        item.onClick()
                    }
                )
            }
        }
    }
}
