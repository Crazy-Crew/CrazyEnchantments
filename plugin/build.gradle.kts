plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    // Silk Spawners
    maven("https://repo.dustplanet.de/artifactory/libs-release-local")

    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude("org.bukkit", "bukkit")
    }

    implementation("de.tr7zw:nbt-data-api:2.10.0-SNAPSHOT")

    compileOnly("org.apache.commons:commons-text:1.9")

    // implementation("org.bstats:bstats-bukkit:3.0.0")

    // Latest Silk Spawners
    compileOnly("de.dustplanet:silkspawners:7.3.0") {
        exclude("*", "*")
    }

    implementation(project(":modern"))
    implementation(project(":api"))
}

tasks {
    shadowJar {
        archiveFileName.set("Crazy-Enchantments-[v${rootProject.version}].jar")

        val path = "me.badbones69.crazyenchantments.libs"

        relocate("de.tr7zw", path)
        relocate("org.apache.commons", path)
        // relocate("org.bstats", path)
    }
}