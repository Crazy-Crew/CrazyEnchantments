import gradle.kotlin.dsl.accessors._e955592cfcca1783c48ac959ec339844.compileJava
import gradle.kotlin.dsl.accessors._e955592cfcca1783c48ac959ec339844.compileKotlin

plugins {
    kotlin("jvm")
    java
}

repositories {

    // Spigot API
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

    // Silk Spawners API
    maven("https://repo.dustplanet.de/artifactory/libs-release-local")

    // FactionsUUID
    maven("https://ci.ender.zone/plugin/repository/everything/")

    // NBT API | StackMob API
    maven("https://repo.codemc.org/repository/maven-public/")

    // Towny API
    maven("https://repo.glaremasters.me/repository/towny/")

    // Spartan API
    maven("https://nexus.sparky.ac/repository/Sparky/")

    // World Edit API / World Guard API
    maven("https://maven.enginehub.org/repo/")

    // Vault API
    maven("https://jitpack.io")

    // Our Repo
    maven("https://repo.badbones69.com/releases")

    mavenCentral()
}

dependencies {

    compileOnly("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT")

    compileOnly("org.apache.commons:commons-text:1.9")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    compileOnly(kotlin("stdlib", "1.6.20"))
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "17"
        javaParameters = true
    }
}

tasks.compileJava {
    options.encoding = "UTF-8"
}