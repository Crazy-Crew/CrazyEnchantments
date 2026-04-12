package com.ryderbelserion.crazyenchantments.common.api.registry;

import com.ryderbelserion.crazyenchantments.api.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.api.constants.Messages;
import com.ryderbelserion.crazyenchantments.api.interfaces.registry.IMessageRegistry;
import com.ryderbelserion.crazyenchantments.common.CEPlugin;
import com.ryderbelserion.crazyenchantments.common.api.adapters.MessageAdapter;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageRegistry implements IMessageRegistry<MessageAdapter> {

    private final CEPlugin plugin = (CEPlugin) CrazyEnchantments.Provider.getInstance();

    private final FusionKyori fusion = this.plugin.getFusion();

    private final FileManager fileManager = this.plugin.getFileManager();

    private final Path path = this.plugin.getDataPath();

    private final Map<Key, Map<Key, MessageAdapter>> messages = new HashMap<>();

    @Override
    public MessageRegistry init() {
        this.messages.clear();

        final List<Path> paths = this.fileManager.getFilesByPath(this.path.resolve("locale"), ".yml", 1);

        paths.add(this.path.resolve("messages.yml")); // add to list

        for (final Path path : paths) {
            this.fileManager.getYamlFile(path).ifPresentOrElse(file -> {
                final String fileName = file.getFileName();

                final Key key = Key.key(CrazyEnchantments.namespace, fileName.equalsIgnoreCase("messages.yml") ? "default" : fileName.toLowerCase());

                final CommentedConfigurationNode configuration = file.getConfiguration();

                addMessage(key, Messages.reload_plugin, new MessageAdapter(configuration, "{prefix}<yellow>You have reloaded the plugin!", "messages", "reload-plugin"));
                addMessage(key, Messages.feature_disabled, new MessageAdapter(configuration, "{prefix}<red>This feature is disabled.", "messages", "feature-disabled"));
                addMessage(key, Messages.must_be_console_sender, new MessageAdapter(configuration, "{prefix}<red>You must be using console to use this command.", "messages", "player", "requirements", "must-be-console-sender"));
                addMessage(key, Messages.must_be_player, new MessageAdapter(configuration, "{prefix}<red>You must be a player to use this command.", "messages", "player", "requirements", "must-be-player"));
                addMessage(key, Messages.target_not_online, new MessageAdapter(configuration, "{prefix}<red>This feature is disabled.", "messages", "player", "target-not-online"));
                addMessage(key, Messages.no_permission, new MessageAdapter(configuration, "{prefix}<red>You do not have permission to use that command!", "messages", "player", "no-permission"));
            }, () -> this.fusion.log(Level.INFO, "Path %s not found in cache.".formatted(path)));
        }

        return this;
    }

    @Override
    public void addMessage(@NotNull final Key locale, @NotNull final Key key, @NotNull final MessageAdapter message) {
        this.fusion.log(Level.INFO, "Registering the message @ %s for %s".formatted(locale.asString(), key.asString()));

        final Map<Key, MessageAdapter> keys = this.messages.getOrDefault(locale, new HashMap<>());

        keys.put(key, message);

        this.messages.put(locale, keys);
    }

    @Override
    public void removeMessage(@NotNull final Key key) {
        this.messages.remove(key);
    }

    @Override
    public MessageAdapter getMessageByLocale(@NotNull final Key locale, @NotNull final Key key) {
        return this.messages.getOrDefault(locale, this.messages.get(Messages.default_locale)).get(key);
    }

    @Override
    public MessageAdapter getMessage(@NotNull final Key key) {
        return this.messages.get(Messages.default_locale).get(key);
    }
}