pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

rootProject.name = "Crazy-Enchantments"

include("api", "plugin", "v1_12_2_down", "v1_13_up")