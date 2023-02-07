@Suppress("DSL_SCOPE_VIOLATION")

plugins {
    id("crazyenchantments.paper-plugin")

    alias(settings.plugins.minotaur)
    alias(settings.plugins.run.paper)
}

dependencies {
    // NBT API / BSTATS
    implementation(libs.nbt.api)
    implementation(libs.bstats.bukkit)

    // PaperMC
    compileOnly(libs.papermc)

    // Custom Items
    compileOnly(libs.oraxen)

    // Anticheats
    compileOnly(libs.nocheatplus)
    compileOnly(libs.spartan)

    // Claims
    compileOnly(libs.plotsquared)
    compileOnly(libs.plotsquared.annotations)
    compileOnly(libs.plotsquared.paster)

    compileOnly(libs.superiorskyblock)

    compileOnly(libs.massivecraft)
    compileOnly(libs.towny)

    // Stackers/Spawners
    compileOnly(libs.wildstacker)

    compileOnly(libs.silkspawners) {
        exclude("org.bukkit", "bukkit")
        exclude("org.spigotmc", "spigot")
        exclude("com.destroystokyo.paper", "paper")
        exclude("com.sk89q", "worldguard")
        exclude("com.sk89q", "worldedit")
        exclude("com.massivecraft.massivecore", "MassiveCore")
        exclude("com.massivecraft.factions", "Factions")
        exclude("net.gravitydevelopment.updater", "updater")
        exclude("com.intellectualsites", "Pipeline")
    }

    // Protection
    compileOnly(libs.worldedit.api)

    compileOnly(libs.griefprevention)

    compileOnly(libs.worldguard.api)

    // Misc
    compileOnly(libs.placeholder.api)
    compileOnly(libs.vault.api)
}

val projectDescription = settings.versions.projectDescription.get()
val projectGithub = settings.versions.projectGithub.get()
val projectGroup = settings.versions.projectGroup.get()
val projectName = settings.versions.projectName.get()
val projectExt = settings.versions.projectExtension.get()

val isBeta = settings.versions.projectBeta.get().toBoolean()

val projectVersion = settings.versions.projectVersion.get()

val finalVersion = if (isBeta) "$projectVersion+Beta" else projectVersion

val projectNameLowerCase = projectName.toLowerCase()

val repo = if (isBeta) "beta" else "releases"
val type = if (isBeta) "beta" else "release"

tasks {
    shadowJar {
        archiveFileName.set("${projectName}+$finalVersion.jar")

        listOf(
            "de.tr7zw.changeme.nbtapi",
            "org.bstats"
        ).forEach { relocate(it, "$projectGroup.plugin.library.$it") }
    }

    runServer {
        minecraftVersion("1.19.3")
    }

    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set(projectNameLowerCase)

        versionName.set("$projectName $finalVersion")
        versionNumber.set(finalVersion)

        versionType.set(type)

        uploadFile.set(shadowJar.get())

        autoAddDependsOn.set(true)

        gameVersions.addAll(
            listOf(
                "1.17",
                "1.17.1",
                "1.18",
                "1.18.1",
                "1.18.2",
                "1.19",
                "1.19.1",
                "1.19.2",
                "1.19.3"
            )
        )

        loaders.addAll(listOf("paper", "purpur"))

        //<h3>The first release for CrazyEnchantments on Modrinth! 🎉🎉🎉🎉🎉<h3><br> If we want a header.
        changelog.set(
            """
                <h4>Changes:</h4>
                 <p>Added 1.17.1 support back.</p>
                 <p>Ability to set Base EXP while also scaling each level off the base level in /tinkerer. (TDL)</p>
                 <p>Added permissions to plugin.yml & new wildcards. <a href="https://github.com/Crazy-Crew/CrazyEnchantments/blob/dev/platforms/paper/src/main/resources/plugin.yml">Click Me</a></p>
                <h4>Under the hood changes</h4>
                 <p>Re-organized the build script for the last time.</p>
                 <p>Cleaned up a few pieces of code.</p>
                 <p>Cleaned up a few typos</p>
                 <p>Reduced dependency on CrazyManager class by using static classes</p>
                <h4>Bug Fixes:</h4>
                 <p>Fixed an NPE with SuperiorSkyBlock.</p>
                 <p>Fixed a few more startup npe's</p>
                 <p>Fixed worldguard support</p>
            """.trimIndent()
        )
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to projectName,
                "group" to projectGroup,
                "version" to finalVersion,
                "description" to projectDescription,
                "website" to "https://modrinth.com/$projectExt/$projectNameLowerCase"
            )
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = projectGroup
            artifactId = "$projectNameLowerCase-paper-api"
            version = finalVersion

            from(components["java"])

            pom {
                name.set(projectName)

                description.set(projectDescription)
                url.set(projectGithub)

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://www.opensource.org/licenses/mit-license.php")
                    }
                }

                developers {
                    developer {
                        id.set("ryderbelserion")
                        name.set("Ryder Belserion")
                    }

                    developer {
                        id.set("badbones69")
                        name.set("BadBones69")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/Crazy-Crew/$projectName.git")
                    developerConnection.set("scm:git:ssh://github.com/Crazy-Crew/$projectName.git")
                    url.set(projectGithub)
                }
            }
        }
    }

    repositories {
        maven("https://repo.crazycrew.us/$repo") {
            name = "crazycrew"
            // Used for locally publishing.
            // credentials(PasswordCredentials::class)

            credentials {
                username = System.getenv("REPOSITORY_USERNAME")
                password = System.getenv("REPOSITORY_PASSWORD")
            }
        }
    }
}