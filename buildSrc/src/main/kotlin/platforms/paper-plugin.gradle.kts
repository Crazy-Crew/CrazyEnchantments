plugins {
    id("io.papermc.paperweight.userdev")
    id("xyz.jpenilla.run-paper")

    id("shadow-plugin")
}

val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.extendedclip.com/releases/")
}

dependencies {
    paperweight.paperDevBundle(libs.findVersion("paper").get().toString())
}