package com.badbones69.crazyenchantments.paper.managers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.builders.types.gkitz.KitsMenu;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import java.util.*;

public class KitsManager {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final PluginManager pluginManager = this.plugin.getPluginManager();

    private final ConfigManager options = this.plugin.getOptions();

    private final FusionPaper fusion = this.plugin.getFusion();

    private final List<GKitz> kits = new ArrayList<>();

    private boolean isRegistered = false;

    public void init() {
        if (!this.options.isGkitzToggle()) {
            this.kits.clear();

            return;
        }

        final YamlConfiguration kits = FileKeys.gkitz.getYamlConfiguration();

        final ConfigurationSection section = kits.getConfigurationSection("GKitz");

        if (section == null) {
            this.fusion.log("warn", "The gkitz section cannot be found in gkitz.yml, It's possible the file is badly formatted!");

            return;
        }

        this.kits.clear();

        for (final String kitName : section.getKeys(false)) {
            final ConfigurationSection kitSection = section.getConfigurationSection(kitName);

            if (kitSection == null) continue; //todo debug

            int slot = kitSection.getInt("Display.Slot", -1);

            if (slot == -1) continue; //todo debug

            final ItemStack displayItem = new ItemBuilder()
                    .setMaterial(kitSection.getString("Display.Item", ColorUtils.getRandomPaneColor().getName()))
                    .setName(kitSection.getString("Display.Name", "The config option for the Display Name is not present for %s".formatted(kitName)))
                    .setLore(kitSection.getStringList("Display.Lore"))
                    .setGlow(kitSection.getBoolean("Display.Glowing", false))
                    .addKey(DataKeys.gkit_type.getNamespacedKey(), kitName)
                    .build();

            final GKitz kit = new GKitz(
                    kitName,
                    displayItem,
                    slot,
                    kitSection.getBoolean("Auto-Equip", false),
                    kitSection.getString("Cooldown", ""),
                    kitSection.getStringList("Commands"),
                    kitSection.getStringList("Items"),
                    kitSection.getStringList("Fake-Items")
            );

            addKit(kit);
        }

        if (this.isRegistered) return;

        this.pluginManager.registerEvents(new KitsMenu.KitsListener(), this.plugin);

        this.isRegistered = true;
    }

    public void addKit(@NotNull final GKitz kit) {
        this.kits.add(kit);
    }

    public void removeKit(@NotNull final GKitz kit) {
        this.kits.remove(kit);
    }

    public GKitz getKitByName(@NotNull final String kitName) {
        GKitz kit = null;

        for (final GKitz value : this.kits) {
            if (!value.getName().equalsIgnoreCase(kitName)) continue;

            kit = value;

            break;
        }

        return kit;
    }

    public @NotNull final List<GKitz> getKits() {
        return Collections.unmodifiableList(this.kits);
    }
}