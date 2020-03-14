package me.badbones69.crazyenchantments.multisupport.anticheats;

import DAKATA.CheatType;
import DAKATA.PlayerCheatEvent;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class DakataAntiCheatSupport implements Listener {
    
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    @EventHandler
    public void onCheatDetect(PlayerCheatEvent e) {
        Player player = e.getPlayer();
        ItemStack item = Methods.getItemInHand(player);
        CheatType cheatType = e.getCheatType();
        if (item != null && (cheatType == CheatType.AUTOCLICKER || cheatType == CheatType.INVALIDBLOCK_BREAK || cheatType == CheatType.NOBREAKDELAY || cheatType == CheatType.REACH_BLOCK) && ce.hasEnchantments(item)) {
            e.setCancelled(true);
        }
    }
    
}