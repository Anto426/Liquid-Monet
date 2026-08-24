package com.kyant.backdrop

import org.intellij.lang.annotations.Language

sealed interface RuntimeShaderCache {

    fun obtainRuntimeShader(key: String, @Language("AGSL") string: String): RuntimeShader?
}

internal class RuntimeShaderCacheImpl : RuntimeShaderCache {

    private val runtimeShaders = mutableMapOf<String, RuntimeShader>()

    override fun obtainRuntimeShader(key: String, string: String): RuntimeShader? {
        if (!isRuntimeShaderSupported()) return null
        return runtimeShaders.getOrPut(key) {
            try {
                RuntimeShader(string)
            } catch (t: Throwable) {
                t.printStackTrace()
                return null
            }
        }
    }

    fun clear() {
        runtimeShaders.clear()
    }
}
