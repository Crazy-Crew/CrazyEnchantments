package com.ryderbelserion.crazyenchantments.core.registry;

import com.ryderbelserion.crazyenchantments.api.interfaces.IMessage;
import com.ryderbelserion.crazyenchantments.api.interfaces.registry.IMessageRegistry;
import com.ryderbelserion.crazyenchantments.core.constants.MessageKeys;
import com.ryderbelserion.crazyenchantments.core.objects.Message;
import com.ryderbelserion.fusion.core.FusionProvider;
import com.ryderbelserion.fusion.core.api.FusionCore;
import com.ryderbelserion.fusion.core.api.interfaces.ILogger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class MessageRegistry implements IMessageRegistry {

    private final FusionCore fusion = FusionProvider.get();

    private final ILogger logger = this.fusion.getLogger();

    private final Map<Key, IMessage> messages = new HashMap<>();

    private final UserRegistry userRegistry;

    public MessageRegistry(@NotNull final UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    @Override
    public void addMessage(@NotNull final Key key, @NotNull final IMessage message) {
        this.logger.safe("Registering the message {}", key.asString());

        this.messages.put(key, message);
    }

    @Override
    public void removeMessage(@NotNull final Key key) {
        if (!this.messages.containsKey(key)) {
            this.logger.warn("No message with key {}", key.asString());

            return;
        }

        this.logger.safe("Unregistering the message {}", key.asString());

        this.messages.remove(key);
    }

    @Override
    public @NotNull final IMessage getMessage(@NotNull final Key key) {
        return this.messages.get(key);
    }

    @Override
    public void init() {
        this.messages.clear();

        addMessage(MessageKeys.reload_plugin, new Message(this.userRegistry, "{prefix}<yellow>You have reloaded the plugin!", "messages", "reload-plugin"));
        addMessage(MessageKeys.feature_disabled, new Message(this.userRegistry, "{prefix}<red>This feature is disabled.", "messages", "feature-disabled"));
        addMessage(MessageKeys.must_be_console_sender, new Message(this.userRegistry, "{prefix}<red>You must be using console to use this command.", "messages", "player", "requirements", "must-be-console-sender"));
        addMessage(MessageKeys.must_be_player, new Message(this.userRegistry, "{prefix}<red>You must be a player to use this command.", "messages", "player", "requirements", "must-be-player"));
        addMessage(MessageKeys.target_not_online, new Message(this.userRegistry, "{prefix}<red>This feature is disabled.", "messages", "player", "target-not-online"));
        addMessage(MessageKeys.target_same_player, new Message(this.userRegistry, "{prefix}<red>You cannot use this command on yourself.", "messages", "player", "target-same-player"));
        addMessage(MessageKeys.no_permission, new Message(this.userRegistry, "{prefix}<red>You do not have permission to use that command!", "messages", "player", "no-permission"));
        addMessage(MessageKeys.inventory_not_empty, new Message(this.userRegistry, "{prefix}<red>Inventory is not empty, Please clear up some room.", "messages", "player", "inventory-not-empty"));
    }
}