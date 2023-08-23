pluginManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")

        gradlePluginPortal()
    }
}

rootProject.name = "CrazyEnchantments"

listOf(
    "paper"
).forEach {
    include(it)
}