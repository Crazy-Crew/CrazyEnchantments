plugins {
    java

    kotlin("jvm") version "1.6.21"

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "17"
    }
}

group = "com.badbones69.crazyenchantments"
version = "[1.18-1.19]-1.9.1-${System.getenv("BUILD_NUMBER") ?: "SNAPSHOT"}"
description = "A plugin full of Crazy Enchantments!"

repositories {

    // PAPI API
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    // Silk Spawners API
    maven("https://repo.dustplanet.de/artifactory/libs-release-local/")

    // MVDW API
    maven("https://repo.mvdw-software.com/content/groups/public/")

    // NBT API | StackMob API
    maven("https://repo.codemc.org/repository/maven-public/")

    // Paper API
    maven("https://repo.papermc.io/repository/maven-public/")

    // Towny API
    maven("https://repo.glaremasters.me/repository/towny/")

    // Spartan API
    maven("https://nexus.sparky.ac/repository/Sparky/")

    // FactionsUUID API
    maven("https://ci.ender.zone/plugin/repository/everything/")

    // Our Repo
    maven("https://repo.badbones69.com/releases/")

    // World Edit API / World Guard API
    maven("https://maven.enginehub.org/repo/")

    // Vault API
    maven("https://jitpack.io/")

    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("com.sk89q.worldguard:worldguard-legacy:7.0.0-SNAPSHOT") {
        exclude("org.bukkit", "bukkit")
        exclude("org.bstats", "bstats-bukkit")
    }

    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.0.0-SNAPSHOT") {
        exclude("org.bukkit", "bukkit")
        exclude("org.bstats", "bstats-bukkit")
    }

    compileOnly("com.plotsquared:PlotSquared-Core:6.8.1")

    // SkyBlock Plugins.
    compileOnly("com.github.OmerBenGera:SuperiorSkyblockAPI:b11")

    // Towny Plugins.
    compileOnly("com.palmergames.bukkit.towny:towny:0.98.2.0")

    // Factions Plugins.
    compileOnly("com.massivecraft:Factions:1.6.9.5-U0.6.9")

    compileOnly("com.github.cryptomorin:kingdoms:1.13.3")

    // Stacker Plugins.
    compileOnly("uk.antiperson.stackmob:StackMob:5.5.3")

    // Spawner Plugins.
    compileOnly("de.dustplanet:silkspawners:7.2.0") {
        exclude("org.bukkit", "bukkit")
        exclude("org.spigotmc", "spigot")
        exclude("com.destroystokyo.paper", "paper")
        exclude("com.sk89q", "worldguard")
        exclude("com.sk89q", "worldedit")
        exclude("com.massivecraft.massivecore", "MassiveCore")
        exclude("com.massivecraft.factions", "Factions")
        exclude("net.gravitydevelopment.updater", "updater")
    }

    // Misc Crap.
    compileOnly("org.apache.commons:commons-text:1.9")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    // Anti Cheats.
    compileOnly("me.vagdedes:SpartanAPI:9.1")

    compileOnly("me.frep:vulcan-api:1.0.0")

    // Required.
    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")

    implementation("org.bstats:bstats-bukkit:3.0.0")

    implementation("de.tr7zw:nbt-data-api:2.10.0")

    compileOnly(kotlin("stdlib", "1.6.21"))
}

tasks {
    shadowJar {
        minimize()

        archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")

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
                "group" to project.group,
                "version" to project.version,
                "description" to project.description
            )
        }
    }
}