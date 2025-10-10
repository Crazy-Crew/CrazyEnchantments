package com.badbones69.crazyenchantments.paper.listeners;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.enums.Scrolls;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.enums.v2.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.badbones69.crazyenchantments.paper.managers.ConfigManager;
import com.badbones69.crazyenchantments.paper.controllers.settings.ProtectionCrystalSettings;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.WordUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.stream.Collectors;

public class ScrollListener implements Listener {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyInstance instance = this.plugin.getInstance();

    private final ConfigManager options = this.plugin.getOptions();

    private final Starter starter = this.plugin.getStarter();

    private final Methods methods = this.starter.getMethods();

    @EventHandler(ignoreCancelled = true)
    public void onScrollUse(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        ItemStack scroll = event.getCursor();

        if (item == null || item.isEmpty() || scroll.isEmpty()) return;

        InventoryType.SlotType slotType = event.getSlotType();

        if (slotType != InventoryType.SlotType.ARMOR && slotType != InventoryType.SlotType.CONTAINER && slotType != InventoryType.SlotType.QUICKBAR) return;

        Scrolls type = Scrolls.getFromPDC(scroll);

        if (type == null) return;

        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (scroll.getAmount() > 1) {
            Messages.NEED_TO_UNSTACK_ITEM.sendMessage(player);

            return;
        }

        switch (type.getConfigName()) {
            case "BlackScroll" -> {
                if (this.methods.isInventoryFull(player)) return;

                final List<CEnchantment> enchantments = this.instance.getEnchantmentsOnItem(item);

                if (!enchantments.isEmpty()) { // Item has enchantments
                    event.setCancelled(true);

                    player.setItemOnCursor(this.methods.removeItem(scroll));

                    if (this.options.isBlackScrollChanceToggle() && !this.methods.randomPicker(this.options.getBlackScrollChance(), 100)) {
                        Messages.BLACK_SCROLL_UNSUCCESSFUL.sendMessage(player);

                        return;
                    }

                    Random random = new Random();

                    CEnchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
                    player.getInventory().addItem(new CEBook(enchantment, this.instance.getLevel(item, enchantment), 1).buildBook());

                    event.setCurrentItem(this.instance.removeEnchantment(item, enchantment));
                }
            }

            case "WhiteScroll" -> {
                if (Scrolls.hasWhiteScrollProtection(item)) return;

                for (EnchantmentType enchantmentType : MenuManager.getEnchantmentTypes()) {
                    if (enchantmentType.getEnchantableMaterials().contains(item.getType())) {
                        event.setCancelled(true);

                        event.setCurrentItem(Scrolls.addWhiteScrollProtection(item));

                        player.setItemOnCursor(this.methods.removeItem(scroll));

                        return;
                    }
                }
            }

            case "TransmogScroll" -> {
                if (this.instance.getEnchantmentsOnItem(item).isEmpty()) return;

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

        if (checkScroll(player.getInventory().getItemInMainHand(), player, event)) return;

        checkScroll(player.getInventory().getItemInOffHand(), player, event);
    }

    private boolean checkScroll(@NotNull final ItemStack scroll, @NotNull final Player player, @NotNull final PlayerInteractEvent event) {
        if (scroll.isEmpty()) return false;

        final PersistentDataContainerView container = scroll.getPersistentDataContainer();

        if (!container.has(DataKeys.scroll.getNamespacedKey())) return false;

        final String data = container.get(DataKeys.scroll.getNamespacedKey(), PersistentDataType.STRING);

        if (data == null) return false;

        if (data.equalsIgnoreCase(Scrolls.BLACK_SCROLL.getConfigName())) {
            event.setCancelled(true);

            Messages.RIGHT_CLICK_BLACK_SCROLL.sendMessage(player);

            return true;
        } else if (data.equalsIgnoreCase(Scrolls.WHITE_SCROLL.getConfigName()) || data.equalsIgnoreCase(Scrolls.TRANSMOG_SCROLL.getConfigName())) {
            event.setCancelled(true);

            return true;
        }

        return false;
    }

    private ItemStack newOrderNewEnchantments(@NotNull final ItemStack item) {
        final YamlConfiguration configuration = FileKeys.config.getYamlConfiguration();

        final List<Component> lore = item.lore();

        final PersistentDataContainerView container = item.getPersistentDataContainer();

        final Enchant data = Methods.getGson().fromJson(container.get(DataKeys.enchantments.getNamespacedKey(), PersistentDataType.STRING), Enchant.class);

        final boolean addSpaces = configuration.getBoolean("Settings.TransmogScroll.Add-Blank-Lines", true);

        final List<CEnchantment> newEnchantmentOrder = new ArrayList<>();

        final Map<CEnchantment, Integer> enchantments = new HashMap<>();

        List<String> order = configuration.getStringList("Settings.TransmogScroll.Lore-Order");

        if (order.isEmpty()) order = Arrays.asList("CE_Enchantments", "Protection", "Normal_Lore");

        if (data == null) return item; // Only order if it has CE_Enchants

        for (final CEnchantment enchantment : this.instance.getRegisteredEnchantments()) {
            if (!data.hasEnchantment(enchantment.getName())) continue;

            enchantments.put(enchantment,ColorUtils.stripStringColour((enchantment.getCustomName() + " " + NumberUtils.toRoman(data.getLevel(enchantment.getName())))).length());

            newEnchantmentOrder.add(enchantment);
        }

        orderInts(newEnchantmentOrder, enchantments); // Order Enchantments by length.

        //List<Component> enchantLore = newEnchantmentOrder.stream().map(i ->
                //ColorUtils.legacyTranslateColourCodes("%s %s".formatted(i.getCustomName(), NumberUtils.toRoman(data.getLevel(i.getName()))))).collect(Collectors.toList());

        List<Component> normalLore = stripNonNormalLore(lore == null ? new ArrayList<>() : lore, newEnchantmentOrder);

        List<Component> protectionLore = getAllProtectionLore(container);

        List<Component> newLore = new ArrayList<>();

        boolean wasEmpty = true;

        for (String selection : order) {
            switch (selection) {
                case "CE_Enchantments" -> {
                    //if (addSpaces && !wasEmpty && !enchantLore.isEmpty()) newLore.add(Component.text(""));
                    //newLore.addAll(enchantLore);

                    //wasEmpty = enchantLore.isEmpty();
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

    private List<Component> getAllProtectionLore(@NotNull final PersistentDataContainerView container) {
        final List<Component> lore = new ArrayList<>();

        //if (Scrolls.hasWhiteScrollProtection(container)) lore.add(ColorUtils.legacyTranslateColourCodes(FileKeys.config.getYamlConfiguration().getString("Settings.WhiteScroll.ProtectedName", "&b&lPROTECTED")));
        //if (ProtectionCrystalSettings.isProtected(container)) lore.add(ColorUtils.legacyTranslateColourCodes(FileKeys.config.getYamlConfiguration().getString("Settings.ProtectionCrystal.Protected", "&6Ancient Protection")));

        return lore;
    }

    private List<Component> stripNonNormalLore(@NotNull final List<Component> lore, @NotNull final List<CEnchantment> enchantments) {
        // Remove blank lines
        //lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).replaceAll(" ", "").isEmpty()); //todo() legacy trash

        // Remove CE enchantment lore
        //enchantments.forEach(enchant -> lore.removeIf(loreComponent ->
        //        ColorUtils.toPlainText(loreComponent).contains(ColorUtils.stripStringColour(enchant.getCustomName()))
        //));

        // Remove white-scroll protection lore
        //lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(ColorUtils.stripStringColour(Scrolls.getWhiteScrollProtectionName())));

        // Remove Protection-crystal protection lore
        //lore.removeIf(loreComponent -> ColorUtils.toPlainText(loreComponent).contains(
        //       ColorUtils.stripStringColour(FileKeys.config.getYamlConfiguration().getString("Settings.ProtectionCrystal.Protected", "&6Ancient Protection"))
        //));

        return lore;
    }

    private void useSuffix(@NotNull final ItemStack item, @NotNull final List<CEnchantment> newEnchantmentOrder) {
        if (this.options.isUseSuffix()) {
            final boolean hasName = item.hasData(DataComponentTypes.ITEM_NAME);

            //String newName = hasName ? ColorUtils.toLegacy(item.getData(DataComponentTypes.ITEM_NAME)) :
                    //"&b" + WordUtils.capitalizeFully(item.getType().toString().replace("_", " "));

            if (hasName) {
                for (int i = 0; i <= 100; i++) {
                    String suffixWithAmount = this.options.getSuffix().replace("%Amount%", String.valueOf(i)).replace("%amount%", String.valueOf(i));

                    //if (!newName.endsWith(suffixWithAmount)) continue;

                    //newName = newName.substring(0, newName.length() - suffixWithAmount.length());

                    break;
                }
            }

            String amount = String.valueOf(this.options.isCountVanillaEnchantments() ? newEnchantmentOrder.size() + item.getEnchantments().size() : newEnchantmentOrder.size());

            //item.setData(DataComponentTypes.ITEM_NAME, ColorUtils.legacyTranslateColourCodes(newName + this.options.getSuffix().replace("%Amount%", amount).replace("%amount%", amount)));
        }
    }

    private void orderInts(@NotNull final List<CEnchantment> list, @NotNull final Map<CEnchantment, Integer> map) {
        list.sort((a1, a2) -> {
            Integer string1 = map.get(a1);
            Integer string2 = map.get(a2);

            return string2.compareTo(string1);
        });
    }
}