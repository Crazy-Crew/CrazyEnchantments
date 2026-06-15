package com.badbones69.crazyenchantments.paper.support.claims;

import com.badbones69.crazyenchantments.paper.support.enums.PluginType;
import com.badbones69.crazyenchantments.paper.support.interfaces.TerritorySupport;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import java.util.UUID;

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
    public boolean isProtected(final Location location) {
        if (!isPluginReady()) {
            return false;
        }

        return this.api.getTown(location) != null;
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
    public boolean canInteract(final Player player, final Location location) {
        return canInteract(player, location.getBlock().getState(false));
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

    @Override
    public boolean isFriendly(final Entity player, final Entity target) {
        if (!isPluginReady()) {
            return false;
        }

        return CombatUtil.isAlly(this.api.getResident(player.getUniqueId()), this.api.getResident(target.getUniqueId()));
    }

    @Override
    public boolean isMember(final Player player) {
        if (!isPluginReady()) {
            return false;
        }

        final Location location = player.getLocation();

        final Town town = this.api.getTown(location);

        if (town == null) {
            return false;
        }

        return town.hasResident(player.getUniqueId());
    }

    @Override
    public boolean isOwner(final Player player) {
        if (!isPluginReady()) {
            return false;
        }

        final Location location = player.getLocation();
        final UUID uuid = player.getUniqueId();

        if (this.api.isNationZone(location)) {
            final Nation nation = this.api.getNation(uuid);

            if (nation != null && nation.hasKing()) {
                return nation.getKing().getUUID().equals(uuid);
            }

            return false;
        }

        final Town town = this.api.getTown(location);

        if (town == null) {
            return false;
        }

        return town.getMayor().getUUID().equals(uuid);
    }
}