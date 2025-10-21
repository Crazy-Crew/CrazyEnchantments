package com.badbones69.crazyenchantments.registry;

import com.badbones69.crazyenchantments.objects.Message;
import com.ryderbelserion.fusion.core.FusionCore;
import com.ryderbelserion.fusion.core.FusionProvider;
import com.ryderbelserion.fusion.core.files.FileManager;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazyenchantments.ICrazyEnchantments;
import us.crazycrew.crazyenchantments.ICrazyProvider;
import us.crazycrew.crazyenchantments.constants.MessageKeys;
import us.crazycrew.crazyenchantments.interfaces.IMessage;
import us.crazycrew.crazyenchantments.interfaces.registry.IMessageRegistry;
import java.nio.file.Path;
import java.util.*;

public class MessageRegistry implements IMessageRegistry {

    private final FusionCore fusion = FusionProvider.getInstance();

    private final Path path = this.fusion.getDataPath();

    private final FileManager fileManager = this.fusion.getFileManager();

    private final StringUtils utils = this.fusion.getStringUtils();

    private final Map<Key, Map<Key, IMessage>> messages = new HashMap<>(); // locale key, (message key, message context)

    @Override
    public void addMessage(@NotNull final Key locale, @NotNull final Key key, @NotNull final IMessage message) {
        this.fusion.log("info", "Registering the message @ {} for {}", locale.asString(), key.asString());

        final Map<Key, IMessage> keys = this.messages.getOrDefault(locale, new HashMap<>());

        keys.put(key, message);

        this.messages.put(locale, keys);
    }

    @Override
    public void removeMessage(@NotNull final Key key) {
        if (!this.messages.containsKey(key)) {
            this.fusion.log("warn", "No message with key {}", key.asString());

            return;
        }

        this.fusion.log("info", "Unregistering the message {}", key.asString());

        this.messages.remove(key);
    }

    @Override
    public @NotNull final IMessage getMessage(@NotNull final Key locale, @NotNull final Key key) {
        return this.messages.getOrDefault(locale, this.messages.get(MessageKeys.default_locale)).get(key);
    }

    @Override
    public @NotNull final IMessage getMessage(@NotNull final Key key) { // only used for console command sender
        return this.messages.get(MessageKeys.default_locale).get(key);
    }

    public @NotNull final Map<Key, Map<Key, IMessage>> getMessages() {
        return Collections.unmodifiableMap(this.messages);
    }

    @Override
    public void init() {
        this.messages.clear();

        final List<Path> paths = new ArrayList<>(this.fusion.getFiles(this.path.resolve("locale"), ".yml"));

        paths.add(this.path.resolve("Messages.yml")); // add to list

        for (final Path path : paths) {
            this.fileManager.getYamlFile(path).ifPresentOrElse(file -> {
                final String fileName = file.getFileName();

                final Key key = Key.key(ICrazyEnchantments.namespace, fileName.equalsIgnoreCase("messages.yml") ? "default" : fileName.toLowerCase());
                
                final CommentedConfigurationNode configuration = file.getConfiguration();
                
                addMessage(key, MessageKeys.correct_usage, new Message(configuration, path, "<red>The correct usage for this command is {usage}.", "Messages", "Correct-Usage"));
                addMessage(key, MessageKeys.players_only, new Message(configuration, path, "<red>Only players can use this command.", "Messages", "Players-Only"));
                addMessage(key, MessageKeys.no_permission, new Message(configuration, path, "<red>You do not have permission to use that command!", "Messages", "No-Perm"));
                addMessage(key, MessageKeys.console_only, new Message(configuration, path, "<red>Only console can use this command.", "Messages", "Console-Only"));
                addMessage(key, MessageKeys.unknown_command, new Message(configuration, path, "<red>The command {command} is not known.", "Messages", "Unknown-Command"));
                addMessage(key, MessageKeys.config_reload, new Message(configuration, path, "<gray>You have reloaded the Config.yml", "Messages", "Config-Reload"));
                addMessage(key, MessageKeys.need_to_unstack_item, new Message(configuration, path, "<red>You need to unstack that item before you can use it.", "Messages", "Need-To-UnStack-Item"));
                addMessage(key, MessageKeys.not_an_enchantment, new Message(configuration, path, "<red>That is not an enchantment.", "Messages", "Not-An-Enchantment"));
                addMessage(key, MessageKeys.right_click_black_scroll, new Message(configuration, path, "<gray>Black scrolls will remove a random enchantment from your item.", "Messages", "Right-Click-Black-Scroll"));
                addMessage(key, MessageKeys.black_scroll_unsuccessful, new Message(configuration, path, "<red>The black scroll was unsuccessful. Please try again with another one.", "Messages", "Black-Scroll-Unsuccessful"));
                addMessage(key, MessageKeys.need_more_xp_lvls, new Message(configuration, path, "<red>You need <gold>{xp} <red>more xp level.", "Messages", "Need-More-XP-Lvls"));
                addMessage(key, MessageKeys.need_more_total_xp, new Message(configuration, path, "<red>You need <gold>{xp} <red>more total xp.", "Messages", "Need-More-Total-XP"));
                addMessage(key, MessageKeys.need_more_money, new Message(configuration, path, "<red>You are in need of <green>${money_needed}<red>.", "Messages", "Need-More-Money"));
                addMessage(key, MessageKeys.hit_enchantment_max, new Message(configuration, path, this.utils.toString(List.of(
                        "<#ff0000>That item already has the maximum amount of enchantments that you can add to it.",
                        "<#880808>Use <dark_green>/ce limit <#880808>to check the current limit on the item.",
                        "<#880808>For information on how to change the limit, read <dark_green>https://docs.crazycrew.us/docs/plugins/crazyenchantments/faq."
                )), "Messages", "Hit-Enchantment-Max"));
                addMessage(key, MessageKeys.conflicting_enchant, new Message(configuration, path, "<gray>This enchant conflicts with one already on the item.", "Messages", "Conflicting-Enchant"));
                addMessage(key, MessageKeys.hit_slot_max, new Message(configuration, path, "<red>You have already added the maximum amount of slots to this item.", "Messages", "Hit-Slot-Max"));
                addMessage(key, MessageKeys.applied_slot_crystal, new Message(configuration, path, "<red>You have successfully added another slot to the item. The item now has {slot} extra slots.", "Messages", "Applied-Slot-Crystal"));
                addMessage(key, MessageKeys.inventory_full, new Message(configuration, path, "<red>Your inventory is too full. Please open up some space to buy that.", "Messages", "Inventory-Full"));
                addMessage(key, MessageKeys.tinker_inventory_full, new Message(configuration, path, "<red>The inventory is full. Sell all or remove items.", "Messages", "Tinker-Inventory-Full"));
                addMessage(key, MessageKeys.need_to_use_player_inventory, new Message(configuration, path, "<red>You can only use that in your player inventory.", "Messages", "Need-To-Use-Player-Inventory"));
                addMessage(key, MessageKeys.tinker_sold_msg, new Message(configuration, path, "<gray>Thank you for trading at <gray><bold>The <dark_red><bold>Crazy <red><bold>Tinkerer<gray>.", "Messages", "Tinker-Sold-Msg"));
                addMessage(key, MessageKeys.not_online, new Message(configuration, path, "<red>That player is not online.", "Messages", "Not-Online"));
                addMessage(key, MessageKeys.remove_enchantment, new Message(configuration, path, "<gray>You have removed the enchantment <green>{enchantment} <gray>from this item.", "Messages", "Remove-Enchantment"));
                addMessage(key, MessageKeys.doesnt_have_enchantment, new Message(configuration, path, "<red>Your item does not contain the enchantment <gold>{enchantment}<red>.", "Messages", "Doesnt-Have-Enchantment"));
                addMessage(key, MessageKeys.doesnt_have_item_in_hand, new Message(configuration, path, "<red>You must have an item in your hand.", "Messages", "Doesnt-Have-Item-In-Hand"));
                addMessage(key, MessageKeys.not_a_number, new Message(configuration, path, "<red>{arg} is not a number.", "Messages", "Not-A-Number"));
                addMessage(key, MessageKeys.get_success_dust, new Message(configuration, path, "<gray>You have gained <green>{amount} <gray>Success Dust.", "Messages", "Get-Success-Dust"));
                addMessage(key, MessageKeys.give_success_dust, new Message(configuration, path, "<gray>You have given <green>{amount} <gray>Success Dust to <gold>{player}<gray>.", "Messages", "Give-Success-Dust"));
                addMessage(key, MessageKeys.get_destroy_dust, new Message(configuration, path, "<gray>You have gained <green>{amount} <gray>Destroy Dust.", "Messages", "Get-Destroy-Dust"));
                addMessage(key, MessageKeys.give_destroy_dust, new Message(configuration, path, "<gray>You have given <green>{amount} <gray>Destroy Dust to <gold>{player}<gray>.", "Messages", "Give-Destroy-Dust"));
                addMessage(key, MessageKeys.get_mystery_dust, new Message(configuration, path, "<gray>You have gained <green>{amount} <gray>Mystery Dust.", "Messages", "Get-Mystery-Dust"));
                addMessage(key, MessageKeys.give_mystery_dust, new Message(configuration, path, "<gray>You have given <green>{amount} <gray>Mystery Dust to <gold>{player}<gray>.", "Messages", "Give-Mystery-Dust"));
                addMessage(key, MessageKeys.not_a_category, new Message(configuration, path, "<gold>{category} <red>is not a category.", "Messages", "Not-A-Category"));
                addMessage(key, MessageKeys.clean_lost_book, new Message(configuration, path, "<gray>You have cleaned a lost book and found {found}<gray>.", "Messages", "Clean-Lost-Book"));
                addMessage(key, MessageKeys.book_works, new Message(configuration, path, "<green>Your item loved this book and accepted it.", "Messages", "Book-Works"));
                addMessage(key, MessageKeys.book_failed, new Message(configuration, path, "<red>Your item must not have liked that enchantment.", "Messages", "Book-Failed"));
                addMessage(key, MessageKeys.item_destroyed, new Message(configuration, path, "<red>Oh no the destroy rate was too much for the item.", "Messages", "Item-Destroyed"));
                addMessage(key, MessageKeys.item_was_protected, new Message(configuration, path, "<red>Luckily your item was blessed with Divine Protection and did not break.", "Messages", "Item-Was-Protected"));
                addMessage(key, MessageKeys.player_is_in_creative_mode, new Message(configuration, path, "<red>You are in creative mode. You need to get out of Creative Mode!", "Messages", "Player-Is-In-Creative-Mode"));
                addMessage(key, MessageKeys.give_protection_crystal, new Message(configuration, path, "<gray>You have given {player} {amount} Protection Crystals.", "Messages", "Give-Protection-Crystal"));
                addMessage(key, MessageKeys.get_protection_crystal, new Message(configuration, path, "<gray>You have gained {amount} Protection Crystals.", "Messages", "Get-Protection-Crystal"));
                addMessage(key, MessageKeys.give_scrambler_crystal, new Message(configuration, path, "<gray>You have given {player} {amount} <yellow><bold>Grand Scramblers<gray>.", "Messages", "Give-Scrambler-Crystal"));
                addMessage(key, MessageKeys.get_scrambler_crystal, new Message(configuration, path, "<gray>You have gained {amount} <yellow><bold>Grand Scramblers<gray>.", "Messages", "Get-Scrambler-Crystal"));
                addMessage(key, MessageKeys.give_slot_crystal, new Message(configuration, path, "<gray>You have given {player} {amount} Slot Crystals.", "Messages", "Give-Slot-Crystal"));
                addMessage(key, MessageKeys.get_slot_crystal, new Message(configuration, path, "<gray>You have gained {amount} Slot Crystals.", "Messages", "Get-Slot-Crystal"));
                addMessage(key, MessageKeys.send_enchantment_book, new Message(configuration, path, "<gray>You have sent <gold>{player} <gray>a Crazy Enchantment Book.", "Messages", "Send-Enchantment-Book"));
                addMessage(key, MessageKeys.not_a_gkit, new Message(configuration, path, "<red>{kit} is not a GKit.", "Messages", "Not-A-GKit"));
                addMessage(key, MessageKeys.still_in_cooldown, new Message(configuration, path, "<red>You still have {day}d {hour}h {minute}m {second{s cool-down left on {kit}<red>.", "Messages", "Still-In-Cooldown"));
                addMessage(key, MessageKeys.given_gkit, new Message(configuration, path, "<gray>You have given <gold>{player}<gray> a {kit}<gray> GKit.", "Messages", "Given-GKit"));
                addMessage(key, MessageKeys.received_gkit, new Message(configuration, path, "<gray>You have received a {kit}<gray> GKit.", "Messages", "Received-GKit"));
                addMessage(key, MessageKeys.no_gkit_permission, new Message(configuration, path, "<red>You do not have permission to use the {kit} GKit.", "Messages", "No-GKit-Permission"));
                addMessage(key, MessageKeys.spawned_book, new Message(configuration, path, "<gray>You have spawned a book at <gold>{World{, {X{, {Y{, {Z}<gray>.", "Messages", "Spawned-Book"));
                addMessage(key, MessageKeys.reset_gkit, new Message(configuration, path, "<gray>You have reset {player}'s {kit} GKit cool-down.", "Messages", "Reset-GKit"));
                addMessage(key, MessageKeys.gkitz_not_enabled, new Message(configuration, path, "<red>GKitz is currently not enabled.", "Messages", "GKitz-Not-Enabled"));
                addMessage(key, MessageKeys.disordered_enemy_hot_bar, new Message(configuration, path, "<gray>Disordered enemies hot bar.", "Messages", "Disordered-Enemy-Hot-Bar"));
                addMessage(key, MessageKeys.enchantment_upgrade_success, new Message(configuration, path, "<gray>You have just upgraded <gold>{enchantment}<gray> to level <gold>{level}<gray>.", "Messages", "Enchantment-Upgrade.Success"));
                addMessage(key, MessageKeys.enchantment_upgrade_destroyed, new Message(configuration, path, "<red>Your upgrade failed and the lower level enchantment was lost.", "Messages", "Enchantment-Upgrade.Destroyed"));
                addMessage(key, MessageKeys.enchantment_upgrade_failed, new Message(configuration, path, "<red>The book failed to upgrade to the item.", "Messages", "Enchantment-Upgrade.Failed"));
                addMessage(key, MessageKeys.rage_building, new Message(configuration, path, "<gray>[<red><bold>Rage<gray>]: <green>Keep it up, your rage is building.", "Messages", "Rage.Building"));
                addMessage(key, MessageKeys.rage_cooled_down, new Message(configuration, path, "<gray>[<red><bold>Rage<gray>]: <red>Your Rage has just cooled down.", "Messages", "Rage.Cooled-Down"));
                addMessage(key, MessageKeys.rage_rage_up, new Message(configuration, path, "<gray>[<red><bold>Rage<gray>]: <gray>You are now doing <green>{level}x <gray>Damage.", "Messages", "Rage.Rage-Up"));
                addMessage(key, MessageKeys.rage_damaged, new Message(configuration, path, "<gray>[<red><bold>Rage<gray>]: <red>You have been hurt and it broke your Rage Multiplier!", "Messages", "Rage.Damaged"));
                addMessage(key, MessageKeys.invalid_item_string, new Message(configuration, path, "<red>Invalid item string supplied.", "Messages", "Invalid-Item-String"));
                addMessage(key, MessageKeys.show_enchants_format_main, new Message(configuration, path, "{item} {item_enchants}", "Messages", "Show-Enchants-Format.Main"));
                addMessage(key, MessageKeys.show_enchants_format_base, new Message(configuration, path, "<dark_green>{enchant}<gray>: <gold>{level} ", "Messages", "Show-Enchants-Format.Base"));
                addMessage(key, MessageKeys.limit_command, new Message(configuration, path, this.utils.toString(List.of(
                        "<black>======================================",
                        "<dark_gray>[<green>CrazyEnchants<dark_gray>]: <aqua>Personal Enchantment Limit:",
                        " ",
                        "<gray>Bypass Limit: <gold>{bypass{",
                        "<gray>Vanilla Enchantment Check: <gold>{vanilla}",
                        "<gray>Max Enchantment Limit: <gold>{limit}",
                        "<gray>Base Enchantment Limit: <gold>{base_limit}",
                        "<gray>Current Items Slot Crystal Limit Adjustment: <gold>{slot_crystal}",
                        "<gray>Current Enchantment amount on item: <gold>{item}",
                        "<gray>This item can have <gold>{max_enchants} <gray>enchants.",
                        "<gray>You can add <gold>{space} <gray>more enchantments to this item.",
                        "<red><red>Limit set in config.yml: {config_limit}",
                        "<black>======================================"
                )), "Messages", "Limit-Command"));
                addMessage(key, MessageKeys.help, new Message(configuration, path, this.utils.toString(List.of(
                        "<dark_green><bold><u>Crazy Enchantments",
                        "<aqua>/ce - <blue>Opens up the menu.",
                        "<aqua>/tinker - <blue>Opens up the Tinkerer menu.",
                        "<aqua>/blacksmith - <blue>Opens up the BlackSmith menu.",
                        "<aqua>/gkitz [kit] [player] - <blue>Open the gkit menu or get a gkit.",
                        "<aqua>/gkitz reset <kit> [player] - <blue>Reset a players gkit cool-down.",
                        "<aqua>/ce help - <blue>Shows all crazy enchantment commands.",
                        "<aqua>/ce debug - <blue>Does a small debug for some errors.",
                        "<aqua>/ce info [enchantment] - <blue>Shows info on all enchantments.",
                        "<aqua>/ce reload - <blue>Reloads all of the configuration files.",
                        "<aqua>/ce remove <enchantment> - <blue>Removes an enchantment from the item in your hand.",
                        "<aqua>/ce add <enchantment> [level] - <blue>Adds an enchantment to the item in your hand.",
                        "<aqua>/ce scroll <black/white/transmog> [amount] [player] - <blue>Gives a player a scroll item.",
                        "<aqua>/ce crystal [amount] [player] - <blue>Gives a player a Protection Crystal item.",
                        "<aqua>/ce scrambler [amount] [player] - <blue>Gives a player a Scrambler item.",
                        "<aqua>/ce dust <success/destroy/mystery> [amount] [player] [percent] - <blue>Give a player a dust item.",
                        "<aqua>/ce book <enchantment> [level/min-max] [amount] [player] - <blue>Gives a player an enchantment Book.",
                        "<aqua>/ce lostbook <category> [amount] [player] - <blue>Gives a player a lost book item.",
                        "<aqua>/ce spawn <enchantment/category> [(level:#/min-max)/world:<world>/x:#/y:#/z:#] - <blue>Drops an enchantment book at the specific coordinates."
                )), "Messages", "Help"));
            }, () -> this.fusion.log("warn", "Path %s not found in cache.".formatted(path)));
        }
    }
}