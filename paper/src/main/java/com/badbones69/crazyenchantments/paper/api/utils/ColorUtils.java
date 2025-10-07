package com.badbones69.crazyenchantments.paper.api.utils;

import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Matcher.quoteReplacement;

public class ColorUtils {

    public static void color(@NotNull final List<Color> colors, @NotNull final String colorString) {
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

    public static String color(@NotNull final String message) { //TODO Remove the usage of bungee.
        Matcher matcher = Pattern.compile("#[a-fA-F\\d]{6}").matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static void sendMessage(@NotNull final CommandSender commandSender, @NotNull final String message, final boolean prefixToggle) {
        if (message.isEmpty()) return;

        String prefix = getPrefix();

        if (commandSender instanceof Player player) {
            if (!prefix.isEmpty() && prefixToggle) player.sendMessage(color(message.replaceAll("%prefix%", quoteReplacement(prefix))).replaceAll("%Prefix%", quoteReplacement(prefix))); else player.sendMessage(color(message));

            return;
        }

        if (!prefix.isEmpty() && prefixToggle) commandSender.sendMessage(color(message.replaceAll("%prefix%", quoteReplacement(prefix))).replaceAll("%Prefix%", quoteReplacement(prefix))); else commandSender.sendMessage(color(message));
    }

    public static String getPrefix() {
        return color(FileKeys.config.getConfiguration().getString("Settings.Prefix", "&8[&aCrazyEnchantments&8]: "));
    }

    public static String getPrefix(@NotNull final String msg) {
        return color(getPrefix() + msg);
    }

    public static String sanitizeColor(@NotNull final String msg) {
        return sanitizeFormat(color(msg));
    }

    public static String sanitizeFormat(@NotNull final String string) {
        return TextComponent.toLegacyText(TextComponent.fromLegacyText(string));
    }

    public static String removeColor(@NotNull final String msg) {
        return ChatColor.stripColor(msg);
    }

    public static net.kyori.adventure.text.TextComponent legacyTranslateColourCodes(@NotNull final String input) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(input).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static String toLegacy(@NotNull final Component text) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(text).replaceAll("§", "&").replaceAll("&&", "&");
    }

    public static String toPlainText(@NotNull final Component text) {
        return PlainTextComponentSerializer.plainText().serialize(text);
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
        return msg.replaceAll("([&§]?#[0-9a-fA-F]{6}|[&§][1-9a-fA-Fk-or])", "");
    }
}