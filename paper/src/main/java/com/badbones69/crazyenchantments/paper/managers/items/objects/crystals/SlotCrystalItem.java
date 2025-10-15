package com.badbones69.crazyenchantments.paper.managers.items.objects.crystals;

import com.badbones69.crazyenchantments.paper.managers.configs.types.items.SlotCrystalConfig;
import com.ryderbelserion.fusion.core.api.enums.ItemState;
import com.ryderbelserion.fusion.paper.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.enums.DataKeys;
import com.badbones69.crazyenchantments.paper.managers.items.interfaces.CustomItem;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

public class SlotCrystalItem extends CustomItem {

    private final SlotCrystalConfig config;
    private final ItemBuilder itemBuilder = new ItemBuilder(ItemType.STONE);

    public SlotCrystalItem() {
        this.config = this.configManager.getSlotCrystalConfig();

        init();
    }

    @Override
    public void init() {
        if (this.config == null) return;

        this.itemBuilder.withCustomItem(this.config.getSlotType())
                .addEnchantGlint(this.config.isGlowing())
                .setPersistentBoolean(DataKeys.slot_crystal.getNamespacedKey(), true);
    }

    @Override
    public @NotNull final ItemStack getItemStack(@Nullable final Player player, @NotNull final Map<String, String> placeholders, final int amount) {
        if (this.config == null) return this.itemBuilder.setAmount(amount).asItemStack(player);

        return this.itemBuilder.displayLore(this.config.asItemComponents(player))
                .displayName(this.config.asItemComponent(player), ItemState.ITEM_NAME)
                .setAmount(amount).asItemStack(player);
    }

    @Override
    public boolean isItem(@NotNull final ItemStack itemStack) {
        return hasKey(itemStack, DataKeys.slot_crystal.getNamespacedKey());
    }

    @Override
    public void addKey(@NotNull final NamespacedKey key, @NotNull final String value) {
        this.itemBuilder.setPersistentString(key, value);
    }

    @Override
    public void removeKey(@NotNull final NamespacedKey key) {
        this.itemBuilder.removePersistentKey(key);
    }
}