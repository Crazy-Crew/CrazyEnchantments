package com.badbones69.crazyenchantments.paper.api.enums;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.ryderbelserion.common.api.enums.Files;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.audience.Audience;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.HashMap;

public enum ShopOption {
    
    GKITZ("GKitz", "GKitz", "Name", "Lore", false),
    BLACKSMITH("BlackSmith", "BlackSmith", "Name", "Lore", false),
    TINKER("Tinker", "Tinker", "Name", "Lore", false),
    INFO("Info", "Info", "Name", "Lore", false),
    
    PROTECTION_CRYSTAL("ProtectionCrystal", "ProtectionCrystal", "GUIName", "GUILore", true),
    SUCCESS_DUST("SuccessDust", "Dust.SuccessDust", "GUIName", "GUILore", true),
    DESTROY_DUST("DestroyDust", "Dust.DestroyDust", "GUIName", "GUILore", true),
    SCRAMBLER("Scrambler", "Scrambler", "GUIName", "GUILore", true),
    
    BLACK_SCROLL("BlackScroll", "BlackScroll", "GUIName", "Lore", true),
    WHITE_SCROLL("WhiteScroll", "WhiteScroll", "GUIName", "Lore", true),
    TRANSMOG_SCROLL("TransmogScroll", "TransmogScroll", "GUIName", "Lore", true),
    SLOT_CRYSTAL("Slot_Crystal", "Slot_Crystal", "GUIName", "GUILore", true);
    
    private static final HashMap<ShopOption, Option> shopOptions = new HashMap<>();
    private final String optionPath;
    private final String path;
    private final String namePath;
    private final String lorePath;
    private Option option;
    private final boolean buyable;
    
    ShopOption(String optionPath, String path, String namePath, String lorePath, boolean buyable) {
        this.optionPath = optionPath;
        this.path = path;
        this.namePath = namePath;
        this.lorePath = lorePath;
        this.buyable = buyable;
    }

    @NotNull
    private final static CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final static FusionPaper fusion = plugin.getPlatform().getFusion();
    
    public static void loadShopOptions() {
        final CommentedConfigurationNode config = Files.CONFIG.getConfiguration();
        shopOptions.clear();

        final CommentedConfigurationNode section = config.node("Settings");
        final CommentedConfigurationNode costs = section.node("Costs");

        for (final ShopOption shopOption : values()) {
            final String shopPath = shopOption.getPath();
            final String optionPath = shopOption.getOptionPath();

            final CommentedConfigurationNode shopSection = section.node(Methods.getString(shopPath));
            final CommentedConfigurationNode optionSection = costs.node(Methods.getString(optionPath));

            final Option option = new Option(
                    new ItemBuilder()
                            .setMaterial(shopSection.node("Item").getString("CHEST"))
                            .setName(shopSection.node(shopOption.getNamePath()).getString("&cError getting name for %s".formatted(shopPath)))
                            .setLore(StringUtils.getStringList(shopSection.node(shopOption.getLorePath())))
                            .setItemModel(shopSection.node("Model", "Namespace").getString(""), shopSection.node("Model", "Key").getString(""))
                            .setPlayerName(shopSection.node("Player").getString(""))
                            .setGlow(shopSection.node("Glowing").getBoolean(false)),
                    shopSection.node("Slot").getInt(1)-1,
                    shopSection.node("InGUI").getBoolean(true),
                    optionSection.node("Cost").getInt(100),
                    Currency.getCurrency(optionSection.node("Currency").getString("Vault"))
            );

            try {
                shopOptions.put(shopOption, option);
            } catch (final Exception exception) {
                fusion.log(com.ryderbelserion.fusion.api.enums.Level.error, "Failed to load %s", exception, optionPath);
            }
        }
    }
    
    public ItemStack getItem(final Audience player) {
        return getItemBuilder().build(player);
    }
    
    public ItemBuilder getItemBuilder() {
        return shopOptions.get(this).itemBuilder();
    }
    
    public int getSlot() {
        return shopOptions.get(this).slot();
    }
    
    public boolean isInGUI() {
        return shopOptions.get(this).inGUI();
    }
    
    public int getCost() {
        return shopOptions.get(this).cost();
    }
    
    public Currency getCurrency() {
        return shopOptions.get(this).currency();
    }
    
    private String getOptionPath() {
        return this.optionPath;
    }
    
    private String getPath() {
        return this.path;
    }
    
    private String getNamePath() {
        return this.namePath;
    }
    
    private String getLorePath() {
        return this.lorePath;
    }
    
    public boolean isBuyable() {
        return this.buyable;
    }

    private record Option(ItemBuilder itemBuilder, int slot, boolean inGUI, int cost, Currency currency) {}
}