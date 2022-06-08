dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT")

    compileOnly("com.sk89q.worldguard:worldguard-legacy:7.0.0-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.0.0-SNAPSHOT")

    compileOnly("com.plotsquared:PlotSquared-Core:6.7.0")

    compileOnly("org.projectlombok:lombok:1.18.24")

    implementation(project(":api"))
}