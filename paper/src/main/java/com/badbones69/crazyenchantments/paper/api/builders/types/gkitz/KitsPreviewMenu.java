package com.badbones69.crazyenchantments.paper.api.builders.types.gkitz;

import com.badbones69.crazyenchantments.paper.api.builders.InventoryBuilder;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class KitsPreviewMenu extends InventoryBuilder {

    public KitsPreviewMenu(Player player, int size, String title, GKitz kit) {
        super(player, size, title, kit);
    }

    @Override
    public InventoryBuilder build() {
        for (ItemStack itemStack : getKit().getKitItems()) {
            getInventory().addItem(itemStack);
        }

        List<ItemStack> items = getKit().getPreviewItems();
        int slots = Math.min(((items.size() / 9) + (items.size() % 9 > 0 ? 1 : 0)) * 9, 54);

        getInventory().setItem(slots - 1, KitsManager.getBackRight());

        return this;
    }
}