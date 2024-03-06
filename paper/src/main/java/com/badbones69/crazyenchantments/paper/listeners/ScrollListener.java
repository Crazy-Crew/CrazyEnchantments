package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.FileManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.paper.api.managers.guis.InfoMenuManager;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EnchantUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.WordUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ScrollListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.get();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    // Plugin Managers.
    private final InfoMenuManager infoMenuManager = starter.getInfoMenuManager();
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
    public void onScrollUse(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        ItemStack scroll = event.getCursor();

        if (item == null || item.getType().isAir() || scroll.getType().isAir()) return;

        InventoryType.SlotType slotType = event.getSlotType();

        if (slotType != InventoryType.SlotType.ARMOR && slotType != InventoryType.SlotType.CONTAINER && slotType != InventoryType.SlotType.QUICKBAR) return;

        Scrolls type = Scrolls.getFromPDC(scroll);
        if (type == null) return;

        if (scroll.getAmount() > 1) {
            player.sendMessage(Messages.NEED_TO_UNSTACK_ITEM.getMessage());
            return;
        }

        switch (type.getConfigName()) {
            case "BlackScroll" -> {
                if (methods.isInventoryFull(player)) return;

                List<CEnchantment> enchantments = enchantmentBookSettings.getEnchantmentsOnItem(item);
                if (!enchantments.isEmpty()) { // Item has enchantments
                    event.setCancelled(true);
                    player.setItemOnCursor(methods.removeItem(scroll));

                    if (blackScrollChanceToggle && !methods.randomPicker(blackScrollChance, 100)) {
                        player.sendMessage(Messages.BLACK_SCROLL_UNSUCCESSFUL.getMessage());
                        return;
                    }

                    Random random = new Random();

                    CEnchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
                    player.getInventory().addItem(new CEBook(enchantment, enchantmentBookSettings.getLevel(item, enchantment), 1).buildBook());
                    event.setCurrentItem(enchantmentBookSettings.removeEnchantment(item, enchantment));
                }
            }

            case "WhiteScroll" -> {
                if (Scrolls.hasWhiteScrollProtection(item)) return;
                for (EnchantmentType enchantmentType : infoMenuManager.getEnchantmentTypes()) {
                    if (enchantmentType.getEnchantableMaterials().contains(item.getType())) {
                        event.setCancelled(true);
                        event.setCurrentItem(Scrolls.addWhiteScrollProtection(item));
                        player.setItemOnCursor(methods.removeItem(scroll));
                        return;
                    }
                }
            }

            case "TransmogScroll" -> {
                if (!enchantmentBookSettings.getEnchantments(item).isEmpty()) return;
                if (item.lore() == null) return;

                ItemStack orderedItem = newOrderNewEnchantments(item.clone());

                if (item.isSimilar(orderedItem)) return;

                event.setCancelled(true);
                event.setCurrentItem(orderedItem);
                player.setItemOnCursor(methods.removeItem(scroll));
            }
        }
    }

    @EventHandler()
    public void onScrollClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (checkScroll(player.getInventory().getItemInMainHand(), player, event)) return;

        checkScroll(player.getInventory().getItemInOffHand(), player, event);

    }

    private boolean checkScroll(ItemStack scroll, Player player, PlayerInteractEvent event) {
        if (scroll.isEmpty() || !scroll.hasItemMeta()) return false;
        PersistentDataContainer container = scroll.getItemMeta().getPersistentDataContainer();
        if (!container.has(DataKeys.scroll.getNamespacedKey())) return false;

        String data = container.get(DataKeys.scroll.getNamespacedKey(), PersistentDataType.STRING);

        assert data != null;

        if (data.equalsIgnoreCase(Scrolls.BLACK_SCROLL.getConfigName())) {
            event.setCancelled(true);
            player.sendMessage(Messages.RIGHT_CLICK_BLACK_SCROLL.getMessage());
            return true;
        } else if (data.equalsIgnoreCase(Scrolls.WHITE_SCROLL.getConfigName()) || data.equalsIgnoreCase(Scrolls.TRANSMOG_SCROLL.getConfigName())) {
            event.setCancelled(true);
            return true;
        }
        return false;
    }

    @Deprecated
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

        useSuffix(item, newMeta, newEnchantmentOrder);

        item.setItemMeta(newMeta);
        return item;
    }

    private ItemStack newOrderNewEnchantments(ItemStack item) {
        Gson gson = new Gson();

        ItemMeta meta = item.getItemMeta();
        List<Component> lore = item.lore();
        assert meta != null && lore != null;

        PersistentDataContainer container = meta.getPersistentDataContainer();
        Enchant data = gson.fromJson(container.get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING), Enchant.class);
        boolean addSpaces = Files.CONFIG.getFile().getBoolean("Settings.TransmogScroll.Add-Blank-Lines", true);
        List<CEnchantment> newEnchantmentOrder = new ArrayList<>();
        Map<CEnchantment, Integer> enchantments = new HashMap<>();
        List<String> order = Files.CONFIG.getFile().getStringList("Settings.TransmogScroll.Lore-Order");
        if (order.isEmpty()) order = Arrays.asList("CE_Enchantments", "Protection", "Normal_Lore");

        for (CEnchantment enchantment : enchantmentBookSettings.getRegisteredEnchantments()) {
            if (!data.hasEnchantment(enchantment.getName())) continue;
            enchantments.put(enchantment, (enchantment.getCustomName() + " " +
                    NumberUtils.toRoman(data.getLevel(enchantment.getName()))).replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "").length());
            newEnchantmentOrder.add(enchantment);
        }

        orderInts(newEnchantmentOrder, enchantments); // Order Enchantments by length.

        List<Component> enchantLore = newEnchantmentOrder.stream().map(i ->
                ColorUtils.legacyTranslateColourCodes("%s %s".formatted(i.getCustomName(), NumberUtils.toRoman(data.getLevel(i.getName()))))).collect(Collectors.toList());
        List<Component> normalLore = stripNonNormalLore(lore, newEnchantmentOrder);
        List<Component> protectionLore = getAllProtectionLore(container);

        List<Component> newLore = new ArrayList<>();
        boolean wasEmpty = true;

        for (String selection : order) {
            switch (selection) {
                case "CE_Enchantments" -> {
                    if (addSpaces && !wasEmpty && !enchantLore.isEmpty()) newLore.add(Component.text(""));
                    newLore.addAll(enchantLore);
                    wasEmpty = enchantLore.isEmpty();
                }
                case "Protection" -> {
                    if (addSpaces && !wasEmpty && !protectionLore.isEmpty()) newLore.add(Component.text(""));
                    newLore.addAll(protectionLore);
                    wasEmpty = protectionLore.isEmpty();
                }
                case "Normal_Lore" -> {
                    if (addSpaces && !wasEmpty && !normalLore.isEmpty()) newLore.add(Component.text(""));
                    newLore.addAll(normalLore);
                    wasEmpty = normalLore.isEmpty();
                }
            }
        }

        useSuffix(item, meta, newEnchantmentOrder);

        meta.lore(newLore);
        item.setItemMeta(meta);
        return item;
    }

    private List<Component> getAllProtectionLore(PersistentDataContainer container) {
        List<Component> lore = new ArrayList<>();

        if (Scrolls.hasWhiteScrollProtection(container)) lore.add(ColorUtils.legacyTranslateColourCodes(Files.CONFIG.getFile().getString("Settings.WhiteScroll.ProtectedName")));
        if (ProtectionCrystalSettings.isProtected(container)) lore.add(ColorUtils.legacyTranslateColourCodes(Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected")));

        return lore;
    }

    private List<Component> stripNonNormalLore(List<Component> lore, List<CEnchantment> enchantments) {

        // Remove blank lines
        lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).replaceAll(" ", "").isEmpty());

        // Remove CE enchantment lore
        enchantments.forEach(enchant -> lore.removeIf(loreComponent ->
                ColorUtils.toPlainText(loreComponent).contains(enchant.getCustomName().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", ""))
        ));

        // Remove white-scroll protection lore
        lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(Scrolls.getWhiteScrollProtectionName().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "")));

        // Remove Protection-crystal protection lore
        lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(
                FileManager.Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected").replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "")
        ));

        return lore;
    }

    private void useSuffix(ItemStack item, ItemMeta meta, List<CEnchantment> newEnchantmentOrder) {
        if (useSuffix) {
            String newName = meta.hasDisplayName() ? ColorUtils.toLegacy(meta.displayName()) :
                    "&b" + WordUtils.capitalizeFully(item.getType().toString().replace("_", " "));

            if (meta.hasDisplayName()) {
                for (int i = 0; i <= 100; i++) {
                    String suffixWithAmount = suffix.replace("%Amount%", String.valueOf(i)).replace("%amount%", String.valueOf(i));

                    if (!newName.endsWith(suffixWithAmount)) continue;

                    newName = newName.substring(0, newName.length() - suffixWithAmount.length());
                    break;
                }
            }

            String amount = String.valueOf(countVanillaEnchantments ? newEnchantmentOrder.size() + item.getEnchantments().size() : newEnchantmentOrder.size());

            meta.displayName(ColorUtils.legacyTranslateColourCodes(newName + suffix.replace("%Amount%", amount).replace("%amount%", amount)));
        }
    }

    private void orderInts(List<CEnchantment> list, final Map<CEnchantment, Integer> map) {
        list.sort((a1, a2) -> {
            Integer string1 = map.get(a1);
            Integer string2 = map.get(a2);
            return string2.compareTo(string1);
        });
    }
}