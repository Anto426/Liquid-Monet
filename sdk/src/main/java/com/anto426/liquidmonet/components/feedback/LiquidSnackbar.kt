package com.anto426.liquidmonet.components.feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import com.anto426.liquidmonet.icons.LiquidIcons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import kotlin.math.roundToInt

enum class LiquidSnackbarType {
    Info, Success, Warning, Error
}

/**
 * LiquidSnackbar - Floating Liquid Glass Notification Banner with Snell Lensing and Swipe-to-Dismiss.
 */
@Composable
fun LiquidSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    type: LiquidSnackbarType = LiquidSnackbarType.Info,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    var offsetX by remember { mutableFloatStateOf(0f) }

    val colorScheme = MaterialTheme.colorScheme
    val contentColor = colorScheme.onSurface
    val icon: ImageVector = when (type) {
        LiquidSnackbarType.Info -> LiquidIcons.Info
        LiquidSnackbarType.Success -> LiquidIcons.Check
        LiquidSnackbarType.Warning -> LiquidIcons.Warning
        LiquidSnackbarType.Error -> LiquidIcons.Info
    }
    val accentColor = when (type) {
        LiquidSnackbarType.Info -> colorScheme.primary
        LiquidSnackbarType.Success -> colorScheme.tertiary
        LiquidSnackbarType.Warning -> colorScheme.secondary
        LiquidSnackbarType.Error -> colorScheme.error
    }

    val shape = Capsule()

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(spring(dampingRatio = 0.78f, stiffness = 380f), initialOffsetY = { it * 2 }) + fadeIn(),
        exit = slideOutVertically(spring(dampingRatio = 0.9f, stiffness = 450f), targetOffsetY = { it * 2 }) + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        offsetX += delta
                    },
                    onDragStopped = {
                        if (kotlin.math.abs(offsetX) > 200f) {
                            onDismiss?.invoke()
                        }
                        offsetX = 0f
                    }
                )
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .liquidGlass(
                    backdrop = backdropState,
                    shape = shape,
                    role = LiquidGlassRole.Surface
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )

                BasicText(
                    text = message,
                    style = TextStyle(
                        color = contentColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 2
                )

                if (actionLabel != null && onActionClick != null) {
                    Box(
                        modifier = Modifier
                            .clip(Capsule())
                            .clickable(onClick = onActionClick)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        BasicText(
                            text = actionLabel,
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                if (onDismiss != null) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(Capsule())
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = LiquidIcons.Close,
                            contentDescription = "Dismiss",
                            tint = contentColor.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
