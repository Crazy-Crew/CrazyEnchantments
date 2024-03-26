package com.badbones69.crazyenchantments.paper.api.enums;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazyenchantments.ConfigManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.support.PluginSupport;
import com.badbones69.crazyenchantments.platform.impl.Config;
import com.badbones69.crazyenchantments.platform.impl.Locale;
import com.plotsquared.core.command.Load;
import com.ryderbelserion.cluster.utils.AdvUtils;
import com.ryderbelserion.cluster.utils.StringUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public enum Messages {

    CONFIG_RELOAD(Locale.config_reload),
    NEED_TO_UNSTACK_ITEM(Locale.unstack_item),
    NOT_AN_ENCHANTMENT(Locale.not_an_enchantment),
    RIGHT_CLICK_BLACK_SCROLL(Locale.right_click_black_scroll),
    BLACK_SCROLL_UNSUCCESSFUL(Locale.black_scroll_unsuccessful),
    NEED_MORE_XP_LEVELS(Locale.need_more_xp_levels),
    NEED_MORE_TOTAL_XP(Locale.need_total_xp),
    NEED_MORE_MONEY(Locale.need_more_money),
    HIT_ENCHANTMENT_MAX(Locale.hit_enchantment_max, true),
    MAX_SLOTS_UNLOCKED(Locale.hit_slot_max),
    APPLIED_SLOT_CRYSTAL(Locale.applied_slot_crystal),
    INVENTORY_FULL(Locale.inventory_not_empty),
    NEED_TO_USE_PLAYER_INVENTORY(Locale.only_in_inventory),
    TINKER_SOLD_MESSAGE(Locale.tinker_sold_message),
    PLAYERS_ONLY(Locale.players_only),
    NO_PERMISSION(Locale.no_permissions),
    NOT_ONLINE(Locale.not_online),
    REMOVED_ENCHANTMENT(Locale.remove_enchantment),
    DOESNT_HAVE_ENCHANTMENT(Locale.doesnt_have_enchantment),
    DOESNT_HAVE_ITEM_IN_HAND(Locale.item_not_in_hand),
    NOT_A_NUMBER(Locale.not_a_number),
    NOT_A_CATEGORY(Locale.not_a_category),
    CLEAN_LOST_BOOK(Locale.lost_book_cleaned),
    BOOK_WORKS(Locale.book_works),
    BOOK_FAILED(Locale.book_failed),
    ITEM_DESTROYED(Locale.item_destroyed),
    ITEM_WAS_PROTECTED(Locale.item_protected),
    PLAYER_IS_IN_CREATIVE_MODE(Locale.player_in_creative),
    SEND_ENCHANTMENT_BOOK(Locale.sent_enchantment_book),
    NOT_A_GKIT(Locale.not_gkit),
    STILL_IN_COOLDOWN(Locale.cooldown),
    GIVEN_GKIT(Locale.gave_gkit),
    RECEIVED_GKIT(Locale.received_gkit),
    NO_GKIT_PERMISSION(Locale.no_gkit_permission),
    SPAWNED_BOOK(Locale.spawned_book),
    RESET_GKIT(Locale.reset_gkit),
    GKIT_NOT_ENABLED(Locale.gkitz_not_enabled),
    DISORDERED_ENEMY_HOT_BAR(Locale.disordered_hot_bar),
    ENCHANTMENT_UPGRADE_SUCCESS(Locale.upgrade_success),
    ENCHANTMENT_UPGRADE_DESTROYED(Locale.upgrade_destroyed),
    ENCHANTMENT_UPGRADE_FAILED(Locale.upgrade_failed),
    RAGE_BUILDING(Locale.rage_building),
    RAGE_COOLED_DOWN(Locale.rage_cooldown),
    RAGE_RAGE_UP(Locale.rage_up),
    RAGE_DAMAGED(Locale.rage_damaged),
    INVALID_ITEM_STRING(Locale.invalid_item_string),
    LIMIT_COMMAND(Locale.limit_command, true),
    HELP(Locale.help, true);

    private Property<String> property;

    private Property<List<String>> listProperty;

    private String message;

    private boolean isList = false;

    /**
     * Used for strings
     *
     * @param property the property
     */
    Messages(Property<String> property) {
        this.property = property;
    }

    /**
     * Used for string lists
     *
     * @param listProperty the list property
     * @param isList Defines if it's a list or not.
     */
    Messages(Property<List<String>> listProperty, boolean isList) {
        this.listProperty = listProperty;

        this.isList = isList;
    }

    private final @NotNull SettingsManager configuration = ConfigManager.getMessages();

    private boolean isList() {
        return this.isList;
    }

    private @NotNull List<String> getPropertyList(Property<List<String>> properties) {
        return this.configuration.getProperty(properties);
    }

    private @NotNull String getProperty(Property<String> property) {
        return this.configuration.getProperty(property);
    }

    public String getMessage() {
        return getMessage(new HashMap<>(), null);
    }

    public String getMessage(Player player) {
        return getMessage(new HashMap<>(), player);
    }

    public String getMessage(Map<String, String> placeholders) {
        return getMessage(placeholders, null);
    }

    public String getMessage(String placeholder, String replacement, Player player) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);

        return getMessage(placeholders, player);
    }

    public String getMessage(String placeholder, String replacement) {
        return getMessage(placeholder, replacement, null);
    }

    public String getMessage(Map<String, String> placeholders, Player player) {
        // Get the string first.
        String message;

        if (isList()) {
            message = StringUtils.convertList(getPropertyList(this.listProperty));
        } else {
            message = getProperty(this.property);
        }

        if (!placeholders.isEmpty()) {
            for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                message = message.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
            }
        }

        this.message = message;

        return asComponent(player);
    }

    private String asComponent(Player player) {
        String prefix = ConfigManager.getConfig().getProperty(Config.prefix);

        String message = this.message.replaceAll("\\{prefix}", prefix);

        //if (MiscUtils.isPapiActive() && player != null) {
        //    return AdvUtils.parse(PlaceholderAPI.setPlaceholders(player, message));
        //}

        return message;
    }
}