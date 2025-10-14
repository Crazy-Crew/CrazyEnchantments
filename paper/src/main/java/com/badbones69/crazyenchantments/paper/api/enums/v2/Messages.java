package com.badbones69.crazyenchantments.paper.api.enums.v2;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.utils.ConfigUtils;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Messages {

    CORRECT_USAGE("Correct-Usage", "<red>The correct usage for this command is {usage}."),
    PLAYERS_ONLY("Players-Only", "<red>Only players can use this command."),
    NO_PERMISSION("No-Perm", "<red>You do not have permission to use that command!"),
    CONSOLE_ONLY("Console-Only", "<red>Only console can use this command."),
    UNKNOWN_COMMAND("Unknown-Command", "<red>The command {command} is not known."),

    CONFIG_RELOAD("Config-Reload", "<gray>You have reloaded the Config.yml"),
    NEED_TO_UNSTACK_ITEM("Need-To-UnStack-Item", "<red>You need to unstack that item before you can use it."),
    NOT_AN_ENCHANTMENT("Not-An-Enchantment", "<red>That is not an enchantment."),
    RIGHT_CLICK_BLACK_SCROLL("Right-Click-Black-Scroll", "<gray>Black scrolls will remove a random enchantment from your item."),
    BLACK_SCROLL_UNSUCCESSFUL("Black-Scroll-Unsuccessful", "<red>The black scroll was unsuccessful. Please try again with another one."),
    NEED_MORE_XP_LEVELS("Need-More-XP-Lvls", "<red>You need <gold>{xp} <red>more xp level."),
    NEED_MORE_TOTAL_XP("Need-More-Total-XP", "<red>You need <gold>{xp} <red>more total xp."),
    NEED_MORE_MONEY("Need-More-Money", "<red>You are in need of <green>${money_needed}<red>."),
    HIT_ENCHANTMENT_MAX("Hit-Enchantment-Max", Arrays.asList(
            "<#ff0000>That item already has the maximum amount of enchantments that you can add to it.",
            "<#880808>Use <dark_green>/ce limit <#880808>to check the current limit on the item.",
            "<#880808>For information on how to change the limit, read <dark_green>https://docs.crazycrew.us/docs/plugins/crazyenchantments/faq."
    )),
    CONFLICTING_ENCHANT("Conflicting-Enchant", "<gray>This enchant conflicts with one already on the item."),
    MAX_SLOTS_UNLOCKED("Hit-Slot-Max", "<red>You have already added the maximum amount of slots to this item."),
    APPLIED_SLOT_CRYSTAL("Applied-Slot-Crystal", "<red>You have successfully added another slot to the item. The item now has {slot} extra slots."),
    INVENTORY_FULL("Inventory-Full", "<red>Your inventory is too full. Please open up some space to buy that."),
    TINKER_INVENTORY_FULL("Tinker-Inventory-Full", "<red>The inventory is full. Sell all or remove items."),
    NEED_TO_USE_PLAYER_INVENTORY("Need-To-Use-Player-Inventory", "<red>You can only use that in your player inventory."),
    TINKER_SOLD_MESSAGE("Tinker-Sold-Msg", "<gray>Thank you for trading at <gray><bold>The <dark_red><bold>Crazy <red><bold>Tinkerer<gray>."),
    NOT_ONLINE("Not-Online", "<red>That player is not online."),
    REMOVED_ENCHANTMENT("Remove-Enchantment", "<gray>You have removed the enchantment <green>{enchantment} <gray>from this item."),
    DOESNT_HAVE_ENCHANTMENT("Doesnt-Have-Enchantment", "<red>Your item does not contain the enchantment <gold>{enchantment}<red>."),
    DOESNT_HAVE_ITEM_IN_HAND("Doesnt-Have-Item-In-Hand", "<red>You must have an item in your hand."),
    NOT_A_NUMBER("Not-A-Number", "<red>{arg} is not a number."),
    GET_SUCCESS_DUST("Get-Success-Dust", "<gray>You have gained <green>{amount} <gray>Success Dust."),
    GIVE_SUCCESS_DUST("Give-Success-Dust", "<gray>You have given <green>{amount} <gray>Success Dust to <gold>{player}<gray>."),
    GET_DESTROY_DUST("Get-Destroy-Dust", "<gray>You have gained <green>{amount} <gray>Destroy Dust."),
    GIVE_DESTROY_DUST("Give-Destroy-Dust", "<gray>You have given <green>{amount} <gray>Destroy Dust to <gold>{player}<gray>."),
    GET_MYSTERY_DUST("Get-Mystery-Dust", "<gray>You have gained <green>{amount} <gray>Mystery Dust."),
    GIVE_MYSTERY_DUST("Give-Mystery-Dust", "<gray>You have given <green>{amount} <gray>Mystery Dust to <gold>{player}<gray>."),
    NOT_A_CATEGORY("Not-A-Category", "<gold>{category} <red>is not a category."),
    CLEAN_LOST_BOOK("Clean-Lost-Book", "<gray>You have cleaned a lost book and found {found}<gray>."),
    BOOK_WORKS("Book-Works", "<green>Your item loved this book and accepted it."),
    BOOK_FAILED("Book-Failed", "<red>Your item must not have liked that enchantment."),
    ITEM_DESTROYED("Item-Destroyed", "<red>Oh no the destroy rate was too much for the item."),
    ITEM_WAS_PROTECTED("Item-Was-Protected", "<red>Luckily your item was blessed with Divine Protection and did not break."),
    PLAYER_IS_IN_CREATIVE_MODE("Player-Is-In-Creative-Mode", "<red>You are in creative mode. You need to get out of Creative Mode!"),
    GIVE_PROTECTION_CRYSTAL("Give-Protection-Crystal", "<gray>You have given {player} {amount} Protection Crystals."),
    GET_PROTECTION_CRYSTAL("Get-Protection-Crystal", "<gray>You have gained {amount} Protection Crystals."),
    GIVE_SCRAMBLER_CRYSTAL("Give-Scrambler-Crystal", "<gray>You have given {player} {amount} <yellow><bold>Grand Scramblers<gray>."),
    GET_SCRAMBLER("Get-Scrambler-Crystal", "<gray>You have gained {amount} <yellow><bold>Grand Scramblers<gray>."),
    GIVE_SLOT_CRYSTAL("Give-Slot-Crystal", "<gray>You have given {player} {amount} Slot Crystals."),
    GET_SLOT_CRYSTAL("Get-Slot-Crystal", "<gray>You have gained {amount} Slot Crystals."),
    SEND_ENCHANTMENT_BOOK("Send-Enchantment-Book", "<gray>You have sent <gold>{player} <gray>a Crazy Enchantment Book."),
    NOT_A_GKIT("Not-A-GKit", "<red>{kit} is not a GKit."),
    STILL_IN_COOLDOWN("Still-In-Cooldown", "<red>You still have {day}d {hour}h {minute}m {second{s cool-down left on {kit}<red>."),
    GIVEN_GKIT("Given-GKit", "<gray>You have given <gold>{player}<gray> a {kit}<gray> GKit."),
    RECEIVED_GKIT("Received-GKit", "<gray>You have received a {kit}<gray> GKit."),
    NO_GKIT_PERMISSION("No-GKit-Permission", "<red>You do not have permission to use the {kit} GKit."),
    SPAWNED_BOOK("Spawned-Book", "<gray>You have spawned a book at <gold>{World{, {X{, {Y{, {Z}<gray>."),
    RESET_GKIT("Reset-GKit", "<gray>You have reset {player}'s {kit} GKit cool-down."),
    GKIT_NOT_ENABLED("GKitz-Not-Enabled", "<red>GKitz is currently not enabled."),
    DISORDERED_ENEMY_HOT_BAR("Disordered-Enemy-Hot-Bar", "<gray>Disordered enemies hot bar."),
    ENCHANTMENT_UPGRADE_SUCCESS("Enchantment-Upgrade.Success", "<gray>You have just upgraded <gold>{enchantment}<gray> to level <gold>{level}<gray>."),
    ENCHANTMENT_UPGRADE_DESTROYED("Enchantment-Upgrade.Destroyed", "<red>Your upgrade failed and the lower level enchantment was lost."),
    ENCHANTMENT_UPGRADE_FAILED("Enchantment-Upgrade.Failed", "<red>The book failed to upgrade to the item."),
    RAGE_BUILDING("Rage.Building", "<gray>[<red><bold>Rage<gray>]: <green>Keep it up, your rage is building."),
    RAGE_COOLED_DOWN("Rage.Cooled-Down", "<gray>[<red><bold>Rage<gray>]: <red>Your Rage has just cooled down."),
    RAGE_RAGE_UP("Rage.Rage-Up", "<gray>[<red><bold>Rage<gray>]: <gray>You are now doing <green>{level}x <gray>Damage."),
    RAGE_DAMAGED("Rage.Damaged", "<gray>[<red><bold>Rage<gray>]: <red>You have been hurt and it broke your Rage Multiplier!"),
    INVALID_ITEM_STRING("Invalid-Item-String", "<red>Invalid item string supplied."),
    MAIN_UPDATE_ENCHANTS("Show-Enchants-Format.Main", "{item} {item_enchants}"),
    BASE_UPDATE_ENCHANTS("Show-Enchants-Format.Base", "<dark_green>{enchant}<gray>: <gold>{level} "),
    LIMIT_COMMAND("Limit-Command", Arrays.asList(
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
            "<black>======================================")),
    HELP("Help", Arrays.asList(
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
            "<aqua>/ce spawn <enchantment/category> [(level:#/min-max)/world:<world>/x:#/y:#/z:#] - <blue>Drops an enchantment book at the specific coordinates."));

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final FusionPaper fusion = this.plugin.getFusion();

    private final StringUtils utils = this.fusion.getStringUtils();

    private final List<String> defaultListMessage;
    private final String defaultMessage;
    private final String path;

    Messages(@NotNull final String path, @NotNull final String message) {
        this.defaultListMessage = List.of();
        this.defaultMessage = message;
        this.path = path;
    }

    Messages(@NotNull final String path, @NotNull final List<String> message) {
        this.defaultListMessage = message;
        this.defaultMessage = "";
        this.path = path;
    }

    public Component getMessage(@NotNull final CommandSender sender) {
        return getMessage(sender, new HashMap<>());
    }

    public Component getMessage(@NotNull final CommandSender sender, @NotNull final String placeholder, @NotNull final String replacement) {
        Map<String, String> placeholders = new HashMap<>() {{
            put(placeholder, replacement);
        }};

        return getMessage(sender, placeholders);
    }

    public Component getMessage(@NotNull final CommandSender sender, @NotNull final Map<String, String> placeholders) {
        return parse(sender, placeholders);
    }

    public void sendMessage(@NotNull final CommandSender sender, @NotNull final String placeholder, @NotNull final String replacement) {
        sendRichMessage(sender, placeholder, replacement);
    }

    public void sendMessage(@NotNull final CommandSender sender, @NotNull final Map<String, String> placeholders) {
        sendRichMessage(sender, placeholders);
    }

    public void sendMessage(@NotNull final CommandSender sender) {
        sendRichMessage(sender);
    }

    public void sendRichMessage(@NotNull final CommandSender sender, @NotNull final String placeholder, @NotNull final String replacement) {
        final Component message = getMessage(sender, placeholder, replacement);

        if (message.equals(Component.empty())) return;

        sender.sendMessage(message);
    }

    public void sendRichMessage(@NotNull final CommandSender sender, @NotNull final Map<String, String> placeholders) {
        final Component message = getMessage(sender, placeholders);

        if (message.equals(Component.empty())) return;

        sender.sendMessage(message);
    }

    public void sendRichMessage(@NotNull final CommandSender sender) {
        final Component message = getMessage(sender);

        if (message.equals(Component.empty())) return;

        sender.sendMessage(message);
    }

    public void migrate() {
        final YamlConfiguration configuration = FileKeys.messages.getPaperConfiguration();

        if (isList()) {
            configuration.set(this.path, this.utils.convertLegacy(ConfigUtils.getStringList(configuration, this.defaultListMessage, this.path)));

            return;
        }

        configuration.set(this.path, this.utils.convertLegacy(configuration.getString(this.path, this.defaultMessage)));
    }

    private @NotNull Component parse(@NotNull final CommandSender sender, @NotNull final Map<String, String> placeholders) {
        final YamlConfiguration configuration = FileKeys.messages.getPaperConfiguration();

        String message;

        if (isList()) {
            message = this.utils.toString(ConfigUtils.getStringList(configuration, this.defaultListMessage, this.path));
        } else {
            message = configuration.getString(this.path, this.defaultMessage);
        }

        return this.fusion.parse(sender, replace(message), placeholders);
    }

    public @NotNull final List<String> getDefaultListMessage() {
        return this.defaultListMessage;
    }

    public @NotNull final String getDefaultMessage() {
        return this.defaultMessage;
    }

    public @NotNull final String getPath() {
        return this.path;
    }

    public final boolean isList() {
        return !this.defaultListMessage.isEmpty();
    }

    private String replace(@NotNull final String message) {
        return message.replaceAll("%command%", "{command}")
                .replaceAll("%usage%", "{usage}")
                .replaceAll("%xp%", "{xp}")
                .replaceAll("%XP%", "{xp}")
                .replaceAll("%slot%", "{slot}")
                .replaceAll("%enchantment%", "{enchantment}")
                .replaceAll("%amount%", "{amount}")
                .replaceAll("%player%", "{player}")
                .replaceAll("%category%", "{category}")
                .replaceAll("%found%", "{found}")
                .replaceAll("%kit%", "{kit}")
                .replaceAll("%day%", "{day}")
                .replaceAll("%hour%", "{hour}")
                .replaceAll("%minute%", "{minute}")
                .replaceAll("%second%", "{second}")
                .replaceAll("%world%", "{world}")
                .replaceAll("%x%", "{x}")
                .replaceAll("%y%", "{y}")
                .replaceAll("%z%", "{z}")
                .replaceAll("%gkit%", "{kit}")
                .replaceAll("%enchant%", "{enchant}")
                .replaceAll("%level%", "{level}")
                .replaceAll("%bypass%", "{bypass}")
                .replaceAll("%vanilla%", "{vanilla}")
                .replaceAll("%limit%", "{limit}")
                .replaceAll("%baseLimit%", "{base_limit}")
                .replaceAll("%slotCrystal%", "{slot_crystal}")
                .replaceAll("%item%", "{item}")
                .replaceAll("%canHave%", "{max_enchants}")
                .replaceAll("%space%", "{space}")
                .replaceAll("%limitSetInConfig%", "{config_limit}")
                .replaceAll("%Arg%", "{arg}")
                .replaceAll("%itemEnchants%", "{item_enchants}")
                .replaceAll("%Money_Needed%", "{money_needed")
                .replaceAll("%maxEnchants%", "{max_enchants}")
                .replaceAll("%enchantAmount%", "{enchant_amount}")
                .replaceAll("%baseEnchants%", "{base_enchants}");
    }
}