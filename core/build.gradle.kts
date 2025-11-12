plugins {
    `java-plugin`
}

dependencies {
    compileOnly(libs.bundles.adventure)

    compileOnly(libs.fusion.core)

    api(project(":api"))
}