package com.badbones69.crazyenchantments.paper.api.builders.types.tinkerer;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.economy.CurrencyAPI;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.objects.CEBook;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
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

    public static boolean useExperience(Player player, PlayerInteractEvent event, boolean mainHand, CommentedConfigurationNode configuration) {
        PlayerInventory inventory = player.getInventory();

        ItemStack item = mainHand ? inventory.getItemInMainHand() : inventory.getItemInOffHand();

        if (item.isEmpty()) return false;

        final PersistentDataContainerView container = item.getPersistentDataContainer();

        if (!container.has(DataKeys.experience.getNamespacedKey())) return false;

        int amount = Integer.parseInt(container.getOrDefault(DataKeys.experience.getNamespacedKey(), PersistentDataType.STRING, "0"));

        event.setCancelled(true);

        if (mainHand) {
            inventory.setItemInMainHand(methods.removeItem(item));
        } else {
            inventory.setItemInOffHand(methods.removeItem(item));
        }

        final String currency = configuration.node("Settings", "Currency").getString("XP_TOTAL");

        if (Currency.isCurrency(currency)) {
            currencyAPI.giveCurrency(player, Currency.getCurrency(currency), amount);
        }

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

        return true;
    }

    /**
     * @param amount Amount of XP to store.
     * @return XP Bottle with custom amount of xp stored in it.
     */
    public static ItemStack getXPBottle(Audience player, String amount, final @NotNull CommentedConfigurationNode config) {
        final String id = config.node("Settings", "BottleOptions", "Item").getString("EXPERIENCE_BOTTLE");
        final String name = config.node("Settings", "BottleOptions", "Name").getString("");
        final List<String> lore = new ArrayList<>();

        for (String line : StringUtils.getStringList(config.node("Settings", "BottleOptions", "Lore"))) {
            lore.add(line.replace("%Total%", amount).replace("%total%", amount));
        }

        return new ItemBuilder().setMaterial(id).setName(name).setItemModel(
                config.node("Settings", "BottleOptions", "Model", "Namespace").getString(""),
                config.node("Settings", "BottleOptions", "Model", "Key").getString(""))
                .setLore(lore)
                .addKey(DataKeys.experience.getNamespacedKey(), amount)
                .build(player);
    }

    public static int getTotalXP(ItemStack item, final CommentedConfigurationNode config) {
        int total = 0;

        final CommentedConfigurationNode crazySection = config.node("Tinker", "Crazy-Enchantments");
        final CommentedConfigurationNode vanillaSection = config.node("Tinker", "Vanilla-Enchantments");

        Map<CEnchantment, Integer> ceEnchants = starter.getEnchantmentBookSettings().getEnchantments(item);

        if (!ceEnchants.isEmpty()) { // CrazyEnchantments
            for (Map.Entry<CEnchantment, Integer> enchantment : ceEnchants.entrySet()) {
                final String keyName = enchantment.getKey().getName();
                final String[] values = crazySection.node(keyName, "Items").getString("0").replaceAll(" ", "").split(",");

                int baseAmount = Integer.parseInt(values[0]);
                int multiplier = values.length < 2 ? 0 : Integer.parseInt(values[1]);
                int enchantmentLevel = enchantment.getValue();

                total += baseAmount + enchantmentLevel * multiplier;
            }
        }

        //todo() test data component usage here
        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) { // Vanilla Enchantments
            for (Map.Entry<Enchantment, Integer> enchantment : item.getEnchantments().entrySet()) {
                final String keyName = convertToLegacy(enchantment.getKey().getKey().value()).toUpperCase();
                final String[] values = vanillaSection.node(keyName).getString("0").replaceAll(" ", "").split(",");

                int baseAmount = Integer.parseInt(values[0]); // TODO add converter to convert legacy to new enchant names.
                int multiplier = values.length < 2 ? 0 : Integer.parseInt(values[1]);
                int enchantmentLevel = enchantment.getValue();
                total += baseAmount + enchantmentLevel * multiplier;
            }
        }

        return total;
    }

    private static String convertLegacy(String from) { // Stolen from Enchantment class -TDL
        if (from == null) {
            return null;
        }

        return switch (from.toLowerCase()) {
            case "protection_environmental" -> "protection";
            case "protection_fire" -> "fire_protection";
            case "protection_fall" -> "feather_falling";
            case "protection_explosions" -> "blast_protection";
            case "protection_projectile" -> "projectile_protection";
            case "oxygen" -> "respiration";
            case "water_worker" -> "aqua_affinity";
            case "damage_all" -> "sharpness";
            case "damage_undead" -> "smite";
            case "damage_arthropods" -> "bane_of_arthropods";
            case "loot_bonus_mobs" -> "looting";
            case "sweeping_edge" -> "sweeping";
            case "dig_speed" -> "efficiency";
            case "durability" -> "unbreaking";
            case "loot_bonus_blocks" -> "fortune";
            case "arrow_damage" -> "power";
            case "arrow_knockback" -> "punch";
            case "arrow_fire" -> "flame";
            case "arrow_infinite" -> "infinity";
            case "luck" -> "luck_of_the_sea";
            default -> from;
        };

    }

    private static String convertToLegacy(String from) { // Stolen inverse of the above method. -TDL
        if (from == null) {
            return null;
        }

        return switch (from.toLowerCase()) {
            case "protection" -> "protection_environmental";
            case "fire_protection" -> "protection_fire";
            case  "feather_falling" -> "protection_fall";
            case  "blast_protection" -> "protection_explosions";
            case  "projectile_protection" -> "protection_projectile";
            case  "respiration" -> "oxygen";
            case  "aqua_affinity" -> "water_worker";
            case  "sharpness" -> "damage_all";
            case  "smite" -> "damage_undead";
            case  "bane_of_arthropods" -> "damage_arthropods";
            case  "looting" -> "loot_bonus_mobs";
            case  "sweeping" -> "sweeping_edge";
            case  "efficiency" -> "dig_speed";
            case  "unbreaking" -> "durability";
            case  "fortune" -> "loot_bonus_blocks";
            case  "power" -> "arrow_damage";
            case  "punch" -> "arrow_knockback";
            case  "flame" -> "arrow_fire";
            case  "infinity" -> "arrow_infinite";
            case  "luck_of_the_sea" -> "luck";
            default -> from;
        };

    }

    public static int getMaxDustLevelFromBook(CEBook book, CommentedConfigurationNode config) {
        final String name = book.getEnchantment().getName();

        if (!config.hasChild("Tinker", "Crazy-Enchantments", name, "Book")) return 1;

        final CommentedConfigurationNode section = config.node("Tinker", "Crazy-Enchantments", name, "Book");

        String[] values = section.getString("0").replaceAll(" ", "").split(",");
        int baseAmount = Integer.parseInt(values[0]);
        int multiplier = values.length < 2 ? 0 : Integer.parseInt(values[1]);

        return baseAmount + book.getLevel() * multiplier;
    }

    public static Map<Integer, Integer> getSlots() {
        final Map<Integer, Integer> slots = new HashMap<>(23);

        slots.put(1, 5);
        slots.put(2, 6);
        slots.put(3, 7);
        slots.put(9, 14);
        slots.put(10, 15);
        slots.put(11, 16);
        slots.put(12, 17);
        slots.put(18, 23);
        slots.put(19, 24);
        slots.put(20, 25);
        slots.put(21, 26);
        slots.put(27, 32);
        slots.put(28, 33);
        slots.put(29, 34);
        slots.put(30, 35);
        slots.put(36, 41);
        slots.put(37, 42);
        slots.put(38, 43);
        slots.put(39, 44);
        slots.put(45, 50);
        slots.put(46, 51);
        slots.put(47, 52);
        slots.put(48, 53);

        return slots;
    }
}