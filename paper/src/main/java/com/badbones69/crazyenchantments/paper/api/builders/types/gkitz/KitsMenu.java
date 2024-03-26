package com.badbones69.crazyenchantments.paper.api.builders.types.gkitz;

import com.badbones69.crazyenchantments.ConfigManager;
import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.builders.InventoryBuilder;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEPlayer;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GkitCoolDown;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.platform.impl.Config;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitsMenu extends InventoryBuilder {

    private final @NotNull Starter starter = this.plugin.getStarter();
    private final @NotNull CrazyManager crazyManager = this.starter.getCrazyManager();

    public KitsMenu(Player player, int size, String title) {
        super(player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        for (String value : ConfigManager.getConfig().getProperty(Config.gui_customization)) {
            int slot = 0;

            for (String option : value.split(", ")) {
                if (option.contains("Slot:")) {
                    slot = Integer.parseInt(option.replace("Slot:", ""));

                    break;
                }
            }

            slot--;

            getInventory().setItem(slot, ItemBuilder.convertString(value).build());
        }

        CEPlayer cePlayer = this.crazyManager.getCEPlayer(getPlayer().getUniqueId());

        for (GKitz kit : this.crazyManager.getGKitz()) {
            ItemStack displayItem = kit.getDisplayItem().clone();
            ItemMeta itemMeta = displayItem.getItemMeta();
            List<Component> lore = new ArrayList<>();
            GkitCoolDown gkitCooldown = !cePlayer.canUseGKit(kit) && cePlayer.hasGkitPermission(kit) ? cePlayer.getCoolDown(kit) : new GkitCoolDown();

            if (displayItem.lore() != null) {
                for (Component line : displayItem.lore()) {
                    String legacyLoreLine = ColorUtils.toLegacy(line);
                    if (legacyLoreLine.toLowerCase().matches(".*%(day|hour|minute|second)%.*"))
                        line = ColorUtils.legacyTranslateColourCodes(gkitCooldown.getCoolDownLeft(legacyLoreLine));
                    lore.add(line);
                }
            }

            itemMeta.lore(lore);
            displayItem.setItemMeta(itemMeta);

            getInventory().setItem(kit.getSlot() - 1, displayItem);
        }

        return this;
    }

    public static class KitsListener implements Listener {

        private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

        private final @NotNull Starter starter = this.plugin.getStarter();

        private final @NotNull CrazyManager crazyManager = this.starter.getCrazyManager();

        @EventHandler(ignoreCancelled = true)
        public void onPreviewClick(InventoryClickEvent event) {
            if (!(event.getInventory().getHolder(false) instanceof KitsPreviewMenu holder)) return;

            event.setCancelled(true);

            if (event.getClickedInventory() != holder.getInventoryView().getTopInventory()) return;

            ItemStack currentItem = event.getCurrentItem();

            if (currentItem == null) return;

            if (!currentItem.isSimilar(KitsManager.getBackRight())) return;

            MenuManager.openKitsMenu(holder.getPlayer());
        }

        @EventHandler(ignoreCancelled = true)
        public void onInventoryClick(InventoryClickEvent event) {
            if (!(event.getInventory().getHolder(false) instanceof KitsMenu holder)) return;

            event.setCancelled(true);

            Player player = holder.getPlayer();

            ItemStack itemStack = event.getCurrentItem();

            if (itemStack == null || itemStack.isEmpty()) return;

            CEPlayer cePlayer = this.crazyManager.getCEPlayer(player.getUniqueId());

            NBTItem nbtItem = new NBTItem(itemStack);

            if (event.getClickedInventory() != holder.getInventoryView().getTopInventory()) return;

            if (!nbtItem.hasTag("gkit")) return;

            GKitz kit = this.crazyManager.getGKitFromName(nbtItem.getString("gkit"));

            if (event.getAction() == InventoryAction.PICKUP_HALF) {
                List<ItemStack> items = kit.getPreviewItems();
                int slots = Math.min(((items.size() / 9) + (items.size() % 9 > 0 ? 1 : 0)) * 9, 54);

                MenuManager.openKitsPreviewMenu(player, slots, kit);

                return;
            }

            Map<String, String> placeholders = new HashMap<>(1) {{
                put("%Kit%", kit.getName());
            }};

            if (cePlayer.hasGkitPermission(kit)) {
                if (cePlayer.canUseGKit(kit)) {
                    cePlayer.giveGKit(kit);
                    cePlayer.addCoolDown(kit);

                    player.updateInventory();

                    player.sendRichMessage(Messages.RECEIVED_GKIT.getMessage(placeholders));
                } else {
                    //player.sendRichMessage(ColorUtils.getPrefix() + cePlayer.getCoolDown(kit).getCoolDownLeft(Messages.STILL_IN_COOLDOWN.getMessage(placeholders)));
                }
            } else {
                player.sendRichMessage(Messages.NO_GKIT_PERMISSION.getMessage(placeholders));
            }
        }
    }
}