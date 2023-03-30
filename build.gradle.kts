import task.WebhookExtension
import java.awt.Color

plugins {
    id("crazyenchantments.root-plugin")
}

val releaseUpdate = Color(27, 217, 106)
val betaUpdate = Color(255, 163, 71)

val beta = settings.versions.beta.get().toBoolean()
val extension = settings.versions.extension.get()

val color = if (beta) betaUpdate else releaseUpdate
val repo = if (beta) "beta" else "releases"

val url = if (beta) "https://ci.crazycrew.us/job/${rootProject.name}/" else "https://modrinth.com/$extension/${rootProject.name.lowercase()}/versions"
val download = if (beta) "https://ci.crazycrew.us/job/${rootProject.name}/" else "https://modrinth.com/$extension/${rootProject.name.lowercase()}/version/${rootProject.version}"
val msg = if (beta) "New version of ${rootProject.name} is ready!" else "New version of ${rootProject.name} is ready! <@&1029922295210311681>"

webhook {
    this.avatar("https://en.gravatar.com/avatar/${WebhookExtension.Gravatar().md5Hex("no-reply@ryderbelserion.com")}.jpeg")

    this.username("Ryder Belserion")

    this.content(msg)

    this.embeds {
        this.embed {
            this.color(color)

            this.fields {
                this.field(
                    "Version ${rootProject.version}",
                    "Download Link: $url"
                )

                this.field(
                    "API Update",
                    "Version ${rootProject.version} has been pushed to https://repo.crazycrew.us/#/$repo"
                )
            }

            this.author(
                rootProject.name,
                url,
                "https://cdn-raw.modrinth.com/data/EMORKQjj/1cf7fffa9bf92d1bc292983dc320984cc764b51e.png"
            )
        }
    }
}