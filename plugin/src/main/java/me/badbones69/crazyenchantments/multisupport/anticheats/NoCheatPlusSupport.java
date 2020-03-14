package me.badbones69.crazyenchantments.multisupport.anticheats;

import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import org.bukkit.entity.Player;

public class NoCheatPlusSupport {
    
    public static void exemptPlayer(Player player) {
        NCPExemptionManager.exemptPermanently(player);
    }
    
    public static void unexemptPlayer(Player player) {
        NCPExemptionManager.unexempt(player);
    }
    
}