package com.badbones69.crazyenchantments.paper.support.claim.lands;

import com.badbones69.crazyenchantments.paper.api.constants.Support;
import com.ryderbelserion.fusion.core.mods.objects.Mod;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class LandsImpl extends Mod {

    public LandsImpl() {
        super(Support.lands);
    }

    @Override
    public void init() {
        if (isEnabled()) {
            new LandsSupport().init();
        }
    }
}