plugins {
    `config-paper`
}

project.group = "${rootProject.group}.paper"

repositories {
    maven("https://repo.md-5.net/content/repositories/snapshots")

    maven("https://ci.ender.zone/plugin/repository/everything")

    maven("https://repo.glaremasters.me/repository/towny")

    maven("https://repo.bg-software.com/repository/api")

    maven("https://maven.enginehub.org/repo")

    maven("https://repo.oraxen.com/releases")
}

dependencies {
    compileOnly(libs.informative.annotations)

    compileOnly(libs.vault) {
        exclude("org.bukkit", "bukkit")
    }

    compileOnly(libs.griefprevention)

    compileOnly(libs.oraxen)

    compileOnly(libs.worldguard)
    compileOnly(libs.worldedit)

    compileOnly(libs.kingdoms)

    compileOnly(libs.factions) {
        exclude("org.kitteh")
        exclude("org.spongepowered")
        exclude("com.darkblade12")
    }

    compileOnly(libs.towny)

    compileOnly(libs.lands)

    compileOnly(libs.paster)

    compileOnly(libs.skyblock)

    compileOnly(libs.plotsquared)

    compileOnly(libs.mcmmo)
}

tasks {
    configurations.all { //todo() FIX ME later, fucking forced dependencies, give me a fucking break
        resolutionStrategy {
            force("org.apache.logging.log4j:log4j-bom:2.24.1")
            force("com.google.guava:guava:33.3.1-jre")
            force("com.google.code.gson:gson:2.11.0")
            force("it.unimi.dsi:fastutil:8.5.15")
        }
    }

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }
}