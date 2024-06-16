import com.ryderbelserion.feather.includeProject

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "CrazyEnchantments"

pluginManagement {
    repositories {
        maven("https://repo.crazycrew.us/releases")

        gradlePluginPortal()
    }
}

plugins {
    id("com.ryderbelserion.feather-settings") version "0.0.1"
}

listOf("paper").forEach(::includeProject)