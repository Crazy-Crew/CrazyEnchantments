pluginManagement {
    repositories {
        gradlePluginPortal()

        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

rootProject.name = "Crazy-Enchantments"

include("api", "plugin", "modern")