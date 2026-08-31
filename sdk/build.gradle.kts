import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    tasks.withType<KotlinCompilationTask<*>>().configureEach {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-Xexpect-actual-classes",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi"
            )
        }
    }

    android {
        namespace = "com.anto426.liquidmonet.sdk"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "LiquidMonet"
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir("src/main/java")
            // The bundled Kyant renderer is part of this SDK. Only its four Android-backed
            // bridges are replaced by expect/actual implementations for KMP.
            kotlin.exclude("com/kyant/backdrop/Platform.kt")
            kotlin.exclude("com/kyant/backdrop/RuntimeShader.kt")
            kotlin.exclude("com/kyant/backdrop/internal/Paint.kt")
            kotlin.exclude("com/kyant/backdrop/internal/RenderEffect.kt")
            kotlin.exclude("com/anto426/liquidmonet/glass/runtime/LiquidGlassPerformanceManager.kt")

            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(libs.compose.material3.multiplatform)
                implementation(compose.ui)
                implementation(libs.navigationevent.compose)
                api(libs.kotlinx.datetime)
            }
        }

        androidMain {
            kotlin.srcDir("src/main/java")
            kotlin.include("**/*.android.kt")
            kotlin.include("com/anto426/liquidmonet/glass/runtime/LiquidGlassPerformanceManager.kt")

            dependencies {
                implementation(libs.androidx.core.ktx)
            }
        }
    }
}
