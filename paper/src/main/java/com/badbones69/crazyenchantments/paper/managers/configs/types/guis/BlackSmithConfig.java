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

public class BlackSmithConfig extends IConfig {

    private final String guiName;
    private final int guiSize;

    private final List<String> resultsLore;
    private final String resultsFound;
    private final String resultsNone;
    private final String resultsType;

    private final Currency transactionCurrency;

    private final int transactionEnchantCost;
    private final int transactionUpgrade;
    private final int transactionPowerUp;

    public BlackSmithConfig(@NotNull final ConfigurationSection section) {
        this.guiName = section.getString("GUIName", "<dark_gray><bold>The Black Smith");
        this.guiSize = 27;

        this.resultsLore = ConfigUtils.getStringList(section, List.of(
                "<gray>No results could be found.",
                "<gray>Please put in two books of",
                "<gray>the same enchantment and level.",
                "<gray>Or put in two items to combined",
                "<gray>the enchantments on them."
        ), "Results.Not-Found-Lore");

        this.resultsFound = section.getString("Results.Found", "<red><bold>Cost: <gold><bold>{cost} XP");
        this.resultsNone = section.getString("Results.None", "<red><bold>No Results");
        this.resultsType = section.getString("Results.Type", "BARRIER");

        this.transactionEnchantCost = section.getInt("Transaction.Costs.Add-Enchantment", 3);
        this.transactionCurrency = Currency.getCurrency(section.getString("Transaction.Currency", "XP_LEVEL"));
        this.transactionUpgrade = section.getInt("Transaction.Costs.Book-Upgrade", 5);
        this.transactionPowerUp = section.getInt("Transaction.Costs.Power-Up", 5);
    }

    public @NotNull final List<Component> asResultsComponents(@Nullable final Audience audience) {
        return asComponents(audience, this.resultsLore);
    }

    public @NotNull final Component asResultsFoundComponent(@Nullable final Audience audience) {
        return asComponent(audience, this.resultsFound);
    }

    public @NotNull final Component asResultsNoneComponent(@Nullable final Audience audience) {
        return asComponent(audience, this.resultsNone);
    }

    public @NotNull final String getGuiName() {
        return this.guiName;
    }

    public final int getGuiSize() {
        return this.guiSize;
    }

    public @NotNull final Currency getTransactionCurrency() {
        return this.transactionCurrency;
    }

    public @NotNull final String getResultsType() {
        return this.resultsType;
    }

    public final int getTransactionEnchantCost() {
        return this.transactionEnchantCost;
    }

    public final int getTransactionUpgrade() {
        return this.transactionUpgrade;
    }

    public final int getTransactionPowerUp() {
        return this.transactionPowerUp;
    }
}