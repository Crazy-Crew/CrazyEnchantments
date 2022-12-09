plugins {
    java

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

val buildNumber: String? = System.getenv("BUILD_NUMBER")

val jenkinsVersion = "1.9.7-b$buildNumber"

group = "com.badbones69.crazyenchantments"
version = "1.9.7"
description = "Adds over 80 unique enchantments to your server and more! "

repositories {
    /**
     * PAPI Team
     */
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

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
     * Paper Team
     */
    maven("https://repo.papermc.io/repository/maven-public/")

    /**
     * NBT API
     */
    maven("https://repo.codemc.org/repository/maven-public/")

    /**
     * Towny Team
     */
    maven("https://repo.glaremasters.me/repository/towny/")

    /**
     * BG Software Team
     */
    maven("https://repo.bg-software.com/repository/api/")

    /**
     * CrazyCrew Repository
     * All 3rd party dependencies
     */
    maven("https://repo.badbones69.com/plugins/")

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
    implementation("com.intellectualsites.informative-annotations", "informative-annotations", "1.2")
    implementation("com.intellectualsites.paster", "Paster", "1.1.4")
    
    implementation("de.tr7zw", "nbt-data-api", "2.10.0")

    implementation("org.bstats", "bstats-bukkit", "3.0.0")

    compileOnly("io.papermc.paper", "paper-api", "1.19.3-R0.1-SNAPSHOT")

    compileOnly("com.bgsoftware", "SuperiorSkyblockAPI", "2022.8.1")
    compileOnly("com.bgsoftware", "WildStackerAPI", "2022.5")

    compileOnly("de.dustplanet", "silkspawners", "7.2.0") {
        exclude("org.bukkit", "bukkit")
        exclude("org.spigotmc", "spigot")
        exclude("com.destroystokyo.paper", "paper")
        exclude("com.sk89q", "worldguard")
        exclude("com.sk89q", "worldedit")
        exclude("com.massivecraft.massivecore", "MassiveCore")
        exclude("com.massivecraft.factions", "Factions")
        exclude("net.gravitydevelopment.updater", "updater")
        exclude("com.intellectualsites", "Pipeline")
    }

    compileOnly("com.github.TechFortress", "GriefPrevention", "16.18")

    compileOnly("com.massivecraft", "Factions", "1.6.9.5-U0.6.9")

    compileOnly("com.palmergames.bukkit.towny", "towny", "0.98.3.10")

    compileOnly("com.github.oraxen", "oraxen", "master-SNAPSHOT")

    compileOnly("fr.neatmonster", "nocheatplus", "3.16.1-SNAPSHOT")
    compileOnly("me.vagdedes.spartan", "SpartanAPI", "9.1")

    compileOnly("com.plotsquared", "PlotSquared-Core", "6.10.1")

    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.2.9") {
        exclude("org.bukkit", "bukkit")
        exclude("org.bstats", "bstats-bukkit")
    }

    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.1.0-SNAPSHOT") {
        exclude("org.bukkit", "bukkit")
        exclude("org.bstats", "bstats-bukkit")
    }

    compileOnly("me.clip", "placeholderapi", "2.11.2") {
        exclude(group = "org.spigotmc", module = "spigot")
        exclude(group = "org.bukkit", module = "bukkit")
    }

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    shadowJar {
        if (buildNumber != null) {
            archiveFileName.set("${rootProject.name}-[v${jenkinsVersion}].jar")
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