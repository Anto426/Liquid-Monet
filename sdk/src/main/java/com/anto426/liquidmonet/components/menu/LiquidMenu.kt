package com.anto426.liquidmonet.components.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.anto426.liquidmonet.components.navigation.LiquidTopBarAction
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.glass.resolveLiquidGlassBackdrop
import com.anto426.liquidmonet.glass.overlay.LiquidGlassDropdownPlacement
import com.anto426.liquidmonet.glass.overlay.LiquidGlassOverlayAnchorState
import com.anto426.liquidmonet.glass.overlay.LiquidGlassOverlayPlacement
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassContentBackdrop
import com.anto426.liquidmonet.glass.overlay.LocalLiquidGlassOverlayState
import com.anto426.liquidmonet.glass.overlay.liquidGlassOverlayAnchor
import com.anto426.liquidmonet.glass.overlay.rememberLiquidGlassOverlayAnchorState
import com.anto426.liquidmonet.glass.runtime.LiquidGlassMotionSpecs
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import com.anto426.liquidmonet.icons.LiquidIcons
import com.anto426.liquidmonet.theme.LiquidGlassTheme
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle

/**
 * LiquidMorphingAction - Liquid Glass Action Button that deforms/morphs organically
 * from a circular glass button into a full glass dropdown menu.
 */
@Composable
fun LiquidMorphingAction(
    action: LiquidTopBarAction,
    isLastItem: Boolean,
    backdropState: Backdrop,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var usesHostedOverlay by remember { mutableStateOf(false) }
    val overlayKey = remember { Any() }
    val overlayState = LocalLiquidGlassOverlayState.current
    val contentBackdrop = LocalLiquidGlassContentBackdrop.current
    val anchorState = rememberLiquidGlassOverlayAnchorState()
    val latestAction by rememberUpdatedState(action)
    val latestBackdrop by rememberUpdatedState(contentBackdrop ?: backdropState)

    val performance = LocalLiquidGlassPerformance.current
    val contentColor = MaterialTheme.colorScheme.onSurface
    val anchorShape = remember { Capsule() }
    val interactiveHighlight = rememberLiquidControlHighlight()
    val isMorphing = expanded && action.subItems.isNotEmpty()

    val buttonAlpha by animateFloatAsState(
        targetValue = if (isMorphing) 0f else 1f,
        animationSpec = LiquidGlassMotionSpecs.tween(performance, 450),
        label = "buttonAlpha"
    )

    val buttonScale by animateFloatAsState(
        targetValue = if (isMorphing) 0.96f else 1f,
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.60f,
            stiffness = 70f
        ),
        label = "buttonScale"
    )

    val iconRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.65f,
            stiffness = 85f
        ),
        label = "iconRotation"
    )

    LaunchedEffect(anchorState.boundsInWindow, usesHostedOverlay) {
        if (usesHostedOverlay) {
            anchorState.boundsInWindow?.let { bounds ->
                overlayState?.updateAnchor(overlayKey, bounds)
            }
        }
    }
    DisposableEffect(overlayState, overlayKey) {
        onDispose { overlayState?.removeImmediately(overlayKey) }
    }

    Box(modifier = modifier.padding(start = 4.dp, end = if (isLastItem) 12.dp else 0.dp)) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .graphicsLayer {
                    alpha = buttonAlpha
                    scaleX = buttonScale
                    scaleY = buttonScale
                }
                .liquidGlassOverlayAnchor(anchorState)
                .liquidGlass(
                    backdrop = backdropState,
                    shape = anchorShape,
                    role = LiquidGlassRole.Navigation,
                    layerBlock = liquidControlLayerBlock(true, interactiveHighlight)
                )
                .clickable {
                    if (action.subItems.isEmpty()) {
                        action.onClick()
                    } else if (expanded) {
                        expanded = false
                        if (usesHostedOverlay) {
                            overlayState?.dismiss(overlayKey)
                        }
                        usesHostedOverlay = false
                    } else {
                        val anchorBounds = anchorState.boundsInWindow
                        if (overlayState != null && anchorBounds != null) {
                            expanded = true
                            usesHostedOverlay = true
                            overlayState.show(
                                key = overlayKey,
                                anchorBoundsInWindow = anchorBounds,
                                placement = LiquidGlassOverlayPlacement.AnchorTopEnd,
                                offset = DpOffset.Zero,
                                onDismissRequest = {
                                    expanded = false
                                    usesHostedOverlay = false
                                }
                            ) { dismiss ->
                                val currentAction = latestAction
                                LiquidGlassMenuSurface(
                                    backdropState = latestBackdrop,
                                    minWidth = 190.dp,
                                    maxWidth = 260.dp
                                ) {
                                    currentAction.subItems.forEach { subItem ->
                                        LiquidMenuItem(
                                            text = subItem.label,
                                            icon = subItem.icon,
                                            selected = subItem.selected,
                                            onClick = {
                                                subItem.onClick()
                                                dismiss()
                                            }
                                        )
                                    }
                                }
                            }
                        } else {
                            // A scene host is unavailable: preserve source compatibility with a
                            // non-refracting platform-popup fallback.
                            usesHostedOverlay = false
                            expanded = true
                        }
                    }
                }
                .liquidControlPressFeedback(
                    enabled = true,
                    interactiveHighlight = interactiveHighlight,
                    shape = anchorShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                tint = contentColor,
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer { rotationZ = iconRotation }
            )
        }

        val fallbackVisibility = remember { MutableTransitionState(false) }
        fallbackVisibility.targetState =
            action.subItems.isNotEmpty() && expanded && !usesHostedOverlay
        if (fallbackVisibility.currentState || fallbackVisibility.targetState) {
            Popup(
                onDismissRequest = {
                    expanded = false
                    usesHostedOverlay = false
                },
                properties = PopupProperties(focusable = true, clippingEnabled = false),
                offset = IntOffset.Zero
            ) {
                AnimatedVisibility(
                    visibleState = fallbackVisibility,
                    enter = glassPopupEnterTransition(),
                    exit = glassPopupExitTransition()
                ) {
                    LiquidGlassMenuSurface(
                        backdropState = emptyBackdrop(),
                        minWidth = 190.dp,
                        maxWidth = 260.dp
                    ) {
                        action.subItems.forEach { subItem ->
                            LiquidMenuItem(
                                text = subItem.label,
                                icon = subItem.icon,
                                selected = subItem.selected,
                                onClick = {
                                    subItem.onClick()
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Liquid dropdown with one canonical API. When [anchorState] is attached to the trigger through
 * [liquidGlassOverlayAnchor], the menu is scene-hosted and samples the composed content beneath it;
 * otherwise it falls back to a platform popup.
 */
@Composable
fun LiquidDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    anchorState: LiquidGlassOverlayAnchorState? = null,
    placement: LiquidGlassDropdownPlacement = LiquidGlassDropdownPlacement.BelowEnd,
    offset: DpOffset = DpOffset(0.dp, 8.dp),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    content: @Composable ColumnScope.() -> Unit
) {
    LiquidDropdownMenuImpl(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        anchorState = anchorState,
        modifier = modifier,
        placement = placement,
        offset = offset,
        backdropState = backdropState,
        content = content
    )
}

@Composable
private fun LiquidDropdownMenuImpl(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    anchorState: LiquidGlassOverlayAnchorState?,
    modifier: Modifier,
    placement: LiquidGlassDropdownPlacement,
    offset: DpOffset,
    backdropState: Backdrop,
    content: @Composable ColumnScope.() -> Unit
) {
    val overlayState = LocalLiquidGlassOverlayState.current
    val contentBackdrop = LocalLiquidGlassContentBackdrop.current
    val overlayKey = remember { Any() }
    val anchorBounds = anchorState?.boundsInWindow
    val canUseHostedOverlay = overlayState != null && anchorBounds != null
    val latestOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val latestBackdrop by rememberUpdatedState(contentBackdrop ?: backdropState)
    val latestContent by rememberUpdatedState(content)
    val hostedContent: @Composable (dismiss: () -> Unit) -> Unit = {
        LiquidGlassMenuSurface(
            modifier = modifier,
            backdropState = latestBackdrop
        ) {
            latestContent()
        }
    }

    LaunchedEffect(
        expanded,
        overlayState,
        anchorBounds,
        offset,
        placement,
        canUseHostedOverlay
    ) {
        if (overlayState != null && anchorBounds != null) {
            if (expanded) {
                overlayState.show(
                    key = overlayKey,
                    anchorBoundsInWindow = anchorBounds,
                    placement = placement,
                    offset = offset,
                    onDismissRequest = { latestOnDismissRequest() },
                    content = hostedContent
                )
            } else {
                overlayState.dismiss(overlayKey, notifyOnDismiss = false)
            }
        }
    }
    DisposableEffect(overlayState, overlayKey) {
        onDispose { overlayState?.removeImmediately(overlayKey) }
    }

    val fallbackVisibility = remember { MutableTransitionState(false) }
    fallbackVisibility.targetState = expanded && !canUseHostedOverlay
    if (fallbackVisibility.currentState || fallbackVisibility.targetState) {
        val density = LocalDensity.current
        val popupOffset = with(density) {
            IntOffset(offset.x.roundToPx(), offset.y.roundToPx())
        }
        Popup(
            onDismissRequest = onDismissRequest,
            properties = PopupProperties(focusable = true, clippingEnabled = false),
            offset = popupOffset
        ) {
            AnimatedVisibility(
                visibleState = fallbackVisibility,
                enter = glassPopupEnterTransition(),
                exit = glassPopupExitTransition()
            ) {
                LiquidGlassMenuSurface(
                    modifier = modifier,
                    backdropState = latestBackdrop
                ) {
                    content()
                }
            }
        }
    }
}

/**
 * LiquidGlassMenuSurface - Optical Liquid Glass Surface for Dropdown Menus.
 * Deforms and stretches organically like liquid gelatin in all directions without any white flash.
 */
@Composable
private fun LiquidGlassMenuSurface(
    backdropState: Backdrop,
    modifier: Modifier = Modifier,
    minWidth: androidx.compose.ui.unit.Dp = 180.dp,
    maxWidth: androidx.compose.ui.unit.Dp = 280.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val effectiveBackdrop = resolveLiquidGlassBackdrop(backdropState)
    val menuShape = remember { RoundedRectangle(22.dp) }
    val surfaceBackdrop = rememberLayerBackdrop()
    Box(
        modifier = modifier
            .widthIn(min = minWidth, max = maxWidth)
            .width(IntrinsicSize.Max)
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = menuShape,
                role = LiquidGlassRole.Menu,
                exportedBackdrop = surfaceBackdrop
            )
            .padding(vertical = 6.dp, horizontal = 4.dp)
    ) {
        CompositionLocalProvider(LocalLiquidGlassContentBackdrop provides surfaceBackdrop) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                content = content
            )
        }
    }
}

@Composable
private fun glassPopupEnterTransition(): EnterTransition {
    val performance = LocalLiquidGlassPerformance.current
    return scaleIn(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.78f,
            stiffness = 420f
        ),
        initialScale = 0.94f,
        transformOrigin = TransformOrigin(0.92f, 0.04f)
    ) + fadeIn(LiquidGlassMotionSpecs.tween(performance, 180))
}

@Composable
private fun glassPopupExitTransition(): ExitTransition {
    val performance = LocalLiquidGlassPerformance.current
    return scaleOut(
        animationSpec = LiquidGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.88f,
            stiffness = 460f
        ),
        targetScale = 0.97f,
        transformOrigin = TransformOrigin(0.92f, 0.04f)
    ) + fadeOut(LiquidGlassMotionSpecs.tween(performance, 130))
}

/**
 * LiquidMenuItem - Clean, borderless menu item row with dynamic touch illumination.
 * Deforms seamlessly together with the outer menu container with interactive spotlight glow.
 */
@Composable
fun LiquidMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    supportingText: String? = null,
    trailingText: String? = null,
    trailingIcon: ImageVector? = null,
    enabled: Boolean = true,
    contentColor: Color = Color.Unspecified,
    selected: Boolean = false,
    destructive: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val colorScheme = MaterialTheme.colorScheme
    val glassColors = LiquidGlassTheme.colors

    val accentColor = if (destructive) colorScheme.error else colorScheme.primary
    val defaultColor = when {
        destructive -> colorScheme.error
        contentColor != Color.Unspecified -> contentColor
        else -> colorScheme.onSurface
    }
    val resolvedColor = if (isPressed || isHovered || selected) accentColor else defaultColor

    val animatedAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (enabled) 1f else 0.40f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 200),
        label = "menuItemAlpha"
    )

    val itemScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150),
        label = "menuItemScale"
    )

    val backgroundColor by androidx.compose.animation.animateColorAsState(
        targetValue = when {
            isPressed -> accentColor.copy(alpha = glassColors.selectedContainer.alpha)
            isHovered -> accentColor.copy(alpha = glassColors.accentContainer.alpha)
            selected -> accentColor.copy(alpha = glassColors.accentContainer.alpha * 0.80f)
            else -> Color.Transparent
        },
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 180),
        label = "menuItemBg"
    )

    val borderColor by androidx.compose.animation.animateColorAsState(
        targetValue = when {
            isPressed -> accentColor.copy(alpha = glassColors.focusIndicator.alpha)
            isHovered -> accentColor.copy(alpha = glassColors.focusIndicator.alpha * 0.62f)
            selected -> accentColor.copy(alpha = glassColors.focusIndicator.alpha * 0.50f)
            else -> Color.Transparent
        },
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 180),
        label = "menuItemBorder"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(14.dp)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 11.dp)
            .graphicsLayer {
                scaleX = itemScale
                scaleY = itemScale
                alpha = animatedAlpha
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = resolvedColor.copy(alpha = if (isPressed || isHovered || selected) 1f else 0.90f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isPressed || selected) FontWeight.SemiBold else FontWeight.Medium,
                color = resolvedColor,
                fontSize = 14.5.sp
            )
            if (!supportingText.isNullOrBlank()) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.labelSmall,
                    color = resolvedColor.copy(alpha = 0.62f),
                    fontSize = 11.5.sp
                )
            }
        }

        if (!trailingText.isNullOrBlank()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = trailingText,
                style = MaterialTheme.typography.bodySmall,
                color = resolvedColor.copy(alpha = 0.65f),
                fontSize = 12.sp
            )
        }
        if (selected) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = LiquidIcons.Check,
                contentDescription = "Selezionato",
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
        } else if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = defaultColor.copy(alpha = 0.60f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
