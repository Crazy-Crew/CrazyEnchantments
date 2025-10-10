package com.badbones69.crazyenchantments.paper.api.utils;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ColorUtils {

    public static List<Color> getColors(@NotNull final String line) {
        final List<Color> colors = new ArrayList<>();

        if (line.contains(", ")) {
            for (String key : line.split(", ")) {
                final Color color = getColor(key);

                if (color != null) {
                    colors.add(color);
                }
            }
        } else {
            final Color color = getColor(line);

            if (color != null) {
                colors.add(color);
            }
        }

        return colors;
    }

    public static Color getColor(@NotNull final String color) {
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

    public static void sendMessage(@NotNull final CommandSender commandSender, @NotNull final String message, final boolean prefixToggle) {
        /*if (message.isEmpty()) return;

        String prefix = getPrefix();

        if (commandSender instanceof Player player) {
            if (!prefix.isEmpty() && prefixToggle) player.sendMessage(color(message.replaceAll("%prefix%", quoteReplacement(prefix))).replaceAll("%Prefix%", quoteReplacement(prefix))); else player.sendMessage(color(message));

            return;
        }

        if (!prefix.isEmpty() && prefixToggle) commandSender.sendMessage(color(message.replaceAll("%prefix%", quoteReplacement(prefix))).replaceAll("%Prefix%", quoteReplacement(prefix))); else commandSender.sendMessage(color(message));*/
    }

    public static String getPrefix() {
        //return color(FileKeys.config.getYamlConfiguration().getString("Settings.Prefix", "&8[&aCrazyEnchantments&8]: "));
        return "";
    }

    public static String getPrefix(@NotNull final String msg) {
        //return color(getPrefix() + msg);
        return msg;
    }

    public static ItemBuilder getRandomPaneColor() {
        Random random = new Random();

        List<String> colors = Arrays.asList(
                "WHITE_STAINED_GLASS_PANE",
                "ORANGE_STAINED_GLASS_PANE",
                "MAGENTA_STAINED_GLASS_PANE",
                "LIGHT_BLUE_STAINED_GLASS_PANE",
                "YELLOW_STAINED_GLASS_PANE",
                "LIME_STAINED_GLASS_PANE",
                "PINK_STAINED_GLASS_PANE",
                "GRAY_STAINED_GLASS_PANE",
                // Skipped 8 due to it being basically invisible in a GUI.
                "CYAN_STAINED_GLASS_PANE",
                "PURPLE_STAINED_GLASS_PANE",
                "BLUE_STAINED_GLASS_PANE",
                "BROWN_STAINED_GLASS_PANE",
                "GREEN_STAINED_GLASS_PANE",
                "RED_STAINED_GLASS_PANE",
                "BLACK_STAINED_GLASS_PANE");

        return new ItemBuilder().setMaterial(colors.get(random.nextInt(colors.size())));
    }

    public static String stripStringColour(@NotNull final String msg) {
        //return msg.replaceAll("([&§]?#[0-9a-fA-F]{6}|[&§][1-9a-fA-Fk-or])", "");
        return msg;
    }
}