package com.badbones69.crazyenchantments.paper.support.claims;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.entity.Player;

public class SuperiorSkyBlockSupport {

    public boolean inTerritory(final Player player) {
        final SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);

        return superiorPlayer.isInsideIsland();
    }

    public boolean isFriendly(final Player player, final Player other) {
        final SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player.getUniqueId()), otherPlayer = SuperiorSkyblockAPI.getPlayer(other.getUniqueId());

        return superiorPlayer.getIsland() != null && superiorPlayer.getIsland().isMember(otherPlayer);
    }
}