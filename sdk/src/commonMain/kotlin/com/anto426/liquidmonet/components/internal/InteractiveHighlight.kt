package com.anto426.liquidmonet.components.internal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastCoerceIn
import com.anto426.liquidmonet.motion.inspectDragGestures
import com.anto426.liquidmonet.glass.runtime.LiquidGlassPerformanceState
import com.kyant.backdrop.RuntimeShader
import com.kyant.backdrop.asComposeShader
import com.kyant.backdrop.isRuntimeShaderSupported
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@androidx.compose.runtime.Stable
internal class InteractiveHighlight(
    val animationScope: CoroutineScope,
    private val performance: () -> LiquidGlassPerformanceState = { LiquidGlassPerformanceState.Fallback },
    val position: (size: Size, offset: Offset) -> Offset = { _, offset -> offset }
) {

    private val pressProgressAnimation =
        Animatable(0f, 0.001f)
    private val positionAnimation =
        Animatable(Offset.Zero, Offset.VectorConverter, Offset.VisibilityThreshold)

    private var startPosition = Offset.Zero
    private var pointerPosition by mutableStateOf(Offset.Zero)
    private var pressed by mutableStateOf(false)
    private var pressJob: Job? = null
    private var releaseJob: Job? = null
    private val currentPosition: Offset get() = if (pressed) pointerPosition else positionAnimation.value
    val motionEnabled: Boolean get() = performance().motionScale > 0f
    val pressProgress: Float get() = pressProgressAnimation.value
    val offset: Offset get() = currentPosition - startPosition

    private val clipPath = Path()
    private var lastClipSize: Size = Size.Unspecified
    private var lastClipShape: Shape? = null

    private val shader by lazy {
        if (isRuntimeShaderSupported()) {
            RuntimeShader(
                """
uniform float2 size;
layout(color) uniform half4 color;
uniform float radius;
uniform float2 position;

half4 main(float2 coord) {
    float dist = distance(coord, position);
    float intensity = smoothstep(radius, radius * 0.5, dist);
    return color * intensity;
}"""
            )
        } else {
            null
        }
    }

    internal fun modifier(
        highlightColor: Color = Color.Unspecified,
        clipShape: Shape? = null
    ): Modifier =
        Modifier.drawWithContent {
            drawContent()

            val progress = pressProgressAnimation.value
            if (progress > 0f) {
                val resolvedColor = if (highlightColor.isSpecified) highlightColor else Color.White

                val drawHighlight: () -> Unit = {
                    val shader = shader
                    if (shader != null) {
                        drawRect(
                            resolvedColor.copy(0.08f * progress),
                            blendMode = BlendMode.Plus
                        )
                        shader.apply {
                            val position = position(size, currentPosition)
                            setFloatUniform("size", size.width, size.height)
                            setColorUniform("color", resolvedColor.copy(0.15f * progress))
                            setFloatUniform("radius", size.minDimension * 1.5f)
                            setFloatUniform(
                                "position",
                                position.x.fastCoerceIn(0f, size.width),
                                position.y.fastCoerceIn(0f, size.height)
                            )
                        }
                        drawRect(
                            ShaderBrush(shader.asComposeShader()),
                            blendMode = BlendMode.Plus
                        )
                    } else {
                        drawRect(
                            resolvedColor.copy(0.25f * progress),
                            blendMode = BlendMode.Plus
                        )
                    }
                }

                if (clipShape != null) {
                    if (lastClipSize != size || lastClipShape != clipShape) {
                        clipPath.reset()
                        clipPath.addOutline(clipShape.createOutline(size, layoutDirection, this))
                        lastClipSize = size
                        lastClipShape = clipShape
                    }
                    clipPath(clipPath) {
                        drawHighlight()
                    }
                } else {
                    drawHighlight()
                }
            }
        }

    internal val modifier: Modifier get() = modifier(Color.Unspecified, null)

    internal fun press(position: Offset) {
        releaseJob?.cancel()
        pressJob?.cancel()
        startPosition = position
        pointerPosition = position
        pressed = true
        pressJob = animationScope.launch {
            pressProgressAnimation.animateTo(
                targetValue = 1f,
                animationSpec = LiquidControlMotion.pressProgress(true, performance())
            )
        }
    }

    internal fun move(position: Offset) {
        if (pressed) pointerPosition = position
    }

    internal fun release() {
        if (!pressed) return
        pressJob?.cancel()
        releaseJob?.cancel()
        val releasePosition = pointerPosition
        releaseJob = animationScope.launch(start = CoroutineStart.UNDISPATCHED) {
            positionAnimation.snapTo(releasePosition)
            pressed = false
            launch {
                pressProgressAnimation.animateTo(
                    targetValue = 0f,
                    animationSpec = LiquidControlMotion.pressProgress(false, performance())
                )
            }
            launch {
                positionAnimation.animateTo(
                    targetValue = startPosition,
                    animationSpec = LiquidControlMotion.pointerPosition(performance())
                )
            }
        }
    }

    internal val gestureModifier: Modifier =
        Modifier.pointerInput(animationScope) {
            inspectDragGestures(
                onDragStart = { down ->
                    press(down.position)
                },
                onDragEnd = { release() },
                onDragCancel = ::release
            ) { change, _ ->
                move(change.position)
            }
        }
}
