package com.badbones69.crazyenchantments.paper.api.utils;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import java.util.Optional;

@NullMarked
public class AttributeUtils {

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private static final FusionPaper fusion = plugin.getFusion();

    public static void setHealth(final LivingEntity entity, final int health) {
        Optional.ofNullable(entity.getAttribute(Attribute.MAX_HEALTH)).ifPresentOrElse(attribute -> {
            if (health > 0) {
                attribute.setBaseValue(health);

                return;
            }

            attribute.setBaseValue(attribute.getBaseValue());
        }, () -> fusion.log(Level.WARNING, "Could not find the MAX_HEALTH attribute on the Entity %s(%s) with type %s", entity.getName(), entity.getUniqueId(), entity.getType()));
    }
}
