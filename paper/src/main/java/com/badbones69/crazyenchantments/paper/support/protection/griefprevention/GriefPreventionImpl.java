package com.badbones69.crazyenchantments.paper.support.protection.griefprevention;

import com.badbones69.crazyenchantments.paper.api.constants.Support;
import com.ryderbelserion.fusion.core.api.registry.mods.objects.Mod;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class GriefPreventionImpl extends Mod {

    public GriefPreventionImpl() {
        super(Support.griefprevention);
    }

    @Override
    public Mod init() {
        if (isEnabled()) {
            new GriefPreventionSupport().init();
        }

        return this;
    }
}