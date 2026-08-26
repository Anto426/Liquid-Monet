package com.anto426.liquidmonet.theme.monet

import android.content.Context
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

internal val White = Color(0xFFFFFFFF)
internal val Black = Color(0xFF000000)

fun contentColorFor(color: Color): Color {
    return if (color.luminance() > 0.179f) Black else White
}

fun blend(base: Color, overlay: Color, amount: Float): Color {
    val safeAmount = amount.coerceIn(0f, 1f)
    val inverse = 1f - safeAmount
    return Color(
        red = base.red * inverse + overlay.red * safeAmount,
        green = base.green * inverse + overlay.green * safeAmount,
        blue = base.blue * inverse + overlay.blue * safeAmount,
        alpha = 1f
    )
}

data class LiquidMonetSeed(
    val lightPrimary: Color,
    val darkPrimary: Color,
    val lightSecondary: Color,
    val darkSecondary: Color,
    val lightTertiary: Color,
    val darkTertiary: Color
)

object LiquidMonetPresets {
    val Sapphire = LiquidMonetSeed(
        lightPrimary = Color(0xFF0061A4),
        darkPrimary = Color(0xFF9ECAFF),
        lightSecondary = Color(0xFF535F70),
        darkSecondary = Color(0xFFBBC7DB),
        lightTertiary = Color(0xFF6B5778),
        darkTertiary = Color(0xFFD6BAE4)
    )

    val Emerald = LiquidMonetSeed(
        lightPrimary = Color(0xFF006C4C),
        darkPrimary = Color(0xFF75DCAC),
        lightSecondary = Color(0xFF4C6356),
        darkSecondary = Color(0xFFB3CCBC),
        lightTertiary = Color(0xFF3D6373),
        darkTertiary = Color(0xFFA5CCE0)
    )

    val Sunset = LiquidMonetSeed(
        lightPrimary = Color(0xFF8B5000),
        darkPrimary = Color(0xFFFFB870),
        lightSecondary = Color(0xFF715B41),
        darkSecondary = Color(0xFFDFC2A4),
        lightTertiary = Color(0xFF53643E),
        darkTertiary = Color(0xFFB8CDA2)
    )

    val Violet = LiquidMonetSeed(
        lightPrimary = Color(0xFF6B4EA2),
        darkPrimary = Color(0xFFD3BBFF),
        lightSecondary = Color(0xFF625B71),
        darkSecondary = Color(0xFFCCC2DC),
        lightTertiary = Color(0xFF7E5260),
        darkTertiary = Color(0xFFEFB8C8)
    )
}

object LiquidMonetEngine {

    fun generateColorScheme(
        context: Context,
        darkTheme: Boolean,
        useSystemDynamic: Boolean = true,
        customSeed: LiquidMonetSeed = LiquidMonetPresets.Sapphire
    ): ColorScheme {
        val seed = if (useSystemDynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            extractSystemMonetSeed(context, customSeed)
        } else {
            customSeed
        }
        return seed.toMaterial3ExpressiveScheme(darkTheme)
    }

    private fun extractSystemMonetSeed(context: Context, fallback: LiquidMonetSeed): LiquidMonetSeed {
        val lightPrimary = getSystemColor(context, "system_accent1_600", fallback.lightPrimary)
        val darkPrimary = getSystemColor(context, "system_accent1_200", fallback.darkPrimary)
        val lightSecondary = getSystemColor(context, "system_accent2_600", fallback.lightSecondary)
        val darkSecondary = getSystemColor(context, "system_accent2_200", fallback.darkSecondary)
        val lightTertiary = getSystemColor(context, "system_accent3_600", fallback.lightTertiary)
        val darkTertiary = getSystemColor(context, "system_accent3_200", fallback.darkTertiary)

        return LiquidMonetSeed(
            lightPrimary = lightPrimary,
            darkPrimary = darkPrimary,
            lightSecondary = lightSecondary,
            darkSecondary = darkSecondary,
            lightTertiary = lightTertiary,
            darkTertiary = darkTertiary
        )
    }

    private fun getSystemColor(context: Context, name: String, fallback: Color): Color {
        val id = context.resources.getIdentifier(name, "color", "android")
        if (id == 0) return fallback
        return try {
            Color(context.getColor(id))
        } catch (_: Exception) {
            fallback
        }
    }

    private fun LiquidMonetSeed.toMaterial3ExpressiveScheme(darkTheme: Boolean): ColorScheme {
        val primary = (if (darkTheme) darkPrimary else lightPrimary).copy(alpha = 1f)
        val secondary = (if (darkTheme) darkSecondary else lightSecondary).copy(alpha = 1f)
        val tertiary = (if (darkTheme) darkTertiary else lightTertiary).copy(alpha = 1f)
        val error = Color(0xFFFFB4AB)
        val target = if (darkTheme) Black else White

        val primaryContainer = blend(primary, target, if (darkTheme) 0.42f else 0.78f)
        val secondaryContainer = blend(secondary, target, if (darkTheme) 0.40f else 0.76f)
        val tertiaryContainer = blend(tertiary, target, if (darkTheme) 0.40f else 0.76f)
        val errorContainer = blend(error, target, if (darkTheme) 0.40f else 0.78f)

        // 100% PURE NEUTRAL SURFACES - Zero Monet tint contamination on containers/surfaces/dialogs/sheets
        return if (darkTheme) {
            val neutralSurface = Color(0xFF121212)
            val neutralBackground = Color(0xFF090A0F)
            val neutralOnSurface = Color(0xFFE6E1E5)
            val neutralOnSurfaceVariant = Color(0xFFCAC4D0)

            darkColorScheme(
                primary = primary,
                onPrimary = contentColorFor(primary),
                primaryContainer = primaryContainer,
                onPrimaryContainer = contentColorFor(primaryContainer),
                secondary = secondary,
                onSecondary = contentColorFor(secondary),
                secondaryContainer = secondaryContainer,
                onSecondaryContainer = contentColorFor(secondaryContainer),
                tertiary = tertiary,
                onTertiary = contentColorFor(tertiary),
                tertiaryContainer = tertiaryContainer,
                onTertiaryContainer = contentColorFor(tertiaryContainer),
                error = error,
                onError = contentColorFor(error),
                errorContainer = errorContainer,
                onErrorContainer = contentColorFor(errorContainer),
                background = neutralBackground,
                onBackground = neutralOnSurface,
                surface = neutralSurface,
                onSurface = neutralOnSurface,
                surfaceDim = Color(0xFF0E0E0E),
                surfaceBright = Color(0xFF323232),
                surfaceContainerLowest = Color(0xFF0C0C0C),
                surfaceContainerLow = Color(0xFF161616),
                surfaceContainer = Color(0xFF1E1E1E),
                surfaceContainerHigh = Color(0xFF262626),
                surfaceContainerHighest = Color(0xFF303030),
                surfaceVariant = Color(0xFF222222),
                onSurfaceVariant = neutralOnSurfaceVariant,
                outline = Color(0xFF49454F),
                outlineVariant = Color(0xFF303030),
                inverseSurface = Color(0xFFE6E1E5),
                inverseOnSurface = Color(0xFF313033),
                inversePrimary = blend(primary, White, 0.34f),
                surfaceTint = Color.Transparent, // ZERO TINTING OF SURFACES
                scrim = Black
            )
        } else {
            val neutralSurface = Color(0xFFFFFFFF)
            val neutralBackground = Color(0xFFFAFAFA)
            val neutralOnSurface = Color(0xFF1C1B1F)
            val neutralOnSurfaceVariant = Color(0xFF49454F)

            lightColorScheme(
                primary = primary,
                onPrimary = contentColorFor(primary),
                primaryContainer = primaryContainer,
                onPrimaryContainer = contentColorFor(primaryContainer),
                secondary = secondary,
                onSecondary = contentColorFor(secondary),
                secondaryContainer = secondaryContainer,
                onSecondaryContainer = contentColorFor(secondaryContainer),
                tertiary = tertiary,
                onTertiary = contentColorFor(tertiary),
                tertiaryContainer = tertiaryContainer,
                onTertiaryContainer = contentColorFor(tertiaryContainer),
                error = error,
                onError = contentColorFor(error),
                errorContainer = errorContainer,
                onErrorContainer = contentColorFor(errorContainer),
                background = neutralBackground,
                onBackground = neutralOnSurface,
                surface = neutralSurface,
                onSurface = neutralOnSurface,
                surfaceDim = Color(0xFFDED8E1),
                surfaceBright = Color(0xFFFEF7FF),
                surfaceContainerLowest = Color(0xFFFFFFFF),
                surfaceContainerLow = Color(0xFFF7F2FA),
                surfaceContainer = Color(0xFFF3EDF7),
                surfaceContainerHigh = Color(0xFFECE6F0),
                surfaceContainerHighest = Color(0xFFE6E0E9),
                surfaceVariant = Color(0xFFE7E0EC),
                onSurfaceVariant = neutralOnSurfaceVariant,
                outline = Color(0xFF79747E),
                outlineVariant = Color(0xFFCAC4D0),
                inverseSurface = Color(0xFF313033),
                inverseOnSurface = Color(0xFFF4EFF4),
                inversePrimary = blend(primary, Black, 0.22f),
                surfaceTint = Color.Transparent, // ZERO TINTING OF SURFACES
                scrim = Black
            )
        }
    }
}
