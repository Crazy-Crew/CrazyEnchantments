package com.badbones69.crazyenchantments.paper.api.builders.types.gkitz;

import com.badbones69.crazyenchantments.paper.api.builders.gui.types.StaticInventory;
import com.badbones69.crazyenchantments.paper.api.enums.v2.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.managers.configs.types.KitConfig;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.Gui;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class KitsPreviewMenu extends StaticInventory {

    private final KitConfig config;
    private final GKitz kit;

    public KitsPreviewMenu(@NotNull final Player player, @NotNull final String title, final int size, @NotNull final GKitz kit) {
        super(player, title, size);

        this.config = this.configManager.getKitConfig();
        this.kit = kit;
    }

    @Override
    public void open() {
        final Player player = getPlayer();

        if (!this.kitsManager.isRegistered()) {
            this.fusion.log("warn", "The kits preview menu is not enabled, either because of an error, or g-kits are not enabled.");

            Messages.GKIT_NOT_ENABLED.sendMessage(player);

            return;
        }

        final Gui gui = getGui();

        for (final ItemStack itemStack : this.kit.getPreviewItems()) {
            gui.addItem(new GuiItem(itemStack));
        }

        final String name = this.config.getInventoryName();
        final int size = this.config.getInventorySize();

        this.itemManager.getItem("back_button_right").ifPresent(item -> gui.setItem(gui.getSize() - 1, item.asGuiItem(player, event -> new KitsMainMenu(player, name, size))));
    }
}