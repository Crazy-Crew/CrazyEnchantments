package com.ryderbelserion.crazyenchantments.paper.api.registry;

import com.ryderbelserion.crazyenchantments.paper.api.interfaces.ICustomEnchantment;
import com.ryderbelserion.crazyenchantments.paper.enchants.pickaxes.veinminer.VeinMinerEnchant;
import com.ryderbelserion.fusion.core.FusionProvider;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.paper.FusionPaper;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnchantmentRegistry {

    private final FusionPaper fusion;

    private final FileManager fileManager;
    private final Path path;

    private final Map<Key, ICustomEnchantment> enchantments = new HashMap<>();

    public EnchantmentRegistry(@NotNull final FusionPaper fusion) {
        this.fusion = fusion;
        this.fileManager = this.fusion.getFileManager();
        this.path = this.fusion.getDataPath();
    }

    public void init() { // runs on startup
        this.fileManager.addFolder(this.path.resolve("curses"), FileType.YAML).addFolder(this.path.resolve("enchants"), FileType.YAML);

        this.enchantments.put(VeinMinerEnchant.veinminer_key, new VeinMinerEnchant(this, this.fusion));
    }

    public void reload() { // runs on reload in case they deleted a static file.
        this.enchantments.forEach((key, enchantment) -> {
            enchantment.build(); // this ensures that the files are always present, and refreshes config options.
        });
    }

    public void addEnchantment(@NotNull final Key key, @NotNull final ICustomEnchantment enchantment) {
        this.enchantments.put(key, enchantment);
    }

    public void removeEnchantment(@NotNull final Key key) {
        this.enchantments.remove(key);
    }

    public final ICustomEnchantment getEnchantment(@NotNull final Key key) {
        return this.enchantments.get(key);
    }

    public final boolean hasEnchantment(@NotNull final Key key) {
        return this.enchantments.containsKey(key);
    }

    public @NotNull final Map<Key, ICustomEnchantment> getEnchantments() {
        return this.enchantments;
    }

    @ApiStatus.Internal
    public void purgeEnchantments() {
        this.enchantments.clear();
    }

    public @NotNull final Set<TagEntry<@NotNull ItemType>> getTagsFromList(@NotNull final List<String> tags) {
        final Set<TagEntry<@NotNull ItemType>> supportedItemTags = new HashSet<>();

        for (String itemTag : tags) {
            if (itemTag == null) continue;

            if (itemTag.startsWith("#")) {
                itemTag = itemTag.substring(1);

                try {
                    Key key = Key.key(itemTag);

                    TagKey<@NotNull ItemType> tagKey = ItemTypeTagKeys.create(key);

                    TagEntry<@NotNull ItemType> tagEntry = TagEntry.tagEntry(tagKey);

                    supportedItemTags.add(tagEntry);
                } catch (final IllegalArgumentException exception) {
                    this.fusion.log("error", "<red>Failed to create a tag entry for <gold>{}", itemTag);
                }

                continue;
            }

            try {
                Key key = Key.key(itemTag);

                TypedKey<@NotNull ItemType> typedKey = TypedKey.create(RegistryKey.ITEM, key);

                TagEntry<@NotNull ItemType> tagEntry = TagEntry.valueEntry(typedKey);

                supportedItemTags.add(tagEntry);
            } catch (final IllegalArgumentException | NullPointerException exception) {
                this.fusion.log("error", "<red>Failed to create the tag entry for <gold>{}", itemTag);
            }
        }

        return supportedItemTags;
    }
}