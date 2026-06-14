package com.badbones69.crazyenchantments.paper.support.v2.claims.skyblock;

import com.badbones69.crazyenchantments.paper.support.v2.interfaces.TerritorySupport;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public final class SuperiorSkyBlockSupport extends TerritorySupport<Block, Location> {

    @Override
    public String getPluginName() {
        return "SuperiorSkyblock2";
    }

    @Override
    public boolean isTerritory(@NonNull final Player player, @NonNull final Location container) {
        if (!isPluginReady()) {
            return false;
        }

        final SuperiorPlayer superior = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());

        if (superior == null) return false;

        return superior.isInsideIsland();
    }

    @Override
    public boolean isTerritory(@NonNull final Player player) {
        return isTerritory(player, player.getLocation());
    }

    @Override
    public boolean isFriendly(@NonNull final Player player, @NonNull final Entity target) {
        if (!isPluginReady()) {
            return false;
        }

        final SuperiorPlayer origin = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());

        if (origin == null) {
            return false;
        }

        final SuperiorPlayer other = SuperiorSkyblockAPI.getPlayer(target.getUniqueId());

        if (other == null) {
            return false;
        }

        final Island island = origin.getIsland();

        if (island == null) {
            return false;
        }

        return island.isMember(other) || island.isVisitor(other, true);
    }
}