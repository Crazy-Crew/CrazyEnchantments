plugins {
    alias(libs.plugins.minotaur)
    alias(libs.plugins.feather)
    alias(libs.plugins.hangar)

    `config-java`
}

val git = feather.getGit()

val commitHash: String = git.getCurrentCommitHash().subSequence(0, 7).toString()
val isSnapshot: Boolean = git.getCurrentBranch() == "dev"
val content: String = if (isSnapshot) "[$commitHash](https://github.com/Crazy-Crew/${rootProject.name}/commit/$commitHash) ${git.getCurrentCommit()}" else rootProject.file("changelog.md").readText(Charsets.UTF_8)
val minecraft = libs.versions.minecraft.get()
val versions = listOf(minecraft)

rootProject.version = if (isSnapshot) "$minecraft-$commitHash" else libs.versions.crazyenchantments.get()
rootProject.description = "Adds over 80 unique enchantments to your server and more!"
rootProject.group = "com.badbones69.crazyenchantments"

feather {
    rootDirectory = rootProject.rootDir.toPath()

    val data = git.getGithubCommit("Crazy-Crew/${rootProject.name}")

    val user = data.user

    discord {
        webhook {
            group(rootProject.name.lowercase())
            task("dev-build")

            if (System.getenv("BUILD_WEBHOOK") != null) {
                post(System.getenv("BUILD_WEBHOOK"))
            }

            username(user.getName())

            avatar(user.avatar)

            embeds {
                embed {
                    color("#ffa347")

                    title("A new dev version of ${rootProject.name} is ready!")

                    fields {
                        field(
                            "Version ${rootProject.version}",
                            listOf(
                                "*Click below to download!*",
                                "<:modrinth:1115307870473420800> [Modrinth](https://modrinth.com/plugin/${rootProject.name.lowercase()}/version/${rootProject.version})",
                                "<:hangar:1139326635313733652> [Hangar](https://hangar.papermc.io/CrazyCrew/${rootProject.name.lowercase()}/versions/${rootProject.version})"
                            ).convertList()
                        )

                        field(
                            ":bug: Report Bugs",
                            "https://github.com/Crazy-Crew/${rootProject.name}/issues"
                        )

                        field(
                            ":hammer: Changelog",
                            content
                        )
                    }
                }
            }
        }

        webhook {
            group(rootProject.name.lowercase())
            task("release-build")

            if (System.getenv("BUILD_WEBHOOK") != null) {
                post(System.getenv("BUILD_WEBHOOK"))
            }

            username(user.getName())

            avatar(user.avatar)

            content("<@&929463452232192063>")

            embeds {
                embed {
                    color("#1bd96a")

                    title("A new release version of ${rootProject.name} is ready!")

                    fields {
                        field(
                            "Version ${rootProject.version}",
                            listOf(
                                "*Click below to download!*",
                                "<:modrinth:1115307870473420800> [Modrinth](https://modrinth.com/plugin/${rootProject.name.lowercase()}/version/${rootProject.version})",
                                "<:hangar:1139326635313733652> [Hangar](https://hangar.papermc.io/CrazyCrew/${rootProject.name.lowercase()}/versions/${rootProject.version})"
                            ).convertList()
                        )

                        field(
                            ":bug: Report Bugs",
                            "https://github.com/Crazy-Crew/${rootProject.name}/issues"
                        )

                        field(
                            ":hammer: Changelog",
                            "[Click](https://modrinth.com/plugin/${rootProject.name.lowercase()}/version/${rootProject.version})"
                        )
                    }
                }
            }
        }
    }
}

fun List<String>.convertList(): String {
    val builder = StringBuilder(size)

    forEach {
        builder.append(it).append("\n")
    }

    return builder.toString()
}

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

modrinth {
    token = System.getenv("MODRINTH_TOKEN")

    projectId = rootProject.name

    versionName = "${rootProject.name} ${rootProject.version}"
    versionNumber = "${rootProject.version}"
    versionType = if (isSnapshot) "beta" else "release"

    changelog = content

    gameVersions.addAll(versions)

    uploadFile = tasks.jar.get().archiveFile.get()

    loaders.addAll(listOf("paper", "folia", "purpur"))

    syncBodyFrom = rootProject.file("description.md").readText(Charsets.UTF_8)

    autoAddDependsOn = false
    detectLoaders = false

    dependencies {
        optional.project("GriefPrevention")

        optional.project("WorldGuard")
        optional.project("WorldEdit")

        optional.project("Towny")
    }
}

hangarPublish {
    publications.register("plugin") {
        apiKey.set(System.getenv("HANGAR_KEY"))

        id.set(rootProject.name)

        version.set("${rootProject.version}")

        channel.set(if (isSnapshot) "Beta" else "Release")

        changelog.set(content)

        platforms {
            paper {
                jar = tasks.jar.flatMap { it.archiveFile }

                platformVersions.set(versions)

                dependencies {
                    hangar("GriefPrevention") {
                        required = false
                    }

                    hangar("PlaceholderAPI") {
                        required = false
                    }

                    hangar("WorldEdit") {
                        required = false
                    }

                    hangar("Towny") {
                        required = false
                    }
                }
            }
        }
    }
}