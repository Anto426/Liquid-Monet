package com.anto426.antoui.components.pickers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.antoui.components.internal.antoControlLayerBlock
import com.anto426.antoui.components.internal.antoControlPressFeedback
import com.anto426.antoui.components.internal.rememberAntoControlHighlight
import com.anto426.antoui.components.selection.AntoSlider
import com.anto426.antoui.glass.AntoGlassRole
import com.anto426.antoui.glass.antoLiquidGlass
import com.anto426.antoui.icons.AntoIcons
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle

/**
 * AntoColorPicker - Optical Liquid Glass Color Spectrum & Palette Selector.
 * 
 * Features:
 * - Real-time Snell optical liquid glass refraction & elastic physics
 * - Magnified Liquid Glass Color Lens with specular reflection
 * - Hex badge & RGB / HSL chromatic readout
 * - Vibrant quick preset droplets with spring bounce animation
 * - Continuous Hue spectrum & Lightness glass sliders
 */
@Composable
fun AntoColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    var hue by remember { mutableFloatStateOf(210f) }
    var lightness by remember { mutableFloatStateOf(0.50f) }
    var saturation by remember { mutableFloatStateOf(0.85f) }

    // Synchronize initial HSL with selectedColor when it changes externally
    LaunchedEffect(selectedColor) {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(selectedColor.toArgb(), hsv)
        hue = hsv[0]
        saturation = hsv[1]
        lightness = hsv[2] * (1f - hsv[1] / 2f)
    }

    val currentColor = remember(hue, saturation, lightness) {
        Color.hsl(hue, saturation.coerceIn(0.1f, 1f), lightness.coerceIn(0.1f, 0.9f))
    }

    val hexString = remember(currentColor) {
        val r = (currentColor.red * 255).toInt().coerceIn(0, 255)
        val g = (currentColor.green * 255).toInt().coerceIn(0, 255)
        val b = (currentColor.blue * 255).toInt().coerceIn(0, 255)
        String.format("#%02X%02X%02X", r, g, b)
    }

    val interactiveHighlight = rememberAntoControlHighlight()
    val colorScheme = MaterialTheme.colorScheme

    val presetColors = remember {
        listOf(
            Color(0xFFFFFFFF), // Bianco Puro
            Color(0xFF2979FF), // Zaffiro
            Color(0xFF00E676), // Smeraldo
            Color(0xFFFF9100), // Tramonto
            Color(0xFFFF1744), // Rubino
            Color(0xFFD500F9), // Ametista
            Color(0xFF00E5FF), // Laguna
            Color(0xFFFFD600), // Ambra
            Color(0xFF212121)  // Grafite Scuro
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .antoLiquidGlass(
                backdrop = backdropState,
                shape = RoundedRectangle(24.dp),
                role = AntoGlassRole.Surface
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Header: Liquid Glass Color Orb + Hex Badge ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Magnified Optical Glass Color Lens
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(Capsule())
                        .background(currentColor)
                        .border(
                            width = 2.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.80f),
                                    currentColor.copy(alpha = 0.40f)
                                )
                            ),
                            shape = Capsule()
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Specular reflection gradient inside the orb
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.35f),
                                        Color.Transparent
                                    ),
                                    radius = 40f
                                ),
                                shape = Capsule()
                            )
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Colore Selezionato",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSurface.copy(alpha = 0.65f),
                        fontSize = 11.5.sp
                    )
                    // Monospace Hex Pill Badge
                    Box(
                        modifier = Modifier
                            .clip(Capsule())
                            .background(currentColor.copy(alpha = 0.18f))
                            .border(1.dp, currentColor.copy(alpha = 0.45f), Capsule())
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = hexString,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.5.sp
                            ),
                            color = colorScheme.onSurface
                        )
                    }
                }
            }

            // RGB / HSL summary tag
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "H: ${hue.toInt()}°",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.5.sp
                )
                Text(
                    text = "L: ${(lightness * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.onSurface.copy(alpha = 0.55f),
                    fontSize = 11.sp
                )
            }
        }

        // --- 2. Quick Preset Liquid Glass Droplets ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            presetColors.forEach { preset ->
                val isPresetSelected = (selectedColor.toArgb() == preset.toArgb()) ||
                        (currentColor.toArgb() == preset.toArgb())
                val isLightPreset = preset == Color.White

                val dropletScale by animateFloatAsState(
                    targetValue = if (isPresetSelected) 1.14f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                    label = "dropletScale"
                )

                val checkTint = if (isLightPreset) Color(0xFF1E1E1E) else Color.White
                val borderColor = when {
                    isPresetSelected && isLightPreset -> colorScheme.primary
                    isPresetSelected -> Color.White
                    isLightPreset -> Color.White.copy(alpha = 0.60f)
                    else -> Color.White.copy(alpha = 0.40f)
                }

                Box(
                    modifier = Modifier
                        .size(31.dp)
                        .graphicsLayer {
                            scaleX = dropletScale
                            scaleY = dropletScale
                        }
                        .clip(Capsule())
                        .background(if (isLightPreset) Color.White else preset)
                        .border(
                            width = if (isPresetSelected) 2.dp else 1.dp,
                            color = borderColor,
                            shape = Capsule()
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onColorSelected(preset)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isPresetSelected) {
                        Icon(
                            imageVector = AntoIcons.Check,
                            contentDescription = "Selezionato",
                            tint = checkTint,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // --- 3. Optical Glass Hue Slider ---
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Spettro Cromatico (Tonalità)",
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurface.copy(alpha = 0.70f),
                fontWeight = FontWeight.Medium
            )
            val hueGradient = remember {
                Brush.horizontalGradient(
                    listOf(
                        Color.Red,
                        Color.Yellow,
                        Color.Green,
                        Color.Cyan,
                        Color.Blue,
                        Color.Magenta,
                        Color.Red
                    )
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(Capsule())
                    .background(hueGradient)
                    .border(1.dp, Color.White.copy(alpha = 0.35f), Capsule())
            )
            AntoSlider(
                value = hue,
                onValueChange = {
                    hue = it
                    val updated = Color.hsl(hue, saturation.coerceIn(0.1f, 1f), lightness.coerceIn(0.1f, 0.9f))
                    onColorSelected(updated)
                },
                valueRange = 0f..360f,
                backdropState = backdropState
            )
        }

        // --- 4. Optical Glass Lightness Slider ---
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Luminosità Rifrattiva",
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurface.copy(alpha = 0.70f),
                fontWeight = FontWeight.Medium
            )
            val lightnessGradient = remember(hue, saturation) {
                Brush.horizontalGradient(
                    listOf(
                        Color.Black,
                        Color.hsl(hue, saturation, 0.50f),
                        Color.White
                    )
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(Capsule())
                    .background(lightnessGradient)
                    .border(1.dp, Color.White.copy(alpha = 0.35f), Capsule())
            )
            AntoSlider(
                value = lightness,
                onValueChange = {
                    lightness = it
                    val updated = Color.hsl(hue, saturation.coerceIn(0.1f, 1f), lightness.coerceIn(0.1f, 0.9f))
                    onColorSelected(updated)
                },
                valueRange = 0.15f..0.85f,
                backdropState = backdropState
            )
        }
    }
}
