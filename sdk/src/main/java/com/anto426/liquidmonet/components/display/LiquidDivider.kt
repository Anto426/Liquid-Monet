package com.anto426.liquidmonet.components.display

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * LiquidHorizontalDivider - Subtle Liquid Glass Divider Line with Specular Gradient.
 */
@Composable
fun LiquidHorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color? = null
) {
    val baseColor = color ?: MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.72f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        baseColor.copy(alpha = 0f),
                        baseColor,
                        baseColor,
                        baseColor.copy(alpha = 0f)
                    )
                )
            )
    )
}

/**
 * LiquidVerticalDivider - Vertical variation of liquid glass divider.
 */
@Composable
fun LiquidVerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color? = null
) {
    val baseColor = color ?: MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.72f)

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(thickness)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        baseColor.copy(alpha = 0f),
                        baseColor,
                        baseColor,
                        baseColor.copy(alpha = 0f)
                    )
                )
            )
    )
}
