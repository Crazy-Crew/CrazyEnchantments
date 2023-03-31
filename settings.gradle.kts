dependencyResolutionManagement {
    versionCatalogs {
        create("settings") {
            from(files("gradle/settings.versions.toml"))
        }
    }

    repositories.gradlePluginPortal()
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "CrazyEnchantments"

val lowerCase = rootProject.name.lowercase()

listOf("paper").forEach(::includePlatform)

fun includeProject(name: String) {
    include(name) {
        this.name = "$lowerCase-$name"
    }
}

fun includeModule(name: String) {
    include(name) {
        this.name = "$lowerCase-module-$name"
        this.projectDir = file("modules/$name")
    }
}

fun includePlatform(name: String) {
    include(name) {
        this.name = "$lowerCase-$name"
        this.projectDir = file("platforms/$name")
    }
}

fun includePlatformModule(name: String, platform: String) {
    include(name) {
        this.name = "$lowerCase-module-$platform-$name"
        this.projectDir = file("modules/$platform/$name")
    }
}

fun include(name: String, block: ProjectDescriptor.() -> Unit) {
    include(name)
    project(":$name").apply(block)
}