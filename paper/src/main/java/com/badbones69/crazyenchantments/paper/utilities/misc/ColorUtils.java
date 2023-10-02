package com.badbones69.crazyenchantments.paper.utilities.misc;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.cluster.bukkit.utils.LegacyUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Color;
import org.bukkit.plugin.java.JavaPlugin;
import us.crazycrew.crazyenchantments.common.config.types.Config;
import java.util.List;

public class ColorUtils {

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
        return PlainTextComponentSerializer.plainText().serialize(LegacyComponentSerializer.legacySection().deserialize(msg));
    }

    public static String getPrefix(String string) {
        CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

        return LegacyUtils.color(plugin.getCrazyHandler().getConfigManager().getConfig().getProperty(Config.command_prefix) + string);
    }

    public static String getPrefix() {
        return getPrefix("");
    }

    public static TextComponent legacyTranslateColourCodes(String input) {
        return (TextComponent) LegacyComponentSerializer.legacyAmpersand().deserialize(input).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static String toLegacy(Component text) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(text);
    }

    public static String toPlainText(Component text) {
        return PlainTextComponentSerializer.plainText().serialize(text);
    }
}