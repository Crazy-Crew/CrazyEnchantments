package me.BadBones69.CrazyEnchantments.Enchantments;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ca.thederpygolems.armorequip.ArmorEquipEvent;
import me.BadBones69.CrazyEnchantments.Api;

public class Boots implements Listener{
	@EventHandler
 	public void onEquip(ArmorEquipEvent e){
		Player player = e.getPlayer();
		ItemStack NewItem = e.getNewArmorPiece();
		ItemStack OldItem = e.getOldArmorPiece();
		if(e.getNewArmorPiece() != null && e.getNewArmorPiece().hasItemMeta() && e.getNewArmorPiece().getType() != Material.AIR){
			if(NewItem.getItemMeta().hasLore()){
				for(String lore : NewItem.getItemMeta().getLore()){
					if(lore.contains(Api.getEnchName("AntiGravity"))){
						if(Api.isEnchantmentEnabled("AntiGravity")){
							player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 55555*20, 1+Api.getPower(lore, Api.getEnchName("AntiGravity"))));
						}
					}
					if(lore.contains(Api.getEnchName("Gears"))){
						if(Api.isEnchantmentEnabled("Gears")){
							player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 55555*20, Api.getPower(lore, Api.getEnchName("Gears"))-1));
						}
					}
					if(lore.contains(Api.getEnchName("Springs"))){
						if(Api.isEnchantmentEnabled("Springs")){
							player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 55555*20, Api.getPower(lore, Api.getEnchName("Springs"))-1));
						}
					}
				}
			}
		}
		if(e.getOldArmorPiece() != null && e.getOldArmorPiece().hasItemMeta() && e.getOldArmorPiece().getType() != Material.AIR){
			if(OldItem.getItemMeta().hasLore()){
				for(String lore : OldItem.getItemMeta().getLore()){
					if(lore.contains(Api.getEnchName("AntiGravity"))){
						if(Api.isEnchantmentEnabled("AntiGravity")){
							player.removePotionEffect(PotionEffectType.JUMP);
						}
					}
					if(lore.contains(Api.getEnchName("Gears"))){
						if(Api.isEnchantmentEnabled("Gears")){
							player.removePotionEffect(PotionEffectType.SPEED);
						}
					}
					if(lore.contains(Api.getEnchName("Springs"))){
						if(Api.isEnchantmentEnabled("Springs")){
							player.removePotionEffect(PotionEffectType.JUMP);
						}
					}
				}
			}
		}
	}
}