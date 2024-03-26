package com.badbones69.crazyenchantments.platform.impl;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class Locale implements SettingsHolder {

    public static final Property<String> config_reload = newProperty("Messages.Config-Reload", "<gray>You have reloaded the Config.yml");

    public static final Property<String> unstack_item = newProperty("Messages.Need-To-UnStack-Item", "<red>You need to unstack that item before you can use it.");

    public static final Property<String> not_an_enchantment = newProperty("Messages.Not-An-Enchantment", "<red>That is not an enchantment.");

    public static final Property<String> right_click_black_scroll = newProperty("Messages.Right-Click-Black-Scroll", "<gray>Black scrolls will remove a random enchantment from your item.");

    public static final Property<String> black_scroll_unsuccessful = newProperty("Messages.Black-Scroll-Unsuccessful", "<red>The black scroll was unsuccessful. Please try again with another one.");

    public static final Property<String> need_more_xp_levels = newProperty("Messages.Need-More-XP-Lvls", "<red>You need <gold>%xp% <red>more xp levels.");

    public static final Property<String> need_total_xp = newProperty("Messages.Need-More-Total-XP", "<red>You need <gold>%xp% <red>more Total XP.");

    public static final Property<String> need_more_money = newProperty("Messages.Need-More-Money", "<red>You need <green>$%money_needed%<red>.");

    public static final Property<List<String>> hit_enchantment_max = newListProperty("Messages.Hit-Enchantment-Max", List.of(
            "<red>That item already has the maximum amount of enchantments that you can add to it.",
            "<red>if you believe that this is incorrect, contact the server owner and ask them to add the permissions",
            "<white>crazyenchantments.limit.# <red>and <white>crazyenchantments.base-limit.# <red>to you, using their preferred permission plugin. (Replacing the # with the amount)"
    ));

    public static final Property<String> hit_slot_max = newProperty("Messages.Hit-Slot-Max", "<red>You have already added the maximum amount of slots to this item.");

    public static final Property<String> applied_slot_crystal = newProperty("Messages.Applied-Slot-Crystal", "<red>You have successfully added another slot to the item. The item now has %slot% extra slots.");

    public static final Property<String> inventory_not_empty = newProperty("Messages.Inventory-Full", "<red>Your inventory is too full. Please open up some space to buy that.");

    public static final Property<String> only_in_inventory = newProperty("Messages.Need-To-Use-Player-Inventory", "<red>You can only use that in your player inventory.");

    public static final Property<String> tinker_sold_message = newProperty("Messages.Tinker-Sold-Msg", "<gray>Thank you for trading at <bold><gray>The <dark_red>Crazy <red>Tinkerer.</bold>");

    public static final Property<String> players_only = newProperty("Messages.Players-Only", "<red>Only players can use this command.");

    public static final Property<String> no_permissions = newProperty("Messages.No-Perm", "<red>You do not have permission to use that command!");

    public static final Property<String> not_online = newProperty("Messages.Not-Online", "<red>That player is not online.");

    public static final Property<String> remove_enchantment = newProperty("Messages.Remove-Enchantment", "<gray>You have removed the enchantment <green>%enchantment% <gray>from this item.");

    public static final Property<String> doesnt_have_enchantment = newProperty("Messages.Doesnt-Have-Enchantment", "<red>Your item does not contain the enchantment <gold>%enchantment%<red>.");

    public static final Property<String> item_not_in_hand = newProperty("Messages.Doesnt-Have-Item-In-Hand", "<red>You must have an item in your hand.");

    public static final Property<String> not_a_number = newProperty("Messages.Not-A-Number", "<red>%Arg% is not a number.");

    public static final Property<String> received_dust = newProperty("Messages.Received-Dust", "<gray>You have received %amount% of %dust%");

    public static final Property<String> gave_dust = newProperty("Messages.Gave-Dust", "<gray>You have given %player% %amount% of %dust%");

    public static final Property<String> not_a_category = newProperty("Messages.Not-A-Category", "<gold>%category% <red>is not a category.");

    public static final Property<String> lost_book_cleaned = newProperty("Messages.Clean-Loot-Book", "<gray>You have cleaned a lost book and found %found%<gray>.");

    public static final Property<String> book_works = newProperty("Messages.Book-Works", "<green>Your item loved this book and accepted it.");

    public static final Property<String> book_failed = newProperty("Messages.Book-Failed", "<red>Your item must not have liked that enchantment.");

    public static final Property<String> item_destroyed = newProperty("Messages.Item-Destroyed", "<red>Oh no the destroy rate was too much for the item.");

    public static final Property<String> item_protected = newProperty("Messages.Item-Was-Protected", "<red>Luckily your item was blessed with Divine Protection and did not break.");

    public static final Property<String> player_in_creative = newProperty("Messages.Player-Is-In-Creative-Mode", "<red>You are in creative mode. You need to get out of Creative Mode!");

    public static final Property<String> received_crystal = newProperty("Messages.Received-Crystal", "<gray>You have received %amount% of %crystal%");

    public static final Property<String> gave_crystal = newProperty("Messages.Gave-Crystal", "<gray>You have given %player% %amount% of %crystal%");

    public static final Property<String> sent_enchantment_book = newProperty("Messages.Send-Enchantment-Book", "<gray>You have sent <gold>%player% <gray>a Crazy Enchantment Book.");

    public static final Property<String> not_gkit = newProperty("Messages.Not-A-Gkit", "<red>%kit% is not a GKit.");

    public static final Property<String> cooldown = newProperty("Messages.Still-In-Cooldown", "<red>You still have %day%d %hour%h %minute%m %second%s cool-down left on %kit%<red>.");

    public static final Property<String> gave_gkit = newProperty("Messages.Given-Gkit", "<gray>You have given <gold>%player%<gray> a %kit%<gray> GKit.");

    public static final Property<String> received_gkit = newProperty("Messages.Received-Gkit", "<gray>You have received a %kit%<gray> GKit.");

    public static final Property<String> no_gkit_permission = newProperty("Messages.No-Gkit-Permission", "<red>You do not have permission to use the %kit% GKit.");

    public static final Property<String> spawned_book = newProperty("Messages.Spawned-Book", "<gray>You have spawned a book at <gold>%world%, %x%, %y%, %z%<gray>.");

    public static final Property<String> reset_gkit = newProperty("Messages.Reset-Gkit", "<gray>You have reset %player%''s %gkit% GKit cool-down.");

    public static final Property<String> gkitz_not_enabled = newProperty("Messages.Gkitz-Not-Enabled", "<red>GKitz is currently not enabled.");

    public static final Property<String> disordered_hot_bar = newProperty("Messages.Disordered-Enemy-Hot-Bar", "<gray>Disordered enemies hot bar.");

    public static final Property<String> invalid_item_string = newProperty("Messages.Invalid-Item-String", "<red>Invalid item string supplied.");

    public static final Property<String> upgrade_success = newProperty("Messages.Enchantment-Upgrade.Success", "<gray>You have upgraded <gold>%enchantment%<gray> to level <gold>%level%<gray>.");

    public static final Property<String> upgrade_destroyed = newProperty("Messages.Enchantment-Upgrade.Destroyed", "<red>Your upgrade failed and the lower level enchantment was lost.");

    public static final Property<String> upgrade_failed = newProperty("Messages.Enchantment-Upgrade.Failed", "<red>The book failed to upgrade to the item.");

    public static final Property<String> rage_building = newProperty("Messages.Rage.Building", "<gray>[<red><bold>Rage</bold><gray>]: <green>Keep it up, your rage is building.");
    public static final Property<String> rage_cooldown = newProperty("Messages.Rage.Cooled-Down", "<gray>[<red><bold>Rage</bold><gray>]: <red>Your Rage has cooled down.");
    public static final Property<String> rage_up = newProperty("Messages.Rage.Rage-Up", "<gray>[<red><bold>Rage</bold><gray>]: <gray>You are now doing <green>%level%x <gray>Damage.");
    public static final Property<String> rage_damaged = newProperty("Messages.Rage.Damaged", "<gray>[<red><bold>Rage</bold><gray>]: <red>You have been hurt and it broke your Rage Multiplier!");

    public static final Property<List<String>> limit_command = newListProperty("Messages.Limit-Command", List.of(
            "<black>======================================",
            "<dark_gray>[<green>CrazyEnchants<dark_gray>]: <blue>Personal Enchantment Limit:",
            "",
            "<gray>Bypass Limit: <gold>%bypass%",
            "<gray>Vanilla Enchantment Check: <gold>%vanilla%",
            "<gray>Max Enchantment Limit: <gold>%limit%",
            "<gray>Base Enchantment Limit: <gold>%baseLimit%",
            "<gray>Current Items Slot Crystal Limit Adjustment: <gold>%slotCrystal%",
            "<gray>Current Enchantment amount on item: <gold>%item%",
            "<gray>You can add <gold>%space% <gray>more enchantments to this item.",
            "<black>======================================"
    ));

    public static final Property<List<String>> help = newListProperty("Messages.Help", List.of(
            "<bold><green>Crazy Enchantments</bold>",
            "<blue>/ce - <dark_blue>Opens up the menu.",
            "<blue>/tinker - <dark_blue>Opens up the Tinkerer menu.",
            "<blue>/blacksmith - <dark_blue>Opens up the BlackSmith menu.",
            "<blue>/gkitz [kit] [player] - <dark_blue>Open the gkit menu or get a gkit.",
            "<blue>/gkitz reset <kit> [player] - <dark_blue>Reset a players gkit cool-down.",
            "<blue>/ce help - <dark_blue>Shows all crazy enchantment commands.",
            "<blue>/ce debug - <dark_blue>Does a small debug for some errors.",
            "<blue>/ce info [enchantment] - <dark_blue>Shows info on all enchantments.",
            "<blue>/ce reload - <dark_blue>Reloads all of the configuration files.",
            "<blue>/ce remove <enchantment> - <dark_blue>Removes an enchantment from the item in your hand.",
            "<blue>/ce add <enchantment> [level] - <dark_blue>Adds an enchantment to the item in your hand.",
            "<blue>/ce scroll <black/white/transmog> [amount] [player] - <dark_blue>Gives a player a scroll item.",
            "<blue>/ce crystal [amount] [player] - <dark_blue>Gives a player a Protection Crystal item.",
            "<blue>/ce scrambler [amount] [player] - <dark_blue>Gives a player a Scrambler item.",
            "<blue>/ce dust <success/destroy/mystery> [amount] [player] [percent] - <dark_blue>Give a player a dust item.",
            "<blue>/ce book <enchantment> [level/min-max] [amount] [player] - <dark_blue>Gives a player an enchantment Book.",
            "<blue>/ce lostbook <category> [amount] [player] - <dark_blue>Gives a player a lost book item.",
            "<blue>/ce spawn <enchantment/category> [(level:#/min-max)/world:<world>/x:#/y:#/z:#] - <dark_blue>Drops an enchantment book at the specific coordinates."
    ));

}