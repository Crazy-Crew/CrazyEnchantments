package com.badbones69.crazyenchantments.paper.support.claim.plotsquared;

import com.badbones69.crazyenchantments.paper.api.constants.Support;
import com.ryderbelserion.fusion.core.api.registry.mods.objects.Mod;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class PlotSquaredImpl extends Mod {

    public PlotSquaredImpl() {
        super(Support.plotsquared);
    }

    @Override
    public Mod init() {
        if (isEnabled()) {
            new PlotSquaredSupport().init();
        }

        return this;
    }
}