package com.ryderbelserion.crazyenchantments.paper.api.objects;

import com.ryderbelserion.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.paper.api.interfaces.IMessage;
import com.ryderbelserion.crazyenchantments.paper.api.enums.Files;
import com.ryderbelserion.crazyenchantments.paper.api.registry.UserRegistry;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.kyori.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Message implements IMessage {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();
    private final UserRegistry userRegistry = this.plugin.getUserRegistry();
    private final CommentedConfigurationNode config = Files.config.getConfig();
    private final CommentedConfigurationNode messages = Files.messages.getConfig();
    private final FusionPaper paper = this.plugin.getPaper();

    private final String defaultValue;
    private final Object[] path;

    public Message(@NotNull final String defaultValue, @NotNull final Object... path) {
        this.defaultValue = defaultValue;
        this.path = path;
    }

    @Override
    public void broadcast(@NotNull final Audience audience, @NotNull final Map<String, String> placeholders) {
        final Optional<User> user = this.userRegistry.getUser(audience);

        final CommentedConfigurationNode node = user.map(value -> value.locale().node(this.path)).orElseGet(() -> this.messages.node(this.path));

        final Component component = parse(node.isList() ? StringUtils.toString(getStringList(node)) : node.getString(this.defaultValue), audience, placeholders);

        if (component.equals(Component.empty())) return;

        this.plugin.getServer().broadcast(component);
    }

    @Override
    public void broadcast(@NotNull final Audience audience) {
        broadcast(audience, new HashMap<>());
    }

    @Override
    public void send(@NotNull final Audience audience, @NotNull final Map<String, String> placeholders) {
        final Optional<User> user = this.userRegistry.getUser(audience);

        final CommentedConfigurationNode node = user.map(value -> value.locale().node(this.path)).orElseGet(() -> this.messages.node(this.path));

        final Component component = parse(node.isList() ? StringUtils.toString(getStringList(node)) : node.getString(this.defaultValue), audience, placeholders);

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

    private @NotNull Component parse(@NotNull final String message, @NotNull final Audience audience, @NotNull final Map<String, String> placeholders) {
        placeholders.putIfAbsent("{prefix}", this.config.node("root", "prefix").getString("<dark_gray>[<green>CrazyEnchantments</green>]</dark_gray> <reset>"));

        return this.paper.color(audience, message, placeholders);
    }

    @Override
    public @NotNull final String toString() {
        return "CrazyMessage{message=" + Arrays.toString(this.path) + "}";
    }

    public @NotNull final List<String> getStringList(@NotNull final CommentedConfigurationNode node) {
        try {
            final List<String> list = node.getList(String.class);

            return list != null ? list : List.of(this.defaultValue);
        } catch (SerializationException exception) {
            throw new FusionException(String.format("Failed to serialize %s!", node.path()), exception);
        }
    }
}