package com.anto426.liquidmonet.components.navigation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.anto426.liquidmonet.components.buttons.LiquidIconButton
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop
import com.anto426.liquidmonet.icons.LiquidIcons
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
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val colorScheme = MaterialTheme.colorScheme
    val hostContentBackdrop = LocalLiquidGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> backdrop
    }

    val safePageCount = pageCount.coerceAtLeast(1)
    val safeCurrentPage = currentPage.coerceIn(0, safePageCount - 1)

    Box(
        modifier = modifier
            .height(50.dp)
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = Capsule(),
                role = LiquidGlassRole.Navigation,
                containerColor = colorScheme.primary.copy(alpha = 0.08f)
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
                animationSpec = spring(
                    dampingRatio = 0.65f,
                    stiffness = 320f
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
                                .background(colorScheme.onSurface.copy(alpha = 0.24f))
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
                    .drawBehind {
                        val glowRadius = size.maxDimension * 2.2f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    colorScheme.primary.copy(alpha = 0.65f),
                                    colorScheme.primary.copy(alpha = 0.25f),
                                    colorScheme.primary.copy(alpha = 0f)
                                ),
                                center = center,
                                radius = glowRadius
                            ),
                            radius = glowRadius,
                            center = center
                        )
                    }
                    .clip(Capsule())
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                colorScheme.primary,
                                colorScheme.tertiary.copy(alpha = 0.90f)
                            )
                        )
                    )
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
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    val colorScheme = MaterialTheme.colorScheme
    val hostContentBackdrop = LocalLiquidGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> backdrop
    }

    val safeTotalPages = totalPages.coerceAtLeast(1)
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
                containerColor = colorScheme.primary.copy(alpha = 0.08f)
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
                animationSpec = spring(
                    dampingRatio = 0.68f,
                    stiffness = 340f
                ),
                label = "paginationPillOffset"
            )

            // Sliding Active Selection Pill with Dynamic Monet Luminescence
            Box(
                modifier = Modifier
                    .offset(x = selectedPillOffset)
                    .size(buttonSize)
                    .drawBehind {
                        val glowRadius = size.maxDimension * 1.8f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    colorScheme.primary.copy(alpha = 0.55f),
                                    colorScheme.primary.copy(alpha = 0.15f),
                                    colorScheme.primary.copy(alpha = 0f)
                                ),
                                center = center,
                                radius = glowRadius
                            ),
                            radius = glowRadius,
                            center = center
                        )
                    }
                    .clip(Capsule())
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                colorScheme.primary,
                                colorScheme.primary.copy(alpha = 0.85f)
                            )
                        )
                    )
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
                            color = if (isSelected) Color.White else colorScheme.onSurface
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
