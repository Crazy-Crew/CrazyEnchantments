plugins {
    `paper-plugin`

    id("io.papermc.paperweight.userdev")

    alias(libs.plugins.run.paper)
    alias(libs.plugins.shadow)
}

val mcVersion: String = providers.gradleProperty("mcVersion").get()

dependencies {
    paperweight.paperDevBundle(libs.versions.bundle)

    implementation(libs.metrics)

    implementation(libs.nbtapi)

    compileOnly(libs.placeholder.api)

    compileOnly(libs.vault) {
        exclude("org.bukkit", "bukkit")
    }

    compileOnly(libs.worldguard)
    compileOnly(libs.worldedit)

    compileOnly(libs.oraxen.api)

    compileOnly(libs.informative.annotations)

    compileOnly(libs.griefprevention)

    compileOnly(libs.towny)

    compileOnly(libs.nocheatplus)

    compileOnly(libs.kingdoms)

    compileOnly(libs.factions) {
        exclude("org.kitteh")
        exclude("org.spongepowered")
        exclude("com.darkblade12")
    }

    compileOnly(libs.paster)

    compileOnly(libs.skyblock)

    compileOnly(libs.plotsquared)

    compileOnly(libs.mcmmo)

    compileOnly(libs.wildstacker)

    compileOnly(fileTree("libs").include("*.jar"))
}

val component: SoftwareComponent = components["java"]

tasks {
    publishing {
        repositories {
            maven {
                url = uri("https://repo.crazycrew.us/releases/")

                credentials {
                    this.username = System.getenv("GRADLE_USERNAME")
                    this.password = System.getenv("GRADLE_PASSWORD")
                }
            }
        }

        publications{
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = "${rootProject.name.lowercase()}-${project.name.lowercase()}-api"
                version = rootProject.version.toString()

                from(component)
            }
        }
    }

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(mcVersion)
    }

    reobfJar {
        dependsOn(assemble)
    }

    shadowJar {
        archiveClassifier.set("")

        listOf(
            "de.tr7zw.changeme.nbtapi",
            "org.bstats"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    processResources {
        val properties = hashMapOf(
                "name" to rootProject.name,
                "version" to rootProject.version,
                "group" to "${project.group}.paper",
                "description" to rootProject.description,
                "apiVersion" to rootProject.properties["apiVersion"],
                "authors" to rootProject.properties["authors"],
                "website" to rootProject.properties["website"]
        )

        inputs.properties(properties)

        filesMatching("plugin.yml") {
            expand(properties)
        }
    }
}