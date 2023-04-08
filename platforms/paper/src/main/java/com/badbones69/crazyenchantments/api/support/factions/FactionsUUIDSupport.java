package com.badbones69.crazyenchantments.api.support.factions;

import com.badbones69.crazyenchantments.api.support.interfaces.claims.FactionsVersion;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FactionsUUIDSupport implements FactionsVersion {

    public boolean isFriendly(Player player, Player other) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        FPlayer oPlayer = FPlayers.getInstance().getByPlayer(other);

        if (fPlayer == null || oPlayer == null) return false;

        if (oPlayer.getFaction().isPeaceful()) return true;

        if (isWilderness(fPlayer) || isWilderness(oPlayer)) return false;

        return fPlayer.getFaction() == oPlayer.getFaction() || fPlayer.getRelationTo(oPlayer).isAlly() || fPlayer.getRelationTo(oPlayer).isTruce();
    }

    public boolean inTerritory(Player player) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

        return !isWilderness(fPlayer) && (fPlayer.isInOwnTerritory() || fPlayer.isInAllyTerritory());
    }

    public boolean canBreakBlock(Player player, Block block) {
        Faction fBlock = Board.getInstance().getFactionAt(new FLocation(block.getLocation()));

        return isWilderness(fBlock) || FPlayers.getInstance().getByPlayer(player).getFaction() == fBlock;
    }

    private boolean isWilderness(FPlayer player) {
        return isWilderness(player.getFaction());
    }

    private boolean isWilderness(Faction faction) {
        return faction != null && faction.isWilderness();
    }
}