package me.badbones69.crazyenchantments.controlers;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandChecker implements Listener {
	
	private CrazyEnchantments ce = CrazyEnchantments.getInstance();
	
	@EventHandler
	public void onInventoryClear(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		if(e.getMessage().toLowerCase().equalsIgnoreCase("/ci") || e.getMessage().toLowerCase().equalsIgnoreCase("/clear") || e.getMessage().toLowerCase().equalsIgnoreCase("/cearinventory")) {
			for(CEnchantments ench : ce.getEnchantmentPotions().keySet()) {
				if(ench.isEnabled()) {
					for(ItemStack armor : player.getEquipment().getArmorContents()) {
						if(armor != null) {
							for(PotionEffectType type : ce.getUpdatedEffects(player, new ItemStack(Material.AIR), new ItemStack(Material.AIR), ench).keySet()) {
								player.removePotionEffect(type);
							}
						}
					}
				}
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					ce.updatePlayerEffects(player);
				}
			}.runTaskLater(ce.getPlugin(), 5);
		}else if(e.getMessage().toLowerCase().equalsIgnoreCase("/heal")) {
			new BukkitRunnable() {
				@Override
				public void run() {
					ce.updatePlayerEffects(player);
				}
			}.runTaskLater(ce.getPlugin(), 5);
		}
	}
	
}