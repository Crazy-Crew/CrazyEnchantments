package com.badbones69.crazyenchantments.listeners;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.api.objects.CEBook;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
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

public class ScramblerListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    private final HashMap<Player, BukkitTask> roll = new HashMap<>();

    private ItemBuilder scramblerItem;
    private ItemBuilder pointer;
    private boolean animationToggle;
    private String guiName;

    public void loadScrambler() {
        FileConfiguration config = Files.CONFIG.getFile();
        scramblerItem = new ItemBuilder()
        .setMaterial(config.getString("Settings.Scrambler.Item"))
        .setName(config.getString("Settings.Scrambler.Name"))
        .setLore(config.getStringList("Settings.Scrambler.Lore"))
        .setGlow(config.getBoolean("Settings.Scrambler.Glowing"));
        pointer = new ItemBuilder()
        .setMaterial(config.getString("Settings.Scrambler.GUI.Pointer.Item"))
        .setName(config.getString("Settings.Scrambler.GUI.Pointer.Name"))
        .setLore(config.getStringList("Settings.Scrambler.GUI.Pointer.Lore"));
        animationToggle = Files.CONFIG.getFile().getBoolean("Settings.Scrambler.GUI.Toggle");
        guiName = methods.color(Files.CONFIG.getFile().getString("Settings.Scrambler.GUI.Name"));
    }

    /**
     * Get a new book that has been scrambled.
     * @param book The old book.
     * @return A new scrambled book.
     */
    public ItemStack getNewScrambledBook(ItemStack book) {
        if (crazyManager.isEnchantmentBook(book)) {
            CEnchantment enchantment = crazyManager.getEnchantmentBookEnchantment(book);
            return new CEBook(enchantment, crazyManager.getBookLevel(book, enchantment), crazyManager.getHighestEnchantmentCategory(enchantment)).buildBook();
        }

        return new ItemStack(Material.AIR);
    }

    /**
     * Get the scrambler item stack.
     * @return The scramblers.
     */
    public ItemStack getScramblers() {
        return getScramblers(1);
    }

    /**
     * Get the scrambler item stack.
     * @param amount The amount you want.
     * @return The scramblers.
     */
    public ItemStack getScramblers(int amount) {
        return scramblerItem.copy().setAmount(amount).build();
    }

    private void setGlass(Inventory inv) {
        for (int slot = 0; slot < 9; slot++) {
            if (slot != 4) {
                inv.setItem(slot, methods.getRandomPaneColor().setName(" ").build());
                inv.setItem(slot + 18, methods.getRandomPaneColor().setName(" ").build());
            } else {
                inv.setItem(slot, pointer.build());
                inv.setItem(slot + 18, pointer.build());
            }
        }
    }

    public void openScrambler(Player player, ItemStack book) {
        Inventory inventory = plugin.getServer().createInventory(null, 27, guiName);
        setGlass(inventory);

        for (int slot = 9; slot > 8 && slot < 18; slot++) {
            inventory.setItem(slot, getNewScrambledBook(book));
        }

        player.openInventory(inventory);
        startScrambler(player, inventory, book);
    }

    private void startScrambler(final Player player, final Inventory inventory, final ItemStack book) {
        roll.put(player, new BukkitRunnable() {
            int time = 1;
            int full = 0;
            int open = 0;

            @Override
            public void run() {
                if (full <= 50) { // When Spinning
                    moveItems(inventory, book);
                    setGlass(inventory);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                }

                open++;

                if (open >= 5) {
                    player.openInventory(inventory);
                    open = 0;
                }

                full++;
                if (full > 51) {
                    if (slowSpin().contains(time)) { // When Slowing Down
                        moveItems(inventory, book);
                        setGlass(inventory);
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    }

                    time++;

                    if (time == 60) { // When done
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        cancel();
                        roll.remove(player);
                        ItemStack item = inventory.getItem(13).clone();
                        item.setType(crazyManager.getEnchantmentBookItem().getType());
                        methods.setDurability(item, methods.getDurability(crazyManager.getEnchantmentBookItem()));

                        if (methods.isInventoryFull(player)) {
                            player.getWorld().dropItem(player.getLocation(), item);
                        } else {
                            player.getInventory().addItem(item);
                        }

                    } else if (time > 60) { // Just in case the cancel fails.
                        cancel();
                    }
                }
            }
        }.runTaskTimer(plugin, 1, 1));
    }

    private List<Integer> slowSpin() {
        List<Integer> slow = new ArrayList<>();
        int full = 120;
        int cut = 15;

        for (int amount = 120; cut > 0; full--) {
            if (full <= amount - cut || full >= amount - cut) {
                slow.add(amount);
                amount = amount - cut;
                cut--;
            }
        }

        return slow;
    }

    private void moveItems(Inventory inv, ItemStack book) {
        List<ItemStack> items = new ArrayList<>();

        for (int slot = 9; slot > 8 && slot < 17; slot++) {
            items.add(inv.getItem(slot));
        }

        ItemStack newBook = getNewScrambledBook(book);
        newBook.setType(methods.getRandomPaneColor().getMaterial());
        inv.setItem(9, newBook);

        for (int amount = 0; amount < 8; amount++) {
            inv.setItem(amount + 10, items.get(amount));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onReRoll(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (e.getClickedInventory() != null) {
            ItemStack book = e.getCurrentItem() != null ? e.getCurrentItem() : new ItemStack(Material.AIR);
            ItemStack scrambler = e.getCursor() != null ? e.getCursor() : new ItemStack(Material.AIR);

            if (book.getType() != Material.AIR && scrambler.getType() != Material.AIR) {
                if (book.getAmount() == 1 && scrambler.getAmount() == 1) {
                    if (getScramblers().isSimilar(scrambler) && crazyManager.isEnchantmentBook(book)) {

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

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent e) {
        if (e.getInventory() != null) {
            if (e.getView().getTitle().equals(guiName)) e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onScramblerClick(PlayerInteractEvent e) {
        ItemStack item = methods.getItemInHand(e.getPlayer());

        if (item != null) {
            if (getScramblers().isSimilar(item)) e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        try {
            roll.get(player).cancel();
            roll.remove(player);
        } catch (Exception ignore) {}
    }
}