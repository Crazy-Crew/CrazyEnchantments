package com.ryderbelserion.crazyenchantments.core.registry;

import com.ryderbelserion.crazyenchantments.api.interfaces.IMessage;
import com.ryderbelserion.crazyenchantments.core.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.core.enums.constants.Messages;
import com.ryderbelserion.crazyenchantments.core.objects.Message;
import com.ryderbelserion.crazyenchantments.api.interfaces.registry.IMessageRegistry;
import com.ryderbelserion.fusion.core.FusionProvider;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.*;

public class MessageRegistry implements IMessageRegistry {

    private final FusionKyori fusion = (FusionKyori) FusionProvider.getInstance();

    private final Path path = this.fusion.getDataPath();

    private final FileManager fileManager = this.fusion.getFileManager();

    private final Map<Key, Map<Key, IMessage>> messages = new HashMap<>(); // locale key, (message key, message context)

    @Override
    public void addMessage(@NotNull final Key locale, @NotNull final Key key, @NotNull final IMessage message) {
        this.fusion.log("info", "Registering the message @ {} for {}", locale.asString(), key.asString());

        final Map<Key, IMessage> keys = this.messages.getOrDefault(locale, new HashMap<>());

        keys.put(key, message);

        this.messages.put(locale, keys);
    }

    @Override
    public void removeMessage(@NotNull final Key key) {
        if (!this.messages.containsKey(key)) {
            this.fusion.log("warn", "No message with key {}", key.asString());

            return;
        }

        this.fusion.log("info", "Unregistering the message {}", key.asString());

        this.messages.remove(key);
    }

    @Override
    public @NotNull final IMessage getMessage(@NotNull final Key locale, @NotNull final Key key) {
        return this.messages.getOrDefault(locale, this.messages.get(Messages.default_locale)).get(key);
    }

    @Override
    public @NotNull final IMessage getMessage(@NotNull final Key key) { // only used for console command sender
        return this.messages.get(Messages.default_locale).get(key);
    }

    public @NotNull final Map<Key, Map<Key, IMessage>> getMessages() {
        return Collections.unmodifiableMap(this.messages);
    }

    @Override
    public void init() {
        this.messages.clear();

        final List<Path> paths = this.fusion.getFiles(this.path.resolve("locale"), ".yml", 1);

        paths.add(this.path.resolve("messages.yml")); // add to list

        for (final Path path : paths) {
            this.fileManager.getYamlFile(path).ifPresentOrElse(file -> {
                final String fileName = file.getFileName();

                final Key key = Key.key(CrazyEnchantments.namespace, fileName.equalsIgnoreCase("messages.yml") ? "default" : fileName.toLowerCase());

                final CommentedConfigurationNode configuration = file.getConfiguration();

                addMessage(key, Messages.reload_plugin, new Message(configuration, "{prefix}<yellow>You have reloaded the plugin!", "messages", "reload-plugin"));
                addMessage(key, Messages.feature_disabled, new Message(configuration, "{prefix}<red>This feature is disabled.", "messages", "feature-disabled"));
                addMessage(key, Messages.must_be_console_sender, new Message(configuration, "{prefix}<red>You must be using console to use this command.", "messages", "player", "requirements", "must-be-console-sender"));
                addMessage(key, Messages.must_be_player, new Message(configuration, "{prefix}<red>You must be a player to use this command.", "messages", "player", "requirements", "must-be-player"));
                addMessage(key, Messages.target_not_online, new Message(configuration, "{prefix}<red>This feature is disabled.", "messages", "player", "target-not-online"));
                addMessage(key, Messages.no_permission, new Message(configuration, "{prefix}<red>You do not have permission to use that command!", "messages", "player", "no-permission"));
            }, () -> this.fusion.log("warn", "Path %s not found in cache.".formatted(path)));
        }
    }
}