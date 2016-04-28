package me.BadBones69.CrazyEnchantments.Enchantments.Swords;

import me.BadBones69.CrazyEnchantments.Api;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Headless implements Listener{
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerDamage(PlayerDeathEvent e){
		if(!Api.isEnchantmentEnabled("Headless"))return;
		if(e.getEntity().getKiller() instanceof Player){
			Player damager = (Player) e.getEntity().getKiller();
			Player player = e.getEntity();
			if(Api.getItemInHand(damager).hasItemMeta()){
				ItemStack head = new ItemStack(397, 1, (short)3);
				SkullMeta m = (SkullMeta) head.getItemMeta();
				for(String lore : Api.getItemInHand(damager).getItemMeta().getLore()){
					if(lore.contains(Api.getEnchName("Headless"))){
						if(Api.randomPicker(7-Api.getPower(lore, Api.getEnchName("Headless")))){
							m.setOwner(player.getName());
							head.setItemMeta(m);
							e.getDrops().add(head);
						}
					}
				}
			}
		}
	}
}