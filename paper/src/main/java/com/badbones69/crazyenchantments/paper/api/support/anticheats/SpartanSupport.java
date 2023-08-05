package com.badbones69.crazyenchantments.paper.api.support.anticheats;

import me.vagdedes.spartan.api.API;
import me.vagdedes.spartan.system.Enums.HackType;
import org.bukkit.entity.Player;

public class SpartanSupport {

    public void cancelFastBreak(Player player) {
        API.cancelCheck(player, HackType.FastBreak, 40);
    }

    public void cancelNoSwing(Player player) {
        API.cancelCheck(player, HackType.NoSwing, 40);
    }

    public void cancelBlockReach(Player player) {
        API.cancelCheck(player, HackType.BlockReach, 40);
    }

    public void cancelFastEat(Player player) {
        API.cancelCheck(player, HackType.FastEat, 40);
    }

    public void cancelSpeed(Player player) {
        API.cancelCheck(player, HackType.Speed, 80);
    }

    public void cancelFly(Player player) {
        API.cancelCheck(player, HackType.IrregularMovements, 80);
    }

    public void cancelClip(Player player) {
        API.cancelCheck(player, HackType.IrregularMovements, 80);
    }

    public void cancelNoFall(Player player) {
        API.cancelCheck(player, HackType.NoFall, 80);
    }

    public void cancelNormalMovements(Player player) {
        API.cancelCheck(player, HackType.IrregularMovements, 80);
    }
}