package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.currencyapi.Currency;
import me.badbones69.crazyenchantments.api.currencyapi.CurrencyAPI;
import me.badbones69.crazyenchantments.api.enums.Dust;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tinkerer implements Listener {
    
    private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    public static void openTinker(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Methods.color(Files.TINKER.getFile().getString("Settings.GUIName")));
        inv.setItem(0, new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:14")
        .setName(Files.TINKER.getFile().getString("Settings.TradeButton"))
        .setLore(Files.TINKER.getFile().getStringList("Settings.TradeButton-Lore")).build());
        List<Integer> slots = new ArrayList<>();
        slots.add(4);
        slots.add(13);
        slots.add(22);
        slots.add(31);
        slots.add(40);
        slots.add(49);
        for (int i : slots) {
            inv.setItem(i, new ItemBuilder().setMaterial("WHITE_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:0").setName(" ").build());
        }
        inv.setItem(8, new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:14")
        .setName(Files.TINKER.getFile().getString("Settings.TradeButton"))
        .setLore(Files.TINKER.getFile().getStringList("Settings.TradeButton-Lore")).build());
        player.openInventory(inv);
    }
    
    @EventHandler
    public void onXPUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK && Methods.getItemInHand(player) != null) {
            ItemStack item = Methods.getItemInHand(player);
            if (item.getType() == new ItemBuilder().setMaterial(Files.TINKER.getFile().getString("Settings.BottleOptions.Item")).getMaterial() &&
            item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().hasDisplayName() &&
            item.getItemMeta().getDisplayName().equals(Methods.color(Files.TINKER.getFile().getString("Settings.BottleOptions.Name")))) {
                e.setCancelled(true);
                Methods.setItemInHand(player, Methods.removeItem(item));
                if (Currency.isCurrency(Files.TINKER.getFile().getString("Settings.Currency"))) {
                    CurrencyAPI.giveCurrency(player, Currency.getCurrency(Files.TINKER.getFile().getString("Settings.Currency")), getXP(item));
                }
                player.playSound(player.getLocation(), ce.getSound("ENTITY_PLAYER_LEVELUP", "LEVEL_UP"), 1, 1);
            }
        }
    }
    
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        Player player = (Player) e.getWhoClicked();
        if (inv != null && e.getView().getTitle().equals(Methods.color(Files.TINKER.getFile().getString("Settings.GUIName")))) {
            e.setCancelled(true);
            ItemStack current = e.getCurrentItem();
            if (current != null && current.getType() != Material.AIR && current.hasItemMeta() && (current.getItemMeta().hasLore() || current.getItemMeta().hasDisplayName() || current.getItemMeta().hasEnchants())) {
                // Recycling things
                if (current.getItemMeta().hasDisplayName() && current.getItemMeta().getDisplayName().equals(Methods.color(Files.TINKER.getFile().getString("Settings.TradeButton")))) {
                    int total = 0;
                    boolean toggle = false;
                    for (int slot : getSlot().keySet()) {
                        if (inv.getItem(getSlot().get(slot)) != null) {
                            if (Currency.getCurrency(Files.TINKER.getFile().getString("Settings.Currency")) == Currency.VAULT) {
                                ItemStack item = inv.getItem(slot);
                                total = total + getTotalXP(item);
                            } else {
                                if (Methods.isInventoryFull(player)) {
                                    player.getWorld().dropItem(player.getLocation(), inv.getItem(getSlot().get(slot)));
                                } else {
                                    player.getInventory().addItem(inv.getItem(getSlot().get(slot)));
                                }
                            }
                            toggle = true;
                        }
                        e.getInventory().setItem(slot, new ItemStack(Material.AIR));
                        e.getInventory().setItem(getSlot().get(slot), new ItemStack(Material.AIR));
                    }
                    player.closeInventory();
                    if (total != 0) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + player.getName() + " " + total);
                    }
                    if (toggle) {
                        player.sendMessage(Messages.TINKER_SOLD_MESSAGE.getMessage());
                    }
                    player.playSound(player.getLocation(), ce.getSound("ENTITY_VILLAGER_YES", "VILLAGER_YES"), 1, 1);
                    return;
                }
                if (!current.getType().toString().endsWith("STAINED_GLASS_PANE")) {// Adding/Taking Items
                    if (current.getType() == ce.getEnchantmentBookItem().getType()) {// Adding a book
                        boolean toggle = false;
                        String enchant = "";
                        for (CEnchantment en : ce.getRegisteredEnchantments()) {
                            if (current.getItemMeta().getDisplayName().contains(Methods.color(en.getBookColor() + en.getCustomName()))) {
                                enchant = en.getName();
                                toggle = true;
                            }
                        }
                        if (toggle) {
                            if (inTinker(e.getRawSlot())) {// Clicking in the Tinkers
                                e.setCurrentItem(new ItemStack(Material.AIR));
                                player.getInventory().addItem(current);
                                inv.setItem(getSlot().get(e.getRawSlot()), new ItemStack(Material.AIR));
                            } else {// Clicking in their inventory
                                if (player.getOpenInventory().getTopInventory().firstEmpty() == -1) {
                                    player.sendMessage(Messages.TINKER_INVENTORY_FULL.getMessage());
                                    return;
                                }
                                e.setCurrentItem(new ItemStack(Material.AIR));
                                inv.setItem(getSlot().get(inv.firstEmpty()), Dust.MYSTERY_DUST.getDust(Files.TINKER.getFile().getInt("Tinker.Crazy-Enchantments." + enchant + ".Book"), 1));
                                inv.setItem(inv.firstEmpty(), current);
                            }
                            player.playSound(player.getLocation(), ce.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
                        }
                    }
                    if (getTotalXP(current) > 0 && current.getType() != ce.getEnchantmentBookItem().getType()) {// Adding an item
                        if (inTinker(e.getRawSlot())) {// Clicking in the Tinkers
                            if (getSlot().containsKey(e.getRawSlot())) {
                                e.setCurrentItem(new ItemStack(Material.AIR));
                                player.getInventory().addItem(current);
                                inv.setItem(getSlot().get(e.getRawSlot()), new ItemStack(Material.AIR));
                                player.playSound(player.getLocation(), ce.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
                            }
                        } else {// Clicking in their inventory
                            if (player.getOpenInventory().getTopInventory().firstEmpty() == -1) {
                                player.sendMessage(Messages.TINKER_INVENTORY_FULL.getMessage());
                                return;
                            }
                            if (current.getAmount() > 1) {
                                player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());
                                return;
                            }
                            e.setCurrentItem(new ItemStack(Material.AIR));
                            inv.setItem(getSlot().get(inv.firstEmpty()), getBottle(current));
                            inv.setItem(inv.firstEmpty(), current);
                            player.playSound(player.getLocation(), ce.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInvClose(final InventoryCloseEvent e) {
        final Inventory inv = e.getInventory();
        final Player player = (Player) e.getPlayer();
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("CrazyEnchantments"), () -> {
            if (inv != null && e.getView().getTitle().equals(Methods.color(Files.TINKER.getFile().getString("Settings.GUIName")))) {
                for (int slot : getSlot().keySet()) {
                    if (inv.getItem(slot) != null && inv.getItem(slot).getType() != Material.AIR) {
                        if (player.isDead()) {
                            player.getWorld().dropItem(player.getLocation(), inv.getItem(slot));
                        } else {
                            if (Methods.isInventoryFull(player)) {
                                player.getWorld().dropItem(player.getLocation(), inv.getItem(slot));
                            } else {
                                player.getInventory().addItem(inv.getItem(slot));
                            }
                        }
                    }
                }
                inv.clear();
            }
        }, 0);
    }
    
    private ItemStack getBottle(ItemStack item) {
        String id = Files.TINKER.getFile().getString("Settings.BottleOptions.Item");
        String name = Files.TINKER.getFile().getString("Settings.BottleOptions.Name");
        List<String> lore = new ArrayList<>();
        for (String l : Files.TINKER.getFile().getStringList("Settings.BottleOptions.Lore")) {
            lore.add(l.replace("%Total%", getTotalXP(item) + "").replace("%total%", getTotalXP(item) + ""));
        }
        ItemStack it = new ItemBuilder().setMaterial(id).setName(name).setLore(lore).build();
        if (SupportedPlugins.MEGA_SKILLS.isPluginLoaded()) {
            it = new ItemBuilder().setMaterial("EXPERIENCE_BOTTLE", "EXP_BOTTLE").setName("&6Enhanced Exp - &a&l" + getTotalXP(item) + " EXP").build();
        }
        return it;
    }
    
    private HashMap<Integer, Integer> getSlot() {
        HashMap<Integer, Integer> slots = new HashMap<>();
        slots.put(1, 5);
        slots.put(2, 6);
        slots.put(3, 7);
        slots.put(9, 14);
        slots.put(10, 15);
        slots.put(11, 16);
        slots.put(12, 17);
        slots.put(18, 23);
        slots.put(19, 24);
        slots.put(20, 25);
        slots.put(21, 26);
        slots.put(27, 32);
        slots.put(28, 33);
        slots.put(29, 34);
        slots.put(30, 35);
        slots.put(36, 41);
        slots.put(37, 42);
        slots.put(38, 43);
        slots.put(39, 44);
        slots.put(45, 50);
        slots.put(46, 51);
        slots.put(47, 52);
        slots.put(48, 53);
        return slots;
    }
    
    private boolean inTinker(int slot) {
        //The last slot in the tinker is 54
        return slot < 54;
    }
    
    private int getTotalXP(ItemStack item) {
        int total = 0;
        if (ce.hasEnchantments(item)) {
            for (CEnchantment enchantment : ce.getEnchantmentsOnItem(item)) {
                total += Files.TINKER.getFile().getInt("Tinker.Crazy-Enchantments." + enchantment.getName() + ".Items");
            }
        }
        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            for (Enchantment enchantment : item.getEnchantments().keySet()) {
                total += Files.TINKER.getFile().getInt("Tinker.Vanilla-Enchantments." + enchantment.getName());
            }
        }
        return total;
    }
    
    private Integer getXP(ItemStack item) {
        String arg = "";
        int i = 0;
        for (String l : Files.TINKER.getFile().getStringList("Settings.BottleOptions.Lore")) {
            l = Methods.color(l);
            String lo = item.getItemMeta().getLore().get(i);
            if (l.contains("%Total%")) {
                String[] b = l.split("%Total%");
                if (b.length >= 1) arg = lo.replace(b[0], "");
                if (b.length >= 2) arg = arg.replace(b[1], "");
            }
            if (l.contains("%total%")) {
                String[] b = l.split("%total%");
                if (b.length >= 1) arg = lo.replace(b[0], "");
                if (b.length >= 2) arg = arg.replace(b[1], "");
            }
            i++;
        }
        return Integer.parseInt(arg);
    }
    
}
