package me.badbones69.crazyenchantments.multisupport.mobstackers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class MobStacker {
    
    private static Plugin ms = Bukkit.getPluginManager().getPlugin("MobStacker");
    
    public static void noStack(Entity en) {
        en.setMetadata("ms-is-stacking", new FixedMetadataValue(ms, false));
    }
    
}
