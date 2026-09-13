package com.anto426.liquidmonet.components.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
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
import kotlin.math.PI
import kotlin.math.sin

/**
 * LiquidControlCenterTile - Interactive Quick Settings Tile with Radiant Monet Illumination,
 * Pure Snell Glass Refraction, and Fluid Bubble Spring Transitions.
 */
@Composable
fun LiquidControlCenterTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingContent: (@Composable () -> Unit)? = null,
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

    // Pure specular rim highlight brush aligned with glass theme
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

    // Rich radiant Monet internal glow brush (colore ricco e saturo, mai sbiadito né sbiancato)
    val internalGlowBrush = remember(isDark, primaryColor) {
        Brush.radialGradient(
            colors = if (isDark) listOf(
                primaryColor.copy(alpha = 0.55f),
                primaryColor.copy(alpha = 0.32f),
                primaryColor.copy(alpha = 0.10f),
                Color.Transparent
            ) else listOf(
                primaryColor.copy(alpha = 0.40f),
                primaryColor.copy(alpha = 0.22f),
                primaryColor.copy(alpha = 0.06f),
                Color.Transparent
            ),
            center = Offset(110f, 110f),
            radius = 380f
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

    // Dynamic fluid bubble spring transition: volume conservation
    val transitionProgress by animateFloatAsState(
        targetValue = if (active) 1f else 0f,
        animationSpec = LiquidMotion.spring(
            performance = performance,
            dampingRatio = 0.58f,
            stiffness = 300f
        ),
        label = "tileActiveProgress"
    )

    val bubblePop = sin(transitionProgress * PI).toFloat()
    val tileSquashX = 1f + bubblePop * 0.022f
    val tileStretchY = 1f - bubblePop * 0.015f
    val iconBubbleScale = 1f + bubblePop * 0.12f + (transitionProgress * 0.04f)

    // Rich luminous Monet container glow
    val animatedContainerColor by animateColorAsState(
        targetValue = if (active) {
            primaryColor.copy(alpha = if (isDark) 0.32f else 0.22f)
        } else {
            Color.Transparent
        },
        animationSpec = LiquidMotion.spring(
            performance = performance,
            dampingRatio = 0.58f,
            stiffness = 300f
        ),
        label = "tileContainerColor"
    )

    // Vibrant solid/radiant Monet droplet pod when on, crystal frosted glass when off (mai nero!)
    val animatedIconBgColor by animateColorAsState(
        targetValue = if (active) {
            primaryColor
        } else {
            if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.65f)
        },
        animationSpec = LiquidMotion.spring(
            performance = performance,
            dampingRatio = 0.58f,
            stiffness = 300f
        ),
        label = "tileIconBgColor"
    )

    // Pure crisp white icon on active state, soft white/surface variant when off (mai nero!)
    val animatedIconTint by animateColorAsState(
        targetValue = if (active) Color.White else (if (isDark) Color.White.copy(alpha = 0.85f) else inactiveContentColor.copy(alpha = 0.85f)),
        animationSpec = LiquidMotion.spring(
            performance = performance,
            dampingRatio = 0.58f,
            stiffness = 300f
        ),
        label = "tileIconTint"
    )

    val animatedSubtitleColor by animateColorAsState(
        targetValue = if (active) (if (isDark) Color.White.copy(alpha = 0.92f) else primaryColor)
        else glassColors.secondaryContent,
        animationSpec = LiquidMotion.spring(
            performance = performance,
            dampingRatio = 0.58f,
            stiffness = 300f
        ),
        label = "tileSubtitleColor"
    )

    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdropState)
    val interactionSource = remember { MutableInteractionSource() }
    val interactiveHighlight = rememberLiquidControlHighlight()

    Box(
        modifier = modifier
            .height(74.dp)
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = tileSquashX
                scaleY = tileStretchY
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            }
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = LiquidGlassRole.Control,
                preset = LiquidGlassPresets.Navigation,
                containerColor = animatedContainerColor.takeIf { active },
                layerBlock = liquidControlLayerBlock(enabled, interactiveHighlight)
            )
            .border(
                width = 1.dp,
                brush = if (active) activeGlassRimBrush else glassRimBrush,
                shape = shape
            )
            .toggleable(
                value = active,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Switch,
                enabled = enabled,
                onValueChange = { onClick() }
            )
            .liquidControlPressFeedback(enabled, interactiveHighlight, shape = shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Radiant Internal Monet Glow Layer (Bagliore interno ricco e saturo)
        if (transitionProgress > 0.01f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { alpha = transitionProgress }
                    .background(internalGlowBrush, shape)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Dedicated Optical Liquid Glass Icon Bubble Pod with Droplet Elastic Spring
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .graphicsLayer {
                        scaleX = iconBubbleScale
                        scaleY = iconBubbleScale
                    }
                    .background(animatedIconBgColor, iconShape)
                    .border(
                        width = 0.5.dp,
                        brush = if (active) activeGlassRimBrush else glassRimBrush,
                        shape = iconShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = animatedIconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                BasicText(
                    text = title,
                    style = TextStyle(
                        color = inactiveContentColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                BasicText(
                    text = subtitle,
                    style = TextStyle(
                        color = animatedSubtitleColor,
                        fontSize = 12.5.sp,
                        fontWeight = if (active) FontWeight.Medium else FontWeight.Normal
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}

/**
 * LiquidControlCenterCompactTile - Squircle Compact Tile for Grid Layouts (Torch, Airplane, Hotspot, DND).
 * Perfectly matches the fluid bubble bounce and specular refraction of the wide tile.
 */
@Composable
fun LiquidControlCenterCompactTile(
    title: String,
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backdropState: Backdrop = emptyBackdrop()
) {
    val performance = LocalLiquidGlassPerformance.current
    val colorScheme = MaterialTheme.colorScheme
    val primaryColor = colorScheme.primary
    val glassColors = LiquidGlassTheme.colors
    val inactiveContentColor = glassColors.content
    val isDark = colorScheme.surface.luminance() < 0.5f

    val shape = RoundedRectangle(20.dp)
    val iconShape = Capsule()

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

    // Rich radiant Monet internal glow brush (colore ricco e saturo, mai sbiadito né sbiancato)
    val internalGlowBrush = remember(isDark, primaryColor) {
        Brush.radialGradient(
            colors = if (isDark) listOf(
                primaryColor.copy(alpha = 0.55f),
                primaryColor.copy(alpha = 0.30f),
                primaryColor.copy(alpha = 0.08f),
                Color.Transparent
            ) else listOf(
                primaryColor.copy(alpha = 0.40f),
                primaryColor.copy(alpha = 0.20f),
                primaryColor.copy(alpha = 0.05f),
                Color.Transparent
            ),
            radius = 200f
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

    val transitionProgress by animateFloatAsState(
        targetValue = if (active) 1f else 0f,
        animationSpec = LiquidMotion.spring(
            performance = performance,
            dampingRatio = 0.58f,
            stiffness = 300f
        ),
        label = "compactTileActiveProgress"
    )

    val bubblePop = sin(transitionProgress * PI).toFloat()
    val tileSquashX = 1f + bubblePop * 0.024f
    val tileStretchY = 1f - bubblePop * 0.016f
    val iconBubbleScale = 1f + bubblePop * 0.14f + (transitionProgress * 0.04f)

    val animatedContainerColor by animateColorAsState(
        targetValue = if (active) {
            primaryColor.copy(alpha = if (isDark) 0.32f else 0.22f)
        } else {
            Color.Transparent
        },
        animationSpec = LiquidMotion.spring(
            performance = performance,
            dampingRatio = 0.58f,
            stiffness = 300f
        ),
        label = "compactTileContainerColor"
    )

    // Vibrant solid/radiant Monet droplet pod when on, crystal frosted glass when off (mai nero!)
    val animatedIconBgColor by animateColorAsState(
        targetValue = if (active) {
            primaryColor
        } else {
            if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.65f)
        },
        animationSpec = LiquidMotion.spring(
            performance = performance,
            dampingRatio = 0.58f,
            stiffness = 300f
        ),
        label = "compactTileIconBgColor"
    )

    // Pure crisp white icon on active state, soft white/surface variant when off (mai nero!)
    val animatedIconTint by animateColorAsState(
        targetValue = if (active) Color.White else (if (isDark) Color.White.copy(alpha = 0.85f) else inactiveContentColor.copy(alpha = 0.85f)),
        animationSpec = LiquidMotion.spring(
            performance = performance,
            dampingRatio = 0.58f,
            stiffness = 300f
        ),
        label = "compactTileIconTint"
    )

    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdropState)
    val interactionSource = remember { MutableInteractionSource() }
    val interactiveHighlight = rememberLiquidControlHighlight()

    Box(
        modifier = modifier
            .height(84.dp)
            .graphicsLayer {
                scaleX = tileSquashX
                scaleY = tileStretchY
                transformOrigin = TransformOrigin(0.5f, 0.5f)
            }
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = LiquidGlassRole.Control,
                preset = LiquidGlassPresets.Navigation,
                containerColor = animatedContainerColor.takeIf { active },
                layerBlock = liquidControlLayerBlock(enabled, interactiveHighlight)
            )
            .border(
                width = 1.dp,
                brush = if (active) activeGlassRimBrush else glassRimBrush,
                shape = shape
            )
            .toggleable(
                value = active,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Switch,
                enabled = enabled,
                onValueChange = { onClick() }
            )
            .liquidControlPressFeedback(enabled, interactiveHighlight, shape = shape)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Radiant Internal Monet Glow Layer (Bagliore interno ricco e saturo)
        if (transitionProgress > 0.01f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { alpha = transitionProgress }
                    .background(internalGlowBrush, shape)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .graphicsLayer {
                        scaleX = iconBubbleScale
                        scaleY = iconBubbleScale
                    }
                    .background(animatedIconBgColor, iconShape)
                    .border(
                        width = 0.5.dp,
                        brush = if (active) activeGlassRimBrush else glassRimBrush,
                        shape = iconShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = animatedIconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            BasicText(
                text = title,
                style = TextStyle(
                    color = if (active) (if (isDark) Color.White else primaryColor) else inactiveContentColor,
                    fontSize = 11.5.sp,
                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
