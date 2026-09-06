package com.anto426.liquidmonet.glass.runtime

import android.os.Debug
import kotlin.math.cos
import kotlin.math.sin

/** Single-core arithmetic/transforms plus memory traffic; core count alone never sets a tier. */
internal object LiquidGlassCpuProbe {
    @Volatile private var sink = 0.0

    @androidx.annotation.WorkerThread
    fun measure(): Pair<Long, Long> {
        val source = IntArray(256 * 1024) { it xor 0x5a5a }
        val destination = IntArray(source.size)
        fun compute() {
            var value = sink
            repeat(2_048) { index ->
                val angle = index * 0.001 + value * 0.00001
                value += sin(angle) * cos(angle * 0.7) + (index and 31) * 0.01
            }
            sink = value
        }
        fun copy() {
            repeat(4) { pass ->
                source.copyInto(destination)
                // Make each transfer observable before the next one, including under ART JIT.
                sink += destination[((pass * 65_537 + sink.toInt()) and Int.MAX_VALUE) % destination.size]
            }
        }
        // Warm the arithmetic loop before sampling ART execution. Thread CPU time excludes
        // scheduling contention from unrelated startup work and other processes.
        repeat(8) { compute(); copy() }
        val cpuSamples = ArrayList<Long>(5)
        val memorySamples = ArrayList<Long>(5)
        repeat(5) {
            val cpuStart = Debug.threadCpuTimeNanos()
            compute()
            cpuSamples += (Debug.threadCpuTimeNanos() - cpuStart).coerceAtLeast(1)
            val memoryStart = Debug.threadCpuTimeNanos()
            copy()
            memorySamples += (Debug.threadCpuTimeNanos() - memoryStart).coerceAtLeast(1)
        }
        return liquidGlassP90(cpuSamples) to liquidGlassP90(memorySamples)
    }
}
