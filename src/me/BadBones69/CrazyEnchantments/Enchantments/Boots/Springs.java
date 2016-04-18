package me.BadBones69.CrazyEnchantments.Enchantments.Boots;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import ca.thederpygolems.armorequip.ArmorEquipEvent;

public class Springs implements Listener{
	@EventHandler
 	public void onEquip(ArmorEquipEvent e){
		Player player = e.getPlayer();
		ItemStack NewItem = e.getNewArmorPiece();
		ItemStack OldItem = e.getOldArmorPiece();
		if(e.getNewArmorPiece() != null && e.getNewArmorPiece().hasItemMeta() && e.getNewArmorPiece().getType() != Material.AIR){
			if(!NewItem.getItemMeta().hasLore())return;
			for(String lore : NewItem.getItemMeta().getLore()){
				if(lore.equals(Api.color("&7Springs I"))){
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 55555*20, 0));
					return;
				}
				if(lore.equals(Api.color("&7Springs II"))){
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 55555*20, 1));
					return;
				}
				if(lore.equals(Api.color("&7Springs III"))){
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 55555*20, 2));
					return;
				}
			}
		}
		if(e.getOldArmorPiece() != null && e.getOldArmorPiece().hasItemMeta() && e.getOldArmorPiece().getType() != Material.AIR){
			if(!OldItem.getItemMeta().hasLore())return;
			for(String lore : OldItem.getItemMeta().getLore()){
				if(lore.equals(Api.color("&7Springs I"))){
					player.removePotionEffect(PotionEffectType.JUMP);
					return;
				}
				if(lore.equals(Api.color("&7Springs II"))){
					player.removePotionEffect(PotionEffectType.JUMP);
					return;
				}
				if(lore.equals(Api.color("&7Springs III"))){
					player.removePotionEffect(PotionEffectType.JUMP);
					return;
				}
			}
		}
	}
}