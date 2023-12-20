import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    alias(libs.plugins.paperweight)
    alias(libs.plugins.shadowjar)

    alias(libs.plugins.modrinth)

    alias(libs.plugins.runpaper)

    alias(libs.plugins.hangar)

    `maven-publish`
}

project.group = "${rootProject.group}.paper"

base {
    archivesName.set(rootProject.name)
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.dustplanet.de/artifactory/libs-release-local/")

    maven("https://repo.md-5.net/content/repositories/snapshots/")

    maven("https://ci.ender.zone/plugin/repository/everything/")

    maven("https://repo.codemc.org/repository/maven-public/")

    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.glaremasters.me/repository/towny/")

    maven("https://repo.bg-software.com/repository/api/")

    maven("https://repo.crazycrew.us/third-party/")

    maven("https://maven.enginehub.org/repo/")

    flatDir { dirs("libs") }
}

val mcVersion = rootProject.properties["minecraftVersion"] as String

dependencies {
    implementation("de.tr7zw", "item-nbt-api", "2.12.0")

    implementation("org.bstats", "bstats-bukkit", "3.0.2")

    compileOnly("com.intellectualsites.informative-annotations", "informative-annotations", "1.3")

    compileOnly("com.sk89q.worldguard", "worldguard-bukkit", "7.1.0-SNAPSHOT")

    compileOnly("com.github.TechFortress", "GriefPrevention", "16.18.1")

    compileOnly("com.sk89q.worldedit", "worldedit-bukkit", "7.2.15")

    compileOnly("com.intellectualsites.paster", "Paster", "1.1.5")

    compileOnly("com.bgsoftware", "SuperiorSkyblockAPI", "2023.2")

    compileOnly("com.massivecraft", "Factions", "1.6.9.5-U0.6.31") {
        exclude("org.kitteh")
        exclude("org.spongepowered")
        exclude("com.darkblade12")
    }

    compileOnly("com.palmergames.bukkit.towny", "towny", "0.99.5.0")

    compileOnly("fr.neatmonster", "nocheatplus", "3.16.1-SNAPSHOT")

    compileOnly("com.plotsquared", "PlotSquared-Core", "6.11.1")

    compileOnly("com.bgsoftware", "WildStackerAPI", "2023.2")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7.1") {
        exclude("org.bukkit", "bukkit")
    }

    compileOnly("de.dustplanet", "silkspawners", "7.5.0") {
        exclude("*", "*")
    }

    compileOnly("me.clip", "placeholderapi", "2.11.4")

    compileOnly("com.github.oraxen", "oraxen", "1.160.0") {
        exclude("*", "*")
    }

    compileOnly(fileTree("libs").include("*.jar"))

    compileOnly("com.gmail.nossr50.mcMMO", "mcMMO", "2.1.225")

    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:$mcVersion-R0.1-SNAPSHOT")

}

val isBeta: Boolean get() = rootProject.extra["isBeta"]?.toString()?.toBoolean() ?: false
val type = if (isBeta) "Beta" else "Release"

val description = """
## Changes:
 * N/A

## Other:
 * [Feature Requests](https://github.com/Crazy-Crew/${rootProject.name}/issues)
 * [Bug Reports](https://github.com/Crazy-Crew/${rootProject.name}/issues)
"""

val file = project.layout.buildDirectory.file("libs/${rootProject.name}-${rootProject.version}.jar").get().asFile

val component: SoftwareComponent = components["java"]

tasks {
    // Publish to hangar.papermc.io.
    hangarPublish {
        publications.register("plugin") {
            version.set("$rootProject.version")

            id.set(rootProject.name)

            channel.set(type)

            changelog.set(description)

            apiKey.set(System.getenv("hangar_key"))

            platforms {
                register(Platforms.PAPER) {
                    jar.set(file)

                    platformVersions.set(listOf(mcVersion))
                }
            }
        }
    }

    // Publish to modrinth.
    modrinth {
        autoAddDependsOn.set(false)

        token.set(System.getenv("modrinth_token"))

        projectId.set(rootProject.name.lowercase())

        versionName.set("${rootProject.name} ${rootProject.version}")

        versionNumber.set("${rootProject.version}")

        versionType.set(type.lowercase())

        uploadFile.set(file)

        gameVersions.add(mcVersion)

        changelog.set(description)

        loaders.addAll("paper", "purpur")
    }

    // Runs a test server.
    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        minecraftVersion(mcVersion)
    }

    // Assembles the plugin.
    assemble {
        dependsOn(reobfJar)
    }

    publishing {
        repositories {
            maven {
                url = uri("https://repo.crazycrew.us/releases/")

                credentials {
                    this.username = System.getenv("GRADLE_USERNAME")
                    this.password = System.getenv("GRADLE_PASSWORD")
                }
            }
        }

        publications{
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = "${rootProject.name.lowercase()}-${project.name.lowercase()}-api"
                version = rootProject.version.toString()

                from(component)
            }
        }
    }

    shadowJar {
        archiveClassifier.set("")

        exclude("META-INF/**")

        listOf(
            "de.tr7zw.changeme.nbtapi",
            "org.bstats"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        val properties = hashMapOf(
                "name" to rootProject.name,
                "version" to rootProject.version,
                "group" to project.group,
                "description" to rootProject.description,
                "apiVersion" to rootProject.properties["apiVersion"],
                "authors" to rootProject.properties["authors"],
                "website" to rootProject.properties["website"]
        )

        inputs.properties(properties)

        filesMatching("plugin.yml") {
            expand(properties)
        }
    }
}