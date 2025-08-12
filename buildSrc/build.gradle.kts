plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    // Bundled plugins
    implementation(libs.bundles.build)

    // Paper plugins
    implementation(libs.bundles.paper)
}