package me.badbones69.crazyenchantments.multisupport;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.api.EntityManager;

public class StackMobSupport implements Listener {

	private static EntityManager entityManager = new EntityManager((StackMob) Bukkit.getPluginManager().getPlugin("StackMob"));

	public static void preventStacking(Entity entity) {
		entityManager.preventFromStacking(entity);
	}

}