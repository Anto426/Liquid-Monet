package com.anto426.liquidmonet.components.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.liquidmonet.components.buttons.LiquidIconButton
import com.anto426.liquidmonet.components.internal.LiquidHapticCue
import com.anto426.liquidmonet.components.internal.LiquidInputNormalization
import com.anto426.liquidmonet.components.internal.performLiquidHaptic
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.resolveLiquidGlassBackdrop
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import com.anto426.liquidmonet.icons.LiquidIcons
import com.anto426.liquidmonet.motion.LiquidMotion
import com.anto426.liquidmonet.theme.LiquidGlassTheme
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * LiquidPageIndicator - Liquid Glass Dot Indicator with Asymmetric Dual-Spring Worm Droplet Dynamics.
 *
 * Slots are rendered as translucent optical glass beads inside a compact floating glass pod.
 * The active indicator is an organic liquid worm droplet that stretches dynamically along the
 * vector of motion with physical volume conservation (thinning during elongation) and
 * capillary oscillation (elastic settling bounce) upon arrival.
 */
@Composable
fun LiquidPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    onPageSelected: ((Int) -> Unit)? = null,
    dotSize: Dp = 8.dp,
    dotSpacing: Dp = 10.dp,
    backdropState: Backdrop = emptyBackdrop()
) {
    LiquidInputNormalization.positive(pageCount, "LiquidPageIndicator pageCount")
    LiquidInputNormalization.positive(dotSize, "LiquidPageIndicator dotSize")
    LiquidInputNormalization.nonNegative(dotSpacing, "LiquidPageIndicator dotSpacing")

    val safePageCount = pageCount
    val safeCurrentPage = currentPage.coerceIn(0, safePageCount - 1)
    val glassColors = LiquidGlassTheme.colors
    val performance = LocalLiquidGlassPerformance.current
    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdropState)
    val hapticFeedback = LocalHapticFeedback.current

    val headPos = remember { Animatable(safeCurrentPage.toFloat()) }
    val tailPos = remember { Animatable(safeCurrentPage.toFloat()) }
    val bounceImpulse = remember { Animatable(0f) }
    var isInitialComposition by remember { mutableStateOf(true) }

    LaunchedEffect(safeCurrentPage) {
        if (isInitialComposition) {
            isInitialComposition = false
            headPos.snapTo(safeCurrentPage.toFloat())
            tailPos.snapTo(safeCurrentPage.toFloat())
            return@LaunchedEffect
        }

        val currentHead = headPos.value
        val currentTail = tailPos.value
        val currentCenter = (currentHead + currentTail) / 2f
        val movingRight = safeCurrentPage >= currentCenter

        // Asymmetric dual-spring: leading edge rushes forward, trailing edge drags with viscous delay
        val fastSpec = LiquidMotion.spring<Float>(
            performance = performance,
            dampingRatio = 0.68f,
            stiffness = 440f
        )
        val slowSpec = LiquidMotion.spring<Float>(
            performance = performance,
            dampingRatio = 0.58f,
            stiffness = 270f
        )

        val headSpec = if (movingRight) fastSpec else slowSpec
        val tailSpec = if (movingRight) slowSpec else fastSpec

        coroutineScope {
            launch {
                headPos.animateTo(safeCurrentPage.toFloat(), animationSpec = headSpec)
            }
            launch {
                tailPos.animateTo(safeCurrentPage.toFloat(), animationSpec = tailSpec)
            }
        }

        // Capillary oscillation upon settling into spherical bead
        bounceImpulse.snapTo(1f)
        bounceImpulse.animateTo(
            targetValue = 0f,
            animationSpec = LiquidMotion.spring<Float>(
                performance = performance,
                dampingRatio = 0.52f,
                stiffness = 480f
            )
        )
    }

    val density = LocalDensity.current
    val dotSizePx = with(density) { dotSize.toPx() }
    val dotSpacingPx = with(density) { dotSpacing.toPx() }
    val pitchPx = dotSizePx + dotSpacingPx
    val horizontalPadding = 14.dp
    val horizontalPaddingPx = with(density) { horizontalPadding.toPx() }
    val podHeight = 32.dp

    val contentWidth = if (safePageCount > 1) {
        (dotSize * safePageCount) + (dotSpacing * (safePageCount - 1))
    } else {
        dotSize
    }
    val podWidth = contentWidth + (horizontalPadding * 2)

    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(podWidth)
                .height(podHeight)
                .liquidGlass(
                    backdrop = effectiveBackdrop,
                    shape = Capsule(),
                    role = LiquidGlassRole.Navigation,
                    containerColor = glassColors.neutralContainer
                )
                .then(
                    if (onPageSelected != null) {
                        Modifier.pointerInput(safePageCount, onPageSelected) {
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val relativeX = down.position.x - horizontalPaddingPx
                                var lastScrubbed = ((relativeX - dotSizePx / 2f) / pitchPx).roundToInt()
                                    .coerceIn(0, safePageCount - 1)
                                if (lastScrubbed != safeCurrentPage) {
                                    hapticFeedback.performLiquidHaptic(LiquidHapticCue.Selection)
                                    onPageSelected(lastScrubbed)
                                }

                                do {
                                    val event = awaitPointerEvent()
                                    val change = event.changes.firstOrNull() ?: break
                                    if (change.pressed) {
                                        val currentX = change.position.x - horizontalPaddingPx
                                        val scrubIdx = ((currentX - dotSizePx / 2f) / pitchPx).roundToInt()
                                            .coerceIn(0, safePageCount - 1)
                                        if (scrubIdx != lastScrubbed) {
                                            lastScrubbed = scrubIdx
                                            hapticFeedback.performLiquidHaptic(LiquidHapticCue.Selection)
                                            onPageSelected(scrubIdx)
                                        }
                                        change.consume()
                                    }
                                } while (event.changes.any { it.pressed })
                            }
                        }
                    } else {
                        Modifier
                    }
                )
                .semantics(mergeDescendants = true) {
                    role = Role.Tab
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerY = size.height / 2f
                val dotRadius = dotSizePx / 2f

                // 1. Inactive Glass Beads with Top Specular Reflection
                for (i in 0 until safePageCount) {
                    val dotCenterX = horizontalPaddingPx + dotRadius + i * pitchPx

                    // Base translucent glass circle
                    drawCircle(
                        color = glassColors.inactiveTrack,
                        radius = dotRadius,
                        center = Offset(dotCenterX, centerY)
                    )

                    // Top specular crescent highlight for realistic 3D glass bead refraction
                    drawCircle(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.40f),
                                Color.Transparent
                            ),
                            startY = centerY - dotRadius,
                            endY = centerY + dotRadius * 0.3f
                        ),
                        radius = dotRadius * 0.82f,
                        center = Offset(dotCenterX, centerY - dotRadius * 0.15f)
                    )
                }

                // 2. Active Liquid Worm Droplet
                val minP = min(headPos.value, tailPos.value)
                val maxP = max(headPos.value, tailPos.value)
                val dLeft = horizontalPaddingPx + minP * pitchPx
                val dRight = horizontalPaddingPx + dotSizePx + maxP * pitchPx
                val dWidth = (dRight - dLeft).coerceAtLeast(dotSizePx)
                val stretchRatio = (dWidth / dotSizePx).coerceAtLeast(1f)

                // Physical Volume Conservation: Droplet cross-section contracts as it stretches
                val thinFactor = (1f / sqrt(stretchRatio)).coerceIn(0.68f, 1f)
                val bounceFactor = 1f + bounceImpulse.value * 0.16f
                val dHeight = (dotSizePx * thinFactor * bounceFactor).coerceIn(dotSizePx * 0.65f, dotSizePx * 1.25f)
                val dTop = centerY - dHeight / 2f
                val dRadius = dHeight / 2f

                // A. Ambient Chromatic Liquid Glow
                val glowSpread = 2.dp.toPx()
                drawRoundRect(
                    color = primaryColor.copy(alpha = 0.25f),
                    topLeft = Offset(dLeft - glowSpread, dTop - glowSpread),
                    size = Size(dWidth + glowSpread * 2f, dHeight + glowSpread * 2f),
                    cornerRadius = CornerRadius(dRadius + glowSpread, dRadius + glowSpread)
                )

                // B. Droplet Liquid Body with Vertical Refraction Gradient
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.95f),
                            primaryColor.copy(alpha = 0.78f)
                        ),
                        startY = dTop,
                        endY = dTop + dHeight
                    ),
                    topLeft = Offset(dLeft, dTop),
                    size = Size(dWidth, dHeight),
                    cornerRadius = CornerRadius(dRadius, dRadius)
                )

                // C. Specular Caustic Highlight along Upper Rim (Zero-Black Guarantee)
                val streakInsetX = (dHeight * 0.25f).coerceAtLeast(1.5f.dp.toPx())
                val streakWidth = (dWidth - streakInsetX * 2f).coerceAtLeast(1f)
                val streakHeight = (dHeight * 0.35f).coerceAtLeast(1.2f.dp.toPx())
                val streakTop = dTop + 1.dp.toPx()
                val streakRadius = streakHeight / 2f

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.85f),
                            Color.White.copy(alpha = 0.10f)
                        ),
                        startY = streakTop,
                        endY = streakTop + streakHeight
                    ),
                    topLeft = Offset(dLeft + streakInsetX, streakTop),
                    size = Size(streakWidth, streakHeight),
                    cornerRadius = CornerRadius(streakRadius, streakRadius)
                )
            }
        }
    }
}

/**
 * LiquidPagination - Numbered page selector with equal-sized geometry and a sliding glass pill.
 */
@Composable
fun LiquidPagination(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    backdropState: Backdrop = emptyBackdrop()
) {
    LiquidInputNormalization.positive(totalPages, "LiquidPagination totalPages")
    val glassColors = LiquidGlassTheme.colors
    val performance = LocalLiquidGlassPerformance.current
    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdropState)

    val safeTotalPages = totalPages
    val safeCurrentPage = currentPage.coerceIn(1, safeTotalPages)
    val itemHeight = 50.dp
    val buttonSize = 36.dp

    Row(
        modifier = modifier
            .height(itemHeight)
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = Capsule(),
                role = LiquidGlassRole.Navigation,
                containerColor = LiquidGlassTheme.colors.neutralContainer
            )
            .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous Page Button
        LiquidIconButton(
            icon = LiquidIcons.ChevronLeft,
            onClick = {
                if (safeCurrentPage > 1) onPageChange(safeCurrentPage - 1)
            },
            enabled = safeCurrentPage > 1,
            size = buttonSize,
            backdropState = effectiveBackdrop
        )

        // Middle Number Slots with Sliding Liquid Selection Pill
        BoxWithConstraints(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            val slotWidth = maxWidth / safeTotalPages

            val selectedPillOffset by animateDpAsState(
                targetValue = slotWidth * (safeCurrentPage - 1) + (slotWidth - buttonSize) / 2f,
                animationSpec = LiquidMotion.spring(
                    performance = performance,
                    dampingRatio = 0.76f,
                    stiffness = 420f
                ),
                label = "paginationPillOffset"
            )

            // Sliding Active Selection Pill with Dynamic Monet Luminescence
            Box(
                modifier = Modifier
                    .offset(x = selectedPillOffset)
                    .size(buttonSize)
                    .clip(Capsule())
                    .background(glassColors.accentContainer)
            )

            // Equal-Width Number Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (p in 1..safeTotalPages) {
                    val isSelected = p == safeCurrentPage

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(itemHeight)
                            .selectable(
                                selected = isSelected,
                                interactionSource = remember(p) { MutableInteractionSource() },
                                indication = null,
                                role = Role.Tab,
                                onClick = { onPageChange(p) }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = p.toString(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            ),
                            color = if (isSelected) glassColors.content else glassColors.secondaryContent
                        )
                    }
                }
            }
        }

        // Next Page Button
        LiquidIconButton(
            icon = LiquidIcons.ChevronRight,
            onClick = {
                if (safeCurrentPage < safeTotalPages) onPageChange(safeCurrentPage + 1)
            },
            enabled = safeCurrentPage < safeTotalPages,
            size = buttonSize,
            backdropState = effectiveBackdrop
        )
    }
}
