package com.badbones69.crazyenchantments.objects;

import com.badbones69.crazyenchantments.CrazyPlugin;
import com.ryderbelserion.fusion.core.FusionCore;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.core.files.FileManager;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import us.crazycrew.crazyenchantments.interfaces.IMessage;
import us.crazycrew.crazyenchantments.ICrazyProvider;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message implements IMessage {

    private final CrazyPlugin plugin = (CrazyPlugin) ICrazyProvider.getInstance();

    private final FusionCore fusion = this.plugin.getFusion();

    private final FileManager fileManager = this.fusion.getFileManager();

    private final StringUtils utils = this.fusion.getStringUtils();

    private final String defaultValue;
    private final String value;
    private final Path location;
    private final Object[] path;

    public Message(@NotNull final CommentedConfigurationNode configuration, @NotNull final Path location, @NotNull final String defaultValue, @NotNull final Object... path) {
        this.defaultValue = defaultValue;

        final CommentedConfigurationNode root = configuration.node(this.path = path);

        this.value = root.isList() ? this.utils.toString(getStringList(root)) : root.getString(this.defaultValue); // store pre-fetch the value from the default Messages.yml

        this.location = location;
    }

    @Override
    public void broadcast(@NotNull final Audience audience, @NotNull final Map<String, String> placeholders) {
        final Component component = getComponent(audience, placeholders);

        if (component.equals(Component.empty())) return;

        this.plugin.broadcast(component);
    }

    @Override
    public void send(@NotNull final Audience audience, @NotNull final Map<String, String> placeholders) {
        final Component component = getComponent(audience, placeholders);

        if (component.equals(Component.empty())) return;

        switch (this.plugin.getMessageType()) {
            case "send_actionbar" -> audience.sendActionBar(component);
            case "send_message" -> audience.sendMessage(component);
        }
    }

    @Override
    public void send(@NotNull final Audience audience) {
        send(audience, new HashMap<>());
    }

    @Override
    public Component getComponent(@NotNull final Audience audience, @NotNull final Map<String, String> placeholders) {
        return parse(this.value, audience, placeholders);
    }

    @Override
    public Component getComponent(@NotNull final Audience audience) {
        return getComponent(audience, new HashMap<>());
    }

    @Override
    public void migrate() {
        this.fileManager.getYamlFile(this.location).ifPresentOrElse(action -> {
            final CommentedConfigurationNode configuration = action.getConfiguration();

            final CommentedConfigurationNode section = configuration.node(this.path);

            if (section.isList()) {
                try {
                    section.set(this.path).setList(String.class, this.utils.convertLegacy(getStringList(section)));
                } catch (final SerializationException exception) {
                    this.fusion.log("warn", "Failed to migrate %s in %s".formatted(this.path, this.location), exception);
                }
            } else {
                try {
                    section.set(this.path).set(String.class, this.utils.convertLegacy(section.node(this.path).getString(this.defaultValue)));
                } catch (final SerializationException exception) {
                    this.fusion.log("warn", "Failed to migrate %s in %s".formatted(this.path, this.location), exception);
                }
            }
        }, () -> this.fusion.log("warn", "Failed to migrate %s in %s".formatted(this.path, this.location)));
    }

    private @NotNull Component parse(@NotNull final String message, @NotNull final Audience audience, @NotNull final Map<String, String> placeholders) {
        final String prefix = this.plugin.getPrefix();

        if (prefix != null) {
            placeholders.putIfAbsent("{prefix}", prefix);
        }

        return this.fusion.parse(audience, replace(message), placeholders);
    }

    private @NotNull List<String> getStringList(@NotNull final CommentedConfigurationNode node) {
        try {
            final List<String> list = node.getList(String.class);

            return list != null ? list : List.of(this.defaultValue);
        } catch (SerializationException exception) {
            throw new FusionException(String.format("Failed to serialize %s!", node.path()), exception);
        }
    }

    private String replace(@NotNull final String message) {
        return message.replaceAll("%command%", "{command}")
                .replaceAll("%usage%", "{usage}")
                .replaceAll("%xp%", "{xp}")
                .replaceAll("%XP%", "{xp}")
                .replaceAll("%slot%", "{slot}")
                .replaceAll("%enchantment%", "{enchantment}")
                .replaceAll("%amount%", "{amount}")
                .replaceAll("%player%", "{player}")
                .replaceAll("%category%", "{category}")
                .replaceAll("%found%", "{found}")
                .replaceAll("%kit%", "{kit}")
                .replaceAll("%day%", "{day}")
                .replaceAll("%hour%", "{hour}")
                .replaceAll("%minute%", "{minute}")
                .replaceAll("%second%", "{second}")
                .replaceAll("%world%", "{world}")
                .replaceAll("%x%", "{x}")
                .replaceAll("%y%", "{y}")
                .replaceAll("%z%", "{z}")
                .replaceAll("%gkit%", "{kit}")
                .replaceAll("%enchant%", "{enchant}")
                .replaceAll("%level%", "{level}")
                .replaceAll("%bypass%", "{bypass}")
                .replaceAll("%vanilla%", "{vanilla}")
                .replaceAll("%limit%", "{limit}")
                .replaceAll("%baseLimit%", "{base_limit}")
                .replaceAll("%slotCrystal%", "{slot_crystal}")
                .replaceAll("%item%", "{item}")
                .replaceAll("%canHave%", "{max_enchants}")
                .replaceAll("%space%", "{space}")
                .replaceAll("%limitSetInConfig%", "{config_limit}")
                .replaceAll("%Arg%", "{arg}")
                .replaceAll("%itemEnchants%", "{item_enchants}")
                .replaceAll("%Money_Needed%", "{money_needed")
                .replaceAll("%maxEnchants%", "{max_enchants}")
                .replaceAll("%enchantAmount%", "{enchant_amount}")
                .replaceAll("%baseEnchants%", "{base_enchants}");
    }
}