repositories {
    // Silk Spawners
    maven("https://repo.dustplanet.de/artifactory/libs-release-local")

    maven("https://repo.codemc.org/repository/maven-public/")

}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    implementation("de.tr7zw:nbt-data-api:2.10.0-SNAPSHOT")

    // Latest Silk Spawners
    compileOnly("de.dustplanet:silkspawners:7.2.0")

    implementation(project(":api"))
    implementation(project(":v1_12_2_down"))
    implementation(project(":v1_13_up"))
}