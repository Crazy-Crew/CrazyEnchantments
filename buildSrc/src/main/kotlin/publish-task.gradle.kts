import java.io.File
import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("root-plugin")

    id("io.papermc.hangar-publish-plugin")
    id("com.modrinth.minotaur")
}

val isSnapshot = rootProject.version.toString().contains("rc")
val type = if (isSnapshot) "beta" else "release"

val desc = """    
## Changes:
* Moved every single enchant/item over to using PDC for checks and to dictate if they are in fact one.
* Moved some things over to using adventure api.
* Removed the use of `Color:` and `BookColor:` as well as remove them from the default config.
* Added support for using hex colour codes throughout the enchantment's names.
* Roman numerals now go up to 3999.
* Bump soft dependencies' versions.
* Misc file cleanup.
* Re-worked how transmog scrolls are used.

## Messages:
* Added new `Player-Is-In-Creative-Mode` message with an option to disable using ''.
* Updated default `Hit-Enchantment-Max` message.

## Commands:
* `/ce updateenchants` -> Loops through the lore of old enchanted items and migrates it to the new system.
 * `crazyenchantments.updateenchants`

## API:
* N/A

## Bugs Fixed:
* Fixed other enchantments not working when the pickaxe has blast but you do not have permission to use blast.
* Fixed error caused by a soft dependency of CrazyEnchantments not loading due to an error on their side which causes CE to also not load.
* Fixed NPE that only appeared once adding Worldguard and Worldedit to the server.
* Reduced the chance of errors that you can get from changing the lore on an item or removing all of the lore while it has an enchantment on it.
* Fixed right-clicking a scroll in the air to get the usage.

**Submit any bugs via <https://github.com/Crazy-Crew/${rootProject.name}/issues>** (WOW IT'S EMPTY)

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
        channel.set("Beta")

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