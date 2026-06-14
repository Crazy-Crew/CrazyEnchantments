package com.badbones69.crazyenchantments.paper.support.v2.interfaces.factions;

import com.badbones69.crazyenchantments.paper.support.v2.interfaces.TerritorySupport;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.jspecify.annotations.NonNull;

public abstract class FactionsSupport<FP, F> extends TerritorySupport<Object, Object> {

    public abstract boolean isWildernessWithPlayer(@NonNull final FP player);

    public abstract boolean isWildernessWithFaction(@NonNull final F faction);

    public abstract boolean isFactionMember(@NonNull final F faction, @NonNull final Player player);

    @Override
    public boolean isFactions() {
        return true;
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