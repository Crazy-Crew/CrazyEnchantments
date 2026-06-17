package com.badbones69.crazyenchantments.paper.support.vanish;

import com.badbones69.crazyenchantments.paper.support.api.interfaces.VanishSupport;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GenericVanishSupport extends VanishSupport {

    @Override
    public boolean isVanished(final Player player) {
        return player.isInvisible();
    }
}