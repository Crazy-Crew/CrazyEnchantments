package com.badbones69.crazyenchantments.paper.support.anticheats;

import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import org.bukkit.entity.Player;

public class NoCheatPlusSupport {

    public void allowPlayer(Player player) {
        NCPExemptionManager.exemptPermanently(player.getUniqueId());
    }

    public void denyPlayer(Player player) {
        NCPExemptionManager.unexempt(player.getUniqueId());
    }
}