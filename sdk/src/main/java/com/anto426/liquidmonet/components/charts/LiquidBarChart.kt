package com.anto426.liquidmonet.components.charts

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.icons.LiquidIcons
import com.anto426.liquidmonet.theme.LiquidGlassTheme
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule

/**
 * LiquidBarChart - Optical Liquid Glass Capsule Bar Chart.
 *
 * Features rounded refractive glass pill bars, Monet vertical gradients,
 * dynamic liquid surface tension meniscus, spring bounce entrance, and an
 * interactive dynamic disclosure pod beneath the chart.
 */
@Composable
fun LiquidBarChart(
    entries: List<LiquidChartEntry>,
    modifier: Modifier = Modifier,
    height: Dp = 190.dp,
    barWidth: Dp = 20.dp,
    barColor: Color? = null,
    maxValue: Float? = null,
    valueSuffix: String = "",
    valueFormatter: (Float) -> String = ::formatLiquidChartValue,
    detailDescription: String = "Valore del dato selezionato",
    showDetails: Boolean = false,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    onEntrySelected: ((LiquidChartEntry?) -> Unit)? = null
) {
    if (entries.isEmpty()) return

    val colorScheme = MaterialTheme.colorScheme
    val glassColors = LiquidGlassTheme.colors
    val primaryColor = barColor ?: colorScheme.primary
    val secondaryColor = colorScheme.tertiary
    val trackColor = LiquidGlassTheme.colors.inactiveTrack

    val dataMax = maxValue ?: ((entries.maxOfOrNull { it.value } ?: 100f) * 1.15f).coerceAtLeast(1f)
    val actualMin = entries.minOfOrNull { it.value } ?: 0f
    val actualMax = entries.maxOfOrNull { it.value } ?: 0f

    // Fluid spring entrance animation
    var animationPlayed by remember { mutableStateOf(false) }
    val progressAnimation by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.76f, stiffness = 260f),
        label = "BarChartProgress"
    )
    androidx.compose.runtime.LaunchedEffect(entries) {
        animationPlayed = true
    }

    // Shimmer sweep animation
    val infiniteTransition = rememberInfiniteTransition(label = "BarShimmerTransition")
    val shimmerPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "BarShimmerPhase"
    )

    // Selection state
    var selectedIndex by remember(entries) { mutableIntStateOf(entries.lastIndex) }
    val selectedEntry = entries[selectedIndex.coerceIn(0, entries.lastIndex)]

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Min ${valueFormatter(actualMin)}${valueSuffix.withLeadingSpace()}",
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Max ${valueFormatter(actualMax)}${valueSuffix.withLeadingSpace()}",
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant,
            )
        }

        // Canvas Viewport
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .pointerInput(entries) {
                    detectTapGestures { offset ->
                        val width = size.width
                        val totalBars = entries.size
                        val step = width / totalBars
                        val index = (offset.x / step).toInt().coerceIn(0, entries.lastIndex)
                        selectedIndex = index
                        onEntrySelected?.invoke(entries[index])
                    }
                }
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val width = size.width
                val canvasHeight = size.height
                val paddingBottom = 8.dp.toPx()
                val availableHeight = canvasHeight - paddingBottom

                val totalBars = entries.size
                val step = width / totalBars
                val actualBarWidth = (barWidth.toPx()).coerceAtMost(step * 0.65f)
                val cornerRadius = CornerRadius(actualBarWidth / 2f, actualBarWidth / 2f)

                entries.forEachIndexed { index, entry ->
                    val isSelected = index == selectedIndex
                    val centerX = step * index + step / 2f
                    val left = centerX - actualBarWidth / 2f
                    val right = centerX + actualBarWidth / 2f

                    // 1. Background Inactive Glass Track
                    val trackRect = RoundRect(
                        left = left,
                        top = 0f,
                        right = right,
                        bottom = availableHeight,
                        cornerRadius = cornerRadius
                    )
                    val trackPath = Path().apply { addRoundRect(trackRect) }
                    drawPath(path = trackPath, color = trackColor)

                    // 2. Active Liquid Bar Capsule
                    val normalized = (entry.value / dataMax).coerceIn(0f, 1f)
                    val barHeight = (normalized * availableHeight * progressAnimation).coerceAtLeast(if (entry.value > 0) actualBarWidth else 0f)
                    val barTop = availableHeight - barHeight

                    if (barHeight > 0) {
                        val barRect = RoundRect(
                            left = left,
                            top = barTop,
                            right = right,
                            bottom = availableHeight,
                            cornerRadius = cornerRadius
                        )
                        val barPath = Path().apply { addRoundRect(barRect) }

                        val activeBarColor = entry.color ?: if (isSelected) secondaryColor else primaryColor
                        
                        // Liquid gradient column
                        drawPath(
                            path = barPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    activeBarColor,
                                    activeBarColor.copy(alpha = 0.75f),
                                    secondaryColor.copy(alpha = 0.45f)
                                ),
                                startY = barTop,
                                endY = availableHeight
                            )
                        )

                        // 3. Glowing Meniscus Droplet Lens on the top cap
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = if (isSelected) 0.90f else 0.50f),
                                    activeBarColor.copy(alpha = 0.20f),
                                    Color.Transparent
                                ),
                                center = Offset(centerX, barTop + actualBarWidth / 2f),
                                radius = actualBarWidth * 0.90f
                            ),
                            radius = actualBarWidth * 0.90f,
                            center = Offset(centerX, barTop + actualBarWidth / 2f)
                        )

                        // 4. Selection Highlight Halo
                        if (isSelected) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        secondaryColor.copy(alpha = 0.50f),
                                        Color.Transparent
                                    ),
                                    center = Offset(centerX, barTop),
                                    radius = actualBarWidth * 2.2f
                                ),
                                radius = actualBarWidth * 2.2f,
                                center = Offset(centerX, barTop)
                            )
                        }
                    }
                }
            }
        }

        if (showDetails) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = entries.first().label,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant,
            )
            if (entries.size > 1) {
                Text(
                    text = entries.last().label,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurfaceVariant,
                )
            }
        }

        // Dynamic Bottom Detail Disclosure Pod (SOTTO AL GRAFICO)
        AnimatedContent(
            targetState = selectedEntry,
            transitionSpec = {
                (slideInVertically { h -> h / 2 } + fadeIn()) togetherWith
                (slideOutVertically { h -> -h / 2 } + fadeOut())
            },
            label = "BarBottomDetailTransition"
        ) { currentSelection ->
            Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(
                            backdrop = backdropState,
                            shape = RoundedCornerShape(20.dp),
                            role = LiquidGlassRole.Control,
                            containerColor = primaryColor.copy(alpha = 0.14f)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .liquidGlass(
                                        backdrop = backdropState,
                                        shape = RoundedCornerShape(10.dp),
                                        role = LiquidGlassRole.Control,
                                        containerColor = primaryColor.copy(alpha = 0.25f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = LiquidIcons.Calendar,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                Text(
                                    text = currentSelection.label,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSurface
                                )
                                Text(
                                    text = detailDescription,
                                    fontSize = 11.sp,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // CFU Bubble
                        Box(
                            modifier = Modifier
                                .liquidGlass(
                                    backdrop = backdropState,
                                    shape = Capsule(),
                                    role = LiquidGlassRole.Control,
                                    containerColor = primaryColor.copy(alpha = 0.28f)
                                )
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "${valueFormatter(currentSelection.value)}${valueSuffix.withLeadingSpace()}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = primaryColor
                            )
                        }
                    }
            }
        }
        }
    }
}

private fun String.withLeadingSpace(): String = if (isBlank()) "" else " $this"
