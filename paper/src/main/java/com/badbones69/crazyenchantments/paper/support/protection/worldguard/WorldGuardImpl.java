package com.badbones69.crazyenchantments.paper.support.protection.worldguard;

import com.badbones69.crazyenchantments.paper.api.constants.Support;
import com.ryderbelserion.fusion.core.api.registry.mods.objects.Mod;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class WorldGuardImpl extends Mod {

    public WorldGuardImpl() {
        super(Support.worldguard);
    }

    @Override
    public Mod init() {
        if (isEnabled()) {
            new WorldGuardSupport().init();
        }

        return this;
    }
}