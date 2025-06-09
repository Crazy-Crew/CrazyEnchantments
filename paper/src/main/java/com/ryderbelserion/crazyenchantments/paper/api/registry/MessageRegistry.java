package com.ryderbelserion.crazyenchantments.paper.api.registry;

import com.ryderbelserion.crazyenchantments.paper.api.MessageKeys;
import com.ryderbelserion.crazyenchantments.paper.api.interfaces.IMessage;
import com.ryderbelserion.crazyenchantments.paper.api.objects.Message;
import com.ryderbelserion.fusion.core.FusionCore;
import com.ryderbelserion.fusion.core.api.interfaces.ILogger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;

public class MessageRegistry {

    private final FusionCore core = FusionCore.Provider.get();

    private final ILogger logger = this.core.getLogger();

    private final Map<Key, IMessage> messages = new HashMap<>();

    public void addMessage(@NotNull final Key key, @NotNull final IMessage message) {
        this.logger.safe("Registering the message {}", key.asString());

        this.messages.put(key, message);
    }

    public void removeMessage(@NotNull final Key key) {
        if (!this.messages.containsKey(key)) {
            this.logger.warn("No message with key {}", key.asString());

            return;
        }

        this.logger.safe("Unregistering the message {}", key.asString());

        this.messages.remove(key);
    }

    @ApiStatus.Internal
    public void populateMessages() {
        purgeMessages();

        addMessage(MessageKeys.reload_plugin, new Message("{prefix}<yellow>You have reloaded the plugin!", "messages", "reload-plugin"));
        addMessage(MessageKeys.feature_disabled, new Message("{prefix}<red>This feature is disabled.", "messages", "feature-disabled"));
        addMessage(MessageKeys.must_be_console_sender, new Message("{prefix}<red>You must be using console to use this command.", "messages", "must_be_console_sender"));
        addMessage(MessageKeys.must_be_player, new Message("{prefix}<red>You must be a player to use this command.", "messages", "must_be_player"));
        addMessage(MessageKeys.target_not_online, new Message("{prefix}<red>This feature is disabled.", "messages", "target_not_online"));
        addMessage(MessageKeys.target_same_player, new Message("{prefix}<red>You cannot use this command on yourself.", "messages", "target_same_player"));
        addMessage(MessageKeys.no_permission, new Message("{prefix}<red>You do not have permission to use that command!", "messages", "no_permission"));
        addMessage(MessageKeys.inventory_not_empty, new Message("{prefix}<red>Inventory is not empty, Please clear up some room.", "messages", "inventory_not_empty"));
    }

    @ApiStatus.Internal
    public void purgeMessages() {
        if (!this.messages.isEmpty()) {
            this.logger.warn("Purging all existing message keys!");
        }

        this.messages.clear();
    }

    public @NotNull final IMessage getMessage(@NotNull final Key key) {
        return this.messages.get(key);
    }
}