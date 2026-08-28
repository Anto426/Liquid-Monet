package com.anto426.liquidmonet.glass.runtime

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun rememberLiquidGlassPerformanceState(
    liquidIntensity: Float
): State<LiquidGlassPerformanceState> {
    val applicationContext = LocalContext.current.applicationContext
    val manager = remember(applicationContext) {
        LiquidGlassPerformanceManager(applicationContext, liquidIntensity)
    }

    DisposableEffect(manager) {
        manager.start()
        onDispose(manager::close)
    }
    LaunchedEffect(manager, liquidIntensity) {
        manager.setLiquidIntensity(liquidIntensity)
    }
    return manager.state
}
