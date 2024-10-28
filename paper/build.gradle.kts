plugins {
    alias(libs.plugins.runPaper)
    alias(libs.plugins.shadow)

    alias(libs.plugins.paperweight)
}

base {
    archivesName.set(rootProject.name)
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public")

    maven("https://repo.md-5.net/content/repositories/snapshots")

    maven("https://ci.ender.zone/plugin/repository/everything")

    maven("https://repo.glaremasters.me/repository/towny")

    maven("https://repo.bg-software.com/repository/api")

    maven("https://maven.enginehub.org/repo")

    maven("https://repo.oraxen.com/releases")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)

    compileOnly(libs.informative.annotations)

    compileOnly(libs.vault) {
        exclude("org.bukkit", "bukkit")
    }

    compileOnly(libs.griefprevention)

    compileOnly(libs.oraxen)

    compileOnly(libs.worldguard)
    compileOnly(libs.worldedit)

    compileOnly(libs.kingdoms)

    compileOnly(libs.factions) {
        exclude("org.kitteh")
        exclude("org.spongepowered")
        exclude("com.darkblade12")
    }

    compileOnly(libs.towny)

    compileOnly(libs.lands)

    compileOnly(libs.paster)

    compileOnly(libs.skyblock)

    compileOnly(libs.plotsquared)

    compileOnly(libs.mcmmo)
}

val component: SoftwareComponent = components["java"]

paperweight {
    paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION
}

tasks {
    publishing {
        repositories {
            maven {
                url = uri("https://repo.crazycrew.us/releases")

                credentials {
                    this.username = System.getenv("gradle_username")
                    this.password = System.getenv("gradle_password")
                }
            }
        }

        publications{
            create<MavenPublication>("maven") {
                groupId = rootProject.group.toString()
                artifactId = "${rootProject.name.lowercase()}-paper-api"
                version = rootProject.version.toString()

                from(component)
            }
        }
    }

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(libs.versions.minecraft.get())
    }

    assemble {
        doLast {
            copy {
                from(shadowJar.get())
                into(rootProject.projectDir.resolve("jars"))
            }
        }
    }

    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")
    }

    processResources {
        inputs.properties("name" to rootProject.name)
        inputs.properties("version" to project.version)
        inputs.properties("group" to "${project.group}.paper")
        inputs.properties("description" to project.description)
        inputs.properties("apiVersion" to libs.versions.minecraft.get())
        inputs.properties("website" to "https://modrinth.com/plugin/crazyenchantments")

        filesMatching("plugin.yml") {
            expand(inputs.properties)
        }
    }
}