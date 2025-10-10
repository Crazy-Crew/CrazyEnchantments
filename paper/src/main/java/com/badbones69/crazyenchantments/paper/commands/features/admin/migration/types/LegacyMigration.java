package com.badbones69.crazyenchantments.paper.commands.features.admin.migration.types;

import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.utils.ConfigUtils;
import com.badbones69.crazyenchantments.paper.commands.features.admin.migration.interfaces.IEnchantMigration;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LegacyMigration extends IEnchantMigration {

    public LegacyMigration(@NotNull final CommandSender sender) {
        super(sender);
    }

    @Override
    public void run() {
        final List<String> failed = new ArrayList<>();
        final List<String> success = new ArrayList<>();

        try {
            final YamlConfiguration configuration = FileKeys.config.getYamlConfiguration();

            getConfigurationSection(configuration, "Settings").ifPresentOrElse(section -> {
                section.set("Prefix", this.utils.convertLegacy(section.getString("Prefix", "<dark_gray>[<green>CrazyEnchantments<dark_gray>]: </dark_gray>")));

                section.set("EnchantmentBookLore", this.utils.convertLegacy(section.getStringList("EnchantmentBookLore")));

                section.set("LostBook.Name", this.utils.convertLegacy(section.getString("LostBook.Name", "<dark_gray><bold><u>A Lost %category%<dark_gray><bold><u> Book")));

                section.set("LostBook.Lore", this.utils.convertLegacy(ConfigUtils.getStringList(section, List.of(
                        "<gray>This book has been lost for centuries",
                        "<gray>It is said to be an enchantment book from %category%",
                        "<gray>But you must clean it off to find out what kind it is.",
                        "<gray><bold>(<gold><bold>!<gray><bold>) <gray>Right Click to clean off."
                ), "LostBook.Lore")));

                getConfigurationSection(section, "Scrambler").ifPresent(action -> {
                    action.set("Name", this.utils.convertLegacy(action.getString("Name", "<yellow><bold>The Grand Scrambler")));

                    action.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of(
                            "<gray>The <yellow><bold>The Grand Scrambler <gray>will allow",
                            "<gray>you to re-roll the destroy and success rates.",
                            "<gray>Drag and drop it on an enchantment book",
                            "<gray>to get a new destroy and success rate."
                    ), "Lore")));

                    action.set("GUI.Name", this.utils.convertLegacy(action.getString("GUI.Name", "<dark_gray>Rolling the <yellow>Scrambler")));

                    action.set("GUI.Pointer.Name", this.utils.convertLegacy(action.getString("GUI.Pointer.Name", "<red><bold>Pointer")));

                    action.set("GUI.Pointer.Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of(
                            "<gray>Whatever percents the item lands on",
                            "<gray>will be the new percents on your book."
                    ), "GUI.Pointer.Lore")));

                    action.set("GUIName", this.utils.convertLegacy(action.getString("GUIName", "<yellow><bold>The Grand Scrambler")));

                    action.set("GUILore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of(
                            "<gray>The <yellow><bold>The Grand Scrambler <gray>will allow",
                            "<gray>you to re-roll the destroy and success rates.",
                            "<gray>Drag and drop it on an enchantment book",
                            "<gray>to get a new destroy and success rate.",
                            "",
                            "<yellow>Cost: <yellow><bold>$800"
                    ), "GUILore")));
                });

                getConfigurationSection(section, "Slot_Crystal").ifPresent(action -> {
                    action.set("Name", this.utils.convertLegacy(action.getString("Name", "<dark_purple><bold>Slot <aqua><bold>Crystal")));

                    action.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of(
                            "<gray>A rare crystal that is said to",
                            "<gray>increase the amount of enchants",
                            "<gray>that can be added onto an item.",
                            "",
                            "<gray><bold>(<gold><bold>!<gray><bold>) <gray>Drag and drop on an item."
                    ), "Lore")));

                    action.set("GUIName", this.utils.convertLegacy(action.getString("GUIName", "<dark_purple><bold>Slot <aqua><bold>Crystal")));

                    action.set("GUILore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of(
                            "<gray>A rare crystal that is said to",
                            "<gray>increase the amount of enchants",
                            "<gray>that can be added onto an item.",
                            "",
                            "<yellow>Cost: <yellow><bold>$500"
                    ), "GUILore")));
                });

                getConfigurationSection(section, "GKitz").ifPresent(action -> {
                    action.set("Name", this.utils.convertLegacy(action.getString("Name", "<red><bold>GKitz")));

                    action.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of("<aqua>>><gray><u>Click to open<aqua><<"), "Lore")));
                });

                getConfigurationSection(section, "ProtectionCrystal").ifPresent(action -> {
                    action.set("Name", this.utils.convertLegacy(action.getString("Name", "<dark_purple><bold>Protection <aqua><bold>Crystal")));

                    action.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of(
                            "<gray>A rare crystal that is said to",
                            "<gray>protect items from getting lost",
                            "<gray>while the owners away in the after life.",
                            "",
                            "<gray><bold>(<gold><bold>!<gray><bold>) <gray>Drag and drop on an item."
                    ), "Lore")));

                    action.set("GUIName", this.utils.convertLegacy(action.getString("GUIName", "<dark_purple><bold>Protection <aqua><bold>Crystal")));

                    action.set("GUILore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of(
                            "<gray>A rare crystal that is said to",
                            "<gray>protect items from getting lost",
                            "<gray>while the owners away in the after life.",
                            "",
                            "<yellow>Cost: <yellow><bold>$500"
                    ), "GUILore")));
                });

                getConfigurationSection(section, "BlackSmith").ifPresent(action -> {
                    action.set("Name", this.utils.convertLegacy(action.getString("Name", "<gray><bold>The <aqua><bold>Black <blue><bold>Smith")));

                    action.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of(
                            "<aqua>>><gray><u>Click to open<aqua><<"
                    ), "Name")));

                    action.set("GUIName", this.utils.convertLegacy(action.getString("GUIName", "<dark_gray><bold>The Black Smith")));

                    action.set("Results.None", this.utils.convertLegacy(action.getString("Results.None", "<red><bold>No Results")));

                    action.set("Results.Not-Found-Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of(
                            "<gray>No results could be found.",
                            "<gray>Please put in two books of",
                            "<gray>the same enchantment and level.",
                            "<gray>Or put in two items to combined",
                            "<gray>the enchantments on them."
                    ), "Not-Found-Lore")));

                    action.set("Results.Found", this.utils.convertLegacy(action.getString("Results.Found", "<red><bold>Cost: <gold><bold>%cost%XP")));
                });

                getConfigurationSection(section, "Tinker").ifPresent(action -> {
                    action.set("Name", this.utils.convertLegacy(action.getString("Name", "<gray><bold>The <aqua><bold>Crazy <blue><bold>Tinkerer")));

                    action.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of("<aqua>>><gray><u>Click to open<aqua><<"), "Lore")));
                });

                getConfigurationSection(section, "Info").ifPresent(action -> {
                    action.set("Name", this.utils.convertLegacy(action.getString("Name", "<yellow>Info on the Enchantments")));

                    action.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of("<aqua>>><gray><u>Click to view<aqua><<"), "Lore")));
                });

                getConfigurationSection(section, "Dust").ifPresent(action -> {
                    getConfigurationSection(action, "MysteryDust").ifPresent(dust -> {
                        dust.set("Name", this.utils.convertLegacy(dust.getString("Name", "<gray>Mystery Dust")));

                        dust.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(dust, List.of(
                                "<green>1-%percent%% <gray>will be on a",
                                "<green>Magical AngelDust <gray>or <yellow>Magical Fixing <gray>Dust",
                                "<gray><bold>(<gold><bold>!<gray><bold>) <gray>Right click"
                        ), "Lore")));
                    });

                    getConfigurationSection(action, "SuccessDust").ifPresent(dust -> {
                        dust.set("Name", this.utils.convertLegacy(dust.getString("Name", "<green>Magical Angel Dust")));

                        dust.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(dust, List.of(
                                "<green>+%percent%% Success Rate",
                                "<gray>Apply to an Enchantment Book to",
                                "<gray>Increase the Success Rate by <yellow>%percent%%"
                        ), "Lore")));

                        dust.set("GUIName", this.utils.convertLegacy(dust.getString("GUIName", "<green>Magical Angel Dust")));

                        dust.set("GUILore", this.utils.convertLegacy(ConfigUtils.getStringList(dust, List.of(
                                "<yellow>Cost: <green><bold>$500",
                                "<gray>Dust allows you to bring",
                                "<gray>up your <green>Success Rate<gray>."
                        ), "GUILore")));
                    });

                    getConfigurationSection(action, "DestroyDust").ifPresent(dust -> {
                        dust.set("Name", this.utils.convertLegacy(dust.getString("Name", "<yellow>Magical Fixing Dust")));

                        dust.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(dust, List.of(
                                "<red>-%percent%% Destroy Rate",
                                "<gray>Apply to an Enchantment Book to",
                                "<gray>Decrease the Destroy Rate by <yellow>%percent%%"
                        ), "Lore")));

                        dust.set("GUIName", this.utils.convertLegacy(dust.getString("GUIName", "<yellow>Magical Fixing Dust")));

                        dust.set("GUILore", this.utils.convertLegacy(ConfigUtils.getStringList(dust, List.of(
                                "<yellow>Cost: <green><bold>$500",
                                "<gray>Dust allows you to bring",
                                "<gray>down your <dark_red>Destroy Rate<gray>."
                        ), "GUILore")));
                    });

                    getConfigurationSection(action, "FailedDust").ifPresent(dust -> {
                        dust.set("Name", this.utils.convertLegacy(dust.getString("Name", "<gray>Failed Dust")));

                        dust.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(dust, List.of(
                                "<gray>This dust has failed.",
                                "<gray>It is now useless unless you",
                                "<gray>needed to make TNT or a throwable potion."
                        ), "Lore")));
                    });
                });

                getConfigurationSection(section, "BlackScroll").ifPresent(action -> {
                    action.set("Name", this.utils.convertLegacy(action.getString("Name", "<dark_gray><bold><u>Black Scroll")));

                    action.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of(
                            "<yellow>Cost: <green><bold>$1000",
                            "<gray>Black Scrolls allow you to",
                            "<gray>take off random enchantments."
                    ), "Lore")));

                    action.set("GUIName", this.utils.convertLegacy(action.getString("Name", "<gray>Black Scroll")));

                    action.set("Item-Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of(
                            "<gray>Have a custom enchantment you don''t want?",
                            "<gray>Drag and drop this on an item with a custom enchantment",
                            "<gray>and it will remove a random enchantment for you.",
                            "<gray><bold>(<gold><bold>!<gray><bold>) <gray>Only works on Custom Enchantments."
                    ), "Item-Lore")));
                });

                getConfigurationSection(section, "WhiteScroll").ifPresent(action -> {
                    action.set("Name", this.utils.convertLegacy(action.getString("Name", "<yellow><bold>White Scroll")));

                    action.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of(
                            "<yellow>Cost: <green><bold>$2000",
                            "<gray>White Scrolls allow you to",
                            "<gray>protect items from breaking."
                    ), "Lore")));

                    action.set("GUIName", this.utils.convertLegacy(action.getString("Name", "<gray>White Scroll")));

                    action.set("Item-Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of(
                            "<gray>Want to protect an item from a book''s destroy rate?",
                            "<gray>Drag and drop this on an enchant-able item",
                            "<gray>and it will protect the item from the destroy rate."
                    ), "Item-Lore")));

                    action.set("ProtectedName", this.utils.convertLegacy(action.getString("ProtectedName", "aqua><bold>PROTECTED")));
                });

                getConfigurationSection(section, "TransmogScroll").ifPresent(action -> {
                    action.set("Name", this.utils.convertLegacy(action.getString("Name", "<light_purple><bold>Transmog Scroll")));

                    action.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of(
                            "<yellow>Cost: <green><bold>$200",
                            "<gray>This scroll allows you to organize",
                            "<gray>your enchantments and tell you how many",
                            "<gray>enchantments you currently have on the item."
                    ), "Lore")));

                    action.set("GUIName", this.utils.convertLegacy(action.getString("Name", "<light_purple>Transmog Scroll")));

                    action.set("Item-Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, List.of(
                            "<gray>This scroll allows you to organize",
                            "<gray>your enchantments and tell you how many",
                            "<gray>enchantments you currently have on the item."
                    ), "Item-Lore")));

                    action.set("Amount-of-Enchantments", this.utils.convertLegacy(action.getString("Amount-of-Enchantments", " <gray>[<gold><u>%amount%<gray>]")));
                });

                getConfigurationSection(section, "EnchantmentOptions").flatMap(action -> getConfigurationSection(action, "Ally-Mobs")).ifPresent(mob -> {
                    mob.set("Wolf", this.utils.convertLegacy(mob.getString("Wolf", "<aqua>%player%''s Saberwolf")));
                    mob.set("Iron-Golem", this.utils.convertLegacy(mob.getString("Iron-Golem", "<gold>%player%''s Golem")));
                    mob.set("Zombie", this.utils.convertLegacy(mob.getString("Zombie", "<dark_green>%player%''s Undead")));
                    mob.set("Endermite", this.utils.convertLegacy(mob.getString("Endermite", "<dark_purple>%player%''s Endermite")));
                    mob.set("Silverfish", this.utils.convertLegacy(mob.getString("Silverfish", "<gray>%player%''s Silverfish")));
                    mob.set("Bee", this.utils.convertLegacy(mob.getString("Bee", "<yellow>%player%''s Bee")));
                });

                final List<String> lines = ConfigUtils.getStringList(section, List.of(
                        "Slot:1, Item:BLACK_STAINED_GLASS_PANE, Name: ",
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
                        "Slot:40, Item:WHITE_STAINED_GLASS_PANE, Name: ",
                        "Slot:41, Item:WHITE_STAINED_GLASS_PANE, Name: ",
                        "Slot:42, Item:WHITE_STAINED_GLASS_PANE, Name: ",
                        "Slot:43, Item:WHITE_STAINED_GLASS_PANE, Name: ",
                        "Slot:44, Item:WHITE_STAINED_GLASS_PANE, Name: "
                ), "GUICustomization");

                final List<String> newLines = new ArrayList<>();

                for (String line : lines) {
                    if (line.isEmpty()) {
                        newLines.add(line);

                        continue;
                    }

                    newLines.add(this.utils.convertLegacy(line));
                }

                section.set("GUICustomization", newLines);

                getConfigurationSection(section, "Categories").ifPresent(action -> {
                    for (final String name : action.getKeys(false)) {
                        getConfigurationSection(action, name).ifPresent(category -> {
                            category.set("Name", this.utils.convertLegacy(category.getString("Name", "N/A")));

                            category.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(category, List.of(
                                    "<yellow>Cost: <gold><bold>%level% <gold>XP Levels"
                            ), "Lore")));

                            getConfigurationSection(category, "LostBook").ifPresent(lostbook -> {
                                lostbook.set("Name", this.utils.convertLegacy(lostbook.getString("Name", "")));

                                category.set("Lore", this.utils.convertLegacy(ConfigUtils.getStringList(category, "Lore")));
                            });
                        });
                    }
                });
            }, () -> failed.add("<red>⤷ config.yml"));

            FileKeys.config.save();

            if (!failed.contains("<red>⤷ config.yml")) {
                success.add("<green>⤷ config.yml");
            }
        } catch (final Exception exception) {
            failed.add("<red>⤷ config.yml");
        }

        try {
            final YamlConfiguration configuration = FileKeys.enchantment_types.getYamlConfiguration();

            getConfigurationSection(configuration, "Info-GUI-Settings").ifPresent(action -> {
                action.set("Back-Item.Name", this.utils.convertLegacy(action.getString("Back-Item.Name", "<gray><bold><<<aqua><bold>Back")));

                action.set("Back-Item.Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, "Back-Item.Name")));

                action.set("Left-Item.Name", this.utils.convertLegacy(action.getString("Left-Item.Name", "<aqua><bold>Back<gray><bold>>>")));

                action.set("Left-Item.Lore", this.utils.convertLegacy(ConfigUtils.getStringList(action, "Left-Item.Name")));
            });

            getConfigurationSection(configuration, "Types").ifPresent(action -> {
                for (final String type : action.getKeys(false)) {
                    getConfigurationSection(action, type).ifPresent(value -> {
                        String displayName = "";
                        List<String> displayLore = List.of();

                        switch (type.toLowerCase()) {
                            case "helmet" -> {
                                displayName = "<yellow><bold>Helmet Enchantments";
                                displayLore = List.of(
                                        "<gray>Enchantable Items:",
                                        "<green>- Leather Helmet",
                                        "<green>- Chain Helmet",
                                        "<green>- Gold Helmet",
                                        "<green>- Iron Helmet",
                                        "<green>- Diamond Helmet",
                                        "<green>- Netherite Helmet",
                                        "<green>- Turtle Helmet"
                                );
                            }

                            case "boots" -> {
                                displayName = "<yellow><bold>Boot Enchantments";
                                displayLore = List.of(
                                        "<gray>Enchantable Items:",
                                        "<green>- Leather Boots",
                                        "<green>- Chain Boots",
                                        "<green>- Gold Boots",
                                        "<green>- Iron Boots",
                                        "<green>- Diamond Boots",
                                        "<green>- Netherite Boots"
                                );
                            }

                            case "armor" -> {
                                displayName = "<yellow><bold>Armor Enchantments";
                                displayLore = List.of(
                                        "<gray>Enchantable Items:",
                                        "<green>- Leather Armor",
                                        "<green>- Chain Armor",
                                        "<green>- Gold Armor",
                                        "<green>- Iron Armor",
                                        "<green>- Diamond Armor",
                                        "<green>- Netherite Armor"
                                );
                            }

                            case "bow" -> {
                                displayName = "<yellow><bold>Bow Enchantments";
                                displayLore = List.of(
                                        "<gray>Enchantable Items:",
                                        "<green>- Bow"
                                );
                            }

                            case "sword" -> {
                                displayName = "<yellow><bold>Sword Enchantments";
                                displayLore = List.of(
                                        "<gray>Enchantable Items:",
                                        "<green>- Wooden Sword",
                                        "<green>- Stone Sword",
                                        "<green>- Gold Sword",
                                        "<green>- Iron Sword",
                                        "<green>- Diamond Sword",
                                        "<green>- Netherite Sword"
                                );
                            }

                            case "axe" -> {
                                displayName = "<yellow><bold>Axe Enchantments";
                                displayLore = List.of(
                                        "<gray>Enchantable Items:",
                                        "<green>- Wooden Axe",
                                        "<green>- Stone Axe",
                                        "<green>- Gold Axe",
                                        "<green>- Iron Axe",
                                        "<green>- Diamond Axe",
                                        "<green>- Netherite Axe"
                                );
                            }

                            case "hoe" -> {
                                displayName = "<yellow><bold>Hoe Enchantments";
                                displayLore = List.of(
                                        "<gray>Enchantable Items:",
                                        "<green>- Wooden Hoe",
                                        "<green>- Stone Hoe",
                                        "<green>- Gold Hoe",
                                        "<green>- Iron Hoe",
                                        "<green>- Diamond Hoe",
                                        "<green>- Netherite Hoe"
                                );
                            }

                            case "pickaxe" -> {
                                displayName = "<yellow><bold>Pickaxe Enchantments";
                                displayLore = List.of(
                                        "<gray>Enchantable Items:",
                                        "<green>- Wooden Pickaxe",
                                        "<green>- Stone Pickaxe",
                                        "<green>- Gold Pickaxe",
                                        "<green>- Iron Pickaxe",
                                        "<green>- Diamond Pickaxe",
                                        "<green>- Netherite Pickaxe"
                                );
                            }

                            case "tool" -> {
                                displayName = "<yellow><bold>Tool Enchantments";
                                displayLore = List.of(
                                        "<gray>Enchantable Items:",
                                        "<green>- All Pickaxes",
                                        "<green>- All Axes",
                                        "<green>- All Shovels",
                                        "<green>- All Hoes",
                                        "<green>- Shears"
                                );
                            }
                            
                            case "damaged-items" -> {
                                displayName = "<yellow><bold>Damaged Item Enchantments";
                                displayLore = List.of(
                                        "<gray>Enchantable Items:",
                                        "<green>- Shears",
                                        "<green>- Fishing Rods",
                                        "<green>- All Tools",
                                        "<green>- All Armor",
                                        "<green>- All Weapons"
                                );
                            }
                        }

                        value.set("Display-Item.Name", this.utils.convertLegacy(value.getString("Display-Item.Name", displayName)));

                        value.set("Display-Item.Lore", this.utils.convertLegacy(ConfigUtils.getStringList(value, displayLore, "Display-Item.Lore")));
                    });
                }
            });

            FileKeys.enchantment_types.save();

            success.add("<green>⤷ Enchantment-Types.yml");
        } catch (final Exception exception) {
            failed.add("<red>⤷ Enchantment-Types.yml");
        }

        try {
            final YamlConfiguration configuration = FileKeys.tinker.getYamlConfiguration();

            getConfigurationSection(configuration, "Settings").ifPresentOrElse(section -> {
                section.set("GUIName", this.utils.convertLegacy(section.getString("GUIName", "<gray><b>The <dark_red><b>Crazy <red><b>Tinkerer")));

                section.set("TradeButton", this.utils.convertLegacy(section.getString("TradeButton", "<yellow>Click to accept the trade")));

                section.set("TradeButton-Lore", this.utils.convertLegacy(ConfigUtils.getStringList(section, List.of(), "TradeButton-Lore")));

                section.set("BottleOptions.Name", this.utils.convertLegacy(section.getString("BottleOptions.Name", "<green>Recycled XP")));

                section.set("BottleOptions.Lore", this.utils.convertLegacy(ConfigUtils.getStringList(section, List.of(
                        "<green><b>Recycled <gold>%total%XP",
                        "<gray><b>(<gold><b>!<gray><b>) <gray>Throw to get XP."
                ), "BottleOptions.Lore")));

            }, () -> failed.add("<red>⤷ Tinker.yml"));

            FileKeys.tinker.save();

            if (!failed.contains("<red>⤷ Tinker.yml")) {
                success.add("<green>⤷ Tinker.yml");
            }
        } catch (final Exception exception) {
            failed.add("<red>⤷ Tinker.yml");
        }

        try {
            final YamlConfiguration configuration = FileKeys.enchantments.getYamlConfiguration();

            getConfigurationSection(configuration, "Enchantments").ifPresentOrElse(section -> {
                for (final String key : section.getKeys(false)) {
                    getConfigurationSection(section, key).ifPresent(enchant -> {
                        enchant.set("Name", this.utils.convertLegacy(enchant.getString("Name", "<gray>%s".formatted(key))));

                        enchant.set("Info.Name", this.utils.convertLegacy(enchant.getString("Info.Name", "<yellow><bold>%s <gray>(<aqua>I<gray>)".formatted(key))));

                        enchant.set("Info.Description", this.utils.convertLegacy(ConfigUtils.getStringList(enchant, "Info.Description")));
                    });
                }
            }, () -> failed.add("<red>⤷ Enchantments.yml"));

            if (!failed.contains("<red>⤷ Enchantments.yml")) {
                success.add("<green>⤷ Enchantments.yml");
            }
        } catch (final Exception exception) {
            failed.add("<red>⤷ Enchantments.yml");
        }

        try {
            final YamlConfiguration configuration = FileKeys.messages.getYamlConfiguration();

            getConfigurationSection(configuration, "Messages").ifPresentOrElse(section -> {
                getConfigurationSection(section, "Show-Enchants-Format").ifPresent(message -> {
                    message.set("Main", this.utils.convertLegacy(message.getString("Main", "%item% %itemEnchants%")));

                    message.set("Base", this.utils.convertLegacy(message.getString("Base", "<dark_green>%enchant%<gray>: <gold>%level% ")));
                });

                getConfigurationSection(section, "Enchantment-Upgrade").ifPresent(message -> {
                    message.set("Success", this.utils.convertLegacy(message.getString("Success", "<gray>You have upgraded <gold>%enchantment%<gray> to level <gold>%level%<gray>.")));
                    message.set("Destroyed", this.utils.convertLegacy(message.getString("Destroyed", "<red>Your upgrade failed and the lower level enchantment was lost.")));
                    message.set("Failed", this.utils.convertLegacy(message.getString("Failed", "<red>The book failed to upgrade to the item.")));
                });

                getConfigurationSection(section, "Rage").ifPresent(message -> {
                    message.set("Building", this.utils.convertLegacy(message.getString("Building", "<gray>[<red><bold>Rage<gray>]: <green>Keep it up, your rage is building.")));
                    message.set("Cooled-Down", this.utils.convertLegacy(message.getString("Cooled-Down", "<gray>[<red><bold>Rage<gray>]: <red>Your Rage has cooled down.")));
                    message.set("Rage-Up", this.utils.convertLegacy(message.getString("Rage-Up", "<gray>[<red><bold>Rage<gray>]: <gray>You are now doing <green>%level%x <gray>Damage.")));
                    message.set("Damaged", this.utils.convertLegacy(message.getString("Damaged", "<gray>[<red><bold>Rage<gray>]: <red>You have been hurt and it broke your Rage Multiplier!")));
                });

                for (final String key : section.getKeys(false)) {
                    if (section.isList(key)) {
                        section.set(key, this.utils.convertLegacy(ConfigUtils.getStringList(section, key)));

                        continue;
                    }

                    section.set(key, this.utils.convertLegacy(section.getString(key, "")));
                }
            }, () -> failed.add("<red>⤷ Messages.yml"));

            FileKeys.messages.save();

            if (!failed.contains("<red>⤷ Messages.yml")) {
                success.add("<green>⤷ Messages.yml");
            }
        } catch (final Exception exception) {
            failed.add("<red>⤷ Messages.yml");
        }

        try {
            final YamlConfiguration configuration = FileKeys.gkitz.getYamlConfiguration();

            getConfigurationSection(configuration, "Settings").ifPresent(section -> {
                section.set("Inventory-Name", this.utils.convertLegacy(section.getString("Inventory-Name", "<dark_gray>List of all GKitz")));

                section.set("GUI-Customization", this.utils.convertLegacy(ConfigUtils.getStringList(section, List.of(
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
                        "Slot:11, Item:GRAY_STAINED_GLASS_PANE, Name: ",
                        "Slot:13, Item:GRAY_STAINED_GLASS_PANE, Name: ",
                        "Slot:15, Item:GRAY_STAINED_GLASS_PANE, Name: ",
                        "Slot:17, Item:GRAY_STAINED_GLASS_PANE, Name: ",
                        "Slot:18, Item:BLACK_STAINED_GLASS_PANE, Name: ",
                        "Slot:19, Item:BLACK_STAINED_GLASS_PANE, Name: ",
                        "Slot:20, Item:BLACK_STAINED_GLASS_PANE, Name: ",
                        "Slot:21, Item:BLACK_STAINED_GLASS_PANE, Name: ",
                        "Slot:22, Item:BLACK_STAINED_GLASS_PANE, Name: ",
                        "Slot:23, Item:BLACK_STAINED_GLASS_PANE, Name: ",
                        "Slot:24, Item:BLACK_STAINED_GLASS_PANE, Name: ",
                        "Slot:25, Item:BLACK_STAINED_GLASS_PANE, Name: ",
                        "Slot:26, Item:BLACK_STAINED_GLASS_PANE, Name: ",
                        "Slot:27, Item:BLACK_STAINED_GLASS_PANE, Name: "
                ), "GUI-Customization")));
            });

            getConfigurationSection(configuration, "GKitz").ifPresent(section -> {
                for (final String kitName : section.getKeys(false)) {
                    getConfigurationSection(section, kitName).ifPresent(kit -> {
                        kit.set("Name", this.utils.convertLegacy(kit.getString("Name", "<gold><bold>%s Kit".formatted(kitName))));

                        kit.set("Lore", this.utils.convertLegacy(kit.getStringList("Lore")));

                        kit.set("Fake-Items", this.utils.convertLegacy(kit.getStringList("Fake-Items")));
                        kit.set("Items", this.utils.convertLegacy(kit.getStringList("Items")));
                    });
                }
            });

            if (!failed.contains("<red>⤷ GKitz.yml")) {
                success.add("<green>⤷ GKitz.yml");
            }
        } catch (final Exception exception) {
            failed.add("<red>⤷ GKitz.yml");
        }
    }

    public @NotNull final Optional<ConfigurationSection> getConfigurationSection(@NotNull final ConfigurationSection section, @NotNull final String path) {
        ConfigurationSection newSection = section.getConfigurationSection(path);

        if (newSection == null) {
            newSection = section.createSection(path);
        }

        return Optional.of(newSection);
    }
}