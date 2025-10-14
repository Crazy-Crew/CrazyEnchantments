package com.badbones69.crazyenchantments.paper.managers.items.objects.navigation;

import com.badbones69.crazyenchantments.paper.managers.configs.types.NavigationConfig;
import com.badbones69.crazyenchantments.paper.managers.items.interfaces.CustomItem;
import com.ryderbelserion.fusion.core.api.enums.ItemState;
import com.ryderbelserion.fusion.paper.builders.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;

public class NavigationItem extends CustomItem {

    private final ItemBuilder itemBuilder = new ItemBuilder(ItemType.STONE);
    private final NavigationConfig config;
    private final boolean isRight;

    public NavigationItem(final boolean isRight) {
        this.config = this.configManager.getNavigationConfig();
        this.isRight = isRight;

        init();
    }

    @Override
    public void init() {
        if (this.config == null) return;

        final String item = this.isRight ? this.config.getNavigationRightItem()
                : this.config.getNavigationLeftItem();

        final String player = this.isRight ? this.config.getNavigationRightPlayer()
                : this.config.getNavigationLeftPlayer();

        this.itemBuilder.withCustomItem(item).withSkull(player);
    }

    @Override
    public @NotNull final ItemStack getItemStack(@Nullable final Player player, @NotNull final Map<String, String> placeholders, final int amount) {
        if (this.config == null) return this.itemBuilder.setAmount(amount).asItemStack(player);

        final Component component = this.isRight ? this.config.getNavigationRightName(player)
                : this.config.getNavigationLeftName(player);

        final List<Component> components = this.isRight ? this.config.getNavigationRightLore(player)
                : this.config.getNavigationLeftLore(player);

        return this.itemBuilder.displayLore(components)
                .displayName(component, ItemState.ITEM_NAME).setAmount(amount).asItemStack(player);
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