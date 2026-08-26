pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Antosdk"
include(":sdk")

// UniApp consumes only the SDK library. Keep the showcase app opt-in so it
// is not imported as a runnable Android application by the UniApp project.
if (providers.gradleProperty("antosdk.demo").orNull == "true") {
    include(":app")
}
