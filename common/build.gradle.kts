plugins {
    `config-java`
}

project.group = "${rootProject.group}.common"

dependencies {
    api(libs.fusion.core)
}