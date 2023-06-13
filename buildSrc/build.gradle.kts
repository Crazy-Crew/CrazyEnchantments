plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()

    maven("https://repo.crazycrew.us/first-party/")
    maven("https://repo.crazycrew.us/third-party/")
}

dependencies {
    implementation(libs.shadow)

    implementation(libs.paperweight)
    implementation(libs.featherweight)

    implementation(libs.minotaur)
    implementation(libs.hangar)

    implementation(libs.turtle)
}