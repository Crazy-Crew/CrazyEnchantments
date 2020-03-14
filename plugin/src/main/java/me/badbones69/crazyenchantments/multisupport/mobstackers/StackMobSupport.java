package me.badbones69.crazyenchantments.multisupport.mobstackers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.api.EntityManager;

public class StackMobSupport {
    
    private static EntityManager entityManager = new EntityManager((StackMob) Bukkit.getPluginManager().getPlugin("StackMob"));
    
    public static void preventStacking(Entity entity) {
        entityManager.preventFromStacking(entity);
    }
    
}