plugins {
    id("com.github.johnrengelman.shadow")

    id("com.modrinth.minotaur")

    id("xyz.jpenilla.run-paper")

    id("crazyenchantments.paper-plugin")

    `maven-publish`
}

releaseBuild {
    tasks {
        shadowJar {
            archiveFileName.set("${getProjectName()}+${getProjectVersion()}.jar")

            listOf(
                "de.tr7zw",
                "org.bstats"
            ).forEach { value ->
                relocate(value, "${getProjectGroup()}.plugin.library.$value")
            }
        }

        runServer {
            minecraftVersion("1.19.3")
        }

        modrinth {
            token.set(System.getenv("MODRINTH_TOKEN"))
            projectId.set(getProjectName().toLowerCase())

            versionName.set("${getProjectName()} ${getProjectVersion()}")
            versionNumber.set(getProjectVersion())

            versionType.set(getProjectType())

            uploadFile.set(shadowJar.get())

            autoAddDependsOn.set(true)

            gameVersions.addAll(listOf("1.18", "1.18.1", "1.18.2", "1.19", "1.19.1", "1.19.2", "1.19.3"))
            loaders.addAll(listOf("paper", "purpur"))

            //<h3>The first release for CrazyEnchantments on Modrinth! 🎉🎉🎉🎉🎉<h3> If we want a header.
            changelog.set("""
            <h3>The first release for CrazyEnchantments on Modrinth! 🎉🎉🎉🎉🎉<h3>
              <h4>Changes:</h4>
               <p>Added 1.18.2 support.</p>
              <h4>Bugs Squashed:</h4>
               <p>Fixed a bug with /gkitz</p>
               <p>Fixed a bug with wings</p>
               <p>Fixed some npes</p>
               <p>Fixed bow enchantments</p>
               <p>Fixed vault economy</p>
            """.trimIndent())
        }

        processResources {
            filesMatching("plugin.yml") {
                expand(
                    "name" to getProjectName(),
                    "group" to getProjectGroup(),
                    "version" to getProjectVersion(),
                    "description" to getProjectDescription(),
                    "website" to "https://modrinth.com/${getExtension()}/${getProjectName().toLowerCase()}"
                )
            }
        }
    }

    publishing {
        repositories {
            val urlExt = if (isBeta()) "beta" else "releases"
            maven("https://repo.crazycrew.us/$urlExt") {
                name = "crazycrew"
                // Used for locally publishing.
                // credentials(PasswordCredentials::class)

                credentials {
                    username = System.getenv("REPOSITORY_USERNAME")
                    password = System.getenv("REPOSITORY_PASSWORD")
                }
            }
        }

        publications {
            create<MavenPublication>("maven") {
                groupId = getProjectGroup()
                artifactId = "${getProjectName().toLowerCase()}-paper"
                version = getProjectVersion()
                from(components["java"])
            }
        }
    }
}