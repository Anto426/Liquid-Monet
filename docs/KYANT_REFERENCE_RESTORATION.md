# Kyant interaction reference

Reference: https://github.com/Kyant0/AndroidLiquidGlass

Compared against branch `kmp`, commit `65ab177e90e5c1d8c62e70cf7755841982da65f6`.
The local comparison checkout is in UniApp's `.local-references/AndroidLiquidGlass`,
excluded through UniApp's `.git/info/exclude`. It is not a build dependency.

The catalog's `LiquidBottomTabs`, `LiquidToggle`, `LiquidSlider` and
`utils/DampedDragAnimation` inform the adapted interaction recipes. Upstream is
Apache-2.0; its license is retained in `licenses/AndroidLiquidGlass-Apache-2.0.txt`.
These are modified implementations, not an unmodified upstream import.

## Restored behavior

- Navbar and category tabs: 78/56 pressed lens scale, 1.2 selected-content scale,
  velocity-dependent deformation, bounded 4dp panel displacement and spring return.
- The panel, recorded tab mask and moving lens translate together. The moving lens
  uses the 10dp/14dp interactive recipe without a second blur over the tab mask.
- A drag returning to the current tab still snaps to its center. RTL lens placement
  uses the full tab range rather than the lens's own width.
- Navbar input is handled on the stationary panel before its graphics transforms.
  After horizontal touch slop it consumes drag events before child tab clicks;
  ordinary taps retain the existing click/semantics path. Pointer deltas are accumulated
  independently of the spring. Only release commits selection; vertical gestures,
  multi-touch and cancellation restore the current selection.
- Shared drag physics separates fast, critically damped tracking from softer X/Y
  deformation, benefiting navbar, category tabs, switches and sliders.
- Old release jobs cannot collapse a new press; completed movement clears residual
  velocity. Switches read current enabled state and density in gesture callbacks.
- Slider/switch resting paint fades away during a press to expose the refracted track.

## Deliberately retained Liquid Monet behavior

- Monet colors, the scene/backdrop system, common optical recipes and device budgets.
- Reduced motion disables elastic scaling, panel displacement and velocity stretch.
- Navigation callbacks only report actual user selections, never router-driven changes.
- The duplicate color-mask row has no input handlers or accessibility nodes.
- Touch highlights stay clipped to the capsule, independently of the expanding lens.
- Material Slider remains the slider's input/semantics layer.
- Stable panels keep the existing SDK depth treatment; moving lenses use the reference's
  non-depth lens treatment. No renderer replacement or app hierarchy change was made.

## Verification status

Source comparison and whitespace/exclusion checks only. No compilation, installation,
instrumented test, screenshot comparison or frame-time benchmark was performed.

Device checklist before declaring visual parity:

1. Navbar and category tabs: tap each destination, hold the selected lens, drag to
   another tab, return to the current tab, then cancel a drag. Check one navigation
   callback per changed user selection and exact centering after release.
2. Rapid tap/drag/press sequences: check there is no early deflation or stuck stretch.
3. Check light/dark themes, a single tab, changed tab count, RTL and rotation.
4. Switch: tap, drag, cancel, disable/re-enable and externally change checked state.
   Slider: tap, drag, use discrete steps, keyboard and accessibility actions.
5. Check that the moving lens magnifies content and the touch highlight never becomes
   a rectangular white halo. The lens itself intentionally grows beyond the panel.
6. Enable reduced motion and low-effect/device-saving policies; verify interaction
   stays usable without bounce and that the existing graphics fallbacks still apply.
