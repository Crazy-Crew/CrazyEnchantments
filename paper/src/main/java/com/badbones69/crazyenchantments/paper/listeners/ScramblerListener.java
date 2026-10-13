package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.ryderbelserion.common.api.enums.messages.Messages;
import com.ryderbelserion.common.api.enums.Files;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.ryderbelserion.common.utils.ConfigUtils;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.builders.folia.FoliaScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ScramblerListener implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final ComponentLogger logger = this.plugin.getComponentLogger();

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private final HashMap<Player, ScheduledTask> roll = new HashMap<>();

    private ItemBuilder scramblerItem;
    private ItemBuilder pointer;
    private boolean animationToggle;
    private String guiName;

    public void loadScrambler() {
        final CommentedConfigurationNode config = Files.CONFIG.getConfiguration();

        this.scramblerItem = new ItemBuilder()
                .setMaterial(ConfigUtils.getNode(config, "Settings.Scrambler.Item").getString("SUNFLOWER"))
        .setItemModel(ConfigUtils.getNode(config, "Settings.Scrambler.Item.Model.Namespace").getString(""),
                ConfigUtils.getNode(config, "Settings.Scrambler.Item.Model.Key").getString(""))
        .setName(ConfigUtils.getNode(config, "Settings.Scrambler.Name").getString("&cError getting name for scrambler item."))
        .setLore(StringUtils.getStringList(config.node("Settings", "Scrambler", "Lore")))
        .setGlow(ConfigUtils.getNode(config, "Settings.Scrambler.Glowing").getBoolean(false));

        this.pointer = new ItemBuilder()
                .setMaterial(ConfigUtils.getNode(config, "Settings.Scrambler.GUI.Pointer.Item").getString("REDSTONE_TORCH"))
        .setItemModel(ConfigUtils.getNode(config, "Settings.Scrambler.GUI.Pointer.Item.Model.Namespace").getString(""),
                ConfigUtils.getNode(config, "Settings.Scrambler.GUI.Pointer.Item.Model.Key").getString(""))
        .setName(ConfigUtils.getNode(config, "Settings.Scrambler.GUI.Pointer.Name").getString("&cError getting name for pointer item."))
        .setLore(StringUtils.getStringList(config.node("Settings", "Scrambler", "GUI", "Pointer", "Lore")));

        this.animationToggle = config.node("Settings", "Scrambler", "GUI", "Toggle").getBoolean(true);
        this.guiName = ColorUtils.color(config.node("Settings", "Scrambler", "GUI", "Name").getString("&cError getting name for scrambler gui name."));
    }

    /**
     * Get the scrambler item stack.
     * @return The scramblers.
     */
    public ItemStack getScramblers(final Player player) {
        return getScramblers(player, 1);
    }

    /**
     * Get the scrambler item stack.
     * @param amount The amount you want.
     * @return The scramblers.
     */
    public ItemStack getScramblers(final Player player, int amount) {
        ItemStack item = this.scramblerItem.setAmount(amount).build(player);

        item.editPersistentDataContainer(container -> {
            container.set(DataKeys.scrambler.getNamespacedKey(), PersistentDataType.BOOLEAN, true);
        });

        return item;
    }

    public boolean isScrambler(ItemStack item) {
        return item.getPersistentDataContainer().has(DataKeys.scrambler.getNamespacedKey());
    }

    private void setGlass(Inventory inv) {
        for (int slot = 0; slot < 9; slot++) {
            if (slot != 4) {
                inv.setItem(slot, ColorUtils.getRandomPaneColor().setName(" ").build());
                inv.setItem(slot + 18, ColorUtils.getRandomPaneColor().setName(" ").build());
            } else {
                inv.setItem(slot, this.pointer.build());
                inv.setItem(slot + 18, this.pointer.build());
            }
        }
    }

    public void openScrambler(Player player, ItemStack book) {
        Inventory inventory = this.plugin.getServer().createInventory(null, 27, this.guiName);

        setGlass(inventory);

        for (int slot = 9; slot > 8 && slot < 18; slot++) {
            inventory.setItem(slot, this.enchantmentBookSettings.getNewScrambledBook(player, book));
        }

        player.openInventory(inventory);
        startScrambler(player, inventory, book);
    }

    private void startScrambler(final Player player, final Inventory inventory, final ItemStack book) {
        this.roll.put(player, new FoliaScheduler(this.plugin, null, player) {
            int time = 1;
            int full = 0;
            int open = 0;

            @Override
            public void run() {
                if (this.full <= 50) { // When spinning.
                    moveItems(player, inventory, book);

                    setGlass(inventory);

                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                }

                this.open++;

                if (this.open >= 5) {
                    player.openInventory(inventory);

                    this.open = 0;
                }

                this.full++;

                if (this.full > 51) {
                    if (slowSpin().contains(this.time)) { // When Slowing Down
                        moveItems(player, inventory, book);

                        setGlass(inventory);

                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    }

                    this.time++;

                    if (this.time == 60) { // When done
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

                        cancel();

                        roll.remove(player);

                        final ItemStack item = inventory.getItem(13);

                        if (item != null) { //todo() what
                            ItemStack clone;

                            clone = item.withType(enchantmentBookSettings.getEnchantmentBookItem(player).getType());

                            methods.setDurability(item, methods.getDurability(enchantmentBookSettings.getEnchantmentBookItem(player)));

                            methods.addItemToInventory(player, clone);
                        } else {
                            logger.error("The item at slot 13 is null, We cannot continue!");
                        }

                    } else if (this.time > 60) { // Just in case the cancel fails.
                        cancel();
                    }
                }
            }
        }.runAtFixedRate(1, 1));
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

    private void moveItems(Player player, Inventory inv, ItemStack book) {
        List<ItemStack> items = new ArrayList<>();

        for (int slot = 9; slot > 8 && slot < 17; slot++) {
            items.add(inv.getItem(slot));
        }

        ItemStack newBook = this.enchantmentBookSettings.getNewScrambledBook(player, book);
        newBook.setType(ColorUtils.getRandomPaneColor().getMaterial());
        inv.setItem(9, newBook);

        for (int amount = 0; amount < 8; amount++) {
            inv.setItem(amount + 10, items.get(amount));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onReRoll(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        final Inventory inventory = event.getClickedInventory();

        if (inventory == null) return;

        final ItemStack currentItem = event.getCurrentItem();

        ItemStack book = currentItem != null ? currentItem : ItemStack.empty();

        ItemStack scrambler = event.getCursor();

        if (book.isEmpty() || scrambler.isEmpty()) return;

        if (book.getAmount() != 1 || scrambler.getAmount() != 1) return;

        if (!isScrambler(scrambler) || !this.enchantmentBookSettings.isEnchantmentBook(book)) return;

        if (inventory.getType() != InventoryType.PLAYER) {
            Messages.need_to_use_player_inventory.sendMessage(player);

            return;
        }

        event.setCancelled(true);

        player.setItemOnCursor(ItemStack.empty());

        if (this.animationToggle) {
            event.setCurrentItem(ItemStack.empty());
            openScrambler(player, book);
        } else {
            event.setCurrentItem(this.enchantmentBookSettings.getNewScrambledBook(player, book));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(this.guiName)) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onScramblerClick(PlayerInteractEvent event) {
        ItemStack item = this.methods.getItemInHand(event.getPlayer());

        if (item.isEmpty()) return;

        if (item.getPersistentDataContainer().has(DataKeys.scrambler.getNamespacedKey())) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        try {
            this.roll.get(player).cancel();
            this.roll.remove(player);
        } catch (Exception ignored) {}
    }

    @EventHandler(ignoreCancelled = true)
    public void onScrollClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (isScrambler(player.getInventory().getItemInMainHand()) || isScrambler(player.getInventory().getItemInOffHand())) event.setCancelled(true);
    }
}