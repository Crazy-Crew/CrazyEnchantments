package com.badbones69.crazyenchantments.api.multisupport.anticheats;

import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import org.bukkit.entity.Player;

public class NoCheatPlusSupport {

    public static void allowPlayer(Player player) {
        NCPExemptionManager.exemptPermanently(player.getUniqueId());
    }

    public static void denyPlayer(Player player) {
        NCPExemptionManager.unexempt(player.getUniqueId());
    }
}
