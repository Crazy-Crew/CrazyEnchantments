plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(tools.jetbrains)
    implementation(tools.license)
    implementation(tools.shadowJar)

    // For the webhook tasks, this applies to the build-logic only
    implementation(tools.ktor.gson)
    implementation(tools.ktor.core)
    implementation(tools.ktor.cio)
    implementation(tools.ktor.cn)
    implementation(tools.kotlinx)
}