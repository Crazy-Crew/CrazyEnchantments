package com.ryderbelserion.crazyenchantments.paper.objects;

import com.ryderbelserion.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.paper.api.interfaces.IMessage;
import com.ryderbelserion.crazyenchantments.paper.enums.Files;
import com.ryderbelserion.fusion.kyori.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.List;
import java.util.Map;

public class Message implements IMessage {

    private @NotNull final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);
    private @NotNull final CommentedConfigurationNode config = Files.config.getConfig();
    private @NotNull final FusionPaper paper = this.plugin.getPaper();

    private final String message;
    private final Map<String, String> placeholders;

    public Message(@NotNull final String message, @NotNull final Map<String, String> placeholders) {
        this.message = message;
        this.placeholders = placeholders;

        this.placeholders.putIfAbsent("{prefix}", this.config.node("root", "prefix").getString("<dark_gray>[<green>CrazyEnchantments</green>]</dark_gray> <reset>"));
    }

    public Message(@NotNull final List<String> message, @NotNull final Map<String, String> placeholders) {
        this(StringUtils.toString(message), placeholders);
    }

    @Override
    public void broadcast(@NotNull final Audience audience) {
        final Component component = parse(audience);

        if (component.equals(Component.empty())) return;

        this.plugin.getServer().broadcast(component);
    }

    @Override
    public void send(@NotNull final Audience audience) {
        final Component component = parse(audience);

        if (component.equals(Component.empty())) return;

        switch (this.config.node("root", "message-action").getString("send_message")) {
            case "send_actionbar" -> audience.sendActionBar(component);
            case "send_message" -> audience.sendMessage(component);
        }
    }

    private @NotNull Component parse(@NotNull final Audience audience) {
        return this.paper.color(audience, this.message, this.placeholders);
    }

    @Override
    public @NotNull final String toString() {
        return "CrazyMessage{message=" + this.message + "}";
    }
}