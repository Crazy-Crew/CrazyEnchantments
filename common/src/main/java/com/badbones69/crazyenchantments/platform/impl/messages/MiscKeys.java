package com.badbones69.crazyenchantments.platform.impl.messages;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class MiscKeys implements SettingsHolder {

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyEnchantments/issues",
                "Features: https://github.com/Crazy-Crew/CrazyEnchantments/issues",
                ""
        };

        conf.setComment("misc", header);
    }

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> unknown_command = newProperty("misc.unknown-command", "{prefix}&cThis command is not known.");

    @Comment("A list of available placeholders: {prefix}, {usage}")
    public static final Property<String> correct_usage = newProperty("misc.correct-usage", "{prefix}&cThe correct usage for this command is &e{usage}");

}