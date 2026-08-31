package com.anto426.liquidmonet.components.charts

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Single data point for Liquid Line and Bar Charts.
 */
@Immutable
data class LiquidChartEntry(
    val label: String,
    val value: Float,
    val secondaryValue: Float? = null,
    val color: Color? = null,
    val payload: Any? = null
)

/**
 * Single slice entry for Liquid Donut and Pie Charts.
 */
@Immutable
data class LiquidPieEntry(
    val label: String,
    val value: Float,
    val color: Color? = null,
    val payload: Any? = null
)

/**
 * Configuration style for optical Liquid Monet chart rendering.
 */
@Immutable
data class LiquidChartStyle(
    val showGridLines: Boolean = true,
    val showRefractiveArea: Boolean = true,
    val showDataDroplets: Boolean = true,
    val smoothCurves: Boolean = true,
    val strokeWidth: Dp = 3.dp,
    val dropletRadius: Dp = 5.dp,
    val refractiveGlow: Boolean = true
)
