plugins {
    id("com.gradleup.shadow")

    `java-library`
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

repositories {
    maven("https://repo.codemc.io/repository/maven-public/")

    maven("https://repo.triumphteam.dev/snapshots/")

    maven("https://repo.crazycrew.us/libraries/")
    maven("https://repo.crazycrew.us/releases/")

    maven("https://libraries.minecraft.net/")

    maven("https://jitpack.io/")

    mavenCentral()
}

dependencies {
    compileOnly(libs.findLibrary("annotations").get())
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    shadowJar {
        archiveClassifier.set("")

        exclude("META-INF/**")
    }

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
            "website" to "https://github.com/Crazy-Crew/${rootProject.name}",
            "id" to rootProject.name.lowercase(),
            "group" to project.group
        )

        with(copySpec {
            include("*plugin.yml", "fabric.mod.json")
            from("src/main/resources") {
                expand(inputs.properties)
            }
        })
    }
}