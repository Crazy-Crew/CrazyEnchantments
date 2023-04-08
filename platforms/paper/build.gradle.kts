@Suppress("DSL_SCOPE_VIOLATION")

plugins {
    id("crazyenchantments.paper-plugin")

    alias(settings.plugins.minotaur)
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

val type = if (beta) "beta" else "release"

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

    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set(rootProject.name.lowercase())

        versionName.set("${rootProject.name} ${rootProject.version}")
        versionNumber.set(rootProject.version.toString())

        versionType.set(type)

        uploadFile.set(layout.buildDirectory.file("$file/${rootProject.name}-Paper-${rootProject.version}.jar"))

        autoAddDependsOn.set(true)

        gameVersions.addAll(
            listOf(
                "1.19",
                "1.19.1",
                "1.19.2",
                "1.19.3",
                "1.19.4"
            )
        )

        loaders.addAll(listOf("paper", "purpur"))

        //<h3>The first release for CrazyEnchantments on Modrinth! 🎉🎉🎉🎉🎉<h3><br> If we want a header.
        changelog.set(
            """
                <h4>Changes:</h4>
                 <p>Added 1.19.4 support</p>
                 <p>Removed 1.18.2 and below support</p>
                 <p>Added a permission requirement to the Blast Enchant: **crazyenchantments.blast.use**</p>
                <h4>Under the hood changes</h4>
                 <p>Simplified build script</p>
                <h4>Bug Fixes:</h4>
                 <p>Fix the NPE that was caused by Entity#remove being called on players. (#699)</p>
                 <p>Fixes the problem that occurs when you have worldguard and worldedit on the server while using specific enchantments which was caused by PluginSupport in Methods being null. (#699)</p>
            """.trimIndent()
        )
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