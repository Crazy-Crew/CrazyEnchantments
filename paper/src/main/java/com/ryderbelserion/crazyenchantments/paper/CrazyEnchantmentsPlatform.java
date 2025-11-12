package com.ryderbelserion.crazyenchantments.paper;

import com.ryderbelserion.crazyenchantments.core.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.core.registry.UserRegistry;
import com.ryderbelserion.crazyenchantments.paper.api.registry.EnchantmentRegistry;
import com.ryderbelserion.crazyenchantments.paper.commands.brigadier.BaseCommand;
import com.ryderbelserion.crazyenchantments.paper.listeners.CacheListener;
import com.ryderbelserion.fusion.core.api.enums.Support;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class CrazyEnchantmentsPlatform extends CrazyEnchantments {

    private final CrazyEnchantmentsPlugin plugin;
    private final EnchantmentRegistry enchantmentRegistry;
    private boolean isYardWatchEnabled = false;
    private final FusionPaper fusion;
    private final Server server;

    public CrazyEnchantmentsPlatform(@NotNull final CrazyEnchantmentsPlugin plugin, @NotNull final FusionPaper fusion) {
        super(fusion.getPath(), fusion.getFileManager());

        this.fusion = fusion;

        this.plugin = plugin;

        fusion.enable(this.plugin);

        this.enchantmentRegistry = plugin.getEnchantmentRegistry();

        this.server = plugin.getServer();
    }

    @Override
    public void start() {
        super.start();

        final PluginManager pluginManager = this.server.getPluginManager();
        final UserRegistry userRegistry = this.getUserRegistry();

        this.isYardWatchEnabled = Support.yard_watch.isEnabled();

        this.enchantmentRegistry.getEnchantments().forEach((key, customEnchantment) -> {
            customEnchantment.init(this.plugin); // registers listeners, or things not meant for the bootstrap loader
        });

        List.of(
                new CacheListener(userRegistry)
        ).forEach(listener -> pluginManager.registerEvents(listener, this.plugin));

        this.fusion.getCommandManager().enable(new BaseCommand(this), "You like my crazy enchants?", List.of("ce"));
    }

    @Override
    public void reload() {
        this.fusion.reload();

        super.reload();

        this.enchantmentRegistry.reload();
    }

    @Override
    public void stop() {
        super.stop();

        this.server.getGlobalRegionScheduler().cancelTasks(this.plugin);
        this.server.getAsyncScheduler().cancelTasks(this.plugin);
    }

    @Override
    public final boolean isConsoleSender(@NotNull final Audience audience) {
        return audience instanceof ConsoleCommandSender;
    }

    @Override
    public void broadcast(@NotNull final Component component, @NotNull final String permission) {
        if (permission.isEmpty()) {
            this.server.broadcast(component);

            return;
        }

        this.server.broadcast(component, permission);
    }

    @Override
    public void broadcast(@NotNull Component component) {
        broadcast(component, "");
    }

    public @NotNull final CrazyEnchantmentsPlugin getPlugin() {
        return this.plugin;
    }

    public @NotNull final FusionPaper getFusion() {
        return this.fusion;
    }

    public final boolean isYardWatchEnabled() {
        return this.isYardWatchEnabled;
    }
}