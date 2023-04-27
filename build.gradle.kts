plugins {
    id("paper-plugin")
    id("library-plugin")
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

tasks {
    shadowJar {
        listOf(
            "de.tr7zw.changeme.nbtapi",
            "org.bstats"
        ).forEach { pack -> relocate(pack, "${rootProject.group}.$pack") }
    }

    reobfJar {
        val file = File("$rootDir/jars")

        if (!file.exists()) file.mkdirs()

        outputJar.set(layout.buildDirectory.file("$file/${rootProject.name}-${rootProject.version}.jar"))
    }
}