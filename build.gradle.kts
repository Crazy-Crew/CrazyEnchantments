import utils.convertList
import utils.updateMarkdown

plugins {
    id("modrinth-plugin")
    id("hangar-plugin")

    `java-plugin`
}

val git = feather.getBuilder()

// https://github.com/granny/Pl3xMap/blob/0547bbba3f0b7468db17983412e95bf59a1a0b7d/build.gradle.kts#L10
tasks {
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        subprojects {
            dependsOn(project.tasks.build)
        }

        archiveClassifier = ""

        val files = subprojects.filter { it.name != "common" && it.name != "api" }.mapNotNull {
            val file = it.tasks.jar.get().archiveFile

            if (file.isPresent) {
                zipTree(file.get().asFile)
            } else {
                null
            }
        }

        from(files) {
            exclude("META-INF/MANIFEST.MF")
        }

        doFirst {
            files.forEach { file ->
                file.matching { include("META-INF/MANIFEST.MF") }.files.forEach {
                    manifest.from(it)
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
    workingDirectory = rootProject.rootDir.toPath()

    val origin = git.getNewestCommit(rootProject.property("repository_owner").toString(),rootProject.name, git.utils.getRemoteCommitHash())

    discord {
        webhook {
            group(rootProject.name.lowercase())
            task("release-build")

            if (System.getenv("BUILD_WEBHOOK") != null) {
                post(System.getenv("BUILD_WEBHOOK"))
            }

            if (isRelease) {
                val user = origin?.user

                username(user?.name ?: "N/A")
                avatar(user?.getAvatar() ?: "N/A")
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
            task("jenkins-build")

            if (System.getenv("BUILD_WEBHOOK") != null) {
                post(System.getenv("BUILD_WEBHOOK"))
            }

            username(rootProject.property("mascot_name").toString())

            avatar(rootProject.property("mascot_avatar").toString())

            embeds {
                embed {
                    color(color)

                    title("${rootProject.name} (Build #${rootProject.ext.get("build_number")})")

                    fields {
                        field(
                            ":hammer: Changelog",
                            rootProject.ext.get("mc_changelog").toString().updateMarkdown()
                        )

                        field(
                            ":link: Build Link",
                            System.getenv("BUILD_URL") ?: "N/A",
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
                            "The developer is likely already aware, he is just getting drunk."
                        )
                    }
                }
            }
        }
    }
}