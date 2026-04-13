package com.ryderbelserion.crazyenchantments.paper.enchants.pickaxes.veinminer;

import com.ryderbelserion.crazyenchantments.common.utils.ConfigUtils;
import com.ryderbelserion.crazyenchantments.paper.CrazyPlugin;
import com.ryderbelserion.crazyenchantments.paper.api.registry.enchants.EnchantmentRegistry;
import com.ryderbelserion.crazyenchantments.paper.api.interfaces.ICustomEnchantment;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.files.types.configurate.JsonCustomFile;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import com.ryderbelserion.fusion.paper.FusionPaper;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class VeinMinerEnchant implements ICustomEnchantment {

    public static final Key veinminer_key = Key.key("crazyenchantments:veinminer");

    private final EnchantmentRegistry registry;
    private final FileManager fileManager;
    private final FusionPaper fusion;
    private final Path path;

    public VeinMinerEnchant(@NotNull final EnchantmentRegistry registry, @NotNull final FusionPaper fusion) {
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
    private List<String> ores;
    private boolean isEnabled;

    @Override
    public void init(@NotNull final CrazyPlugin plugin) {
        if (!this.isEnabled) return;

        final Server server = plugin.getServer();

        server.getPluginManager().registerEvents(new VeinMinerListener(plugin, this.registry), plugin);
    }

    private boolean isScalingChain;
    private int scalingChain;

    private boolean isScalingRadius;
    private int scaleRadius;

    private boolean isDamageItem;
    private boolean requiresCorrectTool;

    private int delay;

    @Override
    public void build() { // used for /ce reload
        final Path path = getPath();

        if (!Files.exists(path)) {
            this.fileManager.addFile(path, FileType.YAML);
        }

        this.fileManager.addFile(this.path.resolve("cache").resolve("ores.json"), FileType.JSON);

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

            this.isScalingChain = config.node("enchant", "settings", "chain", "scale").getBoolean(false);
            this.scalingChain = config.node("enchant", "settings", "chain", "max").getInt(10);

            this.isScalingRadius = config.node("enchant", "settings", "search", "scale").getBoolean(false);
            this.scaleRadius = config.node("enchant", "settings", "search", "max").getInt(10);

            this.isDamageItem = config.node("enchant", "settings", "damage-item").getBoolean(false);

            this.requiresCorrectTool = config.node("enchant", "settings", "need-correct-tool").getBoolean(false);

            this.delay = config.node("enchant", "settings", "delay").getInt(0);

            @NotNull final Optional<JsonCustomFile> ores = this.fileManager.getJsonFile(this.path.resolve("cache").resolve("ores.json"));

            ores.ifPresentOrElse(jsonFile -> this.ores = jsonFile.getStringList("blocks"), () -> {
                this.fusion.log(Level.WARNING, "Could not find ores.json in the cache folder.");

                this.ores = new ArrayList<>();
            });

            // re-bind the config node just in case we need it.
            this.config = config;
        }, () -> this.fusion.log(Level.WARNING, "Could not find veinminer.yml in the enchants folder."));
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
        return this.fusion.asComponent(StringUtils.toString(ConfigUtils.getStringList(this.config, List.of("<yellow>Mines all blocks connected to a vein."), "enchant", "display", "description")));
    }

    @Override
    public final Component getName() {
        return this.fusion.asComponent(StringUtils.toString(ConfigUtils.getStringList(this.config, List.of("<yellow>Mines all blocks connected to a vein."), "enchant", "display", "name")));
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

    public final boolean isRequiresCorrectTool() {
        return this.requiresCorrectTool;
    }

    public final boolean isScalingChain() {
        return this.isScalingChain;
    }

    public final boolean isScalingRadius() {
        return this.isScalingRadius;
    }

    public final boolean isDamageItem() {
        return this.isDamageItem;
    }

    public final int getScalingChain() {
        return this.scalingChain;
    }

    public final int getScaleRadius() {
        return this.scaleRadius;
    }

    public final int getDelay() {
        return this.delay;
    }

    public final boolean hasOre(@NotNull final Block block) {
        return getOres().contains(block.getType().getKey().asString());
    }

    public final List<String> getOres() {
        return this.ores;
    }
}