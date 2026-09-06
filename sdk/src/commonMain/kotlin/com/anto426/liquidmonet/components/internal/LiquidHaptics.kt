package com.anto426.liquidmonet.components.internal

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/** Semantic haptic cues used by Liquid Monet components. */
internal enum class LiquidHapticCue {
    Selection,
    Input,
    Threshold,
    Action
}

/**
 * Keeps platform haptic constants out of components.
 *
 * Compose Multiplatform currently exposes [HapticFeedbackType.TextHandleMove] as the portable
 * lightweight pulse. The semantic cue remains distinct here so platform mappings can evolve
 * without changing every control.
 */
internal fun HapticFeedback.performLiquidHaptic(cue: LiquidHapticCue) {
    val feedbackType = when (cue) {
        LiquidHapticCue.Selection,
        LiquidHapticCue.Input,
        LiquidHapticCue.Threshold,
        LiquidHapticCue.Action -> HapticFeedbackType.TextHandleMove
    }
    performHapticFeedback(feedbackType)
}
