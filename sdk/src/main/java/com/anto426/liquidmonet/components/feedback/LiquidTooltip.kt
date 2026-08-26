package com.anto426.liquidmonet.components.feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import kotlinx.coroutines.delay

/**
 * LiquidTooltip - Contextual Glass Tooltip Popup anchored to components.
 */
@Composable
fun LiquidTooltipBox(
    tooltip: String,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    content: @Composable () -> Unit
) {
    var showTooltip by remember { mutableStateOf(false) }

    val contentColor = MaterialTheme.colorScheme.onSurface

    val shape = Capsule()

    Box(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    showTooltip = true
                }
            )
        }
    ) {
        content()

        if (showTooltip) {
            LaunchedEffect(Unit) {
                delay(2200)
                showTooltip = false
            }

            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, -60),
                onDismissRequest = { showTooltip = false },
                properties = PopupProperties(focusable = false)
            ) {
                AnimatedVisibility(
                    visible = showTooltip,
                    enter = scaleIn(spring(dampingRatio = 0.75f, stiffness = 500f)) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .liquidGlass(
                                backdrop = backdropState,
                                shape = shape,
                                role = LiquidGlassRole.Control
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        BasicText(
                            text = tooltip,
                            style = TextStyle(
                                color = contentColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}
