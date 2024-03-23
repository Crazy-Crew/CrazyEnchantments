plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(libs.paperweight.userdev)
    implementation(libs.publishing.modrinth)
    implementation(libs.publishing.hangar)
}