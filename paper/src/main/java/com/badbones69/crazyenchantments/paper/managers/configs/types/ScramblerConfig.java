package com.badbones69.crazyenchantments.paper.managers.configs.types;

import com.badbones69.crazyenchantments.paper.api.utils.ConfigUtils;
import com.badbones69.crazyenchantments.paper.managers.configs.interfaces.IConfig;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScramblerConfig extends IConfig {

    private final boolean isScramblerAnimationEnabled;
    private final String scramblerAnimationName;

    private final List<String> scramblerGuiLore;
    private final String scramblerGuiName;

    private final List<String> scramblerLore;
    private final String scramblerName;
    private final String scramblerType;
    private final boolean isGlowing;

    private final String pointerItem;
    private final String pointerName;
    private final List<String> pointerLore;

    public ScramblerConfig(@NotNull final ConfigurationSection section) {
        this.isScramblerAnimationEnabled = section.getBoolean("GUI.Toggle", true);
        this.scramblerAnimationName = section.getString("GUI.Name", "<dark_gray>Rolling the <yellow>Scrambler!");

        this.scramblerGuiName = section.getString("GUIName", "<yellow><bold>The Grand Scrambler");
        this.scramblerGuiLore = ConfigUtils.getStringList(section, List.of(
                "<gray>The <yellow><bold>The Grand Scrambler <gray>will allow",
                "<gray>you to re-roll the destroy and success rates.",
                "<gray>Drag and drop it on an enchantment book",
                "<gray>to get a new destroy and success rate.",
                "",
                "<yellow>Cost: <yellow><bold>$800"
        ), "GUILore");

        this.scramblerName = section.getString("Name", "<yellow><bold>The Grand Scrambler");
        this.scramblerLore = ConfigUtils.getStringList(section, List.of(
                "<gray>The <yellow><bold>The Grand Scrambler <gray>will allow",
                "<gray>you to re-roll the destroy and success rates.",
                "<gray>Drag and drop it on an enchantment book",
                "<gray>to get a new destroy and success rate."
        ), "Lore");

        this.scramblerType = section.getString("Item", "SUNFLOWER");
        this.isGlowing = section.getBoolean("Glowing", false);

        this.pointerItem = section.getString("Pointer.Item", "REDSTONE_TORCH");
        this.pointerName = section.getString("Pointer.Name", "<red><bold>Pointer");
        this.pointerLore = ConfigUtils.getStringList(section, List.of(
                "<gray>Whatever percents the item lands on",
                "<gray>will be the new percents on your book."
        ), "Pointer.Lore");
    }

    public @NotNull final Component asGuiAnimationComponent(@Nullable final Audience player) {
        return this.fusion.parse(player, this.scramblerAnimationName);
    }

    public final boolean isScramblerAnimationEnabled() {
        return this.isScramblerAnimationEnabled;
    }

    public @NotNull final List<Component> asPointerComponents(@Nullable final Audience player) {
        return asComponents(player, this.pointerLore);
    }

    public @NotNull final Component asPointerComponent(@Nullable final Audience player) {
        return asComponent(player, this.pointerName);
    }

    public @NotNull final String getPointerItem() {
        return this.pointerItem;
    }

    public @NotNull final List<Component> asGuiComponents(@Nullable final Audience player) {
        return asComponents(player, this.scramblerGuiLore);
    }

    public @NotNull final Component asGuiComponent(@Nullable final Audience player) {
        return asComponent(player, this.scramblerGuiName);
    }

    public @NotNull final List<Component> asItemComponents(@Nullable final Audience player) {
        return asComponents(player, this.scramblerLore);
    }

    public @NotNull final Component asItemComponent(@Nullable final Audience player) {
        return asComponent(player, this.scramblerName);
    }

    public @NotNull final String getScramblerType() {
        return this.scramblerType;
    }

    public final boolean isGlowing() {
        return this.isGlowing;
    }
}