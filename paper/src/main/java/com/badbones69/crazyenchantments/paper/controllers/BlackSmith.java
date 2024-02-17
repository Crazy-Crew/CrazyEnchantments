package com.badbones69.crazyenchantments.paper.controllers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.managers.BlackSmithManager;
import com.badbones69.crazyenchantments.paper.api.objects.BlackSmithResult;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
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
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;

//TODO() Redo blacksmith gui.
public class BlackSmith implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = CrazyEnchantments.get();

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    // Plugin Managers.
    @NotNull
    private final BlackSmithManager blackSmithManager = this.starter.getBlackSmithManager();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    // Economy Management.
    @NotNull
    private final CurrencyAPI currencyAPI = this.starter.getCurrencyAPI();

    private final int mainSlot = 10;
    private final int subSlot = 13;
    private final static int resultSlot = 16;
    private final static List<Integer> resultBoarder = Arrays.asList(7, 8, 9, 16, 18, 25, 26, 27);
    private final static List<Integer> otherBoarder = Arrays.asList(1, 2, 3, 4, 5, 6, 10, 12, 13, 15, 19, 20, 21, 22, 23, 24);
    private final Sound click = Sound.UI_BUTTON_CLICK;
    private final Sound levelUp = Sound.ENTITY_PLAYER_LEVELUP;
    private final Sound villagerNo = Sound.ENTITY_VILLAGER_NO;
    
    public void openBlackSmith(Player player) {
        Inventory inventory = plugin.getServer().createInventory(null, 27, this.blackSmithManager.getMenuName());
        otherBoarder.forEach(slot -> inventory.setItem(slot - 1, this.blackSmithManager.getGrayGlass()));
        resultBoarder.forEach(slot -> inventory.setItem(slot - 1, this.blackSmithManager.getRedGlass()));
        inventory.setItem(resultSlot, this.blackSmithManager.getDenyBarrier());
        player.openInventory(inventory);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        if (event.getView().getTitle().equals(this.blackSmithManager.getMenuName())) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();

            if (item != null) {
                if (event.getRawSlot() > 26) { // Click in players inventory.
                    if (item.getAmount() != 1) return;

                    if (!this.enchantmentBookSettings.getEnchantments(item).isEmpty() || this.enchantmentBookSettings.isEnchantmentBook(item)) {
                        if (inventory.getItem(this.mainSlot) == null) { // Main item slot is empty.
                            event.setCurrentItem(new ItemStack(Material.AIR));
                            inventory.setItem(this.mainSlot, item); // Moves clicked item to main slot.
                            playSound(player, this.click);

                            if (inventory.getItem(this.subSlot) != null) { // Sub item slot is not empty.
                                BlackSmithResult resultItem = new BlackSmithResult(player, inventory.getItem(this.mainSlot), inventory.getItem(this.subSlot));
                                setResultBoarder(resultItem, inventory);
                            }

                        } else { // Main item slot is not empty.
                            event.setCurrentItem(new ItemStack(Material.AIR));

                            // Sub item slot is not empty.
                            // Moves sub slot item to clicked items slot.
                            if (inventory.getItem(subSlot) != null) event.setCurrentItem(inventory.getItem(this.subSlot));

                            inventory.setItem(this.subSlot, item); // Moves clicked item to sub slot.
                            playSound(player, this.click);
                            BlackSmithResult resultItem = new BlackSmithResult(player, inventory.getItem(this.mainSlot), inventory.getItem(this.subSlot));
                            setResultBoarder(resultItem, inventory);
                        }
                    }
                } else { // Menu click in blacksmith.
                    if (event.getRawSlot() == this.mainSlot || event.getRawSlot() == this.subSlot) { // Clicked either the main slot or sub slot.
                        event.setCurrentItem(new ItemStack(Material.AIR)); // Sets the clicked slot to air.
                        this.methods.addItemToInventory(player, item);
                        inventory.setItem(resultSlot, this.blackSmithManager.getDenyBarrier());
                        resultBoarder.forEach(slot -> inventory.setItem(slot - 1, this.blackSmithManager.getRedGlass()));
                        playSound(player, this.click);
                    }

                    if (event.getRawSlot() == resultSlot) { // Clicks the result item slot.
                        if (inventory.getItem(this.mainSlot) != null && inventory.getItem(this.subSlot) != null) { // Main and sub items are not empty.
                            BlackSmithResult resultItem = new BlackSmithResult(player, inventory.getItem(this.mainSlot), inventory.getItem(this.subSlot));

                            if (resultItem.getCost() > 0) { // Items are upgradeable.
                                if (this.blackSmithManager.getCurrency() != null && player.getGameMode() != GameMode.CREATIVE) {
                                    Currency currency = blackSmithManager.getCurrency();

                                    if (this.currencyAPI.canBuy(player, currency, resultItem.getCost())) {
                                        this.currencyAPI.takeCurrency(player, currency, resultItem.getCost());
                                    } else {
                                        String needed = String.valueOf(resultItem.getCost() - currencyAPI.getCurrency(player, currency));

                                        if (currency != null) this.methods.switchCurrency(player, currency, "%Money_Needed%", "%XP%", needed);
                                        return;
                                    }
                                }

                                this.methods.addItemToInventory(player, resultItem.getResultItem());

                                inventory.setItem(this.mainSlot, new ItemStack(Material.AIR));
                                inventory.setItem(this.subSlot, new ItemStack(Material.AIR));
                                playSound(player, this.levelUp);
                                inventory.setItem(resultSlot, this.blackSmithManager.getDenyBarrier());
                                resultBoarder.forEach(slot -> inventory.setItem(slot - 1, this.blackSmithManager.getRedGlass()));
                            } else {
                                playSound(player, this.villagerNo);
                            }
                        } else {
                            playSound(player, this.villagerNo);
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onInvClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        if (event.getView().getTitle().equals(this.blackSmithManager.getMenuName())) {
            Player player = (Player) event.getPlayer();

            for (int slot : Arrays.asList(this.mainSlot, this.subSlot)) {
                if (inventory.getItem(slot) != null && inventory.getItem(slot).getType() != Material.AIR) this.methods.addItemToInventory(player, inventory.getItem(slot));
            }

            inventory.clear();
        }
    }
    
    private void setResultBoarder(BlackSmithResult resultItem, Inventory inventory) {
        if (resultItem.getCost() > 0) { // Items are upgradable.
            inventory.setItem(resultSlot, this.methods.addLore(resultItem.getResultItem(), getFoundString(resultItem)));
            resultBoarder.forEach(slot -> inventory.setItem(slot - 1, this.blackSmithManager.getBlueGlass()));
        } else { // Items are not upgradable.
            inventory.setItem(resultSlot, this.blackSmithManager.getDenyBarrier());
            resultBoarder.forEach(slot -> inventory.setItem(slot - 1, this.blackSmithManager.getRedGlass()));
        }
    }

    private String getFoundString(BlackSmithResult resultItem) {
        return Messages.replacePlaceholders("%Cost%", String.valueOf(resultItem.getCost()), this.blackSmithManager.getFoundString());
    }
    
    private void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1, 1);
    }
}