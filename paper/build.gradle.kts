plugins {
    `paper-plugin`
}

project.group = "${rootProject.group}.paper"

repositories {
    exclusiveContent {
        forRepository {
            maven("https://dependency.download/releases")
        }

        filter {
            includeGroup("dev.kitteh")
        }
    }

    exclusiveContent {
        forRepository {
            maven("https://repo.essentialsx.net/releases")
        }

        filter {
            includeGroup("net.essentialsx")
        }
    }

    maven("https://repo.md-5.net/content/repositories/snapshots/")

    maven("https://ci.ender.zone/plugin/repository/everything/")

    maven("https://repo.glaremasters.me/repository/towny/")

    maven("https://repo.bg-software.com/repository/api/")

    maven("https://repo.incredibleplugins.com/releases/")

    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://maven.enginehub.org/repo/")
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
            "org.bstats"
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

        downloadPlugins {
            modrinth("luckperms", "v5.5.0-bukkit")
        }

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }
}