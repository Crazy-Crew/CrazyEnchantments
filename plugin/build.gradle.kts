repositories {
    // Silk Spawners
    maven("https://repo.dustplanet.de/artifactory/libs-release-local")

    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    implementation("de.tr7zw:nbt-data-api:2.10.0-SNAPSHOT")

    // Latest Silk Spawners
    compileOnly("de.dustplanet:silkspawners:7.2.0")

    implementation(project(":api"))
    implementation(project(":modern"))
}