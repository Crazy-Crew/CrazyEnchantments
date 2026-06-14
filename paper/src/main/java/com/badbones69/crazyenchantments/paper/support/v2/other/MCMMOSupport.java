package com.badbones69.crazyenchantments.paper.support.v2.other;

import com.badbones69.crazyenchantments.paper.support.v2.enums.PluginType;
import com.badbones69.crazyenchantments.paper.support.v2.interfaces.TerritorySupport;
import com.gmail.nossr50.api.PartyAPI;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class MCMMOSupport extends TerritorySupport<Location, Location> {

    @Override
    public PluginType getPluginType() {
        return PluginType.DEFAULT;
    }

    @Override
    public String getPluginName() {
        return "McMMO";
    }

    @Override
    public boolean isFriendly(@NonNull final Entity player, @NonNull final Entity target) {
        if (!(player instanceof Player entityPlayer)) {
            return false;
        }

        if (!(target instanceof Player entityTarget)) {
            return false;
        }

        return PartyAPI.inSameParty(entityPlayer, entityTarget);
    }
}