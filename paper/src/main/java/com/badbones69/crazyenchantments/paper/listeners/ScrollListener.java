package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.CrazyItems;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.ryderbelserion.common.api.enums.messages.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.ryderbelserion.common.api.enums.Files;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import com.ryderbelserion.common.utils.RandomUtils;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.apache.commons.text.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ScrollListener implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyPlatform platform = this.plugin.getPlatform();

    private final CrazyItems items = this.platform.getCrazyItems();

    private final EnchantmentBookSettings bookSettings = this.platform.getBookSettings();

    @EventHandler(ignoreCancelled = true)
    public void onScrollUse(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        final ItemStack item = event.getCurrentItem();
        final ItemStack scroll = event.getCursor();

        if (item == null || item.getType().isAir() || scroll.getType().isAir()) return;

        final InventoryType.SlotType slotType = event.getSlotType();

        if (slotType != InventoryType.SlotType.ARMOR && slotType != InventoryType.SlotType.CONTAINER && slotType != InventoryType.SlotType.QUICKBAR) return;

        final Scrolls type = Scrolls.getFromPDC(scroll);
        if (type == null) return;

        if (scroll.getAmount() > 1) {
            Messages.need_to_unstack_item.sendMessage(player);

            return;
        }

        switch (type.getConfigName()) {
            case "BlackScroll" -> {
                if (Methods.isInventoryFull(player)) return;

                final List<CEnchantment> enchantments = this.bookSettings.getEnchantmentsOnItem(item);
                if (!enchantments.isEmpty()) { // Item has enchantments
                    event.setCancelled(true);
                    player.setItemOnCursor(Methods.removeItem(scroll));

                    if (this.items.isBlackScrollChanceToggle() && !RandomUtils.isChanceLess(this.items.getBlackScrollChance(), 100)) {
                        Messages.black_scroll_unsuccessful.sendMessage(player);

                        return;
                    }

                    final CEnchantment enchantment = enchantments.get(ThreadLocalRandom.current().nextInt(enchantments.size()));

                    player.getInventory().addItem(new CEBook(enchantment, this.bookSettings.getLevel(item, enchantment), 1).buildBook(player));
                    event.setCurrentItem(this.bookSettings.removeEnchantment(item, enchantment));
                }
            }

            case "WhiteScroll" -> {
                if (Scrolls.hasWhiteScrollProtection(item)) return;

                for (EnchantmentType enchantmentType : MenuManager.getEnchantmentTypes()) {
                    if (enchantmentType.getEnchantableMaterials().contains(item.getType())) {
                        event.setCancelled(true);

                        event.setCurrentItem(Scrolls.addWhiteScrollProtection(item));

                        player.setItemOnCursor(Methods.removeItem(scroll));

                        return;
                    }
                }
            }

            case "TransmogScroll" -> {
                if (this.bookSettings.getEnchantments(item).isEmpty()) return;
                if (item.lore() == null) return;

                final ItemStack orderedItem = newOrderNewEnchantments(item.clone());

                if (item.isSimilar(orderedItem)) return;

                event.setCancelled(true);

                event.setCurrentItem(orderedItem);

                player.setItemOnCursor(Methods.removeItem(scroll));
            }
        }
    }

    @EventHandler()
    public void onScrollClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final PlayerInventory inventory = player.getInventory();

        if (checkScroll(inventory.getItemInMainHand(), player, event)) return;

        checkScroll(inventory.getItemInOffHand(), player, event);
    }

    private boolean checkScroll(final ItemStack scroll, final Player player, final PlayerInteractEvent event) {
        if (scroll.isEmpty()) return false;

        final PersistentDataContainerView container = scroll.getPersistentDataContainer();

        if (!container.has(DataKeys.scroll.getNamespacedKey())) return false;

        final String data = container.get(DataKeys.scroll.getNamespacedKey(), PersistentDataType.STRING);

        if (data == null) return false;

        if (data.equalsIgnoreCase(Scrolls.BLACK_SCROLL.getConfigName())) {
            event.setCancelled(true);

            Messages.right_click_black_scroll.sendMessage(player);

            return true;
        } else if (data.equalsIgnoreCase(Scrolls.WHITE_SCROLL.getConfigName()) || data.equalsIgnoreCase(Scrolls.TRANSMOG_SCROLL.getConfigName())) {
            event.setCancelled(true);

            return true;
        }

        return false;
    }

    private ItemStack newOrderNewEnchantments(ItemStack item) {
        final CommentedConfigurationNode configuration = Files.CONFIG.getConfiguration();

        final List<Component> lore = item.lore();

        final PersistentDataContainerView container = item.getPersistentDataContainer();

        final Enchant data = Methods.getGson().fromJson(container.get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING), Enchant.class);

        final boolean addSpaces = configuration.node("Settings", "TransmogScroll", "Add-Blank-Lines").getBoolean(true);

        final List<CEnchantment> newEnchantmentOrder = new ArrayList<>();

        final Map<CEnchantment, Integer> enchantments = new HashMap<>();

        List<String> order = StringUtils.getStringList(configuration.node("Settings", "TransmogScroll", "Lore-Order"), List.of("CE_Enchantments", "Protection", "Normal_Lore"));

        if (order.isEmpty()) order = Arrays.asList("CE_Enchantments", "Protection", "Normal_Lore");

        if (data == null) return item; // Only order if it has CE_Enchants

        for (final CEnchantment enchantment : this.bookSettings.getRegisteredEnchantments()) {
            if (!data.hasEnchantment(enchantment.getName())) continue;

            enchantments.put(enchantment,ColorUtils.stripStringColour((enchantment.getCustomName() + " " + NumberUtils.toRoman(data.getLevel(enchantment.getName())))).length());

            newEnchantmentOrder.add(enchantment);
        }

        orderInts(newEnchantmentOrder, enchantments); // Order Enchantments by length.

        List<Component> enchantLore = newEnchantmentOrder.stream().map(i ->
                ColorUtils.legacyTranslateColourCodes("%s %s".formatted(i.getCustomName(), NumberUtils.toRoman(data.getLevel(i.getName()))))).collect(Collectors.toList());

        List<Component> normalLore = stripNonNormalLore(lore == null ? new ArrayList<>() : lore, newEnchantmentOrder);

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

        useSuffix(item, newEnchantmentOrder);

        item.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(newLore).build());

        return item;
    }

    private List<Component> getAllProtectionLore(@NotNull PersistentDataContainerView container) {
        List<Component> lore = new ArrayList<>();

        final CommentedConfigurationNode configuration = Files.CONFIG.getConfiguration();

        if (Scrolls.hasWhiteScrollProtection(container)) {
            lore.add(ColorUtils.legacyTranslateColourCodes(configuration.node("Settings", "WhiteScroll", "ProtectedName").getString("&b&lPROTECTED")));
        }

        if (ProtectionCrystalSettings.isProtected(container)) {
            lore.add(ColorUtils.legacyTranslateColourCodes(configuration.node("Settings", "ProtectionCrystal", "Protected").getString("&6Ancient Protection")));
        }

        return lore;
    }

    private List<Component> stripNonNormalLore(List<Component> lore, List<CEnchantment> enchantments) {
        // Remove blank lines
        lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).replaceAll(" ", "").isEmpty());

        // Remove CE enchantment lore
        enchantments.forEach(enchant -> lore.removeIf(loreComponent ->
                ColorUtils.toPlainText(loreComponent).contains(ColorUtils.stripStringColour(enchant.getCustomName()))
        ));

        // Remove white-scroll protection lore
        lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(ColorUtils.stripStringColour(Scrolls.getWhiteScrollProtectionName())));

        // Remove Protection-crystal protection lore
        lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(
                ColorUtils.stripStringColour(Files.CONFIG.getConfiguration().node("Settings", "ProtectionCrystal", "Protected").getString("&6Ancient Protection"))
        ));

        return lore;
    }

    private void useSuffix(final ItemStack item, final List<CEnchantment> newEnchantmentOrder) {
        if (this.items.isUseSuffix()) {
            final boolean hasName = item.hasData(DataComponentTypes.ITEM_NAME);

            String newName = hasName ? ColorUtils.toLegacy(item.getData(DataComponentTypes.ITEM_NAME)) :
                    "&b" + WordUtils.capitalizeFully(item.getType().toString().replace("_", " "));

            if (hasName) {
                for (int i = 0; i <= 100; i++) {
                    String suffixWithAmount = this.items.getSuffix().replace("%Amount%", String.valueOf(i)).replace("%amount%", String.valueOf(i));

                    if (!newName.endsWith(suffixWithAmount)) continue;

                    newName = newName.substring(0, newName.length() - suffixWithAmount.length());

                    break;
                }
            }

            String amount = String.valueOf(this.items.isCountVanillaEnchantments() ? newEnchantmentOrder.size() + item.getEnchantments().size() : newEnchantmentOrder.size());

            item.setData(DataComponentTypes.ITEM_NAME, ColorUtils.legacyTranslateColourCodes(newName + this.items.getSuffix().replace("%Amount%", amount).replace("%amount%", amount)));
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