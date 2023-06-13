dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }

    repositories {
        gradlePluginPortal()

        maven("https://repo.crazycrew.us/first-party/")
        maven("https://repo.crazycrew.us/third-party/")
    }
}