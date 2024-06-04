plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("io.papermc.paperweight", "paperweight-userdev", "1.7.1")

    implementation("io.github.goooler.shadow", "shadow-gradle-plugin", "8.1.7")
}