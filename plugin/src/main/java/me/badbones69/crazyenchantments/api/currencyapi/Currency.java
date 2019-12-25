package me.badbones69.crazyenchantments.api.currencyapi;

public enum Currency {
    
    VAULT("Vault"),
    XP_LEVEL("XP_Level"),
    XP_TOTAL("XP_Total");
    
    private String name;
    
    private Currency(String name) {
        this.name = name;
    }
    
    /**
     * Checks if it is a compatible currency.
     * @param currency The currency name you are checking.
     * @return True if it is supported and false if not.
     */
    public static boolean isCurrency(String currency) {
        for (Currency c : Currency.values()) {
            if (currency.equalsIgnoreCase(c.getName())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get a currency enum.
     * @param currency The currency you want.
     * @return The currency enum.
     */
    public static Currency getCurrency(String currency) {
        for (Currency c : Currency.values()) {
            if (currency.equalsIgnoreCase(c.getName())) {
                return c;
            }
        }
        return null;
    }
    
    /**
     * Get the name of the currency.
     * @return The name of the currency.
     */
    public String getName() {
        return name;
    }
    
}