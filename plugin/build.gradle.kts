plugins {
    id("enchantments.common")

    id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
    implementation("de.tr7zw:nbt-data-api:2.10.0-SNAPSHOT")

    implementation(project(":modern"))
    implementation(project(":api"))
}

tasks {
    shadowJar {
        archiveFileName.set("Crazy-Enchantments-[v${rootProject.version}].jar")

        val path = "com.badbones69.crazyenchantments.libs"

        relocate("de.tr7zw", path)
    }
}