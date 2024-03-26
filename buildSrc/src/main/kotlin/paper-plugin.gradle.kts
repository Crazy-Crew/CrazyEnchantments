plugins {
    id("root-plugin")
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.dustplanet.de/artifactory/libs-release-local/")

    maven("https://repo.md-5.net/content/repositories/snapshots/")

    maven("https://ci.ender.zone/plugin/repository/everything/")

    maven("https://repo.codemc.org/repository/maven-public/")

    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.glaremasters.me/repository/towny/")

    maven("https://repo.bg-software.com/repository/api/")

    maven("https://repo.crazycrew.us/third-party/")

    maven("https://maven.enginehub.org/repo/")

    maven("https://repo.oraxen.com/releases/")

    flatDir { dirs("libs") }
}

val mcVersion: String = providers.gradleProperty("mcVersion").get()

project.version = if (System.getenv("BUILD_NUMBER") != null) "${rootProject.version}-${System.getenv("BUILD_NUMBER")}" else rootProject.version

tasks {
    modrinth {
        loaders.addAll("paper", "purpur")
    }
}