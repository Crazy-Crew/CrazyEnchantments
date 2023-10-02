plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("io.papermc.paperweight", "paperweight-userdev", "1.5.6")

    implementation("com.github.johnrengelman", "shadow", "8.1.1")
}