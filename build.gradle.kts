import utils.convertList
import utils.updateMarkdown

plugins {
    id("modrinth-plugin")
    id("hangar-plugin")

    `java-plugin`
}

val git = feather.getGit()

allprojects {
    apply(plugin = "java-library")
}

tasks {
    withType<Jar> {
        subprojects {
            dependsOn(project.tasks.build)
        }

        // get subproject's built jars
        val jars = subprojects.map { zipTree(it.tasks.jar.get().archiveFile.get().asFile) }

        // merge them into main jar (except their manifests)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from(jars) {
            exclude("META-INF/MANIFEST.MF")
        }

        // put behind an action because files don't exist at configuration time
        doFirst {
            // merge all subproject's manifests into main manifest
            jars.forEach { jar ->
                jar.matching { include("META-INF/MANIFEST.MF") }
                    .files.forEach { file ->
                        manifest.from(file)
                    }
            }
        }
    }
}

val releaseType = rootProject.ext.get("release_type").toString()
val color = rootProject.property("${releaseType.lowercase()}_color").toString()
val isRelease = releaseType.equals("release", true)
val isAlpha = releaseType.equals("alpha", true)

feather {
    rootDirectory = rootProject.rootDir.toPath()

    val data = git.getGithubCommit("${rootProject.property("repository_owner")}/${rootProject.name}")

    val user = data.user

    discord {
        webhook {
            group(rootProject.name.lowercase())
            task("release-build")

            if (System.getenv("BUILD_WEBHOOK") != null) {
                post(System.getenv("BUILD_WEBHOOK"))
            }

            if (isRelease) {
                username(user.getName())

                avatar(user.avatar)
            } else {
                username(rootProject.property("author_name").toString())

                avatar(rootProject.property("author_avatar").toString())
            }

            embeds {
                embed {
                    color(color)

                    title("A new $releaseType version of ${rootProject.name} is ready!")

                    if (isRelease) {
                        content("<@&${rootProject.property("discord_role_id").toString()}>")
                    }

                    fields {
                        field(
                            "Version ${rootProject.version}",
                            listOf(
                                "*Click below to download!*",
                                "<:modrinth:1115307870473420800> [Modrinth](https://modrinth.com/plugin/${rootProject.name.lowercase()}/version/${rootProject.version})",
                                "<:hangar:1139326635313733652> [Hangar](https://hangar.papermc.io/${rootProject.property("repository_owner").toString().replace("-", "")}/${rootProject.name.lowercase()}/versions/${rootProject.version})"
                            ).convertList()
                        )

                        field(
                            ":bug: Report Bugs",
                            "https://github.com/${rootProject.property("repository_owner")}/${rootProject.name}/issues"
                        )

                        field(
                            ":hammer: Changelog",
                            rootProject.ext.get("mc_changelog").toString().updateMarkdown()
                        )
                    }
                }
            }
        }

        webhook {
            group(rootProject.name.lowercase())
            task("failed-build")

            if (System.getenv("BUILD_WEBHOOK") != null) {
                post(System.getenv("BUILD_WEBHOOK"))
            }

            username(rootProject.property("mascot_name").toString())

            avatar(rootProject.property("mascot_avatar").toString())

            embeds {
                embed {
                    color(rootProject.property("failed_color").toString())

                    title("Oh no! It failed!")

                    thumbnail("https://raw.githubusercontent.com/ryderbelserion/Branding/refs/heads/main/booze.jpg")

                    fields {
                        field(
                            "The build versioned ${rootProject.version} for project ${rootProject.name} failed.",
                            "The developer is likely already aware, he is just getting drunk.",
                            inline = true
                        )
                    }
                }
            }
        }
    }
}