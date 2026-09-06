package com.anto426.liquidmonet.glass.runtime

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.FloatRange
import androidx.compose.runtime.State
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Publishes a calibrated profile and asynchronously collected diagnostics as Compose [State].
 *
 * The manager retains only [Context.getApplicationContext] and owns every callback it registers.
 * Call [start] when the state becomes visible and [close] when its lifecycle ends. `LiquidMonetTheme`
 * does this automatically.
 */
@Stable
internal class LiquidGlassPerformanceManager(
    context: Context,
    private val calibratedDevice: LiquidGlassCalibratedDevice,
    @FloatRange(from = 0.0, to = 1.0)
    liquidIntensity: Float = 1f,
    private val maximumQuality: LiquidGlassQualityTier = LiquidGlassQualityTier.HIGH,
    private val reduceMotion: Boolean = false
) : AutoCloseable {

    private val applicationContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private val deviceProfile = calibratedDevice.device
    // A caller's explicit cap never overwrites the device calibration on disk.
    private val selectedQualityTier = calibratedDevice.calibration.qualityTier
    private val diagnosticsMonitor = LiquidGlassDiagnosticsMonitor(applicationContext, deviceProfile)

    private var normalizedLiquidIntensity = normalizeIntensity(liquidIntensity)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var observation: Job? = null
    private var diagnostics = LiquidGlassDiagnostics()
    private val mutableState = mutableStateOf(readState())

    val state: State<LiquidGlassPerformanceState>
        get() = mutableState

    /** The platform observer owns its callbacks and all system reads on an IO dispatcher. */
    fun start() = runOnMain {
        if (observation?.isActive == true) return@runOnMain
        observation = scope.launch {
            diagnosticsMonitor.observe().collect { snapshot ->
                diagnostics = snapshot
                updateStateNow()
            }
        }
    }

    /**
     * Changes the global liquid strength. Non-finite input falls back to `1f`; all other values
     * are clamped to `0f..1f`.
     */
    fun setLiquidIntensity(@FloatRange(from = 0.0, to = 1.0) value: Float) {
        val normalized = normalizeIntensity(value)
        runOnMain {
            if (normalizedLiquidIntensity == normalized) return@runOnMain
            normalizedLiquidIntensity = normalized
            updateStateNow()
        }
    }

    /** Requests a background diagnostic read without querying system services here. */
    fun refresh() = diagnosticsMonitor.refresh()

    override fun close() = runOnMain {
        observation?.cancel()
        observation = null
    }

    private fun runOnMain(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            mainHandler.post(block)
        }
    }

    private fun updateStateNow() {
        mutableState.value = readState()
    }

    private fun readState(): LiquidGlassPerformanceState {
        val availableMemoryBytes = diagnostics.availableMemoryBytes
        val memoryPressureHigh = diagnostics.memoryPressureHigh
        val powerSaveMode = diagnostics.powerSaveMode
        val thermalStatus = diagnostics.thermalStatus

        // Material fidelity follows the caller; calibration only budgets texture resolution.
        val baseScales = scalesFor(maximumQuality)
        val intensity = normalizedLiquidIntensity

        return LiquidGlassPerformanceState(
            device = deviceProfile,
            qualityTier = selectedQualityTier,
            thermalStatus = thermalStatus,
            isPowerSaveMode = powerSaveMode,
            isMemoryPressureHigh = memoryPressureHigh,
            availableMemoryBytes = availableMemoryBytes,
            liquidIntensity = intensity,
            blurScale = if (deviceProfile.supportsRenderEffect) baseScales.blur * intensity else 0f,
            refractionScale = if (deviceProfile.supportsRuntimeShader) baseScales.refraction * intensity else 0f,
            motionScale = if (reduceMotion) 0f else baseScales.motion,
            chromaticAberrationScale = if (deviceProfile.supportsRuntimeShader) {
                baseScales.chromaticAberration * intensity
            } else {
                0f
            },
            calibration = calibratedDevice.calibration,
            opticalQualityTier = maximumQuality,
            renderResolutionScale = selectedQualityTier.renderResolutionScale
        )
    }

    private data class PerformanceScales(
        val blur: Float,
        val refraction: Float,
        val motion: Float,
        val chromaticAberration: Float
    )

    private companion object {
        private fun normalizeIntensity(value: Float): Float =
            if (value.isFinite()) value.coerceIn(0f, 1f) else 1f

        private fun scalesFor(tier: LiquidGlassQualityTier): PerformanceScales = when (tier) {
            LiquidGlassQualityTier.MINIMAL -> PerformanceScales(
                blur = 0.4f,
                refraction = 0.2f,
                motion = 0.5f,
                chromaticAberration = 0f
            )

            LiquidGlassQualityTier.BALANCED -> PerformanceScales(
                blur = 0.7f,
                refraction = 0.55f,
                motion = 0.75f,
                chromaticAberration = 0.35f
            )

            LiquidGlassQualityTier.HIGH -> PerformanceScales(
                blur = 0.9f,
                refraction = 0.85f,
                motion = 0.9f,
                chromaticAberration = 0.75f
            )

            LiquidGlassQualityTier.ULTRA -> PerformanceScales(
                blur = 1f,
                refraction = 1f,
                motion = 1f,
                chromaticAberration = 1f
            )
        }

    }
}
