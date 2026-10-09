plugins {
    `java-plugin`
}

project.group = "${rootProject.group}.core"

repositories {

}

dependencies {
    compileOnly(libs.bundles.adventure)
    compileOnly(libs.fusion.kyori)
}