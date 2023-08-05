plugins {
    `java-library`
    `maven-publish`

    id("com.github.johnrengelman.shadow")
}

repositories {
    maven("https://repo.dustplanet.de/artifactory/libs-release-local")

    maven("https://repo.mrivanplays.com/repository/other-developers/")

    maven("https://repo.mrivanplays.com/repository/maven-all/")

    maven("https://repo.codemc.io/repository/maven-public/")

    maven("https://repo.crazycrew.us/first-party/")

    maven("https://repo.crazycrew.us/third-party/")

    maven("https://jitpack.io/")

    mavenCentral()
}

val isSnapshot = rootProject.version.toString().contains("snapshot")

publishing {
    repositories {
        maven {
            credentials {
                this.username = System.getenv("gradle_username")
                this.password = System.getenv("gradle_password")
            }

            if (isSnapshot) {
                url = uri("https://repo.crazycrew.us/snapshots/")
                return@maven
            }

            url = uri("https://repo.crazycrew.us/releases/")
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of("17"))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
}