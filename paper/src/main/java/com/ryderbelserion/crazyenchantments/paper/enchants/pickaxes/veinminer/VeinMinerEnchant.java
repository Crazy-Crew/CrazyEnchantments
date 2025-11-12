package com.ryderbelserion.crazyenchantments.paper.enchants.pickaxes.veinminer;

import com.ryderbelserion.crazyenchantments.core.utils.ConfigUtils;
import com.ryderbelserion.crazyenchantments.paper.CrazyEnchantmentsPlugin;
import com.ryderbelserion.crazyenchantments.paper.api.registry.EnchantmentRegistry;
import com.ryderbelserion.crazyenchantments.paper.api.interfaces.ICustomEnchantment;
import com.ryderbelserion.fusion.core.api.enums.FileAction;
import com.ryderbelserion.fusion.core.api.utils.StringUtils;
import com.ryderbelserion.fusion.core.files.FileManager;
import com.ryderbelserion.fusion.core.files.types.JsonCustomFile;
import com.ryderbelserion.fusion.core.files.types.YamlCustomFile;
import com.ryderbelserion.fusion.paper.FusionPaper;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VeinMinerEnchant implements ICustomEnchantment {

    public static final Key veinminer_key = Key.key("crazyenchantments:veinminer");

    private final EnchantmentRegistry registry;
    private final FileManager fileManager;
    private final FusionPaper fusion;
    private final Path path;

    public VeinMinerEnchant(@NotNull final EnchantmentRegistry registry, @NotNull final FusionPaper fusion, @NotNull final FileManager fileManager, @NotNull final Path path) {
        this.fileManager = fileManager;
        this.registry = registry;
        this.fusion = fusion;
        this.path = path;

        build();
    }

    private final Set<TagKey<Enchantment>> enchantTagKeys = new HashSet<>();
    private EnchantmentRegistryEntry.EnchantmentCost minimumCost;
    private EnchantmentRegistryEntry.EnchantmentCost maximumCost;
    private Set<TagEntry<ItemType>> supportedItemTags;
    private CommentedConfigurationNode config;
    private int anvilCost, maxLevel, weight;
    private List<String> ores;
    private boolean isEnabled;

    @Override
    public void init(@NotNull final CrazyEnchantmentsPlugin plugin) {
        final Server server = plugin.getServer();

        server.getPluginManager().registerEvents(new VeinMinerListener(plugin, this.registry), plugin);
    }

    @Override
    public void build() { // used for /ce reload
        this.fileManager.addFile(this.path.resolve("cache").resolve("ores.json"), new ArrayList<>() {{
            add(FileAction.EXTRACT_FILE);
        }}, null);

        final YamlCustomFile customFile = this.fileManager.getYamlFile(getPath());

        if (customFile != null) {
            if (!Files.exists(getPath())) {
                customFile.save(); // save to the system
            }

            customFile.load(); // load into memory.

            final CommentedConfigurationNode config = customFile.getConfiguration(); // get the node

            // update the values
            this.isEnabled = config.node("enchant", "enabled").getBoolean(false);
            this.anvilCost = config.node("enchant", "anvil-cost").getInt(1);
            this.maxLevel = config.node("enchant", "max-level").getInt(1);
            this.weight = config.node("enchant", "weight").getInt(1);

            this.minimumCost = EnchantmentRegistryEntry.EnchantmentCost.of(config.node("enchant", "minimum-cost", "base").getInt(1),
                    config.node("enchant", "minimum-cost", "extra-per-level").getInt(1));
            this.maximumCost = EnchantmentRegistryEntry.EnchantmentCost.of(config.node("enchant", "maximum-cost", "base").getInt(1),
                    config.node("enchant", "maximum-cost", "extra-per-level").getInt(1));

            final List<String> tags = customFile.getStringList("enchant", "supported-items");

            this.supportedItemTags = this.registry.getTagsFromList(tags.isEmpty() ? List.of("#minecraft:enchantable/mining") : tags);

            if (config.node("enchant", "enchantment-table").getBoolean(false)) {
                this.enchantTagKeys.add(EnchantmentTagKeys.IN_ENCHANTING_TABLE);
            }

            final JsonCustomFile ores = (JsonCustomFile) this.fileManager.getCustomFile(this.path.resolve("cache").resolve("ores.json"));

            if (ores != null) {
                this.ores = ores.getStringList("blocks");
            }

            // re-bind the config node just in case we need it.
            this.config = config;
        }
    }

    @Override
    public final Path getPath() {
        return this.path.resolve("enchants").resolve("veinminer.yml");
    }

    @Override
    public final Key getKey() {
        return veinminer_key;
    }

    @Override
    public final Component getDescription() {
        return this.fusion.color(StringUtils.toString(ConfigUtils.getStringList(this.config, List.of("<yellow>Mines all blocks connected to a vein."), "enchant", "display", "description")));
    }

    @Override
    public final int getAnvilCost() {
        return this.anvilCost;
    }

    @Override
    public final int getMaxLevel() {
        return this.maxLevel;
    }

    @Override
    public final int getWeight() {
        return this.weight;
    }

    @Override
    public final EnchantmentRegistryEntry.EnchantmentCost getMinimumCost() {
        return this.minimumCost;
    }

    @Override
    public final EnchantmentRegistryEntry.EnchantmentCost getMaximumCost() {
        return this.maximumCost;
    }

    @Override
    public final Iterable<EquipmentSlotGroup> getActiveSlots() {
        return Set.of(EquipmentSlotGroup.HAND);
    }

    @Override
    public final boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public final Set<TagEntry<ItemType>> getSupportedItems() {
        return this.supportedItemTags;
    }

    @Override
    public final Set<TagKey<Enchantment>> getEnchantTagKeys() {
        return this.enchantTagKeys;
    }

    public final CommentedConfigurationNode getConfig() {
        return this.config;
    }

    public final List<String> getOres() {
        return this.ores;
    }
}