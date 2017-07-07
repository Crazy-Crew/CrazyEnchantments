package me.badbones69.crazyenchantments.multisupport;

import org.bukkit.entity.Player;

import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;

public class NoCheatPlusSupport {
	
	public static void exemptPlayer(Player player){
		NCPExemptionManager.exemptPermanently(player);
	}
	
	public static void unexemptPlayer(Player player){
		NCPExemptionManager.unexempt(player);
	}
	
}