plugins {
    java

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val buildNumber: String? = System.getenv("BUILD_NUMBER")

val jenkinsVersion = "1.9.5-b$buildNumber"

group = "com.badbones69.crazyenchantments"
version = "1.9.5"
description = "Adds over 80 unique enchantments to your server and more! "

repositories {

    /**
     * PAPI Team
     */
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    /**
     * Spigot Team
     */
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

    /**
     * SilkSpawners Team
     */
    maven("https://repo.dustplanet.de/artifactory/libs-release-local/")

    /**
     * NCP Team
     */
    maven("https://repo.md-5.net/content/repositories/snapshots/")

    /**
     * FactionsUUID API
     */
    maven("https://ci.ender.zone/plugin/repository/everything/")

    /**
     * NBT API
     */
    maven("https://repo.codemc.org/repository/maven-public/")

    /**
     * Towny Team
     */
    maven("https://repo.glaremasters.me/repository/towny/")

    /**
     * SuperiorSkyBlock Team
     */
    maven("https://repo.bg-software.com/repository/api/")

    /**
     * Spartan Team
     */
    maven("https://nexus.sparky.ac/repository/Sparky/")

    /**
     * EngineHub Team
     */
    maven("https://maven.enginehub.org/repo/")

    /**
     * Everything else we need.
     */
    maven("https://jitpack.io/")

    mavenCentral()
}

dependencies {

    implementation(libs.bukkit.bstats)

    implementation(libs.nbt.api)

    compileOnly(libs.bukkit.worldguard) {
        exclude("org.bukkit", "bukkit")
        exclude("org.bstats", "bstats-bukkit")
    }

    compileOnly(libs.bukkit.worldedit) {
        exclude("org.bukkit", "bukkit")
        exclude("org.bstats", "bstats-bukkit")
    }

    // OmeR Team
    compileOnly(libs.superior.skyblock)
    compileOnly(libs.wild.stacker)
    // OmeR Team End

    compileOnly(libs.grief.prevention.api)

    compileOnly(libs.massive.factions.api)

    compileOnly(libs.silk.spawners.api) {
        exclude("org.bukkit", "bukkit")
        exclude("org.spigotmc", "spigot")
        exclude("com.destroystokyo.paper", "paper")
    }

    compileOnly(libs.plot.squared.api)

    compileOnly(libs.oraxen.api)

    compileOnly(libs.towny.api)

    compileOnly(libs.vault.api)

    compileOnly(libs.spigot)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    shadowJar {
        if (buildNumber != null) {
            archiveFileName.set("${rootProject.name}-[v${jenkinsVersion}.jar")
        } else {
            archiveFileName.set("${rootProject.name}-[v${rootProject.version}].jar")
        }

        listOf(
            "de.tr7zw",
            "org.bstats"
        ).forEach {
            relocate(it, "${rootProject.group}.plugin.lib.$it")
        }
    }

    compileJava {
        options.release.set(17)
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to rootProject.group,
                "version" to rootProject.version,
                "description" to rootProject.description
            )
        }
    }
}