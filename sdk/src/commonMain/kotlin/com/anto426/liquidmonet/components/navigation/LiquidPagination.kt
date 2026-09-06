package com.anto426.liquidmonet.components.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.anto426.liquidmonet.components.buttons.LiquidIconButton
import com.anto426.liquidmonet.components.internal.LiquidInputNormalization
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.resolveLiquidGlassBackdrop
import com.anto426.liquidmonet.motion.LiquidMotion
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import com.anto426.liquidmonet.icons.LiquidIcons
import com.anto426.liquidmonet.theme.LiquidGlassTheme
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule

/**
 * LiquidPageIndicator - Liquid Glass Dot Indicator with Dynamic Color Aura & Sliding Droplet Dynamics.
 * Slots are distributed equally across the bar with a sliding liquid droplet matching navbar physics.
 */
@Composable
fun LiquidPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    onPageSelected: ((Int) -> Unit)? = null,
    dotSize: Dp = 10.dp,
    backdropState: Backdrop = emptyBackdrop()
) {
    LiquidInputNormalization.positive(pageCount, "LiquidPageIndicator pageCount")
    LiquidInputNormalization.positive(dotSize, "LiquidPageIndicator dotSize")
    val glassColors = LiquidGlassTheme.colors
    val performance = LocalLiquidGlassPerformance.current
    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdropState)

    val safePageCount = pageCount
    val safeCurrentPage = currentPage.coerceIn(0, safePageCount - 1)
    val movementImpulse = remember { Animatable(0f) }

    LaunchedEffect(safeCurrentPage) {
        movementImpulse.snapTo(1f)
        movementImpulse.animateTo(
            targetValue = 0f,
            animationSpec = LiquidMotion.spring(
                performance = performance,
                dampingRatio = 0.72f,
                stiffness = 430f
            )
        )
    }

    Box(
        modifier = modifier
            .height(50.dp)
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = Capsule(),
                role = LiquidGlassRole.Navigation,
                containerColor = LiquidGlassTheme.colors.neutralContainer
            )
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            val slotWidth = maxWidth / safePageCount
            val activeWidth = dotSize * 1.5f

            val slidingOffset by animateDpAsState(
                targetValue = slotWidth * safeCurrentPage + (slotWidth - activeWidth) / 2f,
                animationSpec = LiquidMotion.spring(
                    performance = performance,
                    dampingRatio = 0.74f,
                    stiffness = 410f
                ),
                label = "pageIndicatorDropletOffset"
            )

            // Equal-Width Interactive Dot Slots
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until safePageCount) {
                    val isClickable = onPageSelected != null
                    val isSelected = i == safeCurrentPage

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .then(
                                if (isClickable) {
                                    Modifier.selectable(
                                        selected = isSelected,
                                        interactionSource = remember(i) { MutableInteractionSource() },
                                        indication = null,
                                        role = Role.Tab,
                                        onClick = { onPageSelected(i) }
                                    )
                                } else {
                                    Modifier.semantics { selected = isSelected }
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(dotSize)
                                .clip(Capsule())
                                .background(LiquidGlassTheme.colors.inactiveTrack)
                        )
                    }
                }
            }

            // The animated droplet is the only selected mark. Keeping the base dots neutral
            // prevents a second selected dot from flashing while this one is moving.
            Box(
                modifier = Modifier
                    .offset(x = slidingOffset)
                    .zIndex(1f)
                    .size(width = activeWidth, height = dotSize)
                    .graphicsLayer {
                        scaleX = 1f + movementImpulse.value * 0.26f
                        scaleY = 1f - movementImpulse.value * 0.10f
                    }
                    .clip(Capsule())
                    .background(glassColors.accentContainer)
            )
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
