package com.ryderbelserion.crazyenchantments.paper;

import com.ryderbelserion.crazyenchantments.core.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.core.commands.player.ISource;
import com.ryderbelserion.crazyenchantments.core.commands.subs.CoreCommand;
import com.ryderbelserion.crazyenchantments.core.enums.Mode;
import com.ryderbelserion.crazyenchantments.core.registry.UserRegistry;
import com.ryderbelserion.crazyenchantments.paper.api.registry.EnchantmentRegistry;
import com.ryderbelserion.crazyenchantments.paper.commands.PaperSource;
import com.ryderbelserion.crazyenchantments.paper.listeners.CacheListener;
import com.ryderbelserion.fusion.kyori.mods.ModSupport;
import com.ryderbelserion.fusion.paper.FusionPaper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CrazyEnchantmentsPlatform extends CrazyEnchantments {

    private final CrazyEnchantmentsPlugin plugin;
    private final EnchantmentRegistry enchantmentRegistry;
    private final FusionPaper fusion;
    private final Server server;

    public CrazyEnchantmentsPlatform(@NotNull final CrazyEnchantmentsPlugin plugin, @NotNull final FusionPaper fusion) {
        super(fusion.getDataPath(), fusion.getFileManager());

        this.plugin = plugin;
        this.fusion = fusion;

        this.enchantmentRegistry = plugin.getEnchantmentRegistry();

        this.server = plugin.getServer();
    }

    private boolean isYardWatchEnabled = false;

    @Override
    public void start(@NotNull final Audience audience) {
        super.start(audience);

        final PluginManager pluginManager = this.server.getPluginManager();
        final UserRegistry userRegistry = this.getUserRegistry();

        this.isYardWatchEnabled = this.fusion.isModReady(ModSupport.yard_watch);

        this.enchantmentRegistry.getEnchantments().forEach((key, customEnchantment) -> {
            customEnchantment.init(this.plugin); // registers listeners, or things not meant for the bootstrap loader
        });

        List.of(
                new CacheListener(userRegistry)
        ).forEach(listener -> pluginManager.registerEvents(listener, this.plugin));
    }

    public Function<CommandSourceStack, ISource> function() {
        return stack -> new PaperSource(stack.getSender());
    }

    @Override
    public void reload() {
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
    public void registerCommands() {
        final LifecycleEventManager<@NotNull Plugin> manager = this.plugin.getLifecycleManager();

        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands registry = event.registrar();

            registry.register(new CoreCommand<>("crazyenchantments.access", "crazyenchantments", function()).build(),
                    "The base command for CrazyEnchantments!",
                    Collections.singletonList("ce"));
        });
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