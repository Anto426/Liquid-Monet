package com.anto426.liquidmonet.glass.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

@Composable
internal expect fun rememberLiquidGlassPerformanceState(
    liquidIntensity: Float
): State<LiquidGlassPerformanceState>
