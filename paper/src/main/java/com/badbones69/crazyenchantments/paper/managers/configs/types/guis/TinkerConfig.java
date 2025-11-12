package com.badbones69.crazyenchantments.paper.managers.configs.types.guis;

import com.badbones69.crazyenchantments.paper.managers.configs.types.guis.objects.TinkerEnchantInfo;
import com.badbones69.crazyenchantments.paper.managers.currency.enums.Currency;
import com.badbones69.crazyenchantments.paper.api.utils.ConfigUtils;
import com.badbones69.crazyenchantments.paper.managers.configs.interfaces.IConfig;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazyenchantments.exceptions.CrazyException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TinkerConfig extends IConfig {

    private final Map<String, TinkerEnchantInfo> enchantments = new HashMap<>();

    private List<String> tradeButtonLore;
    private String tradeButtonName;
    private Currency currency;
    private String guiName;
    
    private List<String> bottleLore;
    private String bottleItem;
    private String bottleName;

    public TinkerConfig(@NotNull final YamlConfiguration configuration) {
        Optional.ofNullable(configuration.getConfigurationSection("Settings")).ifPresentOrElse(section -> {
            this.tradeButtonName = section.getString("TradeButton", "<yellow>Click to accept the trade");
            this.tradeButtonLore = ConfigUtils.getStringList(section, List.of(), "TradeButton-Lore");
            this.currency = Currency.getCurrency(section.getString("Currency", "X_LEVEL"));
            this.guiName = section.getString("GUIName", "<gray><bold>The <dark_red><bold>Crazy <red><bold>Tinkerer");

            this.bottleLore = ConfigUtils.getStringList(section, List.of(
                    "<green><bold>Recycled <gold>{total} xp",
                    "<gray><bold>(<gold><bold>!<gray><bold>) <gray>Throw to get XP."
            ), "BottleOptions.Lore");
            this.bottleItem = section.getString("BottleOptions.Item", "EXPERIENCE_BOTTLE");
            this.bottleName = section.getString("BottleOptions.Name", "<green>Recycled XP");
        }, () -> {
            throw new CrazyException("Tinker.yml does not have the configuration section needed!");
        });

        Optional.ofNullable(configuration.getConfigurationSection("Tinker")).ifPresentOrElse(section -> {
            Optional.ofNullable(section.getConfigurationSection("Vanilla-Enchantments")).ifPresentOrElse(vanilla -> {
                for (final String key : vanilla.getKeys(false)) {
                    final String converted = convert(key);

                    if (converted.isEmpty()) {
                        //todo() logging

                        continue;
                    }

                    final String values = vanilla.getString(converted, "");

                    if (values.isEmpty()) {
                        //todo() logging

                        continue;
                    }

                    final String[] splitter = values.replaceAll(" ", "").split(",");

                    int multiplier = splitter.length < 2 ? 0 : Integer.parseInt(splitter[1]);
                    int amount = Integer.parseInt(splitter[0]);

                    this.enchantments.putIfAbsent(converted, new TinkerEnchantInfo(amount, multiplier));
                }
            }, () -> {
                throw new CrazyException("Tinker.yml does not contain the Vanilla-Enchantments section!");
            });

            Optional.ofNullable(section.getConfigurationSection("Crazy-Enchantments")).ifPresentOrElse(enchant -> {
                for (final String key : enchant.getKeys(false)) {
                    final ConfigurationSection enchantSection = enchant.getConfigurationSection(key);

                    if (enchantSection == null) {
                        //todo() logging

                        continue;
                    }

                    final String values = enchantSection.getString("Items", "");

                    if (values.isEmpty()) {
                        //todo() logging

                        continue;
                    }

                    final String[] splitter = values.replaceAll(" ", "").split(",");

                    int multiplier = splitter.length < 2 ? 0 : Integer.parseInt(splitter[1]);
                    int amount = Integer.parseInt(splitter[0]);

                    this.enchantments.putIfAbsent(key, new TinkerEnchantInfo(amount, multiplier));
                }
            }, () -> {
                throw new CrazyException("Tinker.yml does not contain the Crazy-Enchantments section!");
            });
        }, () -> {
            throw new CrazyException("Tinker.yml does not have the configuration section needed!");
        });
    }

    public @NotNull final List<Component> asTradeButtonLoreComponents(@Nullable final Audience audience) {
        return asComponents(audience, this.tradeButtonLore);
    }

    public @NotNull final Component asTradeButtonComponent(@Nullable final Audience audience) {
        return asComponent(audience, this.tradeButtonName);
    }

    public @NotNull final List<Component> asBottleLore(@Nullable final Audience audience) {
        return asComponents(audience, this.bottleLore);
    }

    public @NotNull final Component asBottleName(@Nullable final Audience audience) {
        return asComponent(audience, this.bottleName);
    }

    public @NotNull final Map<String, TinkerEnchantInfo> getEnchantments() {
        return this.enchantments;
    }

    public @NotNull final String getBottleItem() {
        return this.bottleItem;
    }

    public @NotNull final Currency getCurrency() {
        return this.currency;
    }

    public @NotNull final String getGuiName() {
        return this.guiName;
    }

    private @NotNull String convert(@NotNull final String key) {
        if (key.isEmpty()) {
            return "";
        }

        return switch (key.toLowerCase()) {
            case "protection" -> "protection_environmental";
            case "fire_protection" -> "protection_fire";
            case "feather_falling" -> "protection_fall";
            case "blast_protection" -> "protection_explosions";
            case "projectile_protection" -> "protection_projectile";
            case "respiration" -> "oxygen";
            case "aqua_affinity" -> "water_worker";
            case "sharpness" -> "damage_all";
            case "smite" -> "damage_undead";
            case "bane_of_arthropods" -> "damage_arthropods";
            case "looting" -> "loot_bonus_mobs";
            case "sweeping" -> "sweeping_edge";
            case "efficiency" -> "dig_speed";
            case "unbreaking" -> "durability";
            case "fortune" -> "loot_bonus_blocks";
            case "power" -> "arrow_damage";
            case "punch" -> "arrow_knockback";
            case "flame" -> "arrow_fire";
            case "infinity" -> "arrow_infinite";
            case "luck_of_the_sea" -> "luck";

            default -> key;
        };
    }
}