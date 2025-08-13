package com.badbones69.crazyenchantments.paper.api.builders.types.gkitz;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.builders.InventoryBuilder;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEPlayer;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GkitCoolDown;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitsMenu extends InventoryBuilder {

    public KitsMenu(Player player, int size, String title) {
        super(player, size, title);
    }

    private final Starter starter = this.plugin.getStarter();

    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    @Override
    public InventoryBuilder build() {
        final FileConfiguration configuration = Files.GKITZ.getFile();

        for (String value : configuration.getStringList("Settings.GUI-Customization")) {
            int slot = 0;

            for (final String option : value.split(", ")) {
                if (option.contains("Slot:")) {
                    slot = Integer.parseInt(option.replace("Slot:", ""));

                    break;
                }
            }

            slot--;

            getInventory().setItem(slot, ItemBuilder.convertString(value).build());
        }

        final CEPlayer cePlayer = this.crazyManager.getCEPlayer(getPlayer().getUniqueId());

        for (final GKitz kit : this.crazyManager.getGKitz()) {
            final ItemStack displayItem = kit.getDisplayItem().clone();
            final List<Component> lore = new ArrayList<>();
            final GkitCoolDown gkitCooldown = !cePlayer.canUseGKit(kit) && cePlayer.hasGkitPermission(kit) ? cePlayer.getCoolDown(kit) : new GkitCoolDown();


            final List<Component> currentLore = displayItem.lore();

            if (currentLore != null) {
                for (Component line : currentLore) {
                    String legacyLoreLine = ColorUtils.toLegacy(line);
                    if (legacyLoreLine.toLowerCase().matches(".*%(day|hour|minute|second)%.*")) line = ColorUtils.legacyTranslateColourCodes(gkitCooldown.getCoolDownLeft(legacyLoreLine));
                    lore.add(line);
                }
            }

            displayItem.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(lore).build());

            getInventory().setItem(kit.getSlot() - 1, displayItem);
        }

        return this;
    }

    public static class KitsListener implements Listener {

        private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

        private final Starter starter = this.plugin.getStarter();

        private final CrazyManager crazyManager = this.starter.getCrazyManager();

        @EventHandler(ignoreCancelled = true)
        public void onPreviewClick(InventoryClickEvent event) {
            if (!(event.getInventory().getHolder(false) instanceof KitsPreviewMenu holder)) return;

            event.setCancelled(true);

            if (event.getClickedInventory() != holder.getInventoryView().getTopInventory()) return;

            final ItemStack currentItem = event.getCurrentItem();

            if (currentItem == null) return;

            if (!currentItem.isSimilar(KitsManager.getBackRight())) return;

            MenuManager.openKitsMenu(holder.getPlayer());
        }

        @EventHandler(ignoreCancelled = true)
        public void onInventoryClick(InventoryClickEvent event) {
            if (!(event.getInventory().getHolder(false) instanceof KitsMenu holder)) return;

            event.setCancelled(true);

            final Player player = holder.getPlayer();

            final ItemStack itemStack = event.getCurrentItem();

            if (itemStack == null || itemStack.isEmpty()) return;

            final CEPlayer cePlayer = this.crazyManager.getCEPlayer(player.getUniqueId());

            if (event.getClickedInventory() != holder.getInventoryView().getTopInventory()) return;

            final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

            if (!container.has(DataKeys.gkit_type.getNamespacedKey())) return;

            final String kitName = container.get(DataKeys.gkit_type.getNamespacedKey(), PersistentDataType.STRING);

            if (kitName == null) return;

            final GKitz kit = this.crazyManager.getGKitFromName(kitName);

            if (event.getAction() == InventoryAction.PICKUP_HALF) {
                final int amountOfItems = kit.getPreviewItems().size() + 1; // Add 1 to account for the back button.
                final int slots = Math.min(((amountOfItems / 9) + (amountOfItems % 9 > 0 ? 1 : 0)) * 9, 54);

                MenuManager.openKitsPreviewMenu(player, slots, kit);

                return;
            }

            final Map<String, String> placeholders = new HashMap<>(1) {{ put("%Kit%", kit.getName()); }};

            if (cePlayer.hasGkitPermission(kit)) {
                if (cePlayer.canUseGKit(kit)) {
                    cePlayer.giveGKit(kit);
                    cePlayer.addCoolDown(kit);

                    player.updateInventory();

                    player.sendMessage(Messages.RECEIVED_GKIT.getMessage(placeholders));
                } else {
                    player.sendMessage(ColorUtils.getPrefix() + cePlayer.getCoolDown(kit).getCoolDownLeft(Messages.STILL_IN_COOLDOWN.getMessage(placeholders)));
                }
            } else {
                player.sendMessage(Messages.NO_GKIT_PERMISSION.getMessage(placeholders));
            }
        }
    }
}