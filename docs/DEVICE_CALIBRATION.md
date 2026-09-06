# Device calibration, preparation and rendering caches

Android selects a device sampling budget once before `LiquidMonetTheme` composes application
content. Later launches load that same result. Material fidelity is a separate setting:
`maximumGlassQuality` (HIGH by default) and `liquidIntensity` retain their optical meaning.
A slow CPU or small memory budget must not silently remove refraction, highlights or depth.

## Responsibilities and execution

| Owner | Responsibility | Execution |
| --- | --- | --- |
| Platform theme adapter | Await preparation, manage lifetime and publish Compose state | Main; suspending waits only |
| `LiquidGlassDeviceCalibration` | Share one process-owned operation; load/commit the permanent result | IO |
| `LiquidGlassHardwareProbe` | Read hardware facts, CPU clocks and stable device identity | IO |
| `LiquidGlassCpuProbe` | Timed arithmetic/transforms and memory transfers | Default |
| `LiquidGlassBenchmarkRunner` / `LiquidGlassRenderProbe` | Measure candidates within hardware limits | Worker; native drawing uses HWUI RenderThread/GPU |
| `LiquidGlassCalibrationStore` | Bounded reads and atomic profile writes | IO |
| `LiquidGlassShaderWarmup` | Prepare built-in shaders and a small offscreen render | Worker |
| `LiquidGlassDiagnosticsMonitor` | Register/remove callbacks and read system diagnostics | IO; callbacks only enqueue signals |
| `LiquidGlassPerformanceManager` | Convert immutable results and explicit settings to Compose state | Main, without system/disk queries |
| Backdrop modifiers | Draw the current scene and replay retained optical masks | Compose drawing pipeline |

`LiquidGlassOffscreenRenderSession` owns temporary HWUI resources for the benchmark and warm-up.
It does not own application content, a theme, a calibration decision or persistence.
Composition and draw callbacks still obey Compose's threading model; moving preparation to a
worker does not move arbitrary UI nodes or mutable graphics objects across threads.

The startup surface stays lightweight and animated while awaiting the result. Initial Android
calibration waits for a focused window. Rotation, theme changes and multiple windows await the
same process-owned operation, rather than cancelling and restarting a benchmark.

## Independent hardware limits

The decision takes the minimum of separate memory, CPU and rendering limits, never a weighted
average where abundant RAM could hide a slow processor.

| Signal | Use |
| --- | --- |
| Physical RAM, low-RAM flag, app heap class | Bound working memory |
| Arithmetic/transform workload | Single-core throughput: eight warm-ups, then five timed samples |
| Memory transfers | Four observable 1 MiB transfers per sample, separate from arithmetic |
| Core count, architecture, per-core maximum frequency | Supporting ceilings, not substitutes for measured speed |
| SoC, manufacturer, model, hardware, graphics API support | Hashed device/backend identity; no serial number or IMEI |
| Resolution, density and maximum supported refresh | Account for pixels and frame budget |
| Hardware-rendered scene | Background and six filtered panels at each candidate sampling resolution |

CPU and memory-copy timings use thread CPU time, excluding time descheduled by unrelated work.
Unavailable frequency files mean unknown, not zero. Candidates retain the same complete optical
workload, including dispersion on Android 13+. Only their texture sampling resolution changes:
MINIMAL 0.50, BALANCED 0.67, HIGH 0.85, ULTRA 1.00 along each axis.

Two warm-ups precede five measured frames per graphics candidate. Two output buffers are bounded
at about 1 MP on limited-memory devices or 4 MP on stronger-memory devices. Inter-frame pacing is
outside the timed sample. Smaller test surfaces are conservatively charged for the full display
pixel count, and a candidate must fit 70% of the refresh-rate frame interval.

Graphics timing includes recording, submission, completion and synchronization. It is an
end-to-end estimate, not a GPU hardware counter. A frame commit and the image fence synchronize
Android 13+; Android 12 uses a synchronized readback. Deferred HWUI submissions are awaited, not
mistaken for completed frames. See Android's [FrameRenderRequest contract](https://developer.android.com/reference/android/graphics/HardwareRenderer.FrameRenderRequest)
and [deferred-frame semantics](https://developer.android.com/reference/android/graphics/HardwareRenderer#SYNC_FRAME_DROPPED).
These initial thresholds and pixel extrapolation require validation on representative phones.
They do not establish an application frame-rate guarantee.

## Persistence and recovery

The atomic record lives at `noBackupFilesDir/liquid-glass-device-profile-v1`. The filename is stable;
its internal format version is 2, distinguishing sampling budgets from the earlier experimental
policy that reduced material fidelity. The record contains the hashed device key, tier, reason and
measurements. It stays local and is excluded from backup/transfer.

A pending record is committed before benchmarking. If the process dies, the next launch selects
a stable conservative budget with reason `INTERRUPTED`. Missing capabilities, failed tests and
startup power/thermal/memory constraints similarly produce explicit conservative results. None of
these reasons lowers the caller's requested optical fidelity. Supported platform capabilities
still limit which shader APIs can actually run.

The coordinator waits at most 2.5 seconds for the benchmark. The render loop checks an additional
1.8-second deadline between frames. Native driver calls may ignore cancellation; late results
never overwrite the selected profile. Temporary graphics resources are released when those calls
return. Storage and hardware reads are outside the benchmark timeout and remain on IO.

Normal app updates, transient pressure, rotation and battery saver do not change the saved profile.
Clearing app data, changing hardware/backend identity or an explicitly incompatible record version
allows calibration again. There is no automatic reclassification during use.

`LocalLiquidGlassPerformance.current.calibration` exposes the saved diagnostics. `qualityTier`
is the measured sampling budget, `opticalQualityTier` is the caller's material setting, and
`renderResolutionScale` is the chosen texture scale. A null optical tier retains the original
behavior of manually supplied performance states. Live pressure fields are diagnostics only.
Loading feedback remains animated at every budget unless explicit `reduceMotion` disables motion.

## Shader preparation and cache ownership

Android 13+ prepares the four built-in programs: refraction, dispersion, directional highlight and
ambient highlight. Compilation runs on Default. A bounded reserve holds 2–8 independent instances
per program according to memory capacity; no mutable shader/uniform object is shared between live
render nodes. Reserve refills are coalesced in background. Unknown custom shaders and an exhausted
reserve retain synchronous on-demand construction; this mechanism does not promise zero first-use
cost for an arbitrary number of surfaces or custom programs.

After a successful measured profile, a 256×256 offscreen scene draws all four programs through
their actual RenderEffect/paint paths and waits for GPU completion. This also runs on launches
that reuse a saved profile: process-local objects cannot be restored by a persistent “warmed” flag.
The hardware benchmark is not repeated. Android/driver pipeline reuse is implementation-dependent;
there is no application-managed export/import of compiled AGSL binaries here.

Warm-up has a separate 1.5-second wait budget. A source-versioned pending marker prevents repeating
a warm-up that previously crashed, failed or exceeded its deadline. An interrupted or failed
calibration skips the GPU warm-up. The profile remains unchanged. Native cancellation limitations
are the same as for calibration.

On iOS, four immutable Skia RuntimeEffects are compiled once on Default and reused with separate
builders for each renderer. This prepares source programs; it does not precompile a Metal pipeline.
iOS still retains its existing conservative performance profile pending a native hardware probe
and measurements on Apple devices.

Each local runtime shader cache keeps at most eight successful entries and eight rejected sources.
Changing a source under the same key invalidates that entry; an unchanged rejected source is not
compiled again every frame. Detachment clears the local cache.

## Other rendering caches

- Default backdrop recordings traverse the content subtree once and replay its display list.
  Custom recording callbacks retain their independent traversal.
- Outer offscreen compositing is retained. Downsampling applies only to the filtered backdrop;
  foreground text, controls, highlights and shadows stay at layout resolution. Effect density,
  dimensions and padding scale together so the lens keeps its logical shape and depth.
- Highlight and shadow display lists are reused while geometry and paint remain unchanged.
  Alpha/blend updates use layer properties. Color, dimensions, density, style and offsets invalidate
  the relevant masks; inner-shadow blur updates its effect independently. Custom highlight styles
  and generic paths keep per-draw recording. Detachment resets caches and releases layers.
- Shape outlines include font scale in their cache key. Inverse transform matrices retain their
  allocation and coefficients only while rotation and scale match.
- Monet palettes and resolved glass colors are cached by their inputs. Android system colors use
  direct resource IDs and refresh on configuration changes.
- Shimmer observes animation during drawing, avoiding a full glass-modifier recomposition per frame.

No full-scene screenshot cache is introduced: moving backgrounds, controls, theme changes and
refraction must remain live. Caches are bounded or scoped to a node/process rather than retaining
an unbounded history of bitmaps, shaders or geometry.

## Verification

```sh
./gradlew :sdk:testAndroidHostTest :sdk:compileCommonMainKotlinMetadata :sdk:compileKotlinIosArm64 :app:assembleDebug :app:compileDebugAndroidTestKotlin
python3 scripts/check_sdk_structure.py
./gradlew :app:connectedDebugAndroidTest
```

Host tests cover independent CPU/RAM constraints, clocks, graphics budget, persistent records,
optical-policy invariance, essential motion and shader cache reuse/invalidation/bounds. Android
instrumentation covers nested recording, menu interaction, visible loading motion, backdrop
alignment and retained refraction under downsampling. Compilation of instrumentation tests is not
an execution result.

For acceptance, verify a fresh calibration, process restart without reclassification, preparation
on a cached-profile launch, interrupted-record recovery, light/dark mode, rotation, moving
backgrounds, menus/sheets and scrolling. Inspect optical appearance and collect release-build
frame timing/memory on low-end hardware. Compilation and host tests cannot establish low-end frame
performance, native driver recovery or iOS visual behavior.
