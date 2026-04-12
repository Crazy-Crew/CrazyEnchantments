package com.ryderbelserion.crazyenchantments.paper.api.registry;

import com.ryderbelserion.crazyenchantments.api.interfaces.registry.IContextRegistry;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class PaperContextRegistry implements IContextRegistry<Player> {

    @Override
    public @NotNull final UUID getUUID(@NotNull final Player player) {
        return player.getUniqueId();
    }
}