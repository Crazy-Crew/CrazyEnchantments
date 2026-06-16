package com.badbones69.crazyenchantments.paper.support.claim.lands;

import com.badbones69.crazyenchantments.paper.api.constants.Support;
import com.ryderbelserion.fusion.core.api.registry.mods.objects.Mod;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class LandsImpl extends Mod {

    public LandsImpl() {
        super(Support.lands);
    }

    @Override
    public Mod init() {
        if (isEnabled()) {
            new LandsSupport().init();
        }

        return this;
    }
}