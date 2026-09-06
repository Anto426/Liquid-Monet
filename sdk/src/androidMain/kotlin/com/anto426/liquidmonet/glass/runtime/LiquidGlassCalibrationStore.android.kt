package com.anto426.liquidmonet.glass.runtime

import android.content.Context
import android.util.AtomicFile
import android.util.Log
import java.io.File

/** Atomic local storage only. Call from IO; it never runs a test or publishes Compose state. */
internal class LiquidGlassCalibrationStore(context: Context) {
    private val file = AtomicFile(File(context.noBackupFilesDir, "liquid-glass-device-profile-v1"))

    @androidx.annotation.WorkerThread
    fun read(key: String): LiquidGlassCalibrationRecord? {
        return try {
            file.openRead().use { input ->
                // A corrupted local file must not turn startup into an unbounded allocation.
                val bytes = ByteArray(4097)
                var count = 0
                while (count < bytes.size) {
                    val read = input.read(bytes, count, bytes.size - count)
                    if (read < 0) break
                    count += read
                }
                if (count > 4096) null else LiquidGlassCalibrationRecord.decode(
                    bytes.decodeToString(0, count), key
                )
            }
        } catch (_: Exception) {
            null
        }
    }

    @androidx.annotation.WorkerThread
    fun save(record: LiquidGlassCalibrationRecord): Boolean {
        var output: java.io.FileOutputStream? = null
        return try {
            output = file.startWrite()
            output.write(record.encode().encodeToByteArray())
            file.finishWrite(output)
            true
        } catch (e: Exception) {
            file.failWrite(output)
            Log.w("LiquidGlassCalibration", "Could not persist device profile", e)
            false
        }
    }

}
