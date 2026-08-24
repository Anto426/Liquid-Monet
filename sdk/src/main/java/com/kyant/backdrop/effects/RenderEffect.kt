package com.kyant.backdrop.effects

import androidx.compose.ui.graphics.RenderEffect
import com.kyant.backdrop.BackdropEffectScope
import com.kyant.backdrop.RuntimeShader
import com.kyant.backdrop.internal.RuntimeShaderEffect
import com.kyant.backdrop.internal.chain
import com.kyant.backdrop.isRenderEffectSupported
import com.kyant.backdrop.isRuntimeShaderSupported
import org.intellij.lang.annotations.Language
import kotlin.contracts.ExperimentalContracts

fun BackdropEffectScope.effect(effect: RenderEffect) {
    if (!isRenderEffectSupported()) return

    renderEffect = renderEffect.chain(effect)
}

@OptIn(ExperimentalContracts::class)
fun BackdropEffectScope.runtimeShaderEffect(
    key: String,
    @Language("AGSL") shaderString: String,
    uniformShaderName: String,
    block: RuntimeShader.() -> Unit
) {
    if (!isRuntimeShaderSupported()) return

    val runtimeShader = obtainRuntimeShader(key, shaderString)?.apply(block) ?: return
    val effect =
        RuntimeShaderEffect(
            runtimeShader = runtimeShader,
            uniformShaderName = uniformShaderName
        )
    renderEffect = renderEffect.chain(effect)
}
