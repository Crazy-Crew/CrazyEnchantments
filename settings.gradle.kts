pluginManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")

        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")

        gradlePluginPortal()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            library("triumphcmds", "dev.triumphteam", "triumph-cmd-bukkit").version("2.0.0-ALPHA-9")

            library("worldedit", "com.sk89q.worldedit", "worldedit-bukkit").version("7.2.15")
            library("worldguard", "com.sk89q.worldguard", "worldguard-bukkit").version("7.1.0-SNAPSHOT")

            library("placeholderapi", "me.clip", "placeholderapi").version("2.11.5")
            library("vault", "com.github.MilkBowl", "VaultAPI").version("1.7.1")
            library("metrics", "org.bstats", "bstats-bukkit").version("3.0.2")
            library("nbtapi", "de.tr7zw", "item-nbt-api").version("2.12.2")
            library("oraxen", "io.th0rgal", "oraxen").version("1.164.0")
            library("configme", "ch.jalu", "configme").version("1.4.1")
        }
    }
}

rootProject.name = "CrazyEnchantments"

include("common")
include("paper")