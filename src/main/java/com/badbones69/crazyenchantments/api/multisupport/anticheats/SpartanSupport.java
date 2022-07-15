package com.badbones69.crazyenchantments.api.multisupport.anticheats;

import me.vagdedes.spartan.api.API;
import me.vagdedes.spartan.system.Enums.HackType;
import org.bukkit.entity.Player;

public class SpartanSupport {

    public static void cancelFastBreak(Player player) {
        API.cancelCheck(player, HackType.FastBreak, 40);
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

    public static void cancelNoFall(Player player) {
        API.cancelCheck(player, HackType.NoFall, 80);
    }

    public static void cancelNormalMovements(Player player) {
        API.cancelCheck(player, HackType.IrregularMovements, 80);
    }

}