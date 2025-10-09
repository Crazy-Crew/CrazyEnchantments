plugins {
    `config-java`
}

dependencies {
    compileOnly(libs.fusion.core)

    api(project(":api"))
}