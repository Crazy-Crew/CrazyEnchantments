plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()

    maven("https://repo.crazycrew.us/first-party/")
}

dependencies {
    implementation("com.github.johnrengelman", "shadow", "8.1.1")

    implementation("io.papermc.paperweight", "paperweight-userdev", "1.5.5")

    implementation("com.modrinth.minotaur", "Minotaur", "2.8.0")
    implementation("io.papermc", "hangar-publish-plugin", "0.0.5")
}