import java.io.File
import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("root-plugin")

    id("io.papermc.hangar-publish-plugin")
    id("com.modrinth.minotaur")
}

val isSnapshot = rootProject.version.toString().contains("rc")
val type = if (isSnapshot) "beta" else "release"
val paperType = if (isSnapshot) "Beta" else "Release"

val desc = """    
## Changes:
* N/A

## Messages:
* N/A

## Commands:
* N/A

## API:
* N/A

## Bugs Fixed:
* Fixed telepathy setting an items lore, even when the item has no lore to be shown.

**Submit any bugs via <https://github.com/Crazy-Crew/${rootProject.name}/issues>** (WOW IT'S EMPTY)
**Submit any suggestions via <https://github.com/Crazy-Crew/${rootProject.name}/discussions>**

""".trimIndent()

val versions = listOf(
    "1.20",
    "1.20.1"
)

val javaComponent: SoftwareComponent = components["java"]

tasks {
    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set(rootProject.name.lowercase())

        versionName.set("${rootProject.name} ${rootProject.version}")
        versionNumber.set(rootProject.version.toString())

        versionType.set(type)

        val file = File("$rootDir/jars")
        if (!file.exists()) file.mkdirs()

        uploadFile.set(layout.buildDirectory.file("$file/${rootProject.name}-${rootProject.version}.jar"))

        autoAddDependsOn.set(true)

        gameVersions.addAll(versions)

        loaders.addAll(listOf("paper", "purpur"))

        changelog.set(desc)
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = "${rootProject.name.lowercase()}-api"
                version = rootProject.version.toString()

                from(javaComponent)
            }
        }

        repositories {
            maven {
                credentials {
                    this.username = System.getenv("gradle_username")
                    this.password = System.getenv("gradle_password")
                }

                if (isSnapshot) {
                    url = uri("https://repo.crazycrew.us/snapshots/")
                    return@maven
                }

                url = uri("https://repo.crazycrew.us/releases/")
            }
        }
    }
}

hangarPublish {
    publications.register("plugin") {
        version.set(rootProject.version.toString())

        namespace("CrazyCrew", "CrazyEnchantments")
        channel.set(paperType)

        apiKey.set(System.getenv("hangar_key"))

        changelog.set(desc)

        platforms {
            register(Platforms.PAPER) {
                val file = File("$rootDir/jars")
                if (!file.exists()) file.mkdirs()

                jar.set(layout.buildDirectory.file("$file/${rootProject.name}-${rootProject.version}.jar"))
                platformVersions.set(versions)
            }
        }
    }
}