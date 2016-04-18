package me.BadBones69.CrazyEnchantments.Enchantments.Armor;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class SelfDestruct implements Listener{
	@EventHandler
 	public void onDeath(PlayerDeathEvent e){
		Player player = e.getEntity();
		for(ItemStack item : player.getEquipment().getArmorContents()){
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasLore()){
					for(String l : item.getItemMeta().getLore()){
						if(l.contains("SelfDestruct")){
							 Location loc = e.getEntity().getLocation();
							 loc.getWorld().createExplosion(loc, 1.0F);
							 return;
						}
					}
				}
			}
		}
	}
}