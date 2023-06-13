dependencyResolutionManagement {
    repositories.gradlePluginPortal()
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()

        maven("https://repo.papermc.io/repository/maven-public/")

        maven("https://repo.crazycrew.us/first-party/")
        maven("https://repo.crazycrew.us/third-party/")
    }
}

rootProject.name = "CrazyEnchantments"

val lowerCase = rootProject.name.lowercase()

fun includeProject(name: String) {
    include(name) {
        this.name = "$lowerCase-$name"
    }
}

fun include(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}