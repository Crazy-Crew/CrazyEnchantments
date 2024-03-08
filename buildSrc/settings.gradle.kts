dependencyResolutionManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")

        gradlePluginPortal()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            library("paperweight", "io.papermc.paperweight", "paperweight-userdev").version("1.5.9")

            library("hangar", "io.papermc", "hangar-publish-plugin").version("0.1.2")

            library("modrinth", "com.modrinth.minotaur", "Minotaur").version("2.8.7")

            library("shadow", "com.github.johnrengelman", "shadow").version("8.1.1")

            library("runpaper", "xyz.jpenilla", "run-task").version("2.2.3")
        }
    }
}