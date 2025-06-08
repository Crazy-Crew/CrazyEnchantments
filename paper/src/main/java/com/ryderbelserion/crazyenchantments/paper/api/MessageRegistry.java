package com.ryderbelserion.crazyenchantments.paper.api;

import com.ryderbelserion.crazyenchantments.paper.api.interfaces.IMessage;
import com.ryderbelserion.crazyenchantments.paper.enums.Files;
import com.ryderbelserion.crazyenchantments.paper.objects.Message;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.HashMap;
import java.util.Map;

public class MessageRegistry {

    private final Map<Key, IMessage> messages = new HashMap<>();

    public void addMessage(@NotNull final Key key, @NotNull final IMessage message) {
        this.messages.put(key, message);
    }

    public void removeMessage(@NotNull final Key key) {
        this.messages.remove(key);
    }

    public void populateMessages() {
        purgeMessages();

        final CommentedConfigurationNode config = Files.messages.getConfig();

        final CommentedConfigurationNode section = config.node("messages");

        addMessage(MessageKeys.reload_plugin, new Message(
                section.node("reload-plugin").getString("{prefix}<yellow>You have reloaded the plugin!"),
                new HashMap<>()
        ));

        addMessage(MessageKeys.feature_disabled, new Message(
                section.node("feature-disabled").getString("{prefix}<red>This feature is disabled."),
                new HashMap<>()
        ));

        addMessage(MessageKeys.must_be_console_sender, new Message(
                section.node("player", "requirements", "must-be-console-sender").getString("{prefix}<red>You must be using console to use this command."),
                new HashMap<>()));
        addMessage(MessageKeys.must_be_player, new Message(
                section.node("player", "requirements", "must-be-player").getString("{prefix}<red>You must be a player to use this command."),
                new HashMap<>()));

        addMessage(MessageKeys.target_not_online, new Message(
                section.node("target-not-online").getString("{prefix}<red>This feature is disabled."),
                new HashMap<>()
        ));

        addMessage(MessageKeys.target_same_player, new Message(
                section.node("target-same-player").getString("{prefix}<red>You cannot use this command on yourself."),
                new HashMap<>()
        ));

        addMessage(MessageKeys.no_permission, new Message(
                section.node("no-permission").getString("{prefix}<red>You do not have permission to use that command!"),
                new HashMap<>()
        ));

        addMessage(MessageKeys.inventory_not_empty, new Message(
                section.node("inventory-not-empty").getString("{prefix}<red>Inventory is not empty, Please clear up some room."),
                new HashMap<>()
        ));
    }

    public void purgeMessages() {
        this.messages.clear();
    }

    public @NotNull final IMessage getMessage(@NotNull final Key key) {
        return this.messages.get(key);
    }
}