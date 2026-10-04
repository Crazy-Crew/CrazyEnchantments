package com.badbones69.crazyenchantments.paper.support.protection.griefprevention;

import com.badbones69.crazyenchantments.paper.api.constants.Support;
import com.ryderbelserion.fusion.core.mods.objects.Mod;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class GriefPreventionImpl extends Mod {

    public GriefPreventionImpl() {
        super(Support.griefprevention);
    }

    @Override
    public void init() {
        if (isEnabled()) {
            new GriefPreventionSupport().init();
        }
    }
}