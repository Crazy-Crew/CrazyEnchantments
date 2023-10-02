package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
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
import com.badbones69.crazyenchantments.paper.utilities.misc.ColorUtils;
import com.badbones69.crazyenchantments.paper.utilities.misc.EnchantUtils;
import com.badbones69.crazyenchantments.paper.utilities.misc.NumberUtils;
import com.google.gson.Gson;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ScrollListener implements Listener {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = this.plugin.getStarter();

    private final Methods methods = this.starter.getMethods();

    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private final InfoMenuManager infoMenuManager = this.starter.getInfoMenuManager();

    private String suffix;
    private boolean countVanillaEnchantments;
    private boolean useSuffix;
    private boolean blackScrollChanceToggle;
    private int blackScrollChance;

    public void loadScrollControl() {
        FileConfiguration config = Files.CONFIG.getFile();
        this.suffix = config.getString("Settings.TransmogScroll.Amount-of-Enchantments", " &7[&6&n%amount%&7]");
        this.countVanillaEnchantments = config.getBoolean("Settings.TransmogScroll.Count-Vanilla-Enchantments");
        this.useSuffix = config.getBoolean("Settings.TransmogScroll.Amount-Toggle");
        this.blackScrollChance = config.getInt("Settings.BlackScroll.Chance", 75);
        this.blackScrollChanceToggle = config.getBoolean("Settings.BlackScroll.Chance-Toggle");
    }

    @EventHandler(ignoreCancelled = true)
    public void onScrollUse(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        ItemStack scroll = event.getCursor();

        if (item == null || item.getType() == Material.AIR || scroll == null || scroll.getType() == Material.AIR) return;

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
                if (this.methods.isInventoryFull(player)) {
                    player.sendMessage(Messages.INVENTORY_FULL.getMessage());
                    return;
                }

                List<CEnchantment> enchantments = this.enchantmentBookSettings.getEnchantmentsOnItem(item);
                if (!enchantments.isEmpty()) { // Item has enchantments
                    event.setCancelled(true);
                    player.setItemOnCursor(this.methods.removeItem(scroll));

                    if (this.blackScrollChanceToggle && !this.methods.randomPicker(blackScrollChance, 100)) {
                        player.sendMessage(Messages.BLACK_SCROLL_UNSUCCESSFUL.getMessage());
                        return;
                    }

                    Random random = new Random();

                    CEnchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
                    player.getInventory().addItem(new CEBook(enchantment, this.enchantmentBookSettings.getLevel(item, enchantment), 1).buildBook());
                    event.setCurrentItem(this.enchantmentBookSettings.removeEnchantment(item, enchantment));
                }
            }

            case "WhiteScroll" -> {
                if (Scrolls.hasWhiteScrollProtection(item)) return;
                for (EnchantmentType enchantmentType : this.infoMenuManager.getEnchantmentTypes()) {
                    if (enchantmentType.getEnchantmentMaterials().contains(item.getType())) {
                        event.setCancelled(true);
                        event.setCurrentItem(Scrolls.addWhiteScrollProtection(item));
                        player.setItemOnCursor(this.methods.removeItem(scroll));
                        return;
                    }
                }
            }

            case "TransmogScroll" -> {
                if (!this.enchantmentBookSettings.hasEnchantments(item)) return;
                if (item.lore() == null) return;

                ItemStack orderedItem = newOrderNewEnchantments(item.clone());

                if (item.isSimilar(orderedItem)) return;

                event.setCancelled(true);
                event.setCurrentItem(orderedItem);
                player.setItemOnCursor(this.methods.removeItem(scroll));
            }
        }
    }

    @EventHandler()
    public void onScrollClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack scroll = this.methods.getItemInHand(player);

        if (scroll != null) {
            if (scroll.isSimilar(Scrolls.BLACK_SCROLL.getScroll())) {
                event.setCancelled(true);
                player.sendMessage(Messages.RIGHT_CLICK_BLACK_SCROLL.getMessage());
            } else if (scroll.isSimilar(Scrolls.WHITE_SCROLL.getScroll()) || scroll.isSimilar(Scrolls.TRANSMOG_SCROLL.getScroll())) {
                event.setCancelled(true);
            }
        }
    }

    @Deprecated
    private ItemStack orderNewEnchantments(ItemStack item) {
        HashMap<CEnchantment, Integer> enchantmentLevels = new HashMap<>();
        HashMap<CEnchantment, Integer> categories = new HashMap<>();
        List<CEnchantment> newEnchantmentOrder = new ArrayList<>();

        for (Map.Entry<CEnchantment, Integer> enchantment : this.enchantmentBookSettings.getEnchantments(item).entrySet()) {
            enchantmentLevels.put(enchantment.getKey(), enchantment.getValue());
            categories.put(enchantment.getKey(), EnchantUtils.getHighestEnchantmentCategory(enchantment.getKey()).getRarity());
            newEnchantmentOrder.add(enchantment.getKey());
        }

        orderInts(newEnchantmentOrder, categories);
        ItemMeta newMeta = item.getItemMeta();

        List<Component> newLore = new ArrayList<>();

        newEnchantmentOrder.forEach(enchantment -> newLore.add(ColorUtils.legacyTranslateColourCodes(enchantment.getCustomName() + " " + NumberUtils.convertLevelString(enchantmentLevels.get(enchantment)))));

        newMeta.lore(newLore);

        useSuffix(item, newMeta, newEnchantmentOrder);

        item.setItemMeta(newMeta);
        return item;
    }

    private ItemStack newOrderNewEnchantments(ItemStack item) {
        Gson gson = new Gson();

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        Enchant data = gson.fromJson(container.get(DataKeys.ENCHANTMENTS.getKey(), PersistentDataType.STRING), Enchant.class);
        List<Component> normalLore = item.lore();
        List<CEnchantment> newEnchantmentOrder = new ArrayList<>();

        List<Component> newLore = new ArrayList<>();
        List<Component> enchantLore = new ArrayList<>();

        Map<CEnchantment, Integer> enchantments = new HashMap<>();

        for (CEnchantment enchantment : this.enchantmentBookSettings.getRegisteredEnchantments()) {
            if (!data.hasEnchantment(enchantment.getName())) continue;
            enchantments.put(enchantment, (enchantment.getCustomName() + " " + NumberUtils.toRoman(data.getLevel(enchantment.getName()))).replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "").length());
            newEnchantmentOrder.add(enchantment);
        }

        // Order Enchantments by length.
        orderInts(newEnchantmentOrder, enchantments);

        // Remove blank lines
        normalLore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).replaceAll(" ", "").equals(""));

        // Remove CE enchantment lore
        newEnchantmentOrder.forEach(enchant -> normalLore.removeIf(loreComponent ->
                ColorUtils.toPlainText(loreComponent).contains(enchant.getCustomName().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", ""))
        ));

        // Remove white-scroll protection lore
        normalLore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(Scrolls.getWhiteScrollProtectionName().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "")));

        // Remove Protection-crystal protection lore
        normalLore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(
                Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected").replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "")
        ));

        // Convert CE lore to components.
        for (CEnchantment i : newEnchantmentOrder) {
            enchantLore.add(ColorUtils.legacyTranslateColourCodes(i.getCustomName() + " " + NumberUtils.toRoman(data.getLevel(i.getName()))));
        }

        boolean hasWhiteScrollProtection = Scrolls.hasWhiteScrollProtection(container);
        boolean hasProtectionCrystalProtection = this.plugin.getStarter().getProtectionCrystalSettings().isProtected(container);

        List<String> order = Files.CONFIG.getFile().getStringList("Settings.WhiteScroll.Lore-Order");

        if (order.isEmpty()) order = Arrays.asList("CE_Enchantments", "Protection", "Normal_Lore");

        boolean wasEmpty = true;
        for (String selection : order) {

            switch (selection) {
                case "CE_Enchantments" -> {
                    if (!wasEmpty && !enchantLore.isEmpty()) newLore.add(Component.text(""));
                    newLore.addAll(enchantLore);
                    wasEmpty = enchantLore.isEmpty();
                }
                case "Protection" -> {
                    if (!wasEmpty && (hasWhiteScrollProtection || hasProtectionCrystalProtection)) newLore.add(Component.text(""));
                    if (hasWhiteScrollProtection) newLore.add(ColorUtils.legacyTranslateColourCodes(Files.CONFIG.getFile().getString("Settings.WhiteScroll.ProtectedName")));
                    if (hasProtectionCrystalProtection) newLore.add(ColorUtils.legacyTranslateColourCodes(Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected")));
                    wasEmpty = !(hasWhiteScrollProtection || hasProtectionCrystalProtection);
                }
                case "Normal_Lore" -> {
                    if (!wasEmpty && !normalLore.isEmpty()) newLore.add(Component.text(""));
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

    private void useSuffix(ItemStack item, ItemMeta meta, List<CEnchantment> newEnchantmentOrder) {
        if (this.useSuffix) {
            String newName = meta.hasDisplayName() ? ColorUtils.toLegacy(meta.displayName()) :
                    "&b" + WordUtils.capitalizeFully(item.getType().toString().replace("_", " "));

            if (meta.hasDisplayName()) {
                for (int i = 0; i <= 100; i++) {
                    String suffixWithAmount = this.suffix.replace("%Amount%", String.valueOf(i)).replace("%amount%", String.valueOf(i));

                    if (!newName.endsWith(suffixWithAmount)) continue;

                    newName = newName.substring(0, newName.length() - suffixWithAmount.length());
                    break;
                }
            }

            String amount = String.valueOf(this.countVanillaEnchantments ? newEnchantmentOrder.size() + item.getEnchantments().size() : newEnchantmentOrder.size());

            meta.displayName(ColorUtils.legacyTranslateColourCodes(newName + this.suffix.replace("%Amount%", amount).replace("%amount%", amount)));
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