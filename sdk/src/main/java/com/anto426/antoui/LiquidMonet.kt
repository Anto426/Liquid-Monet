package com.anto426.antoui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anto426.antoui.components.buttons.*
import com.anto426.antoui.components.inputs.*
import com.anto426.antoui.components.selection.*
import com.anto426.antoui.components.navigation.*
import com.anto426.antoui.components.feedback.*
import com.anto426.antoui.components.pickers.*
import com.anto426.antoui.components.cards.*
import com.anto426.antoui.components.display.*
import com.anto426.antoui.components.menu.*
import com.anto426.antoui.components.internal.AntoControlDefaults
import com.anto426.antoui.glass.*
import com.anto426.antoui.motion.*
import com.anto426.antoui.theme.AntoUITheme
import com.anto426.antoui.theme.monet.AntoMonetPresets
import com.anto426.antoui.theme.monet.AntoMonetSeed
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.emptyBackdrop
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle

/**
 * Liquid Monet SDK - Official Entry Point and Brand Suite.
 */

// 1. Theme & Engine
typealias LiquidMonetSeed = AntoMonetSeed
typealias LiquidMonetPresets = AntoMonetPresets

@Composable
fun LiquidMonetTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    useMonetEngine: Boolean = true,
    customMonetSeed: LiquidMonetSeed = LiquidMonetPresets.Sapphire,
    liquidIntensity: Float = 1f,
    content: @Composable () -> Unit
) {
    AntoUITheme(
        darkTheme = darkTheme,
        useMonetEngine = useMonetEngine,
        customMonetSeed = customMonetSeed,
        liquidIntensity = liquidIntensity,
        content = content
    )
}

// 2. Core Liquid Glass Primitives
@Composable
fun LiquidGlass(
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    shape: Shape = RoundedRectangle(24.dp),
    blurRadius: Dp = 4.dp,
    refractionHeight: Dp = 16.dp,
    refractionAmount: Dp = 32.dp,
    containerColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    AntoGlass(
        modifier = modifier,
        backdrop = backdropState,
        shape = shape,
        blurRadius = blurRadius,
        refractionHeight = refractionHeight,
        refractionAmount = refractionAmount,
        containerColor = containerColor,
        content = content
    )
}

// 3. Dynamic Background
@Composable
fun LiquidMonetBackground(
    modifier: Modifier = Modifier,
    effect: AntoBackgroundEffect = AntoBackgroundEffect.RadiantBeam,
    isDark: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    monetSeed: LiquidMonetSeed? = null,
    primaryOverride: Color? = null,
    secondaryOverride: Color? = null,
    tertiaryOverride: Color? = null,
    accentOverride: Color? = null,
    speedFactor: Float = 1.0f,
    intensity: Float = 1.0f,
    showVignette: Boolean = true
) {
    AntoLiquidBackground(
        modifier = modifier,
        effect = effect,
        isDark = isDark,
        monetSeed = monetSeed,
        primaryOverride = primaryOverride,
        secondaryOverride = secondaryOverride,
        tertiaryOverride = tertiaryOverride,
        accentOverride = accentOverride,
        speedFactor = speedFactor,
        intensity = intensity,
        showVignette = showVignette
    )
}

// 4. Liquid Glass Components
typealias LiquidButtonVariant = AntoButtonVariant
typealias LiquidButtonSize = AntoButtonSize

@Composable
fun LiquidCard(
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    shape: Shape = RoundedRectangle(28.dp),
    containerColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    AntoCard(
        modifier = modifier,
        backdrop = backdropState,
        shape = shape,
        containerColor = containerColor,
        content = content
    )
}

/**
 * LiquidButton - Single Unified Liquid Glass Button Component.
 */
@Composable
fun LiquidButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: LiquidButtonVariant = LiquidButtonVariant.Primary,
    size: LiquidButtonSize = LiquidButtonSize.Medium,
    shape: Shape = AntoControlDefaults.shape,
    tint: Color = Color.Unspecified,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    AntoButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        variant = variant,
        size = size,
        shape = shape,
        tint = tint,
        enabled = enabled,
        isLoading = isLoading,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        backdrop = backdropState
    )
}

@Composable
fun LiquidButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: LiquidButtonVariant = LiquidButtonVariant.Primary,
    size: LiquidButtonSize = LiquidButtonSize.Medium,
    shape: Shape = AntoControlDefaults.shape,
    tint: Color = Color.Unspecified,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    content: @Composable RowScope.() -> Unit
) {
    AntoButton(
        onClick = onClick,
        modifier = modifier,
        variant = variant,
        size = size,
        shape = shape,
        tint = tint,
        enabled = enabled,
        isLoading = isLoading,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        backdrop = backdropState,
        content = content
    )
}

/**
 * LiquidFloatingActionButton - Floating Action Button in Pure Liquid Glass.
 */
@Composable
fun LiquidFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    enabled: Boolean = true,
    containerColor: Color? = null,
    shape: Shape = Capsule(),
    content: @Composable () -> Unit
) {
    AntoFloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        size = size,
        backdrop = backdrop,
        backdropState = backdropState,
        enabled = enabled,
        containerColor = containerColor,
        shape = shape,
        content = content
    )
}

/**
 * LiquidExtendedFloatingActionButton - Extended FAB with icon and text in Pure Liquid Glass.
 */
@Composable
fun LiquidExtendedFloatingActionButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    containerColor: Color? = null,
    shape: Shape = Capsule()
) {
    AntoExtendedFloatingActionButton(
        onClick = onClick,
        icon = icon,
        text = text,
        modifier = modifier,
        expanded = expanded,
        enabled = enabled,
        backdrop = backdrop,
        backdropState = backdropState,
        containerColor = containerColor,
        shape = shape
    )
}

@Composable
fun LiquidFluidNavigationBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    items: List<AntoNavItemData>,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    AntoFluidNavigationBar(
        selectedIndex = selectedIndex,
        onItemSelected = onItemSelected,
        items = items,
        modifier = modifier,
        visible = visible,
        backdrop = backdropState
    )
}

@Composable
fun LiquidPaletteSelector(
    seeds: List<Pair<String, LiquidMonetSeed>>,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    AntoMonetPaletteSelector(
        seeds = seeds,
        selectedIndex = selectedIndex,
        onSelectIndex = onSelectIndex,
        modifier = modifier,
        backdrop = backdropState
    )
}

// 7. Liquid Toast Notifications
typealias LiquidToastType = AntoToastType
typealias LiquidToastData = AntoToastData
typealias LiquidToastState = AntoToastState

@Composable
fun rememberLiquidToastState(): LiquidToastState = rememberAntoToastState()

@Composable
fun LiquidToastHost(
    state: LiquidToastState,
    modifier: Modifier = Modifier,
    alignment: androidx.compose.ui.Alignment = androidx.compose.ui.Alignment.TopCenter,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    AntoToastHost(
        state = state,
        modifier = modifier,
        alignment = alignment,
        backdropState = backdropState
    )
}

@Composable
fun LiquidToast(
    data: LiquidToastData,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    AntoToast(
        data = data,
        modifier = modifier,
        onDismiss = onDismiss,
        backdropState = backdropState
    )
}

// 8. Unified Liquid Loading
typealias LiquidLoadingStyle = AntoLoadingStyle
typealias LiquidLoadingSize = AntoLoadingSize

@Composable
fun LiquidLoading(
    modifier: Modifier = Modifier,
    style: LiquidLoadingStyle = LiquidLoadingStyle.Circular,
    progress: Float? = null,
    size: LiquidLoadingSize = LiquidLoadingSize.Medium,
    message: String? = null,
    tint: Color = Color.Unspecified,
    shape: Shape = RoundedRectangle(16.dp),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    AntoLoading(
        modifier = modifier,
        style = style,
        progress = progress,
        size = size,
        message = message,
        tint = tint,
        shape = shape,
        backdrop = backdrop,
        backdropState = backdropState
    )
}

// 9. Unified Optical Liquid Glass Floating Thumb Slider
@Composable
fun LiquidSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    tint: Color = Color.Unspecified,
    trackHeight: androidx.compose.ui.unit.Dp = 8.dp,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f
) {
    AntoSlider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        backdrop = backdrop,
        backdropState = backdropState,
        tint = tint,
        trackHeight = trackHeight,
        valueRange = valueRange
    )
}

// 10. Unified Modal Dialog in Optical Liquid Glass
@Composable
fun LiquidDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    confirmButton: (@Composable () -> Unit)? = null,
    dismissButton: (@Composable () -> Unit)? = null,
    scrimColor: Color? = null,
    containerColor: Color? = null,
    accentColor: Color? = null
) {
    AntoDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        text = text,
        modifier = modifier,
        backdrop = backdrop,
        backdropState = backdropState,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        scrimColor = scrimColor,
        containerColor = containerColor,
        accentColor = accentColor
    )
}

// 11. Unified Bottom Sheet in Optical Liquid Glass
@Composable
fun LiquidSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    containerColor: Color? = null,
    content: @Composable () -> Unit
) {
    AntoSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        backdrop = backdrop,
        backdropState = backdropState,
        containerColor = containerColor,
        content = content
    )
}

// 12. Unified Dropdown Menu in Optical Liquid Glass
@Composable
fun LiquidDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: androidx.compose.ui.unit.DpOffset = androidx.compose.ui.unit.DpOffset(0.dp, 8.dp),
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    AntoGlassDropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        offset = offset,
        backdrop = backdrop,
        backdropState = backdropState,
        content = content
    )
}

@Composable
fun LiquidMenuItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    trailingText: String? = null,
    contentColor: Color = Color.Unspecified
) {
    AntoGlassMenuItem(
        text = text,
        icon = icon,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        trailingText = trailingText,
        contentColor = contentColor
    )
}

// 13. Unified Input Bar in Optical Liquid Glass
@Composable
fun LiquidTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = label ?: "Inserisci testo...",
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minHeight: androidx.compose.ui.unit.Dp = 54.dp,
    shape: Shape = RoundedRectangle(20.dp),
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    keyboardActions: androidx.compose.foundation.text.KeyboardActions = androidx.compose.foundation.text.KeyboardActions.Default,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    AntoTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        maxLines = maxLines,
        minHeight = minHeight,
        shape = shape,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        backdrop = backdrop,
        backdropState = backdropState
    )
}

// 14. Unified Search Bar in Optical Liquid Glass
@Composable
fun LiquidSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholderText: String = "Cerca...",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop,
    onSearch: ((String) -> Unit)? = null
) {
    AntoSearchBar(
        query = query,
        onQueryChange = onQueryChange,
        placeholderText = placeholderText,
        modifier = modifier,
        enabled = enabled,
        backdrop = backdrop,
        backdropState = backdropState,
        onSearch = onSearch
    )
}

// 15. Unified Action & Selection Items in Optical Liquid Glass
@Composable
fun LiquidActionItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    trailingText: String? = null,
    isDestructive: Boolean = false,
    enabled: Boolean = true
) {
    AntoGlassActionItem(
        text = text,
        onClick = onClick,
        modifier = modifier,
        icon = icon,
        trailingText = trailingText,
        isDestructive = isDestructive,
        enabled = enabled
    )
}

@Composable
fun <T> LiquidSelectPicker(
    label: String,
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier,
    itemSubtitle: ((T) -> String?)? = null,
    itemIcon: ((T) -> androidx.compose.ui.graphics.vector.ImageVector?)? = null,
    shape: Shape = RoundedRectangle(18.dp),
    enabled: Boolean = true,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    AntoGlassSelectPicker(
        label = label,
        items = items,
        selectedItem = selectedItem,
        onItemSelected = onItemSelected,
        itemLabel = itemLabel,
        modifier = modifier,
        itemSubtitle = itemSubtitle,
        itemIcon = itemIcon,
        shape = shape,
        enabled = enabled,
        backdrop = backdrop,
        backdropState = backdropState
    )
}

@Composable
fun LiquidBackgroundSelector(
    selectedEffect: AntoBackgroundEffect,
    onEffectSelected: (AntoBackgroundEffect) -> Unit,
    modifier: Modifier = Modifier,
    backdrop: Backdrop = emptyBackdrop(),
    backdropState: Backdrop = backdrop
) {
    AntoBackgroundSelector(
        selectedEffect = selectedEffect,
        onEffectSelected = onEffectSelected,
        modifier = modifier,
        backdrop = backdrop,
        backdropState = backdropState
    )
}

// 12. Motion & Screen Transitions
typealias LiquidNavTransition = AntoNavTransition

@Composable
fun <T> LiquidAnimatedNavContent(
    targetState: T,
    modifier: Modifier = Modifier,
    transition: LiquidNavTransition = LiquidNavTransition.AutoDirectional,
    contentAlignment: androidx.compose.ui.Alignment = androidx.compose.ui.Alignment.TopStart,
    label: String = "LiquidAnimatedNavContent",
    content: @Composable androidx.compose.animation.AnimatedContentScope.(targetState: T) -> Unit
) {
    AntoAnimatedNavContent(
        targetState = targetState,
        modifier = modifier,
        transition = transition,
        contentAlignment = contentAlignment,
        label = label,
        content = content
    )
}


