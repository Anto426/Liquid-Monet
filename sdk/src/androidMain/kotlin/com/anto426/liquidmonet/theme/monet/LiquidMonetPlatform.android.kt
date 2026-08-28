package com.anto426.liquidmonet.theme.monet

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun platformMonetSeed(fallback: LiquidMonetSeed): LiquidMonetSeed {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return fallback
    val context = LocalContext.current
    return LiquidMonetSeed(
        lightPrimary = context.systemColor("system_accent1_600", fallback.lightPrimary),
        darkPrimary = context.systemColor("system_accent1_200", fallback.darkPrimary),
        lightSecondary = context.systemColor("system_accent2_600", fallback.lightSecondary),
        darkSecondary = context.systemColor("system_accent2_200", fallback.darkSecondary),
        lightTertiary = context.systemColor("system_accent3_600", fallback.lightTertiary),
        darkTertiary = context.systemColor("system_accent3_200", fallback.darkTertiary)
    )
}

private fun Context.systemColor(name: String, fallback: Color): Color {
    val id = resources.getIdentifier(name, "color", "android")
    if (id == 0) return fallback
    return runCatching { Color(getColor(id)) }.getOrDefault(fallback)
}
