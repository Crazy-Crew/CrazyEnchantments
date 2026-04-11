plugins {
    `java-plugin`
}

repositories {
    maven("https://libraries.minecraft.net/")
}

dependencies {
    compileOnly(libs.bundles.adventure)
    compileOnly(libs.fusion.kyori)
    compileOnly(libs.brigadier)

    api(project(":api"))
}