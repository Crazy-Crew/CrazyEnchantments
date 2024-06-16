rootProject.name = "buildSrc"

dependencyResolutionManagement {
    repositories {
        maven("https://repo.crazycrew.us/releases")

        gradlePluginPortal()

        mavenCentral()
    }
}

pluginManagement {
    repositories {
        maven("https://repo.crazycrew.us/releases")

        gradlePluginPortal()
    }
}

plugins {
    id("com.ryderbelserion.feather-settings")
}