package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyManager;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.enums.Scrolls;
import me.badbones69.crazyenchantments.api.objects.CEBook;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.EnchantmentType;
import org.apache.commons.text.WordUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.*;

public class ScrollControl implements Listener {
    
    private static CrazyManager ce = CrazyManager.getInstance();
    private Random random = new Random();
    private static String suffix;
    private static boolean countVanillaEnchantments;
    private static boolean useSuffix;
    private static boolean blackScrollChanceToggle;
    private static int blackScrollChance;
    
    public static void loadScrollControl() {
        FileConfiguration config = Files.CONFIG.getFile();
        suffix = Methods.color(config.getString("Settings.TransmogScroll.Amount-of-Enchantments", " &7[&6&n%amount%&7]"));
        countVanillaEnchantments = config.getBoolean("Settings.TransmogScroll.Count-Vanilla-Enchantments");
        useSuffix = config.getBoolean("Settings.TransmogScroll.Amount-Toggle");
        blackScrollChance = config.getInt("Settings.BlackScroll.Chance", 75);
        blackScrollChanceToggle = config.getBoolean("Settings.BlackScroll.Chance-Toggle");
    }
    
    @EventHandler
    public void onScrollUse(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        ItemStack scroll = e.getCursor();
        if (item != null && scroll != null) {
            InventoryType.SlotType slotType = e.getSlotType();

            if (slotType != InventoryType.SlotType.ARMOR && slotType != InventoryType.SlotType.CONTAINER && slotType != InventoryType.SlotType.QUICKBAR) {
                return;
            }

            if (scroll.isSimilar(Scrolls.TRANSMOG_SCROLL.getScroll())) { // The scroll is a Transmog Scroll.

                if (scroll.getAmount() > 1) {
                    player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());
                    return;
                }

                if (ce.hasEnchantments(item)) {
                    // Checks to see if the item is already ordered.
                    if (item.isSimilar(orderEnchantments(item.clone()))) {
                        return;
                    }
                    e.setCancelled(true);
                    e.setCurrentItem(orderEnchantments(item));
                    player.setItemOnCursor(Methods.removeItem(scroll));
                    player.updateInventory();
                }

            } else if (scroll.isSimilar(Scrolls.WHITE_SCROLL.getScroll())) { // The scroll is a white scroll.

                if (scroll.getAmount() > 1) {
                    player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());
                    return;
                }

                if (!ce.hasWhiteScrollProtection(item)) {
                    for (EnchantmentType enchantmentType : ce.getInfoMenuManager().getEnchantmentTypes()) {
                        if (enchantmentType.getEnchantableMaterials().contains(item.getType())) {
                            e.setCancelled(true);
                            e.setCurrentItem(ce.addWhiteScrollProtection(item));
                            player.setItemOnCursor(Methods.removeItem(scroll));
                            return;
                        }
                    }
                }
            } else if (scroll.isSimilar(Scrolls.BLACK_SCROLL.getScroll())) { // The scroll is a black scroll.

                if (scroll.getAmount() > 1) {
                    player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());
                    return;
                }

                if (Methods.isInventoryFull(player)) {
                    player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                    return;
                }

                List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
                if (!enchantments.isEmpty()) { // Item has enchantments
                    e.setCancelled(true);
                    player.setItemOnCursor(Methods.removeItem(scroll));
                    if (blackScrollChanceToggle && !Methods.randomPicker(blackScrollChance, 100)) {
                        player.sendMessage(Messages.BLACK_SCROLL_UNSUCCESSFUL.getMessage());
                        return;
                    }
                    CEnchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
                    player.getInventory().addItem(new CEBook(enchantment, ce.getLevel(item, enchantment), 1).buildBook());
                    e.setCurrentItem(ce.removeEnchantment(item, enchantment));
                    player.updateInventory();
                }
            }
        }
    }
    
    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack scroll = Methods.getItemInHand(player);
        if (scroll != null) {
            if (scroll.isSimilar(Scrolls.BLACK_SCROLL.getScroll())) {
                e.setCancelled(true);
                player.sendMessage(Messages.RIGHT_CLICK_BLACK_SCROLL.getMessage());
            } else if (scroll.isSimilar(Scrolls.WHITE_SCROLL.getScroll()) || scroll.isSimilar(Scrolls.TRANSMOG_SCROLL.getScroll())) {
                e.setCancelled(true);
            }
        }
    }
    
    public static ItemStack orderEnchantments(ItemStack item) {
        HashMap<CEnchantment, Integer> enchantmentLevels = new HashMap<>();
        HashMap<CEnchantment, Integer> categories = new HashMap<>();
        List<CEnchantment> newEnchantmentOrder = new ArrayList<>();

        for (CEnchantment enchantment : ce.getEnchantmentsOnItem(item)) {
            enchantmentLevels.put(enchantment, ce.getLevel(item, enchantment));
            ce.removeEnchantment(item, enchantment);
            categories.put(enchantment, ce.getHighestEnchantmentCategory(enchantment).getRarity());
            newEnchantmentOrder.add(enchantment);
        }

        newEnchantmentOrder = orderInts(newEnchantmentOrder, categories);
        ItemMeta itemMeta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();

        for (CEnchantment enchantment : newEnchantmentOrder) {
            lore.add(enchantment.getColor() + enchantment.getCustomName() + " " + ce.convertLevelString(enchantmentLevels.get(enchantment)));
        }

        if (itemMeta.hasLore()) {
            lore.addAll(itemMeta.getLore());
        }

        itemMeta.setLore(lore);
        // If adding suffix to the item name then it can run this.

        if (useSuffix) {
            String newName = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : Methods.color("&b" + WordUtils.capitalizeFully(item.getType().toString().replace("_", " ").toLowerCase()));
            // Checks if the item has a custom name and if so checks to see if it already has the suffix.

            if (itemMeta.hasDisplayName()) {
                for (int i = 0; i <= 100; i++) {
                    String msg = suffix.replace("%Amount%", i + "").replace("%amount%", i + "");
                    if (itemMeta.getDisplayName().endsWith(Methods.color(msg))) {
                        newName = itemMeta.getDisplayName().substring(0, itemMeta.getDisplayName().length() - msg.length());
                        break;
                    }
                }
            }
            int amount = newEnchantmentOrder.size();

            if (countVanillaEnchantments) {
                amount += item.getEnchantments().size();
            }

            itemMeta.setDisplayName(newName + suffix.replace("%Amount%", amount + "").replace("%amount%", amount + ""));
        }

        item.setItemMeta(itemMeta);
        return item;
    }
    
    private static List<CEnchantment> orderInts(List<CEnchantment> list, final Map<CEnchantment, Integer> map) {
        list.sort((a1, a2) -> {
            Integer string1 = map.get(a1);
            Integer string2 = map.get(a2);
            return string2.compareTo(string1);
        });
        return list;
    }
}