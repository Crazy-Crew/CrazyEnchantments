package me.badbones69.crazyenchantments.multisupport.factions;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.kingdoms.constants.kingdom.Kingdom;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.land.SimpleLocation;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.manager.game.GameManagement;

public class KingdomSupport implements FactionPlugin {
    
    public boolean isFriendly(Player player, Player other) {
        KingdomPlayer kPlayer = GameManagement.getPlayerManager().getSession(player);
        KingdomPlayer kOther = GameManagement.getPlayerManager().getSession(other);
        return kPlayer != null && kOther != null && kPlayer.getKingdom() != null && kOther.getKingdom() != null &&
        (kPlayer.getKingdom() == kOther.getKingdom() || kPlayer.getKingdom() != null && kPlayer.getKingdom().isAllianceWith(kOther.getKingdom()));
    }
    
    public boolean inTerritory(Player player) {
        KingdomPlayer kPlayer = GameManagement.getPlayerManager().getSession(player);
        Land land = GameManagement.getLandManager().getOrLoadLand(new SimpleLocation(player.getLocation()).toSimpleChunk());
        Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwner());
        return kPlayer.getKingdom() != null && kingdom != null && kingdom.equals(kPlayer.getKingdom());
    }
    
    public boolean canBreakBlock(Player player, Block block) {
        KingdomPlayer kPlayer = GameManagement.getPlayerManager().getSession(player);
        Land land = GameManagement.getLandManager().getOrLoadLand(new SimpleLocation(block.getLocation()).toSimpleChunk());
        Kingdom kingdom = GameManagement.getKingdomManager().getOrLoadKingdom(land.getOwner());
        return land.getOwner() == null || kPlayer.isAdminMode() || kingdom.equals(kPlayer.getKingdom());
        
    }
    
}