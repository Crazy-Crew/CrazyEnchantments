plugins {
    alias(libs.plugins.minotaur)
    alias(libs.plugins.hangar)
}

val content: String = rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8)

val isBeta = false
val pluginName = rootProject.name
val mcVersion = libs.versions.minecraft.get()

tasks {
    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))

        projectId.set(rootProject.name)

        versionType.set(if (isBeta) "beta" else "release")

        versionName.set("$pluginName ${rootProject.version}")
        versionNumber.set(rootProject.version as String)

        changelog.set(content)

        uploadFile.set(rootProject.projectDir.resolve("jars/$pluginName-${rootProject.version}.jar"))

        gameVersions.set(listOf(mcVersion))

        loaders.addAll(listOf("purpur", "paper", "folia"))

        syncBodyFrom.set(rootProject.file("README.md").readText(Charsets.UTF_8))

        autoAddDependsOn.set(false)
        detectLoaders.set(false)
    }

    hangarPublish {
        publications.register("plugin") {
            apiKey.set(System.getenv("HANGAR_KEY"))

            id.set(pluginName)

            version.set(rootProject.version as String)

            channel.set(if (isBeta) "Beta" else "Release")

            changelog.set(content)

            platforms {
                paper {
                    jar.set(rootProject.projectDir.resolve("jars/$pluginName-${rootProject.version}.jar"))

                    platformVersions.set(listOf(mcVersion))

                    dependencies {
                        hangar("PlaceholderAPI") {
                            required = false
                        }
                    }
                }
            }
        }
    }
}