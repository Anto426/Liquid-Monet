package com.kyant.backdrop

import android.graphics.RuntimeShader as NativeRuntimeShader
import androidx.annotation.RequiresApi
import com.kyant.backdrop.internal.BuiltinRuntimeShader
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Bounded process-local reserve. Every render node receives its own mutable uniform state. */
@RequiresApi(33)
internal object RuntimeShaderPrewarm {
    private class Entry(val source: String) {
        val prepared = ConcurrentLinkedQueue<NativeRuntimeShader>()
        val filling = AtomicBoolean(false)
        @Volatile var failure: Throwable? = null
    }

    private val entries = ConcurrentHashMap<String, Entry>()
    private val worker = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    @Volatile private var reserveSize = 4

    suspend fun prepare(instancesPerShader: Int): Int = withContext(Dispatchers.Default) {
        reserveSize = instancesPerShader.coerceIn(2, 8)
        for (shader in BuiltinRuntimeShader.entries) {
            val entry = entries.getOrPut(shader.source) { Entry(shader.source) }
            if (entry.filling.compareAndSet(false, true)) fill(entry)
        }
        entries.values.count { it.failure == null && it.prepared.isNotEmpty() }
    }

    fun obtain(source: String): NativeRuntimeShader {
        val entry = entries[source] ?: return NativeRuntimeShader(source)
        entry.failure?.let { throw IllegalArgumentException("Shader rejected during warm-up", it) }
        val prepared = entry.prepared.poll()
        // A cache hit never waits for compilation or holds the worker's lock.
        if (entry.prepared.size <= reserveSize / 2 && entry.filling.compareAndSet(false, true)) {
            worker.launch { fill(entry) }
        }
        return prepared ?: NativeRuntimeShader(source)
    }

    private fun fill(entry: Entry) {
        try {
            if (entry.failure != null) return
            // Bound each refill even if the UI consumes instances while this batch compiles.
            repeat((reserveSize - entry.prepared.size).coerceAtLeast(0)) {
                entry.prepared.add(NativeRuntimeShader(entry.source))
            }
        } catch (e: Exception) {
            entry.failure = e
        } catch (e: LinkageError) {
            entry.failure = e
        } catch (e: OutOfMemoryError) {
            entries.values.forEach { it.prepared.clear() }
            entry.failure = e
        } finally {
            entry.filling.set(false)
        }
    }
}
