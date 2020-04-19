package me.badbones69.crazyenchantments.multisupport.factions;

import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.manager.PlayerManager;
import net.prosavage.factionsx.util.Relation;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FactionsX implements FactionPlugin {
    
    private PlayerManager playerManager = PlayerManager.INSTANCE;
    private GridManager gridManager = GridManager.INSTANCE;
    
    @Override
    public boolean isFriendly(Player player, Player other) {
        FPlayer fPlayer = playerManager.getFPlayer(player);
        FPlayer fOther = playerManager.getFPlayer(other);
        if (fPlayer == null || fOther == null || fOther.getFaction().isWilderness()) {
            return false;
        }
        Faction factionPlayer = fPlayer.getFaction();
        Faction factionOther = fOther.getFaction();
        Relation relation = factionPlayer.getRelationTo(factionOther);
        return factionPlayer.getId() == factionOther.getId() || relation == Relation.ALLY || relation == Relation.TRUCE;
    }
    
    @Override
    public boolean inTerritory(Player player) {
        Faction factionPlayer = playerManager.getFPlayer(player).getFaction();
        Faction factionBlock = gridManager.getFactionAt(player.getLocation().getChunk());
        return !factionBlock.isWilderness() && factionPlayer.getId() == factionBlock.getId();
    }
    
    @Override
    public boolean canBreakBlock(Player player, Block block) {
        Faction factionPlayer = playerManager.getFPlayer(player).getFaction();
        Faction factionBlock = gridManager.getFactionAt(block.getChunk());
        return factionPlayer.isWilderness() || factionPlayer.getId() == factionBlock.getId();
    }
    
}