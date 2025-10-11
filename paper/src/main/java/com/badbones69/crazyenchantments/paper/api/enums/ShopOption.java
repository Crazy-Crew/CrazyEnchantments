package com.badbones69.crazyenchantments.paper.api.enums;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.objects.CEOption;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
import java.util.Map;

public enum ShopOption {

    GKITZ("Name", "Lore", false, "GKitz", "<red><b>GKitz", List.of(
            "<aqua>>><gray><u>Click to open<aqua><<"
    )),
    BLACKSMITH("Name", "Lore", false, "BlackSmith", "<gray><b>The <aqua><b>Black <blue><b>Smith", List.of(
            "<aqua>>><gray><u>Click to open<aqua><<"
    )),
    TINKER("Name", "Lore", false, "Tinker", "<gray><b>The <aqua><b>Crazy <blue><b>Tinkerer", List.of(
            "<aqua>>><gray><u>Click to open<aqua><<"
    )),
    INFO("Name", "Lore", false, "Info", "<yellow>Info on the Enchantments", List.of(
            "<aqua>>><gray><u>Click to view<aqua><<"
    )),

    PROTECTION_CRYSTAL("GUIName", "GUILore", true, "ProtectionCrystal", "<dark_purple><b>Protection <aqua><b>Crystal", List.of(
            "<gray>A rare crystal that is said to",
            "<gray>protect items from getting lost",
            "<gray>while the owners away in the after life.",
            "",
            "<yellow>Cost: <yellow><b>$500"
    )),
    SUCCESS_DUST("GUIName", "GUILore", true, "SuccessDust", "<green>Magical Angel Dust", List.of(
            "<green>+%percent%% Success Rate",
            "<gray>Apply to an Enchantment Book to",
            "<gray>Increase the Success Rate by <yellow>%percent%%"
    )),
    DESTROY_DUST("GUIName", "GUILore", true, "DestroyDust", "<yellow>Magical Fixing Dust", List.of(
            "<yellow>Cost: <green><b>$500",
            "<gray>Dust allows you to bring",
            "<gray>down your <dark_red>Destroy Rate<gray>."
    )),
    SCRAMBLER("GUIName", "GUILore", true, "Scrambler", "<yellow><b>The Grand Scrambler", List.of(
            "<gray>The <yellow><b>The Grand Scrambler <gray>will allow",
            "<gray>you to re-roll the destroy and success rates.",
            "<gray>Drag and drop it on an enchantment book",
            "<gray>to get a new destroy and success rate.",
            "",
            "<yellow>Cost: <yellow><b>$800"
    )),

    BLACK_SCROLL("GUIName", "Lore", true, "BlackScroll", "<gray>Black Scroll", List.of(
            "<yellow>Cost: <green><b>$1000",
            "<gray>Black Scrolls allow you to",
            "<gray>take off random enchantments."
    )),
    WHITE_SCROLL("GUIName", "Lore", true, "WhiteScroll", "<gray>White Scroll", List.of(
            "<yellow>Cost: <green><b>$2000",
            "<gray>White Scrolls allow you to",
            "<gray>protect items from breaking."
    )),
    TRANSMOG_SCROLL("GUIName", "Lore", true, "TransmogScroll", "<light_purple>Transmog Scroll", List.of(
            "<yellow>Cost: <green><b>$200",
            "<gray>This scroll allows you to organize",
            "<gray>your enchantments and tell you how many",
            "<gray>enchantments you currently have on the item."
    )),
    SLOT_CRYSTAL("GUIName", "GUILore", true, "Slot_Crystal", "<dark_purple><b>Slot <aqua><b>Crystal", List.of(
            "<gray>A rare crystal that is said to",
            "<gray>increase the amount of enchants",
            "<gray>that can be added onto an item.",
            "",
            "<yellow>Cost: <yellow><b>$500"
    ));

    private final boolean buyable;
    private final String namePath;
    private final String lorePath;
    private final String path;

    private final List<String> defaultLore;
    private final String defaultName;

    ShopOption(final String namePath, final String lorePath, final boolean buyable, final String path, final String defaultName, final List<String> defaultLore) {
        this.defaultName = defaultName;
        this.defaultLore = defaultLore;

        this.namePath = namePath;
        this.lorePath = lorePath;
        this.buyable = buyable;
        this.path = path;
    }

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyInstance crazyManager = this.plugin.getInstance();

    private final Map<ShopOption, CEOption> shopOptions = this.crazyManager.getShopOptions();

    public ItemStack getItem() {
        return getItemBuilder().build();
    }

    public ItemBuilder getItemBuilder() {
        return this.shopOptions.get(this).itemBuilder();
    }

    public int getSlot() {
        return this.shopOptions.get(this).slot();
    }

    public boolean isInGUI() {
        return this.shopOptions.get(this).inGUI();
    }

    public int getCost() {
        return this.shopOptions.get(this).cost();
    }

    public Currency getCurrency() {
        return this.shopOptions.get(this).currency();
    }

    public String getPath() {
        return this.path;
    }

    public String getNamePath() {
        return this.namePath;
    }

    public String getLorePath() {
        return this.lorePath;
    }

    public boolean isBuyable() {
        return this.buyable;
    }

    public String getDefaultName() {
        return this.defaultName;
    }

    public List<String> getDefaultLore() {
        return this.defaultLore;
    }
}