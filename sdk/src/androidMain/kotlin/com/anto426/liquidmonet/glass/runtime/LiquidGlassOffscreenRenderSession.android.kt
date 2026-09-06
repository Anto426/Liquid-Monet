package com.anto426.liquidmonet.glass.runtime

import android.graphics.Bitmap
import android.graphics.HardwareRenderer
import android.graphics.PixelFormat
import android.graphics.RecordingCanvas
import android.graphics.RenderNode
import android.hardware.HardwareBuffer
import android.media.ImageReader
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay

/** Private HWUI resources shared by calibration and shader warm-up; never an application scene. */
@RequiresApi(31)
internal class LiquidGlassOffscreenRenderSession(private val width: Int, private val height: Int) : AutoCloseable {
    private val root = RenderNode("LiquidGlassPreparation")
    private val reader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2,
        HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE or HardwareBuffer.USAGE_GPU_COLOR_OUTPUT)
    private var renderer: HardwareRenderer? = null

    init {
        try {
            root.setPosition(0, 0, width, height)
            renderer = HardwareRenderer().also {
                it.setName("LiquidGlassPreparation")
                it.setSurface(reader.surface)
                it.isOpaque = true
                it.setLightSourceAlpha(0.15f, 0.20f)
                it.setLightSourceGeometry(width / 2f, -height.toFloat(), 600f, 800f)
                it.setContentRoot(root)
            }
        } catch (error: Throwable) {
            close()
            throw error
        }
    }

    /** End-to-end drawing latency, including GPU completion but excluding inter-frame pacing. */
    suspend fun render(draw: (RecordingCanvas) -> Unit): Long {
        // HWUI can defer requests submitted in the same vsync. Pace outside the timed work.
        delay(17L)
        val start = System.nanoTime()
        val canvas = root.beginRecording()
        try { draw(canvas) } finally { root.endRecording() }
        val committed = CountDownLatch(1)
        val status = checkNotNull(renderer).createRenderRequest().setVsyncTime(start)
            .setFrameCommitCallback(completionExecutor) { committed.countDown() }
            .setWaitForPresent(true).syncAndDraw()
        check(status and (HardwareRenderer.SYNC_LOST_SURFACE_REWARD_IF_FOUND or
            HardwareRenderer.SYNC_CONTEXT_IS_STOPPED) == 0) { "Render failed: $status" }
        // A deferred frame is not a completed frame. Its callback arrives after resubmission.
        check(committed.await(300, TimeUnit.MILLISECONDS)) { "Frame commit timed out ($status)" }
        checkNotNull(reader.acquireNextImage()) { "No committed image ($status)" }.use { image ->
            if (Build.VERSION.SDK_INT >= 33) {
                image.fence.use { fence ->
                    check(!fence.isValid || fence.await(Duration.ofMillis(150))) { "GPU completion timed out" }
                }
            } else {
                image.hardwareBuffer?.use { buffer ->
                    val hardware = checkNotNull(Bitmap.wrapHardwareBuffer(buffer, null))
                    try {
                        val pixels = checkNotNull(hardware.copy(Bitmap.Config.ARGB_8888, false))
                        pixels.recycle()
                    } finally { hardware.recycle() }
                } ?: error("No hardware buffer")
            }
        }
        return (System.nanoTime() - start).coerceAtLeast(1L)
    }

    override fun close() {
        try {
            renderer?.destroy()
            renderer = null
        } finally {
            try { root.discardDisplayList() } finally { reader.close() }
        }
    }

    private companion object {
        // RenderThread callback performs only countDown; all waiting is on the worker.
        val completionExecutor = Executor { it.run() }
    }
}
