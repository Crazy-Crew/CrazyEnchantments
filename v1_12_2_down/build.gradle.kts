dependencies {
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")

    compileOnly("com.sk89q.worldguard:worldguard-legacy:6.2")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:6.1.4-SNAPSHOT")

    implementation(project(":api"))
}