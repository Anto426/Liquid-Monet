package com.anto426.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.backdrop.drawBackdrop
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Tests the real draw pipeline: a normal scene must not traverse its subtree twice per layer. */
class LiquidBackdropRecordingTest {
    @get:Rule val compose = createAndroidComposeRule<LiquidTestActivity>()

    @Test fun nestedBackdropRecordingsDrawContentOncePerTraversal() {
        val version = mutableIntStateOf(0)
        val rootDraws = AtomicInteger()
        val leafDraws = AtomicInteger()
        compose.setContent {
            val first = rememberLayerBackdrop()
            val second = rememberLayerBackdrop()
            val third = rememberLayerBackdrop()
            Box(Modifier.size(120.dp).drawWithContent {
                version.intValue
                rootDraws.incrementAndGet()
                drawContent()
            }.layerBackdrop(first)) {
                Box(Modifier.size(100.dp).layerBackdrop(second)) {
                    Box(Modifier.size(80.dp).layerBackdrop(third).drawWithContent {
                        version.intValue
                        leafDraws.incrementAndGet()
                        drawContent()
                    }.background(Color.Blue))
                }
            }
        }
        compose.waitForIdle()
        compose.runOnIdle {
            rootDraws.set(0)
            leafDraws.set(0)
            version.intValue++
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertTrue(rootDraws.get() > 0)
            assertEquals(rootDraws.get(), leafDraws.get())
        }
    }

    @Test fun customBackdropRecordingStillRunsSeparatelyFromVisibleContent() {
        val contentDraws = AtomicInteger()
        val recordingDraws = AtomicInteger()
        compose.setContent {
            val custom = rememberLayerBackdrop(onDraw = {
                recordingDraws.incrementAndGet()
                drawRect(Color.Red)
            })
            Box(Modifier.size(80.dp).layerBackdrop(custom).drawWithContent {
                contentDraws.incrementAndGet()
                drawRect(Color.Blue)
            })
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertTrue(contentDraws.get() > 0)
            assertEquals(contentDraws.get(), recordingDraws.get())
        }
    }

    @Test fun exportedSurfaceReusesTheVisibleRecordingAndUpdatesOnNextDraw() {
        val version = mutableIntStateOf(0)
        val traversals = AtomicInteger()
        val samples = AtomicInteger()
        val sampledVersion = AtomicInteger(-1)
        compose.setContent {
            val exported = rememberLayerBackdrop()
            Box(Modifier.size(80.dp).drawWithContent {
                version.intValue
                traversals.incrementAndGet()
                drawContent()
            }.drawBackdrop(
                backdrop = emptyBackdrop(),
                shape = { RectangleShape },
                effects = {},
                exportedBackdrop = exported,
                onDrawBackdrop = {
                    samples.incrementAndGet()
                    sampledVersion.set(version.intValue)
                    drawRect(if (version.intValue == 0) Color.Blue else Color.Red)
                }
            ))
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertTrue(traversals.get() > 0)
            assertEquals(traversals.get(), samples.get())
            traversals.set(0)
            samples.set(0)
            version.intValue++
        }
        compose.waitForIdle()
        compose.runOnIdle {
            assertTrue(traversals.get() > 0)
            assertEquals(traversals.get(), samples.get())
            assertEquals(1, sampledVersion.get())
        }
    }
}
