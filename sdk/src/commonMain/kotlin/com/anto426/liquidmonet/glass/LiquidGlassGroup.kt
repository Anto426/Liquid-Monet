package com.anto426.liquidmonet.glass

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPreset
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.RoundedRectangle

/**
 * Groups nearby, functionally related controls behind one sampled optical surface.
 *
 * Use one group for a toolbar cluster, segmented control, media controls or another local family.
 * Do not group distant elements merely because they have the same Kotlin component type.
 * [LiquidGlassContainer] remains as the source-compatible name for the same rendering contract.
 */
@Composable
fun LiquidGlassGroup(
    modifier: Modifier = Modifier,
    backdropState: Backdrop = emptyBackdrop(),
    shape: Shape = RoundedRectangle(24.dp),
    role: LiquidGlassRole = LiquidGlassRole.Surface,
    containerColor: Color? = null,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    preset: LiquidGlassPreset? = null,
    contentAlignment: Alignment = Alignment.TopStart,
    backdropPolicy: LiquidGlassBackdropPolicy = LiquidGlassBackdropPolicy.SceneFirst,
    mode: LiquidGlassContainerMode = LiquidGlassContainerMode.Shared,
    content: @Composable BoxScope.() -> Unit
) = LiquidGlassContainer(
    modifier = modifier,
    backdropState = backdropState,
    shape = shape,
    role = role,
    containerColor = containerColor,
    contentColor = contentColor,
    preset = preset,
    contentAlignment = contentAlignment,
    backdropPolicy = backdropPolicy,
    mode = mode,
    content = content
)
