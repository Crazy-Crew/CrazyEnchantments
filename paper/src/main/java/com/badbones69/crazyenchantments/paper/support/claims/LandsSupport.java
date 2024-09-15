package com.badbones69.crazyenchantments.paper.support.claims;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.support.interfaces.claims.ClaimSupport;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class LandsSupport implements ClaimSupport {

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);
    private static final LandsIntegration api = LandsIntegration.of(plugin);

    public boolean isFriendly(Player player, Player other) {
        Land land = api.getLandPlayer(other.getUniqueId()).getOwningLand();

        return (land != null && land.isTrusted(player.getUniqueId()));
    }

    public boolean inTerritory(Player player) {
        UUID uuid = player.getUniqueId();
        Chunk chunk = player.getChunk();
        Land land = api.getLandByChunk(player.getWorld(), chunk.getX(), chunk.getZ());

        if (land == null) return false;

        return (land.getOwnerUID() == uuid || land.isTrusted(uuid));
    }

    @Override
    public boolean canBreakBlock(Player player, Block block) {
        return false;
    }

}