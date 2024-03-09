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
}