plugins {
    `maven-publish`

    `java-library`
}


repositories {
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi")

    maven("https://repo.codemc.io/repository/maven-public")

    maven("https://repo.crazycrew.us/releases")

    maven("https://repo.oraxen.com/releases")

    maven("https://jitpack.io")

    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}