package com.badbones69.crazyenchantments.platform;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;

public class BlockConfig implements SettingsHolder {

    @Comment("All blocks that can't be broken by the Blast Enchantment.")
    public static final Property<List<String>> block_list = newListProperty("blast-blacklist", List.of(
            "obsidian",
            "bedrock"
    ));
}