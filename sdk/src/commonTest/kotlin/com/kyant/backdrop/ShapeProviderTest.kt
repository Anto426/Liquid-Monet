package com.kyant.backdrop

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.kyant.backdrop.internal.ShapeProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame

class ShapeProviderTest {
    @Test fun retainedGeometryRespondsToAccessibilityFontScale() {
        var computations = 0
        val shape = object : Shape {
            override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
                computations++
                return Outline.Rectangle(Rect(0f, 0f, size.width * density.fontScale, size.height))
            }
        }
        val cached = ShapeProvider { shape }.shape
        val size = Size(100f, 50f)
        val first = cached.createOutline(size, LayoutDirection.Ltr, Density(1f, 1f))
        repeat(100) { assertSame(first, cached.createOutline(size, LayoutDirection.Ltr, Density(1f, 1f))) }
        assertEquals(1, computations)
        val enlarged = cached.createOutline(size, LayoutDirection.Ltr, Density(1f, 1.5f))
        assertNotEquals(first, enlarged)
        assertEquals(2, computations)
    }
}
