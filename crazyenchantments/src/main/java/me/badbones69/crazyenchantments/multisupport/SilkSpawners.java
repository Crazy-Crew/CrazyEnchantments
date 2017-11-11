package me.badbones69.crazyenchantments.multisupport;

import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import de.dustplanet.util.SilkUtil;
import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class SilkSpawners implements Listener {

	private SilkUtil su = SilkUtil.hookIntoSilkSpanwers();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(SilkSpawnersSpawnerBreakEvent e) {
		if(e.isCancelled()) return;
		Player player = e.getPlayer();
		Block block = e.getBlock();
		if(player != null) {
			if(block != null) {
				if(player.getGameMode() != GameMode.CREATIVE) {
					ItemStack item = Methods.getItemInHand(player);
					if(Main.CE.hasEnchantments(item)) {
						if(Main.CE.hasEnchantment(item, CEnchantments.TELEPATHY)) {
							if(CEnchantments.TELEPATHY.isEnabled()) {
								String mobName = su.getCreatureName(e.getEntityID()).toLowerCase().replace(" ", "");
								if(player.hasPermission("silkspawners.silkdrop." + mobName)) {
									EnchantmentUseEvent useEnchant = new EnchantmentUseEvent(player, CEnchantments.TELEPATHY, item);
									Bukkit.getPluginManager().callEvent(useEnchant);
									if(useEnchant.isCancelled()) {
										return;
									}
									ItemStack it = su.newSpawnerItem(e.getEntityID(), su.getCustomSpawnerName(su.getCreatureName(e.getEntityID())), 1, false);
									if(!Methods.isInvFull(player)) {
										player.getInventory().addItem(it);
									}else {
										block.getWorld().dropItemNaturally(block.getLocation(), it);
									}
									block.setType(Material.AIR);
									e.setCancelled(true);
								}
							}
						}
					}
				}
			}
		}
	}

}