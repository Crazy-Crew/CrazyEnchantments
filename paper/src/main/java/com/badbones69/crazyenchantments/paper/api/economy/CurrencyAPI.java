package com.badbones69.crazyenchantments.paper.api.economy;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.economy.vault.VaultSupport;
import com.badbones69.crazyenchantments.paper.api.enums.ShopOption;
import com.badbones69.crazyenchantments.paper.api.objects.Category;
import com.badbones69.crazyenchantments.paper.api.objects.LostBook;
import com.badbones69.crazyenchantments.paper.support.PluginSupport.SupportedPlugins;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CurrencyAPI {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    /**
     * Get the amount that a player has from a specific currency.
     * @param player The player you wish to get the amount from.
     * @param currency The currency you wish to get from.
     * @return The amount that the player has of that currency.
     */
    public int getCurrency(@NotNull final Player player, @NotNull final Currency currency) {
        try {
            return switch (currency) {
                case VAULT -> player.getLevel();
                //case VAULT -> (int) starter.getVaultSupport().getVault().getBalance(player);
                case XP_LEVEL -> player.getLevel();
                case XP_TOTAL -> getTotalExperience(player);
            };
        } catch (Exception | NoClassDefFoundError ignored) {}
        return 0;
    }
    
    /**
     * Take an amount from a player's currency.
     * @param player The player you wish to take from.
     * @param category The category you wish to use.
     */
    public void takeCurrency(@NotNull final Player player, @NotNull final Category category) {
        takeCurrency(player, category.getCurrency(), category.getCost());
    }
    
    /**
     * Take an amount from a player's currency.
     * @param player The player you wish to take from.
     * @param lostBook The lostbook you wish to use.
     */
    public void takeCurrency(@NotNull final Player player, @NotNull final LostBook lostBook) {
        takeCurrency(player, lostBook.getCurrency(), lostBook.getCost());
    }
    
    /**
     * Take an amount from a player's currency.
     * @param player The player you wish to take from.
     * @param option The ShopOption you wish to use.
     */
    public void takeCurrency(@NotNull final Player player, @NotNull final ShopOption option) {
        takeCurrency(player, option.getCurrency(), option.getCost());
    }
    
    /**
     * Take an amount from a player's currency.
     * @param player The player you wish to take from.
     * @param currency The currency you wish to use.
     * @param amount The amount you want to take.
     */
    public void takeCurrency(@NotNull final Player player, @NotNull final Currency currency, final int amount) {
        try {
            switch (currency) {
                //case VAULT -> starter.getVaultSupport().getVault().withdrawPlayer(player, amount);
                case XP_LEVEL -> player.setLevel(player.getLevel() - amount);
                case XP_TOTAL -> takeTotalExperience(player, amount);
            }
        } catch (Exception | NoClassDefFoundError ignored) {}
    }
    
    /**
     * Give an amount to a player's currency.
     * @param player The player you are giving to.
     * @param currency The currency you want to use.
     * @param amount The amount you are giving to the player.
     */
    public void giveCurrency(@NotNull final Player player, @NotNull final Currency currency, final int amount) {
        try {
            switch (currency) {
                //case VAULT -> starter.getVaultSupport().getVault().depositPlayer(player, amount);
                case XP_LEVEL -> player.setLevel(player.getLevel() + amount);
                case XP_TOTAL -> takeTotalExperience(player, -amount);
            }
        } catch (Exception | NoClassDefFoundError ignored) {}
    }
    
    /**
     * Checks if the player has enough of a currency.
     * @param player The player you are checking.
     * @param category The category you wish to check.
     * @return True if they have enough to buy it or false if they don't.
     */
    public boolean canBuy(@NotNull final Player player, @NotNull final Category category) {
        return canBuy(player, category.getCurrency(), category.getCost());
    }
    
    /**
     * Checks if the player has enough of a currency.
     * @param player The player you are checking.
     * @param lostBook The lostBook you wish to check.
     * @return True if they have enough to buy it or false if they don't.
     */
    public boolean canBuy(@NotNull final Player player, @NotNull final LostBook lostBook) {
        return canBuy(player, lostBook.getCurrency(), lostBook.getCost());
    }
    
    /**
     * Checks if the player has enough of a currency.
     * @param player The player you are checking.
     * @param option The ShopOption you wish to check.
     * @return True if they have enough to buy it or false if they don't.
     */
    public boolean canBuy(@NotNull final Player player, @NotNull final ShopOption option) {
        return canBuy(player, option.getCurrency(), option.getCost());
    }
    
    /**
     * Checks if the player has enough of a currency.
     * @param player The player you are checking.
     * @param currency The currency you wish to check.
     * @param cost The cost of the item you are checking.
     * @return True if they have enough to buy it or false if they don't.
     */
    public boolean canBuy(@NotNull final Player player, @NotNull final Currency currency, final int cost) {
        return getCurrency(player, currency) >= cost;
    }
    
    private void takeTotalExperience(@NotNull final Player player, final int amount) {
        int total = getTotalExperience(player) - amount;
        player.setTotalExperience(0);
        player.setTotalExperience(total);
        player.setLevel(0);
        player.setExp(0);

        while (total > player.getExpToLevel()) {
            total -= player.getExpToLevel();
            player.setLevel(player.getLevel() + 1);
        }

        float xp = (float) total / (float) player.getExpToLevel();
        player.setExp(xp);
    }
    
    private int getTotalExperience(@NotNull final Player player) { // https://www.spigotmc.org/threads/72804
        int experience;
        int level = player.getLevel();

        if (level >= 0 && level <= 15) {
            experience = (int) Math.ceil(Math.pow(level, 2) + (6 * level));
            int requiredExperience = 2 * level + 7;
            double currentExp = Double.parseDouble(Float.toString(player.getExp()));
            experience += (int) Math.ceil(currentExp * requiredExperience);
            return experience;
        } else if (level > 15 && level <= 30) {
            experience = (int) Math.ceil((2.5 * Math.pow(level, 2) - (40.5 * level) + 360));
            int requiredExperience = 5 * level - 38;
            double currentExp = Double.parseDouble(Float.toString(player.getExp()));
            experience += (int) Math.ceil(currentExp * requiredExperience);
            return experience;
        } else {
            experience = (int) Math.ceil((4.5 * Math.pow(level, 2) - (162.5 * level) + 2220));
            int requiredExperience = 9 * level - 158;
            double currentExp = Double.parseDouble(Float.toString(player.getExp()));
            experience += (int) Math.ceil(currentExp * requiredExperience);
            return experience;
        }
    }

    /**
     * Loads the currency if it is on the server.
     */
    public void loadCurrency() {
        for (SupportedPlugins supportedPlugin : SupportedPlugins.values()) {
            if (supportedPlugin.isPluginLoaded() && supportedPlugin.getLoadedPlugin().isEnabled()) {
                if (supportedPlugin == SupportedPlugins.VAULT) {
                    //starter.setVaultSupport(new VaultSupport());
                }

                return;
            }
        }

        plugin.getLogger().warning("No eco plugin found or the eco plugin didn't enable. Any economy based feature will not work.");
    }
}