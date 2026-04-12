package com.ryderbelserion.crazyenchantments.paper.api.registry.adapters;

import com.ryderbelserion.crazyenchantments.common.CEPlugin;
import com.ryderbelserion.crazyenchantments.common.api.FileKeys;
import com.ryderbelserion.crazyenchantments.common.api.adapters.sender.ISenderAdapter;
import com.ryderbelserion.crazyenchantments.paper.api.CrazyEnchantmentsPaper;
import com.ryderbelserion.crazyenchantments.paper.api.registry.PaperMessageRegistry;
import com.ryderbelserion.crazyenchantments.paper.api.registry.PaperUserRegistry;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PaperSenderAdapter extends ISenderAdapter<CrazyEnchantmentsPaper, Component, CommandSender> {

    private final PaperMessageRegistry messageRegistry;
    private final PaperUserRegistry userRegistry;
    private final FusionPaper fusion;

    public PaperSenderAdapter(@NotNull final CrazyEnchantmentsPaper platform) {
        super();

        this.messageRegistry = platform.getMessageRegistry();
        this.userRegistry = platform.getUserRegistry();

        this.fusion = (FusionPaper) platform.getFusion();
    }

    @Override
    public UUID getUniqueId(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getUniqueId();
        }

        return CEPlugin.CONSOLE_UUID;
    }

    @Override
    public String getName(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getName();
        }

        return CEPlugin.CONSOLE_NAME;
    }

    @Override
    public void sendMessage(@NotNull final CommandSender sender, @NotNull final Key id, @NotNull final Map<String, String> placeholders) {
        sender.sendMessage(getComponent(sender, id, placeholders));
    }

    @Override
    public Component getComponent(@NotNull final CommandSender sender, @NotNull final Key id, @NotNull final Map<String, String> placeholders) {
        final Map<String, String> map = new HashMap<>(placeholders);

        final CommentedConfigurationNode configuration = FileKeys.config.getYamlConfig();

        final String prefix = configuration.node("root", "prefix").getString(" <gold>ChatterBox <reset>");

        if (!prefix.isEmpty()) {
            map.putIfAbsent("{prefix}", prefix);
        }

        if (!(sender instanceof Player player)) {
            return this.fusion.asComponent(sender, this.messageRegistry.getMessage(id).getValue(), map);
        }

        final Optional<PaperUserAdapter> optional = this.userRegistry.getUser(player.getUniqueId());

        if (optional.isEmpty()) return this.fusion.asComponent(player, this.messageRegistry.getMessage(id).getValue(), map);

        final PaperUserAdapter user = optional.get();

        return this.fusion.asComponent(player, this.messageRegistry.getMessageByLocale(user.getLocaleKey(), id).getValue(), map);
    }

    @Override
    public boolean isConsole(@NotNull final CommandSender sender) {
        return sender instanceof ConsoleCommandSender;
    }
}