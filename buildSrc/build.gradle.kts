plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin)
    implementation(libs.shadow)

    implementation(libs.paperweight)

    implementation(libs.ktor.core)
    implementation(libs.ktor.cio)
    implementation(libs.ktor.content)
    implementation(libs.ktor.gson)

    implementation(libs.kotlin.coroutines)

    implementation(libs.turtle)
}