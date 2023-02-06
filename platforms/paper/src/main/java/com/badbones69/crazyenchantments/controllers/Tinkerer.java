package com.badbones69.crazyenchantments.controllers;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.economy.Currency;
import com.badbones69.crazyenchantments.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.api.enums.Dust;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.utilities.misc.ColorUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import java.util.Objects;

public class Tinkerer implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    // Economy Management.
    private final CurrencyAPI currencyAPI = starter.getCurrencyAPI();

    public void openTinker(Player player) {
        Inventory inv = plugin.getServer().createInventory(null, 54, ColorUtils.color(Files.TINKER.getFile().getString("Settings.GUIName")));

        inv.setItem(0, new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE")
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
            inv.setItem(i, new ItemBuilder().setMaterial("WHITE_STAINED_GLASS_PANE").setName(" ").build());
        }

        inv.setItem(8, new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE")
        .setName(Files.TINKER.getFile().getString("Settings.TradeButton"))
        .setLore(Files.TINKER.getFile().getStringList("Settings.TradeButton-Lore")).build());
        player.openInventory(inv);
    }

    @EventHandler(ignoreCancelled = true)
    public void onXPUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK && methods.getItemInHand(player) != null) {
            ItemStack item = methods.getItemInHand(player);

            if (item.getType() == new ItemBuilder().setMaterial(Files.TINKER.getFile().getString("Settings.BottleOptions.Item")).getMaterial() &&
            item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().hasDisplayName() &&
            item.getItemMeta().getDisplayName().equals(ColorUtils.color(Files.TINKER.getFile().getString("Settings.BottleOptions.Name")))) {
                e.setCancelled(true);
                methods.setItemInHand(player, methods.removeItem(item));

                if (Currency.isCurrency(Files.TINKER.getFile().getString("Settings.Currency"))) currencyAPI.giveCurrency(player, Currency.getCurrency(Files.TINKER.getFile().getString("Settings.Currency")), getXP(item));

                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        Player player = (Player) e.getWhoClicked();

        if (e.getView().getTitle().equals(ColorUtils.color(Files.TINKER.getFile().getString("Settings.GUIName")))) {
            e.setCancelled(true);
            ItemStack current = e.getCurrentItem();

            if (current != null && current.getType() != Material.AIR && current.hasItemMeta() && (current.getItemMeta().hasLore() || current.getItemMeta().hasDisplayName() || current.getItemMeta().hasEnchants())) {
                // Recycling things.
                if (current.getItemMeta().hasDisplayName() && current.getItemMeta().getDisplayName().equals(ColorUtils.color(Files.TINKER.getFile().getString("Settings.TradeButton")))) {
                    int total = 0;
                    boolean toggle = false;

                    for (int slot : getSlot().keySet()) {
                        if (inv.getItem(getSlot().get(slot)) != null) {
                            if (Currency.getCurrency(Files.TINKER.getFile().getString("Settings.Currency")) == Currency.VAULT) {
                                ItemStack item = inv.getItem(slot);
                                total = total + getTotalXP(item);
                            } else {
                                if (methods.isInventoryFull(player)) {
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

                    if (total != 0) plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "eco give " + player.getName() + " " + total);

                    if (toggle) player.sendMessage(Messages.TINKER_SOLD_MESSAGE.getMessage());

                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                    return;
                }

                if (!current.getType().toString().endsWith("STAINED_GLASS_PANE")) { // Adding/taking items.
                    if (current.getType() == enchantmentBookSettings.getEnchantmentBookItem().getType()) { // Adding a book.
                        boolean toggle = false;
                        String enchant = "";

                        for (CEnchantment en : crazyManager.getRegisteredEnchantments()) {
                            if (current.getItemMeta().getDisplayName().contains(ColorUtils.color(en.getBookColor() + en.getCustomName()))) {
                                enchant = en.getName();
                                toggle = true;
                            }
                        }

                        if (toggle) {
                            if (inTinker(e.getRawSlot())) { // Clicking in the tinkers.
                                e.setCurrentItem(new ItemStack(Material.AIR));
                                player.getInventory().addItem(current);
                                inv.setItem(getSlot().get(e.getRawSlot()), new ItemStack(Material.AIR));
                            } else { // Clicking in their inventory.
                                if (player.getOpenInventory().getTopInventory().firstEmpty() == -1) {
                                    player.sendMessage(Messages.TINKER_INVENTORY_FULL.getMessage());
                                    return;
                                }

                                e.setCurrentItem(new ItemStack(Material.AIR));
                                inv.setItem(getSlot().get(inv.firstEmpty()), Dust.MYSTERY_DUST.getDust(Files.TINKER.getFile().getInt("Tinker.Crazy-Enchantments." + enchant + ".Book"), 1));
                                inv.setItem(inv.firstEmpty(), current);
                            }

                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                        }
                    }

                    if (getTotalXP(current) > 0 && current.getType() != enchantmentBookSettings.getEnchantmentBookItem().getType()) { // Adding an item.
                        if (inTinker(e.getRawSlot())) { // Clicking in the tinkers.

                            if (getSlot().containsKey(e.getRawSlot())) {
                                e.setCurrentItem(new ItemStack(Material.AIR));
                                player.getInventory().addItem(current);
                                inv.setItem(getSlot().get(e.getRawSlot()), new ItemStack(Material.AIR));
                                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                            }
                        } else { // Clicking in their inventory.

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
                            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInvClose(final InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        Player player = (Player) e.getPlayer();

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (e.getView().getTitle().equals(ColorUtils.color(Files.TINKER.getFile().getString("Settings.GUIName")))) {
                for (int slot : getSlot().keySet()) {
                    if (inv.getItem(slot) != null && inv.getItem(slot).getType() != Material.AIR) {
                        if (player.isDead()) {
                            player.getWorld().dropItem(player.getLocation(), inv.getItem(slot));
                        } else {
                            if (methods.isInventoryFull(player)) {
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

        assert id != null;
        return new ItemBuilder().setMaterial(id).setName(name).setLore(lore).build();
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
        // The last slot in the tinker is 54.
        return slot < 54;
    }

    private int getTotalXP(ItemStack item) {
        int total = 0;

        if (enchantmentBookSettings.hasEnchantments(item)) {
            for (CEnchantment enchantment : enchantmentBookSettings.getEnchantmentsOnItem(item)) {
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
        int amount = 0;

        for (String lore : Files.TINKER.getFile().getStringList("Settings.BottleOptions.Lore")) {
            lore = ColorUtils.color(lore);
            String itemLore = Objects.requireNonNull(item.getItemMeta().getLore()).get(amount);

            if (lore.contains("%Total%")) {
                String[] b = lore.split("%Total%");
                if (b.length >= 1) arg = itemLore.replace(b[0], "");
                if (b.length >= 2) arg = arg.replace(b[1], "");
            }

            if (lore.contains("%total%")) {
                String[] b = lore.split("%total%");
                if (b.length >= 1) arg = itemLore.replace(b[0], "");
                if (b.length >= 2) arg = arg.replace(b[1], "");
            }

            amount++;
        }

        return Integer.parseInt(arg);
    }
}