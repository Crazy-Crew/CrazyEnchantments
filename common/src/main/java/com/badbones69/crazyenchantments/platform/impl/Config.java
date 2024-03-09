package com.badbones69.crazyenchantments.platform.impl;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class Config implements SettingsHolder {

    @Comment("The prefix used in messages.")
    public static final Property<String> prefix = newProperty("Settings.Prefix", "&8[&aCrazyEnchants&8]: ");

    @Comment("Turn on and off whether your server stats are sent to https://bstats.org/ - Requires a restart!")
    public static final Property<Boolean> toggle_metrics = newProperty("Settings.Toggle-Metrics", true);

    @Comment("Do you want to be notified if player data is backed up?")
    public static final Property<Boolean> notify_backup_complete = newProperty("Settings.Player-Info-Backup-Message", true);

    @Comment("This is for some servers that get a health duplication glitch from plugins like CombatTagPlus(v1.2.3 down)")
    public static final Property<Boolean> reset_players_max_health = newProperty("Settings.Reset-Players-Max-Health", true);

    @Comment("In the odd chance potion effects disappear on world change. You can enable this!")
    public static final Property<Boolean> refresh_effects_world_change = newProperty("Settings.Refresh-Potion-Effects-On-World-Change", false);

    @Comment("The name of /ce inventory")
    public static final Property<String> inventory_name = newProperty("Settings.InvName", "&4&l&nCrazy Enchanter");

    @Comment("The item that the enchantment book is.")
    public static final Property<String> enchantment_book_item = newProperty("Settings.Enchantment-Book-Item", "BOOK");

    @Comment("Toggle on or off if the book will be glowing.")
    public static final Property<Boolean> enchantment_book_glowing = newProperty("Settings.Enchantment-Book-Glowing", true);

    @Comment("The size of the GUI. It must be a factor of 9.")
    public static final Property<Integer> inventory_size = newProperty("Settings.GUISize", 54);

    @Comment("The lore on enchantment books.")
    public static final Property<List<String>> enchantment_book_lore = newListProperty("Settings.EnchantmentBookLore", List.of(
            "&7Drag book and drop on Item.",
            "&7Right click for more Info.",
            "",
            "%description%",
            "",
            "&4%destroy_rate%% Destroy Chance",
            "&a%success_rate%% Success Chance"
    ));

    @Comment("The item the lost books are.")
    public static final Property<String> lost_book_item = newProperty("Settings.LostBook.Item", "BOOK");

    @Comment("The name of the lost books.")
    public static final Property<String> lost_book_name = newProperty("Settings.LostBook.Name", "&8&l&nA Lost %category%&8&l&n Book");

    @Comment("The lore of the lost books.")
    public static final Property<List<String>> lost_book_lore = newListProperty("Settings.LostBook.Lore", List.of(
            "&7This book has been lost for centuries",
            "&7It is said to be an enchantment book from %category%",
            "&7But you must clean it off to find out what kind it is.",
            "&7&l(&6&l!&7&l) &7Right Click to clean off."
    ));

    @Comment("The item it will be.")
    public static final Property<String> scrambler_item = newProperty("Settings.Scrambler.Item", "BOOK");

    @Comment("If it has a glowing effect.")
    public static final Property<Boolean> scrambler_glowing = newProperty("Settings.Scrambler.Glowing", true);

    @Comment("The name of the item.")
    public static final Property<String> scrambler_name = newProperty("Settings.Scrambler.Name", "&e&lThe Grand Scrambler");

    @Comment("The lore of the item.")
    public static final Property<List<String>> scrambler_lore = newListProperty("Settings.Scrambler.Lore", List.of(
            "&7The &e&lThe Grand Scrambler &7will allow",
            "&7you to re-roll the destroy and success rates.",
            "&7Drag and drop it on an enchantment book",
            "&7to get a new destroy and success rate."
    ));

    @Comment("Name of the GUI.")
    public static final Property<String> scrambler_gui = newProperty("Settings.Scrambler.GUI", "&8Rolling the &eScrambler");

    @Comment("If the GUI will show. Warning>> This may cause lag if a lot of players are using it all the time.")
    public static final Property<Boolean> scrambler_toggle = newProperty("Settings.Scrambler.GUI.Toggle", true);

    @Comment("The item the pointer is.")
    public static final Property<String> scrambler_pointer_item = newProperty("Settings.Scrambler.GUI.Pointer.Item", "REDSTONE_TORCH");

    @Comment("The name of the pointer.")
    public static final Property<String> scrambler_pointer_name = newProperty("Settings.Scrambler.GUI.Pointer.Name", "&c&lPointer");

    @Comment("The lore on the pointer.")
    public static final Property<List<String>> scrambler_pointer_lore = newListProperty("Settings.Scrambler.GUI.Pointer.Lore", List.of(
            "&7Whatever percents the item lands on",
            "&7will be the new percents on your book."
    ));

    @Comment("If you can buy it in the GUI.")
    public static final Property<Boolean> scrambler_in_gui = newProperty("Settings.Scrambler.InGUI", false);

    @Comment("The player you want to appear as a skull.")
    public static final Property<String> scrambler_gui_player = newProperty("Settings.Scrambler.Player", "");

    @Comment("The slot the item will be in.")
    public static final Property<Integer> scrambler_gui_slot = newProperty("Settings.Scrambler.Slot", 1);

    @Comment("The name of the item in the gui.")
    public static final Property<String> scrambler_gui_name = newProperty("Settings.Scrambler.GUIName", "");

    @Comment("The lore of the item in the gui.")
    public static final Property<List<String>> scrambler_gui_lore = newListProperty("Settings.Scrambler.GUILore", List.of(
            "&7The &e&lThe Grand Scrambler &7will allow",
            "&7you to re-roll the destroy and success rates.",
            "&7Drag and drop it on an enchantment book",
            "&7to get a new destroy and success rate.",
            "",
            "&eCost: &e&l$800"
    ));

    @Comment("If it's in the GUI.")
    public static final Property<Boolean> slot_crystal_in_gui = newProperty("Settings.Slot_Crystal.InGUI", false);

    @Comment("The player you want to appear as a skull.")
    public static final Property<String> slot_crystal_gui_player = newProperty("Settings.Slot_Crystal.Player", "");

    @Comment("Slot it is in")
    public static final Property<Integer> slot_crystal_gui_slot = newProperty("Settings.Slot_Crystal.Slot", 14);

    @Comment("Item it is")
    public static final Property<String> slot_crystal_item = newProperty("Settings.Slot_Crystal.Item", "EMERALD");

    @Comment("#If it has a glowing effect.")
    public static final Property<Boolean> slot_crystal_glowing = newProperty("Settings.Slot_Crystal.Glowing", true);

    @Comment("Name of the item")
    public static final Property<String> slot_crystal_name = newProperty("Settings.Slot_Crystal.Name", "&5&lSlot &b&lCrystal");

    @Comment("Lore of the item")
    public static final Property<List<String>> slot_crystal_lore = newListProperty("Settings.Slot_Crystal.Lore", List.of(
            "&7A rare crystal that is said to",
            "&7increase the amount of enchants",
            "&7that can be added onto an item.",
            "",
            "&7&l(&6&l!&7&l) &7Drag and drop on an item."
    ));

    @Comment("The name of the item in the gui.")
    public static final Property<String> slot_crystal_gui_name = newProperty("Settings.Slot_Crystal.GUIName", "&5&lSlot &b&lCrystal");

    @Comment("The lore of the item in the gui.")
    public static final Property<List<String>> slot_crystal_gui_lore = newListProperty("Settings.Slot_Crystal.GUILore", List.of(
            "&7A rare crystal that is said to",
            "&7increase the amount of enchants",
            "&7that can be added onto an item.",
            "",
            "&eCost: &e&l$500"
    ));

    @Comment("If you can open the GKitz GUI from the main /ce gui.")
    public static final Property<Boolean> gkitz_in_gui = newProperty("Settings.GKitz.InGUI", false);

    @Comment("If the GKitz option is enabled on the server.")
    public static final Property<Boolean> gkitz_toggle = newProperty("Settings.Gkitz.Enabled", true);

    @Comment("The player you want to appear as a skull.")
    public static final Property<String> gkitz_player = newProperty("Settings.GKitz.Player", "");

    @Comment("The slot it will be in.")
    public static final Property<Integer> gkitz_slot = newProperty("Settings.GKitz.Slot", 1);

    @Comment("The item it is.")
    public static final Property<String> gkitz_item = newProperty("Settings.GKitz.Item", "DIAMOND_CHESTPLATE");

    @Comment("If it has a glowing effect.")
    public static final Property<Boolean> gkitz_glowing = newProperty("Settings.GKitz.Glowing", true);

    @Comment("The name of the item.")
    public static final Property<String> gkitz_name = newProperty("Settings.GKitz.Name", "&c&lGKitz");

    @Comment("The lore of the item.")
    public static final Property<List<String>> gkitz_lore = newListProperty("Settings.GKitz.Lore", List.of(
            "&b>>&7&nClick to open&b<<"
    ));

    @Comment("If it should be in the gui.")
    public static final Property<Boolean> protection_crystal_in_gui = newProperty("Settings.ProtectionCrystal.InGUI", false);

    @Comment("The player you want to appear as a skull.")
    public static final Property<String> protection_crystal_gui_player = newProperty("Settings.ProtectionCrystal.Player", "");

    @Comment("The slot the crystal will be at in the gui.")
    public static final Property<Integer> protection_crystal_slot = newProperty("Settings.ProtectionCrystal.Slot", 1);

    @Comment("The item the crystal will be.")
    public static final Property<String> protection_crystal_item = newProperty("Settings.ProtectionCrystal.Item", "EMERALD");

    @Comment("If the item should glow.")
    public static final Property<Boolean> protection_crystal_glowing = newProperty("Settings.ProtectionCrystal.Glowing", true);

    @Comment("The message telling the player if it is protected.")
    public static final Property<String> protection_crystal_protected = newProperty("Settings.ProtectionCrystal.Protected", "&6Ancient Protection");

    @Comment("If the crystal loses protection on death.")
    public static final Property<Boolean> protection_crystal_lose_protection_on_death = newProperty("Settings.ProtectionCrystal.Lose-Protection-On-Death", true);

    @Comment("If the crystal should function.")
    public static final Property<Boolean> protection_crystal_chance_toggle = newProperty("Settings.ProtectionCrystal.Chance.Toggle", false);

    @Comment("The chance the protection crystal has to activate.")
    public static final Property<Integer> protection_crystal_chance = newProperty("Settings.ProtectionCrystal.Chance.Success-Chance", 75);

    @Comment("The name of the item when given.")
    public static final Property<String> protection_crystal_name = newProperty("Settings.ProtectionCrystal.Name", "&5&lProtection &b&lCrystal");

    @Comment("The lore of the item when given.")
    public static final Property<List<String>> protection_crystal_lore = newListProperty("Settings.ProtectionCrystal.Lore", List.of(
            "&7A rare crystal that is said to",
            "&7protect items from getting lost",
            "&7while the owners away in the after life.",
            "",
            "&7&l(&6&l!&7&l) &7Drag and drop on an item."
    ));

    @Comment("The name of the item in the gui.")
    public static final Property<String> protection_crystal_gui_name = newProperty("Settings.ProtectionCrystal.GUIName", "&5&lProtection &b&lCrystal");

    @Comment("The lore of the item in the gui.")
    public static final Property<List<String>> protection_crystal_gui_lore = newListProperty("Settings.ProtectionCrystal.GUILore", List.of(
            "&7A rare crystal that is said to",
            "&7protect items from getting lost",
            "&7while the owners away in the after life.",
            "",
            "&eCost: &e&l$500"
    ));

    @Comment("If the item should be in the gui.")
    public static final Property<Boolean> blacksmith_in_gui = newProperty("Settings.BlackSmith.InGUI", true);

    @Comment("The player you want to appear as a skull.")
    public static final Property<String> blacksmith_player = newProperty("Settings.BlackSmith.Player", "");

    @Comment("The slot the item is at.")
    public static final Property<Integer> blacksmith_slot = newProperty("Settings.BlackSmith.Slot", 24);

    @Comment("The item in the gui.")
    public static final Property<String> blacksmith_item = newProperty("Settings.BlackSmith.Item", "ANVIL");

    @Comment("If the item should glow")
    public static final Property<Boolean> blacksmith_glowing = newProperty("Settings.BlackSmith.Glowing", false);

    @Comment("Name of the item in the gui")
    public static final Property<String> blacksmith_name = newProperty("Settings.BlackSmith.Name", "&7&lThe &b&lBlack &9&lSmith");

    @Comment("Lore on the item")
    public static final Property<List<String>> blacksmith_lore = newListProperty("Settings.BlackSmith.Lore", List.of(
            "&b>>&7&nClick to open&b<<"
    ));

    @Comment("Name of the black smith gui")
    public static final Property<String> blacksmith_gui_name = newProperty("Settings.BlackSmith.GUIName", "&8&lThe Black Smith");

    @Comment("Name of the item when there are no results")
    public static final Property<String> blacksmith_results_none = newProperty("Settings.BlackSmith.Results.None", "&c&lNo Results");

    @Comment("The not found lore")
    public static final Property<List<String>> blacksmith_results_lore = newListProperty("Settings.BlackSmith.Results.Not-Found-Lore", List.of(
            "&7No results could be found.",
            "&7Please put in two books of",
            "&7the same enchantment and level.",
            "&7Or put in two items to combined",
            "&7the enchantments on them."
    ));

    @Comment("The lore at the bottom of the item")
    public static final Property<String> blacksmith_results_found = newProperty("Settings.BlackSmith.Results.Found", "&c&lCost: &6&l%cost%XP");

    @Comment("Available currencies: XP_LEVEL, XP_TOTAL, VAULT")
    public static final Property<String> blacksmith_transaction_currency = newProperty("Settings.BlackSmith.Transaction.Currency", "XP_LEVEL");

    @Comment("When an enchantment levels up 1 on an item")
    public static final Property<Integer> blacksmith_transaction_power_up = newProperty("Settings.BlackSmith.Transaction.Costs.Power-Up", 5);

    @Comment("When it adds an enchantment to the item from another item")
    public static final Property<Integer> blacksmith_transaction_add_enchantment = newProperty("Settings.BlackSmith.Transaction.Costs.Add-Enchantment", 3);

    @Comment("Leveling up the power of a book")
    public static final Property<Integer> blacksmith_transaction_book_upgrade = newProperty("Settings.BlackSmith.Transaction.Costs.Book-Upgrade", 5);

    @Comment("Toggle whether it is in the /CE GUI or not")
    public static final Property<Boolean> tinker_in_gui = newProperty("Settings.Tinker.InGUI", true);

    @Comment("The player you want to appear as a skull.")
    public static final Property<String> tinker_player = newProperty("Settings.Tinker.Player", "");

    @Comment("Slot it is in")
    public static final Property<Integer> tinker_slot = newProperty("Settings.Tinker.Slot", 22);

    @Comment("Item it is")
    public static final Property<String> tinker_item = newProperty("Settings.Tinker.Item", "NETHER_STAR");

    @Comment("If it has a glowing effect.")
    public static final Property<Boolean> tinker_glowing = newProperty("Settings.Tinker.Glowing", false);

    @Comment("Name of the Item")
    public static final Property<String> tinker_name = newProperty("Settings.Tinker.Name", "&7&lThe &b&lCrazy &9&lTinkerer");

    @Comment("Lore of the item")
    public static final Property<List<String>> tinker_lore = newListProperty("Settings.Tinker.Lore", List.of(
            "&b>>&7&nClick to open&b<<"
    ));

    @Comment("Toggle whether it is in the /CE GUI or not")
    public static final Property<Boolean> info_in_gui = newProperty("Settings.Info.InGUI", true);

    @Comment("The player you want to appear as a skull.")
    public static final Property<String> info_player = newProperty("Settings.Info.Player", "");

    @Comment("Slot it is in")
    public static final Property<Integer> info_slot = newProperty("Settings.Info.Slot", 23);

    @Comment("Item it is")
    public static final Property<String> info_item = newProperty("Settings.Info.Item", "COMPASS");

    @Comment("If it has a glowing effect.")
    public static final Property<Boolean> info_glowing = newProperty("Settings.Info.Glowing", true);

    @Comment("Name of the Item")
    public static final Property<String> info_name = newProperty("Settings.Info.Name", "&eInfo on the Enchantments");

    @Comment("Lore of the item")
    public static final Property<List<String>> info_lore = newListProperty("Settings.Info.Lore", List.of(
            "&b>>&7&nClick to view&b<<"
    ));

    @Comment("Item mystery dust is")
    public static final Property<String> mystery_dust_item = newProperty("Settings.Dust.MysteryDust.Item", "GUNPOWDER");

    @Comment("Name of the dust")
    public static final Property<String> mystery_dust_name = newProperty("Settings.Dust.MysteryDust.Name", "&7Mystery Dust");

    @Comment("Use %percent% to set the percent")
    public static final Property<List<String>> mystery_dust_lore = newListProperty("Settings.Dust.MysteryDust.Lore", List.of(
            "&a1-%percent%% &7will be on a",
            "&aMagical AngelDust &7or &eMagical Fixing &7Dust",
            "&7&l(&6&l!&7&l) &7Right click"
    ));

    @Comment("Toggle if the firework happens on open.")
    public static final Property<Boolean> mystery_dust_firework_toggle = newProperty("Settings.Dust.MysteryDust.Firework.Toggle", true);

    @Comment("The colors the firework will be.")
    public static final Property<String> mystery_dust_firework_colors = newProperty("Settings.Dust.MysteryDust.Firework.Colors", "Black, Gray, Lime");

    @Comment("Toggle whether you can get success dust from Mystery Dust.")
    public static final Property<Boolean> mystery_dust_toggle_success = newProperty("Settings.Dust.MysteryDust.Dust-Toggle.Success", true);

    @Comment("Toggle whether you can get destroy dust from Mystery Dust.")
    public static final Property<Boolean> mystery_dust_toggle_destroy = newProperty("Settings.Dust.MysteryDust.Dust-Toggle.Destroy", true);

    @Comment("Toggle whether you can get failed dust from Mystery Dust.")
    public static final Property<Boolean> mystery_dust_toggle_failed = newProperty("Settings.Dust.MysteryDust.Dust-Toggle.Failed", true);

    @Comment("Min will always be 1.")
    public static final Property<Integer> mystery_dust_min_percent_range = newProperty("Settings.Dust.MysteryDust.PercentRange.Min", 1);

    @Comment("Max percent")
    public static final Property<Integer> mystery_dust_max_percent_range = newProperty("Settings.Dust.MysteryDust.PercentRange.Max", 30);

    @Comment("If you can buy it in the /CE GUI")
    public static final Property<Boolean> success_dust_in_gui = newProperty("Settings.Dust.SuccessDust.InGUI", true);

    @Comment("Slot number it is on")
    public static final Property<Integer> success_dust_slot = newProperty("Settings.Dust.SuccessDust.Slot", 25);

    @Comment("Name of the item in the GUI")
    public static final Property<String> success_dust_gui_name = newProperty("Settings.Dust.SuccessDust.GUIName", "&aMagical Angel Dust");

    @Comment("Lore in the GUI.")
    public static final Property<List<String>> success_dust_gui_lore = newListProperty("Settings.Dust.SuccessDust.GUILore", List.of(
            "&eCost: &a&l$500",
            "&7Dust allows you to bring",
            "&7up your &aSuccess Rate&7."
    ));

    @Comment("Item the dust is")
    public static final Property<String> success_dust_item = newProperty("Settings.Dust.SuccessDust.Item", "SUGAR");

    @Comment("Name of the dust")
    public static final Property<String> success_dust_name = newProperty("Settings.Dust.SuccessDust.Name", "&aMagical Angel Dust");

    @Comment("Lore for the dust")
    public static final Property<List<String>> success_dust_lore = newListProperty("Settings.Dust.SuccessDust.Lore", List.of(
            "&a+%percent%% Success Rate",
            "&7Apply to an Enchantment Book to",
            "&7Increase the Success Rate by &e%percent%%"
    ));

    @Comment("Max percent")
    public static final Property<Integer> success_dust_max_percent_range = newProperty("Settings.Dust.SuccessDust.PercentRange.Max", 30);

    @Comment("Minimum percent")
    public static final Property<Integer> success_dust_min_percent_range = newProperty("Settings.Dust.SuccessDust.PercentRange.Min", 2);

    @Comment("If you can buy it in the /CE GUI")
    public static final Property<Boolean> destroy_dust_in_gui = newProperty("Settings.Dust.DestroyDust.InGUI", true);

    @Comment("Slot number it is on")
    public static final Property<Integer> destroy_dust_slot = newProperty("Settings.Dust.DestroyDust.Slot", 34);

    @Comment("Name of the item in the GUI")
    public static final Property<String> destroy_dust_gui_name = newProperty("Settings.Dust.DestroyDust.GUIName", "&eMagical Fixing Dust");

    @Comment("Lore in the GUI.")
    public static final Property<List<String>> destroy_dust_gui_lore = newListProperty("Settings.Dust.DestroyDust.GUILore", List.of(
            "&eCost: &a&l$500",
            "&7Dust allows you to bring",
            "&7down your &4Destroy Rate&7."
    ));

    @Comment("Item the dust is")
    public static final Property<String> destroy_dust_item = newProperty("Settings.Dust.DestroyDust.Item", "REDSTONE");

    @Comment("Name of the dust")
    public static final Property<String> destroy_dust_name = newProperty("Settings.Dust.DestroyDust.Name", "&eMagical Fixing Dust");

    @Comment("Lore for the dust")
    public static final Property<List<String>> destroy_dust_lore = newListProperty("Settings.Dust.DestroyDust.Lore", List.of(
            "&a+%percent%% Success Rate",
            "&7Apply to an Enchantment Book to",
            "&7Increase the Success Rate by &e%percent%%"
    ));

    @Comment("Max percent")
    public static final Property<Integer> destroy_dust_max_percent_range = newProperty("Settings.Dust.DestroyDust.PercentRange.Max", 10);

    @Comment("Minimum percent")
    public static final Property<Integer> destroy_dust_min_percent_range = newProperty("Settings.Dust.DestroyDust.PercentRange.Min", 2);

    @Comment("Item the dust is")
    public static final Property<String> failed_dust_item = newProperty("Settings.Dust.FailedDust.Item", "GUNPOWDER");

    @Comment("Name of the dust")
    public static final Property<String> failed_dust_name = newProperty("Settings.Dust.FailedDust.Name", "&7Failed Dust");

    @Comment("Lore for the dust")
    public static final Property<List<String>> failed_dust_lore = newListProperty("Settings.Dust.FailedDust.Lore", List.of(
            "&7This dust has failed.",
            "&7It is now useless unless you",
            "&7needed to make TNT or a throwable potion."
    ));

    @Comment("The item the black scroll can be")
    public static final Property<String> black_scroll_item = newProperty("Settings.BlackScroll.Item", "INK_SAC");

    @Comment("The name of the black scroll")
    public static final Property<String> black_scroll_name = newProperty("Settings.BlackScroll.Name", "&8&l&nBlack Scroll");

    @Comment("The lore that will be on the black scroll.")
    public static final Property<List<String>> black_scroll_lore = newListProperty("Settings.BlackScroll.Item-Lore", List.of(
            "&7Have a custom enchantment you don''t want?",
            "&7Drag and drop this on an item with a custom enchantment",
            "&7and it will remove a random enchantment for you.",
            "&7&l(&6&l!&7&l) &7Only works on Custom Enchantments."
    ));

    @Comment("Toggle on and off if the black scrolls have a chance of failing.")
    public static final Property<Boolean> black_scroll_chance_toggle = newProperty("Settings.BlackScroll.Chance-Toggle", false);

    @Comment("The chance that the black scroll will work.")
    public static final Property<Integer> black_scroll_chance = newProperty("Settings.BlackScroll.Chance", 75);

    @Comment("Toggle being able to buy in the /CE GUI.")
    public static final Property<Boolean> black_scroll_in_gui = newProperty("Settings.BlackScroll.InGUI", true);

    @Comment("The name of the gui.")
    public static final Property<String> black_scroll_gui_name = newProperty("Settings.BlackScroll.GUIName", "&7Black Scroll");

    @Comment("")
    public static final Property<String> black_scroll_gui_player = newProperty("Settings.BlackScroll.Player", "");

    @Comment("If it has a glowing effect.")
    public static final Property<Boolean> black_scroll_glowing = newProperty("Settings.BlackScroll.Glowing", false);

    @Comment("Slot you can buy the scroll at")
    public static final Property<Integer> black_scroll_gui_slot = newProperty("Settings.BlackScroll.Slot", 21);

    @Comment("Lore of the item in the GUI")
    public static final Property<List<String>> black_scroll_gui_lore = newListProperty("Settings.BlackScroll.Lore", List.of(
            "&eCost: &a&l$1000",
            "&7Black Scrolls allow you to",
            "&7take off random enchantments."
    ));

    @Comment("The Max percent")
    public static final Property<Integer> black_scroll_success_chance_max = newProperty("Settings.BlackScroll.SuccessChance.Max", 75);

    @Comment("The Minimum percent")
    public static final Property<Integer> black_scroll_success_chance_min = newProperty("Settings.BlackScroll.SuccessChance.Min", 35);

    @Comment("The Max percent")
    public static final Property<Integer> black_scroll_destroy_chance_max = newProperty("Settings.BlackScroll.DestroyChance.Max", 25);

    @Comment("The Minimum percent")
    public static final Property<Integer> black_scroll_destroy_chance_min = newProperty("Settings.BlackScroll.DestroyChance.Min", 5);

    @Comment("The item the white scroll is.")
    public static final Property<String> white_scroll_item = newProperty("Settings.WhiteScroll.Item", "PAPER");

    @Comment("The name of the white scrolls.")
    public static final Property<String> white_scroll_name = newProperty("Settings.WhiteScroll.Name", "&e&lWhite Scroll");

    @Comment("The lore that will be on the white scroll.")
    public static final Property<List<String>> white_scroll_lore = newListProperty("Settings.WhiteScroll.Item-Lore", List.of(
            "&7Want to protect an item from a book''s destroy rate?",
            "&7Drag and drop this on an enchant-able item",
            "&7and it will protect the item from the destroy rate."
    ));

    @Comment("Toggle being able to buy in the /CE GUI.")
    public static final Property<Boolean> white_scroll_in_gui = newProperty("Settings.WhiteScroll.InGUI", true);

    @Comment("The name of the gui.")
    public static final Property<String> white_scroll_gui_name = newProperty("Settings.WhiteScroll.GUIName", "&7White Scroll");

    @Comment("If it has a glowing effect.")
    public static final Property<Boolean> white_scroll_glowing = newProperty("Settings.WhiteScroll.Glowing", false);

    @Comment("The player you want to appear as a skull.")
    public static final Property<String> white_scroll_gui_player = newProperty("Settings.WhiteScroll.Player", "");

    @Comment("Slot you can buy the scroll at")
    public static final Property<Integer> white_scroll_gui_slot = newProperty("Settings.WhiteScroll.Slot", 30);

    @Comment("Lore of the scroll in the GUI")
    public static final Property<List<String>> white_scroll_gui_lore = newListProperty("Settings.WhiteScroll.Lore", List.of(
            "&eCost: &a&l$2000",
            "&7White Scrolls allow you to",
            "&7protect items from breaking."
    ));

    @Comment("The lore that is added to protect an item.")
    public static final Property<String> white_scroll_protected = newProperty("Settings.WhiteScroll.ProtectedName", "&b&lPROTECTED");

    @Comment("The item the scroll is.")
    public static final Property<String> transmog_scroll_item = newProperty("Settings.TransmogScroll.Item", "QUARTZ");

    @Comment("The name of the scroll.")
    public static final Property<String> transmog_scroll_name = newProperty("Settings.TransmogScroll.Name", "&d&lTransmog Scroll");

    @Comment("The lore that will be on the scroll.")
    public static final Property<List<String>> transmog_scroll_lore = newListProperty("Settings.TransmogScroll.Item-Lore", List.of(
            "&7This scroll allows you to organize",
            "&7your enchantments and tell you how many",
            "&7enchantments you currently have on the item."
    ));

    @Comment("Toggle being able to buy in the /CE GUI.")
    public static final Property<Boolean> transmog_scroll_in_gui = newProperty("Settings.TransmogScroll.InGUI", true);

    @Comment("The name of the gui.")
    public static final Property<String> transmog_scroll_gui_name = newProperty("Settings.TransmogScroll.GUIName", "&dTransmog Scroll");

    @Comment("If it has a glowing effect.")
    public static final Property<Boolean> transmog_scroll_glowing = newProperty("Settings.TransmogScroll.Glowing", false);

    @Comment("The player you want to appear as a skull.")
    public static final Property<String> transmog_scroll_gui_player = newProperty("Settings.TransmogScroll.Player", "");

    @Comment("Slot you can buy the scroll at")
    public static final Property<Integer> transmog_scroll_gui_slot = newProperty("Settings.TransmogScroll.Slot", 30);

    @Comment("Lore of the scroll in the GUI")
    public static final Property<List<String>> transmog_scroll_gui_lore = newListProperty("Settings.TransmogScroll.Lore", List.of(
            "&eCost: &a&l$200",
            "&7This scroll allows you to organize",
            "&7your enchantments and tell you how many",
            "&7enchantments you currently have on the item."
    ));

    @Comment("The suffix that will be added at the end of the item's name.")
    public static final Property<String> amount_of_enchantments = newProperty("Settings.TransmogScroll.Amount-of-Enchantments", " &7[&6&n%amount%&7]");

    @Comment("If the amount is added to the suffix of the item's name.")
    public static final Property<Boolean> amount_toggle = newProperty("Settings.TransmogScroll.Amount-Toggle", true);

    @Comment("If it counts the vanilla enchantments in the amount.")
    public static final Property<Boolean> count_vanilla_enchantments = newProperty("Settings.TransmogScroll.Count-Vanilla-Enchantments", true);

    @Comment("The order in which you want the lore sections to be applied to the item when using the transmog scroll.")
    public static final Property<List<String>> lore_order = newListProperty("Settings.TransmogScroll.Lore-Order", List.of(
            "CE_Enchantments",
            "Protection",
            "Normal_Lore"
    ));

    @Comment("Dictates if each lore section listed above should have a blank line between them.")
    public static final Property<Boolean> add_blank_lines = newProperty("Settings.TransmogScroll.Add-Blank-Lines", true);

    @Comment("Turn on and off the Max amount of enchantments. Use crazyenchantments.limit.# permission to set the limit.")
    public static final Property<Boolean> max_amount_of_enchantments = newProperty("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle", true);

    @Comment("Count Vanilla enchantments also.")
    public static final Property<Boolean> include_vanilla_enchantments = newProperty("Settings.EnchantmentOptions.IncludeVanillaEnchantments", false);

    @Comment("Turn on if you wish for enchantment books to enchant stacked items.")
    public static final Property<Boolean> enchant_stacked_items = newProperty("Settings.EnchantmentOptions.Enchant-Stacked-Items", false);

    @Comment("The max amount of rage that scan stack when fighting.")
    public static final Property<Integer> max_rage_level = newProperty("Settings.EnchantmentOptions.MaxRageLevel", 4);

    @Comment("If the player is damaged while building rage, it will be broken if this is enabled.")
    public static final Property<Boolean> break_rage_on_damage = newProperty("Settings.EnchantmentOptions.Break-Rage-On-Damage", true);

    @Comment("If true, messages from rage will be displayed on a boss bar.")
    public static final Property<Boolean> rage_boss_bar = newProperty("Settings.EnchantmentOptions.Rage-Boss-Bar", false);

    @Comment("Rage will increase in damage by a multiple of enchantLevel*rageIncrease every hit.")
    public static final Property<Double> rage_increase = newProperty("Settings.EnchantmentOptions.Rage-Increase", 0.1);

    @Comment("Toggles whether when you right-click the enchantment book if it messages the player the description or not.")
    public static final Property<Boolean> right_click_book = newProperty("Settings.EnchantmentOptions.Right-Click-Book-Description", true);

    @Comment("Having this set to true will allow players to use enchantments levels above the max level.")
    public static final Property<Boolean> unsafe_enchantments = newProperty("Settings.EnchantmentOptions.Unsafe-Enchantments", true);

    @Comment("Toggle if the item takes damage for every block it breaks or just one. True to take full damage or false to take only 1 damage.")
    public static final Property<Boolean> blast_full_durability = newProperty("Settings.EnchantmentOptions.Blast-Full-Durability", true);

    @Comment("Toggle if the item takes damage for every block it breaks or just one. True to take full damage or false to take only 1 damage.")
    public static final Property<Boolean> vein_miner_full_durability = newProperty("Settings.EnchantmentOptions.VeinMiner-Full-Durability", true);

    @Comment("Toggle if the blast enchantment drops blocks when used.")
    public static final Property<Boolean> drop_blocks_for_blast = newProperty("Settings.EnchantmentOptions.Drop-Blocks-For-Blast", true);

    @Comment("Toggle if the veinminer enchantment drops blocks when used.")
    public static final Property<Boolean> drop_blocks_for_veinminer = newProperty("Settings.EnchantmentOptions.Drop-Blocks-For-VeinMiner", true);

    @Comment("When set to true if a player right-clicks an enchantment table it will open the /ce GUI.")
    public static final Property<Boolean> right_click_enchantment_table = newProperty("Settings.EnchantmentOptions.Right-Click-Enchantment-Table", false);

    @Comment("Only needs to be true if the player's XP sometimes glitches to 1Bill after buying an item with all their XP.")
    public static final Property<Boolean> experience_bug = newProperty("Settings.EnchantmentOptions.EXP-Bug", false);

    @Comment("Range in blocks where the lightning sound should become inaudible.")
    public static final Property<Integer> lightning_sound_range = newProperty("Settings.EnchantmentOptions.Lightning-Sound-Range", 100);

    @Comment("The name of ally mobs that show above their heads.")
    public static final Property<String> ally_mobs_wolf = newProperty("Settings.EnchantmentOptions.Ally-Mobs.Wolf", "&b%player%''s Saberwolf");

    @Comment("The name of ally mobs that show above their heads.")
    public static final Property<String> ally_mobs_golem = newProperty("Settings.EnchantmentOptions.Ally-Mobs.Iron-Golem", "&6%player%''s Golem");

    @Comment("The name of ally mobs that show above their heads.")
    public static final Property<String> ally_mobs_zombie = newProperty("Settings.EnchantmentOptions.Ally-Mobs.Zombie", "&2%player%''s Undead");

    @Comment("The name of ally mobs that show above their heads.")
    public static final Property<String> ally_mobs_endermite = newProperty("Settings.EnchantmentOptions.Ally-Mobs.Endermite", "&5%player%''s Endermite");

    @Comment("The name of ally mobs that show above their heads.")
    public static final Property<String> ally_mobs_silverfish = newProperty("Settings.EnchantmentOptions.Ally-Mobs.Silverfish", "&7%player%''s Silverfish");

    @Comment("The name of ally mobs that show above their heads.")
    public static final Property<String> ally_mobs_bee = newProperty("Settings.EnchantmentOptions.Ally-Mobs.Bee", "&e%player%''s Bee");

    @Comment("Toggle on or off the ability to upgrade the enchantments.")
    public static final Property<Boolean> armor_upgrade_toggle = newProperty("Settings.EnchantmentOptions.Armor-Upgrade.Toggle", true);

    @Comment("If the book's destroy rate happens the item doesn't break but instead the current lower version of the enchantment is destroyed. If false then the item will be destroyed instead.")
    public static final Property<Boolean> armor_upgrade_break = newProperty("Settings.EnchantmentOptions.Armor-Upgrade.Enchantment-Break", true);

    @Comment("Toggle if the wings enchantment shows particles under the player's feet. Warning this option has been known to cause lag so toggle it off if you get lag.")
    public static final Property<Boolean> wings_clouds = newProperty("Settings.EnchantmentOptions.Wings.Clouds", true);

    @Comment("This toggles on and off the wings enchantment when an enemy is close to the player.")
    public static final Property<Boolean> wings_enemy_toggle = newProperty("Settings.EnchantmentOptions.Wings.Enemy-Toggle", true);

    @Comment("How far away the enemy has to be to toggle off the enchantment.")
    public static final Property<Integer> wings_distance = newProperty("Settings.EnchantmentOptions.Wings.Distance", 25);

    @Comment("Toggle if a player is a member of a world guard region if they can fly.")
    public static final Property<Boolean> wings_members_can_fly = newProperty("Settings.EnchantmentOptions.Wings.Members-Can-Fly", true);

    @Comment("Toggle if a player is an owner of a world guard region if they can fly.")
    public static final Property<Boolean> wings_owners_can_fly = newProperty("Settings.EnchantmentOptions.Wings.Owners-Can-Fly", true);

    @Comment("All world guard regions the Wings enchantment can work in.")
    public static final Property<List<String>> wings_regions = newListProperty("Settings.EnchantmentOptions.Wings.Regions", List.of(
            "Free_Fly",
            "SafeZone"
    ));

    @Comment("Worlds that players are allowed to fly for a limitless amount of time. This bypasses enemy checks as well.")
    public static final Property<List<String>> wings_worlds_limitless = newListProperty("Settings.EnchantmentOptions.Wings.Worlds.Limitless-Flight-Worlds", List.of(
            "Hub_Example"
    ));

    @Comment("If the player has wings enchantment in these worlds they will be able to fly anywhere regardless of regions/enemies.")
    public static final Property<List<String>> wings_worlds_whitelisted = newListProperty("Settings.EnchantmentOptions.Wings.Worlds.Whitelisted", List.of(
            "Hub_Example"
    ));

    @Comment("Wings will not work at all in these worlds regardless of regions.")
    public static final Property<List<String>> wings_worlds_blacklisted = newListProperty("Settings.EnchantmentOptions.Wings.Worlds.Blacklisted", List.of(
            "SkyBlock_Example"
    ));

    @Comment("How much the item will cost.")
    public static final Property<Integer> scrambler_cost = newProperty("Settings.Costs.Scrambler.Cost", 800);

    @Comment("The currency that will be taken when bought.")
    public static final Property<String> scrambler_currency = newProperty("Settings.Costs.Scrambler.Currency", "Vault");

    @Comment("How much the item will cost.")
    public static final Property<Integer> protection_crystal_cost = newProperty("Settings.Costs.ProtectionCrystal.Cost", 500);

    @Comment("The currency that will be taken when bought.")
    public static final Property<String> protection_crystal_currency = newProperty("Settings.Costs.ProtectionCrystal.Currency", "Vault");

    @Comment("How much the item will cost.")
    public static final Property<Integer> transmog_cost = newProperty("Settings.Costs.TransmogScroll.Cost", 200);

    @Comment("The currency that will be taken when bought.")
    public static final Property<String> transmog_currency = newProperty("Settings.Costs.TransmogScroll.Currency", "Vault");

    @Comment("How much the item will cost.")
    public static final Property<Integer> black_scroll_cost = newProperty("Settings.Costs.BlackScroll.Cost", 1000);

    @Comment("The currency that will be taken when bought.")
    public static final Property<String> black_scroll_currency = newProperty("Settings.Costs.BlackScroll.Currency", "Vault");

    @Comment("How much the item will cost.")
    public static final Property<Integer> white_scroll_cost = newProperty("Settings.Costs.WhiteScroll.Cost", 2000);

    @Comment("The currency that will be taken when bought.")
    public static final Property<String> white_scroll_currency = newProperty("Settings.Costs.WhiteScroll.Currency", "Vault");

    @Comment("How much the item will cost.")
    public static final Property<Integer> success_dust_cost = newProperty("Settings.Costs.SuccessDust.Cost", 500);

    @Comment("The currency that will be taken when bought.")
    public static final Property<String> success_dust_currency = newProperty("Settings.Costs.SuccessDust.Currency", "Vault");

    @Comment("How much the item will cost.")
    public static final Property<Integer> destroy_dust_cost = newProperty("Settings.Costs.DestroyDust.Cost", 500);

    @Comment("The currency that will be taken when bought.")
    public static final Property<String> destroy_dust_currency = newProperty("Settings.Costs.DestroyDust.Currency", "Vault");

    @Comment("How much the item will cost.")
    public static final Property<Integer> slot_crystal_cost = newProperty("Settings.Costs.Slot_Crystal.Cost", 500);

    @Comment("The currency that will be taken when bought.")
    public static final Property<String> slot_crystal_currency = newProperty("Settings.Costs.Slot_Crystal.Currency", "Vault");

    @Comment({
            "#####################",
            " Slot:<Slot> - The slot the item will go in.",
            " Item:<Item Name> - The item that it will be. You can use meta data for the items. List of names: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html",
            " Name:<Name> - The name that will go on the item.",
            " Lore:<Line1>,<Line2>,<Line3>,<Line4> - The lore can use multiple lines by adding a , in-between letters. Example: \"Lore:&7I am line1,&aI am line 2,&eI am line3\"",
            "Placeholders can be used in the name and lore, and they must be lowercase.",
            "Placeholders: %vault% %xp_level% %xp_total%",
            "#####################"
    })
    public static final Property<List<String>> gui_customization = newListProperty("Settings.GUICustomization", List.of(
            "Slot:1, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:2, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:3, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:4, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:5, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:6, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:7, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:8, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:9, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:10, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:18, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:19, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:27, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:28, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:36, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:37, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:45, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:46, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:47, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:48, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:49, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:50, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:51, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:52, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:53, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:54, Item:BLACK_STAINED_GLASS_PANE, Name: ",

            "Slot:11, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:12, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:13, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:14, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:15, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:16, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:17, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:20, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:26, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:29, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:35, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:38, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:39, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:41, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:42, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:43, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:44, Item:WHITE_STAINED_GLASS_PANE, Name: "
    ));
}