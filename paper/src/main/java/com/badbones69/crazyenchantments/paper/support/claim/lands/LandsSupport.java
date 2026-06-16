package com.badbones69.crazyenchantments.paper.support.claim.lands;

import com.badbones69.crazyenchantments.paper.support.api.enums.PluginType;
import com.badbones69.crazyenchantments.paper.support.api.interfaces.TerritorySupport;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import java.util.UUID;

@NullMarked
public final class LandsSupport extends TerritorySupport<BlockState, Location> {

    private final LandsIntegration api = LandsIntegration.of(this.plugin);

    @Override
    public PluginType getPluginType() {
        return PluginType.DEFAULT;
    }

    @Override
    public String getPluginName() {
        return "Lands";
    }

    @Override
    public boolean isProtected(final Location location) {
        if (!isPluginReady()) {
            return false;
        }

        return this.api.getArea(location) != null;
    }

    @Override
    public boolean canBreakBlock(final Player player, final BlockState blockState) {
        if (!isPluginReady()) {
            return true;
        }

        final Area area = this.api.getArea(blockState.getLocation());

        if (area == null) {
            return true;
        }

        return area.hasRoleFlag(player.getUniqueId(), Flags.BLOCK_BREAK);
    }

    @Override
    public boolean canPlaceBlock(final Player player, final BlockState blockState) {
        if (!isPluginReady()) {
            return true;
        }

        final Area area = this.api.getArea(blockState.getLocation());

        if (area == null) {
            return true;
        }

        return area.hasRoleFlag(player.getUniqueId(), Flags.BLOCK_PLACE);
    }

    @Override
    public boolean canInteract(final Player player, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final Area area = this.api.getArea(location);

        if (area == null) {
            return true;
        }

        return area.hasRoleFlag(player.getUniqueId(), Flags.INTERACT_GENERAL);
    }

    @Override
    public boolean canInteract(final Player player, final BlockState blockState) {
        return canInteract(player, blockState.getLocation());
    }

    @Override
    public boolean canExplodeBlock(final Entity entity, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final Area area = this.api.getArea(location);

        if (area == null) {
            return true;
        }

        return area.hasRoleFlag(entity.getUniqueId(), Flags.BLOCK_IGNITE) || area.hasNaturalFlag(Flags.TNT_GRIEFING);
    }

    @Override
    public boolean canExplodeBlock(final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final Area area = this.api.getArea(location);

        if (area == null) {
            return true;
        }

        return area.hasNaturalFlag(Flags.TNT_GRIEFING);
    }

    @Override
    public boolean isFriendly(final Entity player, final Entity target) {
        if (!isPluginReady()) {
            return false;
        }

        final Land land = this.api.getLandPlayer(player.getUniqueId()).getOwningLand();

        if (land == null) {
            return false;
        }

        return land.isTrusted(target.getUniqueId());
    }

    @Override
    public boolean isTerritory(final Player player, final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final UUID uuid = player.getUniqueId();
        final Chunk chunk = location.getChunk();

        final int x = chunk.getX();
        final int z = chunk.getZ();

        final Land land = this.api.getLandByChunk(chunk.getWorld(), x, z);

        if (land == null) {
            return false;
        }

        return land.getOwnerUID().equals(uuid) || land.isTrusted(uuid);
    }

    @Override
    public boolean isTerritory(final Player player) {
        return isTerritory(player, player.getLocation());
    }

    @Override
    public boolean isCombatEnabled(final Location location) {
        if (!isPluginReady()) {
            return true;
        }

        final Chunk chunk = location.getChunk();

        final int x = chunk.getX();
        final int z = chunk.getZ();

        final Land land = this.api.getLandByChunk(chunk.getWorld(), x, z);

        if (land == null) {
            return true;
        }

        return land.isInWar() || land.isWarField();
    }

    @Override
    public boolean isMember(final Player player) {
        if (!isPluginReady()) {
            return false;
        }

        final Location location = player.getLocation();
        final Chunk chunk = location.getChunk();

        final int x = chunk.getX();
        final int z = chunk.getZ();

        final Land land = this.api.getLandByChunk(chunk.getWorld(), x, z);

        if (land == null) {
            return true;
        }

        return land.isTrusted(player.getUniqueId());
    }

    @Override
    public boolean isOwner(final Player player) {
        if (!isPluginReady()) {
            return false;
        }

        final Location location = player.getLocation();
        final Chunk chunk = location.getChunk();

        final int x = chunk.getX();
        final int z = chunk.getZ();

        final Land land = this.api.getLandByChunk(chunk.getWorld(), x, z);

        if (land == null) {
            return true;
        }

        return land.getOwnerUID().equals(player.getUniqueId());
    }
}