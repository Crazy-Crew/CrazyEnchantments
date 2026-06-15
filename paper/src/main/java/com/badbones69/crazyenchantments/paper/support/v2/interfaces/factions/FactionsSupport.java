package com.badbones69.crazyenchantments.paper.support.v2.interfaces.factions;

import com.badbones69.crazyenchantments.paper.support.v2.enums.PluginType;
import com.badbones69.crazyenchantments.paper.support.v2.interfaces.TerritorySupport;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class FactionsSupport<FP, F, B, L> extends TerritorySupport<B, L> {

    public abstract boolean isFactionMember(final F faction, final Player player);

    public abstract boolean isWilderness(final FP player);

    public abstract boolean isSafezone(final FP player);

    public abstract boolean isWarzone(final FP player);

    @Override
    public PluginType getPluginType() {
        return PluginType.FACTIONS;
    }

    @Override
    public void init() {
        if (this.isEnabled) {
            return;
        }

        this.isEnabled = true;

        this.servicesManager.register(
                FactionsSupport.class,
                this,
                this.plugin,
                ServicePriority.Normal
        );
    }
}