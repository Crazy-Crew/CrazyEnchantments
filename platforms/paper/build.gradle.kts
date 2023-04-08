@Suppress("DSL_SCOPE_VIOLATION")

plugins {
    id("crazyenchantments.paper-plugin")

    alias(settings.plugins.run.paper)
}

dependencies {
    // Nbt Api / Bstats
    implementation(libs.nbt.api)
    implementation(libs.bstats.bukkit)

    // PaperMC
    compileOnly(libs.papermc)

    // Custom Items
    compileOnly(libs.oraxen)

    // Anticheats
    compileOnly(libs.nocheatplus)
    compileOnly(libs.spartan)

    // Claims
    compileOnly(libs.plotsquared)
    compileOnly(libs.plotsquared.annotations)
    compileOnly(libs.plotsquared.paster)

    compileOnly(libs.superiorskyblock)

    compileOnly(libs.massivecraft) {
        exclude("org.kitteh")
        exclude("org.spongepowered")
        exclude("com.darkblade12")
    }

    compileOnly(libs.towny)

    // Stackers/Spawners
    compileOnly(libs.wildstacker)

    compileOnly(libs.silkspawners) {
        exclude("org.bukkit", "bukkit")
        exclude("org.spigot", "spigot")
        exclude("org.spigotmc", "spigot")
        exclude("com.destroystokyo.paper", "paper")
        exclude("com.sk89q", "worldguard")
        exclude("com.sk89q", "worldedit")
        exclude("com.massivecraft.massivecore", "MassiveCore")
        exclude("com.massivecraft.factions", "Factions")
        exclude("net.gravitydevelopment.updater", "updater")
        exclude("com.intellectualsites", "Pipeline")
    }

    // Protection
    compileOnly(libs.worldedit.api)

    compileOnly(libs.griefprevention)

    compileOnly(libs.worldguard.api)

    // Misc
    compileOnly(libs.placeholder.api)
    compileOnly(libs.vault.api)
}

val github = settings.versions.github.get()
val extension = settings.versions.extension.get()

val beta = settings.versions.beta.get().toBoolean()

tasks {
    shadowJar {
        listOf(
            "de.tr7zw.changeme.nbtapi",
            "org.bstats"
        ).forEach { pack -> relocate(pack, "${rootProject.group}.$pack") }
    }

    runServer {
        minecraftVersion("1.19.4")
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to rootProject.group,
                "version" to rootProject.version,
                "description" to rootProject.description,
                "website" to "https://modrinth.com/$extension/${rootProject.name.lowercase()}"
            )
        }
    }
}

publishing {
    repositories {
        val repo = if (beta) "beta" else "releases"
        maven("https://repo.crazycrew.us/$repo") {
            name = "crazycrew"
            credentials(PasswordCredentials::class)
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = "${rootProject.name.lowercase()}-api"
            version = rootProject.version.toString()

            from(components["java"])
        }
    }
}