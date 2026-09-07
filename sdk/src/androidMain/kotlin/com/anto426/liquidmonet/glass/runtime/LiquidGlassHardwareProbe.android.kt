package com.anto426.liquidmonet.glass.runtime

import android.app.ActivityManager
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Process
import android.view.Display
import java.io.File
import java.security.MessageDigest

/** Hardware facts only. No Compose state, persistence or benchmark decisions. */
internal object LiquidGlassHardwareProbe {
    @androidx.annotation.WorkerThread
    fun read(context: Context): LiquidGlassDeviceProfile {
        val activity = context.getSystemService(ActivityManager::class.java)
        val memory = ActivityManager.MemoryInfo()
        activity?.getMemoryInfo(memory)
        val cpuName = Regex("cpu[0-9]+")
        val cpuDirectories = try {
            File("/sys/devices/system/cpu").listFiles { file -> cpuName.matches(file.name) }.orEmpty()
                .sortedBy { it.name.removePrefix("cpu").toIntOrNull() ?: 0 }
        } catch (_: Exception) { emptyList() }
        val frequencies = cpuDirectories.mapNotNull { directory ->
            try {
                File(directory, "cpufreq/cpuinfo_max_freq").readText().trim().toLongOrNull()
                    ?.takeIf { it in 100_000L..10_000_000L }
            } catch (_: Exception) { null }
        }
        val display = context.getSystemService(DisplayManager::class.java)?.getDisplay(Display.DEFAULT_DISPLAY)
        // Supported modes are stable across rotation, battery saver and adaptive refresh changes.
        val mode = display?.supportedModes?.maxWithOrNull(
            compareBy<Display.Mode> { it.physicalWidth.toLong() * it.physicalHeight }.thenBy { it.refreshRate }
        )
        val metrics = context.resources.displayMetrics
        val width = mode?.physicalWidth ?: metrics.widthPixels
        val height = mode?.physicalHeight ?: metrics.heightPixels
        val socModel = if (Build.VERSION.SDK_INT >= 31) Build.SOC_MODEL else Build.HARDWARE
        return LiquidGlassDeviceProfile(
            sdkInt = Build.VERSION.SDK_INT,
            supportsRenderEffect = Build.VERSION.SDK_INT >= 31,
            supportsRuntimeShader = Build.VERSION.SDK_INT >= 33,
            isLowRamDevice = activity?.isLowRamDevice ?: true,
            totalMemoryBytes = memory.totalMem,
            appMemoryClassMb = activity?.memoryClass ?: 0,
            cpuCoreCount = cpuDirectories.size.takeIf { it > 0 }
                ?: Runtime.getRuntime().availableProcessors().coerceAtLeast(1),
            is64Bit = Process.is64Bit(),
            cpuMaxFrequenciesKhz = frequencies,
            displayWidthPixels = minOf(width, height),
            displayHeightPixels = maxOf(width, height),
            displayRefreshRateHz = mode?.refreshRate ?: 60f,
            displayDensity = metrics.density,
            socModel = socModel,
            processorFamily = LiquidGlassProcessorFamilies.identify(socModel, Build.HARDWARE)
        )
    }

    fun key(device: LiquidGlassDeviceProfile): String {
        // No serial/IMEI, current frequency, free RAM, OS build fingerprint or app version:
        // none of those should cause a previously calibrated device to be tested again.
        val identity = listOf(
            Build.MANUFACTURER, Build.MODEL, Build.DEVICE, Build.HARDWARE, device.socModel,
            device.is64Bit, device.supportsRenderEffect, device.supportsRuntimeShader
        ).joinToString("|")
        return MessageDigest.getInstance("SHA-256").digest(identity.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
