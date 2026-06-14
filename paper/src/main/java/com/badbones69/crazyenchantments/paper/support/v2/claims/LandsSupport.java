package com.badbones69.crazyenchantments.paper.support.v2.claims;

import com.badbones69.crazyenchantments.paper.support.v2.interfaces.TerritorySupport;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.land.Area;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import java.util.UUID;

public final class LandsSupport extends TerritorySupport<Block, Location> {

    private final LandsIntegration api = LandsIntegration.of(this.plugin);

    @Override
    public String getPluginName() {
        return "Lands";
    }

    @Override
    public boolean canBreakBlock(@NonNull final Player player, @NonNull final Block container) {
        if (!isPluginReady()) {
            return true;
        }

        final Location location = container.getLocation();

        final Area area = this.api.getArea(location);

        if (area == null) return true;

        return area.hasRoleFlag(player.getUniqueId(), Flags.BLOCK_BREAK);
    }

    @Override
    public boolean isCombatEnabled(@NonNull final Location container) {
        if (!isPluginReady()) {
            return true;
        }

        return true;
    }

    @Override
    public boolean isTerritory(@NonNull final Player player, @NonNull final Location container) {
        if (!isPluginReady()) {
            return true;
        }

        final UUID uuid = player.getUniqueId();
        final Chunk chunk = container.getChunk();

        final int x = chunk.getX();
        final int z = chunk.getZ();

        final Land land = this.api.getLandByChunk(chunk.getWorld(), x, z);

        if (land == null) return false;

        return land.getOwnerUID().equals(uuid) || land.isTrusted(uuid);
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

        final Land land = this.api.getLandPlayer(target.getUniqueId()).getOwningLand();

        if (land == null) return false;

        return land.isTrusted(player.getUniqueId());
    }
}