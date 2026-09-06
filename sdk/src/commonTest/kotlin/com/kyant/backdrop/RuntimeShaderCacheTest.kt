package com.kyant.backdrop

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertNotSame
import androidx.compose.ui.graphics.Color

class RuntimeShaderCacheTest {
    @Test fun rejectedShaderIsNotCompiledAgainEveryFrame() {
        var attempts = 0
        val cache = RuntimeShaderCacheImpl(
            createShader = { attempts++; error("Simulated compiler rejection") },
            isSupported = { true }
        )
        repeat(120) { assertNull(cache.obtainRuntimeShader("lens", "invalid shader")) }
        assertEquals(1, attempts)
        cache.clear()
        assertNull(cache.obtainRuntimeShader("lens", "invalid shader"))
        assertEquals(2, attempts)
    }

    @Test fun unsupportedBackendNeverTriesToCompile() {
        var attempts = 0
        val cache = RuntimeShaderCacheImpl(
            createShader = { attempts++; error("Must not compile") },
            isSupported = { false }
        )
        repeat(10) { assertNull(cache.obtainRuntimeShader("lens", "shader")) }
        assertEquals(0, attempts)
    }

    @Test fun repeatedSourceReusesOneObjectAndSourceChangesInvalidateIt() {
        var compilations = 0
        val cache = RuntimeShaderCacheImpl({ compilations++; FakeShader() }, { true })
        val first = cache.obtainRuntimeShader("lens", "first program")
        repeat(120) { assertSame(first, cache.obtainRuntimeShader("lens", "first program")) }
        val changed = cache.obtainRuntimeShader("lens", "second program")
        assertNotSame(first, changed)
        assertEquals(2, compilations)
        cache.clear()
        assertNotSame(changed, cache.obtainRuntimeShader("lens", "second program"))
        assertEquals(3, compilations)
    }

    @Test fun rejectedKeyCanCompileACorrectedSource() {
        var compilations = 0
        val cache = RuntimeShaderCacheImpl({ source ->
            compilations++
            if (source == "broken") error("Rejected") else FakeShader()
        }, { true })
        repeat(10) { assertNull(cache.obtainRuntimeShader("lens", "broken")) }
        val corrected = cache.obtainRuntimeShader("lens", "correct")
        repeat(10) { assertSame(corrected, cache.obtainRuntimeShader("lens", "correct")) }
        assertEquals(2, compilations)
    }

    @Test fun differentRenderNodesNeverShareMutableShaderUniforms() {
        val first = RuntimeShaderCacheImpl({ FakeShader() }, { true })
        val second = RuntimeShaderCacheImpl({ FakeShader() }, { true })
        assertNotSame(first.obtainRuntimeShader("lens", "source"), second.obtainRuntimeShader("lens", "source"))
    }

    @Test fun customProgramChurnCannotGrowTheLocalCacheWithoutBound() {
        var compilations = 0
        val cache = RuntimeShaderCacheImpl({ compilations++; FakeShader() }, { true })
        val original = cache.obtainRuntimeShader("first", "source")
        repeat(100) { cache.obtainRuntimeShader("program-$it", "source-$it") }
        assertNotSame(original, cache.obtainRuntimeShader("first", "source"))
        assertEquals(102, compilations)
    }

}

private class FakeShader : RuntimeShader {
    override fun setFloatUniform(name: String, value: Float) {}
    override fun setFloatUniform(name: String, value1: Float, value2: Float) {}
    override fun setFloatUniform(name: String, value1: Float, value2: Float, value3: Float) {}
    override fun setFloatUniform(name: String, value1: Float, value2: Float, value3: Float, value4: Float) {}
    override fun setFloatUniform(name: String, values: FloatArray) {}
    override fun setIntUniform(name: String, value: Int) {}
    override fun setIntUniform(name: String, value1: Int, value2: Int) {}
    override fun setIntUniform(name: String, value1: Int, value2: Int, value3: Int) {}
    override fun setIntUniform(name: String, value1: Int, value2: Int, value3: Int, value4: Int) {}
    override fun setIntUniform(name: String, values: IntArray) {}
    override fun setColorUniform(name: String, color: Color) {}
}
