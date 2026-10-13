package com.badbones69.crazyenchantments.paper.support.skyblock.superor;

import com.badbones69.crazyenchantments.paper.api.constants.Support;
import com.ryderbelserion.fusion.core.mods.objects.Mod;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class SuperiorSkyBlockImpl extends Mod {

    public SuperiorSkyBlockImpl() {
        super(Support.superiorskyblock);
    }

    @Override
    public void init() {
        if (isEnabled()) {
            new SuperiorSkyBlockSupport().init();
        }
    }
}