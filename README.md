# 💧 Liquid Monet SDK

**Liquid Monet** is a cutting-edge, high-performance UI library for Jetpack Compose that seamlessly unites **Optical Liquid Glassmorphism** (AGSL Snell Refraction, Lens Physics, 3D Specular Highlights) with **Google Material 3 Expressive & Monet Dynamic Color**.

---

## ✨ Features

- 💎 **Pure Optical Liquid Glass**: Crystal-clear refraction with Snell's law, chromatic aberration, specular reflections, and realistic 3D depth shadows.
- 🎨 **Monet Dynamic Color Theming**: Real-time palette harmonization with smooth animated transitions between seeds (`Sapphire`, `Emerald`, `Sunset`, `Violet`).
- ⚡ **Next.js-Inspired Dynamic Backgrounds**: Synthesized canvas backgrounds including the iconic `RadiantBeam` spotlight with radial-masked sub-pixel grid, `Aurora`, `MeshGlow`, and `OrbitalPulse`.
- 🌊 **Material 3 Expressive Components**:
  - `AntoExpressiveTopBar` & `AntoAnimatedSearchField`
  - `AntoFluidNavigationBar` & `AntoGlassBottomTabs`
  - `AntoDialog` & `AntoSheet` (Optical crystal modal panels)
  - `AntoCard`, `AntoButton`, `AntoSwitch`, `AntoSlider`
  - `AntoLinearProgressIndicator` & `AntoCircularProgressIndicator` (Wavy & Standard)
  - `AntoMediaController`, `AntoControlCenterTile`, `AntoFilterChip`, `AntoShimmerBox`
  - `AntoMonetPaletteSelector`

---

## 🚀 Getting Started

### 1. Setup Theme & Glass Scene

```kotlin
AntoUITheme(
    useMonetEngine = true,
    customMonetSeed = AntoMonetPresets.Sapphire
) {
    AntoGlassScene(
        modifier = Modifier.fillMaxSize(),
        background = {
            AntoLiquidBackground(
                effect = AntoBackgroundEffect.RadiantBeam
            )
        }
    ) { backdropState ->
        // Your liquid glass UI components here
        AntoCard(backdropState = backdropState) {
            Text("Powered by Liquid Monet")
        }
    }
}
```

### 2. Add Floating Liquid Navigation Bar

```kotlin
AntoFluidNavigationBar(
    selectedIndex = selectedTab,
    onItemSelected = { selectedTab = it },
    items = listOf(
        AntoNavItemData(AntoIcons.Home, "Home"),
        AntoNavItemData(AntoIcons.Star, "Componenti"),
        AntoNavItemData(AntoIcons.Settings, "Impostazioni")
    ),
    backdropState = backdropState
)
```

---

## 🛠️ Build & Run

To build and install the demo showcase application:

```bash
./gradlew :app:assembleDebug
```
