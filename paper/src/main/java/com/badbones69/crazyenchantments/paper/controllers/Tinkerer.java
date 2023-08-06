package com.badbones69.crazyenchantments.paper.controllers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.utilities.misc.ColorUtils;
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

    @EventHandler
    public void onXPUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK && methods.getItemInHand(player) != null) {
            ItemStack item = methods.getItemInHand(player);

            if (item.getType() == new ItemBuilder().setMaterial(Files.TINKER.getFile().getString("Settings.BottleOptions.Item")).getMaterial() &&
            item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().hasDisplayName() &&
            item.getItemMeta().getDisplayName().equals(ColorUtils.color(Files.TINKER.getFile().getString("Settings.BottleOptions.Name")))) {
                event.setCancelled(true);
                methods.setItemInHand(player, methods.removeItem(item));

                if (Currency.isCurrency(Files.TINKER.getFile().getString("Settings.Currency"))) currencyAPI.giveCurrency(player, Currency.getCurrency(Files.TINKER.getFile().getString("Settings.Currency")), getXP(item));

                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().equals(ColorUtils.color(Files.TINKER.getFile().getString("Settings.GUIName")))) return;

        event.setCancelled(true);
        ItemStack current = event.getCurrentItem();

        if (current == null || current.getType().isAir() || !current.hasItemMeta()) return;

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

                event.getInventory().setItem(slot, new ItemStack(Material.AIR));
                event.getInventory().setItem(getSlot().get(slot), new ItemStack(Material.AIR));
            }

            player.closeInventory();

            if (total != 0) plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "eco give " + player.getName() + " " + total);

            if (toggle) player.sendMessage(Messages.TINKER_SOLD_MESSAGE.getMessage());

            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
            return;
        }

        if (current.getType().toString().endsWith("STAINED_GLASS_PANE")) return;

        // Adding/taking items.
        if (enchantmentBookSettings.isEnchantmentBook(current)) { // Adding a book.

            CEBook book = enchantmentBookSettings.getCEBook(current);
            if (book == null) return;

            if (inTinker(event.getRawSlot())) { // Clicking in the tinkers.
                event.setCurrentItem(new ItemStack(Material.AIR));
                player.getInventory().addItem(current);
                inv.setItem(getSlot().get(event.getRawSlot()), new ItemStack(Material.AIR));
            } else { // Clicking in their inventory.

                if (player.getOpenInventory().getTopInventory().firstEmpty() == -1) {
                    player.sendMessage(Messages.TINKER_INVENTORY_FULL.getMessage());
                    return;
                }
                if (current.getAmount() > 1) {
                    player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());
                    return;
                }


                event.setCurrentItem(new ItemStack(Material.AIR));
                inv.setItem(getSlot().get(inv.firstEmpty()), Dust.MYSTERY_DUST.getDust(getMaxDustLevelFromBook(book), 1));
                inv.setItem(inv.firstEmpty(), current);
            }

            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            return;
        }

        int totalXP = getTotalXP(current);
        if (totalXP > 0) {
            // Adding an item.
            if (inTinker(event.getRawSlot())) { // Clicking in the tinkers.
                if (getSlot().containsKey(event.getRawSlot())) {
                    event.setCurrentItem(new ItemStack(Material.AIR));
                    player.getInventory().addItem(current);
                    inv.setItem(getSlot().get(event.getRawSlot()), new ItemStack(Material.AIR));
                }
            } else {
                // Clicking in their inventory.
                if (player.getOpenInventory().getTopInventory().firstEmpty() == -1) {
                    player.sendMessage(Messages.TINKER_INVENTORY_FULL.getMessage());
                    return;
                }

                if (current.getAmount() > 1) {
                    player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());
                    return;
                }

                event.setCurrentItem(new ItemStack(Material.AIR));
                inv.setItem(getSlot().get(inv.firstEmpty()), getBottle(String.valueOf(totalXP)));
                inv.setItem(inv.firstEmpty(), current);

            }
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInvClose(final InventoryCloseEvent event) {
        Inventory inv = event.getInventory();
        Player player = (Player) event.getPlayer();

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (event.getView().getTitle().equals(ColorUtils.color(Files.TINKER.getFile().getString("Settings.GUIName")))) {
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

    private ItemStack getBottle(String totalXP) {
        String id = Files.TINKER.getFile().getString("Settings.BottleOptions.Item");
        String name = Files.TINKER.getFile().getString("Settings.BottleOptions.Name");
        List<String> lore = new ArrayList<>();

        for (String l : Files.TINKER.getFile().getStringList("Settings.BottleOptions.Lore")) {
            lore.add(l.replace("%Total%", totalXP).replace("%total%", totalXP));
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
            for (CEnchantment enchantment : enchantmentBookSettings.getEnchantments(item).keySet()) {
                String[] values = Files.TINKER.getFile().getString("Tinker.Crazy-Enchantments." + enchantment.getName() + ".Items").replaceAll(" ", "").split(",");
                int baseAmount = Integer.parseInt(values[0]);
                int multiplier = values.length < 2 ? 0 : Integer.parseInt(values[1]);
                int enchantmentLevel = enchantmentBookSettings.getEnchantments(item).get(enchantment);

                total += baseAmount + enchantmentLevel * multiplier;
            }
        }

        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            for (Enchantment enchantment : item.getEnchantments().keySet()) {
                String[] values = Files.TINKER.getFile().getString("Tinker.Vanilla-Enchantments." + enchantment.getName()).replaceAll(" ", "").split(",");
                int baseAmount = Integer.parseInt(values[0]);
                int multiplier = values.length < 2 ? 0 : Integer.parseInt(values[1]);
                int enchantmentLevel = item.getEnchantments().get(enchantment);

                total += baseAmount + enchantmentLevel * multiplier;
            }
        }

        return total;
    }

    private int getMaxDustLevelFromBook(CEBook book) {
        String path = "Tinker.Crazy-Enchantments." + book.getEnchantment().getName() + ".Book";
        if (!Files.TINKER.getFile().contains(path)) return 1;

        String[] values = Files.TINKER.getFile().getString(path).replaceAll(" ", "").split(",");
        int baseAmount = Integer.parseInt(values[0]);
        int multiplier = values.length < 2 ? 0 : Integer.parseInt(values[1]);

        return baseAmount + book.getLevel() * multiplier;
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