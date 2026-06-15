package com.badbones69.crazyenchantments.paper.support.v2.claims.skyblock;

import com.badbones69.crazyenchantments.paper.support.v2.enums.PluginType;
import com.badbones69.crazyenchantments.paper.support.v2.interfaces.TerritorySupport;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public final class SuperiorSkyBlockSupport extends TerritorySupport<BlockState, Location> {

    @Override
    public PluginType getPluginType() {
        return PluginType.DEFAULT;
    }

    @Override
    public String getPluginName() {
        return "SuperiorSkyblock2";
    }

    @Override
    public boolean isProtected(final Location location) {
        if (!isPluginReady()) {
            return false;
        }

        return SuperiorSkyblockAPI.getIslandAt(location) != null;
    }

    @Override
    public boolean canBreakBlock(final Player player, final BlockState blockState) {
        if (!isPluginReady()) {
            return true;
        }

        final Island island = SuperiorSkyblockAPI.getIslandAt(blockState.getLocation());

        if (island == null) {
            return true;
        }

        return island.hasPermission(player, IslandPrivilege.getByName("BREAK"));
    }

    @Override
    public boolean canPlaceBlock(final Player player, final BlockState blockState) {
        if (!isPluginReady()) {
            return true;
        }

        final Island island = SuperiorSkyblockAPI.getIslandAt(blockState.getLocation());

        if (island == null) {
            return true;
        }

        return island.hasPermission(player, IslandPrivilege.getByName("BUILD"));
    }

    @Override
    public boolean canInteract(final Player player, final BlockState blockState) {
        if (!isPluginReady()) {
            return true;
        }

        final Island island = SuperiorSkyblockAPI.getIslandAt(blockState.getLocation());

        if (island == null) {
            return true;
        }

        return island.hasPermission(player, IslandPrivilege.getByName("INTERACT"));
    }

    @Override
    public boolean isFriendly(final Entity player, final Entity target) {
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

        return island.isMember(other) || island.isCoop(other);
    }

    @Override
    public boolean isTerritory(final Player player, final Location location) {
        if (!isPluginReady()) {
            return false;
        }

        final SuperiorPlayer superior = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());

        if (superior == null) {
            return false;
        }

        return superior.isInsideIsland();
    }

    @Override
    public boolean isTerritory(final Player player) {
        return isTerritory(player, player.getLocation());
    }

    @Override
    public boolean isMember(final Player player) {
        if (!isPluginReady()) {
            return false;
        }

        final SuperiorPlayer superior = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());

        if (superior == null) {
            return false;
        }

        final Island island = SuperiorSkyblockAPI.getIslandAt(player.getLocation());

        if (island == null) {
            return false;
        }

        return island.isMember(superior);
    }

    @Override
    public boolean isOwner(final Player player) {
        if (!isPluginReady()) {
            return false;
        }

        final UUID uuid = player.getUniqueId();

        final SuperiorPlayer superior = SuperiorSkyblockAPI.getPlayer(uuid);

        if (superior == null) {
            return false;
        }

        final Island island = superior.getIsland();

        if (island == null) {
            return false;
        }

        return island.getOwner().getUniqueId().equals(uuid);
    }
}