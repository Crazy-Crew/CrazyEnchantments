package com.ryderbelserion.crazyenchantments.paper.enchants;

import com.ryderbelserion.crazyenchantments.paper.enchants.interfaces.CustomEnchantment;
import com.ryderbelserion.crazyenchantments.paper.enchants.types.pickaxes.veinminer.VeinMinerEnchant;
import com.ryderbelserion.fusion.core.files.FileManager;
import com.ryderbelserion.fusion.core.files.FileType;
import com.ryderbelserion.fusion.paper.FusionPaper;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnchantmentRegistry {

    private final Map<Key, CustomEnchantment> enchantments = new HashMap<>();

    private final FileManager fileManager;
    private final ComponentLogger logger;
    private final Path path;

    public EnchantmentRegistry(@NotNull final FusionPaper paper, @NotNull final ComponentLogger logger, @NotNull final Path path) {
        this.fileManager = paper.getFileManager();
        this.logger = logger;
        this.path = path;
    }

    public void init() { // runs on startup
        this.fileManager.addFolder(this.path.resolve("curses"), FileType.YAML, new ArrayList<>(), null).addFolder(this.path.resolve("enchants"), FileType.YAML, new ArrayList<>(), null);

        this.enchantments.put(VeinMinerEnchant.veinminer_key, new VeinMinerEnchant(this, this.fileManager, this.path));
    }

    public void reload() { // runs on reload in case they deleted a static file.
        this.enchantments.forEach((key, enchantment) -> {
            enchantment.build(); // this ensures that the files are always present, and refreshes config options.
        });
    }

    public void addEnchantment(@NotNull final Key key, @NotNull final CustomEnchantment enchantment) {
        this.enchantments.put(key, enchantment);
    }

    public void removeEnchantment(@NotNull final Key key) {
        this.enchantments.remove(key);
    }

    public final CustomEnchantment getEnchantment(@NotNull final Key key) {
        return this.enchantments.get(key);
    }

    public final boolean hasEnchantment(@NotNull final Key key) {
        return this.enchantments.containsKey(key);
    }

    public @NotNull final Map<Key, CustomEnchantment> getEnchantments() {
        return this.enchantments;
    }

    public @NotNull final Set<TagEntry<ItemType>> getTagsFromList(@NotNull final List<String> tags) {
        final Set<TagEntry<ItemType>> supportedItemTags = new HashSet<>();

        for (String itemTag : tags) {
            if (itemTag == null) continue;

            if (itemTag.startsWith("#")) {
                itemTag = itemTag.substring(1);

                try {
                    Key key = Key.key(itemTag);

                    TagKey<ItemType> tagKey = ItemTypeTagKeys.create(key);

                    TagEntry<ItemType> tagEntry = TagEntry.tagEntry(tagKey);

                    supportedItemTags.add(tagEntry);
                } catch (final IllegalArgumentException exception) {
                    this.logger.warn("Failed to create a tag entry for {}", itemTag);
                }

                continue;
            }

            try {
                Key key = Key.key(itemTag);

                TypedKey<ItemType> typedKey = TypedKey.create(RegistryKey.ITEM, key);

                TagEntry<ItemType> tagEntry = TagEntry.valueEntry(typedKey);

                supportedItemTags.add(tagEntry);
            } catch (final IllegalArgumentException | NullPointerException exception) {
                this.logger.warn("Failed to create the tag entry for {}", itemTag);
            }
        }

        return supportedItemTags;
    }
}