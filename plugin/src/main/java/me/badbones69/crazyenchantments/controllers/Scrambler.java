package me.badbones69.crazyenchantments.controllers;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.objects.CEBook;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Scrambler implements Listener {
    
    public static HashMap<Player, BukkitTask> roll = new HashMap<>();
    private static CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private static ItemBuilder scramblerItem;
    private static ItemBuilder pointer;
    private static boolean animationToggle;
    private static String guiName;
    
    public static void loadScrambler() {
        FileConfiguration config = Files.CONFIG.getFile();
        scramblerItem = new ItemBuilder()
        .setMaterial(config.getString("Settings.Scrambler.Item"))
        .setName(config.getString("Settings.Scrambler.Name"))
        .setLore(config.getStringList("Settings.Scrambler.Lore"))
        .setGlowing(config.getBoolean("Settings.Scrambler.Glowing"));
        pointer = new ItemBuilder()
        .setMaterial(config.getString("Settings.Scrambler.GUI.Pointer.Item"))
        .setName(config.getString("Settings.Scrambler.GUI.Pointer.Name"))
        .setLore(config.getStringList("Settings.Scrambler.GUI.Pointer.Lore"));
        animationToggle = Files.CONFIG.getFile().getBoolean("Settings.Scrambler.GUI.Toggle");
        guiName = Methods.color(Files.CONFIG.getFile().getString("Settings.Scrambler.GUI.Name"));
    }
    
    /**
     * Get a new book that has been scrambled.
     * @param book The old book.
     * @return A new scrambled book.
     */
    public static ItemStack getNewScrambledBook(ItemStack book) {
        if (ce.isEnchantmentBook(book)) {
            CEnchantment enchantment = ce.getEnchantmentBookEnchantment(book);
            return new CEBook(enchantment, ce.getBookLevel(book, enchantment), ce.getHighestEnchantmentCategory(enchantment)).buildBook();
        }
        return new ItemStack(Material.AIR);
    }
    
    /**
     * Get the scrambler itemstack.
     * @return The scramblers.
     */
    public static ItemStack getScramblers() {
        return getScramblers(1);
    }
    
    /**
     * Get the scrambler itemstack.
     * @param amount The amount you want.
     * @return The scramblers.
     */
    public static ItemStack getScramblers(int amount) {
        return scramblerItem.clone().setAmount(amount).build();
    }
    
    private static void setGlass(Inventory inv) {
        for (int slot = 0; slot < 9; slot++) {
            if (slot != 4) {
                inv.setItem(slot, Methods.getRandomPaneColor().setName(" ").build());
                inv.setItem(slot + 18, Methods.getRandomPaneColor().setName(" ").build());
                
            } else {
                inv.setItem(slot, pointer.build());
                inv.setItem(slot + 18, pointer.build());
            }
        }
    }
    
    public static void openScrambler(Player player, ItemStack book) {
        Inventory inventory = Bukkit.createInventory(null, 27, guiName);
        setGlass(inventory);
        for (int slot = 9; slot > 8 && slot < 18; slot++) {
            inventory.setItem(slot, getNewScrambledBook(book));
        }
        player.openInventory(inventory);
        startScrambler(player, inventory, book);
    }
    
    private static void startScrambler(final Player player, final Inventory inventory, final ItemStack book) {
        roll.put(player, new BukkitRunnable() {
            int time = 1;
            int full = 0;
            int open = 0;
            
            @Override
            public void run() {
                if (full <= 50) {//When Spinning
                    moveItems(inventory, player, book);
                    setGlass(inventory);
                    player.playSound(player.getLocation(), ce.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
                }
                open++;
                if (open >= 5) {
                    player.openInventory(inventory);
                    open = 0;
                }
                full++;
                if (full > 51) {
                    if (slowSpin().contains(time)) {//When Slowing Down
                        moveItems(inventory, player, book);
                        setGlass(inventory);
                        player.playSound(player.getLocation(), ce.getSound("UI_BUTTON_CLICK", "CLICK"), 1, 1);
                    }
                    time++;
                    if (time == 60) {// When done
                        player.playSound(player.getLocation(), ce.getSound("ENTITY_PLAYER_LEVELUP", "LEVEL_UP"), 1, 1);
                        cancel();
                        roll.remove(player);
                        ItemStack item = inventory.getItem(13).clone();
                        item.setType(ce.getEnchantmentBookItem().getType());
                        item.setDurability(ce.getEnchantmentBookItem().getDurability());
                        if (Methods.isInventoryFull(player)) {
                            player.getWorld().dropItem(player.getLocation(), item);
                        } else {
                            player.getInventory().addItem(item);
                        }
                    } else if (time > 60) {//Just in case the cancel fails.
                        cancel();
                    }
                }
            }
        }.runTaskTimer(ce.getPlugin(), 1, 1));
    }
    
    private static List<Integer> slowSpin() {
        List<Integer> slow = new ArrayList<>();
        int full = 120;
        int cut = 15;
        for (int i = 120; cut > 0; full--) {
            if (full <= i - cut || full >= i - cut) {
                slow.add(i);
                i = i - cut;
                cut--;
            }
        }
        return slow;
    }
    
    private static void moveItems(Inventory inv, Player player, ItemStack book) {
        List<ItemStack> items = new ArrayList<>();
        for (int slot = 9; slot > 8 && slot < 17; slot++) {
            items.add(inv.getItem(slot));
        }
        ItemStack newBook = getNewScrambledBook(book);
        newBook.setType(Methods.getRandomPaneColor().getMaterial());
        inv.setItem(9, newBook);
        for (int i = 0; i < 8; i++) {
            inv.setItem(i + 10, items.get(i));
        }
    }
    
    @EventHandler
    public void onReRoll(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getClickedInventory() != null) {
            ItemStack book = e.getCurrentItem() != null ? e.getCurrentItem() : new ItemStack(Material.AIR);
            ItemStack scrambler = e.getCursor() != null ? e.getCursor() : new ItemStack(Material.AIR);
            if (book.getType() != Material.AIR && scrambler.getType() != Material.AIR) {
                if (book.getAmount() == 1 && scrambler.getAmount() == 1) {
                    if (getScramblers().isSimilar(scrambler) && ce.isEnchantmentBook(book)) {
                        if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
                            e.setCancelled(true);
                            player.setItemOnCursor(new ItemStack(Material.AIR));
                            if (animationToggle) {
                                e.setCurrentItem(new ItemStack(Material.AIR));
                                openScrambler(player, book);
                            } else {
                                e.setCurrentItem(getNewScrambledBook(book));
                            }
                        } else {
                            player.sendMessage(Messages.NEED_TO_USE_PLAYER_INVENTORY.getMessage());
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getInventory() != null) {
            if (e.getView().getTitle().equals(guiName)) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onScramblerClick(PlayerInteractEvent e) {
        ItemStack item = Methods.getItemInHand(e.getPlayer());
        if (item != null) {
            if (getScramblers().isSimilar(item)) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        try {
            roll.get(player).cancel();
            roll.remove(player);
        } catch (Exception ignore) {
        }
    }
    
}