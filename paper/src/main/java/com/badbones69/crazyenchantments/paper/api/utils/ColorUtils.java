package com.badbones69.crazyenchantments.paper.api.utils;

import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ColorUtils {

    public static void color(List<Color> colors, String colorString) {
        if (colorString.contains(", ")) {
            for (String key : colorString.split(", ")) {
                Color color = getColor(key);

                if (color != null) colors.add(color);
            }
        } else {
            Color color = getColor(colorString);

            if (color != null) colors.add(color);
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

    public static net.kyori.adventure.text.TextComponent legacyTranslateColourCodes(String input) {
        return (net.kyori.adventure.text.TextComponent) LegacyComponentSerializer.legacyAmpersand().deserialize(input).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static String toLegacy(Component text) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(text).replaceAll("§", "&").replaceAll("&&", "&");
    }

    public static String toPlainText(Component text) {
        return PlainTextComponentSerializer.plainText().serialize(text);
    }

    public static ItemBuilder getRandomPaneColor() {
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

        return new ItemBuilder().setMaterial(colors.get(ThreadLocalRandom.current().nextInt(colors.size())));
    }
}