package com.badbones69.crazyenchantments.paper.managers.items.objects.crystals;

import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.managers.configs.types.items.ProtectionCrystalConfig;
import com.badbones69.crazyenchantments.paper.managers.items.interfaces.CustomItem;
import com.ryderbelserion.fusion.core.api.enums.ItemState;
import com.ryderbelserion.fusion.paper.builders.ItemBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProtectionCrystalItem extends CustomItem {

    private final ItemBuilder itemBuilder = new ItemBuilder(ItemType.STONE);
    private final ProtectionCrystalConfig config;
    private Component protectionLine;

    public ProtectionCrystalItem() {
        this.config = this.configManager.getProtectionCrystalConfig();

        init();
    }

    @Override
    public void init() {
        if (this.config == null) return;

        this.itemBuilder.withCustomItem(this.config.getProtectionType())
                .addEnchantGlint(this.config.isGlowing())
                .setPersistentBoolean(DataKeys.protection_crystal.getNamespacedKey(), true);

        this.protectionLine = this.config.asComponent(Audience.empty(), this.config.getProtectionLine());
    }

    @Override
    public @NotNull final ItemStack getItemStack(@Nullable final Player player, @NotNull final Map<String, String> placeholders, final int amount) {
        if (this.config == null) return this.itemBuilder.setAmount(amount).asItemStack(player);

        return this.itemBuilder.displayLore(this.config.asItemComponents(player))
                .displayName(this.config.asItemComponent(player), ItemState.ITEM_NAME)
                .asItemStack(player);
    }

    @Override
    public boolean isItem(@NotNull final ItemStack itemStack) {
        return hasKey(itemStack, DataKeys.protection_crystal.getNamespacedKey());
    }

    @Override
    public void addKey(@NotNull final ItemStack itemStack, @NotNull final NamespacedKey key, @NotNull final String value) {
        final List<Component> itemLore = itemStack.lore();

        final List<Component> lore = itemLore != null ? itemLore : new ArrayList<>();

        itemStack.editPersistentDataContainer(container -> container.set(key, PersistentDataType.STRING, value));

        lore.add(this.protectionLine);

        itemStack.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(lore).build());
    }

    @Override
    public void addKey(@NotNull final NamespacedKey key, @NotNull final String value) {
        this.itemBuilder.setPersistentString(key, value);
    }

    @Override
    public void removeKey(@NotNull final ItemStack itemStack, @NotNull final NamespacedKey key) {
        itemStack.editPersistentDataContainer(container -> container.remove(key));

        final List<Component> lore = itemStack.lore();

        if (lore != null) {
            final String component = PlainTextComponentSerializer.plainText().serialize(this.protectionLine);

            lore.removeIf(line -> PlainTextComponentSerializer.plainText().serialize(line).contains(component));

            itemStack.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(lore).build());
        }
    }

    @Override
    public void removeKey(@NotNull final NamespacedKey key) {
        this.itemBuilder.removePersistentKey(key);
    }
}