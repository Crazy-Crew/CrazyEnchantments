package com.badbones69.crazyenchantments.utilities.misc;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class NumberUtils {

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    /**
     * This converts a String into a number if using a roman numeral from I-X.
     * @param i The string you want to convert.
     * @return The roman numeral as a number.
     */
    public static int convertLevelInteger(String i) {
        switch (i) {
            case "I" -> {
                return 1;
            }

            case "II" -> {
                return 2;
            }

            case "III" -> {
                return 3;
            }

            case "IV" -> {
                return 4;
            }

            case "V" -> {
                return 5;
            }

            case "VI" -> {
                return 6;
            }

            case "VII" -> {
                return 7;
            }

            case "VIII" -> {
                return 8;
            }

            case "IX" -> {
                return 9;
            }

            case "X" -> {
                return 10;
            }

            default -> {
                if (isInt(i)) {
                    return Integer.parseInt(i);
                } else {
                    return 0;
                }
            }
        }
    }

    public static String checkLevels(ItemStack item, String customName) {
        String line = "";

        if (ItemUtils.verifyItemLore(item)) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasLore()) {
                List<String> itemLore = meta.getLore();

                if (itemLore != null) {
                    for (String lore : itemLore) {
                        if (lore.contains(customName)) {
                            line = lore;
                            break;
                        }
                    }
                }
            }
        }

        return line;
    }
}