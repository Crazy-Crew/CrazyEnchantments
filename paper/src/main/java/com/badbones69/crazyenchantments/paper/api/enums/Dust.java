package com.badbones69.crazyenchantments.paper.api.enums;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DustData;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.ryderbelserion.crazyenchantments.enums.FileKeys;
import com.ryderbelserion.crazyenchantments.utils.ConfigUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public enum Dust {
    
    SUCCESS_DUST("Success-Dust", "SuccessDust", Arrays.asList("s", "success")),
    DESTROY_DUST("Destroy-Dust", "DestroyDust", Arrays.asList("d", "destroy")),
    MYSTERY_DUST("Mystery-Dust", "MysteryDust", Arrays.asList("m", "mystery")),
    FAILED_DUST("Failed-Dust", "FailedDust", Arrays.asList("f", "failed"));
    
    private static final Map<Dust, ItemBuilder> itemBuilderDust = new HashMap<>();
    private final String name;
    private final String configName;
    private final List<String> knownNames;
    private final int max;
    private final int min;

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Methods methods = this.plugin.getStarter().getMethods();
    
    Dust(final String name, final String configName, final List<String> knowNames) {
        this.name = name;
        this.knownNames = knowNames;
        this.configName = configName;

        final CommentedConfigurationNode config = FileKeys.config.getConfig();

        this.max = config.node("Settings", "Dust", configName, "PercentRange", "Max").getInt(100);
        this.min = config.node("Settings", "Dust", configName, "PercentRange", "Min").getInt(this.max);
    }
    
    public static void loadDust(final CommentedConfigurationNode config) {

        itemBuilderDust.clear();

        for (final Dust dust : values()) {
            final CommentedConfigurationNode child = config.node("Settings", "Dust", dust.getConfigName());

            Dust.itemBuilderDust.put(dust, new ItemBuilder().setName(child.node("Name").getString("Error getting name."))
            .setLore(ConfigUtils.getStringList(child, "Lore"))
            .setMaterial(config.node("Item").getString("GLOWSTONE_DUST")));
        }
    }
    
    public static Dust getFromName(final String nameString) {
        for (final Dust dust : Dust.values()) {
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
        return this.configName;
    }
    
    public ItemStack getDust() {
        return getDust(1);
    }
    
    public ItemStack getDust(final int amount) {
        return getDust(this.methods.percentPick(this.max, this.min), amount);
    }
    
    public ItemStack getDust(final int percent, final int amount) {
        final ItemStack item = itemBuilderDust.get(this).addLorePlaceholder("%Percent%", String.valueOf(percent)).setAmount(amount).build();

        if (Objects.equals(getName(), FAILED_DUST.getName())) return item;

        item.editPersistentDataContainer(container -> container.set(DataKeys.dust.getNamespacedKey(), PersistentDataType.STRING, Methods.getGson().toJson(new DustData(getConfigName(), this.min, this.max, percent))));

        return item;
    }
}