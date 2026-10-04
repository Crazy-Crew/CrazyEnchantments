package com.badbones69.crazyenchantments.paper.support.api.interfaces;

import com.ryderbelserion.fusion.api.FusionProvider;
import com.ryderbelserion.fusion.core.FusionCore;
import com.ryderbelserion.fusion.api.objects.FusionKey;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class VanishSupport {

    private final FusionCore fusion = (FusionCore) FusionProvider.api();

    protected final FusionKey key;

    public VanishSupport(final FusionKey key) {
        this.key = key;
    }

    public VanishSupport() {
        this.key = FusionKey.key("crazyenchantments", "generic");
    }

    public abstract boolean isVanished(final Player player);

    public boolean isEnabled() {
        return this.fusion.isModReady(this.key);
    }
}