package com.kyant.backdrop

sealed interface RuntimeShaderCache {

    fun obtainRuntimeShader(key: String, string: String): RuntimeShader?
}

internal class RuntimeShaderCacheImpl(
    private val createShader: (String) -> RuntimeShader = ::RuntimeShader,
    private val isSupported: () -> Boolean = ::isRuntimeShaderSupported
) : RuntimeShaderCache {

    private data class Prepared(val source: String, val shader: RuntimeShader)
    private val runtimeShaders = linkedMapOf<String, Prepared>()
    private val rejectedShaders = linkedMapOf<String, String>()

    override fun obtainRuntimeShader(key: String, string: String): RuntimeShader? {
        if (!isSupported() || rejectedShaders[key] == string) return null
        runtimeShaders[key]?.takeIf { it.source == string }?.let { return it.shader }
        runtimeShaders.remove(key)
        rejectedShaders.remove(key)
        return try {
            val shader = createShader(string)
            if (runtimeShaders.size >= MaxEntries) runtimeShaders.remove(runtimeShaders.keys.first())
            runtimeShaders[key] = Prepared(string, shader)
            shader
        } catch (t: Throwable) {
            // A corrected source may retry the same key; an unchanged rejected program may not.
            if (rejectedShaders.size >= MaxEntries) rejectedShaders.remove(rejectedShaders.keys.first())
            rejectedShaders[key] = string
            t.printStackTrace()
            null
        }
    }

    fun clear() {
        runtimeShaders.clear()
        rejectedShaders.clear()
    }

    private companion object { const val MaxEntries = 8 }
}
