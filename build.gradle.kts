import java.awt.Color

plugins {
    id("crazyenchantments.root-plugin")
}

val legacyUpdate = Color(255, 73, 110)
val releaseUpdate = Color(27, 217, 106)
val betaUpdate = Color(255, 163, 71)

releaseBuild {
    val pluginVersion = getProjectVersion()
    val pluginName = getProjectName()

    val versionColor = if (isBeta()) betaUpdate else releaseUpdate

    val pageExtension = getExtension()

    webhook {
        this.avatar("https://cdn.discordapp.com/avatars/209853986646261762/eefe3c03882cbb885d98107857d0b022.png")

        this.username("Ryder Belserion")

        this.content("New version of $pluginName is ready! <@&929463452232192063>")

        this.embeds {
            this.embed {
                this.color(versionColor)

                this.fields {
                    this.field(
                        "Version $pluginVersion",
                        "Download Link: https://modrinth.com/$pageExtension/${pluginName.toLowerCase()}/version/$pluginVersion"
                    )

                    if (isBeta()) {
                        this.field(
                            "API Update",
                            "Version $pluginVersion has been pushed to https://repo.crazycrew.us/#/beta/"
                        )
                    }

                    if (!isBeta()) this.field(
                        "API Update",
                        "Version $pluginVersion has been pushed to https://repo.crazycrew.us/#/releases/"
                    )
                }

                this.author(
                    pluginName,
                    "https://modrinth.com/$pageExtension/${pluginName.toLowerCase()}/versions",
                    "https://cdn-raw.modrinth.com/data/krxPuhWb/1c347285ccaef4e5214787acc5dcd2fbe9719875.png"
                )
            }
        }
    }
}