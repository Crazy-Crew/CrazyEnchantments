package com.badbones69.crazyenchantments.paper.managers.items.objects.tinker;

import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.managers.configs.types.guis.TinkerConfig;
import com.badbones69.crazyenchantments.paper.managers.items.interfaces.CustomItem;
import com.ryderbelserion.fusion.core.api.enums.ItemState;
import com.ryderbelserion.fusion.paper.builders.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

public class TinkerBottleItem extends CustomItem {

    private final ItemBuilder itemBuilder = new ItemBuilder(ItemType.STONE);
    private final TinkerConfig config;

    public TinkerBottleItem() {
        this.config = this.configManager.getTinkerConfig();

        init();
    }

    @Override
    public void init() {
        if (this.config == null) return;

        this.itemBuilder.withCustomItem(this.config.getBottleItem());
    }

    @Override
    public void addKey(@NotNull final NamespacedKey key, @NotNull final String value) {
        this.itemBuilder.setPersistentString(key, value);
    }

    @Override
    public void removeKey(@NotNull final NamespacedKey key) {
        this.itemBuilder.removePersistentKey(key);
    }

    @Override
    public final ItemStack getItemStack(@Nullable final Player player, @NotNull final Map<String, String> placeholders, final int amount) {
        if (this.config == null) return this.itemBuilder.setAmount(amount).asItemStack(player);

        return this.itemBuilder.displayLore(this.config.asBottleLore(player))
                .displayName(this.config.asBottleName(player), ItemState.ITEM_NAME).setAmount(amount).asItemStack(player);
    }

    @Override
    public final boolean isItem(@NotNull final ItemStack itemStack) {
        return itemStack.getPersistentDataContainer().has(DataKeys.experience.getNamespacedKey());
    }
}