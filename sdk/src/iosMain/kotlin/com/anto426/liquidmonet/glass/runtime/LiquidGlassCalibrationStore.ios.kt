package com.anto426.liquidmonet.glass.runtime

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileSize
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLIsExcludedFromBackupKey
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile

/** Application Support, excluded from backup/transfer. All access is on a worker dispatcher. */
@OptIn(ExperimentalForeignApi::class)
internal class LiquidGlassIosFileCalibrationStore(directory: NSURL? = null) : LiquidGlassIosCalibrationStore {
    private val files = NSFileManager.defaultManager
    private val folder: NSURL? = directory ?: (files.URLsForDirectory(NSApplicationSupportDirectory, NSUserDomainMask)
        .firstOrNull() as? NSURL)?.URLByAppendingPathComponent("liquid-glass", isDirectory = true)

    override fun read(key: String): LiquidGlassCalibrationRecord? =
        readText("device-profile-v1")?.let { LiquidGlassCalibrationRecord.decode(it, key) }

    override fun save(record: LiquidGlassCalibrationRecord): Boolean = writeText("device-profile-v1", record.encode())

    override fun beginWarmup(signature: String): Boolean =
        readText("shader-warmup.pending") != signature && writeText("shader-warmup.pending", signature)

    override fun finishWarmup() {
        path("shader-warmup.pending")?.let { files.removeItemAtPath(it, error = null) }
    }

    private fun path(name: String): String? = folder?.URLByAppendingPathComponent(name)?.path

    private fun readText(name: String): String? {
        val path = path(name) ?: return null
        val size = (files.attributesOfItemAtPath(path, error = null)?.get(NSFileSize) as? NSNumber)?.longLongValue ?: return null
        if (size !in 1L..4096L) return null
        return NSString.create(contentsOfFile = path, encoding = NSUTF8StringEncoding, error = null)?.toString()
    }

    private fun writeText(name: String, value: String): Boolean {
        if (value.encodeToByteArray().size > 4096) return false
        val folder = folder ?: return false
        if (!files.createDirectoryAtURL(folder, withIntermediateDirectories = true, attributes = null, error = null)) return false
        if (!folder.setResourceValue(NSNumber(bool = true), forKey = NSURLIsExcludedFromBackupKey, error = null)) return false
        val path = path(name) ?: return false
        return NSString.create(string = value).writeToFile(path, atomically = true, encoding = NSUTF8StringEncoding, error = null)
    }
}
