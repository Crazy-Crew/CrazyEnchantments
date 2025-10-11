package com.badbones69.crazyenchantments.paper.managers.items.objects;

import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.managers.configs.types.ScramblerConfig;
import com.badbones69.crazyenchantments.paper.managers.items.interfaces.CustomItem;
import com.ryderbelserion.fusion.core.api.enums.ItemState;
import com.ryderbelserion.fusion.paper.builders.ItemBuilder;
import net.kyori.adventure.audience.Audience;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScramblerItem extends CustomItem {

    private final ItemBuilder itemBuilder = new ItemBuilder(ItemType.STONE);
    private final ScramblerConfig config;

    public ScramblerItem() {
        this.config = this.configManager.getScramblerConfig();

        init();
    }

    @Override
    public void init() {
        this.itemBuilder.withCustomItem(this.config.getScramblerType())
                .addEnchantGlint(this.config.isGlowing())
                .setPersistentBoolean(DataKeys.scrambler.getNamespacedKey(), true);
    }

    @Override
    public @NotNull final ItemStack getItemStack(@Nullable final Player player, final int amount) {
        return this.itemBuilder.displayLore(this.config.asItemComponents(player == null ? Audience.empty() : player))
                .displayName(this.config.asItemComponent(player == null ? Audience.empty() : player), ItemState.ITEM_NAME)
                .setAmount(amount).asItemStack(player == null ? Audience.empty() : player);
    }

    @Override
    public boolean isItem(@NotNull final ItemStack itemStack) {
        return this.itemBuilder.hasKey(DataKeys.scrambler.getNamespacedKey());
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