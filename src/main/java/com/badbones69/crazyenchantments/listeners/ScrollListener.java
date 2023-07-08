package com.badbones69.crazyenchantments.listeners;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.api.enums.Scrolls;
import com.badbones69.crazyenchantments.api.managers.guis.InfoMenuManager;
import com.badbones69.crazyenchantments.api.objects.CEBook;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.utilities.misc.ColorUtils;
import com.badbones69.crazyenchantments.utilities.misc.EnchantUtils;
import com.badbones69.crazyenchantments.utilities.misc.NumberUtils;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ScrollListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    // Plugin Managers.
    private final InfoMenuManager infoMenuManager = starter.getInfoMenuManager();

    private final Random random = new Random();
    private String suffix;
    private boolean countVanillaEnchantments;
    private boolean useSuffix;
    private boolean blackScrollChanceToggle;
    private int blackScrollChance;

    public void loadScrollControl() {
        FileConfiguration config = Files.CONFIG.getFile();
        suffix = config.getString("Settings.TransmogScroll.Amount-of-Enchantments", " &7[&6&n%amount%&7]");
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

        if (item == null || item.getType() == Material.AIR || scroll == null || scroll.getType() == Material.AIR) return;

        InventoryType.SlotType slotType = e.getSlotType();

        if (slotType != InventoryType.SlotType.ARMOR && slotType != InventoryType.SlotType.CONTAINER && slotType != InventoryType.SlotType.QUICKBAR) return;

        Scrolls type = Scrolls.getFromPDC(scroll);
        if (type == null) return;

        if (scroll.getAmount() > 1) {
            player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());
            return;
        }

        switch (type.getConfigName()) {
            case "BlackScroll" -> {
                if (methods.isInventoryFull(player)) {
                    player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                    return;
                }
                List<CEnchantment> enchantments = enchantmentBookSettings.getEnchantmentsOnItem(item);
                if (!enchantments.isEmpty()) { // Item has enchantments
                    e.setCancelled(true);
                    player.setItemOnCursor(methods.removeItem(scroll));

                    if (blackScrollChanceToggle && !methods.randomPicker(blackScrollChance, 100)) {
                        player.sendMessage(Messages.BLACK_SCROLL_UNSUCCESSFUL.getMessage());
                        return;
                    }

                    CEnchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
                    player.getInventory().addItem(new CEBook(enchantment, enchantmentBookSettings.getLevel(item, enchantment), 1).buildBook());
                    e.setCurrentItem(enchantmentBookSettings.removeEnchantment(item, enchantment));
                }
            }
            case "WhiteScroll" -> {
                if (Scrolls.hasWhiteScrollProtection(item)) return;
                for (EnchantmentType enchantmentType : infoMenuManager.getEnchantmentTypes()) {
                    if (enchantmentType.getEnchantableMaterials().contains(item.getType())) {
                        e.setCancelled(true);
                        e.setCurrentItem(Scrolls.addWhiteScrollProtection(item));
                        player.setItemOnCursor(methods.removeItem(scroll));
                        return;
                    }
                }
            }
            case "TransmogScroll" -> {
                if (!enchantmentBookSettings.hasEnchantments(item)) return;

                ItemStack orderedItem = orderNewEnchantments(item.clone());

                if (item.isSimilar(orderedItem)) return;

                e.setCancelled(true);
                e.setCurrentItem(orderedItem);
                player.setItemOnCursor(methods.removeItem(scroll));
            }
        }
    }

    @EventHandler()
    public void onScrollClick(PlayerInteractEvent e) {
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

    private ItemStack orderNewEnchantments(ItemStack item) {

        HashMap<CEnchantment, Integer> enchantmentLevels = new HashMap<>();
        HashMap<CEnchantment, Integer> categories = new HashMap<>();
        List<CEnchantment> newEnchantmentOrder = new ArrayList<>();

        for (Map.Entry<CEnchantment, Integer> enchantment : enchantmentBookSettings.getEnchantments(item).entrySet()) {
            enchantmentLevels.put(enchantment.getKey(), enchantment.getValue());
            categories.put(enchantment.getKey(), EnchantUtils.getHighestEnchantmentCategory(enchantment.getKey()).getRarity());
            newEnchantmentOrder.add(enchantment.getKey());
        }
        orderInts(newEnchantmentOrder, categories);
        ItemMeta newMeta = item.getItemMeta();

        List<Component> newLore = new ArrayList<>();

        newEnchantmentOrder.forEach(enchantment ->
                newLore.add(ColorUtils.legacyTranslateColourCodes(
                        enchantment.getCustomName() + " " + NumberUtils.convertLevelString(enchantmentLevels.get(enchantment)))));

        newMeta.lore(newLore);

        if (useSuffix) {
            String newName = newMeta.hasDisplayName() ? ColorUtils.toLegacy(newMeta.displayName()) :
                    "&b" + WordUtils.capitalizeFully(item.getType().toString().replace("_", " "));

            if (newMeta.hasDisplayName()) {
                for (int i = 0; i <= 100; i++) {
                    String suffixWithAmount = suffix.replace("%Amount%", String.valueOf(i)).replace("%amount%", String.valueOf(i));

                    if (!newName.endsWith(suffixWithAmount)) continue;

                    newName = newName.substring(0, newName.length() - suffixWithAmount.length());
                    break;
                }
            }

            String amount = String.valueOf(countVanillaEnchantments ? newEnchantmentOrder.size() + item.getEnchantments().size() : newEnchantmentOrder.size());

            newMeta.displayName(ColorUtils.legacyTranslateColourCodes(newName + suffix.replace("%Amount%", amount).replace("%amount%", amount)));
        }
        item.setItemMeta(newMeta);
        return item;
    }
    private void orderInts(List<CEnchantment> list, final Map<CEnchantment, Integer> map) {
        list.sort((a1, a2) -> {
            Integer string1 = map.get(a1);
            Integer string2 = map.get(a2);
            return string2.compareTo(string1);
        });
    }
}