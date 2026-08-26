package com.anto426.liquidmonet.components.inputs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.liquidmonet.components.internal.liquidControlLayerBlock
import com.anto426.liquidmonet.components.internal.liquidControlPressFeedback
import com.anto426.liquidmonet.components.internal.rememberLiquidControlHighlight
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.icons.LiquidIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule

/**
 * LiquidSearchBar - Optical Liquid Glass Search Bar.
 * Features Snell lens refraction, Monet dynamic chromatic luminescence,
 * smooth focus glow, live query clearing, and 360-degree omnidirectional liquid touch dynamics.
 */
@Composable
fun LiquidSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholderText: String = "Cerca...",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    onSearch: ((String) -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val searchHighlight = rememberLiquidControlHighlight()

    val colorScheme = MaterialTheme.colorScheme
    val contentColor = colorScheme.onSurface
    val primaryColor = colorScheme.primary
    val placeholderColor = contentColor.copy(alpha = 0.50f)
    val shape = Capsule()

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isFocused) primaryColor.copy(alpha = 0.55f) else Color.Transparent,
        animationSpec = tween(durationMillis = 200),
        label = "searchBorderColor"
    )

    val effectiveBackdrop = if (backdropState != emptyBackdrop()) backdropState else backdrop

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .graphicsLayer(liquidControlLayerBlock(enabled, searchHighlight) ?: {})
            .liquidControlPressFeedback(
                enabled = enabled,
                interactiveHighlight = searchHighlight,
                drawHighlightOverlay = false
            )
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = shape,
                role = LiquidGlassRole.Control
            )
            .border(width = 1.2.dp, color = animatedBorderColor, shape = shape),
        enabled = enabled,
        singleLine = true,
        textStyle = TextStyle(
            color = contentColor,
            fontSize = 15.5.sp,
            fontWeight = FontWeight.Medium
        ),
        cursorBrush = SolidColor(primaryColor),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch?.invoke(query) }),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = LiquidIcons.Search,
                    contentDescription = "Cerca",
                    tint = if (isFocused) primaryColor else contentColor.copy(alpha = 0.70f),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholderText,
                            style = TextStyle(
                                color = placeholderColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    innerTextField()
                }
                AnimatedVisibility(
                    visible = query.isNotEmpty(),
                    enter = fadeIn(tween(160)),
                    exit = fadeOut(tween(140))
                ) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = LiquidIcons.Close,
                            contentDescription = "Cancella",
                            tint = contentColor.copy(alpha = 0.70f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    )
}

/**
 * LiquidAnimatedSearchField - Animated expandable liquid glass search bar with smooth vertical transitions.
 */
@Composable
fun LiquidAnimatedSearchField(
    visible: Boolean,
    query: String,
    onQueryChange: (String) -> Unit,
    placeholderText: String = "Cerca...",
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    onSearch: ((String) -> Unit)? = null
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
        exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            LiquidSearchBar(
                query = query,
                onQueryChange = onQueryChange,
                placeholderText = placeholderText,
                modifier = modifier,
                backdropState = backdropState,
                onSearch = onSearch
            )
        }
    }
}
