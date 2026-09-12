package com.anto426.liquidmonet.glass.runtime

import com.kyant.backdrop.isRenderEffectSupported
import com.kyant.backdrop.isRuntimeShaderSupported
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.useContents
import platform.Foundation.NSProcessInfo
import platform.Metal.MTLCreateSystemDefaultDevice
import platform.Metal.MTLGPUFamilyApple4
import platform.Metal.MTLGPUFamilyApple7
import platform.UIKit.UIScreen
import platform.posix.uname
import platform.posix.utsname

internal data class LiquidGlassIosHardware(
    val device: LiquidGlassDeviceProfile,
    val qualityTier: LiquidGlassQualityTier,
    val deviceKey: String,
    val isSimulator: Boolean
)

/** Read on Main once per theme lifetime; UIKit screen access belongs to the UI thread. */
@OptIn(ExperimentalForeignApi::class)
internal fun probeLiquidGlassIosHardware(): LiquidGlassIosHardware {
    val process = NSProcessInfo.processInfo
    val screen = UIScreen.mainScreen
    val gpu = MTLCreateSystemDefaultDevice()
    val appleGpuFamily = when {
        gpu?.supportsFamily(MTLGPUFamilyApple7) == true -> 7
        gpu?.supportsFamily(MTLGPUFamilyApple4) == true -> 4
        else -> 0
    }
    val memory = process.physicalMemory.toLong()
    val socModel = gpu?.name().orEmpty().removeSuffix(" GPU").trim()
    val simulatorModel = process.environment["SIMULATOR_MODEL_IDENTIFIER"] as? String
    val machine = simulatorModel ?: memScoped {
        val info = alloc<utsname>()
        if (uname(info.ptr) == 0) info.machine.toKString() else "unknown"
    }
    val device = LiquidGlassDeviceProfile(
        sdkInt = 0,
        supportsRenderEffect = isRenderEffectSupported(),
        supportsRuntimeShader = isRuntimeShaderSupported(),
        isLowRamDevice = memory < 2L * 1024 * 1024 * 1024,
        totalMemoryBytes = memory,
        appMemoryClassMb = 0,
        cpuCoreCount = process.processorCount.toInt(),
        is64Bit = true, // Both supported iOS targets are arm64.
        displayWidthPixels = screen.nativeBounds.useContents { size.width.toInt() },
        displayHeightPixels = screen.nativeBounds.useContents { size.height.toInt() },
        displayRefreshRateHz = screen.maximumFramesPerSecond.toFloat(),
        displayDensity = screen.nativeScale.toFloat(),
        socModel = socModel,
        processorFamily = LiquidGlassProcessorFamilies.identify(socModel)
    )
    return LiquidGlassIosHardware(
        device, LiquidGlassIosPerformancePolicy.qualityTier(device, appleGpuFamily),
        liquidGlassIosDeviceKey(machine, socModel, device, simulatorModel != null), simulatorModel != null
    )
}
