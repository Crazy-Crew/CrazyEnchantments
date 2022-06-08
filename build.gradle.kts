plugins {
    java
}

allprojects {
    apply(plugin = "java")

    group = "me.badbones69"
    version = "1.8-Dev-Build-v12"

    repositories {
        // Plot Squared
        maven("https://mvn.intellectualsites.com/content/repositories/snapshots/")

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        // Vault API moved here.
        maven("https://jitpack.io")

        // World Edit / World Guard
        maven("https://maven.enginehub.org/repo/")
    }
}