package com.badbones69.crazyenchantments.paper.commands.features.admin.validation.types;

import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.commands.features.admin.validation.IEnchantMigration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
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

        for (final ItemStack item : items) {
            StringBuilder builder = new StringBuilder();

            final Map<CEnchantment, Integer> enchantments = this.instance.getEnchantments(item);

            if (enchantments.isEmpty()) {
                this.fusion.log("warn", "The item with the display name {} does not have any enchantments", PlainTextComponentSerializer.plainText().serialize(item.displayName()));

                continue;
            }

            enchantments.forEach((enchantment, level) -> {
                builder.append(Messages.BASE_UPDATE_ENCHANTS.getMessageNoPrefix(new HashMap<>() {{
                    put("%enchant%", enchantment.getName());
                    put("%level%", String.valueOf(level));
                }}));
            });

            String message = Messages.MAIN_UPDATE_ENCHANTS.getMessageNoPrefix(new HashMap<>() {{
                put("%item%", item.getType().toString());
                put("%itemEnchants%", builder.toString());
            }});

            sender.sendMessage(message);
        }
    }
}