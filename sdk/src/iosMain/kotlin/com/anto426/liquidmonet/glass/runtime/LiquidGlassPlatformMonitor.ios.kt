package com.anto426.liquidmonet.glass.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf

@Composable
internal actual fun rememberLiquidGlassPerformanceState(
    liquidIntensity: Float
): State<LiquidGlassPerformanceState> = remember(liquidIntensity) {
    mutableStateOf(
        LiquidGlassPerformanceState.Fallback.copy(
            liquidIntensity = liquidIntensity.coerceIn(0f, 1f)
        )
    )
}
