package me.badbones69.crazyenchantments.controlers;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.api.CEnchantments;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandChecker implements Listener {

	@EventHandler
	public void onInventoryClear(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		if(e.getMessage().toLowerCase().equalsIgnoreCase("/ci") || e.getMessage().toLowerCase().equalsIgnoreCase("/clear") || e.getMessage().toLowerCase().equalsIgnoreCase("/cearinventory")) {
			for(CEnchantments ench : Main.CE.getEnchantmentPotions().keySet()) {
				if(ench.isEnabled()) {
					for(ItemStack armor : player.getEquipment().getArmorContents()) {
						if(armor != null) {
							for(PotionEffectType type : Main.CE.getUpdatedEffects(player, new ItemStack(Material.AIR), new ItemStack(Material.AIR), ench).keySet()) {
								player.removePotionEffect(type);
							}
						}
					}
				}
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					Main.CE.updatePlayerEffects(player);
				}
			}.runTaskLater(Main.CE.getPlugin(), 5);
		}else if(e.getMessage().toLowerCase().equalsIgnoreCase("/heal")) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Main.CE.updatePlayerEffects(player);
				}
			}.runTaskLater(Main.CE.getPlugin(), 5);
		}
	}

}