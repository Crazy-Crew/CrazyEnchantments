plugins {
    id("root-plugin")
}

val mcVersion: String = providers.gradleProperty("mcVersion").get()
val fabricVersion: String = providers.gradleProperty("version").get()

project.version = if (System.getenv("BUILD_NUMBER") != null) "$fabricVersion-${System.getenv("BUILD_NUMBER")}" else fabricVersion

tasks {
    modrinth {
        loaders.addAll("fabric")
    }
}