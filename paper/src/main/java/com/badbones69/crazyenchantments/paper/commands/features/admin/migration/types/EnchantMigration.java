package com.badbones69.crazyenchantments.paper.commands.features.admin.migration.types;

import com.badbones69.crazyenchantments.objects.User;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.commands.features.admin.migration.interfaces.IEnchantMigration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenchantments.constants.MessageKeys;
import java.util.*;

public class EnchantMigration extends IEnchantMigration {

    public EnchantMigration(@NotNull final CommandSender sender) {
        super(sender);
    }

    @Override
    public void run() {
        final EntityEquipment equipment = this.player.getEquipment();
        final ItemStack[] armor = equipment.getArmorContents();

        final List<ItemStack> items = Arrays.stream(armor).filter(Objects::nonNull).toList();

        if (items.isEmpty()) {
            //todo() send message

            return;
        }

        final User user = this.userRegistry.getUser(this.player);

        for (final ItemStack item : items) {
            StringBuilder builder = new StringBuilder();

            final Map<CEnchantment, Integer> enchantments = this.instance.getEnchantments(item);

            if (enchantments.isEmpty()) {
                this.fusion.log("warn", "The item with the display name {} does not have any enchantments", PlainTextComponentSerializer.plainText().serialize(item.displayName()));

                continue;
            }

            enchantments.forEach((enchantment, level) -> {
                user.sendMessage(MessageKeys.show_enchants_format_base, new HashMap<>() {{
                    put("{enchant}", enchantment.getName());
                    put("{level}", String.valueOf(level));
                }});
            });

            user.sendMessage(MessageKeys.show_enchants_format_main, new HashMap<>() {{
                put("{item}", item.getType().toString());
                put("{item_enchants}", builder.toString());
            }});
        }
    }
}