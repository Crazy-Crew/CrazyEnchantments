package com.badbones69.crazyenchantments.paper.support.v2.other;

import com.badbones69.crazyenchantments.paper.support.v2.interfaces.TerritorySupport;
import com.gmail.nossr50.api.PartyAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class MCMMOSupport extends TerritorySupport<Location, Location> {

    @Override
    public String getPluginName() {
        return "McMMO";
    }

    @Override
    public boolean isFriendly(@NonNull final Player player, @NonNull final Entity target) {
        return PartyAPI.inSameParty(player, target);
    }
}