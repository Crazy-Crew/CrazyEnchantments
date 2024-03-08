import gradle.kotlin.dsl.accessors._8291d1211fdf2e346e0abe66afb65704.idea
import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("io.papermc.hangar-publish-plugin")

    id("com.github.johnrengelman.shadow")

    id("com.modrinth.minotaur")

    `java-library`

    `maven-publish`

    idea
}

base {
    archivesName.set(rootProject.name)
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

repositories {
    maven("https://repo.crazycrew.us/snapshots/")

    maven("https://repo.crazycrew.us/releases/")

    maven("https://jitpack.io/")

    mavenCentral()
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    shadowJar {
        archiveClassifier.set("")

        exclude("META-INF/**")
    }

    val directory = File("$rootDir/jars/${project.name.lowercase()}")
    val mcVersion = providers.gradleProperty("mcVersion").get()

    val isBeta: Boolean = providers.gradleProperty("isBeta").get().toBoolean()
    val type = if (isBeta) "Beta" else "Release"

    // Publish to hangar.papermc.io.
    hangarPublish {
        publications.register("plugin") {
            version.set("${project.version}")

            id.set(rootProject.name)

            channel.set(type)

            changelog.set(rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8))

            apiKey.set(System.getenv("hangar_key"))

            platforms {
                register(Platforms.PAPER) {
                    jar.set(file("$directory/${rootProject.name}-${project.version}.jar"))

                    platformVersions.set(listOf(mcVersion))
                }
            }
        }
    }

    modrinth {
        versionType.set(type.lowercase())

        autoAddDependsOn.set(false)

        token.set(System.getenv("modrinth_token"))

        projectId.set(rootProject.name.lowercase())

        changelog.set(rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8))

        versionName.set("${rootProject.name} ${project.version}")

        versionNumber.set("${project.version}")

        uploadFile.set("$directory/${rootProject.name}-${project.version}.jar")

        gameVersions.add(mcVersion)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of("17"))
}