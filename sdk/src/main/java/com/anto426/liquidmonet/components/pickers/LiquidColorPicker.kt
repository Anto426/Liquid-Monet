package com.anto426.liquidmonet.components.pickers

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anto426.liquidmonet.components.display.LiquidHorizontalDivider
import com.anto426.liquidmonet.components.display.LiquidIconBox
import com.anto426.liquidmonet.components.selection.LiquidSlider
import com.anto426.liquidmonet.glass.LiquidGlassRole
import com.anto426.liquidmonet.glass.liquidGlass
import com.anto426.liquidmonet.icons.LiquidIcons
import com.anto426.liquidmonet.theme.LiquidGlassTheme
import com.anto426.liquidmonet.theme.monet.contentColorFor
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * LiquidColorPicker - Advanced Optical Liquid Glass Color Spectrum & Palette Selector.
 *
 * Features:
 * - Real-time Snell optical liquid glass refraction & elastic physics
 * - Magnified Liquid Glass Color Lens with specular reflection & split preview
 * - 3 Interactive Modes: Curated Thematic Palettes, 2D Spectrum Canvas, and Precision Sliders
 * - Direct Hex input with clipboard copy/paste feedback
 * - Recent colors history strip
 * - High precision HSV / HSL / RGB / Hex bidirectional synchronization
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LiquidColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    showAlpha: Boolean = false,
) {
    val effectiveBackdrop = if (backdropState != emptyBackdrop()) backdropState else backdrop
    val colorScheme = MaterialTheme.colorScheme
    val glassColors = LiquidGlassTheme.colors
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    val initialColor = remember { selectedColor }
    var hue by remember { mutableFloatStateOf(210f) }
    var saturation by remember { mutableFloatStateOf(0.85f) }
    var value by remember { mutableFloatStateOf(0.90f) }
    var alpha by remember { mutableFloatStateOf(selectedColor.alpha) }

    var selectedMode by remember { mutableIntStateOf(0) } // 0: Tavolozze, 1: Spettro 2D, 2: Cursori
    var selectedCategoryIndex by remember { mutableIntStateOf(0) } // 0: Tutti, 1: Accademici, 2: Vibranti, 3: Pastello, 4: Material
    var showHexDialog by remember { mutableStateOf(false) }
    var hexInputText by remember { mutableStateOf("") }
    var copiedFeedback by remember { mutableStateOf(false) }

    val recentColors = remember { mutableStateListOf<Color>() }

    // Synchronize initial HSV with selectedColor when it changes externally
    LaunchedEffect(selectedColor) {
        val (h, s, v) = colorToHsv(selectedColor)
        hue = h
        saturation = s
        value = v
        alpha = selectedColor.alpha
        if (!recentColors.contains(selectedColor)) {
            recentColors.add(0, selectedColor)
            if (recentColors.size > 8) {
                recentColors.removeLast()
            }
        }
    }

    val currentColor = remember(hue, saturation, value, alpha) {
        hsvToColor(hue, saturation, value, alpha)
    }

    val hexString = remember(currentColor, showAlpha) {
        colorToHex(currentColor, showAlpha)
    }

    val rInt = (currentColor.red * 255).toInt().coerceIn(0, 255)
    val gInt = (currentColor.green * 255).toInt().coerceIn(0, 255)
    val bInt = (currentColor.blue * 255).toInt().coerceIn(0, 255)

    val academicPresets = remember {
        listOf(
            Color(0xFF0061A4), // Sapphire
            Color(0xFF006C4C), // Emerald
            Color(0xFFBA1A1A), // Crimson
            Color(0xFF6B4EA2), // Violet
            Color(0xFF8B5000), // Sunset
            Color(0xFF243B55), // Navy
            Color(0xFF8C2D19), // Sienna
            Color(0xFF002147), // Oxford
        )
    }

    val vibrantPresets = remember {
        listOf(
            Color(0xFF2979FF), // Electric Blue
            Color(0xFF00E676), // Neon Mint
            Color(0xFFFF9100), // Vivid Amber
            Color(0xFFFF1744), // Laser Ruby
            Color(0xFFD500F9), // Neon Magenta
            Color(0xFF00E5FF), // Cyan Aqua
            Color(0xFFFFD600), // Sunny Gold
            Color(0xFFFF6D00), // Cyber Orange
        )
    }

    val pastelPresets = remember {
        listOf(
            Color(0xFFB39DDB), // Lavender
            Color(0xFFA5D6A7), // Sage
            Color(0xFFFFCC80), // Peach
            Color(0xFFF48FB1), // Soft Pink
            Color(0xFF90CAF9), // Sky Blue
            Color(0xFFFFAB91), // Soft Coral
            Color(0xFF80CBC4), // Pastel Mint
            Color(0xFFFFF59D), // Buttercream
        )
    }

    val materialPresets = remember {
        listOf(
            Color(0xFF009688), // Teal
            Color(0xFF3F51B5), // Indigo
            Color(0xFF673AB7), // Deep Purple
            Color(0xFF8BC34A), // Light Green
            Color(0xFFFFB300), // Amber
            Color(0xFFFF5722), // Deep Orange
            Color(0xFF607D8B), // Slate
            Color(0xFFFFFFFF), // White
            Color(0xFF121212), // Dark
        )
    }

    val currentPalette = remember(selectedCategoryIndex) {
        when (selectedCategoryIndex) {
            1 -> academicPresets
            2 -> vibrantPresets
            3 -> pastelPresets
            4 -> materialPresets
            else -> academicPresets + vibrantPresets + pastelPresets + materialPresets
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                backdrop = effectiveBackdrop,
                shape = RoundedRectangle(24.dp),
                role = LiquidGlassRole.Surface
            )
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // =========================================================
        // 1. HEADER: MAGNIFIED LENS + DUAL SPLIT PREVIEW + HEX BADGE
        // =========================================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Optical Glass Lens with Split Preview (Initial vs Current)
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.85f),
                                    currentColor.copy(alpha = 0.50f)
                                )
                            ),
                            shape = CircleShape
                        )
                        .clickable {
                            // Quick revert to initial on tap
                            onColorSelected(initialColor)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Left half: initial color, Right half: new current color
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(initialColor)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .background(currentColor)
                        )
                    }

                    // Specular reflection gloss sheen
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.40f),
                                        Color.Transparent
                                    ),
                                    radius = 45f
                                ),
                                shape = CircleShape
                            )
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Colore Selezionato",
                        style = MaterialTheme.typography.labelSmall,
                        color = glassColors.secondaryContent,
                        fontSize = 11.5.sp
                    )

                    // Clickable Hex Badge (copies to clipboard)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clip(Capsule())
                            .background(currentColor.copy(alpha = 0.16f))
                            .border(1.dp, currentColor.copy(alpha = 0.50f), Capsule())
                            .clickable {
                                clipboardManager.setText(AnnotatedString(hexString))
                                copiedFeedback = true
                                coroutineScope.launch {
                                    delay(1500)
                                    copiedFeedback = false
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
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

                        Icon(
                            imageVector = if (copiedFeedback) LiquidIcons.Check else LiquidIcons.Copy,
                            contentDescription = "Copia Hex",
                            tint = if (copiedFeedback) colorScheme.primary else glassColors.secondaryContent,
                            modifier = Modifier.size(14.dp)
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
                    text = "RGB: $rInt, $gInt, $bInt",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.5.sp
                )
                Text(
                    text = "H: ${hue.toInt()}°  S: ${(saturation * 100).toInt()}%  V: ${(value * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = glassColors.secondaryContent,
                    fontSize = 10.5.sp
                )
                if (showAlpha) {
                    Text(
                        text = "Alpha: ${(alpha * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = glassColors.secondaryContent,
                        fontSize = 10.5.sp
                    )
                }
            }
        }

        // =========================================================
        // 2. MODE SELECTOR SEGMENT (Tavolozze / Spettro 2D / Cursori)
        // =========================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(colorScheme.onSurface.copy(alpha = 0.05f))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val modes = listOf(
                Triple("Tavolozze", LiquidIcons.Palette, 0),
                Triple("Spettro 2D", LiquidIcons.Star, 1),
                Triple("Cursori", LiquidIcons.Settings, 2)
            )

            modes.forEach { (label, icon, index) ->
                val isSelected = selectedMode == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .background(
                            if (isSelected) colorScheme.primary.copy(alpha = 0.16f)
                            else Color.Transparent
                        )
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) colorScheme.primary.copy(alpha = 0.35f) else Color.Transparent,
                            shape = RoundedCornerShape(11.dp)
                        )
                        .clickable { selectedMode = index }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // =========================================================
        // 3. TAB CONTENT
        // =========================================================
        when (selectedMode) {
            // -----------------------------------------------------
            // MODE 0: TAVOLOZZE (THEMATIC CURATED PALETTES)
            // -----------------------------------------------------
            0 -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Category Filter Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val categories = listOf("Tutti", "Accademici", "Vibranti", "Pastello", "Material")
                        categories.forEachIndexed { catIndex, catName ->
                            val isCatSelected = selectedCategoryIndex == catIndex
                            Box(
                                modifier = Modifier
                                    .clip(Capsule())
                                    .background(
                                        if (isCatSelected) colorScheme.primary.copy(alpha = 0.2f)
                                        else colorScheme.onSurface.copy(alpha = 0.04f)
                                    )
                                    .border(
                                        width = if (isCatSelected) 1.dp else 0.5.dp,
                                        color = if (isCatSelected) colorScheme.primary else colorScheme.outline.copy(alpha = 0.15f),
                                        shape = Capsule()
                                    )
                                    .clickable { selectedCategoryIndex = catIndex }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = catName,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCatSelected) colorScheme.primary else colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Palette Droplets Grid
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        maxItemsInEachRow = 8
                    ) {
                        currentPalette.forEach { preset ->
                            val isPresetSelected = (selectedColor.toArgb() == preset.toArgb()) ||
                                    (currentColor.toArgb() == preset.toArgb())
                            val isLightPreset = preset == Color.White

                            val dropletScale by animateFloatAsState(
                                targetValue = if (isPresetSelected) 1.15f else 1.0f,
                                animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                                label = "dropletScale"
                            )

                            val checkTint = contentColorFor(preset)

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .graphicsLayer {
                                        scaleX = dropletScale
                                        scaleY = dropletScale
                                    }
                                    .clip(CircleShape)
                                    .background(if (isLightPreset) Color.White else preset)
                                    .border(
                                        width = if (isPresetSelected) 2.5.dp else 1.dp,
                                        color = if (isPresetSelected) colorScheme.primary else glassColors.outline.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    )
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        val (h, s, v) = colorToHsv(preset)
                                        hue = h
                                        saturation = s
                                        value = v
                                        alpha = preset.alpha
                                        onColorSelected(preset)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isPresetSelected) {
                                    Icon(
                                        imageVector = LiquidIcons.Check,
                                        contentDescription = "Selezionato",
                                        tint = checkTint,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Recent Colors Strip
                    if (recentColors.isNotEmpty()) {
                        LiquidHorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Colori Recenti",
                                style = MaterialTheme.typography.labelSmall,
                                color = glassColors.secondaryContent,
                                fontWeight = FontWeight.SemiBold
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                recentColors.take(6).forEach { recentColor ->
                                    val isCurrent = currentColor.toArgb() == recentColor.toArgb()
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(recentColor)
                                            .border(
                                                width = if (isCurrent) 2.dp else 1.dp,
                                                color = if (isCurrent) colorScheme.primary else glassColors.outline.copy(alpha = 0.4f),
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                val (h, s, v) = colorToHsv(recentColor)
                                                hue = h
                                                saturation = s
                                                value = v
                                                alpha = recentColor.alpha
                                                onColorSelected(recentColor)
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // -----------------------------------------------------
            // MODE 1: SPETTRO 2D (2D SATURATION-VALUE CANVAS + HUE)
            // -----------------------------------------------------
            1 -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // 2D Saturation / Value Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, glassColors.outline.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    ) {
                        val pureHueColor = remember(hue) {
                            hsvToColor(hue, 1f, 1f, 1f)
                        }

                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(hue) {
                                    detectTapGestures { offset ->
                                        val sat = (offset.x / size.width).coerceIn(0f, 1f)
                                        val v = (1f - (offset.y / size.height)).coerceIn(0f, 1f)
                                        saturation = sat
                                        value = v
                                        val updated = hsvToColor(hue, saturation, value, alpha)
                                        onColorSelected(updated)
                                    }
                                }
                                .pointerInput(hue) {
                                    detectDragGestures { change, _ ->
                                        change.consume()
                                        val sat = (change.position.x / size.width).coerceIn(0f, 1f)
                                        val v = (1f - (change.position.y / size.height)).coerceIn(0f, 1f)
                                        saturation = sat
                                        value = v
                                        val updated = hsvToColor(hue, saturation, value, alpha)
                                        onColorSelected(updated)
                                    }
                                }
                        ) {
                            // 1. Base pure hue fill
                            drawRect(color = pureHueColor)

                            // 2. Horizontal white gradient (Saturation: Left=White, Right=Transparent)
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    listOf(Color.White, Color.Transparent)
                                )
                            )

                            // 3. Vertical black gradient (Value/Lightness: Top=Transparent, Bottom=Black)
                            drawRect(
                                brush = Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black)
                                )
                            )

                            // 4. Reticle / Crosshair Indicator
                            val reticleX = saturation * size.width
                            val reticleY = (1f - value) * size.height

                            // Outer black shadow ring
                            drawCircle(
                                color = Color.Black.copy(alpha = 0.6f),
                                radius = 12.dp.toPx(),
                                center = Offset(reticleX, reticleY),
                                style = Stroke(width = 3.5.dp.toPx())
                            )
                            // Inner white ring
                            drawCircle(
                                color = Color.White,
                                radius = 10.dp.toPx(),
                                center = Offset(reticleX, reticleY),
                                style = Stroke(width = 2.5.dp.toPx())
                            )
                            // Core color dot
                            drawCircle(
                                color = currentColor,
                                radius = 7.5.dp.toPx(),
                                center = Offset(reticleX, reticleY)
                            )
                        }
                    }

                    // Continuous Hue Bar
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tonalità (Spettro)",
                                style = MaterialTheme.typography.labelSmall,
                                color = glassColors.secondaryContent,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${hue.toInt()}°",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

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
                                .height(12.dp)
                                .clip(Capsule())
                                .background(hueGradient)
                                .border(1.dp, glassColors.outline, Capsule())
                        )
                        LiquidSlider(
                            value = hue,
                            onValueChange = {
                                hue = it
                                val updated = hsvToColor(hue, saturation, value, alpha)
                                onColorSelected(updated)
                            },
                            valueRange = 0f..360f,
                            backdropState = effectiveBackdrop
                        )
                    }
                }
            }

            // -----------------------------------------------------
            // MODE 2: CURSORI DI PRECISIONE (HSV & ALPHA SLIDERS)
            // -----------------------------------------------------
            2 -> {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // 1. Hue Slider
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tonalità (Hue)",
                                style = MaterialTheme.typography.labelSmall,
                                color = glassColors.secondaryContent,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${hue.toInt()}°",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                                .height(12.dp)
                                .clip(Capsule())
                                .background(hueGradient)
                                .border(1.dp, glassColors.outline, Capsule())
                        )
                        LiquidSlider(
                            value = hue,
                            onValueChange = {
                                hue = it
                                val updated = hsvToColor(hue, saturation, value, alpha)
                                onColorSelected(updated)
                            },
                            valueRange = 0f..360f,
                            backdropState = effectiveBackdrop
                        )
                    }

                    // 2. Saturation Slider
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Saturazione Cromatica",
                                style = MaterialTheme.typography.labelSmall,
                                color = glassColors.secondaryContent,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${(saturation * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        val satGradient = remember(hue, value) {
                            Brush.horizontalGradient(
                                listOf(
                                    hsvToColor(hue, 0f, value, 1f),
                                    hsvToColor(hue, 1f, value, 1f)
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(Capsule())
                                .background(satGradient)
                                .border(1.dp, glassColors.outline, Capsule())
                        )
                        LiquidSlider(
                            value = saturation,
                            onValueChange = {
                                saturation = it
                                val updated = hsvToColor(hue, saturation, value, alpha)
                                onColorSelected(updated)
                            },
                            valueRange = 0f..1f,
                            backdropState = effectiveBackdrop
                        )
                    }

                    // 3. Value / Lightness Slider
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Luminosità / Brillantezza",
                                style = MaterialTheme.typography.labelSmall,
                                color = glassColors.secondaryContent,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${(value * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        val valGradient = remember(hue, saturation) {
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Black,
                                    hsvToColor(hue, saturation, 1f, 1f)
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(Capsule())
                                .background(valGradient)
                                .border(1.dp, glassColors.outline, Capsule())
                        )
                        LiquidSlider(
                            value = value,
                            onValueChange = {
                                value = it
                                val updated = hsvToColor(hue, saturation, value, alpha)
                                onColorSelected(updated)
                            },
                            valueRange = 0f..1f,
                            backdropState = effectiveBackdrop
                        )
                    }

                    // 4. Optional Alpha Slider
                    if (showAlpha) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Opacità (Trasparenza)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = glassColors.secondaryContent,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${(alpha * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            val alphaGradient = remember(currentColor) {
                                Brush.horizontalGradient(
                                    listOf(
                                        currentColor.copy(alpha = 0f),
                                        currentColor.copy(alpha = 1f)
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(Capsule())
                                    .background(alphaGradient)
                                    .border(1.dp, glassColors.outline, Capsule())
                            )
                            LiquidSlider(
                                value = alpha,
                                onValueChange = {
                                    alpha = it
                                    val updated = hsvToColor(hue, saturation, value, alpha)
                                    onColorSelected(updated)
                                },
                                valueRange = 0f..1f,
                                backdropState = effectiveBackdrop
                            )
                        }
                    }
                }
            }
        }

        // =========================================================
        // 4. QUICK HEX INPUT / PASTE ACCORDION
        // =========================================================
        LiquidHorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { showHexDialog = !showHexDialog }
                    .padding(vertical = 4.dp, horizontal = 6.dp)
            ) {
                LiquidIconBox(
                    icon = LiquidIcons.Edit,
                    size = 28.dp,
                    iconSize = 14.dp,
                    containerColor = colorScheme.primary.copy(alpha = 0.12f),
                    iconTint = colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
                Text(
                    text = "Inserimento Manuale Codice Esadecimale",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = if (showHexDialog) LiquidIcons.KeyboardArrowUp else LiquidIcons.KeyboardArrowDown,
                contentDescription = null,
                tint = glassColors.secondaryContent,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { showHexDialog = !showHexDialog }
            )
        }

        AnimatedVisibility(visible = showHexDialog) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(colorScheme.onSurface.copy(alpha = 0.05f))
                    .border(1.dp, colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "#",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = colorScheme.primary
                    )
                )

                BasicTextField(
                    value = hexInputText,
                    onValueChange = { input ->
                        val clean = input.filter { it.isLetterOrDigit() }.take(8).uppercase()
                        hexInputText = clean
                        val parsed = parseHexToColor(clean)
                        if (parsed != null) {
                            val (h, s, v) = colorToHsv(parsed)
                            hue = h
                            saturation = s
                            value = v
                            alpha = parsed.alpha
                            onColorSelected(parsed)
                        }
                    },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = colorScheme.onSurface
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.weight(1f),
                    cursorBrush = SolidColor(colorScheme.primary),
                    decorationBox = { innerTextField ->
                        if (hexInputText.isEmpty()) {
                            Text(
                                text = hexString.removePrefix("#"),
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 15.sp,
                                    color = colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                                )
                            )
                        }
                        innerTextField()
                    }
                )

                // Incolla da Clipboard Button
                Box(
                    modifier = Modifier
                        .clip(Capsule())
                        .background(colorScheme.primary.copy(alpha = 0.12f))
                        .clickable {
                            val clipText = clipboardManager.getText()?.text?.trim().orEmpty()
                            val clean = clipText.removePrefix("#").filter { it.isLetterOrDigit() }.take(8).uppercase()
                            if (clean.isNotEmpty()) {
                                hexInputText = clean
                                val parsed = parseHexToColor(clean)
                                if (parsed != null) {
                                    val (h, s, v) = colorToHsv(parsed)
                                    hue = h
                                    saturation = s
                                    value = v
                                    alpha = parsed.alpha
                                    onColorSelected(parsed)
                                }
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Incolla",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Converts HSV values (h in 0..360, s in 0..1, v in 0..1) to Compose Color.
 */
private fun hsvToColor(h: Float, s: Float, v: Float, alpha: Float = 1f): Color {
    val c = v * s
    val hPrime = (h % 360f) / 60f
    val x = c * (1f - abs((hPrime % 2f) - 1f))
    val m = v - c

    val (r1, g1, b1) = when {
        hPrime < 1f -> Triple(c, x, 0f)
        hPrime < 2f -> Triple(x, c, 0f)
        hPrime < 3f -> Triple(0f, c, x)
        hPrime < 4f -> Triple(0f, x, c)
        hPrime < 5f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    return Color(
        red = (r1 + m).coerceIn(0f, 1f),
        green = (g1 + m).coerceIn(0f, 1f),
        blue = (b1 + m).coerceIn(0f, 1f),
        alpha = alpha.coerceIn(0f, 1f)
    )
}

/**
 * Converts Compose Color to HSV (Hue 0..360, Saturation 0..1, Value 0..1).
 */
private fun colorToHsv(color: Color): Triple<Float, Float, Float> {
    val r = color.red
    val g = color.green
    val b = color.blue
    val max = maxOf(r, maxOf(g, b))
    val min = minOf(r, minOf(g, b))
    val delta = max - min

    val h = when {
        delta == 0f -> 0f
        max == r -> 60f * (((g - b) / delta) % 6f)
        max == g -> 60f * (((b - r) / delta) + 2f)
        else -> 60f * (((r - g) / delta) + 4f)
    }.let { if (it < 0f) it + 360f else it }

    val s = if (max == 0f) 0f else delta / max
    val v = max
    return Triple(h, s.coerceIn(0f, 1f), v.coerceIn(0f, 1f))
}

/**
 * Formats a Compose Color to #RRGGBB or #AARRGGBB hex string.
 */
private fun colorToHex(color: Color, includeAlpha: Boolean = false): String {
    val a = (color.alpha * 255).toInt().coerceIn(0, 255)
    val r = (color.red * 255).toInt().coerceIn(0, 255)
    val g = (color.green * 255).toInt().coerceIn(0, 255)
    val b = (color.blue * 255).toInt().coerceIn(0, 255)
    return if (includeAlpha && a < 255) {
        "#${a.toHexByte()}${r.toHexByte()}${g.toHexByte()}${b.toHexByte()}"
    } else {
        "#${r.toHexByte()}${g.toHexByte()}${b.toHexByte()}"
    }
}

private fun parseHexToColor(hex: String): Color? {
    val clean = hex.trim().removePrefix("#")
    return try {
        when (clean.length) {
            6 -> {
                val r = clean.substring(0, 2).toInt(16) / 255f
                val g = clean.substring(2, 4).toInt(16) / 255f
                val b = clean.substring(4, 6).toInt(16) / 255f
                Color(r, g, b, 1f)
            }
            8 -> {
                val a = clean.substring(0, 2).toInt(16) / 255f
                val r = clean.substring(2, 4).toInt(16) / 255f
                val g = clean.substring(4, 6).toInt(16) / 255f
                val b = clean.substring(6, 8).toInt(16) / 255f
                Color(r, g, b, a)
            }
            3 -> {
                val r = clean.substring(0, 1).repeat(2).toInt(16) / 255f
                val g = clean.substring(1, 2).repeat(2).toInt(16) / 255f
                val b = clean.substring(2, 3).repeat(2).toInt(16) / 255f
                Color(r, g, b, 1f)
            }
            else -> null
        }
    } catch (_: Exception) {
        null
    }
}

private fun Int.toHexByte(): String = toString(16).uppercase().padStart(2, '0')
