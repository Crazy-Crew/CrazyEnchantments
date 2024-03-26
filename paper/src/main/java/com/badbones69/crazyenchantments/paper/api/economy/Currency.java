package com.badbones69.crazyenchantments.paper.api.economy;

public enum Currency {

    VAULT("Vault"),
    XP_LEVEL("XP_Level"),
    XP_TOTAL("XP_Total");

    private final String name;

    Currency(String name) {
        this.name = name;
    }

    /**
     * Checks if it is a compatible currency.
     *
     * @param currency The currency name you are checking.
     * @return True if it is supported and false if not.
     */
    public static boolean isCurrency(String currency) {
        for (Currency value : Currency.values()) {
            if (currency.equalsIgnoreCase(value.getName())) return true;
        }

        return false;
    }

    /**
     * Get a currency enum.
     *
     * @param currency The currency you want.
     * @return The currency enum.
     */
    public static Currency getCurrency(String currency) {
        for (Currency value : Currency.values()) {
            if (currency.equalsIgnoreCase(value.getName())) return value;
        }

        return null;
    }

    /**
     * Get the name of the currency.
     *
     * @return The name of the currency.
     */
    public String getName() {
        return name;
    }
}