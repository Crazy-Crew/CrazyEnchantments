package com.ryderbelserion.crazyenchantments.paper.enchants.pickaxes.furnace;

import com.ryderbelserion.crazyenchantments.common.utils.ConfigUtils;
import com.ryderbelserion.crazyenchantments.paper.CrazyPlugin;
import com.ryderbelserion.crazyenchantments.paper.api.interfaces.ICustomEnchantment;
import com.ryderbelserion.crazyenchantments.paper.api.registry.enchants.EnchantmentRegistry;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
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
import java.util.*;

public class FurnaceEnchant implements ICustomEnchantment {

    public static final Key furnace_key = Key.key("crazyenchantments:furnace");

    private final EnchantmentRegistry registry;
    private final FileManager fileManager;
    private final FusionPaper fusion;
    private final Path path;

    public FurnaceEnchant(@NotNull final EnchantmentRegistry registry, @NotNull final FusionPaper fusion) {
        this.registry = registry;

        this.fusion = fusion;
        this.fileManager = this.fusion.getFileManager();
        this.path = this.fusion.getDataPath();

        build();
    }

    private final Set<TagKey<@NotNull Enchantment>> enchantTagKeys = new HashSet<>();
    private EnchantmentRegistryEntry.EnchantmentCost minimumCost;
    private EnchantmentRegistryEntry.EnchantmentCost maximumCost;
    private Set<TagEntry<@NotNull ItemType>> supportedItemTags;
    private CommentedConfigurationNode config;
    private int anvilCost, maxLevel, weight;
    private boolean isEnabled;

    @Override
    public void init(@NotNull final CrazyPlugin plugin) {
        if (!this.isEnabled) return;

        final Server server = plugin.getServer();

        server.getPluginManager().registerEvents(new FurnaceListener(plugin, this.registry), plugin);
    }

    @Override
    public void build() { // used for /ce reload
        @NotNull final Optional<YamlCustomFile> customFile = this.fileManager.getYamlFile(getPath());

        customFile.ifPresentOrElse(file -> {
            if (!Files.exists(getPath())) {
                file.save(); // save to the system
            }

            file.load();

            final CommentedConfigurationNode config = file.getConfiguration(); // get the node

            // update the values
            this.isEnabled = config.node("enchant", "enabled").getBoolean(false);
            this.anvilCost = config.node("enchant", "anvil-cost").getInt(1);
            this.maxLevel = config.node("enchant", "max-level").getInt(1);
            this.weight = config.node("enchant", "weight").getInt(1);

            this.minimumCost = EnchantmentRegistryEntry.EnchantmentCost.of(config.node("enchant", "minimum-cost", "base").getInt(1),
                    config.node("enchant", "minimum-cost", "extra-per-level").getInt(1));
            this.maximumCost = EnchantmentRegistryEntry.EnchantmentCost.of(config.node("enchant", "maximum-cost", "base").getInt(1),
                    config.node("enchant", "maximum-cost", "extra-per-level").getInt(1));

            final List<String> tags = file.getStringList("enchant", "supported-items");

            this.supportedItemTags = this.registry.getTagsFromList(tags.isEmpty() ? List.of("#minecraft:enchantable/mining") : tags);

            if (config.node("enchant", "enchantment-table").getBoolean(false)) {
                this.enchantTagKeys.add(EnchantmentTagKeys.IN_ENCHANTING_TABLE);
            }

            // re-bind the config node just in case we need it.
            this.config = config;
        }, () -> this.fusion.log(Level.WARNING, "Could not find furnace.yml in the enchants folder."));
    }

    @Override
    public final Path getPath() {
        return this.path.resolve("enchants").resolve("furnace.yml");
    }

    @Override
    public final Key getKey() {
        return furnace_key;
    }

    @Override
    public final Component getDescription() {
        return this.fusion.asComponent(StringUtils.toString(ConfigUtils.getStringList(this.config, List.of("<yellow>Smelts all blocks that are mined."), "enchant", "display", "description")));
    }

    @Override
    public final Component getName() {
        return this.fusion.asComponent(StringUtils.toString(ConfigUtils.getStringList(this.config, List.of("<yellow>Smelts all blocks that are mined."), "enchant", "display", "name")));
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
    public final Set<TagEntry<@NotNull ItemType>> getSupportedItems() {
        return this.supportedItemTags;
    }

    @Override
    public final Set<TagKey<@NotNull Enchantment>> getEnchantTagKeys() {
        return this.enchantTagKeys;
    }

    public final CommentedConfigurationNode getConfig() {
        return this.config;
    }
}