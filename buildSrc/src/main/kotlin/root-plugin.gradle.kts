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

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of("17"))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
}