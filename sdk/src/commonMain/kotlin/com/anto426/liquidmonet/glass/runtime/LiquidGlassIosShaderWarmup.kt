package com.anto426.liquidmonet.glass.runtime

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

/** Source-versioned crash guard. A late driver return cannot clear a timed-out marker. */
internal class LiquidGlassIosShaderWarmup(
    private val scope: CoroutineScope,
    private val store: LiquidGlassIosCalibrationStore,
    private val signature: String,
    private val prepareGpu: suspend () -> Unit,
    private val timeoutMillis: Long = 1_500L
) {
    private val mutex = Mutex()
    private var preparation: Deferred<Boolean>? = null

    suspend fun prepare(): Boolean {
        val operation = mutex.withLock {
            preparation ?: scope.async {
                if (!store.beginWarmup(signature)) return@async false
                val worker = scope.async {
                    try { prepareGpu(); true }
                    catch (error: CancellationException) { throw error }
                    catch (_: Exception) { false }
                }
                val completed = withTimeoutOrNull(timeoutMillis) { worker.await() }
                if (completed == null) worker.cancel()
                if (completed == true) store.finishWarmup()
                completed == true
            }.also { preparation = it }
        }
        return operation.await()
    }
}
