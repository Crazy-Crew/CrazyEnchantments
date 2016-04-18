package me.BadBones69.CrazyEnchantments.Enchantments.Bow;

import java.util.ArrayList;
import java.util.Random;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

public class Piercing implements Listener{
	ArrayList<Projectile> arrow1 = new ArrayList<Projectile>();
	ArrayList<Projectile> arrow2 = new ArrayList<Projectile>();
	ArrayList<Projectile> arrow3 = new ArrayList<Projectile>();
	@EventHandler
	public void onBowShoot(EntityShootBowEvent e){
		if (e.getBow().hasItemMeta()) {
			if (e.getBow().getItemMeta().getLore().contains(Api.color("&7Piercing I"))) {
				arrow1.add((Projectile) e.getProjectile());
			}
			if (e.getBow().getItemMeta().getLore().contains(Api.color("&7Piercing II"))) {
				arrow2.add((Projectile) e.getProjectile());
			}
			if (e.getBow().getItemMeta().getLore().contains(Api.color("&7Piercing III"))) {
				arrow3.add((Projectile) e.getProjectile());
			}
		}
	}
	@EventHandler
 	public void onArrowLand(EntityDamageByEntityEvent e){
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.getDamager() instanceof Arrow){
			Projectile arrow = (Projectile) e.getDamager();
			if(arrow1.contains(arrow)){
				Random number = new Random();
				int chance;
				for(int counter = 1; counter<=1; counter++){
					chance = 1 + number.nextInt(15);
					if(chance == 1){
						e.setDamage(e.getDamage() *2);
					}
				}
			}
			if(arrow2.contains(arrow)){
				Random number = new Random();
				int chance;
				for(int counter = 1; counter<=1; counter++){
					chance = 1 + number.nextInt(10);
					if(chance == 1){
						e.setDamage(e.getDamage() *2);
					}
				}
			}
			if(arrow3.contains(arrow)){
				Random number = new Random();
				int chance;
				for(int counter = 1; counter<=1; counter++){
					chance = 1 + number.nextInt(7);
					if(chance == 1){
						e.setDamage(e.getDamage() *2);
					}
				}
			}
		}
		
		return;
	}
}