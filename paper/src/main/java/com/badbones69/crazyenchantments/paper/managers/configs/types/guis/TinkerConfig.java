package com.badbones69.crazyenchantments.paper.managers.configs.types.guis;

import com.badbones69.crazyenchantments.paper.managers.currency.enums.Currency;
import com.badbones69.crazyenchantments.paper.api.utils.ConfigUtils;
import com.badbones69.crazyenchantments.paper.managers.configs.interfaces.IConfig;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class TinkerConfig extends IConfig {

    private final List<String> tradeButtonLore;
    private final String tradeButtonName;
    private final Currency currency;
    private final String guiName;
    
    private final List<String> bottleLore;
    private final String bottleItem;
    private final String bottleName;

    public TinkerConfig(@NotNull final ConfigurationSection section) {
        this.tradeButtonName = section.getString("TradeButton", "<yellow>Click to accept the trade");
        this.tradeButtonLore = ConfigUtils.getStringList(section, List.of(), "TradeButton-Lore");
        this.currency = Currency.getCurrency(section.getString("Currency", "X_LEVEL"));
        this.guiName = section.getString("GUIName", "<gray><bold>The <dark_red><bold>Crazy <red><bold>Tinkerer");
        
        this.bottleLore = ConfigUtils.getStringList(section, List.of(
                "<green><bold>Recycled <gold>{total} xp",
                "<gray><bold>(<gold><bold>!<gray><bold>) <gray>Throw to get XP."
        ), "BottleOptions.Lore");
        this.bottleItem = section.getString("BottleOptions.Item", "EXPERIENCE_BOTTLE");
        this.bottleName = section.getString("BottleOptions.Name", "<green>Recycled XP");
    }

    public @NotNull final List<Component> asTradeButtonLoreComponents(@Nullable final Audience audience) {
        return asComponents(audience, this.tradeButtonLore);
    }

    public @NotNull final Component asTradeButtonComponent(@Nullable final Audience audience) {
        return asComponent(audience, this.tradeButtonName);
    }

    public @NotNull final List<Component> asBottleLore(@Nullable final Audience audience) {
        return asComponents(audience, this.bottleLore);
    }

    public @NotNull final Component asBottleName(@Nullable final Audience audience) {
        return asComponent(audience, this.bottleName);
    }

    public @NotNull final String getBottleItem() {
        return this.bottleItem;
    }

    public @NotNull final Currency getCurrency() {
        return this.currency;
    }

    public @NotNull final String getGuiName() {
        return this.guiName;
    }
}