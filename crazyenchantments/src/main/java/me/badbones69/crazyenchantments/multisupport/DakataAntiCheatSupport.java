package me.badbones69.crazyenchantments.multisupport;

import DAKATA.CheatType;
import DAKATA.PlayerCheatEvent;
import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DakataAntiCheatSupport implements Listener {

	@EventHandler
	public void onCheatDetect(PlayerCheatEvent e) {
		Player player = e.getPlayer();
		ItemStack item = Methods.getItemInHand(player);
		CheatType cheatType = e.getCheatType();
		if(cheatType == CheatType.AUTOCLICKER || cheatType == CheatType.INVALIDBLOCK_BREAK || cheatType == CheatType.NOBREAKDELAY || cheatType == CheatType.REACH_BLOCK) {
			if(item != null) {
				if(Main.CE.hasEnchantments(item) || Main.CustomE.hasEnchantments(item)) {
					e.setCancelled(true);
				}
			}
		}
	}

}