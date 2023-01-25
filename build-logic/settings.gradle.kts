@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    versionCatalogs {
        create("tools") {
            from(files("../gradle/tools.versions.toml"))
        }
    }

    repositories.gradlePluginPortal()
}