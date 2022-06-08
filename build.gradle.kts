plugins {
    java

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

rootProject.group = "me.badbones69"
rootProject.version = "1.9-Dev-Build-v13"

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        // Vault API moved here.
        maven("https://jitpack.io")

        // World Edit / World Guard
        maven("https://maven.enginehub.org/repo/")
    }

    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}