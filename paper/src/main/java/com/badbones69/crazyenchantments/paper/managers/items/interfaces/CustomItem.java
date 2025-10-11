package com.badbones69.crazyenchantments.paper.managers.items.interfaces;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.managers.configs.ConfigManager;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.GuiAction;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.GuiItem;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public abstract class CustomItem {

    protected final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    protected final ConfigManager configManager = this.plugin.getOptions();

    protected final FusionPaper fusion = this.plugin.getFusion();

    public void addKey(@NotNull final ItemStack itemStack, @NotNull final NamespacedKey key, @NotNull final String value) {
        itemStack.editPersistentDataContainer(container -> container.set(key, PersistentDataType.STRING, value));
    }

    public abstract void addKey(@NotNull final NamespacedKey key, @NotNull final String value);

    public void removeKey(@NotNull final ItemStack itemStack, @NotNull final NamespacedKey key) {
        itemStack.editPersistentDataContainer(container -> container.remove(key));
    }

    public abstract void removeKey(@NotNull final NamespacedKey key);

    public abstract ItemStack getItemStack(@Nullable final Player player, final int amount);

    public ItemStack getItemStack(@Nullable final Player player) {
        return getItemStack(player, 1);
    }

    public ItemStack getItemStack(final int amount) {
        return getItemStack(null, amount);
    }

    public ItemStack getItemStack() {
        return getItemStack(null);
    }

    public GuiItem asGuiItem(@Nullable final Player player, final int amount, @Nullable final GuiAction<@NotNull InventoryClickEvent> action) {
        return new GuiItem(getItemStack(player, amount), action);
    }

    public GuiItem asGuiItem(@Nullable final Player player, @Nullable final GuiAction<@NotNull InventoryClickEvent> action) {
        return asGuiItem(player, 1, action);
    }

    public GuiItem asGuiEventItem(@Nullable final GuiAction<@NotNull InventoryClickEvent> action) {
        return asGuiItem(null, action);
    }

    public GuiItem asGuiBasicItem(@Nullable final Player player) {
        return asGuiItem(player, null);
    }

    public GuiItem asGuiItem() {
        return asGuiBasicItem(null);
    }

    public abstract boolean isItem(@NotNull final ItemStack itemStack);

    public boolean hasKey(@NotNull final ItemStack itemStack, @NotNull final NamespacedKey key) {
        if (itemStack.isEmpty()) {
            return false;
        }

        return itemStack.getPersistentDataContainer().has(key);
    }

    public abstract void init();

    public @NotNull final Optional<ConfigurationSection> getConfigurationSection(@NotNull final ConfigurationSection section, @NotNull final String path) {
        return Optional.ofNullable(section.getConfigurationSection(path));
    }
}