package com.badbones69.crazyenchantments.platform.impl;

import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import java.util.List;

import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class Locale implements SettingsHolder {

    public static final Property<String> config_reload = newProperty("Messages.Config-Reload", "&7You have reloaded the Config.yml");

    public static final Property<String> unstack_item = newProperty("Messages.Need-To-UnStack-Item", "&cYou need to unstack that item before you can use it.");

    public static final Property<String> not_an_enchantment = newProperty("Messages.Not-An-Enchantment", "&cThat is not an enchantment.");

    public static final Property<String> right_click_black_scroll = newProperty("Messages.Right-Click-Black-Scroll", "&7Black scrolls will remove a random enchantment from your item.");

    public static final Property<String> black_scroll_unsuccessful = newProperty("Messages.Black-Scroll-Unsuccessful", "&cThe black scroll was unsuccessful. Please try again with another one.");

    public static final Property<String> need_more_xp_levels = newProperty("Messages.Need-More-XP-Lvls", "&cYou need &6%xp% &cmore xp levels.");

    public static final Property<String> need_total_xp = newProperty("Messages.Need-More-Total-XP", "&cYou need &6%xp% &cmore Total XP.");

    public static final Property<String> need_more_money = newProperty("Messages.Need-More-Money", "&cYou need &a$%money_needed%&c.");

    public static final Property<List<String>> hit_enchantment_max = newListProperty("Messages.Hit-Enchantment-Max", List.of(
            "&cThat item already has the maximum amount of enchantments that you can add to it.",
            "&cif you believe that this is incorrect, contact the server owner and ask them to add the permissions",
            "&fcrazyenchantments.limit.# &cand &fcrazyenchantments.base-limit.# &cto you, using their preferred permission plugin. (Replacing the # with the amount)"
    ));

    public static final Property<String> hit_slot_max = newProperty("Messages.Hit-Slot-Max", "&cYou have already added the maximum amount of slots to this item.");

    public static final Property<String> applied_slot_crystal = newProperty("Messages.Applied-Slot-Crystal", "&cYou have successfully added another slot to the item. The item now has %slot% extra slots.");

    public static final Property<String> inventory_not_empty = newProperty("Messages.Inventory-Full", "&cYour inventory is too full. Please open up some space to buy that.");

    public static final Property<String> only_in_inventory = newProperty("Messages.Need-To-Use-Player-Inventory", "&cYou can only use that in your player inventory.");

    public static final Property<String> tinker_sold_message = newProperty("Messages.Tinker-Sold-Msg", "&7Thank you for trading at &7&lThe &4&lCrazy &c&lTinkerer&7.");

    public static final Property<String> players_only = newProperty("Messages.Players-Only", "&cOnly players can use this command.");

    public static final Property<String> no_permissions = newProperty("Messages.No-Perm", "&cYou do not have permission to use that command!");

    public static final Property<String> not_online = newProperty("Messages.Not-Online", "&cThat player is not online.");

    public static final Property<String> remove_enchantment = newProperty("Messages.Remove-Enchantment", "&7You have removed the enchantment &a%enchantment% &7from this item.");

    public static final Property<String> doesnt_have_enchantment = newProperty("Messages.Doesnt-Have-Enchantment", "&cYour item does not contain the enchantment &6%enchantment%&c.");

    public static final Property<String> item_not_in_hand = newProperty("Messages.Doesnt-Have-Item-In-Hand", "&cYou must have an item in your hand.");

    public static final Property<String> not_a_number = newProperty("Messages.Not-A-Number", "&c%Arg% is not a number.");

    public static final Property<String> received_dust = newProperty("Messages.Received-Dust", "&7You have received %amount% of %dust%");

    public static final Property<String> gave_dust = newProperty("Messages.Gave-Dust", "&7You have given %player% %amount% of %dust%");

    public static final Property<String> not_a_category = newProperty("Messages.Not-A-Category", "&6%category% &cis not a category.");

    public static final Property<String> lost_book_cleaned = newProperty("Messages.Clean-Loot-Book", "&7You have cleaned a lost book and found %found%&7.");

    public static final Property<String> book_works = newProperty("Messages.Book-Works", "&aYour item loved this book and accepted it.");

    public static final Property<String> book_failed = newProperty("Messages.Book-Failed", "&cYour item must not have liked that enchantment.");

    public static final Property<String> item_destroyed = newProperty("Messages.Item-Destroyed", "&cOh no the destroy rate was too much for the item.");

    public static final Property<String> item_protected = newProperty("Messages.Item-Was-Protected", "&cLuckily your item was blessed with Divine Protection and did not break.");

    public static final Property<String> player_in_creative = newProperty("Messages.Player-Is-In-Creative-Mode", "&cYou are in creative mode. You need to get out of Creative Mode!");

    public static final Property<String> received_crystal = newProperty("Messages.Received-Crystal", "&7You have received %amount% of %crystal%");

    public static final Property<String> gave_crystal = newProperty("Messages.Gave-Crystal", "&7You have given %player% %amount% of %crystal%");

    public static final Property<String> sent_enchantment_book = newProperty("Messages.Send-Enchantment-Book", "&7You have sent &6%player% &7a Crazy Enchantment Book.");

    public static final Property<String> not_gkit = newProperty("Messages.Not-A-Gkit", "&c%kit% is not a GKit.");

    public static final Property<String> cooldown = newProperty("Messages.Still-In-Cooldown", "&cYou still have %day%d %hour%h %minute%m %second%s cool-down left on %kit%&c.");

    public static final Property<String> gave_gkit = newProperty("Messages.Given-Gkit", "&7You have given &6%player%&7 a %kit%&7 GKit.");

    public static final Property<String> received_gkit = newProperty("Messages.Received-Gkit", "&7You have received a %kit%&7 GKit.");

    public static final Property<String> no_gkit_permission = newProperty("Messages.No-Gkit-Permission", "&cYou do not have permission to use the %kit% GKit.");

    public static final Property<String> spawned_book = newProperty("Messages.Spawned-Book", "&7You have spawned a book at &6%world%, %x%, %y%, %z%&7.");

    public static final Property<String> reset_gkit = newProperty("Messages.Reset-Gkit", "&7You have reset %player%''s %gkit% GKit cool-down.");

    public static final Property<String> gkitz_not_enabled = newProperty("Messages.Gkitz-Not-Enabled", "&cGKitz is currently not enabled.");

    public static final Property<String> disordered_hot_bar = newProperty("Messages.Disordered-Enemy-Hot-Bar", "&7Disordered enemies hot bar.");

    public static final Property<String> invalid_item_string = newProperty("Messages.Invalid-Item-String", "&cInvalid item string supplied.");

    public static final Property<String> upgrade_success = newProperty("Messages.Enchantment-Upgrade.Success", "&7You have upgraded &6%enchantment%&7 to level &6%level%&7.");

    public static final Property<String> upgrade_destroyed = newProperty("Messages.Enchantment-Upgrade.Destroyed", "&cYour upgrade failed and the lower level enchantment was lost.");

    public static final Property<String> upgrade_failed = newProperty("Messages.Enchantment-Upgrade.Failed", "&cThe book failed to upgrade to the item.");

    public static final Property<String> rage_building = newProperty("Messages.Rage.Building", "&7[&c&lRage&7]: &aKeep it up, your rage is building.");
    public static final Property<String> rage_cooldown = newProperty("Messages.Rage.Cooled-Down", "&7[&c&lRage&7]: &cYour Rage has cooled down.");
    public static final Property<String> rage_up = newProperty("Messages.Rage.Rage-Up", "&7[&c&lRage&7]: &7You are now doing &a%level%x &7Damage.");
    public static final Property<String> rage_damaged = newProperty("Messages.Rage.Damaged", "&7[&c&lRage&7]: &cYou have been hurt and it broke your Rage Multiplier!");

    public static final Property<List<String>> limit_command = newListProperty("Messages.Limit-Command", List.of(
            "&0======================================",
            "&8[&aCrazyEnchants&8]: &bPersonal Enchantment Limit:",
            "",
            "&7Bypass Limit: &6%bypass%",
            "&7Vanilla Enchantment Check: &6%vanilla%",
            "&7Max Enchantment Limit: &6%limit%",
            "&7Base Enchantment Limit: &6%baseLimit%",
            "&7Current Items Slot Crystal Limit Adjustment: &6%slotCrystal%",
            "&7Current Enchantment amount on item: &6%item%",
            "&7You can add &6%space% &7more enchantments to this item.",
            "&0======================================"
    ));

    public static final Property<List<String>> help = newListProperty("Messages.Help", List.of(
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
            "&b/ce spawn <enchantment/category> [(level:#/min-max)/world:<world>/x:#/y:#/z:#] - &9Drops an enchantment book at the specific coordinates."
    ));

}