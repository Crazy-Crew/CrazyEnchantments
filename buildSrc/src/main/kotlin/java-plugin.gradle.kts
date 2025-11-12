plugins {
    id("com.ryderbelserion.feather.core")

    `java-library`
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

repositories {
    maven("https://repo.codemc.io/repository/maven-public/")

    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://repo.crazycrew.us/libraries/")
    maven("https://repo.crazycrew.us/releases/")

    maven("https://jitpack.io/")

    mavenCentral()
    mavenLocal()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()

        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        inputs.properties(
            "name" to rootProject.name,
            "version" to rootProject.version,
            "description" to rootProject.description.toString(),
            "minecraft" to libs.findVersion("minecraft").get(),
            "website" to "https://github.com/${rootProject.property("repository_owner")}/${rootProject.name}",
            "group" to project.group
        )

        with(copySpec {
            include("*paper-plugin.yml", "*plugin.yml")

            from("src/main/resources") {
                expand(inputs.properties)
            }
        })
    }
}