package com.ryderbelserion.crazyenchantments.paper.api.registry;

import com.ryderbelserion.crazyenchantments.api.interfaces.IUser;
import com.ryderbelserion.crazyenchantments.api.interfaces.registry.IUserRegistry;
import com.ryderbelserion.crazyenchantments.common.CEPlugin;
import com.ryderbelserion.crazyenchantments.paper.CrazyPlugin;
import com.ryderbelserion.crazyenchantments.paper.api.registry.adapters.PaperUserAdapter;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PaperUserRegistry implements IUserRegistry<Player> {

    private final CrazyPlugin plugin = CrazyPlugin.getPlugin(CrazyPlugin.class);

    private final Map<UUID, PaperUserAdapter> users = new HashMap<>();

    @Override
    public void init() {
        this.users.put(CEPlugin.CONSOLE_UUID, new PaperUserAdapter());
    }

    @Override
    public PaperUserAdapter addUser(@NotNull final Player player) {
        final PaperUserAdapter adapter = new PaperUserAdapter(player);

        return this.users.putIfAbsent(player.getUniqueId(), adapter);
    }

    @Override
    public PaperUserAdapter removeUser(@NotNull final UUID uuid) {
        return this.users.remove(uuid);
    }

    @Override
    public Optional<PaperUserAdapter> getUser(@NotNull final UUID uuid) {
        final Server server = this.plugin.getServer();

        final Player player = server.getPlayer(uuid);

        if (this.users.containsKey(uuid) && player != null) {
            return Optional.of(addUser(player));
        }

        return Optional.of(this.users.get(uuid));
    }

    @Override
    public @NotNull final IUser getConsole() {
        return this.users.get(CEPlugin.CONSOLE_UUID);
    }
}