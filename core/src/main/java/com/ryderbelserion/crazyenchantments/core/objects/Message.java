package com.ryderbelserion.crazyenchantments.core.objects;

import com.ryderbelserion.crazyenchantments.api.CrazyEnchantmentsProvider;
import com.ryderbelserion.crazyenchantments.api.interfaces.IMessage;
import com.ryderbelserion.crazyenchantments.core.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.core.registry.UserRegistry;
import com.ryderbelserion.crazyenchantments.core.enums.Files;
import com.ryderbelserion.fusion.core.FusionProvider;
import com.ryderbelserion.fusion.core.exceptions.FusionException;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message implements IMessage {

    private final FusionKyori fusion = (FusionKyori) FusionProvider.getInstance();

    private final CrazyEnchantments plugin = (CrazyEnchantments) CrazyEnchantmentsProvider.getInstance();
    private final CommentedConfigurationNode config = Files.config.getConfig();
    private final CommentedConfigurationNode messages = Files.messages.getConfig();

    private final UserRegistry userRegistry;

    private final String defaultValue;
    private final Object[] path;

    public Message(@NotNull final UserRegistry userRegistry, @NotNull final String defaultValue, @NotNull final Object... path) {
        this.userRegistry = userRegistry;

        // config data
        this.defaultValue = defaultValue;
        this.path = path;
    }

    @Override
    public void broadcast(@NotNull final Audience audience, @NotNull final Map<String, String> placeholders) {
        final Component component = getComponent(audience, placeholders);

        if (component.equals(Component.empty())) return;

        this.plugin.broadcast(component);
    }

    @Override
    public void broadcast(@NotNull final Audience audience) {
        broadcast(audience, new HashMap<>());
    }

    @Override
    public void send(@NotNull final Audience audience, @NotNull final Map<String, String> placeholders) {
        final Component component = getComponent(audience, placeholders);

        if (component.equals(Component.empty())) return;

        switch (this.config.node("root", "message-action").getString("send_message")) {
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
        if (this.plugin.isConsoleSender(audience)) {
            final CommentedConfigurationNode config = this.messages.node(this.path);

            return parse(config.isList() ? StringUtils.toString(getStringList(config)) : config.getString(this.defaultValue), audience, placeholders);
        }

        final User user = this.userRegistry.getUser(audience);

        final CommentedConfigurationNode node = user.locale().node(this.path);

        return parse(node.isList() ? StringUtils.toString(getStringList(node)) : node.getString(this.defaultValue), audience, placeholders);
    }

    @Override
    public Component getComponent(@NotNull final Audience audience) {
        return getComponent(audience, new HashMap<>());
    }

    private @NotNull Component parse(@NotNull final String message, @NotNull final Audience audience, @NotNull final Map<String, String> placeholders) {
        placeholders.putIfAbsent("{prefix}", this.config.node("root", "prefix").getString("<blue>[<gold>CrazyEnchantments<blue>] <reset>"));

        return this.fusion.parse(audience, message, placeholders);
    }

    private @NotNull List<String> getStringList(@NotNull final CommentedConfigurationNode node) {
        try {
            final List<String> list = node.getList(String.class);

            return list != null ? list : List.of(this.defaultValue);
        } catch (final SerializationException exception) {
            throw new FusionException(String.format("Failed to serialize %s!", node.path()), exception);
        }
    }
}