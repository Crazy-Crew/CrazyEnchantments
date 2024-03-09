package com.badbones69.crazyenchantments.paper.api.enums;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazyenchantments.ConfigManager;
import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DustData;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.platform.impl.Config;
import com.google.gson.Gson;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public enum Dust {

    SUCCESS_DUST(List.of("s", "success"), "Success-Dust", Config.success_dust_min_percent_range, Config.success_dust_max_percent_range, Config.success_dust_item, Config.success_dust_name, Config.success_dust_lore),
    DESTROY_DUST(List.of("d", "destroy"),"Destroy-Dust", Config.destroy_dust_min_percent_range, Config.destroy_dust_max_percent_range, Config.destroy_dust_item, Config.destroy_dust_name, Config.destroy_dust_lore),
    MYSTERY_DUST(List.of("m", "mystery"), "Mystery-Dust", Config.mystery_dust_min_percent_range, Config.mystery_dust_max_percent_range, Config.mystery_dust_item, Config.mystery_dust_name, Config.mystery_dust_lore),
    FAILED_DUST(List.of("f", "failed"), "Failed-Dust", Config.failed_dust_item, Config.failed_dust_name, Config.failed_dust_lore);
    
    private static final Map<Dust, ItemBuilder> itemBuilderDust = new HashMap<>();

    private final String name;
    private final List<String> knownNames;
    private final List<String> itemLore;
    private final Material material;
    private final String itemName;

    private int max;
    private int min;

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Methods methods = this.plugin.getStarter().getMethods();

    @NotNull
    private final SettingsManager config = ConfigManager.getConfig();

    Dust(List<String> knownNames, String name, Property<Integer> min, Property<Integer> max, Property<String> material, Property<String> displayName, Property<List<String>> lore) {
        this.knownNames = knownNames;
        this.name = name;

        this.min = this.config.getProperty(min);
        this.max = this.config.getProperty(max);

        this.material = Material.matchMaterial(this.config.getProperty(material));

        this.itemName = this.config.getProperty(displayName);

        this.itemLore = this.config.getProperty(lore);
    }

    Dust(List<String> knownNames, String name, Property<String> material, Property<String> displayName, Property<List<String>> lore) {
        this.knownNames = knownNames;
        this.name = name;

        this.material = Material.matchMaterial(this.config.getProperty(material));

        this.itemName = this.config.getProperty(displayName);

        this.itemLore = this.config.getProperty(lore);
    }
    
    public static void loadDust() {
        itemBuilderDust.clear();

        for (Dust dust : values()) {
            ItemBuilder itemStack = new ItemBuilder()
                    .setMaterial(dust.material)
                    .setName(dust.itemName)
                    .setLore(dust.itemLore);

            Dust.itemBuilderDust.put(dust, itemStack);
        }
    }
    
    public static Dust getFromName(String nameString) {
        for (Dust dust : Dust.values()) {
            if (dust.getKnownNames().contains(nameString.toLowerCase()) || dust.getConfigName().contains(nameString)) return dust;
        }

        return null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<String> getKnownNames() {
        return this.knownNames;
    }
    
    public String getConfigName() {
        return getName().replaceAll("-", "");
    }
    
    public ItemStack getDust() {
        return getDust(1);
    }
    
    public ItemStack getDust(int amount) {
        return getDust(this.methods.percentPick(this.max, this.min), amount);
    }
    
    public ItemStack getDust(int percent, int amount) {
        ItemStack item = itemBuilderDust.get(this).addLorePlaceholder("%Percent%", String.valueOf(percent)).setAmount(amount).build();

        if (Objects.equals(getName(), FAILED_DUST.getName())) return item;

        // PDC Start
        Gson gson = new Gson();

        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(DataKeys.dust.getNamespacedKey(), PersistentDataType.STRING, gson.toJson(new DustData(getConfigName(), this.min, this.max, percent)));
        item.setItemMeta(meta);
        // PDC End

        return item;
    }
}