package com.badbones69.crazyenchantments.utilities.misc;

import com.badbones69.crazyenchantments.api.FileManager;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F\\d]{6}");

    public static String color(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static void color(List<Color> colors, String colorString) {
        if (colorString.contains(", ")) {
            for (String color : colorString.split(", ")) {
                Color c = getColor(color);

                if (c != null) colors.add(c);
            }
        } else {
            Color c = getColor(colorString);

            if (c != null) colors.add(c);
        }
    }

    public static Color getColor(String color) {
        return switch (color.toUpperCase()) {
            case "AQUA" -> Color.AQUA;
            case "BLACK" -> Color.BLACK;
            case "BLUE" -> Color.BLUE;
            case "FUCHSIA" -> Color.FUCHSIA;
            case "GRAY" -> Color.GRAY;
            case "GREEN" -> Color.GREEN;
            case "LIME" -> Color.LIME;
            case "MAROON" -> Color.MAROON;
            case "NAVY" -> Color.NAVY;
            case "OLIVE" -> Color.OLIVE;
            case "ORANGE" -> Color.ORANGE;
            case "PURPLE" -> Color.PURPLE;
            case "RED" -> Color.RED;
            case "SILVER" -> Color.SILVER;
            case "TEAL" -> Color.TEAL;
            case "YELLOW" -> Color.YELLOW;
            default -> Color.WHITE;
        };
    }

    public static String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }

    public static String getPrefix(String string) {
        return color(FileManager.Files.CONFIG.getFile().getString("Settings.Prefix") + string);
    }

    public static String getPrefix() {
        return getPrefix("");
    }
}