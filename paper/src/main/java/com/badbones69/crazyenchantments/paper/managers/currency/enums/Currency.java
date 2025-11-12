package com.badbones69.crazyenchantments.paper.managers.currency.enums;

import org.jetbrains.annotations.NotNull;

public enum Currency {
    
    VAULT("Vault"),
    XP_LEVEL("XP_Level"),
    XP_TOTAL("XP_Total");
    
    private final String name;
    
    Currency(@NotNull final String name) {
        this.name = name;
    }
    
    /**
     * Checks if it is a compatible currency.
     * @param currency The currency name you are checking.
     * @return True if it is supported and false if not.
     */
    public static boolean isCurrency(@NotNull final String currency) {
        boolean isCurrency = false;

        for (final Currency value : Currency.values()) {
            if (!currency.equalsIgnoreCase(value.getName())) continue;

            isCurrency = true;

            break;
        }

        return isCurrency;
    }
    
    /**
     * Get a currency enum.
     * @param name The currency you want.
     * @return The currency enum.
     */
    public static Currency getCurrency(@NotNull final String name) {
        Currency currency = Currency.XP_LEVEL;

        for (final Currency value : Currency.values()) {
            if (!name.equalsIgnoreCase(value.getName())) continue;

            currency = value;

            break;
        }

        return currency;
    }
    
    /**
     * Get the name of the currency.
     * @return The name of the currency.
     */
    public String getName() {
        return this.name;
    }
}