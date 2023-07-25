plugins {
    id("paper-plugin")

    //id("publish-task")
}

dependencies {
    implementation("de.tr7zw", "item-nbt-api", "2.11.3")

    implementation("org.bstats", "bstats-bukkit", "3.0.2")

    compileOnly("com.plotsquared", "PlotSquared-Core", "6.11.1")

    compileOnly("com.intellectualsites.informative-annotations", "informative-annotations", "1.3")

    compileOnly("com.intellectualsites.paster", "Paster", "1.1.5")

    compileOnly("com.bgsoftware", "SuperiorSkyblockAPI", "2023.1")

    compileOnly("de.dustplanet", "silkspawners", "7.5.0") {
        exclude("*", "*")
    }

    compileOnly("com.massivecraft", "Factions", "1.6.9.5-U0.6.31") {
        exclude("org.kitteh")
        exclude("org.spongepowered")
        exclude("com.darkblade12")
    }

    compileOnly("com.palmergames.bukkit.towny", "towny", "0.99.0.0")

    compileOnly("fr.neatmonster", "nocheatplus", "3.16.1-SNAPSHOT")

    compileOnly("me.vagdedes", "spartanapi", "9.1")

    compileOnly("com.bgsoftware", "WildStackerAPI", "2023.1")

    compileOnly("io.th0rgal", "oraxen", "1.156.3")

    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.2.15")

    compileOnly("com.github.TechFortress", "GriefPrevention", "16.18.1")

    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.1.0-SNAPSHOT")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7.1") {
        exclude("org.bukkit", "bukkit")
    }

    compileOnly("me.clip", "placeholderapi", "2.11.3")
}

tasks {
    shadowJar {
        listOf(
            "de.tr7zw.changeme.nbtapi",
            "org.bstats"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    reobfJar {
        val file = File("$rootDir/jars")

        if (!file.exists()) file.mkdirs()

        outputJar.set(layout.buildDirectory.file("$file/${rootProject.name}-${rootProject.version}.jar"))
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to rootProject.group,
                "version" to rootProject.version,
                "description" to rootProject.description,
                "website" to "https://modrinth.com/plugin/${rootProject.name.lowercase()}"
            )
        }
    }
}