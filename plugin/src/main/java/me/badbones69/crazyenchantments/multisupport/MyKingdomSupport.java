package me.badbones69.crazyenchantments.multisupport;

import me.badbones69.premiumhooks.factions.FactionPlugin;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.kingdoms.constants.kingdom.Kingdom;
import org.kingdoms.constants.kingdom.KingdomRelation;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.player.KingdomPlayer;


public class MyKingdomSupport implements FactionPlugin {
    @Override
    public boolean isFriendly(Player player, Player other) {
        KingdomPlayer kPlayer = KingdomPlayer.getKingdomPlayer(player);
        KingdomPlayer kOther = KingdomPlayer.getKingdomPlayer(other);
        Kingdom playerKingdom = kPlayer.getKingdom();
        Kingdom otherKingdom = kOther.getKingdom();

        if (kPlayer.isAdmin()) //If the player is an admin he can attack anyone
            return true;

        if (playerKingdom == null && otherKingdom == null)
            return false;

        if (playerKingdom == null)
            return otherKingdom.getAttributes().get(KingdomRelation.NEUTRAL).contains(KingdomRelation.Attribute.CEASEFIRE);

        if (otherKingdom == null)
            return playerKingdom.getAttributes().get(KingdomRelation.NEUTRAL).contains(KingdomRelation.Attribute.CEASEFIRE);

        return playerKingdom.hasAttribute(otherKingdom, KingdomRelation.Attribute.CEASEFIRE);

    }

    @Override
    public boolean inTerritory(Player player) {
        KingdomPlayer kPlayer = KingdomPlayer.getKingdomPlayer(player);
        Land land = Land.getLand(player.getLocation());
        return land.getKingdom() != null && kPlayer.getKingdom() != null && land.getKingdom() == kPlayer.getKingdom();
    }

    @Override
    public boolean canBreakBlock(Player player, Block block) {
        KingdomPlayer kPlayer = KingdomPlayer.getKingdomPlayer(player);
        Land land = Land.getLand(block.getLocation());
        Kingdom playerKingdom = kPlayer.getKingdom();
        Kingdom blockKingdom = land.getKingdom();
        if (blockKingdom == null || kPlayer.isAdmin())
            return true;
        if (playerKingdom == null) {
            return false;
        }
        return playerKingdom.hasAttribute(blockKingdom, KingdomRelation.Attribute.BUILD);
    }
}
