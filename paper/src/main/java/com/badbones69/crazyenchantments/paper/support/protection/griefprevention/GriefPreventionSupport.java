package com.badbones69.crazyenchantments.paper.support.protection.griefprevention;

import com.badbones69.crazyenchantments.paper.support.api.enums.PluginType;
import com.badbones69.crazyenchantments.paper.support.api.interfaces.TerritorySupport;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import java.util.ArrayList;
import java.util.UUID;

@NullMarked
public final class GriefPreventionSupport extends TerritorySupport<Location, Location> {

    private final GriefPrevention instance = GriefPrevention.instance;

    private final DataStore dataStore = this.instance.dataStore;

    @Override
    public PluginType getPluginType() {
        return PluginType.DEFAULT;
    }

    @Override
    public String getPluginName() {
        return "GriefPrevention";
    }

    private final UUID dummy = new UUID(0, 0);

    @Override
    public boolean isProtected(final Location location) {
        if (!isPluginReady()) {
            return false;
        }

        final Claim claim = this.dataStore.getClaimAt(location, true, null);

        if (claim == null) {
            return false;
        }

        return claim.checkPermission(this.dummy, ClaimPermission.Build, null) != null;
    }

    @Override
    public boolean canBreakBlock(final Player player, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final Claim claim = this.dataStore.getClaimAt(location, true, null);

        if (claim == null) {
            return true;
        }

        final UUID uuid = player.getUniqueId();

        return claim.hasExplicitPermission(uuid, ClaimPermission.Build) || claim.hasExplicitPermission(uuid, ClaimPermission.Edit);
    }

    @Override
    public boolean canPlaceBlock(final Player player, final Location location) {
        return canBreakBlock(player, location);
    }

    @Override
    public boolean isFriendly(final Entity damager, final Entity target) {
        if (!isPluginReady()) {
            return false;
        }

        final Claim claim = this.dataStore.getClaimAt(target.getLocation(), true, null);

        if (claim == null) {
            return false;
        }

        return !claim.hasExplicitPermission(damager.getUniqueId(), ClaimPermission.Inventory) && !claim.hasExplicitPermission(target.getUniqueId(), ClaimPermission.Inventory);
    }

    @Override
    public boolean isTerritory(final Player player) {
        if (!isPluginReady()) {
            return true;
        }

        final Claim claim = this.dataStore.getClaimAt(player.getLocation(), true, null);

        if (claim == null) {
            return true;
        }

        final ArrayList<String> users = new ArrayList<>();

        claim.getPermissions(users, new ArrayList<>(), new ArrayList<>(), users);

        return users.contains(player.getUniqueId().toString());
    }

    @Override
    public boolean isMember(final Player player) {
        return isTerritory(player);
    }

    @Override
    public boolean isOwner(final Player player) {
        if (!isPluginReady()) {
            return false;
        }

        final Claim claim = this.dataStore.getClaimAt(player.getLocation(), true, null);

        if (claim == null) {
            return false;
        }

        return claim.ownerID.equals(player.getUniqueId());
    }
}