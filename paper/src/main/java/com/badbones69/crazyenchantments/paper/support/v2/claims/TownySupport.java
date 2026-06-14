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
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public final class TownySupport extends TerritorySupport<Block, Location> {

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
    public boolean canBreakBlock(@NonNull final Player player, @NonNull final Block container) {
        if (!isPluginReady()) {
            return true;
        }

        final Location location = container.getLocation();

        final TownBlock town = this.api.getTownBlock(location);

        if (town == null || !town.hasTown()) {
            return true;
        }

        final Resident resident = this.api.getResident(player.getUniqueId());

        if (resident == null || !resident.hasTown()) {
            return false;
        }

        final BlockState state = container.getState(true);
        final Material type = state.getType();

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
    public boolean isCombatEnabled(@NonNull final Location container) {
        if (!isPluginReady()) {
            return true;
        }

        final TownBlock town = this.api.getTownBlock(container);

        if (town == null || !town.hasTown()) {
          return true;
        }

        return !CombatUtil.preventPvP(town.getWorld(), town);
    }

    @Override
    public boolean isTerritory(@NonNull final Player player, @NonNull final Location container) {
        final TownBlock town = this.api.getTownBlock(container);

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
    public boolean isTerritory(@NonNull final Player player) {
        return isTerritory(player, player.getLocation());
    }

    @Override
    public boolean isFriendly(@NonNull final Entity player, @NonNull final Entity target) {
        if (!isPluginReady()) {
            return false;
        }

        return CombatUtil.isAlly(this.api.getResident(player.getUniqueId()), this.api.getResident(target.getUniqueId()));
    }
}