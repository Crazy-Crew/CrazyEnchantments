package com.badbones69.crazyenchantments.paper.managers.configs.types.items;

import com.badbones69.crazyenchantments.paper.api.utils.ConfigUtils;
import com.badbones69.crazyenchantments.paper.managers.configs.interfaces.IConfig;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class SlotCrystalConfig extends IConfig {

    private final String slotCrystalItem;
    private final String slotCrystalName;
    private final List<String> slotCrystalLore;
    private final boolean isGlowing;

    public SlotCrystalConfig(@NotNull final ConfigurationSection section) {
        this.slotCrystalItem = section.getString("Item", "EMERALD");
        this.slotCrystalName = section.getString("Name", "<dark_purple><bold>Slot <aqua><bold>Crystal");
        this.slotCrystalLore = ConfigUtils.getStringList(section, List.of(
                "<gray>A rare crystal that is said to",
                "<gray>increase the amount of enchants",
                "<gray>that can be added onto an item.",
                "",
                "<gray><bold>(<gold><bold>!<gray><bold>) <gray>Drag and drop on an item."
        ), "Lore");

        this.isGlowing = section.getBoolean("Glowing", false);
    }

    public @NotNull final List<Component> asItemComponents(@Nullable final Audience player) {
        return asComponents(player, this.slotCrystalLore);
    }

    public @NotNull final Component asItemComponent(@Nullable final Audience player) {
        return asComponent(player, this.slotCrystalName);
    }

    public @NotNull final String getSlotType() {
        return this.slotCrystalItem;
    }

    public final boolean isGlowing() {
        return this.isGlowing;
    }
}