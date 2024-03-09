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

    @Comment("In the odd chance potion effects disappear on world change. You can enable this!")
    public static final Property<Boolean> refresh_effects_world_change = newProperty("Settings.Refresh-Potion-Effects-On-world-Change", false);

    @Comment("The name of /ce inventory")
    public static final Property<String> inventory_name = newProperty("Settings.InvName", "");

    @Comment("The item that the enchantment book is.")
    public static final Property<String> enchantment_book_item = newProperty("Settings.Enchantment-Book-Item", "BOOK");

    @Comment("Toggle on or off if the book will be glowing.")
    public static final Property<Boolean> enchantment_book_glowing = newProperty("Settings.Enchantment-Book-Glowing", true);

    @Comment("The size of the GUI. It must be a factor of 9.")
    public static final Property<Integer> inventory_size = newProperty("Settings.GUISize", 54);

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

    public static final Property<String> scrambler_gui = newProperty("Settings.Scrambler.GUI", "&8Rolling the &eScrambler");

    public static final Property<Boolean> scrambler_toggle = newProperty("Settings.Scrambler.GUI.Toggle", true);

    public static final Property<String> scrambler_pointer_item = newProperty("Settings.Scrambler.GUI.Pointer.Item", "REDSTONE_TORCH");

    public static final Property<String> scrambler_pointer_name = newProperty("Settings.Scrambler.GUI.Pointer.Name", "&c&lPointer");

    public static final Property<List<String>> scrambler_pointer_lore = newListProperty("Settings.Scrambler.GUI.Pointer.Lore", List.of(
            "&7Whatever percents the item lands on",
            "&7will be the new percents on your book."
    ));

    public static final Property<Boolean> scrambler_in_gui = newProperty("Settings.Scrambler.InGUI", false);

    public static final Property<Integer> scrambler_slot = newProperty("Settings.Scrambler.Slot", 1);

    public static final Property<String> scrambler_gui_name = newProperty("Settings.Scrambler.GUIName", "");

    public static final Property<List<String>> scrambler_gui_lore = newListProperty("Settings.Scrambler.GUILore", List.of(
            "&7The &e&lThe Grand Scrambler &7will allow",
            "&7you to re-roll the destroy and success rates.",
            "&7Drag and drop it on an enchantment book",
            "&7to get a new destroy and success rate.",
            "",
            "&eCost: &e&l$800"
    ));

    public static final Property<Boolean> slot_crystal_in_gui = newProperty("Settings.Slot_Crystal.InGUI", false);

    public static final Property<Integer> slot_crystal_slot = newProperty("Settings.Slot_Crystal.Slot", 14);

    public static final Property<String> slot_crystal_item = newProperty("Settings.Slot_Crystal.Item", "EMERALD");

    public static final Property<Boolean> slot_crystal_glowing = newProperty("Settings.Slot_Crystal.Glowing", true);

    public static final Property<String> slot_crystal_name = newProperty("Settings.Slot_Crystal.Name", "&5&lSlot &b&lCrystal");

    public static final Property<List<String>> slot_crystal_lore = newListProperty("Settings.Slot_Crystal.Lore", List.of(
            "&7A rare crystal that is said to",
            "&7increase the amount of enchants",
            "&7that can be added onto an item.",
            "",
            "&7&l(&6&l!&7&l) &7Drag and drop on an item."
    ));

    public static final Property<String> slot_crystal_gui_name = newProperty("Settings.Slot_Crystal.GUIName", "&5&lSlot &b&lCrystal");

    public static final Property<List<String>> slot_crystal_gui_lore = newListProperty("Settings.Slot_Crystal.GUILore", List.of(
            "&7A rare crystal that is said to",
            "&7increase the amount of enchants",
            "&7that can be added onto an item.",
            "",
            "&eCost: &e&l$500"
    ));

    public static final Property<Boolean> gkitz_in_gui = newProperty("Settings.GKitz.InGUI", false);

    public static final Property<Integer> gkitz_slot = newProperty("Settings.GKitz.Slot", 1);

    public static final Property<String> gkitz_item = newProperty("Settings.GKitz.Item", "DIAMOND_CHESTPLATE");

    public static final Property<Boolean> gkitz_glowing = newProperty("Settings.GKitz.Glowing", true);

    public static final Property<String> gkitz_name = newProperty("Settings.GKitz.Name", "&c&lGKitz");

    public static final Property<List<String>> gkitz_lore = newListProperty("Settings.GKitz.Lore", List.of(
            "&b>>&7&nClick to open&b<<"
    ));

    public static final Property<Boolean> protection_crystal_in_gui = newProperty("Settings.ProtectionCrystal.InGUI", false);

    public static final Property<Integer> protection_crystal_slot = newProperty("Settings.ProtectionCrystal.Slot", 1);

    public static final Property<String> protection_crystal_item = newProperty("Settings.ProtectionCrystal.Item", "EMERALD");

    public static final Property<Boolean> protection_crystal_glowing = newProperty("Settings.ProtectionCrystal.Glowing", true);

    public static final Property<String> protection_crystal_protected = newProperty("Settings.ProtectionCrystal.Protected", "&6Ancient Protection");

    public static final Property<Boolean> protection_crystal_lose_protection_on_death = newProperty("Settings.ProtectionCrystal.Lose-Protection-On-Death", true);

    public static final Property<Boolean> protection_crystal_chance_toggle = newProperty("Settings.ProtectionCrystal.Chance.Toggle", false);

    public static final Property<Integer> protection_crystal_chance_chance = newProperty("Settings.ProtectionCrystal.Chance.Success-Chance", 75);

    public static final Property<String> protection_crystal_name = newProperty("Settings.ProtectionCrystal.Name", "&5&lProtection &b&lCrystal");

    public static final Property<List<String>> protection_crystal_lore = newListProperty("Settings.ProtectionCrystal.Lore", List.of(
            "&7A rare crystal that is said to",
            "&7protect items from getting lost",
            "&7while the owners away in the after life.",
            "",
            "&7&l(&6&l!&7&l) &7Drag and drop on an item."
    ));

    public static final Property<String> protection_crystal_gui_name = newProperty("Settings.ProtectionCrystal.GUIName", "&5&lProtection &b&lCrystal");

    public static final Property<List<String>> protection_crystal_gui_lore = newListProperty("Settings.ProtectionCrystal.GUILore", List.of(
            "&7A rare crystal that is said to",
            "&7protect items from getting lost",
            "&7while the owners away in the after life.",
            "",
            "&eCost: &e&l$500"
    ));

    public static final Property<Boolean> blacksmith_in_gui = newProperty("Settings.BlackSmith.InGUI", true);

    public static final Property<Integer> blacksmith_slot = newProperty("Settings.BlackSmith.Slot", 24);

    public static final Property<String> blacksmith_item = newProperty("Settings.BlackSmith.Item", "ANVIL");

    public static final Property<Boolean> blacksmith_glowing = newProperty("Settings.BlackSmith.Glowing", false);

    public static final Property<String> blacksmith_name = newProperty("Settings.BlackSmith.Name", "&7&lThe &b&lBlack &9&lSmith");

    public static final Property<List<String>> blacksmith_lore = newListProperty("Settings.BlackSmith.Lore", List.of(
            "&b>>&7&nClick to open&b<<"
    ));

    public static final Property<String> blacksmith_gui_name = newProperty("Settings.BlackSmith.GUIName", "&8&lThe Black Smith");

    public static final Property<String> blacksmith_results_none = newProperty("Settings.BlackSmith.Results.None", "&c&lNo Results");

    public static final Property<List<String>> blacksmith_results_lore = newListProperty("Settings.BlackSmith.Results.Not-Found-Lore", List.of(
            "&7No results could be found.",
            "&7Please put in two books of",
            "&7the same enchantment and level.",
            "&7Or put in two items to combined",
            "&7the enchantments on them."
    ));

    public static final Property<String> blacksmith_results_found = newProperty("Settings.BlackSmith.Results.Found", "&c&lCost: &6&l%cost%XP");

    public static final Property<String> blacksmith_transaction_currency = newProperty("Settings.BlackSmith.Transaction.Currency", "XP_LEVEL");

    public static final Property<Integer> blacksmith_transaction_power_up = newProperty("Settings.BlackSmith.Transaction.Costs.Power-Up", 5);

    public static final Property<Integer> blacksmith_transaction_add_enchantment = newProperty("Settings.BlackSmith.Transaction.Costs.Add-Enchantment", 3);

    public static final Property<Integer> blacksmith_transaction_book_upgrade = newProperty("Settings.BlackSmith.Transaction.Costs.Book-Upgrade", 5);

    public static final Property<Boolean> tinker_in_gui = newProperty("Settings.Tinker.InGUI", true);

    public static final Property<Integer> tinker_slot = newProperty("Settings.Tinker.Slot", 22);

    public static final Property<String> tinker_item = newProperty("Settings.Tinker.Item", "NETHER_STAR");

    public static final Property<Boolean> tinker_glowing = newProperty("Settings.Tinker.Glowing", false);

    public static final Property<String> tinker_name = newProperty("Settings.Tinker.Name", "&7&lThe &b&lCrazy &9&lTinkerer");

    public static final Property<List<String>> tinker_lore = newListProperty("Settings.Tinker.Lore", List.of(
            "&b>>&7&nClick to open&b<<"
    ));

    public static final Property<Boolean> info_in_gui = newProperty("Settings.Info.InGUI", true);

    public static final Property<Integer> info_slot = newProperty("Settings.Info.Slot", 23);

    public static final Property<String> info_item = newProperty("Settings.Info.Item", "COMPASS");

    public static final Property<Boolean> info_glowing = newProperty("Settings.Info.Glowing", true);

    public static final Property<String> info_name = newProperty("Settings.Info.Name", "&eInfo on the Enchantments");

    public static final Property<List<String>> info_lore = newListProperty("Settings.Info.Lore", List.of(
            "&b>>&7&nClick to view&b<<"
    ));

    public static final Property<String> mystery_dust_item = newProperty("Settings.Dust.MysteryDust.Item", "GUNPOWDER");

    public static final Property<String> mystery_dust_name = newProperty("Settings.Dust.MysteryDust.Name", "&7Mystery Dust");

    public static final Property<List<String>> mystery_dust_lore = newListProperty("Settings.Dust.MysteryDust.Lore", List.of(
            "&a1-%percent%% &7will be on a",
            "&aMagical AngelDust &7or &eMagical Fixing &7Dust",
            "&7&l(&6&l!&7&l) &7Right click"
    ));

    public static final Property<Boolean> mystery_dust_firework_toggle = newProperty("Settings.Dust.MysteryDust.Firework.Toggle", true);

    public static final Property<String> mystery_dust_firework_colors = newProperty("Settings.Dust.MysteryDust.Firework.Colors", "Black, Gray, Lime");

    public static final Property<Boolean> mystery_dust_toggle_success = newProperty("Settings.Dust.MysteryDust.Dust-Toggle.Success", true);

    public static final Property<Boolean> mystery_dust_toggle_destroy = newProperty("Settings.Dust.MysteryDust.Dust-Toggle.Destroy", true);

    public static final Property<Boolean> mystery_dust_toggle_failed = newProperty("Settings.Dust.MysteryDust.Dust-Toggle.Failed", true);

    public static final Property<Integer> mystery_dust_percent_range = newProperty("Settings.Dust.MysteryDust.PercentRange.Max", 30);

    public static final Property<Boolean> success_dust_in_gui = newProperty("Settings.Dust.SuccessDust.InGUI", true);

    public static final Property<String> success_dust_gui_name = newProperty("Settings.Dust.SuccessDust.GUIName", "&aMagical Angel Dust");

    public static final Property<List<String>> success_dust_gui_lore = newListProperty("Settings.Dust.SuccessDust.GUILore", List.of(
            "&eCost: &a&l$500",
            "&7Dust allows you to bring",
            "&7up your &aSuccess Rate&7."
    ));

    public static final Property<String> success_dust_item = newProperty("Settings.Dust.SuccessDust.Item", "SUGAR");

    public static final Property<String> success_dust_name = newProperty("Settings.Dust.SuccessDust.Name", "&aMagical Angel Dust");

    public static final Property<List<String>> success_dust_lore = newListProperty("Settings.Dust.SuccessDust.Lore", List.of(
            "&a+%percent%% Success Rate",
            "&7Apply to an Enchantment Book to",
            "&7Increase the Success Rate by &e%percent%%"
    ));

    public static final Property<Integer> success_dust_max_percent_range = newProperty("Settings.Dust.SuccessDust.PercentRange.Max", 30);
    public static final Property<Integer> success_dust_min_percent_range = newProperty("Settings.Dust.SuccessDust.PercentRange.Min", 2);

    public static final Property<Boolean> destroy_dust_in_gui = newProperty("Settings.Dust.DestroyDust.InGUI", true);

    public static final Property<String> destroy_dust_gui_name = newProperty("Settings.Dust.DestroyDust.GUIName", "&eMagical Fixing Dust");

    public static final Property<List<String>> destroy_dust_gui_lore = newListProperty("Settings.Dust.DestroyDust.GUILore", List.of(
            "&eCost: &a&l$500",
            "&7Dust allows you to bring",
            "&7down your &4Destroy Rate&7."
    ));

    public static final Property<String> destroy_dust_item = newProperty("Settings.Dust.DestroyDust.Item", "REDSTONE");

    public static final Property<String> destroy_dust_name = newProperty("Settings.Dust.DestroyDust.Name", "&eMagical Fixing Dust");

    public static final Property<List<String>> destroy_dust_lore = newListProperty("Settings.Dust.DestroyDust.Lore", List.of(
            "&a+%percent%% Success Rate",
            "&7Apply to an Enchantment Book to",
            "&7Increase the Success Rate by &e%percent%%"
    ));

    public static final Property<Integer> destroy_dust_max_percent_range = newProperty("Settings.Dust.DestroyDust.PercentRange.Max", 10);
    public static final Property<Integer> destroy_dust_min_percent_range = newProperty("Settings.Dust.DestroyDust.PercentRange.Min", 2);

    public static final Property<String> failed_dust_item = newProperty("Settings.Dust.FailedDust.Item", "GUNPOWDER");

    public static final Property<String> failed_dust_name = newProperty("Settings.Dust.FailedDust.Name", "&7Failed Dust");

    public static final Property<List<String>> failed_dust_lore = newListProperty("Settings.Dust.FailedDust.Lore", List.of(
            "&7This dust has failed.",
            "&7It is now useless unless you",
            "&7needed to make TNT or a throwable potion."
    ));

    public static final Property<String> black_scroll_item = newProperty("Settings.BlackScroll.Item", "INK_SAC");

    public static final Property<String> black_scroll_name = newProperty("Settings.BlackScroll.Name", "&8&l&nBlack Scroll");

    public static final Property<List<String>> black_scroll_lore = newListProperty("Settings.BlackScroll.Item-Lore", List.of(
            "&7Have a custom enchantment you don''t want?",
            "&7Drag and drop this on an item with a custom enchantment",
            "&7and it will remove a random enchantment for you.",
            "&7&l(&6&l!&7&l) &7Only works on Custom Enchantments."
    ));

    public static final Property<Boolean> black_scroll_chance_toggle = newProperty("Settings.BlackScroll.Chance-Toggle", false);

    public static final Property<Integer> black_scroll_chance = newProperty("Settings.BlackScroll.Chance", 75);

    public static final Property<Boolean> black_scroll_in_gui = newProperty("Settings.BlackScroll.InGUI", true);

    public static final Property<String> black_scroll_gui_name = newProperty("Settings.BlackScroll.GUIName", "&7Black Scroll");

    public static final Property<Boolean> black_scroll_glowing = newProperty("Settings.BlackScroll.Glowing", false);

    public static final Property<Integer> black_scroll_slot = newProperty("Settings.BlackScroll.Slot", 21);

    public static final Property<List<String>> black_scroll_gui_lore = newListProperty("Settings.BlackScroll.Lore", List.of(
            "&eCost: &a&l$1000",
            "&7Black Scrolls allow you to",
            "&7take off random enchantments."
    ));

    public static final Property<Integer> black_scroll_success_chance_max = newProperty("Settings.BlackScroll.SuccessChance.Max", 75);

    public static final Property<Integer> black_scroll_success_chance_min = newProperty("Settings.BlackScroll.SuccessChance.Min", 35);

    public static final Property<Integer> black_scroll_destroy_chance_max = newProperty("Settings.BlackScroll.DestroyChance.Max", 25);

    public static final Property<Integer> black_scroll_destroy_chance_min = newProperty("Settings.BlackScroll.DestroyChance.Min", 5);

    public static final Property<String> white_scroll_item = newProperty("Settings.WhiteScroll.Item", "PAPER");

    public static final Property<String> white_scroll_name = newProperty("Settings.WhiteScroll.Name", "&e&lWhite Scroll");

    public static final Property<List<String>> white_scroll_lore = newListProperty("Settings.WhiteScroll.Item-Lore", List.of(
            "&7Want to protect an item from a book''s destroy rate?",
            "&7Drag and drop this on an enchant-able item",
            "&7and it will protect the item from the destroy rate."
    ));

    public static final Property<Boolean> white_scroll_in_gui = newProperty("Settings.WhiteScroll.InGUI", true);

    public static final Property<String> white_scroll_gui_name = newProperty("Settings.WhiteScroll.GUIName", "&7White Scroll");

    public static final Property<Boolean> white_scroll_glowing = newProperty("Settings.WhiteScroll.Glowing", false);

    public static final Property<Integer> white_scroll_slot = newProperty("Settings.WhiteScroll.Slot", 30);

    public static final Property<List<String>> white_scroll_gui_lore = newListProperty("Settings.WhiteScroll.Lore", List.of(
            "&eCost: &a&l$2000",
            "&7White Scrolls allow you to",
            "&7protect items from breaking."
    ));

    public static final Property<String> white_scroll_protected = newProperty("Settings.WhiteScroll.ProtectedName", "&b&lPROTECTED");

    public static final Property<String> transmog_scroll_item = newProperty("Settings.TransmogScroll.Item", "QUARTZ");

    public static final Property<String> transmog_scroll_name = newProperty("Settings.TransmogScroll.Name", "&d&lTransmog Scroll");

    public static final Property<List<String>> transmog_scroll_lore = newListProperty("Settings.TransmogScroll.Item-Lore", List.of(
            "&7This scroll allows you to organize",
            "&7your enchantments and tell you how many",
            "&7enchantments you currently have on the item."
    ));

    public static final Property<Boolean> transmog_scroll_in_gui = newProperty("Settings.TransmogScroll.InGUI", true);

    public static final Property<String> transmog_scroll_gui_name = newProperty("Settings.TransmogScroll.GUIName", "&dTransmog Scroll");

    public static final Property<Boolean> transmog_scroll_glowing = newProperty("Settings.TransmogScroll.Glowing", false);

    public static final Property<Integer> transmog_scroll_slot = newProperty("Settings.TransmogScroll.Slot", 30);

    public static final Property<List<String>> transmog_scroll_gui_lore = newListProperty("Settings.TransmogScroll.Lore", List.of(
            "&eCost: &a&l$200",
            "&7This scroll allows you to organize",
            "&7your enchantments and tell you how many",
            "&7enchantments you currently have on the item."
    ));

    public static final Property<String> amount_of_enchantments = newProperty("Settings.TransmogScroll.Amount-of-Enchantments", " &7[&6&n%amount%&7]");

    public static final Property<Boolean> amount_toggle = newProperty("Settings.TransmogScroll.Amount-Toggle", true);

    public static final Property<Boolean> count_vanilla_enchantments = newProperty("Settings.TransmogScroll.Count-Vanilla-Enchantments", true);

    public static final Property<List<String>> lore_order = newListProperty("Settings.TransmogScroll.Lore-Order", List.of(
            "CE_Enchantments",
            "Protection",
            "Normal_Lore"
    ));

    public static final Property<Boolean> add_blank_lines = newProperty("Settings.TransmogScroll.Add-Blank-Lines", true);

    public static final Property<Boolean> max_amount_of_enchantments = newProperty("Settings.EnchantmentOptions.MaxAmountOfEnchantmentsToggle", true);

    public static final Property<Boolean> include_vanilla_enchantments = newProperty("Settings.EnchantmentOptions.IncludeVanillaEnchantments", false);

    public static final Property<Boolean> enchant_stacked_items = newProperty("Settings.EnchantmentOptions.Enchant-Stacked-Items", false);

    public static final Property<Integer> max_rage_level = newProperty("Settings.EnchantmentOptions.MaxRageLevel", 4);

    public static final Property<Boolean> break_rage_on_damage = newProperty("Settings.EnchantmentOptions.Break-Rage-On-Damage", true);

    public static final Property<Boolean> rage_boss_Bar = newProperty("Settings.EnchantmentOptions.Rage-Boss-Bar", false);

    public static final Property<Double> rage_increase = newProperty("Settings.EnchantmentOptions.Rage-Increase", 0.1);

    public static final Property<Boolean> right_click_book = newProperty("Settings.EnchantmentOptions.Right-Click-Book-Description", true);

    public static final Property<Boolean> unsafe_enchantments = newProperty("Settings.EnchantmentOptions.Unsafe-Enchantments", true);

    public static final Property<Boolean> blast_full_durability = newProperty("Settings.EnchantmentOptions.Blast-Full-Durability", true);

    public static final Property<Boolean> vein_miner_full_durability = newProperty("Settings.EnchantmentOptions.VeinMiner-Full-Durability", true);

    public static final Property<Boolean> drop_blocks_for_blast = newProperty("Settings.EnchantmentOptions.Drop-Blocks-For-Blast", true);

    public static final Property<Boolean> drop_blocks_for_veinminer = newProperty("Settings.EnchantmentOptions.Drop-Blocks-For-VeinMiner", true);

    public static final Property<Boolean> experience_bug = newProperty("Settings.EnchantmentOptions.EXP-Bug", false);

    public static final Property<Integer> lightning_sound_range = newProperty("Settings.EnchantmentOptions.Lightning-Sound-Range", 100);

    public static final Property<String> ally_mobs_wolf = newProperty("Settings.EnchantmentOptions.Ally-Mobs.Wolf", "&b%player%''s Saberwolf");

    public static final Property<String> ally_mobs_golem = newProperty("Settings.EnchantmentOptions.Ally-Mobs.Iron-Golem", "&6%player%''s Golem");

    public static final Property<String> ally_mobs_zombie = newProperty("Settings.EnchantmentOptions.Ally-Mobs.Zombie", "&2%player%''s Undead");

    public static final Property<String> ally_mobs_endermite = newProperty("Settings.EnchantmentOptions.Ally-Mobs.Endermite", "&5%player%''s Endermite");

    public static final Property<String> ally_mobs_silverfish = newProperty("Settings.EnchantmentOptions.Ally-Mobs.Silverfish", "&7%player%''s Silverfish");

    public static final Property<String> ally_mobs_bee = newProperty("Settings.EnchantmentOptions.Ally-Mobs.Bee", "&e%player%''s Bee");
}