package com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyenchantments.ConfigManager;
import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.platform.TinkerConfig;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TinkererManager {

    @NotNull
    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private static final Starter starter = plugin.getStarter();

    @NotNull
    private static final Methods methods = starter.getMethods();

    @NotNull
    private static final CurrencyAPI currencyAPI = starter.getCurrencyAPI();

    @NotNull
    private static final SettingsManager tinker = ConfigManager.getTinker();

    public static boolean useExperience(Player player, PlayerInteractEvent event, boolean mainHand) {
        PlayerInventory inventory = player.getInventory();

        ItemStack item = mainHand ? inventory.getItemInMainHand() : inventory.getItemInOffHand();

        if (item.isEmpty() || !item.hasItemMeta()) return false;

        ItemMeta itemMeta = item.getItemMeta();

        if (!itemMeta.getPersistentDataContainer().has(DataKeys.experience.getNamespacedKey())) return false;

        int amount = Integer.parseInt(item.getItemMeta().getPersistentDataContainer().getOrDefault(DataKeys.experience.getNamespacedKey(), PersistentDataType.STRING, "0"));

        event.setCancelled(true);

        if (mainHand) {
            inventory.setItemInMainHand(methods.removeItem(item));
        } else {
            inventory.setItemInOffHand(methods.removeItem(item));
        }

        if (Currency.isCurrency(tinker.getProperty(TinkerConfig.currency))) {
            currencyAPI.giveCurrency(player, Currency.getCurrency(tinker.getProperty(TinkerConfig.currency)), amount);
        }

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        return true;
    }

    /**
     * @param amount Amount of XP to store.
     * @return XP Bottle with custom amount of xp stored in it.
     */
    public static ItemStack getXPBottle(String amount) {
        String id = tinker.getProperty(TinkerConfig.bottle_item);
        String name = tinker.getProperty(TinkerConfig.bottle_name);
        List<String> lore = new ArrayList<>();

        for (String line : tinker.getProperty(TinkerConfig.bottle_lore)) {
            lore.add(line.replace("%Total%", amount).replace("%total%", amount));
        }

        return new ItemBuilder().setMaterial(id).setName(name).setLore(lore).setStringPDC(DataKeys.experience.getNamespacedKey(), amount).build();
    }

    public static int getTotalXP(ItemStack item) {
        int total = 0;

        Map<CEnchantment, Integer> ceEnchants = starter.getEnchantmentBookSettings().getEnchantments(item);

        List<String> values = new ArrayList<>(tinker.getProperty(TinkerConfig.crazyEnchantments));

        if (!ceEnchants.isEmpty()) { // CrazyEnchantments
            for (Map.Entry<CEnchantment, Integer> enchantment : ceEnchants.entrySet()) {
                boolean hasMatch = tinker.getProperty(TinkerConfig.crazyEnchantments).iterator().next().equalsIgnoreCase(enchantment.getKey().getName());

                if (hasMatch) {

                }

                //String[] values = config.getString("Tinker.Crazy-Enchantments." + enchantment.getKey().getName() + ".Items", "0").replaceAll(" ", "").split(",");
                //int baseAmount = Integer.parseInt(values[0]);
                //int multiplier = values.length < 2 ? 0 : Integer.parseInt(values[1]);
                //int enchantmentLevel = enchantment.getValue();

                //total += baseAmount + enchantmentLevel * multiplier;
            }
        }

        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) { // Vanilla Enchantments
            for (Map.Entry<Enchantment, Integer> enchantment : item.getEnchantments().entrySet()) {
                //String[] values = config.getString("Tinker.Vanilla-Enchantments." + enchantment.getKey(), "0").replaceAll(" ", "").split(",");
                //int baseAmount = Integer.parseInt(values[0]);
                //int multiplier = values.length < 2 ? 0 : Integer.parseInt(values[1]);
                //int enchantmentLevel = enchantment.getValue();

                //total += baseAmount + enchantmentLevel * multiplier;
            }
        }

        return total;
    }

    public static int getMaxDustLevelFromBook(CEBook book) {
        //String path = "Tinker.Crazy-Enchantments." + book.getEnchantment().getName() + ".Book";

        //if (!config.contains(path)) return 1;

        //String[] values = config.getString(path, "0").replaceAll(" ", "").split(",");
        //int baseAmount = Integer.parseInt(values[0]);
        //int multiplier = values.length < 2 ? 0 : Integer.parseInt(values[1]);

        //return baseAmount + book.getLevel() * multiplier;
        return 1;
    }

    public static Map<Integer, Integer> getSlots() {
        return new HashMap<>(23) {{
            put(1, 5);
            put(2, 6);
            put(3, 7);
            put(9, 14);
            put(10, 15);
            put(11, 16);
            put(12, 17);
            put(18, 23);
            put(19, 24);
            put(20, 25);
            put(21, 26);
            put(27, 32);
            put(28, 33);
            put(29, 34);
            put(30, 35);
            put(36, 41);
            put(37, 42);
            put(38, 43);
            put(39, 44);
            put(45, 50);
            put(46, 51);
            put(47, 52);
            put(48, 53);
        }};
    }
}