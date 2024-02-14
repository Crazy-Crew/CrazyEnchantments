plugins {
    id("paper-plugin")
}

val mcVersion = rootProject.properties["minecraftVersion"] as String

dependencies {
    implementation(libs.metrics)

    implementation(libs.nbtapi)

    compileOnly(libs.placeholderapi)

    compileOnly(libs.worldguard)
    compileOnly(libs.worldedit)

    compileOnly(libs.oraxen)

    compileOnly("com.intellectualsites.informative-annotations", "informative-annotations", "1.3")

    compileOnly("com.github.TechFortress", "GriefPrevention", "16.18.1")

    compileOnly("com.palmergames.bukkit.towny", "towny", "0.99.5.0")

    compileOnly("fr.neatmonster", "nocheatplus", "3.16.1-SNAPSHOT")

    compileOnly("com.massivecraft", "Factions", "1.6.9.5-U0.6.31") {
        exclude("org.kitteh")
        exclude("org.spongepowered")
        exclude("com.darkblade12")
    }

    compileOnly("com.intellectualsites.paster", "Paster", "1.1.5")

    compileOnly("com.bgsoftware", "SuperiorSkyblockAPI", "2023.2")

    compileOnly("com.plotsquared", "PlotSquared-Core", "6.11.1")

    compileOnly("com.gmail.nossr50.mcMMO", "mcMMO", "2.1.226")

    compileOnly("com.bgsoftware", "WildStackerAPI", "2023.2")

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7.1") {
        exclude("org.bukkit", "bukkit")
    }

    compileOnly(fileTree("libs").include("*.jar"))

    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:$mcVersion-R0.1-SNAPSHOT")

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

    shadowJar {
        archiveClassifier.set("")

        exclude("META-INF/**")

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