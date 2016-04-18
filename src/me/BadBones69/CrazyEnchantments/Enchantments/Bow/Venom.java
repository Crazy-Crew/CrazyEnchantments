package me.BadBones69.CrazyEnchantments.Enchantments.Bow;

import java.util.ArrayList;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Venom implements Listener{
	ArrayList<Projectile> arrow1 = new ArrayList<Projectile>();
	ArrayList<Projectile> arrow2 = new ArrayList<Projectile>();
	ArrayList<Projectile> arrow3 = new ArrayList<Projectile>();
	@EventHandler
	public void onBowShoot(EntityShootBowEvent e){
		if (e.getBow().hasItemMeta()){
			if(e.getBow().getItemMeta().hasLore()){
				if (e.getBow().getItemMeta().getLore().contains(Api.color("&7Venom I"))) {
					arrow1.add((Projectile) e.getProjectile());
				}
				if (e.getBow().getItemMeta().getLore().contains(Api.color("&7Venom II"))) {
					arrow2.add((Projectile) e.getProjectile());
				}
				if (e.getBow().getItemMeta().getLore().contains(Api.color("&7Venom III"))) {
					arrow3.add((Projectile) e.getProjectile());
				}
			}
		}
	}
	@EventHandler
 	public void onArrowLand(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Arrow&&e.getEntity() instanceof LivingEntity){
			LivingEntity en = (LivingEntity) e.getEntity();
			Projectile arrow = (Projectile) e.getDamager();
			if(arrow1.contains(arrow)){
				en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5*20, 1));
			}
			if(arrow2.contains(arrow)){
				en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5*20, 2));
			}
			if(arrow3.contains(arrow)){
				en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5*20, 3));
			}
		}
		return;
	}
}