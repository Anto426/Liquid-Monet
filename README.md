# 💧 Liquid Monet SDK

**Liquid Monet** is a cutting-edge, high-performance UI library for Jetpack Compose that seamlessly unites **Optical Liquid Glassmorphism** (AGSL Snell Refraction, Lens Physics, 3D Specular Highlights) with **Google Material 3 Expressive & Monet Dynamic Color**.

---

## ✨ Features

- 💎 **Pure Optical Liquid Glass**: Crystal-clear refraction with Snell's law, chromatic aberration, specular reflections, and realistic 3D depth shadows.
- 🎨 **Monet Dynamic Color Theming**: Real-time palette harmonization with smooth animated transitions between seeds (`Sapphire`, `Emerald`, `Sunset`, `Violet`).
- ⚡ **Next.js-Inspired Dynamic Backgrounds**: Synthesized canvas backgrounds including the iconic `RadiantBeam` spotlight with radial-masked sub-pixel grid, `Aurora`, `MeshGlow`, and `OrbitalPulse`.
- 🌊 **Material 3 Expressive Components**:
  - `LiquidTopBar` & `LiquidAnimatedSearchField`
  - `LiquidNavigationBar` & `LiquidGlassBottomTabs`
  - `LiquidDialog` & `LiquidSheet` (Optical crystal modal panels)
  - `LiquidCard`, `LiquidButton`, `LiquidSwitch`, `LiquidSlider`
  - `LiquidLinearProgressIndicator` & `LiquidCircularProgressIndicator` (Wavy & Standard)
  - `LiquidMediaController`, `LiquidControlCenterTile`, `LiquidFilterChip`, `LiquidShimmerBox`
  - `LiquidMonetPaletteSelector`

---

## 🚀 Getting Started

### 1. Setup Theme & Glass Scene

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
    ) { backdropState ->
        // Your liquid glass UI components here
        LiquidCard(backdropState = backdropState) {
            Text("Powered by Liquid Monet")
        }
    }
}
```

### 2. Add Floating Liquid Navigation Bar

```kotlin
LiquidNavigationBar(
    selectedIndex = selectedTab,
    onItemSelected = { selectedTab = it },
    items = listOf(
        LiquidNavItemData(LiquidIcons.Home, "Home"),
        LiquidNavItemData(LiquidIcons.Star, "Componenti"),
        LiquidNavItemData(LiquidIcons.Settings, "Impostazioni")
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
