package com.badbones69.crazyenchantments.paper.api.enums;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.api.enums.keys.FileKeys;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Messages {

    CORRECT_USAGE("&cThe correct usage for this command is &e%usage%.", "Correct-Usage"),

    MUST_BE_CONSOLE_SENDER("&cYou must be console sender to run this command.", "Must-Be-Console-Sender"),

    COMMAND_NOT_FOUND("&c%command% is not a known command.", "Command-Not-Found"),

    ITEM_CANNOT_BE_EMPTY("&7The item trying to be fetched using %command% cannot be empty!", "Item-Cannot-Be-Empty"),

    GIVE_SCROLL("&7You have given &6%player% %type% &7scroll!", "Give-Scroll"),
    GET_SCROLL("&7You have been given a &6%type% &7scroll!", "Get-Scroll"),

    GIVE_LOSTBOOK("&7You have given &6%player% %category% &7lostbook!", "Give-LostBook"),
    GET_LOSTBOOK("&7You have been given a &6%category% &7lostbook!", "Get-LostBook"),

    GIVE_BOTTLE("&7You have given &6%player% %amount% &7exp bottle!", "Give-Bottle"),
    GET_BOTTLE("&7You have been given a &6%amount% &7exp bottle!", "Get-Bottle"),

    CONFIG_RELOAD("&7You have reloaded the Config.yml", "Config-Reload"),
    NEED_TO_UNSTACK_ITEM("&cYou need to unstack that item before you can use it.", "Need-To-UnStack-Item"),
    NOT_AN_ENCHANTMENT("&cThat is not an enchantment.", "Not-An-Enchantment"),
    RIGHT_CLICK_BLACK_SCROLL("&7Black scrolls will remove a random enchantment from your item.", "Right-Click-Black-Scroll"),
    BLACK_SCROLL_UNSUCCESSFUL("&cThe black scroll was unsuccessful. Please try again with another one.", "Black-Scroll-Unsuccessful"),
    NEED_MORE_XP_LEVELS("&cYou need &6%XP% &cmore xp level.", "Need-More-XP-Lvls"),
    NEED_MORE_TOTAL_XP("&cYou need &6%XP% &cmore total xp.", "Need-More-Total-XP"),
    NEED_MORE_MONEY("&cYou are in need of &a$%Money_Needed%&c.", "Need-More-Money"),
    HIT_ENCHANTMENT_MAX(StringUtils.toString(List.of(
            "#ff0000That item already has the maximum amount of enchantments that you can add to it.",
            "#880808Use &2/ce limit #880808to check the current limit on the item.",
            "#880808For information on how to change the limit, read &2https://docs.crazycrew.us/docs/plugins/crazyenchantments/faq."
    )), "list", "Hit-Enchantment-Max"),
    CONFLICTING_ENCHANT("&7This enchant conflicts with one already on the item.", "Conflicting-Enchant"),
    MAX_SLOTS_UNLOCKED("&cYou have already added the maximum amount of slots to this item.", "Hit-Slot-Max"),
    APPLIED_SLOT_CRYSTAL("&cYou have successfully added another slot to the item. The item now has %slot% extra slots.", "Applied-Slot-Crystal"),
    INVENTORY_FULL("&cYour inventory is too full. Please open up some space to buy that.", "Inventory-Full"),
    TINKER_INVENTORY_FULL("&cThe inventory is full. Sell all or remove items.", "Tinker-Inventory-Full"),
    NEED_TO_USE_PLAYER_INVENTORY("&cYou can only use that in your player inventory.", "Need-To-Use-Player-Inventory"),
    TINKER_SOLD_MESSAGE("&7Thank you for trading at &7&lThe &4&lCrazy &c&lTinkerer&7.", "Tinker-Sold-Msg"),
    PLAYERS_ONLY("&cOnly players can use this command.", "Players-Only"),
    NO_PERMISSION("&cYou do not have permission to use that command!", "No-Perm"),
    NOT_ONLINE("&cThat player is not online.", "Not-Online"),
    REMOVED_ENCHANTMENT("&7You have removed the enchantment &a%Enchantment% &7from this item.", "Remove-Enchantment"),
    DOESNT_HAVE_ENCHANTMENT("&cYour item does not contain the enchantment &6%Enchantment%&c.", "Doesnt-Have-Enchantment"),
    DOESNT_HAVE_ITEM_IN_HAND("&cYou must have an item in your hand.", "Doesnt-Have-Item-In-Hand"),
    NOT_A_NUMBER("&c%Arg% is not a number.", "Not-A-Number"),
    GET_SUCCESS_DUST("&7You have gained &a%amount% &7Success Dust.", "Get-Success-Dust"),
    GIVE_SUCCESS_DUST("&7You have given &a%amount% &7Success Dust to &6%player%&7.", "Give-Success-Dust"),
    GET_DESTROY_DUST("&7You have gained &a%amount% &7Destroy Dust.", "Get-Destroy-Dust"),
    GIVE_DESTROY_DUST("&7You have given &a%amount% &7Destroy Dust to &6%player%&7.", "Give-Destroy-Dust"),
    GET_MYSTERY_DUST("&7You have gained &a%amount% &7Mystery Dust.", "Get-Mystery-Dust"),
    GIVE_MYSTERY_DUST("&7You have given &a%amount% &7Mystery Dust to &6%player%&7.", "Give-Mystery-Book"),
    NOT_A_CATEGORY("&6%category% &cis not a category.", "Not-A-Category"),
    CLEAN_LOST_BOOK("&7You have cleaned a lost book and found %found%&7.", "Clean-Lost-Book"),
    BOOK_WORKS("&aYour item loved this book and accepted it.", "Book-Works"),
    BOOK_FAILED("&cYour item must not have liked that enchantment.", "Book-Failed"),
    ITEM_DESTROYED("&cOh no the destroy rate was too much for the item.", "Item-Destroyed"),
    ITEM_WAS_PROTECTED("&cLuckily your item was blessed with Divine Protection and did not break.", "Item-Was-Protected"),
    PLAYER_IS_IN_CREATIVE_MODE("&cYou are in creative mode. You need to get out of Creative Mode!", "Player-Is-In-Creative-Mode"),
    GIVE_PROTECTION_CRYSTAL("&7You have given %player% %amount% Protection Crystals.", "Give-Protection-Crystal"),
    GET_PROTECTION_CRYSTAL("&7You have gained %amount% Protection Crystals.", "Get-Protection-Crystal"),
    GIVE_SCRAMBLER_CRYSTAL("&7You have given %player% %amount% &e&lGrand Scramblers&7.", "Give-Scrambler-Crystal"),
    GET_SCRAMBLER("&7You have gained %amount% &e&lGrand Scramblers&7.", "Get-Scrambler-Crystal"),
    GIVE_SLOT_CRYSTAL("&7You have given %player% %amount% Slot Crystals.", "Give-Slot-Crystal"),
    GET_SLOT_CRYSTAL("&7You have gained %amount% Slot Crystals.", "Get-Slot-Crystal"),
    BREAK_ENCHANTMENT_SHOP_SIGN("&cYou have removed a Crazy Enchantment Shop Sign.", "Break-Enchantment-Shop-Sign"),
    SEND_ENCHANTMENT_BOOK("&7You have sent &6%player% &7a Crazy Enchantment Book.", "Send-Enchantment-Book"),
    NOT_A_GKIT("&c%kit% is not a GKit.", "Not-A-GKit"),
    STILL_IN_COOLDOWN("&cYou still have %day%d %hour%h %minute%m %second%s cool-down left on %kit%&c.", "Still-In-Cooldown"),
    GIVEN_GKIT("&7You have given &6%player%&7 a %kit%&7 GKit.", "Given-GKit"),
    RECEIVED_GKIT("&7You have received a %kit%&7 GKit.", "Received-GKit"),
    NO_GKIT_PERMISSION("&cYou do not have permission to use the %kit% GKit.", "No-GKit-Permission"),
    SPAWNED_BOOK("&7You have spawned a book at &6%World%, %X%, %Y%, %Z%&7.", "Spawned-Book"),
    RESET_GKIT("&7You have reset %player%'s %GKit% GKit cool-down.", "Reset-GKit"),
    GKIT_NOT_ENABLED("&cGKitz is currently not enabled.", "Gkitz-Not-Enabled"),
    DISORDERED_ENEMY_HOT_BAR("&7Disordered enemies hot bar.", "Disordered-Enemy-Hot-Bar"),
    ENCHANTMENT_UPGRADE_SUCCESS("&7You have just upgraded &6%Enchantment%&7 to level &6%Level%&7.", "Enchantment-Upgrade", "Success"),
    ENCHANTMENT_UPGRADE_DESTROYED("&cYour upgrade failed and the lower level enchantment was lost.", "Enchantment-Upgrade", "Destroyed"),
    ENCHANTMENT_UPGRADE_FAILED("&cThe book failed to upgrade to the item.", "Enchantment-Upgrade", "Failed"),
    RAGE_BUILDING("&7[&c&lRage&7]: &aKeep it up, your rage is building.", "Rage", "Building"),
    RAGE_COOLED_DOWN("&7[&c&lRage&7]: &cYour Rage has just cooled down.", "Rage", "Cooled-Down"),
    RAGE_RAGE_UP("&7[&c&lRage&7]: &7You are now doing &a%Level%x &7Damage.", "Rage", "Rage-Up"),
    RAGE_DAMAGED("&7[&c&lRage&7]: &cYou have been hurt and it broke your Rage Multiplier!", "Rage", "Damaged"),
    INVALID_ITEM_STRING("&cInvalid item string supplied.", "Invalid-Item-String"),
    MAIN_UPDATE_ENCHANTS("%item% %itemEnchants%", "Show-Enchants-Format", "Main"),
    BASE_UPDATE_ENCHANTS("&2%enchant%&7: &6%level% ", "Show-Enchants-Format", "Base"),
    LIMIT_COMMAND(StringUtils.toString(List.of(
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
            "&0======================================"
    )), "list", "Limit-Command"),
    HELP(StringUtils.toString(List.of(
            "&2&l&nCrazy Enchantments",
            "&b/ce - &9Opens up the menu.",
            "&b/tinker - &9Opens up the Tinkerer menu.",
            "&b/blacksmith - &9Opens up the BlackSmith menu.",
            "&b/gkitz [kit] [player] - &9Open the gkit menu or get a gkit.",
            "&b/gkitz reset <kit> [player] - &9Reset a players gkit cool-down.",
            "&b/ce help - &9Shows all crazy enchantment commands.",
            "&b/ce debug - &9Does a small debug for some errors.",
            "&b/ce info [enchantment] - &9Shows info on all enchantments.",
            "&b/ce reload - &9Reloads all of the configuration FileKeys.",
            "&b/ce remove <enchantment> - &9Removes an enchantment from the item in your hand.",
            "&b/ce add <enchantment> [level] - &9Adds an enchantment to the item in your hand.",
            "&b/ce scroll <black/white/transmog> [amount] [player] - &9Gives a player a scroll item.",
            "&b/ce crystal [amount] [player] - &9Gives a player a Protection Crystal item.",
            "&b/ce scrambler [amount] [player] - &9Gives a player a Scrambler item.",
            "&b/ce dust <success/destroy/mystery> [amount] [player] [percent] - &9Give a player a dust item.",
            "&b/ce book <enchantment> [level/min-max] [amount] [player] - &9Gives a player an enchantment Book.",
            "&b/ce lostbook <category> [amount] [player] - &9Gives a player a lost book item.",
            "&b/ce spawn <enchantment/category> [(level:#/min-max)/world:<world>/x:#/y:#/z:#] - &9Drops an enchantment book at the specific coordinates."
    )), "list", "Help");

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyPlatform platform = this.plugin.getPlatform();

    private final FusionPaper fusion = this.platform.getFusion();

    private final String defaultMessage;
    private final boolean isList;
    private final Object[] path;

    Messages(final String defaultMessage, final String type, final Object... path) {
        this.defaultMessage = defaultMessage;
        this.isList = type.equalsIgnoreCase("list");
        this.path = path;
    }

    Messages(final String defaultMessage, final Object... path) {
        this.defaultMessage = defaultMessage;
        this.isList = false;
        this.path = path;
    }
    
    public static void addMissingMessages() {
        final CommentedConfigurationNode messages = FileKeys.MESSAGES.getConfiguration().node("Messages");

        boolean isSave = false;

        for (Messages message : values()) {
            final Object[] path = message.getPath();

            if (messages.hasChild(path)) {
                continue;
            }

            final String line = message.getDefaultMessage();

            if (message.isList()) {
                Methods.setNode(messages, List.class, line.split("\n"), path);
            } else {
                Methods.setNode(messages, String.class, line, path);
            }

            isSave = true;
        }

        if (isSave) {
            FileKeys.MESSAGES.save();
        }
    }
    
    public String getMessage() {
        return getMessage(true);
    }
    
    public String getMessage(String placeholder, String replacement) {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put(placeholder, replacement);

        return getMessage(placeholders, true);
    }
    
    public String getMessage(Map<String, String> placeholders) {
        return getMessage(placeholders, true);
    }
    
    public String getMessageNoPrefix() {
        return getMessage(false);
    }
    
    public String getMessageNoPrefix(Map<String, String> placeholders) {
        return getMessage(placeholders, false);
    }
    
    private String getMessage(boolean prefix) {
        return getMessage(new HashMap<>(), prefix);
    }
    
    private String getMessage(Map<String, String> placeholders, boolean prefix) {
        String message;

        final CommentedConfigurationNode configuration = FileKeys.MESSAGES.getConfiguration().node("Messages");

        if (this.isList) {
            message = this.fusion.replacePlaceholders(StringUtils.toString(StringUtils.getStringList(configuration.node(this.path), Arrays.asList(this.defaultMessage.split("\n")))), placeholders);
        } else {
            message = this.fusion.replacePlaceholders(configuration.node(this.path).getString(this.defaultMessage), placeholders);
        }

        if (this.isList) {
            return ColorUtils.color(message);
        }

        if (prefix) {
            return ColorUtils.getPrefix(message);
        }

        return ColorUtils.color(message);
    }
    
    private boolean exists() {
        return FileKeys.MESSAGES.getConfiguration().hasChild(this.path);
    }

    private String getDefaultMessage() {
        return this.defaultMessage;
    }
    
    private boolean isList() {
        return this.isList;
    }
    
    private Object[] getPath() {
        return this.path;
    }
}