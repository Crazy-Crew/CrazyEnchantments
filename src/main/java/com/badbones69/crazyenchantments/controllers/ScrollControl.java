package com.badbones69.crazyenchantments.controllers;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.api.enums.Scrolls;
import com.badbones69.crazyenchantments.api.managers.InfoMenuManager;
import com.badbones69.crazyenchantments.api.objects.CEBook;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.EnchantmentType;
import org.apache.commons.lang.WordUtils;
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
    
    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();
    
    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private final InfoMenuManager infoMenuManager = plugin.getStarter().getInfoMenuManager();
    
    private final Methods methods = plugin.getStarter().getMethods();
    
    private final Random random = new Random();
    private String suffix;
    private boolean countVanillaEnchantments;
    private boolean useSuffix;
    private boolean blackScrollChanceToggle;
    private int blackScrollChance;
    
    public void loadScrollControl() {
        FileConfiguration config = Files.CONFIG.getFile();
        suffix = methods.color(config.getString("Settings.TransmogScroll.Amount-of-Enchantments", " &7[&6&n%amount%&7]"));
        countVanillaEnchantments = config.getBoolean("Settings.TransmogScroll.Count-Vanilla-Enchantments");
        useSuffix = config.getBoolean("Settings.TransmogScroll.Amount-Toggle");
        blackScrollChance = config.getInt("Settings.BlackScroll.Chance", 75);
        blackScrollChanceToggle = config.getBoolean("Settings.BlackScroll.Chance-Toggle");
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onScrollUse(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        ItemStack scroll = e.getCursor();

        if (item != null && scroll != null) {
            InventoryType.SlotType slotType = e.getSlotType();

            if (slotType != InventoryType.SlotType.ARMOR && slotType != InventoryType.SlotType.CONTAINER && slotType != InventoryType.SlotType.QUICKBAR) return;

            if (scroll.isSimilar(Scrolls.TRANSMOG_SCROLL.getScroll())) { // The scroll is a Transmog Scroll.

                if (scroll.getAmount() > 1) {
                    player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());
                    return;
                }

                if (crazyManager.hasEnchantments(item)) {

                    // Checks to see if the item is already ordered.
                    if (item.isSimilar(orderEnchantments(item.clone()))) return;

                    e.setCancelled(true);
                    e.setCurrentItem(orderEnchantments(item));
                    player.setItemOnCursor(methods.removeItem(scroll));
                    player.updateInventory();
                }

            } else if (scroll.isSimilar(Scrolls.WHITE_SCROLL.getScroll())) { // The scroll is a white scroll.

                if (scroll.getAmount() > 1) {
                    player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());
                    return;
                }

                if (!crazyManager.hasWhiteScrollProtection(item)) {
                    for (EnchantmentType enchantmentType : infoMenuManager.getEnchantmentTypes()) {
                        if (enchantmentType.getEnchantableMaterials().contains(item.getType())) {
                            e.setCancelled(true);
                            e.setCurrentItem(crazyManager.addWhiteScrollProtection(item));
                            player.setItemOnCursor(methods.removeItem(scroll));
                            return;
                        }
                    }
                }
            } else if (scroll.isSimilar(Scrolls.BLACK_SCROLL.getScroll())) { // The scroll is a black scroll.

                if (scroll.getAmount() > 1) {
                    player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());
                    return;
                }

                if (methods.isInventoryFull(player)) {
                    player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                    return;
                }

                List<CEnchantment> enchantments = crazyManager.getEnchantmentsOnItem(item);
                if (!enchantments.isEmpty()) { // Item has enchantments
                    e.setCancelled(true);
                    player.setItemOnCursor(methods.removeItem(scroll));

                    if (blackScrollChanceToggle && !methods.randomPicker(blackScrollChance, 100)) {
                        player.sendMessage(Messages.BLACK_SCROLL_UNSUCCESSFUL.getMessage());
                        return;
                    }

                    CEnchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
                    player.getInventory().addItem(new CEBook(enchantment, crazyManager.getLevel(item, enchantment), 1).buildBook());
                    e.setCurrentItem(crazyManager.removeEnchantment(item, enchantment));
                    player.updateInventory();
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack scroll = methods.getItemInHand(player);

        if (scroll != null) {
            if (scroll.isSimilar(Scrolls.BLACK_SCROLL.getScroll())) {
                e.setCancelled(true);
                player.sendMessage(Messages.RIGHT_CLICK_BLACK_SCROLL.getMessage());
            } else if (scroll.isSimilar(Scrolls.WHITE_SCROLL.getScroll()) || scroll.isSimilar(Scrolls.TRANSMOG_SCROLL.getScroll())) {
                e.setCancelled(true);
            }
        }
    }
    
    public ItemStack orderEnchantments(ItemStack item) {
        HashMap<CEnchantment, Integer> enchantmentLevels = new HashMap<>();
        HashMap<CEnchantment, Integer> categories = new HashMap<>();
        List<CEnchantment> newEnchantmentOrder = new ArrayList<>();

        for (CEnchantment enchantment : crazyManager.getEnchantmentsOnItem(item)) {
            enchantmentLevels.put(enchantment, crazyManager.getLevel(item, enchantment));
            crazyManager.removeEnchantment(item, enchantment);
            categories.put(enchantment, crazyManager.getHighestEnchantmentCategory(enchantment).getRarity());
            newEnchantmentOrder.add(enchantment);
        }

        orderInts(newEnchantmentOrder, categories);
        ItemMeta itemMeta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();

        for (CEnchantment enchantment : newEnchantmentOrder) {
            lore.add(enchantment.getColor() + enchantment.getCustomName() + " " + crazyManager.convertLevelString(enchantmentLevels.get(enchantment)));
        }

        assert itemMeta != null;
        if (itemMeta.hasLore()) lore.addAll(itemMeta.getLore());

        itemMeta.setLore(lore);
        // If adding suffix to the item name then it can run this.

        if (useSuffix) {
            String newName = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : methods.color("&b" + WordUtils.capitalizeFully(item.getType().toString().replace("_", " ").toLowerCase()));
            // Checks if the item has a custom name and if so checks to see if it already has the suffix.

            if (itemMeta.hasDisplayName()) {
                for (int i = 0; i <= 100; i++) {
                    String msg = suffix.replace("%Amount%", i + "").replace("%amount%", i + "");

                    if (itemMeta.getDisplayName().endsWith(methods.color(msg))) {
                        newName = itemMeta.getDisplayName().substring(0, itemMeta.getDisplayName().length() - msg.length());
                        break;
                    }
                }
            }

            int amount = newEnchantmentOrder.size();

            if (countVanillaEnchantments) amount += item.getEnchantments().size();

            itemMeta.setDisplayName(newName + suffix.replace("%Amount%", amount + "").replace("%amount%", amount + ""));
        }

        item.setItemMeta(itemMeta);
        return item;
    }
    
    private List<CEnchantment> orderInts(List<CEnchantment> list, final Map<CEnchantment, Integer> map) {
        list.sort((a1, a2) -> {
            Integer string1 = map.get(a1);
            Integer string2 = map.get(a2);
            return string2.compareTo(string1);
        });

        return list;
    }
}