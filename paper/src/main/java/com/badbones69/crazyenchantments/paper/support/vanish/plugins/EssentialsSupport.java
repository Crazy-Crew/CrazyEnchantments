package com.badbones69.crazyenchantments.paper.support.vanish.plugins;

import com.badbones69.crazyenchantments.paper.support.api.interfaces.VanishSupport;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.ryderbelserion.fusion.core.api.FusionKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class EssentialsSupport extends VanishSupport {

    private final Essentials essentials = JavaPlugin.getPlugin(Essentials.class);

    public EssentialsSupport(final FusionKey key) {
        super(key);
    }

    @Override
    public boolean isVanished(final Player player) {
        if (!isEnabled()) {
            return false;
        }

        final User user = this.essentials.getUser(player);

        if (user == null) {
            return false;
        }

        return user.isVanished();
    }
}