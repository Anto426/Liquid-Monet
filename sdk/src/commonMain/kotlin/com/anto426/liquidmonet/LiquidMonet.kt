package com.anto426.liquidmonet

import com.anto426.liquidmonet.components.menu.LiquidMenu
import com.anto426.liquidmonet.icons.LiquidIcons
import com.anto426.liquidmonet.motion.LiquidMotion
import com.anto426.liquidmonet.theme.LiquidGlassTheme
import com.anto426.liquidmonet.theme.monet.LiquidMonetPresets

/**
 * Liquid Monet - Unified SDK Facade
 *
 * Provides central metadata, documentation pointers, and top-level shortcuts
 * to the core subsystems:
 * - [LiquidMotion] for animations, springs, easings and transitions.
 * - [LiquidGlassTheme] for dynamic Monet color palettes, blur effects, and shapes.
 * - [LiquidMenu] for optical glass menus, dropdowns, and morphing actions.
 * - [LiquidIcons] for unified iconography.
 * - [LiquidMonetPresets] for standard Monet themes and color schemes.
 */
object LiquidMonet {
    const val Version: String = "1.0.0"

    val Motion: LiquidMotion get() = LiquidMotion
    val Menu: LiquidMenu get() = LiquidMenu
    val Icons: LiquidIcons get() = LiquidIcons
    val Theme: LiquidGlassTheme get() = LiquidGlassTheme
    val Presets: LiquidMonetPresets get() = LiquidMonetPresets
}
