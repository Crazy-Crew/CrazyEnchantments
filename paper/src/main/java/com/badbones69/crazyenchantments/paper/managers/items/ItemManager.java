package com.badbones69.crazyenchantments.paper.managers.items;

import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.managers.items.interfaces.CustomItem;
import com.badbones69.crazyenchantments.paper.managers.items.objects.CloseItem;
import com.badbones69.crazyenchantments.paper.managers.items.objects.PointerItem;
import com.badbones69.crazyenchantments.paper.managers.items.objects.crystals.ProtectionCrystalItem;
import com.badbones69.crazyenchantments.paper.managers.items.objects.ScramblerItem;
import com.badbones69.crazyenchantments.paper.managers.items.objects.crystals.SlotCrystalItem;
import com.badbones69.crazyenchantments.paper.managers.items.objects.navigation.NavigationItem;
import com.badbones69.crazyenchantments.paper.managers.items.objects.tinker.TinkerBottleItem;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemManager {

    private final Map<String, CustomItem> items = new HashMap<>();

    private boolean scramblerAnimation;
    private String scramblerGuiName;

    public void init() {
        final YamlConfiguration config = FileKeys.config.getYamlConfiguration();

        addItem("protection_crystal_item", new ProtectionCrystalItem());
        addItem("back_button_right", new NavigationItem(true));
        addItem("back_button_left", new NavigationItem(false));
        addItem("tinker_exp_bottle", new TinkerBottleItem());
        addItem("slot_crystal_item", new SlotCrystalItem());
        addItem("scrambler_item", new ScramblerItem());
        addItem("pointer_item", new PointerItem());
        addItem("close_item", new CloseItem());

        //this.scramblerGuiName = ColorUtils.color(config.getString("Settings.Scrambler.GUI.Name", "<dark_gray>Rolling the <yellow>Scrambler")); //todo() legacy trash, we need papi support as well.
        this.scramblerAnimation = config.getBoolean("Settings.Scrambler.GUI.Toggle", true);
    }

    public void addItem(@NotNull final String item, @NotNull final CustomItem customItem) {
        this.items.putIfAbsent(item, customItem);
    }

    public void reloadItems() {
        this.items.forEach((name, customItem) -> customItem.init());
    }

    public Optional<CustomItem> getItem(@NotNull final String item) {
        return Optional.of(this.items.get(item));
    }

    public @NotNull final Map<String, CustomItem> getItems() {
        return Collections.unmodifiableMap(this.items);
    }

    public void removeItem(@NotNull final String item) {
        this.items.remove(item);
    }

    public @NotNull final String getScramblerGuiName() {
        return this.scramblerGuiName;
    }

    public final boolean isScramblerAnimation() {
        return this.scramblerAnimation;
    }
}