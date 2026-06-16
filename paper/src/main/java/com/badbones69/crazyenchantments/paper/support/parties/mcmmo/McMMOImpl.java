package com.badbones69.crazyenchantments.paper.support.parties.mcmmo;

import com.badbones69.crazyenchantments.paper.api.constants.Support;
import com.ryderbelserion.fusion.core.api.registry.mods.objects.Mod;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class McMMOImpl extends Mod {

    public McMMOImpl() {
        super(Support.mcmmo);
    }

    @Override
    public Mod init() {
        if (isEnabled()) {
            new McMMOSupport().init();
        }

        return this;
    }
}