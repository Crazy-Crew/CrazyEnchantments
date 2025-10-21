package com.badbones69.crazyenchantments.paper.support.factions;

import com.badbones69.crazyenchantments.paper.support.interfaces.claims.ClaimSupport;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FactionsUUIDSupport implements ClaimSupport {

    public boolean isFriendly(@NotNull final Player player, @NotNull final Player other) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        FPlayer oPlayer = FPlayers.getInstance().getByPlayer(other);

        if (fPlayer == null || oPlayer == null) return false;

        if (oPlayer.getFaction().isPeaceful()) return true;

        if (isWilderness(fPlayer) || isWilderness(oPlayer)) return false;

        // TODO Find a way for factions forks to work with each other.
        try {
            return fPlayer.getFaction() == oPlayer.getFaction() || fPlayer.getRelationTo(oPlayer).isAlly() || fPlayer.getRelationTo(oPlayer).isTruce();
        } catch (NoSuchMethodError e) {
            return false;
        }
    }

    public boolean inTerritory(@NotNull final Player player) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

        return !isWilderness(fPlayer) && (fPlayer.isInOwnTerritory() || fPlayer.isInAllyTerritory());
    }

    public boolean canBreakBlock(@NotNull final Player player, @NotNull final Block block) {
        Faction fBlock = Board.getInstance().getFactionAt(new FLocation(block.getLocation()));

        return isWilderness(fBlock) || FPlayers.getInstance().getByPlayer(player).getFaction() == fBlock;
    }

    private boolean isWilderness(@NotNull final FPlayer player) {
        return isWilderness(player.getFaction());
    }

    private boolean isWilderness(@Nullable final Faction faction) {
        return faction != null && faction.isWilderness();
    }
}