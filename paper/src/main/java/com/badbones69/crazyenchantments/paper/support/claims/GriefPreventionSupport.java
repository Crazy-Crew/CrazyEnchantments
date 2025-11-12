package com.badbones69.crazyenchantments.paper.support.claims;

import com.badbones69.crazyenchantments.paper.support.interfaces.claims.ClaimSupport;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GriefPreventionSupport implements ClaimSupport {

    @Override
    public boolean isFriendly(@NotNull final Player player, @NotNull final Player other) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, null);

        return claim != null && claim.hasExplicitPermission(player.getUniqueId(), ClaimPermission.Access);
    }

    @Override
    public boolean inTerritory(@NotNull final Player player) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), true, null);

        return claim != null && (claim.getOwnerID().equals(player.getUniqueId()) || claim.hasExplicitPermission(player.getUniqueId(), ClaimPermission.Access));
    }

    @Override
    public boolean canBreakBlock(@NotNull final Player player, @NotNull final Block block) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(block.getLocation(), true, null);

        return claim == null || claim.hasExplicitPermission(player.getUniqueId(), ClaimPermission.Build) || claim.hasExplicitPermission(player.getUniqueId(), ClaimPermission.Edit);
    }
}