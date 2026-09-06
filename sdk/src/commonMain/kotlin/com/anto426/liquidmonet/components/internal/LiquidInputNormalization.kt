package com.anto426.liquidmonet.components.internal

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Shared boundary normalization for public Liquid component inputs. */
internal object LiquidInputNormalization {
    fun unit(value: Float, fallback: Float = 0f): Float =
        if (value.isFinite()) value.coerceIn(0f, 1f) else fallback.coerceIn(0f, 1f)

    fun optionalUnit(value: Float?, fallback: Float = 0f): Float? =
        value?.let { unit(it, fallback) }

    fun positive(value: Dp, name: String): Dp {
        require(value > 0.dp) { "$name must be positive." }
        return value
    }

    fun nonNegative(value: Dp, name: String): Dp {
        require(value >= 0.dp) { "$name must not be negative." }
        return value
    }

    fun positive(value: Int, name: String): Int {
        require(value > 0) { "$name must be greater than zero." }
        return value
    }

    fun nonNegative(value: Int, name: String): Int {
        require(value >= 0) { "$name must not be negative." }
        return value
    }

    fun finite(value: Float, name: String): Float {
        require(value.isFinite()) { "$name must be finite." }
        return value
    }

    fun positive(value: Float, name: String): Float {
        require(value.isFinite() && value > 0f) { "$name must be finite and positive." }
        return value
    }

    fun nonNegative(value: Float, name: String): Float {
        require(value.isFinite() && value >= 0f) { "$name must be finite and non-negative." }
        return value
    }

    fun index(index: Int, itemCount: Int): Int =
        if (itemCount <= 0) 0 else index.coerceIn(0, itemCount - 1)
}
