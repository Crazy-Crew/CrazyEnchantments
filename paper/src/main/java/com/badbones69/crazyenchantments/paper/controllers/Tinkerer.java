package com.badbones69.crazyenchantments.paper.controllers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.enums.Dust;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.utilities.misc.ColorUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class Tinkerer implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    private final HashMap<Integer, Integer> slots = getSlots();

    // Economy Management.
    private final CurrencyAPI currencyAPI = starter.getCurrencyAPI();

    private final ItemStack tradeButton = new ItemBuilder().setMaterial("RED_STAINED_GLASS_PANE")
            .setName(Files.TINKER.getFile().getString("Settings.TradeButton"))
            .setLore(Files.TINKER.getFile().getStringList("Settings.TradeButton-Lore")).build();

    public void openTinker(Player player) {
        Inventory inv = plugin.getServer().createInventory(null, 54, ColorUtils.legacyTranslateColourCodes(Files.TINKER.getFile().getString("Settings.GUIName")));

        inv.setItem(0, tradeButton);
        inv.setItem(8, tradeButton);

        // Set Divider
        ItemStack divider = new ItemBuilder().setMaterial("WHITE_STAINED_GLASS_PANE").setName(" ").build();
        List.of(4, 13, 22, 31, 40, 49).forEach(x -> inv.setItem(x, divider));

        player.openInventory(inv);
    }

    @EventHandler
    public void onXPUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;

        ItemStack item = methods.getItemInHand(player);
        ItemMeta meta = item.getItemMeta();

        if (meta == null || !item.getItemMeta().getPersistentDataContainer().has(DataKeys.EXPERIENCE.getKey())) return;
        int amount = Integer.parseInt(item.getItemMeta().getPersistentDataContainer().getOrDefault(DataKeys.EXPERIENCE.getKey(), PersistentDataType.STRING, "0"));

        event.setCancelled(true);
        methods.setItemInHand(player, methods.removeItem(item));

        if (Currency.isCurrency(Files.TINKER.getFile().getString("Settings.Currency")))
            currencyAPI.giveCurrency(player, Currency.getCurrency(Files.TINKER.getFile().getString("Settings.Currency")), amount);

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        Player player = (Player) event.getWhoClicked();

        if (!event.getView().title().equals(ColorUtils.legacyTranslateColourCodes(Files.TINKER.getFile().getString("Settings.GUIName")))) return;

        event.setCancelled(true);
        ItemStack current = event.getCurrentItem();

        if (current == null || current.isEmpty() || !current.hasItemMeta()) return;

        // Recycling things.
        if (Objects.equals(current, tradeButton)) {
            int total = 0;
            boolean toggle = false;

             for (int slot : slots.keySet()) {
                ItemStack reward = inv.getItem(slots.get(slot));
                if (reward != null) {
                    if (Currency.getCurrency(Files.TINKER.getFile().getString("Settings.Currency")) == Currency.VAULT) {
                        total = getTotalXP(inv.getItem(slot));
                    } else {
                        player.getInventory().addItem(reward).values().forEach(item -> player.getWorld().dropItem(player.getLocation(), item));
                    }

                    toggle = true;
                }

                event.getInventory().setItem(slot, new ItemStack(Material.AIR));
                event.getInventory().setItem(slots.get(slot), new ItemStack(Material.AIR));
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
                inv.setItem(slots.get(event.getRawSlot()), new ItemStack(Material.AIR));
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
                inv.setItem(slots.get(inv.firstEmpty()), Dust.MYSTERY_DUST.getDust(getMaxDustLevelFromBook(book), 1));
                inv.setItem(inv.firstEmpty(), current);
            }

            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            return;
        }

        int totalXP = getTotalXP(current);
        if (totalXP > 0) {
            // Adding an item.
            if (inTinker(event.getRawSlot())) { // Clicking in the tinkers.
                if (slots.containsKey(event.getRawSlot())) {
                    event.setCurrentItem(new ItemStack(Material.AIR));
                    player.getInventory().addItem(current);
                    inv.setItem(slots.get(event.getRawSlot()), new ItemStack(Material.AIR));
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
                inv.setItem(slots.get(inv.firstEmpty()), getXPBottle(String.valueOf(totalXP)));
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
            if (event.getView().title().equals(ColorUtils.legacyTranslateColourCodes(Files.TINKER.getFile().getString("Settings.GUIName")))) {
                for (int slot : slots.keySet()) {
                    ItemStack item = inv.getItem(slot);
                    if (item == null || item.isEmpty()) continue;

                    if (player.isDead()) {
                        player.getWorld().dropItem(player.getLocation(), item);
                    } else {
                        player.getInventory().addItem(item).values().forEach(item2 -> player.getWorld().dropItem(player.getLocation(), item2));
                    }
                }

                inv.clear();
            }
        }, 0);
    }

    /**
     *
     * @param amount Amount of XP to store.
     * @return XP Bottle with custom amount of xp stored in it.
     */
    public ItemStack getXPBottle(String amount) {
        String id = Files.TINKER.getFile().getString("Settings.BottleOptions.Item");
        String name = Files.TINKER.getFile().getString("Settings.BottleOptions.Name");
        List<String> lore = new ArrayList<>();

        for (String l : Files.TINKER.getFile().getStringList("Settings.BottleOptions.Lore")) {
            lore.add(l.replace("%Total%", amount).replace("%total%", amount));
        }

        assert id != null;
        return new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setStringPDC(DataKeys.EXPERIENCE.getKey(), amount).build();
    }

    private HashMap<Integer, Integer> getSlots() {

        return new HashMap<>(23) {{
            put(1, 5);
            put(2, 6);
            put(3, 7);
            put(9, 14);
            put(10, 15);
            put(11, 16);
            put(12, 17);
            put(18, 23);
            put(19, 24);
            put(20, 25);
            put(21, 26);
            put(27, 32);
            put(28, 33);
            put(29, 34);
            put(30, 35);
            put(36, 41);
            put(37, 42);
            put(38, 43);
            put(39, 44);
            put(45, 50);
            put(46, 51);
            put(47, 52);
            put(48, 53);
        }};
    }

    private boolean inTinker(int slot) {
        // The last slot in the tinker is 54.
        return slot < 54;
    }

    private int getTotalXP(ItemStack item) {
        int total = 0;

        Map<CEnchantment, Integer> ceEnchants = enchantmentBookSettings.getEnchantments(item);
        if (!ceEnchants.isEmpty()) { // CrazyEnchantments
            for (Map.Entry<CEnchantment, Integer> enchantment : ceEnchants.entrySet()) {
                String[] values = Files.TINKER.getFile().getString("Tinker.Crazy-Enchantments." + enchantment.getKey().getName() + ".Items", "0").replaceAll(" ", "").split(",");
                int baseAmount = Integer.parseInt(values[0]);
                int multiplier = values.length < 2 ? 0 : Integer.parseInt(values[1]);
                int enchantmentLevel = enchantment.getValue();

                total += baseAmount + enchantmentLevel * multiplier;
            }
        }

        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) { // Vanilla Enchantments
            for (Map.Entry<Enchantment, Integer> enchantment : item.getEnchantments().entrySet()) {
                String[] values = Files.TINKER.getFile().getString("Tinker.Vanilla-Enchantments." + enchantment.getKey(), "0").replaceAll(" ", "").split(",");
                int baseAmount = Integer.parseInt(values[0]);
                int multiplier = values.length < 2 ? 0 : Integer.parseInt(values[1]);
                int enchantmentLevel = enchantment.getValue();

                total += baseAmount + enchantmentLevel * multiplier;
            }
        }

        return total;
    }

    private int getMaxDustLevelFromBook(CEBook book) {
        String path = "Tinker.Crazy-Enchantments." + book.getEnchantment().getName() + ".Book";
        if (!Files.TINKER.getFile().contains(path)) return 1;

        String[] values = Files.TINKER.getFile().getString(path, "0").replaceAll(" ", "").split(",");
        int baseAmount = Integer.parseInt(values[0]);
        int multiplier = values.length < 2 ? 0 : Integer.parseInt(values[1]);

        return baseAmount + book.getLevel() * multiplier;
    }

}