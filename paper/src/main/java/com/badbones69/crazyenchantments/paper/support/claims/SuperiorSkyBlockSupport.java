package com.badbones69.crazyenchantments.paper.support.claims;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SuperiorSkyBlockSupport {

    public boolean inTerritory(@NotNull final Player player) {
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);

        return superiorPlayer.isInsideIsland();
    }

    public boolean isFriendly(@NotNull final Player player, @NotNull final Player other) {
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player.getUniqueId()), otherPlayer = SuperiorSkyblockAPI.getPlayer(other.getUniqueId());

        return superiorPlayer.getIsland() != null && superiorPlayer.getIsland().isMember(otherPlayer);
    }
}