package com.anto426.antoui.components.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.anto426.antoui.components.display.AntoHorizontalDivider
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop
import com.anto426.antoui.icons.AntoIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.RoundedRectangle

/**
 * AntoAccordionItem - Expandable Optical Liquid Glass Panel with simulated fluid dynamics.
 * Features continuous corner curvature morphing, Monet luminous tint dilation,
 * and damped fluid cascading content expansion.
 */
@Composable
fun AntoAccordionItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    isExpanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit = {},
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val hostContentBackdrop = LocalAntoGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> backdrop
    }

    val headerHighlight = rememberAntoControlHighlight()

    // Smooth fluid expansion progress for continuous physical morphing
    val expansionProgress by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = 360f
        ),
        label = "accordionLiquidProgress"
    )

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = 0.68f,
            stiffness = 400f
        ),
        label = "accordionChevronRotation"
    )

    val chevronColor by animateColorAsState(
        targetValue = if (isExpanded) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.65f),
        animationSpec = tween(durationMillis = 200),
        label = "accordionChevronColor"
    )

    // Dynamic fluid corner curvature morphing (20.dp -> 26.dp on expansion)
    val liquidCornerRadius = lerp(20f, 26f, expansionProgress).dp
    val liquidShape = remember(liquidCornerRadius) { RoundedRectangle(liquidCornerRadius) }

    // Subtle Monet chromatic expansion tint
    val liquidContainerTint = colorScheme.primary.copy(alpha = 0.045f * expansionProgress)

    val density = androidx.compose.ui.platform.LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .antoLiquidGlass(
                backdrop = effectiveBackdrop,
                shape = liquidShape,
                role = AntoGlassRole.Control,
                containerColor = liquidContainerTint
            )
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(antoControlLayerBlock(true, headerHighlight) ?: {})
                    .antoControlPressFeedback(
                        enabled = true,
                        interactiveHighlight = headerHighlight,
                        drawHighlightOverlay = false
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        onClick = { onExpandedChange(!isExpanded) }
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (leadingIcon != null) {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = if (isExpanded) colorScheme.primary else colorScheme.primary.copy(alpha = 0.85f),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colorScheme.onSurface
                        )
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurface.copy(alpha = 0.60f)
                            )
                        }
                    }
                }

                Icon(
                    imageVector = AntoIcons.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Comprimi" else "Espandi",
                    tint = chevronColor,
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(chevronRotation)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = 0.72f,
                        stiffness = 380f
                    ),
                    expandFrom = Alignment.Top
                ) + fadeIn(
                    animationSpec = tween(
                        durationMillis = 240,
                        easing = LinearOutSlowInEasing
                    )
                ),
                exit = shrinkVertically(
                    animationSpec = spring(
                        dampingRatio = 0.86f,
                        stiffness = 450f
                    ),
                    shrinkTowards = Alignment.Top
                ) + fadeOut(
                    animationSpec = tween(
                        durationMillis = 180,
                        easing = FastOutSlowInEasing
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                        .graphicsLayer {
                            // Fluid cascading downward momentum into expanded chamber
                            translationY = with(density) { -12.dp.toPx() * (1f - expansionProgress) }
                            alpha = expansionProgress.coerceIn(0f, 1f)
                        }
                ) {
                    AntoHorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))
                    content()
                }
            }
        }
    }
}
