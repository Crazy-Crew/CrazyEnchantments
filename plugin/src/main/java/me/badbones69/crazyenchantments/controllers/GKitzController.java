package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.managers.InfoMenuManager;
import me.badbones69.crazyenchantments.api.objects.CEPlayer;
import me.badbones69.crazyenchantments.api.objects.Cooldown;
import me.badbones69.crazyenchantments.api.objects.GKitz;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.nbttagapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.simpleyaml.configuration.file.FileConfiguration;

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
        Inventory inv = e.getInventory();
        if (inv != null && e.getCurrentItem() != null) {
            Player player = (Player) e.getWhoClicked();
            CEPlayer cePlayer = ce.getCEPlayer(player);
            ItemStack item = e.getCurrentItem();
            NBTItem nbtItem = new NBTItem(item);
            for (GKitz kit : ce.getGKitz())
                if (e.getView().getTitle().equals(Methods.color(kit.getDisplayItem().getItemMeta().getDisplayName()))) {
                    e.setCancelled(true);
                    if (e.getRawSlot() < inv.getSize() && item.isSimilar(infoManager.getBackRightButton())) {
                        openGUI(player);
                    }
                    return;
                }
            if (e.getView().getTitle().equals(Methods.color(Files.GKITZ.getFile().getString("Settings.Inventory-Name")))) {
                e.setCancelled(true);
                if (e.getRawSlot() < inv.getSize() && nbtItem.hasKey("gkit")) {
                    GKitz kit = ce.getGKitFromName(nbtItem.getString("gkit"));
                    if (e.getAction() == InventoryAction.PICKUP_HALF) {
                        List<ItemStack> items = kit.getPreviewItems();
                        int slots = 9;
                        for (int size = items.size(); size >= 9; size -= 9, e.getCurrentItem()) ;
                        Inventory inventory = Bukkit.createInventory(null, slots, kit.getDisplayItem().getItemMeta().getDisplayName());
                        for (ItemStack itemStack : items) {
                            inventory.addItem(itemStack);
                        }
                        inventory.setItem(slots - 1, infoManager.getBackRightButton());
                        player.openInventory(inventory);
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