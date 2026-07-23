plugins {
    `paper-plugin`
}

project.group = "${rootProject.group}.paper"

repositories {
    exclusiveContent {
        forRepository {
            maven("https://dependency.download/releases/")
        }

        filter {
            includeGroup("dev.kitteh")
        }
    }

    exclusiveContent {
        forRepository {
            maven("https://repo.essentialsx.net/releases/")
        }

        filter {
            includeGroup("net.essentialsx")
        }
    }

    exclusiveContent {
        forRepository {
            maven("https://repo.glaremasters.me/repository/towny/")
        }

        filter {
            includeGroup("com.palmergames.bukkit.towny")
        }
    }

    maven("https://repo.md-5.net/content/repositories/snapshots/")

    maven("https://ci.ender.zone/plugin/repository/everything/")

    maven("https://repo.bg-software.com/repository/api/")

    maven("https://repo.incredibleplugins.com/releases/")

    maven("https://repo.fancyinnovations.com/releases/")

    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://repo.hibiscusmc.com/releases/")

    maven("https://repo.momirealms.net/releases/")

    maven("https://maven.enginehub.org/repo/")

    maven("https://repo.nexomc.com/releases/")

    maven("https://repo.oraxen.com/releases/")

    maven("https://maven.devs.beer/")
}

dependencies {
    implementation(libs.triumph.cmds)
    implementation(libs.fusion.paper)

    implementation(libs.metrics)

    compileOnly(libs.vault) {
        exclude("org.bukkit", "bukkit")
    }

    compileOnly(libs.bundles.protection)
    compileOnly(libs.bundles.factions)
    compileOnly(libs.bundles.skyblock)
    compileOnly(libs.bundles.parties)
    compileOnly(libs.bundles.vanish)
    compileOnly(libs.bundles.claims)
    compileOnly(libs.bundles.plot)

    compileOnly(libs.bundles.shared)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        listOf(
            "com.ryderbelserion.fusion",
            "com.google.errorprone",
            "io.leangen.geantyref",
            "dev.triumphteam.cmd",
            "org.spongepowered",
            "com.google.gson",
            "org.jspecify",
            "org.bstats",
            "org.yaml",
            "ch.jalu"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    configurations.all { //todo() FIX ME later, fucking forced dependencies, give me a fucking break
        resolutionStrategy {
            force("org.apache.logging.log4j:log4j-bom:2.24.1")
            force("com.google.guava:guava:33.3.1-jre")
            force("com.google.code.gson:gson:2.11.0")
            force("it.unimi.dsi:fastutil:8.5.15")
        }
    }

    runPaper.folia.registerTask()

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")
        jvmArgs("-Dcom.mojang.eula.agree=true")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }
}