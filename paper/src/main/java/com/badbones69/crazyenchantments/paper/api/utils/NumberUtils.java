package com.badbones69.crazyenchantments.paper.api.utils;

public class NumberUtils {

    public static boolean isInt(final String s) {
        if (s.isEmpty()) return false;

        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;

                continue;
            }

            if (Character.digit(s.charAt(i), 10) == -1) return false;
        }

        return true;
    }

    public static String toRoman(int number) {
        if (number > 3999 || number < 1) return String.valueOf(number);

        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] romanLetters = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder roman = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                number = number - values[i];
                roman.append(romanLetters[i]);
            }
        }

        return roman.toString();
    }

    /**
     * This converts a String into a number if using a roman numeral from I-X.
     * @param i The string you want to convert.
     * @return The roman numeral as a number.
     */
    public static int convertLevelInteger(final String i) {
        return switch (i) {
            case "I" -> 1;
            case "II" -> 2;
            case "III" -> 3;
            case "IV" -> 4;
            case "V" -> 5;
            case "VI" -> 6;
            case "VII" -> 7;
            case "VIII" -> 8;
            case "IX" -> 9;
            case "X" -> 10;
            default -> isInt(i) ? Integer.parseInt(i) : 0;
        };
    }

    /**
     * This converts an integer into a roman numeral if its between 1-3999 otherwise it will just be the number as a string.
     * @param number The integer you want to convert.
     * @return The integer as a roman numeral if between 1-3999 otherwise the number as a string.
     */
    public static String convertLevelString(final int number) {
        return toRoman(number);
    }
}