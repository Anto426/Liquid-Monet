package com.anto426.liquidmonet.glass.runtime

import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.CLOCK_MONOTONIC_RAW
import platform.posix.CLOCK_THREAD_CPUTIME_ID
import platform.posix.clock_gettime_nsec_np

@OptIn(ExperimentalForeignApi::class)
internal fun liquidGlassIosNanoTime(): Long = clock_gettime_nsec_np(CLOCK_MONOTONIC_RAW.toUInt()).toLong()

/** Same arithmetic and 4 x 1 MiB observable transfers as Android, timed with thread CPU time. */
@OptIn(ExperimentalForeignApi::class, ExperimentalAtomicApi::class)
internal object LiquidGlassIosCpuProbe {
    fun measure(checkActive: () -> Unit): Pair<Long, Long> {
        val sink = AtomicLong(0.0.toBits())
        val source = IntArray(256 * 1024) { it xor 0x5a5a }
        val destination = IntArray(source.size)
        fun compute() {
            var value = Double.fromBits(sink.load())
            repeat(2_048) { index ->
                val angle = index * 0.001 + value * 0.00001
                value += sin(angle) * cos(angle * 0.7) + (index and 31) * 0.01
            }
            sink.store(value.toBits())
        }
        fun copy() {
            repeat(4) { pass ->
                source.copyInto(destination)
                val value = Double.fromBits(sink.load())
                sink.store((value + destination[((pass * 65_537 + value.toInt()) and Int.MAX_VALUE) % destination.size]).toBits())
            }
        }
        fun timed(block: () -> Unit): Long {
            val start = clock_gettime_nsec_np(CLOCK_THREAD_CPUTIME_ID.toUInt()).toLong()
            check(start > 0L) { "Thread CPU clock unavailable" }
            block()
            return (clock_gettime_nsec_np(CLOCK_THREAD_CPUTIME_ID.toUInt()).toLong() - start).also {
                check(it > 0L) { "Invalid CPU sample" }
            }
        }
        repeat(8) { checkActive(); compute(); copy() }
        val cpu = ArrayList<Long>(5)
        val memory = ArrayList<Long>(5)
        repeat(5) {
            checkActive()
            cpu += timed(::compute)
            memory += timed(::copy)
        }
        return liquidGlassP90(cpu) to liquidGlassP90(memory)
    }
}
