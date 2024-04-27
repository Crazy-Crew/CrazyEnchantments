plugins {
    id("io.papermc.paperweight.userdev")

    id("root-plugin")
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.dustplanet.de/artifactory/libs-release-local/")

    maven("https://repo.md-5.net/content/repositories/snapshots/")

    maven("https://ci.ender.zone/plugin/repository/everything/")

    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.glaremasters.me/repository/towny/")

    maven("https://repo.bg-software.com/repository/api/")

    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://repo.crazycrew.us/third-party/")

    maven("https://repo.crazycrew.us/snapshots/")

    maven("https://maven.enginehub.org/repo/")

    maven("https://repo.oraxen.com/releases/")
}

dependencies {
    paperweight.paperDevBundle("1.20.5-R0.1-SNAPSHOT")
}

tasks {
    paperweight {
        reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
    }
}