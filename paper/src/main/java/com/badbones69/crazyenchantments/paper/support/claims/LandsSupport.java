package com.badbones69.crazyenchantments.paper.support.claims;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.support.interfaces.claims.ClaimSupport;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class LandsSupport implements ClaimSupport {

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);
    private static final LandsIntegration api = LandsIntegration.of(plugin);

    public boolean isFriendly(@NotNull final Player player, @NotNull final Player other) {
        Land land = api.getLandPlayer(other.getUniqueId()).getOwningLand();

        return (land != null && land.isTrusted(player.getUniqueId()));
    }

    public boolean inTerritory(@NotNull final Player player) {
        UUID uuid = player.getUniqueId();
        Chunk chunk = player.getChunk();
        Land land = api.getLandByChunk(player.getWorld(), chunk.getX(), chunk.getZ());

        if (land == null) return false;

        return (land.getOwnerUID() == uuid || land.isTrusted(uuid));
    }

    @Override
    public boolean canBreakBlock(@NotNull final Player player, @NotNull final Block block) {
        return false;
    }

}