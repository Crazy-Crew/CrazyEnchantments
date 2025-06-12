import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

repositories {
    maven("https://repo.crazycrew.us/releases")

    mavenCentral()
}

dependencies {
    //compileOnly(libs.findLibrary("annotations").get())
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        javaParameters.assign(true)
    }

    jvmToolchain(21)
}