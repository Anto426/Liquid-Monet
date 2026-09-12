package com.anto426.liquidmonet.glass.runtime

import com.kyant.backdrop.RuntimeShaderPrewarm
import com.kyant.backdrop.internal.BuiltinRuntimeShader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationState.UIApplicationStateActive

internal data class LiquidGlassIosCalibratedDevice(
    val device: LiquidGlassDeviceProfile,
    val calibration: LiquidGlassCalibration
)

/** Process-owned coordinator: UI recreation/caller settings never rerun the native workload. */
internal object LiquidGlassIosDeviceCalibration {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val mutex = Mutex()
    private var loading: Deferred<LiquidGlassIosCalibratedDevice>? = null

    suspend fun load(): LiquidGlassIosCalibratedDevice = mutex.withLock {
        loading ?: scope.async { loadOnce() }.also { loading = it }
    }.await()

    private suspend fun loadOnce(): LiquidGlassIosCalibratedDevice {
        val hardware = withContext(Dispatchers.Main) { probeLiquidGlassIosHardware() }
        val store = LiquidGlassIosFileCalibrationStore()
        val coordinator = LiquidGlassIosCalibration(scope, store, hardware.deviceKey, hardware.qualityTier, measure = {
            withEnvironment { environment -> LiquidGlassIosBenchmarkRunner.measure(hardware, environment::checkSafe) }
        })
        val calibration = coordinator.load()
        // Optional, small GPU preparation is separate from the persisted sampling score.
        // Failed/interrupted calibration must not immediately repeat unsafe native work.
        scope.launch {
            withTimeoutOrNull(1_000L) { RuntimeShaderPrewarm.prepare() }
            if (calibration.source == LiquidGlassCalibrationSource.MEASURED) {
                val signature = hardware.deviceKey + ":" + BuiltinRuntimeShader.entries.joinToString(":") { it.source.hashCode().toString() }
                LiquidGlassIosShaderWarmup(scope, store, signature, prepareGpu = {
                    withEnvironment { environment ->
                        val coroutine = currentCoroutineContext()
                        LiquidGlassIosOffscreenRenderSession.open(256, 256).use { session ->
                            repeat(2) { frame ->
                                coroutine.ensureActive()
                                environment.checkSafe()
                                session.render(LiquidGlassQualityTier.ULTRA, frame, 1f, warmup = true)
                            }
                            session.verifyOutput()
                        }
                    }
                }).prepare()
            }
        }
        return LiquidGlassIosCalibratedDevice(hardware.device, calibration)
    }

    private suspend fun <T> withEnvironment(block: suspend (LiquidGlassIosBenchmarkEnvironment) -> T): T {
        check(withTimeoutOrNull(2_000L) {
            while (!withContext(Dispatchers.Main) { UIApplication.sharedApplication.applicationState == UIApplicationStateActive }) delay(50)
            true
        } == true) { "No active application window" }
        val environment = withContext(Dispatchers.Main) { LiquidGlassIosBenchmarkEnvironment.start() }
        try {
            environment.checkSafe()
            return block(environment)
        } finally {
            withContext(NonCancellable + Dispatchers.Main) { environment.close() }
        }
    }
}
