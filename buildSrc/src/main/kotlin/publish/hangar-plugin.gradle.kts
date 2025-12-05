import gradle.kotlin.dsl.accessors._bc702d41505d332776c95ae061891e6e.ext
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

        platforms {
            register(Platforms.PAPER) {
                jar.set(tasks.named<Jar>("jar").flatMap { it.archiveFile })

                platformVersions.set(rootProject.property("project_versions").toString().split(",").map { it.trim() })

                dependencies {
                    hangar("PlaceholderAPI") {
                        required = false
                    }

                    hangar("FancyHolograms") {
                        required = false
                    }

                    url("CMI", "https://www.spigotmc.org/resources/cmi-300-commands-insane-kits-portals-essentials-economy-mysql-sqlite-much-more.3742/") {
                        required = false
                    }

                    url("DecentHolograms", "https://modrinth.com/plugin/decentholograms") {
                        required = false
                    }

                    url("ItemsAdder", "https://polymart.org/product/1851/itemsadder") {
                        required = false
                    }

                    url("Oraxen", "https://polymart.org/product/629/oraxen") {
                        required = false
                    }

                    url("Nexo", "https://polymart.org/resource/nexo.6901") {
                        required = false
                    }
                }
            }
        }
    }
}