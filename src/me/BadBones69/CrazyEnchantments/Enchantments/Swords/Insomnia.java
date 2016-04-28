package me.BadBones69.CrazyEnchantments.Enchantments.Swords;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ca.thederpygolems.armorequip.ArmorEquipEvent;

public class Insomnia implements Listener{
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		if(!Api.isEnchantmentEnabled("Insomnia"))return;
		if(!Api.allowsPVP(e.getEntity()))return;
		if(!Api.allowsPVP(e.getDamager()))return;
		if(e.isCancelled())return;
		double damage = e.getDamage()*2;
		if(e.getEntity() instanceof LivingEntity){
			if(e.getDamager() instanceof Player){
				Player damager = (Player) e.getDamager();
				if(Api.getItemInHand(damager).hasItemMeta()){
					if(!Api.getItemInHand(damager).getItemMeta().hasLore())return;
					if(!e.getEntity().isDead()){
						for(String lore : Api.getItemInHand(damager).getItemMeta().getLore()){
							if(lore.contains(Api.getEnchName("Insomnia"))){
								if(Api.randomPicker(3)){
									e.setDamage(damage);
								}
							}
						}
					}
				}
			}
		}
	}
	@EventHandler
 	public void onEquip(ArmorEquipEvent e){
		if(!Api.isEnchantmentEnabled("Insomnia"))return;
		Player player = e.getPlayer();
		ItemStack NewItem = e.getNewArmorPiece();
		ItemStack OldItem = e.getOldArmorPiece();
		if(e.getNewArmorPiece() != null && e.getNewArmorPiece().hasItemMeta() && e.getNewArmorPiece().getType() != Material.AIR){
			if(NewItem.getItemMeta().hasLore()){
				for(String lore : NewItem.getItemMeta().getLore()){
					if(lore.equals(Api.color("Insomnia"))){
						player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 55555, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 55555, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 55555, 1));
						return;
					}
				}
			}
		}
		if(e.getOldArmorPiece() != null && e.getOldArmorPiece().hasItemMeta() && e.getOldArmorPiece().getType() != Material.AIR){
			if(OldItem.getItemMeta().hasLore()){
				for(String lore : OldItem.getItemMeta().getLore()){
					if(lore.equals(Api.color("Insomnia"))){
						player.removePotionEffect(PotionEffectType.CONFUSION);
						player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
						player.removePotionEffect(PotionEffectType.SLOW);
						return;
					}
				}
			}
		}
	}
}