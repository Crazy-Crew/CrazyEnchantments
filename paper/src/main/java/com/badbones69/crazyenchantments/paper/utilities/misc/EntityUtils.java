package com.badbones69.crazyenchantments.paper.utilities.misc;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class EntityUtils {
    public static Material getHeadMaterial(Entity entity) {
        Material headMat = null;
        switch(entity.getType()) {
            case ZOMBIE:
                headMat = Material.ZOMBIE_HEAD;
                break;
            case SKELETON:
                headMat = Material.SKELETON_SKULL;
                break;
            case CREEPER:
                headMat = Material.CREEPER_HEAD;
                break;
            // Piglins only drop their heads when killed by a charged creeper
            case PIGLIN:
                headMat = Material.PIGLIN_HEAD;
                break;
            case WITHER_SKELETON:
                headMat = Material.WITHER_SKELETON_SKULL;
                break;
            case ENDER_DRAGON:
                headMat = Material.DRAGON_HEAD;
                break;
        }
        return headMat;
    }
}
