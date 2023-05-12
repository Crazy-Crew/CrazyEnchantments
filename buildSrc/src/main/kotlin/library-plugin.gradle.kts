import java.awt.Color
import java.io.File
import task.WebhookExtension
import java.io.ByteArrayOutputStream

plugins {
    id("root-plugin")

    id("featherpatcher")
    id("com.modrinth.minotaur")
}

val releaseColor = Color(27, 217, 106)
val betaColor = Color(255, 163, 71)
val logColor = Color(37, 137, 204)

val isBeta = false
val color = if (isBeta) logColor else releaseColor
val repo = if (isBeta) "beta" else "releases"

val type = if (isBeta) "beta" else "release"
val otherType = if (isBeta) "Beta" else "Release"

val msg = "New version of ${rootProject.name} is ready! <@&929463452232192063>"

val downloads = """
    https://modrinth.com/plugin/${rootProject.name.lowercase()}/version/${rootProject.version}
""".trimIndent()

// The commit id for the "main" branch prior to merging a pull request.
val start = "771117"

// The commit id AFTER merging the pull request so the last commit before you release.
val end = "9deae3"

val commitLog = getGitHistory().joinToString(separator = "") { formatGitLog(it) }

val desc = """
  # Release ${rootProject.version}
  ### Changes         
  * Added missing ores for Telepathy.
  * Fixed the problems with `PlayerArmorChangeEvent which was caused by the event being called on damage.
  * Fixed the category level selections option not working.
  * Bring back right-clicking air which stopped working as it is a cancelled event.
           
  ### Commits
            
  <details>
          
  <summary>Other</summary>
           
  $commitLog
            
  </details>
                
  As always, report any bugs @ https://github.com/Crazy-Crew/${rootProject.name}/issues
""".trimIndent()

val versions = listOf(
    "1.19.4"
)

fun getGitHistory(): List<String> {
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
}

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
}

webhook {
    this.avatar("https://en.gravatar.com/avatar/${WebhookExtension.Gravatar().md5Hex("no-reply@ryderbelserion.com")}.jpeg")

    this.username("Ryder Belserion")

    this.content(msg)

    this.embeds {
        this.embed {
            this.color(logColor)

            this.title("READ ME")

            this.description("""
                TDL has fixed a few bugs however in doing so, Spigot compatibility is no more. You must use https://papermc.io from this point on.
                
                What this also means as according to spigot
                 » Resources must support the latest stable version of Spigot
                 
                We no longer support the latest stable version of Spigot so we cannot update any new versions on SpigotMC.
                
                Please read the full change log and take backups just to be safe before updating CrazyEnchantments.
            """.trimIndent())
        }

        this.embed {
            this.color(color)

            this.fields {
                this.field(
                    "Download: ",
                    downloads
                )

                this.field(
                    "API: ",
                    "https://repo.crazycrew.us/#/$repo/${rootProject.group.toString().replace(".", "/")}/${rootProject.name.lowercase()}-api/${rootProject.version}"
                )
            }

            this.author(
                "${rootProject.name} | Version ${rootProject.version}",
                downloads,
                "https://raw.githubusercontent.com/RyderBelserion/assets/main/crazycrew/png/${rootProject.name}Website.png"
            )
        }
    }

    this.url("DISCORD_WEBHOOK")
}

publishing {
    repositories {
        val repo = if (isBeta) "beta" else "releases"
        maven("https://repo.crazycrew.us/$repo") {
            name = "crazycrew"
            //credentials(PasswordCredentials::class)

            credentials {
                username = System.getenv("REPOSITORY_USERNAME")
                password = System.getenv("REPOSITORY_PASSWORD")
            }
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
