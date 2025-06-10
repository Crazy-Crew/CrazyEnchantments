plugins {
    `config-java`
}

dependencies {
    api(project(":crazyenchantments-api"))

    compileOnly(libs.bundles.adventure)

    compileOnly(libs.fusion.core)
}