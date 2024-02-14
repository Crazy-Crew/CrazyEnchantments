import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    id("root-plugin")
}

base {
    archivesName.set("${rootProject.name}-${project.name.uppercaseFirstChar()}")
}

val mcVersion = rootProject.properties["minecraftVersion"] as String
val fabricVersion = rootProject.properties["fabricVersion"] as String

project.version = if (System.getenv("BUILD_NUMBER") != null) "$fabricVersion-${System.getenv("BUILD_NUMBER")}" else fabricVersion

tasks {
    val isBeta: Boolean = rootProject.extra["isBeta"]?.toString()?.toBoolean() ?: false
    val type = if (isBeta) "Beta" else "Release"

    modrinth {
        versionType.set(type.lowercase())

        loaders.addAll("fabric")
    }
}