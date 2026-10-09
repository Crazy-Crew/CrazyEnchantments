package com.ryderbelserion.common.api.enums.messages;

import com.ryderbelserion.common.CrazyEnchantmentsPlugin;
import com.ryderbelserion.common.api.EnchantmentProvider;
import com.ryderbelserion.common.api.sender.ISenderAdapter;
import com.ryderbelserion.fusion.api.objects.FusionKey;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.core.api.registry.message.adapter.YamlMessageAdapter;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import net.kyori.adventure.audience.Audience;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.List;
import java.util.Map;
import static com.ryderbelserion.common.CrazyEnchantmentsPlugin.namespace;

@NullMarked
public enum Messages {

    correct_usage("correct.usage", "<red>The correct usage for this command is <yellow>%usage%.", "Correct-Usage"),

    must_be_console_sender("must.be.console.sender", "<red>You must be console sender to run this command.", "Must-Be-Console-Sender"),
    inventory_full("inventory.full", "<red>Your inventory is too full. Please open up some space to buy that.", "Inventory-Full"),
    need_to_use_player_inventory("must.be.player.inventory", "<red>You can only use that in your player inventory.", "Need-To-Use-Player-Inventory"),
    players_only("players.only", "<red>Only players can use this command.", "Players-Only"),
    no_permission("no.permission", "<red>You do not have permission to use that command!", "No-Perm"),
    not_online("not.online", "<red>That player is not online.", "Not-Online"),
    command_not_found("command.not.found", "<red>%command% is not a known command.", "Command-Not-Found"),
    doesnt_have_item_in_hand("doesnt.have.item.in.hand", "<red>You must have an item in your hand.", "Doesnt-Have-Item-In-Hand"),
    player_is_in_creative_mode("player.in.creative.mode", "<red>You are in creative mode. You need to get out of Creative Mode!", "Player-Is-In-Creative-Mode"),
    not_a_number("not.a.number", "<red>%Arg% is not a number.", "Not-A-Number"),

    item_cannot_be_empty("item.cannot.be.empty", "<gray>The item trying to be fetched using %command% cannot be empty!", "Item-Cannot-Be-Empty"),
    need_to_unstack_item("need.to.unstack.item", "<red>You need to unstack that item before you can use it.", "Need-To-UnStack-Item"),

    right_click_black_scroll("right.click.black.scroll", "<gray>Black scrolls will remove a random enchantment from your item.", "Right-Click-Black-Scroll"),
    black_scroll_unsuccessful("right.click.black.scroll.unsuccessful", "<red>The black scroll was unsuccessful. Please try again with another one.", "Black-Scroll-Unsuccessful"),
    give_scroll("give.scroll", "<gray>You have given <gold>%player% %type% <gray>scroll!", "Give-Scroll"),
    get_scroll("get.scroll", "<gray>You have been given a <gold>%type% <gray>scroll!", "Get-Scroll"),

    give_lostbook("give.lostbook", "<gray>You have given <gold>%player% %category% <gray>lostbook!", "Give-LostBook"),
    get_lostbook("get.lostbook", "<gray>You have been given a <gold>%category% <gray>lostbook!", "Get-LostBook"),
    clean_lost_book("clean.lost.book", "<gray>You have cleaned a lost book and found %found%<gray>.", "Clean-Lost-Book"),

    book_works("book.works", "<green>Your item loved this book and accepted it.", "Book-Works"),
    book_failed("book.failed", "<red>Your item must not have liked that enchantment.", "Book-Failed"),

    give_bottle("give.bottle", "<gray>You have given <gold>%player% %amount% <gray>exp bottle!", "Give-Bottle"),
    get_bottle("get.bottle", "<gray>You have been given a <gold>%amount% <gray>exp bottle!", "Get-Bottle"),

    need_more_xp_levels("need.more.xp.levels", "<red>You need <gold>%XP% <red>more xp level.", "Need-More-XP-Lvls"),
    need_more_total_xp("need.more.total.xp.levels", "<red>You need <gold>%XP% <red>more total xp.", "Need-More-Total-XP"),
    need_more_money("need.more.money", "<red>You are in need of <green>$%Money_Needed%<red>.", "Need-More-Money"),

    tinker_inventory_full("tinker.inventory.full", "<red>The inventory is full. Sell all or remove items.", "Tinker-Inventory-Full"),
    tinker_sold_message("tinker.sold.msg", "<gray>Thank you for trading at <gray><b>The <dark_red><b>Crazy <red><b>Tinkerer<gray>.", "Tinker-Sold-Msg"),

    config_reload("config.reload", "<gray>You have reloaded the Config.yml", "Config-Reload"),
    not_an_enchantment("not.an.enchantment", "<red>That is not an enchantment.", "Not-An-Enchantment"),
    hit_enchantment_max("hit.enchantment.max", List.of(
            "<#ff0000>That item already has the maximum amount of enchantments that you can add to it.",
            "<#880808>Use <dark_green>/ce limit <#880808>to check the current limit on the item.",
            "<#880808>For information on how to change the limit, read <dark_green>https://docs.crazycrew.us/docs/plugins/crazyenchantments/faq."
    ), "Hit-Enchantment-Max"),
    conflicting_enchant("conflicting.enchant", "<gray>This enchant conflicts with one already on the item.", "Conflicting-Enchant"),
    max_slots_unlocked("hit.slot.max", "<red>You have already added the maximum amount of slots to this item.", "Hit-Slot-Max"),
    applied_slot_crystal("applied.slot.crystal", "<red>You have successfully added another slot to the item. The item now has %slot% extra slots.", "Applied-Slot-Crystal"),

    removed_enchantment("remove.enchantment", "<gray>You have removed the enchantment <green>%Enchantment% <gray>from this item.", "Remove-Enchantment"),
    doesnt_have_enchantment("doesnt.have.enchantment", "<red>Your item does not contain the enchantment <gold>%Enchantment%<red>.", "Doesnt-Have-Enchantment"),

    get_success_dust("get.success.dust", "<gray>You have gained <green>%amount% <gray>Success Dust.", "Get-Success-Dust"),
    give_success_dust("give.success.dust", "<gray>You have given <green>%amount% <gray>Success Dust to <gold>%player%<gray>.", "Give-Success-Dust"),

    get_destroy_dust("get.destroy.dust", "<gray>You have gained <green>%amount% <gray>Destroy Dust.", "Get-Destroy-Dust"),
    give_destroy_dust("give.destroy.dust", "<gray>You have given <green>%amount% <gray>Destroy Dust to <gold>%player%<gray>.", "Give-Destroy-Dust"),

    get_mystery_dust("get.mystery.dust", "<gray>You have gained <green>%amount% <gray>Mystery Dust.", "Get-Mystery-Dust"),
    give_mystery_dust("give.mystery.dust", "<gray>You have given <green>%amount% <gray>Mystery Dust to <gold>%player%<gray>.", "Give-Mystery-Book"),

    not_a_category("not.a.category", "<gold>%category% <red>is not a category.", "Not-A-Category"),

    item_destroyed("item.destroyed", "<red>Oh no the destroy rate was too much for the item.", "Item-Destroyed"),
    item_was_protected("item.was.protected", "<red>Luckily your item was blessed with Divine Protection and did not break.", "Item-Was-Protected"),

    give_protection_crystal("give.protection.crystal", "<gray>You have given %player% %amount% Protection Crystals.", "Give-Protection-Crystal"),
    get_protection_crystal("get.protection.crystal", "<gray>You have gained %amount% Protection Crystals.", "Get-Protection-Crystal"),

    give_scrambler_crystal("give.scrambler.crystal", "<gray>You have given %player% %amount% <yellow><b>Grand Scramblers<gray>.", "Give-Scrambler-Crystal"),
    get_scrambler_crystal("get.scrambler.crystal", "<gray>You have gained %amount% <yellow><b>Grand Scramblers<gray>.", "Get-Scrambler-Crystal"),

    give_slot_crystal("give.slot.crystal", "<gray>You have given %player% %amount% Slot Crystals.", "Give-Slot-Crystal"),
    get_slot_crystal("get.slot.crystal", "<gray>You have gained %amount% Slot Crystals.", "Get-Slot-Crystal"),

    send_enchantment_book("send.enchantment.book", "<gray>You have sent <gold>%player% <gray>a Crazy Enchantment Book.", "Send-Enchantment-Book"),

    not_a_gkit("not.a.gkit", "<red>%kit% is not a GKit.", "Not-A-GKit"),
    still_in_cooldown("still.in.cooldown", "<red>You still have %day%d %hour%h %minute%m %second%s cool-down left on %kit%<red>.", "Still-In-Cooldown"),
    given_gkit("given.gkit", "<gray>You have given <gold>%player%<gray> a %kit%<gray> GKit.", "Given-GKit"),
    received_gkit("received.gkit", "<gray>You have received a %kit%<gray> GKit.", "Received-GKit"),
    no_gkit_permission("no.gkit.permission", "<red>You do not have permission to use the %kit% GKit.", "No-GKit-Permission"),
    spawned_book("spawned.book", "<gray>You have spawned a book at <gold>%World%, %X%, %Y%, %Z%<gray>.", "Spawned-Book"),
    reset_gkit("reset.gkit", "<gray>You have reset %player%'s %GKit% GKit cool-down.", "Reset-GKit"),
    gkit_not_enabled("gkitz.not.enabled", "<red>GKitz is currently not enabled.", "Gkitz-Not-Enabled"),

    disordered_enemy_hot_bar("disordered.enemy.hot.bar", "<gray>Disordered enemies hot bar.", "Disordered-Enemy-Hot-Bar"),

    enchantment_upgrade_success("enchantment.upgrade.success", "<gray>You have just upgraded <gold>%Enchantment%<gray> to level <gold>%Level%<gray>.", "Enchantment-Upgrade", "Success"),
    enchantment_upgrade_destroyed("enchantment.upgrade.destroyed", "<red>Your upgrade failed and the lower level enchantment was lost.", "Enchantment-Upgrade", "Destroyed"),

    enchantment_upgrade_failed("enchantment.upgrade.failed", "<red>The book failed to upgrade to the item.", "Enchantment-Upgrade", "Failed"),

    rage_building("rage.building", "<gray>[<red><b>Rage<gray>]: <green>Keep it up, your rage is building.", "Rage", "Building"),
    rage_cooled_down("rage.cooled.down", "<gray>[<red><b>Rage<gray>]: <red>Your Rage has just cooled down.", "Rage", "Cooled-Down"),
    rage_rage_up("rage.rage.up", "<gray>[<red><b>Rage<gray>]: <gray>You are now doing <green>%Level%x <gray>Damage.", "Rage", "Rage-Up"),
    rage_damaged("rage.damaged", "<gray>[<red><b>Rage<gray>]: <red>You have been hurt and it broke your Rage Multiplier!", "Rage", "Damaged"),

    invalid_item_string("invalid.item.string", "<red>Invalid item string supplied.", "Invalid-Item-String"),

    main_update_enchants("show.enchants.format.main", "%item% %itemEnchants%", "Show-Enchants-Format", "Main"),
    base_update_enchants("show.enchants.format.base", "<dark_green>%enchant%<gray>: <gold>%level% ", "Show-Enchants-Format", "Base"),

    limit_command("limit.command", List.of(
            "<black>======================================",
            "<dark_gray>[<green>CrazyEnchants<dark_gray>]: <aqua>Personal Enchantment Limit:",
            " ",
            "<gray>Bypass Limit: <gold>%bypass%",
            "<gray>Vanilla Enchantment Check: <gold>%vanilla%",
            "<gray>Max Enchantment Limit: <gold>%limit%",
            "<gray>Base Enchantment Limit: <gold>%baseLimit%",
            "<gray>Current Items Slot Crystal Limit Adjustment: <gold>%slotCrystal%",
            "<gray>Current Enchantment amount on item: <gold>%item%",
            "<gray>This item can have <gold>%canHave% <gray>enchants.",
            "<gray>You can add <gold>%space% <gray>more enchantments to this item.",
            "<red><red>Limit set in config.yml: %limitSetInConfig%",
            "<black>======================================"
    ), "Limit-Command"),

    help("help", List.of(
            "<dark_green><b><u>Crazy Enchantments",
            "<aqua>/ce - <blue>Opens up the menu.",
            "<aqua>/tinker - <blue>Opens up the Tinkerer menu.",
            "<aqua>/blacksmith - <blue>Opens up the BlackSmith menu.",
            "<aqua>/gkitz [kit] [player] - <blue>Open the gkit menu or get a gkit.",
            "<aqua>/gkitz reset <kit> [player] - <blue>Reset a players gkit cool-down.",
            "<aqua>/ce help - <blue>Shows all crazy enchantment commands.",
            "<aqua>/ce debug - <blue>Does a small debug for some errors.",
            "<aqua>/ce info [enchantment] - <blue>Shows info on all enchantments.",
            "<aqua>/ce reload - <blue>Reloads all of the configuration FileKeys.",
            "<aqua>/ce remove <enchantment> - <blue>Removes an enchantment from the item in your hand.",
            "<aqua>/ce add <enchantment> [level] - <blue>Adds an enchantment to the item in your hand.",
            "<aqua>/ce scroll <black/white/transmog> [amount] [player] - <blue>Gives a player a scroll item.",
            "<aqua>/ce crystal [amount] [player] - <blue>Gives a player a Protection Crystal item.",
            "<aqua>/ce scrambler [amount] [player] - <blue>Gives a player a Scrambler item.",
            "<aqua>/ce dust <success/destroy/mystery> [amount] [player] [percent] - <blue>Give a player a dust item.",
            "<aqua>/ce book <enchantment> [level/min-max] [amount] [player] - <blue>Gives a player an enchantment Book.",
            "<aqua>/ce lostbook <category> [amount] [player] - <blue>Gives a player a lost book item.",
            "<aqua>/ce spawn <enchantment/category> [(level:#/min-max)/world:<world>/x:#/y:#/z:#] - <blue>Drops an enchantment book at the specific coordinates."
    ), "Help");

    private final CrazyEnchantmentsPlugin plugin = EnchantmentProvider.api();

    private final ISenderAdapter senderAdapter = this.plugin.getSenderAdapter();

    private final String defaultValue;
    private final Object[] path;
    private final FusionKey id;

    Messages(final String id, final String defaultValue, final Object... path) {
        this.defaultValue = defaultValue;
        this.id = FusionKey.key(namespace, id);
        this.path = path;
    }

    Messages(final String id, final List<String> defaultValue, final Object... path) {
        this.defaultValue = StringUtils.toString(defaultValue);
        this.id = FusionKey.key(namespace, id);
        this.path = path;
    }

    public void addKey(final MessageRegistry registry, final CommentedConfigurationNode configuration, final FusionKey id) {
        final YamlMessageAdapter adapter = new YamlMessageAdapter(configuration, this.defaultValue, this.path);

        registry.addKey(
                id,
                this.id,
                adapter
        );
    }

    public void sendMessage(final Audience audience, final Map<String, String> placeholders) {
        this.senderAdapter.sendMessage(audience, this.id, placeholders);
    }

    public void sendMessage(final Audience audience, final String placeholder, final String value) {
        sendMessage(audience, Map.of(placeholder, value));
    }

    public void sendMessage(final Audience audience) {
        sendMessage(audience, Map.of());
    }

    public String getMessage(final Audience audience, final Map<String, String> placeholders) {
        return this.senderAdapter.getMessage(audience, this.id, placeholders);
    }

    public String getMessage(final Audience audience, final String placeholder, final String value) {
        return getMessage(audience, Map.of(placeholder, value));
    }

    public String getMessage(final Audience audience) {
        return this.senderAdapter.getMessage(audience, this.id);
    }

    public FusionKey getKey() {
        return this.id;
    }
}