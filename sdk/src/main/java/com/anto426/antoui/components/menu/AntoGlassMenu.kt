package com.anto426.antoui.components.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.compositionLocalOf
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
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.components.navigation.AntoTopBarAction
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.anto426.antoui.glass.overlay.AntoGlassDropdownPlacement
import com.anto426.antoui.glass.overlay.AntoGlassOverlayAnchorState
import com.anto426.antoui.glass.overlay.AntoGlassOverlayPlacement
import com.anto426.antoui.glass.overlay.LocalAntoGlassContentBackdrop
import com.anto426.antoui.glass.overlay.LocalAntoGlassOverlayState
import com.anto426.antoui.glass.overlay.antoGlassOverlayAnchor
import com.anto426.antoui.glass.overlay.rememberAntoGlassOverlayAnchorState
import com.anto426.antoui.glass.runtime.AntoGlassMotionSpecs
import com.anto426.antoui.glass.runtime.LocalAntoGlassPerformance
import com.anto426.antoui.icons.AntoIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.catalog.utils.InteractiveHighlight
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle

internal val LocalMenuSurfaceHighlight = compositionLocalOf<InteractiveHighlight?> { null }

/**
 * AntoGlassMorphingAction - Liquid Glass Action Button that deforms/morphs organically
 * from a circular glass button into a full glass dropdown menu.
 */
@Composable
fun AntoGlassMorphingAction(
    action: AntoTopBarAction,
    isLastItem: Boolean,
    backdropState: Backdrop,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var usesHostedOverlay by remember { mutableStateOf(false) }
    val overlayKey = remember { Any() }
    val overlayState = LocalAntoGlassOverlayState.current
    val contentBackdrop = LocalAntoGlassContentBackdrop.current
    val anchorState = rememberAntoGlassOverlayAnchorState()
    val latestAction by rememberUpdatedState(action)
    val latestBackdrop by rememberUpdatedState(contentBackdrop ?: backdropState)

    val performance = LocalAntoGlassPerformance.current
    val contentColor = MaterialTheme.colorScheme.onSurface
    val anchorShape = remember { Capsule() }
    val isMorphing = expanded && action.subItems.isNotEmpty()

    val buttonAlpha by animateFloatAsState(
        targetValue = if (isMorphing) 0f else 1f,
        animationSpec = AntoGlassMotionSpecs.tween(performance, 450),
        label = "buttonAlpha"
    )

    val buttonScale by animateFloatAsState(
        targetValue = if (isMorphing) 1.40f else 1f,
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.60f,
            stiffness = 70f
        ),
        label = "buttonScale"
    )

    val iconRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = AntoGlassMotionSpecs.spring(
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
                .antoGlassOverlayAnchor(anchorState)
                .antoLiquidGlass(
                    backdrop = backdropState,
                    shape = anchorShape,
                    role = AntoGlassRole.Navigation
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
                                placement = AntoGlassOverlayPlacement.AnchorTopEnd,
                                offset = DpOffset.Zero,
                                onDismissRequest = {
                                    expanded = false
                                    usesHostedOverlay = false
                                }
                            ) { dismiss ->
                                val currentAction = latestAction
                                AntoGlassMenuSurface(
                                    backdropState = latestBackdrop,
                                    minWidth = 190.dp,
                                    maxWidth = 260.dp
                                ) {
                                    currentAction.subItems.forEach { subItem ->
                                        AntoGlassMenuItem(
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
                },
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
                properties = PopupProperties(focusable = true),
                offset = IntOffset.Zero
            ) {
                AnimatedVisibility(
                    visibleState = fallbackVisibility,
                    enter = glassPopupEnterTransition(),
                    exit = glassPopupExitTransition()
                ) {
                    AntoGlassMenuSurface(
                        backdropState = emptyBackdrop(),
                        minWidth = 190.dp,
                        maxWidth = 260.dp
                    ) {
                        action.subItems.forEach { subItem ->
                            AntoGlassMenuItem(
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
 * Compatibility popup variant of [AntoGlassDropdownMenu].
 */
@Composable
fun AntoGlassDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    placement: AntoGlassDropdownPlacement = AntoGlassDropdownPlacement.BelowEnd,
    offset: DpOffset = DpOffset(0.dp, 8.dp),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    content: @Composable ColumnScope.() -> Unit
) {
    AntoGlassDropdownMenuImpl(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        anchorState = null,
        modifier = modifier,
        placement = placement,
        offset = offset,
        backdropState = backdropState,
        content = content
    )
}

/**
 * Scene-hosted dropdown variant. Attach [anchorState] to the real trigger with
 * [antoGlassOverlayAnchor]; the menu is then clamped to the scene and can sample its composed
 * content backdrop.
 */
@Composable
fun AntoGlassDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    anchorState: AntoGlassOverlayAnchorState,
    modifier: Modifier = Modifier,
    placement: AntoGlassDropdownPlacement = AntoGlassDropdownPlacement.BelowEnd,
    offset: DpOffset = DpOffset(0.dp, 8.dp),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    content: @Composable ColumnScope.() -> Unit
) {
    AntoGlassDropdownMenuImpl(
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
private fun AntoGlassDropdownMenuImpl(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    anchorState: AntoGlassOverlayAnchorState?,
    modifier: Modifier,
    placement: AntoGlassDropdownPlacement,
    offset: DpOffset,
    backdropState: Backdrop,
    content: @Composable ColumnScope.() -> Unit
) {
    val overlayState = LocalAntoGlassOverlayState.current
    val contentBackdrop = LocalAntoGlassContentBackdrop.current
    val overlayKey = remember { Any() }
    val anchorBounds = anchorState?.boundsInWindow
    val canUseHostedOverlay = overlayState != null && anchorBounds != null
    val latestOnDismissRequest by rememberUpdatedState(onDismissRequest)
    val latestBackdrop by rememberUpdatedState(contentBackdrop ?: backdropState)
    val latestContent by rememberUpdatedState(content)
    val hostedContent: @Composable (dismiss: () -> Unit) -> Unit = {
        AntoGlassMenuSurface(
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
            properties = PopupProperties(focusable = true),
            offset = popupOffset
        ) {
            AnimatedVisibility(
                visibleState = fallbackVisibility,
                enter = glassPopupEnterTransition(),
                exit = glassPopupExitTransition()
            ) {
                AntoGlassMenuSurface(
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
 * AntoGlassMenuSurface - Optical Liquid Glass Surface for Dropdown Menus.
 * Deforms and stretches organically like liquid gelatin in all directions without any white flash.
 */
@Composable
private fun AntoGlassMenuSurface(
    backdropState: Backdrop,
    modifier: Modifier = Modifier,
    minWidth: androidx.compose.ui.unit.Dp = 180.dp,
    maxWidth: androidx.compose.ui.unit.Dp = 280.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val hostContentBackdrop = LocalAntoGlassContentBackdrop.current
    val effectiveBackdrop = when {
        backdropState != emptyBackdrop() -> backdropState
        hostContentBackdrop != null && hostContentBackdrop != emptyBackdrop() -> hostContentBackdrop
        else -> emptyBackdrop()
    }
    val menuShape = remember { RoundedRectangle(22.dp) }
    val surfaceHighlight = rememberAntoControlHighlight()

    CompositionLocalProvider(LocalMenuSurfaceHighlight provides surfaceHighlight) {
        Box(
            modifier = modifier
                .widthIn(min = minWidth, max = maxWidth)
                .width(IntrinsicSize.Max)
                .antoLiquidGlass(
                    backdrop = effectiveBackdrop,
                    shape = menuShape,
                    role = AntoGlassRole.Menu,
                    preset = com.anto426.antoui.glass.runtime.AntoGlassPreset(
                        blurRadius = 16.dp,
                        refractionHeight = 22.dp,
                        refractionAmount = 36.dp,
                        chromaticAberration = 0.22f
                    ),
                    layerBlock = antoControlLayerBlock(true, surfaceHighlight)
                )
                .antoControlPressFeedback(
                    enabled = true,
                    interactiveHighlight = surfaceHighlight,
                    drawHighlightOverlay = false
                )
                .padding(vertical = 6.dp, horizontal = 4.dp)
        ) {
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
    val performance = LocalAntoGlassPerformance.current
    return scaleIn(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.58f,
            stiffness = 68f
        ),
        initialScale = 0.02f,
        transformOrigin = TransformOrigin(0.92f, 0.04f)
    ) + expandIn(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.60f,
            stiffness = 75f
        ),
        expandFrom = Alignment.TopEnd
    ) + fadeIn(AntoGlassMotionSpecs.tween(performance, 500))
}

@Composable
private fun glassPopupExitTransition(): ExitTransition {
    val performance = LocalAntoGlassPerformance.current
    return scaleOut(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.82f,
            stiffness = 110f
        ),
        targetScale = 0.02f,
        transformOrigin = TransformOrigin(0.92f, 0.04f)
    ) + shrinkOut(
        animationSpec = AntoGlassMotionSpecs.spring(
            performance = performance,
            dampingRatio = 0.82f,
            stiffness = 110f
        ),
        shrinkTowards = Alignment.TopEnd
    ) + fadeOut(AntoGlassMotionSpecs.tween(performance, 360))
}

/**
 * AntoGlassMenuItem - Clean, borderless menu item row with dynamic touch illumination.
 * Deforms seamlessly together with the outer menu container with interactive spotlight glow.
 */
@Composable
fun AntoGlassMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    trailingText: String? = null,
    enabled: Boolean = true,
    contentColor: Color = Color.Unspecified,
    selected: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val colorScheme = MaterialTheme.colorScheme

    val resolvedColor = when {
        isPressed || isHovered || selected -> colorScheme.primary
        contentColor != Color.Unspecified -> contentColor
        else -> colorScheme.onSurface
    }

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
            isPressed -> colorScheme.primary.copy(alpha = 0.28f)
            isHovered -> colorScheme.primary.copy(alpha = 0.20f)
            selected -> colorScheme.primary.copy(alpha = 0.16f)
            else -> Color.Transparent
        },
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 180),
        label = "menuItemBg"
    )

    val borderColor by androidx.compose.animation.animateColorAsState(
        targetValue = when {
            isPressed -> colorScheme.primary.copy(alpha = 0.45f)
            isHovered -> colorScheme.primary.copy(alpha = 0.25f)
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

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isPressed || selected) FontWeight.SemiBold else FontWeight.Medium,
            color = resolvedColor,
            fontSize = 14.5.sp,
            modifier = Modifier.weight(1f)
        )

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
                imageVector = AntoIcons.Check,
                contentDescription = "Selezionato",
                tint = colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * AntoGlassActionItem - Dedicated Action Item for Dropdown Menus (Commands, Buttons, Shortcuts).
 * Designed specifically for stateless trigger actions (e.g. Share, Refresh, Delete, Settings)
 * with instant touch spotlight illumination.
 */
@Composable
fun AntoGlassActionItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    trailingText: String? = null,
    trailingIcon: ImageVector? = null,
    isDestructive: Boolean = false,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val colorScheme = MaterialTheme.colorScheme

    val accentColor = if (isDestructive) colorScheme.error else colorScheme.primary
    val defaultColor = if (isDestructive) colorScheme.error else colorScheme.onSurface
    val resolvedColor = if (isPressed || isHovered) accentColor else defaultColor

    val animatedAlpha by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (enabled) 1f else 0.40f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 180),
        label = "actionItemAlpha"
    )

    val itemScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 150),
        label = "actionItemScale"
    )

    val backgroundColor by androidx.compose.animation.animateColorAsState(
        targetValue = when {
            isPressed -> accentColor.copy(alpha = 0.25f)
            isHovered -> accentColor.copy(alpha = 0.16f)
            else -> Color.Transparent
        },
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 180),
        label = "actionItemBg"
    )

    val borderColor by androidx.compose.animation.animateColorAsState(
        targetValue = when {
            isPressed -> accentColor.copy(alpha = 0.40f)
            isHovered -> accentColor.copy(alpha = 0.20f)
            else -> Color.Transparent
        },
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 180),
        label = "actionItemBorder"
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
                tint = resolvedColor.copy(alpha = if (isPressed || isHovered) 1f else 0.85f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isPressed) FontWeight.SemiBold else FontWeight.Medium,
            color = resolvedColor,
            fontSize = 14.5.sp,
            modifier = Modifier.weight(1f)
        )

        if (!trailingText.isNullOrBlank()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = trailingText,
                style = MaterialTheme.typography.labelSmall,
                color = defaultColor.copy(alpha = 0.50f),
                fontSize = 12.sp
            )
        }

        if (trailingIcon != null) {
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
