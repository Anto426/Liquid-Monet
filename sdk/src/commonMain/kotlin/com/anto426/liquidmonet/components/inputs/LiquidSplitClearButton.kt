package com.anto426.liquidmonet.components.inputs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.resolveLiquidGlassBackdrop
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPresets
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import com.anto426.liquidmonet.icons.LiquidIcons
import com.anto426.liquidmonet.motion.LiquidMotion
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import kotlin.math.PI
import kotlin.math.sin

/**
 * LiquidSplitClearButton - Dedicated Optical Liquid Glass companion button for text clearing.
 *
 * Implements fluid liquid water droplet pinch-off & separation ("goccia d'acqua che si separa"):
 * As text is entered, this button buds off from the trailing edge of the input container
 * like a stretching water droplet, snapping into a discrete refracting glass bubble with
 * capillary fluid surface-tension oscillations.
 *
 * When tapped or when text is cleared, it smoothly contracts and fuses back into the parent capsule.
 */
@Composable
fun LiquidSplitClearButton(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    shape: Shape = Capsule(),
    backdropState: Backdrop = emptyBackdrop(),
    contentDescription: String = "Cancella",
    enabled: Boolean = true
) {
    val performance = LocalLiquidGlassPerformance.current
    val colorScheme = MaterialTheme.colorScheme
    val isDark = colorScheme.surface.luminance() < 0.5f

    // Organic capillary spring physics for water droplet pinch-off
    val dropletProgress by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = LiquidMotion.spring(
            performance = performance,
            dampingRatio = 0.54f, // Organic liquid water bounce
            stiffness = 300f
        ),
        label = "waterDropletProgress"
    )

    // Water droplet pinch-off and volume-conserving oscillation
    val dropPinchStretch = sin(dropletProgress.coerceIn(0f, 1f) * PI).toFloat()
    val dropletOvershoot = dropletProgress - 1f
    val dropletScaleX = (1f + dropPinchStretch * 0.22f + dropletOvershoot * 0.20f).coerceAtLeast(0.01f)
    val dropletScaleY = (1f - dropPinchStretch * 0.16f - dropletOvershoot * 0.16f).coerceAtLeast(0.01f)
    val dynamicGap = (8.dp * dropletProgress.coerceIn(0f, 1f))

    // Crystalline meniscus specular rim highlight brush aligned with glass theme (mai nero!)
    val glassRimBrush = remember(isDark) {
        Brush.linearGradient(
            colors = if (isDark) listOf(
                Color.White.copy(alpha = 0.45f),
                Color.White.copy(alpha = 0.10f),
                Color.White.copy(alpha = 0.25f)
            ) else listOf(
                Color.White.copy(alpha = 0.80f),
                Color.White.copy(alpha = 0.25f),
                Color.White.copy(alpha = 0.55f)
            )
        )
    }

    val interactionSource = remember { MutableInteractionSource() }
    val interactiveHighlight = rememberLiquidControlHighlight()
    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdropState)

    AnimatedVisibility(
        visible = visible,
        enter = expandHorizontally(
            animationSpec = LiquidMotion.spring(
                performance = performance,
                dampingRatio = 0.56f,
                stiffness = 300f
            ),
            expandFrom = Alignment.Start
        ) + fadeIn(
            animationSpec = LiquidMotion.tween(performance, 160)
        ) + scaleIn(
            animationSpec = LiquidMotion.spring(
                performance = performance,
                dampingRatio = 0.54f,
                stiffness = 300f
            ),
            initialScale = 0.4f,
            transformOrigin = TransformOrigin(0f, 0.5f)
        ),
        exit = shrinkHorizontally(
            animationSpec = LiquidMotion.spring(
                performance = performance,
                dampingRatio = 0.72f,
                stiffness = 380f
            ),
            shrinkTowards = Alignment.Start
        ) + fadeOut(
            animationSpec = LiquidMotion.tween(performance, 140)
        ) + scaleOut(
            animationSpec = LiquidMotion.spring(
                performance = performance,
                dampingRatio = 0.72f,
                stiffness = 380f
            ),
            targetScale = 0.4f,
            transformOrigin = TransformOrigin(0f, 0.5f)
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(dynamicGap))
            Box(
                modifier = modifier
                    .size(size)
                    .graphicsLayer {
                        scaleX = dropletScaleX
                        scaleY = dropletScaleY
                        transformOrigin = TransformOrigin(0f, 0.5f) // Pinches off from contact point on the left
                    }
                    .liquidGlass(
                        backdrop = effectiveBackdrop,
                        shape = shape,
                        role = LiquidGlassRole.Control,
                        preset = LiquidGlassPresets.Interactive,
                        layerBlock = liquidControlLayerBlock(enabled, interactiveHighlight)
                    )
                    .border(width = 1.dp, brush = glassRimBrush, shape = shape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        enabled = enabled,
                        onClick = onClick
                    )
                    .liquidControlPressFeedback(
                        enabled = enabled,
                        interactiveHighlight = interactiveHighlight,
                        shape = shape,
                        drawHighlightOverlay = true
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = LiquidIcons.Close,
                    contentDescription = contentDescription,
                    tint = if (isDark) Color.White.copy(alpha = 0.92f) else colorScheme.onSurface.copy(alpha = 0.80f),
                    modifier = Modifier
                        .size(size * 0.38f)
                        .graphicsLayer {
                            val iconPop = 1f + dropletOvershoot * 0.12f
                            scaleX = iconPop.coerceIn(0.7f, 1.3f)
                            scaleY = iconPop.coerceIn(0.7f, 1.3f)
                        }
                )
            }
        }
    }
}
