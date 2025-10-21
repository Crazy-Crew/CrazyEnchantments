package com.badbones69.crazyenchantments.paper.support.mods.vanish;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.fusion.core.api.support.objects.Mod;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public class GenericVanishMod extends Mod {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Server server = this.plugin.getServer();

    @Override
    public boolean isVanished(@NotNull final UUID uuid) {
        boolean isVanished = false;

        if (!isEnabled()) return isVanished;

        final Player player = this.server.getPlayer(uuid);

        if (player == null) return isVanished;

        for (final MetadataValue value : player.getMetadata("vanished")) {
            final boolean isValid = value.asBoolean();

            if (isValid) {
                isVanished = isValid;

                break;
            }
        }

        return isVanished;
    }

    @Override
    public final boolean isEnabled() { //todo() add a config check maybe
        return true;
    }
}