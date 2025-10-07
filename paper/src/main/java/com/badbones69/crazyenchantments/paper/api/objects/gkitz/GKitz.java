package com.badbones69.crazyenchantments.paper.api.objects.gkitz;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.utils.RandomUtils;
import com.ryderbelserion.fusion.paper.utils.ItemUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GKitz {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyInstance instance = this.plugin.getInstance();

    private final List<String> commands;
    private final ItemStack displayItem;
    private final boolean autoEquip;
    private final String cooldown;
    private final String name;
    private final int slot;

    private final List<ItemStack> preview;
    private final List<String> items;

    public GKitz(@NotNull final String kitName, @NotNull final ItemStack displayItem, final int slot, final boolean autoEquip, @NotNull final String cooldown,
                 @NotNull final List<String> commands, @NotNull final List<String> items, @NotNull final List<String> fakeItems) {
        this.autoEquip = autoEquip;
        this.cooldown = cooldown;
        this.name = kitName;
        this.slot = slot;

        this.preview = buildItems(this.items = items);

        this.preview.addAll(buildItems(fakeItems));

        this.displayItem = displayItem;

        this.commands = commands;
    }

    public String getName() {
        return this.name;
    }
    
    public int getSlot() {
        return this.slot;
    }
    
    public String getCooldown() {
        return this.cooldown;
    }
    
    public ItemStack getDisplayItem() {
        return this.displayItem;
    }
    
    public List<ItemStack> getPreviewItems() {
        return this.preview;
    }
    
    public List<String> getCommands() {
        return this.commands;
    }
    
    public List<String> getItems() {
        return this.items;
    }
    
    public boolean canAutoEquip() {
        return this.autoEquip;
    }

    public List<ItemStack> buildItems(@NotNull final List<String> items) {
        final List<ItemStack> itemStacks = new ArrayList<>();

        for (final String item : items) {
            if (item.isEmpty()) continue;  //todo debug

            final ItemBuilder builder = ItemBuilder.convertString(item);

            final Map<Enchantment, Integer> vanilla = new HashMap<>();
            final List<String> enchantments = new ArrayList<>();

            for (final String option : item.split(", ")) {
                final String[] splitter = option.split(":");

                final String level = splitter[1];
                final String name = splitter[0];

                final Enchantment enchantment = ItemUtils.getEnchantment(name);

                if (enchantment != null) {
                    if (level.contains("-")) {
                        enchantments.add("&7%s %s".formatted(name, level));

                        continue;
                    }

                    vanilla.put(enchantment, Integer.parseInt(level));

                    continue;
                }

                final CEnchantment custom = this.instance.getEnchantmentFromName(name);

                if (custom == null) continue; //todo debug

                enchantments.add("%s %s".formatted(custom.getCustomName(), level));
            }

            builder.getLore().addAll(0, enchantments.stream().map(ColorUtils::legacyTranslateColourCodes).toList());

            builder.setEnchantments(vanilla);

            itemStacks.add(builder.addKey(DataKeys.random_number.getNamespacedKey(), String.valueOf(RandomUtils.getRandomNumber(0, Integer.MAX_VALUE))).build());
        }

        return itemStacks;
    }

    public @NotNull final List<ItemStack> getKitItems() {
        final List<ItemStack> items = new ArrayList<>();

        for (final String value : this.items) {
            final GKitzItem item = new GKitzItem(ItemBuilder.convertString(value));

            for (final String option : value.split(", ")) {
                final String[] splitter = option.split(":");

                final String level = splitter[1];
                final String name = splitter[0];

                final CEnchantment enchantment = this.instance.getEnchantmentFromName(name);

                if (enchantment != null) {
                    if (level.contains("-")) {
                        final String[] numbers = level.split("-");

                        int randomLevel = RandomUtils.getRandomNumber(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));

                        if (randomLevel > 0) {
                            item.addCEEnchantment(enchantment, randomLevel);
                        }

                        continue;
                    }

                    item.addCEEnchantment(enchantment, Integer.parseInt(level));
                }
            }

            items.add(item.build());
        }

        return items;
    }
}