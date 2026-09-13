package com.anto426.liquidmonet.components.cards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.resolveLiquidGlassBackdrop
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPresets
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import com.anto426.liquidmonet.motion.LiquidMotion
import com.anto426.liquidmonet.theme.LiquidGlassTheme
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle

/**
 * LiquidControlCenterSlider - Interactive Optical Liquid Glass Capsule Slider for Quick Settings.
 * Supports smooth horizontal touch dragging, fluid Monet fill illumination, and specular glass rim highlights.
 */
@Composable
fun LiquidControlCenterSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    enabled: Boolean = true,
    backdropState: Backdrop = emptyBackdrop()
) {
    val performance = LocalLiquidGlassPerformance.current
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary
    val glassColors = LiquidGlassTheme.colors
    val inactiveContentColor = glassColors.content
    val isDark = colorScheme.surface.luminance() < 0.5f

    val shape = RoundedRectangle(22.dp)
    val iconShape = Capsule()

    val rangeSpan = (valueRange.endInclusive - valueRange.start).let { if (it <= 0f) 1f else it }
    val normalizedFraction = ((value - valueRange.start) / rangeSpan).coerceIn(0f, 1f)

    val animatedFraction by animateFloatAsState(
        targetValue = normalizedFraction,
        animationSpec = LiquidMotion.spring(
            performance = performance,
            dampingRatio = 0.72f,
            stiffness = 400f
        ),
        label = "sliderFillProgress"
    )

    var isInteracting by remember { mutableStateOf(false) }

    val glassRimBrush = remember(isDark) {
        Brush.linearGradient(
            colors = if (isDark) listOf(
                Color.White.copy(alpha = 0.38f),
                Color.White.copy(alpha = 0.08f),
                Color.White.copy(alpha = 0.22f)
            ) else listOf(
                Color.White.copy(alpha = 0.75f),
                Color.White.copy(alpha = 0.25f),
                Color.White.copy(alpha = 0.50f)
            )
        )
    }

    val activeGlassRimBrush = remember(isDark, primaryColor) {
        Brush.linearGradient(
            colors = if (isDark) listOf(
                primaryColor.copy(alpha = 0.70f),
                primaryColor.copy(alpha = 0.40f),
                Color.White.copy(alpha = 0.20f),
                primaryColor.copy(alpha = 0.55f)
            ) else listOf(
                primaryColor.copy(alpha = 0.55f),
                primaryColor.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.45f),
                primaryColor.copy(alpha = 0.45f)
            )
        )
    }

    val fillBrush = remember(isDark, primaryColor) {
        Brush.horizontalGradient(
            colors = if (isDark) listOf(
                primaryColor.copy(alpha = 0.65f),
                primaryColor.copy(alpha = 0.85f)
            ) else listOf(
                primaryColor.copy(alpha = 0.52f),
                primaryColor.copy(alpha = 0.72f)
            )
        )
    }

    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdropState)

    BoxWithConstraints(
        modifier = modifier
            .height(64.dp)
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = if (isInteracting) 1.01f else 1.0f
                scaleY = if (isInteracting) 0.985f else 1.0f
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            }
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = LiquidGlassRole.Control,
                preset = LiquidGlassPresets.Navigation,
                containerColor = null
            )
            .border(
                width = 1.dp,
                brush = if (normalizedFraction > 0.05f) activeGlassRimBrush else glassRimBrush,
                shape = shape
            )
            .pointerInput(enabled, valueRange) {
                if (!enabled) return@pointerInput
                detectTapGestures(
                    onPress = {
                        isInteracting = true
                        tryAwaitRelease()
                        isInteracting = false
                    },
                    onTap = { offset ->
                        val newFraction = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                        val newValue = valueRange.start + newFraction * rangeSpan
                        onValueChange(newValue)
                    }
                )
            }
            .pointerInput(enabled, valueRange) {
                if (!enabled) return@pointerInput
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        isInteracting = true
                        val newFraction = (offset.x / size.width.toFloat()).coerceIn(0f, 1f)
                        val newValue = valueRange.start + newFraction * rangeSpan
                        onValueChange(newValue)
                    },
                    onDragEnd = { isInteracting = false },
                    onDragCancel = { isInteracting = false },
                    onHorizontalDrag = { change, _ ->
                        change.consume()
                        val newFraction = (change.position.x / size.width.toFloat()).coerceIn(0f, 1f)
                        val newValue = valueRange.start + newFraction * rangeSpan
                        onValueChange(newValue)
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        val totalWidth = maxWidth

        // Luminous Monet Fluid Fill Bar
        if (animatedFraction > 0f) {
            Box(
                modifier = Modifier
                    .width(totalWidth * animatedFraction)
                    .fillMaxHeight()
                    .background(fillBrush, shape)
            )
        }

        // Content Row: Icon Pod + Title + Percentage
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        if (normalizedFraction > 0.05f) primaryColor
                        else (if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.65f)),
                        iconShape
                    )
                    .border(
                        width = 0.5.dp,
                        brush = if (normalizedFraction > 0.05f) activeGlassRimBrush else glassRimBrush,
                        shape = iconShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (normalizedFraction > 0.05f) Color.White else (if (isDark) Color.White.copy(alpha = 0.85f) else inactiveContentColor.copy(alpha = 0.85f)),
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                BasicText(
                    text = title,
                    style = TextStyle(
                        color = inactiveContentColor,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                BasicText(
                    text = "${(normalizedFraction * 100).toInt()}%",
                    style = TextStyle(
                        color = if (normalizedFraction > 0.05f) (if (isDark) Color.White.copy(alpha = 0.95f) else primaryColor) else glassColors.secondaryContent,
                        fontSize = 12.sp,
                        fontWeight = if (normalizedFraction > 0.05f) FontWeight.SemiBold else FontWeight.Normal
                    )
                )
            }
        }
    }
}
