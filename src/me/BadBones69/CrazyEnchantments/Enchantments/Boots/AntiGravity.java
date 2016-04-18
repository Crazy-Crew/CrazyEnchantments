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

public class AntiGravity implements Listener{
	@EventHandler
 	public void onEquip(ArmorEquipEvent e){
		Player player = e.getPlayer();
		ItemStack NewItem = e.getNewArmorPiece();
		ItemStack OldItem = e.getOldArmorPiece();
		if(e.getNewArmorPiece() != null && e.getNewArmorPiece().hasItemMeta() && e.getNewArmorPiece().getType() != Material.AIR){
			if(!NewItem.getItemMeta().hasLore())return;
			for(String lore : NewItem.getItemMeta().getLore()){
				if(lore.equals(Api.color("&7AntiGravity I"))){
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 55555*20, 4));
					return;
				}
				if(lore.equals(Api.color("&7AntiGravity II"))){
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 55555*20, 7));
					return;
				}
				if(lore.equals(Api.color("&7AntiGravity III"))){
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 55555*20, 10));
					return;
				}
			}
		}
		if(e.getOldArmorPiece() != null && e.getOldArmorPiece().hasItemMeta() && e.getOldArmorPiece().getType() != Material.AIR){
			if(!OldItem.getItemMeta().hasLore())return;
			for(String lore : OldItem.getItemMeta().getLore()){
				if(lore.equals(Api.color("&7AntiGravity I"))){
					player.removePotionEffect(PotionEffectType.JUMP);
					return;
				}
				if(lore.equals(Api.color("&7AntiGravity II"))){
					player.removePotionEffect(PotionEffectType.JUMP);
					return;
				}
				if(lore.equals(Api.color("&7AntiGravity III"))){
					player.removePotionEffect(PotionEffectType.JUMP);
					return;
				}
			}
		}
	}
}