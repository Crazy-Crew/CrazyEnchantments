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

    // Factions Plugins
    compileOnly("com.massivecraft:Factions:1.6.9.5-U0.6.9")

    // SkyBlock Plugins
    compileOnly("com.github.OmerBenGera:SuperiorSkyblockAPI:b11")

    // Anti Cheats

    compileOnly("me.vagdedes:SpartanAPI:9.1")

    // Base Project

    compileOnly(project(":api"))
}