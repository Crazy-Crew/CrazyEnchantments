package com.badbones69.crazyenchantments.paper.support.other;

import com.badbones69.crazyenchantments.paper.support.enums.PluginType;
import com.badbones69.crazyenchantments.paper.support.interfaces.TerritorySupport;
import com.gmail.nossr50.api.PartyAPI;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
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
    public boolean isFriendly(final Entity player, final Entity target) {
        if (!(player instanceof Player entityPlayer) || !(target instanceof Player entityTarget)) {
            return false;
        }

        return PartyAPI.inSameParty(entityPlayer, entityTarget);
    }
}