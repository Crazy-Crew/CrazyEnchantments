package com.badbones69.crazyenchantments.paper.support.factions.uuid;

import com.badbones69.crazyenchantments.paper.api.constants.Support;
import com.ryderbelserion.fusion.core.api.registry.mods.objects.Mod;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class FactionsUUIDImpl extends Mod {

    public FactionsUUIDImpl() {
        super(Support.factions_uuid);
    }

    @Override
    public Mod init() {
        if (isEnabled()) {
            new FactionsUUIDSupport().init();
        }

        return this;
    }
}