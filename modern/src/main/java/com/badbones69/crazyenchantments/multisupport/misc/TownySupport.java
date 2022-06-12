package com.badbones69.crazyenchantments.multisupport.misc;

import com.badbones69.crazyenchantments.multisupport.factions.FactionsVersion;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class TownySupport implements FactionsVersion {

    public boolean isFriendly(Player player, Player other) {
        return CombatUtil.preventDamageCall(player, other, DamageCause.ENTITY_ATTACK);
    }

    public boolean inTerritory(Player player) {
        TownyAPI api = TownyAPI.getInstance();

        if (api == null) return true;

        TownBlock block = api.getTownBlock(player.getLocation());

        Resident resident = api.getResident(player.getName());

        try {
            if (block != null && block.hasTown() && resident.hasTown() && resident.getTown().equals(block.getTown())) return true;
        } catch (NotRegisteredException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public boolean canBreakBlock(Player player, Block block) {
        return true;
    }

    public static boolean allowsCombat(Location location) {

        try {
            TownBlock block = TownyAPI.getInstance().getTownBlock(location);
            if (block != null && block.hasTown() && !block.getTown().isPVP()) return false;
        } catch (NotRegisteredException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
