package com.badbones69.crazyenchantments.paper.support.claim.towny;

import com.badbones69.crazyenchantments.paper.api.constants.Support;
import com.ryderbelserion.fusion.core.api.registry.mods.objects.Mod;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class TownyImpl extends Mod {

    public TownyImpl() {
        super(Support.towny);
    }

    @Override
    public Mod init() {
        if (isEnabled()) {
            new TownySupport().init();
        }

        return this;
    }
}