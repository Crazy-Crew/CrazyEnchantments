package me.badbones69.crazyenchantments.multisupport;

import org.bukkit.entity.Player;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;

public class NoCheatPlusSupport {
	
	public static void exemptPlayer(Player player){
		NCPExemptionManager.exemptPermanently(player, CheckType.BLOCKBREAK);
		NCPExemptionManager.exemptPermanently(player, CheckType.BLOCKBREAK_FASTBREAK);
	}
	
	public static void unexemptPlayer(Player player){
		NCPExemptionManager.unexempt(player, CheckType.BLOCKBREAK);
		NCPExemptionManager.unexempt(player, CheckType.BLOCKBREAK_FASTBREAK);
	}
	
}
