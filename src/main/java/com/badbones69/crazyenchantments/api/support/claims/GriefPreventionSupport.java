package com.badbones69.crazyenchantments.api.support.claims;

import com.badbones69.crazyenchantments.api.support.interfaces.claims.FactionsVersion;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class GriefPreventionSupport implements FactionsVersion {

    @Override
    public boolean isFriendly(Player player, Player other) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, null);

        return claim != null && claim.allowAccess(other) == null;
    }

    @Override
    public boolean inTerritory(Player player) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, null);

        return claim != null && (claim.getOwnerName().equalsIgnoreCase(player.getName()) || claim.allowAccess(player) == null);
    }

    @Override
    public boolean canBreakBlock(Player player, Block block) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(block.getLocation(), true, null);

        return claim == null || claim.allowEdit(player) == null;
    }
}