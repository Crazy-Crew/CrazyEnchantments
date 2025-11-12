package com.ryderbelserion.crazyenchantments.core.objects;

import com.ryderbelserion.crazyenchantments.api.CrazyEnchantmentsProvider;
import com.ryderbelserion.crazyenchantments.core.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.core.enums.Files;
import com.ryderbelserion.crazyenchantments.api.interfaces.IMessage;
import com.ryderbelserion.fusion.core.FusionProvider;
import com.ryderbelserion.fusion.core.exceptions.FusionException;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
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

    private final CrazyEnchantments plugin = (CrazyEnchantments) CrazyEnchantmentsProvider.getInstance();
    private final FusionKyori fusion = (FusionKyori) FusionProvider.getInstance();

    private final String defaultValue;
    private final String value;

    private final YamlCustomFile customFile = Files.config.getYamlCustomFile();

    public Message(@NotNull final CommentedConfigurationNode configuration, @NotNull final String defaultValue, @NotNull final Object... path) {
        this.defaultValue = defaultValue;

        final CommentedConfigurationNode root = configuration.node(path);

        this.value = root.isList() ? StringUtils.toString(getStringList(root)) : root.getString(this.defaultValue); // store pre-fetch the value from the default Messages.yml
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

        switch (this.customFile.getStringValueWithDefault("send_message", "message-action")) {
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

    private @NotNull Component parse(@NotNull final String message, @NotNull final Audience audience, @NotNull final Map<String, String> placeholders) {
        final String prefix = this.customFile.getStringValueWithDefault("<dark_gray>[<green>CrazyEnchantments</green>]</dark_gray> <reset>", "root", "prefix");

        if (!prefix.isEmpty()) {
            placeholders.putIfAbsent("{prefix}", prefix);
        }

        return this.fusion.parse(audience, message, placeholders);
    }

    private @NotNull List<String> getStringList(@NotNull final CommentedConfigurationNode node) {
        try {
            final List<String> list = node.getList(String.class);

            return list != null ? list : List.of(this.defaultValue);
        } catch (SerializationException exception) {
            throw new FusionException(String.format("Failed to serialize %s!", node.path()), exception);
        }
    }
}