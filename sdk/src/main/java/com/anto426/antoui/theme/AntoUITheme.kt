package com.anto426.antoui.theme

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
import com.anto426.antoui.glass.runtime.AntoGlassPerformanceManager
import com.anto426.antoui.glass.runtime.AntoGlassPresets
import com.anto426.antoui.glass.runtime.LocalAntoGlassPerformance
import com.anto426.antoui.glass.runtime.LocalAntoGlassTokens
import com.anto426.antoui.theme.monet.AntoMonetEngine
import com.anto426.antoui.theme.monet.AntoMonetPresets
import com.anto426.antoui.theme.monet.AntoMonetSeed

/**
 * AntoUITheme - Official Google Material 3 Expressive Theme with MonetEngine Dynamic Color.
 * Fuses Material 3 Expressive color schemes & motion with AGSL Snell Glass Backdrops.
 *
 * [liquidIntensity] is clamped to `0f..1f` and is combined with the automatic device profile.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AntoUITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useMonetEngine: Boolean = true,
    customMonetSeed: AntoMonetSeed = AntoMonetPresets.Sapphire,
    @FloatRange(from = 0.0, to = 1.0)
    liquidIntensity: Float = 1f,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val applicationContext = context.applicationContext
    val colorScheme = AntoMonetEngine.generateColorScheme(
        context = context,
        darkTheme = darkTheme,
        useSystemDynamic = useMonetEngine,
        customSeed = customMonetSeed
    )

    val performanceManager = remember(applicationContext) {
        AntoGlassPerformanceManager(
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
    val glassTokens = remember(glassPerformance) {
        AntoGlassPresets.Standard.resolve(glassPerformance)
    }

    CompositionLocalProvider(
        LocalAntoGlassPerformance provides glassPerformance,
        LocalAntoGlassTokens provides glassTokens
    ) {
        MaterialExpressiveTheme(
            colorScheme = colorScheme,
            motionScheme = MotionScheme.expressive(),
            content = content
        )
    }
}

/**
 * LiquidMonetTheme - Official Theme for Liquid Monet SDK.
 */
@Composable
fun LiquidMonetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useMonetEngine: Boolean = true,
    customMonetSeed: AntoMonetSeed = AntoMonetPresets.Sapphire,
    @FloatRange(from = 0.0, to = 1.0)
    liquidIntensity: Float = 1f,
    content: @Composable () -> Unit
) {
    AntoUITheme(
        darkTheme = darkTheme,
        useMonetEngine = useMonetEngine,
        customMonetSeed = customMonetSeed,
        liquidIntensity = liquidIntensity,
        content = content
    )
}

