package me.BadBones69.CrazyEnchantments.Enchantments.Bow;

import java.util.HashMap;
import java.util.Random;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class Boom implements Listener{
	HashMap<Projectile, Integer> Arrow = new HashMap<Projectile, Integer>();
	@EventHandler
	public void onBowShoot(EntityShootBowEvent e){
		if (e.getBow().hasItemMeta()) {
			for(String lore : e.getBow().getItemMeta().getLore()){
				if(lore.contains(Api.getEnchName("Boom"))){
					Arrow.put((Projectile) e.getProjectile(), Api.getPower(lore, Api.getEnchName("Boom")));
				}
			}
		}
	}
	@EventHandler
	public void onland(ProjectileHitEvent e) {
		if(Arrow.containsKey(e.getEntity())){
			Random number = new Random();
			int chance;
			for(int counter = 1; counter<=1; counter++){
				chance = 1 + number.nextInt(20-Api.getPower(Arrow.get(e.getEntity())+"", Api.getEnchName("Boom")));
				if(chance == 1){
					e.getEntity().getWorld().spawn(e.getEntity().getLocation(), TNTPrimed.class);
					Arrow.remove(e.getEntity());
					e.getEntity().remove();
				}
			}
		}
	}
}
