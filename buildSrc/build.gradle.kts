plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20-RC2")
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "17"
    }
}