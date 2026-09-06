# Liquid Monet SDK

**Liquid Monet** is a Compose Multiplatform UI library that combines adaptive Material 3 controls,
Monet color and real optical glass (blur, lens refraction, chromatic aberration and highlights).
The public API lives under `com.anto426.liquidmonet`; renderer implementation details are internal.

---

## Features

- **Component-owned optical glass**: cards, controls, menus, bars, dialogs, sheets, toast and media surfaces sample the active scene themselves.
- **Adaptive rendering engine**: quality, refraction and motion respond to the device/performance profile.
- **Monet dynamic color**: real-time palette harmonization with animated seed changes.
- **Material-style controls with liquid interaction**:
  - `LiquidTopBar` & `LiquidSearchBar`
  - `LiquidNavigationBar` & `LiquidTabBar`
  - `LiquidDialog`, `LiquidSheet`, `LiquidToast` & `LiquidSnackbar`
  - `LiquidCard`, `LiquidButton`, `LiquidFloatingActionButton`, `LiquidSwitch`, `LiquidSlider` & `LiquidRangeSlider`
  - `LiquidLoading`, `LiquidLinearProgressIndicator` & `LiquidCircularProgressIndicator`
  - `LiquidMediaController`, `LiquidControlCenterTile`, `LiquidChip` & `LiquidShimmerBox`
  - `LiquidAnimatedSwitcher`, `LiquidChipSelectionGroup` & `LiquidLazyFooter`
  - one `LiquidTextField` API with `LiquidTextFieldType` for text, email, phone, number, password and text area

---

## Getting started

### Theme and glass scene

```kotlin
LiquidMonetTheme(
    useMonetEngine = true,
    customMonetSeed = LiquidMonetPresets.Sapphire
) {
    LiquidGlassScene(
        modifier = Modifier.fillMaxSize(),
        background = {
            LiquidBackground(
                effect = LiquidBackgroundEffect.RadiantBeam
            )
        }
    ) { backdrop ->
        LiquidCard(backdropState = backdrop) {
            Text("Powered by Liquid Monet")
        }
    }
}
```

Components inside a `LiquidGlassScene` resolve its backdrop automatically. The explicit
`backdropState` parameter remains useful for standalone surfaces and advanced layer composition.

### Floating navigation bar

```kotlin
LiquidNavigationBar(
    selectedIndex = selectedTab,
    onItemSelected = { selectedTab = it },
    items = listOf(
        LiquidNavigationItem(label = "Home", icon = LiquidIcons.Home),
        LiquidNavigationItem(label = "Componenti", icon = LiquidIcons.Star),
        LiquidNavigationItem(label = "Impostazioni", icon = LiquidIcons.Settings)
    ),
    backdropState = backdropState
)
```

### Swipe-driven animated content

```kotlin
LiquidAnimatedSwitcher(
    targetState = page,
    transition = LiquidSwitcherTransition.LiquidMorph,
    onSwipeForward = { page++ },
    onSwipeBackward = { page-- }
) { currentPage ->
    Page(currentPage)
}
```

---

## Build and run

Build the Android SDK and demo showcase:

```bash
./gradlew :sdk:assembleAndroidMain :app:assembleDebug
```

Install the demo with `./gradlew :app:installDebug`. Run device interaction tests with
`./gradlew :app:connectedDebugAndroidTest` and validate shared Kotlin metadata with
`./gradlew :sdk:compileCommonMainKotlinMetadata`.

See [SDK architecture](docs/ARCHITECTURE.md) for source ownership and verification boundaries.
Android uses a [persistent device calibration](docs/DEVICE_CALIBRATION.md) with CPU, memory and
graphics measurements on first launch. Later launches reuse the same profile.
Run `python scripts/check_sdk_structure.py` to check the complete source layout.
