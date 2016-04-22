package me.BadBones69.CrazyEnchantments.Enchantments.Bow;

import java.util.HashMap;

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
	HashMap<Projectile, Integer> Arrow = new HashMap<Projectile, Integer>();
	@EventHandler
	public void onBowShoot(EntityShootBowEvent e){
		if(!Api.allowsPVP(e.getEntity()))return;
		if (e.getBow().hasItemMeta()){
			if(e.getBow().getItemMeta().hasLore()){
				for(String lore : e.getBow().getItemMeta().getLore()){
					if(lore.contains(Api.getEnchName("Venom"))){
						Arrow.put((Projectile) e.getProjectile(), Api.getPower(lore, Api.getEnchName("Venom")));
					}
				}
			}
		}
	}
	@EventHandler
 	public void onArrowLand(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Arrow&&e.getEntity() instanceof LivingEntity){
			LivingEntity en = (LivingEntity) e.getEntity();
			Projectile arrow = (Projectile) e.getDamager();
			if(Arrow.containsKey(arrow)){
				if(!Api.allowsPVP(e.getEntity()))return;
				en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, Api.getPower(Arrow.get(arrow)+"", Api.getEnchName("Venom"))+2*20, 1));
			}
		}
		return;
	}
}