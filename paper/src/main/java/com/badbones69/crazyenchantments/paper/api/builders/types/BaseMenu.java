package com.badbones69.crazyenchantments.paper.api.builders.types;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.builders.gui.types.StaticInventory;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantType;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.Gui;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class BaseMenu extends StaticInventory {

    public BaseMenu(@NotNull final Player player, @NotNull final String title, final int size) {
        super(player, title, size);
    }

    private final Player player = getPlayer();
    private final String title = getTitle();
    private final int size = getSize();
    private final Gui gui = getGui();

    @Override
    public void open() {
        if (this.enchantmentType == null) {
            for (final EnchantType type : this.instance.getRegisteredEnchantmentTypes()) {
                this.gui.setItem(type.getSlot(), new GuiItem(type.getDisplayItem(), event -> {
                    if (!(event.getWhoClicked() instanceof Player entity)) return;

                    new BaseMenu(entity, this.title, this.size).setEnchantmentType(type);
                }));
            }

            this.gui.open(this.player);

            return;
        }

        final List<CEnchantment> enchantments = this.enchantmentType.getEnchantments();

        final ItemBuilder book = this.instance.getEnchantmentBookBuilder();

        this.itemManager.getItem("back_button_right").ifPresent(item -> this.gui.setItem(this.size - 1, item.asGuiEventItem(event -> {
            if (!(event.getWhoClicked() instanceof Player entity)) return;

            new BaseMenu(entity, this.title, this.size);
        })));

        this.itemManager.getItem("back_button_left").ifPresent(item -> this.gui.setItem(this.size - 8, item.asGuiEventItem(event -> {
            if (!(event.getWhoClicked() instanceof Player entity)) return;

            new BaseMenu(entity, this.title, this.size);
        })));

        for (final CEnchantment enchantment : enchantments) {
            if (!enchantment.isActivated()) continue;

            final ItemStack itemStack = book.setName(enchantment.getInfoName()).setLore(enchantment.getInfoDescription()).build();

            this.gui.addItem(new GuiItem(itemStack));
        }

        this.gui.open(this.player);
    }
}