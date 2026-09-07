# Consumer R8 / ProGuard rules for LiquidMonet SDK (Aggressive Optimization & Obfuscation Mode)

# Jetpack Compose runtime
-keepclassmembers class * extends androidx.compose.runtime.RecomposeScope { *; }
-dontwarn androidx.compose.**



# Android graphics RenderEffect and RuntimeShader (API 31/33 fallback)
-dontwarn android.graphics.RenderEffect
-dontwarn android.graphics.RuntimeShader

# Allow aggressive shrinking and obfuscation of unused LiquidMonet & Backdrop components and shaders
-keep,allowshrinking,allowobfuscation class com.anto426.liquidmonet.** { *; }
-keep,allowshrinking,allowobfuscation class com.kyant.backdrop.** { *; }


