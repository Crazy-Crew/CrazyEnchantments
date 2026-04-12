package com.ryderbelserion.crazyenchantments.paper.api;

import com.ryderbelserion.crazyenchantments.common.CEPlugin;
import com.ryderbelserion.crazyenchantments.common.api.enums.Mode;
import com.ryderbelserion.crazyenchantments.common.commands.player.ISource;
import com.ryderbelserion.crazyenchantments.common.commands.subs.CoreCommand;
import com.ryderbelserion.crazyenchantments.paper.CrazyPlugin;
import com.ryderbelserion.crazyenchantments.paper.api.registry.PaperContextRegistry;
import com.ryderbelserion.crazyenchantments.paper.api.registry.PaperMessageRegistry;
import com.ryderbelserion.crazyenchantments.paper.api.registry.PaperUserRegistry;
import com.ryderbelserion.crazyenchantments.paper.api.registry.adapters.PaperSenderAdapter;
import com.ryderbelserion.crazyenchantments.paper.api.registry.enchants.EnchantmentRegistry;
import com.ryderbelserion.crazyenchantments.paper.commands.PaperSource;
import com.ryderbelserion.crazyenchantments.paper.listeners.CacheListener;
import com.ryderbelserion.fusion.core.api.constants.ModSupport;
import com.ryderbelserion.fusion.paper.FusionPaper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CrazyEnchantmentsPaper extends CEPlugin<CommandSender> {

    private final EnchantmentRegistry enchantmentRegistry;
    private final CrazyPlugin plugin;
    private final Server server;

    public CrazyEnchantmentsPaper(@NotNull final CrazyPlugin plugin, @NotNull final FusionPaper fusion) {
        super(fusion.setPlugin(plugin));

        this.enchantmentRegistry = plugin.getEnchantmentRegistry();
        this.server = plugin.getServer();
        this.plugin = plugin;
    }

    private PaperMessageRegistry messageRegistry;
    private PaperContextRegistry contextRegistry;
    private PaperSenderAdapter userAdapter;
    private PaperUserRegistry userRegistry;

    private boolean isYardWatchEnabled = false;

    @Override
    public void init() {
        super.init();

        this.isYardWatchEnabled = this.fusion.isModReady(ModSupport.yard_watch);

        this.contextRegistry = new PaperContextRegistry();

        this.userRegistry = new PaperUserRegistry();
        this.userRegistry.init();

        this.messageRegistry = new PaperMessageRegistry();
        this.messageRegistry.init();

        this.userAdapter = new PaperSenderAdapter(this);

        post();
    }

    @Override
    public void post() {
        super.post();

        final PluginManager pluginManager = this.server.getPluginManager();

        this.enchantmentRegistry.getEnchantments().forEach((_, customEnchantment) -> {
            customEnchantment.init(this.plugin); // registers listeners, or things not meant for the bootstrap loader
        });

        List.of(
                new CacheListener(this)
        ).forEach(listener -> pluginManager.registerEvents(listener, this.plugin));
    }

    @Override
    public void reload() {
        super.reload();

        this.enchantmentRegistry.reload();
    }

    @Override
    public void shutdown() {
        super.shutdown();

        this.server.getGlobalRegionScheduler().cancelTasks(this.plugin);
        this.server.getAsyncScheduler().cancelTasks(this.plugin);
    }

    @Override
    public void registerCommands() {
        final LifecycleEventManager<@NotNull Plugin> manager = this.plugin.getLifecycleManager();

        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands registry = event.registrar();

            registry.register(new CoreCommand<>("crazyenchantments.access", "crazyenchantments", function()).build(),
                    "The base command for CrazyEnchantments!",
                    Collections.singletonList("ce"));
        });
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
    public void registerPermission(@NotNull final Mode mode, @NotNull final String parent, @NotNull final String description, @NotNull final Map<String, Boolean> children) {
        PermissionDefault permissionDefault;

        switch (mode) {
            case NOT_OP -> permissionDefault = PermissionDefault.NOT_OP;
            case TRUE -> permissionDefault = PermissionDefault.TRUE;
            case FALSE -> permissionDefault = PermissionDefault.FALSE;
            default -> permissionDefault = PermissionDefault.OP;
        }

        final PluginManager pluginManager = this.server.getPluginManager();

        if (pluginManager.getPermission(parent) != null) return;

        final Permission permission = new Permission(
                parent,
                description,
                permissionDefault,
                children
        );

        pluginManager.addPermission(permission);
    }

    @Override
    public final boolean hasPermission(@NotNull final Audience audience, @NotNull final String permission) {
        final CommandSender sender = (CommandSender) audience;

        return sender.hasPermission(permission);
    }

    @Override
    public @NotNull final PaperContextRegistry getContextRegistry() {
        return this.contextRegistry;
    }

    @Override
    public @NotNull final PaperMessageRegistry getMessageRegistry() {
        return this.messageRegistry;
    }

    @Override
    public @NotNull final PaperSenderAdapter getSenderAdapter() {
        return this.userAdapter;
    }

    @Override
    public @NotNull final PaperUserRegistry getUserRegistry() {
        return this.userRegistry;
    }

    public @NotNull final Function<CommandSourceStack, ISource> function() {
        return stack -> new PaperSource(stack.getSender());
    }

    public final boolean isYardWatchEnabled() {
        return this.isYardWatchEnabled;
    }
}