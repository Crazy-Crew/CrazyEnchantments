plugins {
    `config-java`
}

repositories {
    maven("https://libraries.minecraft.net/")
}

dependencies {
    compileOnly(libs.bundles.adventure)
    compileOnly(libs.fusion.core)
    compileOnly(libs.brigadier)

    api(project(":api"))
}