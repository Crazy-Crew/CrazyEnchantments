package com.badbones69.crazyenchantments.paper.api.enums;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazyenchantments.ConfigManager;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.platform.impl.Config;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;

public enum ShopOption {

    GKITZ(Config.gkitz_item, Config.gkitz_name, Config.gkitz_lore, Config.gkitz_glowing, Config.gkitz_in_gui, Config.gkitz_player, Config.gkitz_slot),
    BLACKSMITH(Config.blacksmith_item, Config.blacksmith_gui_name, Config.blacksmith_lore, Config.blacksmith_glowing, Config.blacksmith_in_gui, Config.blacksmith_player, Config.blacksmith_slot),
    TINKER(Config.tinker_item, Config.tinker_name, Config.tinker_lore, Config.tinker_glowing, Config.tinker_in_gui, Config.tinker_player, Config.tinker_slot),
    INFO(Config.info_item, Config.info_name, Config.info_lore, Config.info_glowing, Config.info_in_gui, Config.info_player, Config.info_slot),

    PROTECTION_CRYSTAL(Config.protection_crystal_item, Config.protection_crystal_gui_name, Config.protection_crystal_gui_lore, Config.gkitz_glowing, Config.protection_crystal_in_gui, Config.protection_crystal_gui_player, Config.protection_crystal_slot),
    DESTROY_DUST(Config.destroy_dust_item, Config.destroy_dust_gui_name, Config.destroy_dust_gui_lore, Config.destroy_dust_in_gui, Config.destroy_dust_slot),
    SUCCESS_DUST(Config.success_dust_item, Config.success_dust_gui_name, Config.success_dust_gui_lore, Config.success_dust_in_gui, Config.success_dust_slot),

    SCRAMBLER(Config.scrambler_item, Config.scrambler_gui_name, Config.scrambler_gui_lore, true, Config.scrambler_glowing, Config.scrambler_in_gui, Config.scrambler_gui_player, Config.scrambler_currency, Config.scrambler_cost, Config.scrambler_gui_slot),

    SLOT_CRYSTAL(Config.slot_crystal_item, Config.slot_crystal_gui_name, Config.slot_crystal_gui_lore, true, Config.slot_crystal_glowing, Config.slot_crystal_in_gui, Config.slot_crystal_gui_player, Config.slot_crystal_currency, Config.slot_crystal_cost, Config.slot_crystal_gui_slot),

    BLACK_SCROLL(Config.black_scroll_item, Config.black_scroll_gui_name, Config.black_scroll_gui_lore, true, Config.black_scroll_glowing, Config.black_scroll_in_gui, Config.black_scroll_gui_player, Config.black_scroll_currency, Config.black_scroll_cost, Config.black_scroll_gui_slot),
    WHITE_SCROLL(Config.white_scroll_item, Config.white_scroll_gui_name, Config.white_scroll_gui_lore, true, Config.white_scroll_glowing, Config.white_scroll_in_gui, Config.white_scroll_gui_player, Config.white_scroll_currency, Config.white_scroll_cost, Config.white_scroll_gui_slot),
    TRANSMOG_SCROLL(Config.transmog_scroll_item, Config.transmog_scroll_gui_name, Config.transmog_scroll_gui_lore, true, Config.black_scroll_glowing, Config.transmog_scroll_in_gui, Config.transmog_scroll_gui_player, Config.transmog_currency, Config.transmog_cost, Config.transmog_scroll_gui_slot);
    
    private static final HashMap<ShopOption, Option> shopOptions = new HashMap<>();

    private final Material material;
    private final String name;
    private final List<String> lore;
    private final boolean isBuyable;

    private boolean isGlowing;
    private final String player;
    private String currency;
    private int cost;
    private final int slot;

    private final boolean inGui;

    @NotNull
    private final SettingsManager config = ConfigManager.getConfig();

    ShopOption(Property<String> item, Property<String> name, Property<List<String>> lore, boolean isBuyable, Property<Boolean> inGui, Property<Boolean> isGlowing, Property<String> player, Property<String> currency, Property<Integer> cost, Property<Integer> slot) {
        this.material = Material.matchMaterial(this.config.getProperty(item));

        this.name = this.config.getProperty(name);

        this.lore = this.config.getProperty(lore);

        this.isBuyable = isBuyable;

        this.inGui = this.config.getProperty(inGui);

        this.isGlowing = this.config.getProperty(isGlowing);

        this.player = this.config.getProperty(player);

        this.currency = this.config.getProperty(currency);

        this.cost = this.config.getProperty(cost);

        this.slot = this.config.getProperty(slot);
    }

    ShopOption(Property<String> item, Property<String> name, Property<List<String>> lore, Property<Boolean> inGui, Property<Boolean> isGlowing, Property<String> player, Property<Integer> slot) {
        this.material = Material.matchMaterial(this.config.getProperty(item));

        this.name = this.config.getProperty(name);

        this.lore = this.config.getProperty(lore);

        this.isBuyable = false;

        this.inGui = this.config.getProperty(inGui);

        this.isGlowing = this.config.getProperty(isGlowing);

        this.player = this.config.getProperty(player);

        this.slot = this.config.getProperty(slot);
    }

    ShopOption(Property<String> item, Property<String> name, Property<List<String>> lore, Property<Boolean> inGui, Property<Integer> slot) {
        this.material = Material.matchMaterial(this.config.getProperty(item));

        this.name = this.config.getProperty(name);

        this.lore = this.config.getProperty(lore);

        this.isBuyable = false;

        this.inGui = this.config.getProperty(inGui);

        this.isGlowing = false;

        this.player = "";

        this.slot = this.config.getProperty(slot);
    }

    public String getName() {
        return this.name;
    }

    public Material getMaterial() {
        return this.material;
    }

    public boolean isGlowing() {
        return this.isGlowing;
    }

    public boolean isInGui() {
        return this.inGui;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public String getPlayer() {
        return this.player;
    }

    public static void loadShopOptions() {
        shopOptions.clear();

        for (ShopOption option : values()) {
            ItemBuilder builder = new ItemBuilder()
                    .setName(option.getName())
                    .setLore(option.getLore())
                    .setMaterial(option.getMaterial())
                    .setPlayerName(option.getPlayer())
                    .setGlow(option.isGlowing());

            Option shop = new Option(builder, option.getSlot(), option.isInGui(), option.getCost(), option.getCurrency());

            shopOptions.put(option, shop);
        }
    }
    
    public ItemStack getItem() {
        return getItemBuilder().build();
    }
    
    public ItemBuilder getItemBuilder() {
        return shopOptions.get(this).itemBuilder();
    }
    
    public int getSlot() {
        return this.slot;
    }
    
    public int getCost() {
        return this.cost;
    }
    
    public Currency getCurrency() {
        return Currency.getCurrency(this.currency);
    }
    
    public boolean isBuyable() {
        return this.isBuyable;
    }

    private record Option(ItemBuilder itemBuilder, int slot, boolean inGUI, int cost, Currency currency) {}
}