import gradle.kotlin.dsl.accessors._3c6de1dd92ae3b7d1ad54590cc9ae150.base
import io.papermc.hangarpublishplugin.model.Platforms
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    id("io.papermc.hangar-publish-plugin")

    id("io.papermc.paperweight.userdev")

    id("xyz.jpenilla.run-paper")

    id("root-plugin")
}

base {
    archivesName.set("${rootProject.name}-${project.name.uppercaseFirstChar()}")
}

repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.dustplanet.de/artifactory/libs-release-local/")

    maven("https://repo.md-5.net/content/repositories/snapshots/")

    maven("https://ci.ender.zone/plugin/repository/everything/")

    maven("https://repo.codemc.org/repository/maven-public/")

    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.glaremasters.me/repository/towny/")

    maven("https://repo.bg-software.com/repository/api/")

    maven("https://repo.crazycrew.us/third-party/")

    maven("https://maven.enginehub.org/repo/")

    maven("https://repo.oraxen.com/releases/")

    flatDir { dirs("libs") }
}

val mcVersion = rootProject.properties["minecraftVersion"] as String

project.version = if (System.getenv("BUILD_NUMBER") != null) "${rootProject.version}-${System.getenv("BUILD_NUMBER")}" else rootProject.version

dependencies {
    paperweight.paperDevBundle("$mcVersion-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    runServer {
        jvmArgs("-Dnet.kyori.ansi.colorLevel=truecolor")

        defaultCharacterEncoding = Charsets.UTF_8.name()

        minecraftVersion(mcVersion)
    }

    val directory = File("$rootDir/jars")
    val isBeta: Boolean = rootProject.extra["isBeta"]?.toString()?.toBoolean() ?: false
    val type = if (isBeta) "Beta" else "Release"

    // Publish to hangar.papermc.io.
    hangarPublish {
        publications.register("plugin") {
            version.set("${project.version}")

            id.set(rootProject.name)

            channel.set(type)

            changelog.set(rootProject.file("CHANGELOG.md").readText())

            apiKey.set(System.getenv("hangar_key"))

            platforms {
                register(Platforms.PAPER) {
                    jar.set(file("$directory/${rootProject.name}-${project.name.uppercaseFirstChar()}-${project.version}.jar"))

                    platformVersions.set(listOf(mcVersion))
                }
            }
        }
    }

    modrinth {
        versionType.set(type.lowercase())

        loaders.addAll("paper", "purpur")
    }
}