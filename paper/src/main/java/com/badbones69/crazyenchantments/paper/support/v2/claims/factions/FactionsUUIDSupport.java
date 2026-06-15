package com.badbones69.crazyenchantments.paper.support.v2.claims.factions;

import com.badbones69.crazyenchantments.paper.support.v2.interfaces.factions.FactionsSupport;
import dev.kitteh.factions.FLocation;
import dev.kitteh.factions.FPlayer;
import dev.kitteh.factions.FPlayers;
import dev.kitteh.factions.Faction;
import dev.kitteh.factions.Factions;
import dev.kitteh.factions.permissible.PermissibleActions;
import dev.kitteh.factions.permissible.Relation;
import dev.kitteh.factions.permissible.Role;
import dev.kitteh.factions.protection.Protection;
import dev.kitteh.factions.util.WorldUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class FactionsUUIDSupport extends FactionsSupport<FPlayer, Faction, BlockState, Location> {

    private final FPlayers instance = FPlayers.fPlayers();
    private final Factions factions = Factions.factions();

    @Override
    public String getPluginName() {
        return "Factions";
    }

    // faction unique methods
    @Override
    public boolean isWilderness(final FPlayer player) {
        if (!isPluginReady()) {
            return true;
        }

        return player.faction().isWilderness();
    }

    @Override
    public boolean isSafezone(final FPlayer player) {
        if (!isPluginReady()) {
            return false;
        }

        return player.faction().isSafeZone();
    }

    @Override
    public boolean isWarzone(final FPlayer player) {
        if (!isPluginReady()) {
            return false;
        }

        return player.faction().isWarZone();
    }

    @Override
    public boolean isFactionMember(final Faction faction, final Player player) {
        if (!isPluginReady()) {
            return false;
        }

        return !WorldUtil.isEnabled(player.getWorld()) || this.instance.get(player.getUniqueId()).faction().id() == faction.id();
    }

    // generic methods
    @Override
    public boolean isProtected(final Location location) {
        if (!isPluginReady()) {
            return false;
        }

        return WorldUtil.isEnabled(location) && !new FLocation(location).faction().isWilderness();
    }

    @Override
    public boolean canBreakBlock(final Player player, final BlockState blockState) {
        if (!isPluginReady()) {
            return true;
        }

        final World world = blockState.getWorld();

        return !WorldUtil.isEnabled(world) || !Protection.denyBuildOrDestroyBlock(
                player,
                blockState.getLocation(),
                PermissibleActions.DESTROY,
                false
        );
    }

    @Override
    public boolean canPlaceBlock(final Player player, final BlockState blockState) {
        if (!isPluginReady()) {
            return true;
        }

        final World world = blockState.getWorld();

        return !WorldUtil.isEnabled(world) || !Protection.denyBuildOrDestroyBlock(
                player,
                blockState.getLocation(),
                PermissibleActions.BUILD,
                false
        );
    }

    @Override
    public boolean canExplodeBlock(final Entity entity, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final Block block = location.getBlock();
        final BlockState blockState = block.getState(true);

        return !WorldUtil.isEnabled(blockState.getWorld()) || !Protection.denyExplode(entity, new FLocation(blockState.getLocation()));
    }

    @Override
    public boolean canInteract(final Player player, final BlockState blockState) {
        if (!isPluginReady()) {
            return true;
        }

        return !WorldUtil.isEnabled(blockState.getWorld()) || !Protection.denyUseBlock(player, blockState.getType(), blockState.getLocation(), false);
    }

    @Override
    public boolean canInteract(final Player player, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        return !WorldUtil.isEnabled(location) || !Protection.denyInteract(player, location, false);
    }

    @Override
    public boolean isFriendly(final Entity damager, final Entity target) {
        if (!isPluginReady() ||
                !(damager instanceof Player player) ||
                !(target instanceof Player receiver) ||
                !WorldUtil.isEnabled(damager.getWorld())
        ) {
            return false;
        }

        final FPlayer source = this.instance.get(player.getUniqueId());

        final FPlayer output = this.instance.get(receiver.getUniqueId());

        final Faction sourceFaction = source.faction();
        final Faction outputFaction = output.faction();

        if (sourceFaction.isWilderness() || outputFaction.isWilderness()) {
            return false;
        }

        return outputFaction.relationWish(sourceFaction).isAtLeast(Relation.ALLY) || outputFaction.relationWish(sourceFaction).isAtLeast(Relation.MEMBER);
    }

    @Override
    public boolean isTerritory(final Player player, final Location location) {
        return isTerritory(player);
    }

    @Override
    public boolean isTerritory(final Player player) {
        if (!isPluginReady()) {
            return false;
        }

        return !WorldUtil.isEnabled(player.getWorld()) || this.instance.get(player.getUniqueId()).isInOwnTerritory();
    }

    @Override
    public boolean isCombatEnabled(final Location location) {
        if (!isPluginReady() || !WorldUtil.isEnabled(location)) {
            return true;
        }

        final Faction faction = this.factions.getAt(location);

        if (faction.isSafeZone()) {
            return false;
        }

        return faction.isWarZone() || !faction.noPvPInTerritory();
    }

    @Override
    public boolean isMember(final Player player) {
        if (!isPluginReady() || !WorldUtil.isEnabled(player.getWorld())) {
            return false;
        }

        final Faction faction = this.factions.getAt(player.getLocation());

        if (faction.isWilderness() || faction.isWarZone() || faction.isSafeZone()) {
            return false;
        }

        return isFactionMember(faction, player);
    }

    @Override
    public boolean isOwner(final Player player) {
        if (!isPluginReady() || !WorldUtil.isEnabled(player.getWorld())) {
            return false;
        }

        final FPlayer fPlayer = this.instance.get(player.getUniqueId());

        if (isWilderness(fPlayer) || isWarzone(fPlayer) || isSafezone(fPlayer)) {
            return false;
        }

        return fPlayer.faction().defaultRole().isAtLeast(Role.ADMIN);
    }
}