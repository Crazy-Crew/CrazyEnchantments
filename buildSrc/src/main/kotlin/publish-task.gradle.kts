import java.io.File

plugins {
    id("root-plugin")

    id("featherpatcher")
    id("com.modrinth.minotaur")
}

val isSnapshot = rootProject.version.toString().contains("rc")
val type = if (isSnapshot) "beta" else "release"

// The commit id for the "main" branch prior to merging a pull request.
//val start = "363a0d"

// The commit id BEFORE merging the pull request so before "Merge pull request #30"
//val end = "52b8d7"

//val commitLog = getGitHistory().joinToString(separator = "") { formatGitLog(it) }

val desc = """
# This build is incompatible with all previous versions of CrazyEnchantments, It will break all enchants currently on items. You have been warned!
# Please join https://discord.gg/badbones-s-live-chat-182615261403283459 and get the beta tester role to report bugs!
    
## Changes:
 * Added 1.20 support.
 * Slight code enhancements.
 * A more random type of random.
 * Slowly start changing to Adventure API.
 ### Optimizations:
 * Reduce some checks that should not be needed.
 * Changed over to using JSON strings for enchantment storage in PDC.
 * Adds in PDC and changes the check to see if an item has enchantments over to PDC.
 * Opens up how you can customize lore a lot more in the future.
 * Should provide better performance.
 * Information for checks has been moved over to being under PDC, so lore no longer has an impact.
  * Adding enchants.
  * Removing enchants.
  * Checking levels.
  
## PDC should have a substantial increase for server performance when using our plugins. Enchants previously added to items will no longer work so you should not use this on an already existing server.

## API:
 * N/A

## Bugs:
* Fixes infinite effect length for 1.20.
* Removes an error that gets throw on crash that would only be thrown if the plugin doesn't load up properly before.
* Fixes NPE caused by /gkit reset with no other args.
* Fix for gkit no adding any crazyEnchantments. (Which might have been added in with the PDC changes)

 * Submit any bugs @ https://github.com/Crazy-Crew/${rootProject.name}/issues 
""".trimIndent()

val versions = listOf(
    "1.20"
)

/*fun getGitHistory(): List<String> {
    val output: String = ByteArrayOutputStream().use { outputStream ->
        project.exec {
            executable("git")
            args("log",  "$start..$end", "--format=format:%h %s")
            standardOutput = outputStream
        }

        outputStream.toString()
    }

    return output.split("\n")
}

fun formatGitLog(commitLog: String): String {
    val hash = commitLog.take(7)
    val message = commitLog.substring(8) // Get message after commit hash + space between
    return "[$hash](https://github.com/Crazy-Crew/${rootProject.name}/commit/$hash) $message<br>"
}*/

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