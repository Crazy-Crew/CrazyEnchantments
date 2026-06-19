import io.papermc.hangarpublishplugin.model.Platforms
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    id("io.papermc.hangar-publish-plugin")

    id("shared-plugin")
}

hangarPublish {
    publications.register("plugin") {
        apiKey.set(System.getenv("HANGAR_KEY"))

        id.set("${rootProject.property("project_id")}")

        version.set("${rootProject.version}")

        changelog.set(rootProject.ext.get("mc_changelog").toString())

        channel.set(rootProject.ext.get("release_type").toString().uppercaseFirstChar())

        pages.resourcePage(rootProject.file("README.md").readText(Charsets.UTF_8))

        platforms {
            register(Platforms.PAPER) {
                jar.set(tasks.named<Jar>("jar").flatMap { it.archiveFile })

                platformVersions.set(rootProject.property("project_versions").toString().split(",").map { it.trim() })

                dependencies {
                    // protection plugins
                    url("WorldGuard", "https://modrinth.com/plugin/worldguard") {
                        required = false
                    }

                    hangar("GriefPrevention") {
                        required = false
                    }

                    // placeholder plugins
                    hangar("PlaceholderAPI") {
                        required = false
                    }

                    // other plugins
                    hangar("Essentials") {
                        required = false
                    }

                    // party plugins
                    url("McMMO", "https://www.spigotmc.org/resources/official-mcmmo-original-author-returns.64348/") {
                        required = false
                    }

                    // claim plugins
                    url("Lands", "https://www.spigotmc.org/resources/lands-%E2%AD%95-land-claim-plugin-%E2%9C%85-grief-prevention-protection-gui-management-nations-wars-26-x-support.53313/") {
                        required = false
                    }

                    url("PlotSquared", "https://www.spigotmc.org/resources/plotsquared-v7.77506/") {
                        required = false
                    }

                    hangar("Towny") {
                        required = false
                    }

                    // skyblock plugins
                    url("SuperiorSkyBlock2", "https://bg-software.com/superiorskyblock/") {
                        required = false
                    }

                    // factions plugins
                    url("FactionsUUID", "https://www.spigotmc.org/resources/factionsuuid.1035/") {
                        required = false
                    }
                }
            }
        }
    }
}