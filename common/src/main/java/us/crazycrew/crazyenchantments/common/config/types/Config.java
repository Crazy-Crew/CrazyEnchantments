package us.crazycrew.crazyenchantments.common.config.types;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;
import org.jetbrains.annotations.NotNull;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class Config implements SettingsHolder {
    
    protected Config() {}

    @Override
    public void registerComments(@NotNull CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyEnchantments/issues",
                "Features: https://github.com/Crazy-Crew/CrazyEnchantments/issues",
                ""
        };

        String[] deprecation = {
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens.",
                ""
        };
        
        conf.setComment("Settings", header);
    }
    
    @Comment("The prefix that shows up for all commands.")
    public static final Property<String> command_prefix = newProperty("Settings.Prefix", "&7[&6CrazyVouchers&7]: ");

    @Comment("Pick which locale you want to use if your server is in another language.")
    public static final Property<String> locale_file = newProperty("Settings.locale", "en-US");

    @Comment("The prefix that shows up for all console logs.")
    public static final Property<String> console_prefix = newProperty("Settings.Console-Prefix", "&7[&cCrazyVouchers&7]: ");

    @Comment({
            "Sends anonymous statistics about how the plugin is used to bstats.org.",
            "bstats is a service for plugin developers to find out how the plugin being used,",
            "This information helps us figure out how to better improve the plugin."
    })
    public static final Property<Boolean> toggle_metrics = newProperty("settings.Toggle_Metrics", true);

    @Comment("Whether you want CrazyEnchantments to shut up or not, This option is ignored by errors.")
    public static final Property<Boolean> verbose_logging = PropertyInitializer.newProperty("Settings.verbose_logging", true);
}