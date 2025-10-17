package com.badbones69.crazyenchantments.paper.managers.configs.types.items;

import com.badbones69.crazyenchantments.paper.api.utils.ConfigUtils;
import com.badbones69.crazyenchantments.paper.managers.configs.interfaces.IConfig;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProtectionCrystalConfig extends IConfig {

    private final String protectionItem;
    private final String protectionName;
    private final List<String> protectionLore;
    private final boolean isGlowing;

    private final String protectionLine;

    public ProtectionCrystalConfig(@NotNull final ConfigurationSection section) {
        this.protectionItem = section.getString("Item", "EMERALD");
        this.protectionName = section.getString("Name", "");
        this.protectionLore = ConfigUtils.getStringList(section, List.of(
                "<gray>A rare crystal that is said to",
                "<gray>protect items from getting lost",
                "<gray>while the owners away in the after life.",
                "",
                "<gray><bold>(<gold><bold>!<gray><bold>) <gray>Drag and drop on an item."
        ), "Lore");

        this.isGlowing = section.getBoolean("Glowing", false);

        this.protectionLine = section.getString("Protected", "<gold>Ancient Protection");
    }

    public @NotNull final List<Component> asItemComponents(@Nullable final Audience player) {
        return asComponents(player, this.protectionLore);
    }

    public @NotNull final Component asItemComponent(@Nullable final Audience player) {
        return asComponent(player, this.protectionName);
    }

    public @NotNull final String getProtectionLine() {
        return this.protectionLine;
    }

    public @NotNull final String getProtectionType() {
        return this.protectionItem;
    }

    public final boolean isGlowing() {
        return this.isGlowing;
    }
}