import com.lordcodes.turtle.shellRun
import task.WebhookExtension
import java.awt.Color

@Suppress("DSL_SCOPE_VIOLATION")

plugins {
    id("crazyenchantments.root-plugin")

    alias(settings.plugins.minotaur)

    id("featherpatcher") version "0.0.0.2"
}

val releaseUpdate = Color(27, 217, 106)
val betaUpdate = Color(255, 163, 71)
val changeLogs = Color(37, 137, 204)

val beta = settings.versions.beta.get().toBoolean()
val extension = settings.versions.extension.get()

val color = if (beta) betaUpdate else releaseUpdate
val repo = if (beta) "beta" else "releases"

val url = if (beta) "https://ci.crazycrew.us/job/${rootProject.name}/" else "https://modrinth.com/$extension/${rootProject.name.lowercase()}/versions"
val download = if (beta) "https://ci.crazycrew.us/job/${rootProject.name}/" else "https://modrinth.com/$extension/${rootProject.name.lowercase()}/version/${rootProject.version}"

val desc = if (beta) """
    Changes:
    » N/A
""".trimIndent() else "https://modrinth.com/$extension/${rootProject.name.lowercase()}/version/${rootProject.version}"

val msg = if (beta) "New version of ${rootProject.name} is ready!" else "New version of ${rootProject.name} is ready!"

val hash = shellRun("git", listOf("rev-parse", "--short", "HEAD"))

rootProject.version = if (beta) hash else "1.9.8.2"

tasks.named("build") {
    dependsOn("publish", "webhook", "modrinth")
}

val type = if (beta) "beta" else "release"

tasks {
    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set(rootProject.name.lowercase())

        versionName.set("${rootProject.name} ${rootProject.version}")
        versionNumber.set(rootProject.version.toString())

        versionType.set(type)

        uploadFile.set(layout.buildDirectory.file("$file/${rootProject.name}-Paper-${rootProject.version}.jar"))

        autoAddDependsOn.set(true)

        gameVersions.addAll(
            listOf(
                "1.19",
                "1.19.1",
                "1.19.2",
                "1.19.3",
                "1.19.4"
            )
        )

        loaders.addAll(listOf("paper", "purpur"))

        //<h3>The first release for CrazyEnchantments on Modrinth! 🎉🎉🎉🎉🎉<h3><br> If we want a header.
        changelog.set(
            """
            <h4>Changes:</h4>
             <p>Added 1.19.4 support</p>
             <p>Removed 1.18.2 and below support</p>
             <p>Added a permission requirement to the Blast Enchant: **crazyenchantments.blast.use**</p>
            <h4>Under the hood changes</h4>
             <p>Simplified build script</p>
            <h4>Bug Fixes:</h4>
             <p>Fix the NPE that was caused by Entity#remove being called on players. (#699)</p>
             <p>Fixes the problem that occurs when you have worldguard and worldedit on the server while using specific enchantments which was caused by PluginSupport in Methods being null. (#699)</p>
        """.trimIndent()
        )
    }
}

webhook {
    this.avatar("https://en.gravatar.com/avatar/${WebhookExtension.Gravatar().md5Hex("no-reply@ryderbelserion.com")}.jpeg")

    this.username("Ryder Belserion")

    this.content(msg)

    this.embeds {
        this.embed {
            this.color(color)

            this.fields {
                this.field(
                    "Download: ",
                    url
                )

                this.field(
                    "API: ",
                    "https://repo.crazycrew.us/#/$repo/${rootProject.group.toString().replace(".", "/")}/${rootProject.name.lowercase()}-api/${rootProject.version}"
                )
            }

            this.author(
                "${rootProject.name} | Version ${rootProject.version}",
                url,
                "https://raw.githubusercontent.com/RyderBelserion/assets/main/crazycrew/png/${rootProject.name}Website.png"
            )
        }

        this.embed {
            this.color(changeLogs)

            this.title("What changed?")

            this.description(desc)
        }
    }

    this.url("DISCORD_WEBHOOK")
}
