package me.badbones69.crazyenchantments.multisupport;

import me.vagdedes.spartan.api.API;
import me.vagdedes.spartan.system.Enums.HackType;
import org.bukkit.entity.Player;

public class SpartanSupport {

	public static void cancelNucker(Player player) {
		API.cancelCheck(player, HackType.Nuker, 40);
	}

	public static void cancelNoSwing(Player player) {
		API.cancelCheck(player, HackType.NoSwing, 40);
	}

	public static void cancelBlockReach(Player player) {
		API.cancelCheck(player, HackType.BlockReach, 40);
	}

	public static void cancelFastEat(Player player) {
		API.cancelCheck(player, HackType.FastEat, 40);
	}

	public static void cancelSpeed(Player player) {
		API.cancelCheck(player, HackType.Speed, 80);
	}

	public static void cancelFly(Player player) {
		API.cancelCheck(player, HackType.Fly, 80);
	}

	public static void cancelClip(Player player) {
		API.cancelCheck(player, HackType.Clip, 80);
	}

	public static void cancelJesus(Player player) {
		API.cancelCheck(player, HackType.Jesus, 80);
	}

	public static void cancelNoFall(Player player) {
		API.cancelCheck(player, HackType.NoFall, 80);
	}

	public static void cancelNormalMovements(Player player) {
		API.cancelCheck(player, HackType.IrregularMovements, 80);
	}

}