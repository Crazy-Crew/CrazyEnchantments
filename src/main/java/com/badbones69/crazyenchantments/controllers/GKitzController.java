package com.badbones69.crazyenchantments.controllers;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.api.managers.InfoMenuManager;
import com.badbones69.crazyenchantments.api.objects.CEPlayer;
import com.badbones69.crazyenchantments.api.objects.Cooldown;
import com.badbones69.crazyenchantments.api.objects.GKitz;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GKitzController implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    // Plugin Managers.
    private final InfoMenuManager infoMenuManager = crazyManager.getInfoMenuManager();

    public void openGUI(Player player) {
        FileConfiguration gkitz = Files.GKITZ.getFile();
        Inventory inventory = plugin.getServer().createInventory(null, gkitz.getInt("Settings.GUI-Size"), methods.color(gkitz.getString("Settings.Inventory-Name")));

        for (String customItemString : gkitz.getStringList("Settings.GUI-Customization")) {
            int slot = 0;

            for (String option : customItemString.split(", ")) {
                if (option.contains("Slot:")) {
                    option = option.replace("Slot:", "");
                    slot = Integer.parseInt(option);
                    break;
                }
            }

            slot--;
            inventory.setItem(slot, ItemBuilder.convertString(customItemString).build());
        }

        CEPlayer cePlayer = crazyManager.getCEPlayer(player);

        for (GKitz kit : crazyManager.getGKitz()) {
            ItemStack displayItem = kit.getDisplayItem().clone();
            ItemMeta itemMeta = displayItem.getItemMeta();
            List<String> lore = new ArrayList<>();
            Cooldown cooldown = !cePlayer.canUseGKit(kit) && cePlayer.hasGkitPermission(kit) ? cePlayer.getCooldown(kit) : new Cooldown();

            if (displayItem.hasItemMeta() && displayItem.getItemMeta().hasLore()) {
                for (String line : displayItem.getItemMeta().getLore()) {
                    lore.add(cooldown.getCooldownLeft(line));
                }
            }

            itemMeta.setLore(lore);
            displayItem.setItemMeta(itemMeta);
            inventory.setItem(kit.getSlot() - 1, displayItem);
        }

        player.openInventory(inventory);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();
        ItemStack item = e.getCurrentItem();

        if (item != null && item.getType() != Material.AIR) {
            Player player = (Player) e.getWhoClicked();
            CEPlayer cePlayer = crazyManager.getCEPlayer(player);
            NBTItem nbtItem = new NBTItem(item);

            for (GKitz kit : crazyManager.getGKitz()) {
                if (e.getView().getTitle().equals(methods.color(kit.getDisplayItem().getItemMeta().getDisplayName()))) {
                    e.setCancelled(true);

                    if (e.getRawSlot() < inventory.getSize() && item.isSimilar(infoMenuManager.getBackRightButton())) openGUI(player);

                    return;
                }
            }

            if (e.getView().getTitle().equals(methods.color(Files.GKITZ.getFile().getString("Settings.Inventory-Name")))) {
                e.setCancelled(true);

                if (e.getRawSlot() < inventory.getSize() && nbtItem.hasKey("gkit")) {
                    GKitz kit = crazyManager.getGKitFromName(nbtItem.getString("gkit"));

                    if (e.getAction() == InventoryAction.PICKUP_HALF) {
                        List<ItemStack> items = kit.getPreviewItems();
                        int slots = Math.min(((items.size() / 9) + (items.size() % 9 > 0 ? 1 : 0)) * 9, 54);

                        Inventory previewInventory = plugin.getServer().createInventory(null, slots, kit.getDisplayItem().getItemMeta().getDisplayName());

                        for (ItemStack itemStack : items) {
                            previewInventory.addItem(itemStack);
                        }

                        previewInventory.setItem(slots - 1, infoMenuManager.getBackRightButton());
                        player.openInventory(previewInventory);
                    } else {
                        HashMap<String, String> placeholders = new HashMap<>();
                        placeholders.put("%Kit%", kit.getName());

                        if (cePlayer.hasGkitPermission(kit)) {
                            if (cePlayer.canUseGKit(kit)) {
                                cePlayer.giveGKit(kit);
                                cePlayer.addCooldown(kit);
                                player.sendMessage(Messages.RECEIVED_GKIT.getMessage(placeholders));
                            } else {
                                player.sendMessage(methods.getPrefix() + cePlayer.getCooldown(kit).getCooldownLeft(Messages.STILL_IN_COOLDOWN.getMessage(placeholders)));
                            }
                        } else {
                            player.sendMessage(Messages.NO_GKIT_PERMISSION.getMessage(placeholders));
                        }
                    }
                }
            }
        }
    }
}