package me.BadBones69.CrazyEnchantments.Enchantments.Armor;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class SelfDestruct implements Listener{
	@EventHandler
 	public void onDeath(PlayerDeathEvent e){
		if(!Api.isEnchantmentEnabled("SelfDestruct"))return;
		Player player = e.getEntity();
		if(!Api.allowsPVP(player))return;
		for(ItemStack item : player.getEquipment().getArmorContents()){
			if(item==null)return;
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasLore()){
					for(String l : item.getItemMeta().getLore()){
						if(l.contains(Api.getEnchName("SelfDestruct"))){
							 Location loc = e.getEntity().getLocation();
							 loc.getWorld().createExplosion(loc, Api.getPower(l, Api.getEnchName("SelfDestruct")));
							 return;
						}
					}
				}
			}
		}
	}
}