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

    GKITZ("Name", "Lore", false, "GKitz", "&c&lGKitz", List.of(
            "&b>>&7&nClick to open&b<<"
    )),
    BLACKSMITH("Name", "Lore", false, "BlackSmith", "&7&lThe &b&lBlack &9&lSmith", List.of(
            "&b>>&7&nClick to open&b<<"
    )),
    TINKER("Name", "Lore", false, "Tinker", "&7&lThe &b&lCrazy &9&lTinkerer", List.of(
            "&b>>&7&nClick to open&b<<"
    )),
    INFO("Name", "Lore", false, "Info", "&eInfo on the Enchantments", List.of(
            "&b>>&7&nClick to view&b<<"
    )),

    PROTECTION_CRYSTAL("GUIName", "GUILore", true, "ProtectionCrystal", "&5&lProtection &b&lCrystal", List.of(
            "&7A rare crystal that is said to",
            "&7protect items from getting lost",
            "&7while the owners away in the after life.",
            "",
            "&eCost: &e&l$500"
    )),
    SUCCESS_DUST("GUIName", "GUILore", true, "SuccessDust", "&aMagical Angel Dust", List.of(
            "&a+%percent%% Success Rate",
            "&7Apply to an Enchantment Book to",
            "&7Increase the Success Rate by &e%percent%%"
    )),
    DESTROY_DUST("GUIName", "GUILore", true, "DestroyDust", "&eMagical Fixing Dust", List.of(
            "&eCost: &a&l$500",
            "&7Dust allows you to bring",
            "&7down your &4Destroy Rate&7."
    )),
    SCRAMBLER("GUIName", "GUILore", true, "Scrambler", "&e&lThe Grand Scrambler", List.of(
            "&7The &e&lThe Grand Scrambler &7will allow",
            "&7you to re-roll the destroy and success rates.",
            "&7Drag and drop it on an enchantment book",
            "&7to get a new destroy and success rate.",
            "",
            "&eCost: &e&l$800"
    )),

    BLACK_SCROLL("GUIName", "Lore", true, "BlackScroll", "&7Black Scroll", List.of(
            "&eCost: &a&l$1000",
            "&7Black Scrolls allow you to",
            "&7take off random enchantments."
    )),
    WHITE_SCROLL("GUIName", "Lore", true, "WhiteScroll", "&7White Scroll", List.of(
            "&eCost: &a&l$2000",
            "&7White Scrolls allow you to",
            "&7protect items from breaking."
    )),
    TRANSMOG_SCROLL("GUIName", "Lore", true, "TransmogScroll", "&dTransmog Scroll", List.of(
            "&eCost: &a&l$200",
            "&7This scroll allows you to organize",
            "&7your enchantments and tell you how many",
            "&7enchantments you currently have on the item."
    )),
    SLOT_CRYSTAL("GUIName", "GUILore", true, "Slot_Crystal", "&5&lSlot &b&lCrystal", List.of(
            "&7A rare crystal that is said to",
            "&7increase the amount of enchants",
            "&7that can be added onto an item.",
            "",
            "&eCost: &e&l$500"
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