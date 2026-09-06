package com.anto426.liquidmonet.theme.monet

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration

@Composable
internal actual fun platformMonetSeed(fallback: LiquidMonetSeed): LiquidMonetSeed {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return fallback
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    return remember(context, configuration, fallback) {
        LiquidMonetSeed(
            lightPrimary = context.systemColor(android.R.color.system_accent1_600, fallback.lightPrimary),
            darkPrimary = context.systemColor(android.R.color.system_accent1_200, fallback.darkPrimary),
            lightSecondary = context.systemColor(android.R.color.system_accent2_600, fallback.lightSecondary),
            darkSecondary = context.systemColor(android.R.color.system_accent2_200, fallback.darkSecondary),
            lightTertiary = context.systemColor(android.R.color.system_accent3_600, fallback.lightTertiary),
            darkTertiary = context.systemColor(android.R.color.system_accent3_200, fallback.darkTertiary)
        )
    }
}

private fun Context.systemColor(id: Int, fallback: Color): Color {
    return runCatching { Color(getColor(id)) }.getOrDefault(fallback)
}
