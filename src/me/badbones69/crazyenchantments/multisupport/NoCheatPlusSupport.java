package me.badbones69.crazyenchantments.multisupport;

import org.bukkit.entity.Player;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;

public class NoCheatPlusSupport {
	
	public static void exemptPlayer(Player player){
		NCPExemptionManager.exemptPermanently(player, CheckType.BLOCKBREAK);
		NCPExemptionManager.exemptPermanently(player, CheckType.BLOCKBREAK_REACH);
		NCPExemptionManager.exemptPermanently(player, CheckType.BLOCKBREAK_NOSWING);
		NCPExemptionManager.exemptPermanently(player, CheckType.BLOCKINTERACT_SPEED);
		NCPExemptionManager.exemptPermanently(player, CheckType.BLOCKBREAK_FASTBREAK);
		NCPExemptionManager.exemptPermanently(player, CheckType.BLOCKBREAK_WRONGBLOCK);
	}
	
	public static void unexemptPlayer(Player player){
		NCPExemptionManager.unexempt(player, CheckType.BLOCKBREAK);
		NCPExemptionManager.unexempt(player, CheckType.BLOCKBREAK_REACH);
		NCPExemptionManager.unexempt(player, CheckType.BLOCKBREAK_NOSWING);
		NCPExemptionManager.unexempt(player, CheckType.BLOCKINTERACT_SPEED);
		NCPExemptionManager.unexempt(player, CheckType.BLOCKBREAK_FASTBREAK);
		NCPExemptionManager.unexempt(player, CheckType.BLOCKBREAK_WRONGBLOCK);
	}
	
}