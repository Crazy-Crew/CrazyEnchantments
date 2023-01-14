plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.21")

    // For the webhook tasks, this applies to the build-logic only
    implementation("io.ktor:ktor-client-core:2.2.2")
    implementation("io.ktor:ktor-client-cio:2.2.2")
    implementation("io.ktor:ktor-client-content-negotiation:2.2.2")
    implementation("io.ktor:ktor-serialization-gson:2.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}
