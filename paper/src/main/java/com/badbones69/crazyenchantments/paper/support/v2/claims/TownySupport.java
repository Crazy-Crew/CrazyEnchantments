package com.badbones69.crazyenchantments.paper.support.v2.claims;

import com.badbones69.crazyenchantments.paper.support.v2.enums.PluginType;
import com.badbones69.crazyenchantments.paper.support.v2.interfaces.TerritorySupport;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class TownySupport extends TerritorySupport<BlockState, Location> {

    private final TownyAPI api = TownyAPI.getInstance();

    @Override
    public PluginType getPluginType() {
        return PluginType.DEFAULT;
    }

    @Override
    public String getPluginName() {
        return "Towny";
    }

    @Override
    public boolean canBreakBlock(final Player player, final BlockState blockState) {
        if (!isPluginReady()) {
            return true;
        }

        final Location location = blockState.getLocation();

        final TownBlock town = this.api.getTownBlock(location);

        if (town == null || !town.hasTown()) {
            return true;
        }

        final Resident resident = this.api.getResident(player.getUniqueId());

        if (resident == null || !resident.hasTown()) {
            return false;
        }

        final Material type = blockState.getType();

        if (type.isAir()) {
            return false;
        }

        return PlayerCacheUtil.getCachePermission(
                player,
                location,
                type,
                TownyPermission.ActionType.DESTROY
        );
    }

    @Override
    public boolean canPlaceBlock(final Player player, final BlockState blockState) {
        if (!isPluginReady()) {
            return true;
        }

        final Location location = blockState.getLocation();

        final TownBlock town = this.api.getTownBlock(location);

        if (town == null || !town.hasTown()) {
            return true;
        }

        final Resident resident = this.api.getResident(player.getUniqueId());

        if (resident == null || !resident.hasTown()) {
            return false;
        }

        final Material type = blockState.getType();

        if (type.isAir()) {
            return false;
        }

        return PlayerCacheUtil.getCachePermission(
                player,
                location,
                type,
                TownyPermission.ActionType.BUILD
        );
    }

    @Override
    public boolean canInteract(final Player player, final BlockState blockState) {
        if (!isPluginReady()) {
            return true;
        }

        final Location location = blockState.getLocation();

        final TownBlock town = this.api.getTownBlock(location);

        if (town == null || !town.hasTown()) {
            return true;
        }

        final Resident resident = this.api.getResident(player.getUniqueId());

        if (resident == null || !resident.hasTown()) {
            return false;
        }

        final Material type = blockState.getType();

        if (type.isAir()) {
            return false;
        }

        return PlayerCacheUtil.getCachePermission(
                player,
                location,
                type,
                TownyPermission.ActionType.ITEM_USE
        );
    }

    @Override
    public boolean isCombatEnabled(final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final TownBlock town = this.api.getTownBlock(location);

        if (town == null || !town.hasTown()) {
            return true;
        }

        return !CombatUtil.preventPvP(town.getWorld(), town);
    }

    @Override
    public boolean isFriendly(final Entity player, final Entity target) {
        if (!isPluginReady()) {
            return false;
        }

        return CombatUtil.isAlly(this.api.getResident(player.getUniqueId()), this.api.getResident(target.getUniqueId()));
    }

    @Override
    public boolean isTerritory(final Player player, final Location location) {
        final TownBlock town = this.api.getTownBlock(location);

        if (town == null || !town.hasTown()) {
            return true;
        }

        final Resident resident = this.api.getResident(player.getUniqueId());

        if (resident == null || !resident.hasTown()) {
            return false;
        }

        try {
            return resident.getTown().getUUID().equals(town.getTown().getUUID());
        } catch (final Exception exception) {
            return false;
        }
    }

    @Override
    public boolean isTerritory(final Player player) {
        return isTerritory(player, player.getLocation());
    }
}