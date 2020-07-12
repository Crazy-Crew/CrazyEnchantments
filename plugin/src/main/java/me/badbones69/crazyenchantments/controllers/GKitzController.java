package me.badbones69.crazyenchantments.controllers;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.managers.InfoMenuManager;
import me.badbones69.crazyenchantments.api.objects.CEPlayer;
import me.badbones69.crazyenchantments.api.objects.Cooldown;
import me.badbones69.crazyenchantments.api.objects.GKitz;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Bukkit;
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
    
    private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private InfoMenuManager infoManager = ce.getInfoMenuManager();
    
    public static void openGUI(Player player) {
        FileConfiguration gkitz = Files.GKITZ.getFile();
        Inventory inventory = Bukkit.createInventory(null, gkitz.getInt("Settings.GUI-Size"), Methods.color(gkitz.getString("Settings.Inventory-Name")));
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
        CEPlayer cePlayer = ce.getCEPlayer(player);
        for (GKitz kit : ce.getGKitz()) {
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
    
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();
        ItemStack item = e.getCurrentItem();
        if (inventory != null && item != null && item.getType() != Material.AIR) {
            Player player = (Player) e.getWhoClicked();
            CEPlayer cePlayer = ce.getCEPlayer(player);
            NBTItem nbtItem = new NBTItem(item);
            for (GKitz kit : ce.getGKitz())
                if (e.getView().getTitle().equals(Methods.color(kit.getDisplayItem().getItemMeta().getDisplayName()))) {
                    e.setCancelled(true);
                    if (e.getRawSlot() < inventory.getSize() && item.isSimilar(infoManager.getBackRightButton())) {
                        openGUI(player);
                    }
                    return;
                }
            if (e.getView().getTitle().equals(Methods.color(Files.GKITZ.getFile().getString("Settings.Inventory-Name")))) {
                e.setCancelled(true);
                if (e.getRawSlot() < inventory.getSize() && nbtItem.hasKey("gkit")) {
                    GKitz kit = ce.getGKitFromName(nbtItem.getString("gkit"));
                    if (e.getAction() == InventoryAction.PICKUP_HALF) {
                        List<ItemStack> items = kit.getPreviewItems();
                        int slots = Math.min(((items.size() / 9) + (items.size() % 9 > 0 ? 1 : 0)) * 9, 54);
                        //Some debug code for when checking the math for slots.
                        //System.out.println((items.size() / 9) + " : " + ((items.size() / 9) * 9) + " : " + items.size() % 9 + " : " + slots);
                        Inventory previewInventory = Bukkit.createInventory(null, slots, kit.getDisplayItem().getItemMeta().getDisplayName());
                        for (ItemStack itemStack : items) {
                            previewInventory.addItem(itemStack);
                        }
                        previewInventory.setItem(slots - 1, infoManager.getBackRightButton());
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
                                player.sendMessage(Methods.getPrefix() + cePlayer.getCooldown(kit).getCooldownLeft(Messages.STILL_IN_COOLDOWN.getMessage(placeholders)));
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