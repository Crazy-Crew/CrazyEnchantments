plugins {
    `config-paper`
}

project.group = "${rootProject.group}.paper"

repositories {
    maven("https://repo.md-5.net/content/repositories/snapshots/")

    maven("https://ci.ender.zone/plugin/repository/everything/")

    maven("https://repo.glaremasters.me/repository/towny/")

    maven("https://repo.bg-software.com/repository/api/")

    maven("https://maven.enginehub.org/repo/")

    maven("https://repo.nexomc.com/releases/")

    maven("https://repo.oraxen.com/releases/")
}

dependencies {
    implementation(libs.triumph.cmds)

    implementation(libs.fusion.paper)

    implementation(project(":core"))

    implementation(libs.metrics)

    compileOnly(libs.bundles.shared) {
        exclude("org.bukkit", "bukkit")
    }

    compileOnly(libs.informative.annotations)

    compileOnly(libs.griefprevention)

    compileOnly(libs.worldguard)
    compileOnly(libs.worldedit)

    compileOnly(libs.kingdoms)

    compileOnly(libs.factions) {
        exclude("org.kitteh")
        exclude("org.spongepowered")
        exclude("com.darkblade12")
    }

    compileOnly(libs.plotsquared)

    compileOnly(libs.skyblock)

    compileOnly(libs.paster)

    compileOnly(libs.towny)

    compileOnly(libs.lands)

    compileOnly(libs.mcmmo)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        listOf(
            "org.bstats"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    runPaper.folia.registerTask()

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")
        jvmArgs("-Dcom.mojang.eula.agree=true")

        downloadPlugins {
            modrinth("luckperms", "v5.5.0-bukkit")
        }

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }
}