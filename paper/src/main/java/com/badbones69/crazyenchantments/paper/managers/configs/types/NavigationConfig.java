package com.badbones69.crazyenchantments.paper.managers.configs.types;

import com.badbones69.crazyenchantments.paper.api.utils.ConfigUtils;
import com.badbones69.crazyenchantments.paper.managers.configs.interfaces.IConfig;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class NavigationConfig extends IConfig {

    private final String navigationRightItem;
    private final String navigationRightPlayer;
    private final String navigationRightName;
    private final List<String> navigationRightLore;

    private final String navigationLeftItem;
    private final String navigationLeftPlayer;
    private final String navigationLeftName;
    private final List<String> navigationLeftLore;

    public NavigationConfig(@NotNull final ConfigurationSection section) {
        this.navigationRightItem = section.getString("Right.Item", "NETHER_STAR");
        this.navigationRightPlayer = section.getString("Right.Player", "");
        this.navigationRightName = section.getString("Right.Name", "<gray><bold><<<aqua><bold>Back");
        this.navigationRightLore = ConfigUtils.getStringList(section, "Right.Lore");

        this.navigationLeftItem = section.getString("Left.Item", "NETHER_STAR");
        this.navigationLeftPlayer = section.getString("Left.Player", "");
        this.navigationLeftName = section.getString("Left.Name", "<aqua><bold>Back<gray><bold>>>");
        this.navigationLeftLore = ConfigUtils.getStringList(section, "Left.Lore");
    }

    public @NotNull final List<Component> getNavigationRightLore(@Nullable final Audience player) {
        return asComponents(player, this.navigationRightLore);
    }

    public @NotNull final Component getNavigationRightName(@Nullable final Audience player) {
        return asComponent(player, this.navigationRightName);
    }

    public @NotNull final String getNavigationRightPlayer() {
        return this.navigationRightPlayer;
    }

    public @NotNull final String getNavigationRightItem() {
        return this.navigationRightItem;
    }

    public @NotNull final List<Component> getNavigationLeftLore(@Nullable final Audience player) {
        return asComponents(player, this.navigationLeftLore);
    }

    public @NotNull final Component getNavigationLeftName(@Nullable final Audience player) {
        return asComponent(player, this.navigationLeftName);
    }

    public @NotNull final String getNavigationLeftPlayer() {
        return this.navigationLeftPlayer;
    }

    public @NotNull final String getNavigationLeftItem() {
        return this.navigationLeftItem;
    }
}