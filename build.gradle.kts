import task.WebhookExtension
import java.awt.Color

plugins {
    id("crazyenchantments.root-plugin")
}

val releaseUpdate = Color(27, 217, 106)
val betaUpdate = Color(255, 163, 71)

val isBeta = settings.versions.projectBeta.get().toBoolean()
val projectVersion = settings.versions.projectVersion.get()
val projectName = settings.versions.projectName.get()
val projectExt = settings.versions.projectExtension.get()

val finalVersion = if (isBeta) "$projectVersion+beta" else projectVersion

val color = if (isBeta) betaUpdate else releaseUpdate
val repo = if (isBeta) "beta" else "release"

webhook {
    this.avatar("https://en.gravatar.com/avatar/${WebhookExtension.Gravatar().md5Hex("no-reply@ryderbelserion.com")}.jpeg")

    this.username("Ryder Belserion")

    this.content("New version of $projectName is ready! <@&929463452232192063>")

    this.embeds {
        this.embed {
            this.color(color)

            this.fields {
                this.field(
                    "Version $finalVersion",
                    "Download Link: https://modrinth.com/$projectExt/${projectName.lowercase()}/version/$finalVersion"
                )

                this.field(
                    "API Update",
                    "Version $finalVersion has been pushed to https://repo.crazycrew.us/#/$repo"
                )
            }

            this.author(
                projectName,
                "https://modrinth.com/$projectExt/${projectName.lowercase()}/versions",
                "https://cdn-raw.modrinth.com/data/krxPuhWb/1c347285ccaef4e5214787acc5dcd2fbe9719875.png"
            )
        }
    }
}