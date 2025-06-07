package com.badbones69.crazyenchantments.paper.api.enums;

import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public enum Messages {
    
    CONFIG_RELOAD("Config-Reload", "&7You have reloaded the Config.yml"),
    NEED_TO_UNSTACK_ITEM("Need-To-UnStack-Item", "&cYou need to unstack that item before you can use it."),
    NOT_AN_ENCHANTMENT("Not-An-Enchantment", "&cThat is not an enchantment."),
    RIGHT_CLICK_BLACK_SCROLL("Right-Click-Black-Scroll", "&7Black scrolls will remove a random enchantment from your item."),
    BLACK_SCROLL_UNSUCCESSFUL("Black-Scroll-Unsuccessful", "&cThe black scroll was unsuccessful. Please try again with another one."),
    NEED_MORE_XP_LEVELS("Need-More-XP-Lvls", "&cYou need &6%XP% &cmore xp level."),
    NEED_MORE_TOTAL_XP("Need-More-Total-XP", "&cYou need &6%XP% &cmore total xp."),
    NEED_MORE_MONEY("Need-More-Money", "&cYou are in need of &a$%Money_Needed%&c."),
    HIT_ENCHANTMENT_MAX("Hit-Enchantment-Max", Arrays.asList(
            "#ff0000That item already has the maximum amount of enchantments that you can add to it.",
            "#880808Use &2/ce limit #880808to check the current limit on the item.",
            "#880808For information on how to change the limit, read &2https://docs.crazycrew.us/docs/plugins/crazyenchantments/faq."
    )),
    CONFLICTING_ENCHANT("Conflicting-Enchant", "&7This enchant conflicts with one already on the item."),
    MAX_SLOTS_UNLOCKED("Hit-Slot-Max", "&cYou have already added the maximum amount of slots to this item."),
    APPLIED_SLOT_CRYSTAL("Applied-Slot-Crystal", "&cYou have successfully added another slot to the item. The item now has %slot% extra slots."),
    INVENTORY_FULL("Inventory-Full", "&cYour inventory is too full. Please open up some space to buy that."),
    TINKER_INVENTORY_FULL("Tinker-Inventory-Full", "&cThe inventory is full. Sell all or remove items."),
    NEED_TO_USE_PLAYER_INVENTORY("Need-To-Use-Player-Inventory", "&cYou can only use that in your player inventory."),
    TINKER_SOLD_MESSAGE("Tinker-Sold-Msg", "&7Thank you for trading at &7&lThe &4&lCrazy &c&lTinkerer&7."),
    PLAYERS_ONLY("Players-Only", "&cOnly players can use this command."),
    NO_PERMISSION("No-Perm", "&cYou do not have permission to use that command!"),
    NOT_ONLINE("Not-Online", "&cThat player is not online."),
    REMOVED_ENCHANTMENT("Remove-Enchantment", "&7You have removed the enchantment &a%Enchantment% &7from this item."),
    DOESNT_HAVE_ENCHANTMENT("Doesnt-Have-Enchantment", "&cYour item does not contain the enchantment &6%Enchantment%&c."),
    DOESNT_HAVE_ITEM_IN_HAND("Doesnt-Have-Item-In-Hand", "&cYou must have an item in your hand."),
    NOT_A_NUMBER("Not-A-Number", "&c%Arg% is not a number."),
    GET_SUCCESS_DUST("Get-Success-Dust", "&7You have gained &a%amount% &7Success Dust."),
    GIVE_SUCCESS_DUST("Give-Success-Dust", "&7You have given &a%amount% &7Success Dust to &6%player%&7."),
    GET_DESTROY_DUST("Get-Destroy-Dust", "&7You have gained &a%amount% &7Destroy Dust."),
    GIVE_DESTROY_DUST("Give-Destroy-Dust", "&7You have given &a%amount% &7Destroy Dust to &6%player%&7."),
    GET_MYSTERY_DUST("Get-Mystery-Dust", "&7You have gained &a%amount% &7Mystery Dust."),
    GIVE_MYSTERY_DUST("Give-Mystery-Dust", "&7You have given &a%amount% &7Mystery Dust to &6%player%&7."),
    NOT_A_CATEGORY("Not-A-Category", "&6%category% &cis not a category."),
    CLEAN_LOST_BOOK("Clean-Lost-Book", "&7You have cleaned a lost book and found %found%&7."),
    BOOK_WORKS("Book-Works", "&aYour item loved this book and accepted it."),
    BOOK_FAILED("Book-Failed", "&cYour item must not have liked that enchantment."),
    ITEM_DESTROYED("Item-Destroyed", "&cOh no the destroy rate was too much for the item."),
    ITEM_WAS_PROTECTED("Item-Was-Protected", "&cLuckily your item was blessed with Divine Protection and did not break."),
    PLAYER_IS_IN_CREATIVE_MODE("Player-Is-In-Creative-Mode", "&cYou are in creative mode. You need to get out of Creative Mode!"),
    GIVE_PROTECTION_CRYSTAL("Give-Protection-Crystal", "&7You have given %player% %amount% Protection Crystals."),
    GET_PROTECTION_CRYSTAL("Get-Protection-Crystal", "&7You have gained %amount% Protection Crystals."),
    GIVE_SCRAMBLER_CRYSTAL("Give-Scrambler-Crystal", "&7You have given %player% %amount% &e&lGrand Scramblers&7."),
    GET_SCRAMBLER("Get-Scrambler-Crystal", "&7You have gained %amount% &e&lGrand Scramblers&7."),
    GIVE_SLOT_CRYSTAL("Give-Slot-Crystal", "&7You have given %player% %amount% Slot Crystals."),
    GET_SLOT_CRYSTAL("Get-Slot-Crystal", "&7You have gained %amount% Slot Crystals."),
    BREAK_ENCHANTMENT_SHOP_SIGN("Break-Enchantment-Shop-Sign", "&cYou have removed a Crazy Enchantment Shop Sign."),
    SEND_ENCHANTMENT_BOOK("Send-Enchantment-Book", "&7You have sent &6%player% &7a Crazy Enchantment Book."),
    NOT_A_GKIT("Not-A-GKit", "&c%kit% is not a GKit."),
    STILL_IN_COOLDOWN("Still-In-Cooldown", "&cYou still have %day%d %hour%h %minute%m %second%s cool-down left on %kit%&c."),
    GIVEN_GKIT("Given-GKit", "&7You have given &6%player%&7 a %kit%&7 GKit."),
    RECEIVED_GKIT("Received-GKit", "&7You have received a %kit%&7 GKit."),
    NO_GKIT_PERMISSION("No-GKit-Permission", "&cYou do not have permission to use the %kit% GKit."),
    SPAWNED_BOOK("Spawned-Book", "&7You have spawned a book at &6%World%, %X%, %Y%, %Z%&7."),
    RESET_GKIT("Reset-GKit", "&7You have reset %player%'s %GKit% GKit cool-down."),
    GKIT_NOT_ENABLED("GKitz-Not-Enabled", "&cGKitz is currently not enabled."),
    DISORDERED_ENEMY_HOT_BAR("Disordered-Enemy-Hot-Bar", "&7Disordered enemies hot bar."),
    ENCHANTMENT_UPGRADE_SUCCESS("Enchantment-Upgrade.Success", "&7You have just upgraded &6%Enchantment%&7 to level &6%Level%&7."),
    ENCHANTMENT_UPGRADE_DESTROYED("Enchantment-Upgrade.Destroyed", "&cYour upgrade failed and the lower level enchantment was lost."),
    ENCHANTMENT_UPGRADE_FAILED("Enchantment-Upgrade.Failed", "&cThe book failed to upgrade to the item."),
    RAGE_BUILDING("Rage.Building", "&7[&c&lRage&7]: &aKeep it up, your rage is building."),
    RAGE_COOLED_DOWN("Rage.Cooled-Down", "&7[&c&lRage&7]: &cYour Rage has just cooled down."),
    RAGE_RAGE_UP("Rage.Rage-Up", "&7[&c&lRage&7]: &7You are now doing &a%Level%x &7Damage."),
    RAGE_DAMAGED("Rage.Damaged", "&7[&c&lRage&7]: &cYou have been hurt and it broke your Rage Multiplier!"),
    INVALID_ITEM_STRING("Invalid-Item-String", "&cInvalid item string supplied."),
    MAIN_UPDATE_ENCHANTS("Show-Enchants-Format.Main", "%item% %itemEnchants%"),
    BASE_UPDATE_ENCHANTS("Show-Enchants-Format.Base", "&2%enchant%&7: &6%level% "),
    LIMIT_COMMAND("Limit-Command", Arrays.asList(
    "&0======================================",
    "&8[&aCrazyEnchants&8]: &bPersonal Enchantment Limit:",
    " ",
    "&7Bypass Limit: &6%bypass%",
    "&7Vanilla Enchantment Check: &6%vanilla%",
    "&7Max Enchantment Limit: &6%limit%",
    "&7Base Enchantment Limit: &6%baseLimit%",
    "&7Current Items Slot Crystal Limit Adjustment: &6%slotCrystal%",
    "&7Current Enchantment amount on item: &6%item%",
    "&7This item can have &6%canHave% &7enchants.",
    "&7You can add &6%space% &7more enchantments to this item.",
    "&c&cLimit set in config.yml: %limitSetInConfig%",
    "&0======================================")),
    HELP("Help", Arrays.asList(
    "&2&l&nCrazy Enchantments",
    "&b/ce - &9Opens up the menu.",
    "&b/tinker - &9Opens up the Tinkerer menu.",
    "&b/blacksmith - &9Opens up the BlackSmith menu.",
    "&b/gkitz [kit] [player] - &9Open the gkit menu or get a gkit.",
    "&b/gkitz reset <kit> [player] - &9Reset a players gkit cool-down.",
    "&b/ce help - &9Shows all crazy enchantment commands.",
    "&b/ce debug - &9Does a small debug for some errors.",
    "&b/ce info [enchantment] - &9Shows info on all enchantments.",
    "&b/ce reload - &9Reloads all of the configuration files.",
    "&b/ce remove <enchantment> - &9Removes an enchantment from the item in your hand.",
    "&b/ce add <enchantment> [level] - &9Adds an enchantment to the item in your hand.",
    "&b/ce scroll <black/white/transmog> [amount] [player] - &9Gives a player a scroll item.",
    "&b/ce crystal [amount] [player] - &9Gives a player a Protection Crystal item.",
    "&b/ce scrambler [amount] [player] - &9Gives a player a Scrambler item.",
    "&b/ce dust <success/destroy/mystery> [amount] [player] [percent] - &9Give a player a dust item.",
    "&b/ce book <enchantment> [level/min-max] [amount] [player] - &9Gives a player an enchantment Book.",
    "&b/ce lostbook <category> [amount] [player] - &9Gives a player a lost book item.",
    "&b/ce spawn <enchantment/category> [(level:#/min-max)/world:<world>/x:#/y:#/z:#] - &9Drops an enchantment book at the specific coordinates."));

    private final String path;
    private String defaultMessage;
    private List<String> defaultListMessage;
    
    Messages(String path, String defaultMessage) {
        this.path = path;
        this.defaultMessage = defaultMessage;
    }
    
    Messages(String path, List<String> defaultListMessage) {
        this.path = path;
        this.defaultListMessage = defaultListMessage;
    }
    
    public static String convertList(List<String> list) {
        StringBuilder message = new StringBuilder();

        for (String line : list) {
            message.append(ColorUtils.color(line)).append("\n");
        }

        return message.toString();
    }
    
    public static void addMissingMessages() {
        FileConfiguration messages = Files.MESSAGES.getFile();
        boolean saveFile = false;

        for (Messages message : values()) {
            if (!messages.contains("Messages." + message.getPath())) {
                saveFile = true;

                if (message.getDefaultMessage() != null) {
                    messages.set("Messages." + message.getPath(), message.getDefaultMessage());
                } else {
                    messages.set("Messages." + message.getPath(), message.getDefaultListMessage());
                }
            }
        }

        if (saveFile) Files.MESSAGES.saveFile();
    }
    
    public static String replacePlaceholders(String placeholder, String replacement, String message) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);

        return replacePlaceholders(placeholders, message);
    }
    
    public static String replacePlaceholders(HashMap<String, String> placeholders, String message) {
        for (Entry<String, String> placeholder : placeholders.entrySet()) {
            message = message.replaceAll(placeholder.getKey(), placeholder.getValue())
            .replaceAll(placeholder.getKey().toLowerCase(), placeholder.getValue());
        }

        return message;
    }
    
    public static List<String> replacePlaceholders(String placeholder, String replacement, List<String> messageList) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);

        return replacePlaceholders(placeholders, messageList);
    }
    
    public static List<String> replacePlaceholders(HashMap<String, String> placeholders, List<String> messageList) {
        List<String> newMessageList = new ArrayList<>();

        for (String message : messageList) {
            for (Entry<String, String> placeholder : placeholders.entrySet()) {
                newMessageList.add(message.replaceAll(placeholder.getKey(), placeholder.getValue())
                .replaceAll(placeholder.getKey().toLowerCase(), placeholder.getValue()));
            }
        }

        return newMessageList;
    }
    
    public String getMessage() {
        return getMessage(true);
    }
    
    public String getMessage(String placeholder, String replacement) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);
        return getMessage(placeholders, true);
    }
    
    public String getMessage(Map<String, String> placeholders) {
        return getMessage(placeholders, true);
    }
    
    public String getMessageNoPrefix() {
        return getMessage(false);
    }
    
    public String getMessageNoPrefix(String placeholder, String replacement) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);

        return getMessage(placeholders, false);
    }
    
    public String getMessageNoPrefix(Map<String, String> placeholders) {
        return getMessage(placeholders, false);
    }
    
    private String getMessage(boolean prefix) {
        return getMessage(new HashMap<>(), prefix);
    }
    
    private String getMessage(Map<String, String> placeholders, boolean prefix) {
        String message;
        boolean isList = isList();
        boolean exists = exists();

        FileConfiguration config = Files.MESSAGES.getFile();

        if (isList) {
            if (exists) {
                message = ColorUtils.color(convertList(config.getStringList("Messages." + path)));
            } else {
                message = ColorUtils.color(convertList(getDefaultListMessage()));
            }
        } else {
            if (exists) {
                message = ColorUtils.color(config.getString("Messages." + path));
            } else {
                message = ColorUtils.color(getDefaultMessage());
            }
        }

        for (Entry<String, String> placeholder : placeholders.entrySet()) {
            message = message.replaceAll(placeholder.getKey(), placeholder.getValue())
            .replaceAll(placeholder.getKey().toLowerCase(), placeholder.getValue());
        }

        if (isList) { // Don't want to add a prefix to a list of messages.
            return ColorUtils.color(message);
        } else { // If the message isn't a list.
            if (prefix) { // If the message needs a prefix.
                return ColorUtils.getPrefix(message);
            } else { // If the message doesn't need a prefix.
                return ColorUtils.color(message);
            }
        }
    }
    
    private boolean exists() {
        return Files.MESSAGES.getFile().contains("Messages." + path);
    }
    
    private boolean isList() {
        FileConfiguration config = Files.MESSAGES.getFile();

        if (config.contains("Messages." + this.path)) {
            return !config.getStringList("Messages." + this.path).isEmpty();
        } else {
            return this.defaultMessage == null;
        }
    }
    
    private String getPath() {
        return this.path;
    }
    
    private String getDefaultMessage() {
        return this.defaultMessage;
    }
    
    private List<String> getDefaultListMessage() {
        return this.defaultListMessage;
    }
}