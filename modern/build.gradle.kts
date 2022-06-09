plugins {
    id("enchantments.common")
}

dependencies {

    compileOnly("com.sk89q.worldguard:worldguard-legacy:7.0.0-SNAPSHOT") {
        exclude("org.bukkit", "bukkit")
        exclude("org.bstats", "bstats-bukkit")
    }

    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.0.0-SNAPSHOT") {
        exclude("org.bukkit", "bukkit")
        exclude("org.bstats", "bstats-bukkit")
    }

    compileOnly("com.plotsquared:PlotSquared-Core:6.8.1") {
        exclude("com.destroystokyo.paper", "paper-api")
    }

    compileOnly("uk.antiperson.stackmob:StackMob:5.5.3")

    compileOnly("me.vagdedes:SpartanAPI:9.1")

    compileOnly(project(":api"))
}