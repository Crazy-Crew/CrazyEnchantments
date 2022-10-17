package com.badbones69.crazyenchantments.controllers;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.economy.Currency;
import com.badbones69.crazyenchantments.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.api.managers.BlackSmithManager;
import com.badbones69.crazyenchantments.api.objects.BlackSmithResult;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.Arrays;
import java.util.List;

public class BlackSmith implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    // Plugin Managers.
    private final BlackSmithManager blackSmithManager = starter.getBlackSmithManager();

    // Economy Management.
    private final CurrencyAPI currencyAPI = starter.getCurrencyAPI();

    private final int mainSlot = 10;
    private final int subSlot = 13;
    private final static int resultSlot = 16;
    private final static List<Integer> resultBoarder = Arrays.asList(7, 8, 9, 16, 18, 25, 26, 27);
    private final static List<Integer> otherBoarder = Arrays.asList(1, 2, 3, 4, 5, 6, 10, 12, 13, 15, 19, 20, 21, 22, 23, 24);
    private final Sound click = Sound.UI_BUTTON_CLICK;
    private final Sound levelUp = Sound.ENTITY_PLAYER_LEVELUP;
    private final Sound villagerNo = Sound.ENTITY_VILLAGER_NO;
    
    public void openBlackSmith(Player player) {
        Inventory inventory = plugin.getServer().createInventory(null, 27, blackSmithManager.getMenuName());
        otherBoarder.forEach(slot -> inventory.setItem(slot - 1, blackSmithManager.getGrayGlass()));
        resultBoarder.forEach(slot -> inventory.setItem(slot - 1, blackSmithManager.getRedGlass()));
        inventory.setItem(resultSlot, blackSmithManager.getDenyBarrier());
        player.openInventory(inventory);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inventory = e.getInventory();

        if (e.getView().getTitle().equals(blackSmithManager.getMenuName())) {
            e.setCancelled(true);
            ItemStack item = e.getCurrentItem();

            if (item != null) {
                if (e.getRawSlot() > 26) { // Click In Players Inventory
                    if (item.getAmount() != 1) return;
                    if (crazyManager.hasEnchantments(item) || crazyManager.isEnchantmentBook(item)) {
                        if (inventory.getItem(mainSlot) == null) { // Main item slot is empty
                            e.setCurrentItem(new ItemStack(Material.AIR));
                            inventory.setItem(mainSlot, item); // Moves clicked item to main slot
                            playSound(player, click);

                            if (inventory.getItem(subSlot) != null) { // Sub item slot is not empty
                                BlackSmithResult resultItem = new BlackSmithResult(player, inventory.getItem(mainSlot), inventory.getItem(subSlot));
                                setResultBoarder(resultItem, inventory);
                            }

                        } else { // Main item slot is not empty
                            e.setCurrentItem(new ItemStack(Material.AIR));

                            // Sub item slot is not empty
                            // Moves sub slot item to clicked items slot
                            if (inventory.getItem(subSlot) != null) e.setCurrentItem(inventory.getItem(subSlot));

                            inventory.setItem(subSlot, item); // Moves clicked item to sub slot
                            playSound(player, click);
                            BlackSmithResult resultItem = new BlackSmithResult(player, inventory.getItem(mainSlot), inventory.getItem(subSlot));
                            setResultBoarder(resultItem, inventory);
                        }
                    }
                } else { // Click In the Black Smith
                    if (e.getRawSlot() == mainSlot || e.getRawSlot() == subSlot) { // Clicked either the Main slot or Sub slot
                        e.setCurrentItem(new ItemStack(Material.AIR)); // Sets the clicked slot to air
                        givePlayerItem(player, item);
                        inventory.setItem(resultSlot, blackSmithManager.getDenyBarrier());
                        resultBoarder.forEach(slot -> inventory.setItem(slot - 1, blackSmithManager.getRedGlass()));
                        playSound(player, click);
                    }

                    if (e.getRawSlot() == resultSlot) { // Clicks the result item slot
                        if (inventory.getItem(mainSlot) != null && inventory.getItem(subSlot) != null) { // Main and Sub items are not empty
                            BlackSmithResult resultItem = new BlackSmithResult(player, inventory.getItem(mainSlot), inventory.getItem(subSlot));

                            if (resultItem.getCost() > 0) { // Items are upgradeable
                                if (blackSmithManager.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                                    Currency currency = blackSmithManager.getCurrency();

                                    if (currencyAPI.canBuy(player, currency, resultItem.getCost())) {
                                        currencyAPI.takeCurrency(player, currency, resultItem.getCost());
                                    } else {
                                        String needed = (resultItem.getCost() - currencyAPI.getCurrency(player, currency)) + "";

                                        if (currency != null) methods.switchCurrency(player, currency, "%Money_Needed%", "%XP%", needed);
                                        return;
                                    }
                                }

                                givePlayerItem(player, resultItem.getResultItem());
                                inventory.setItem(mainSlot, new ItemStack(Material.AIR));
                                inventory.setItem(subSlot, new ItemStack(Material.AIR));
                                playSound(player, levelUp);
                                inventory.setItem(resultSlot, blackSmithManager.getDenyBarrier());
                                resultBoarder.forEach(slot -> inventory.setItem(slot - 1, blackSmithManager.getRedGlass()));
                            } else {
                                playSound(player, villagerNo);
                            }
                        } else {
                            playSound(player, villagerNo);
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onInvClose(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();

        if (e.getView().getTitle().equals(blackSmithManager.getMenuName())) {
            Player player = (Player) e.getPlayer();

            for (int slot : Arrays.asList(mainSlot, subSlot)) {
                if (inventory.getItem(slot) != null && inventory.getItem(slot).getType() != Material.AIR) givePlayerItem(player, inventory.getItem(slot));
            }

            inventory.clear();
        }
    }
    
    private void setResultBoarder(BlackSmithResult resultItem, Inventory inventory) {
        if (resultItem.getCost() > 0) { // Items are upgradable
            inventory.setItem(resultSlot, methods.addLore(resultItem.getResultItem(), getFoundString(resultItem)));
            resultBoarder.forEach(slot -> inventory.setItem(slot - 1, blackSmithManager.getBlueGlass()));
        } else { // Items are not upgradable
            inventory.setItem(resultSlot, blackSmithManager.getDenyBarrier());
            resultBoarder.forEach(slot -> inventory.setItem(slot - 1, blackSmithManager.getRedGlass()));
        }
    }
    
    private void givePlayerItem(Player player, ItemStack item) {
        if (methods.isInventoryFull(player) || player.isDead()) {
            player.getWorld().dropItem(player.getLocation(), item);
        } else {
            player.getInventory().addItem(item);
        }
    }
    
    private String getFoundString(BlackSmithResult resultItem) {
        return Messages.replacePlaceholders("%Cost%", resultItem.getCost() + "", blackSmithManager.getFoundString());
    }
    
    private void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1, 1);
    }
}