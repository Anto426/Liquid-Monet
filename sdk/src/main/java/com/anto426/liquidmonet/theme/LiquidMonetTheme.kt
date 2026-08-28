package com.anto426.liquidmonet.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.annotation.FloatRange
import androidx.compose.ui.platform.LocalContext
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPerformanceManager
import com.anto426.liquidmonet.glass.runtime.LocalLiquidGlassPerformance
import com.anto426.liquidmonet.theme.monet.LiquidMonetEngine
import com.anto426.liquidmonet.theme.monet.LiquidMonetPresets
import com.anto426.liquidmonet.theme.monet.LiquidMonetSeed

/**
 * LiquidMonetTheme - Official Google Material 3 Expressive Theme with MonetEngine Dynamic Color.
 * Fuses Material 3 Expressive color schemes & motion with AGSL Snell Glass Backdrops.
 *
 * [liquidIntensity] is clamped to `0f..1f` and is combined with the automatic device profile.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LiquidMonetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useMonetEngine: Boolean = true,
    customMonetSeed: LiquidMonetSeed = LiquidMonetPresets.Sapphire,
    @FloatRange(from = 0.0, to = 1.0)
    liquidIntensity: Float = 1f,
    glassColors: LiquidGlassColors? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val applicationContext = context.applicationContext
    val colorScheme = LiquidMonetEngine.generateColorScheme(
        context = context,
        darkTheme = darkTheme,
        useSystemDynamic = useMonetEngine,
        customSeed = customMonetSeed
    )

    val performanceManager = remember(applicationContext) {
        LiquidGlassPerformanceManager(
            context = applicationContext,
            liquidIntensity = liquidIntensity
        )
    }
    DisposableEffect(performanceManager) {
        performanceManager.start()
        onDispose(performanceManager::close)
    }
    LaunchedEffect(performanceManager, liquidIntensity) {
        performanceManager.setLiquidIntensity(liquidIntensity)
    }

    val glassPerformance by performanceManager.state
    val resolvedGlassColors = glassColors ?: LiquidGlassDefaults.colors(colorScheme)
    CompositionLocalProvider(
        LocalLiquidGlassPerformance provides glassPerformance,
        LocalLiquidGlassColors provides resolvedGlassColors
    ) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            motionScheme = MotionScheme.expressive(),
            content = content
        )
    }
}
