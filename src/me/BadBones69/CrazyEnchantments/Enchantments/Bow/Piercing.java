package me.BadBones69.CrazyEnchantments.Enchantments.Bow;

import java.util.HashMap;
import java.util.Random;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

public class Piercing implements Listener{
	HashMap<Projectile, Integer> Arrow = new HashMap<Projectile, Integer>();
	@EventHandler
	public void onBowShoot(EntityShootBowEvent e){
		if (e.getBow().hasItemMeta()) {
			for(String lore : e.getBow().getItemMeta().getLore()){
				if(lore.contains(Api.getEnchName("Piercing"))){
					Arrow.put((Projectile) e.getProjectile(), Api.getPower(lore, Api.getEnchName("Piercing")));
				}
			}
		}
	}
	@EventHandler
 	public void onArrowLand(EntityDamageByEntityEvent e){
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.getDamager() instanceof Arrow){
			Projectile arrow = (Projectile) e.getDamager();
			if(Arrow.containsKey(arrow)){
				Random number = new Random();
				int chance;
				for(int counter = 1; counter<=1; counter++){
					chance = 1 + number.nextInt(20-Api.getPower(Arrow.get(arrow)+"", Api.getEnchName("Piercing")));
					if(chance == 1){
						e.setDamage(e.getDamage() *2);
					}
				}
			}
		}
		
		return;
	}
}