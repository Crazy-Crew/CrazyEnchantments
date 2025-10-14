package com.badbones69.crazyenchantments.paper.managers.items.objects;

import com.badbones69.crazyenchantments.paper.managers.configs.types.items.ScramblerConfig;
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

public class PointerItem extends CustomItem {

    private final ItemBuilder itemBuilder = new ItemBuilder(ItemType.STONE);
    private final ScramblerConfig config;

    public PointerItem() {
        this.config = this.configManager.getScramblerConfig();

        init();
    }

    @Override
    public void init() {
        if (this.config == null) return;

        this.itemBuilder.withCustomItem(this.config.getPointerItem());
    }

    @Override
    public @NotNull final ItemStack getItemStack(@Nullable final Player player, @NotNull final Map<String, String> placeholders, final int amount) {
        if (this.config == null) return this.itemBuilder.setAmount(amount).asItemStack(player);

        return this.itemBuilder.displayLore(this.config.asPointerComponents(player))
                .displayName(this.config.asPointerComponent(player), ItemState.ITEM_NAME)
                .asItemStack(player);
    }

    @Override
    public boolean isItem(@NotNull final ItemStack itemStack) {
        return true;
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