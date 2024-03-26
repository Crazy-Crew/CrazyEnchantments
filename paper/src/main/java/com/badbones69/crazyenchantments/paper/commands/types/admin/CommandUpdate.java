package com.badbones69.crazyenchantments.paper.commands.types.admin;

import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.Enchant;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.badbones69.crazyenchantments.paper.commands.BaseCommand;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.google.gson.Gson;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CommandUpdate extends BaseCommand {

    private final @NotNull Starter starter = this.plugin.getStarter();

    private final @NotNull EnchantmentBookSettings bookSettings = this.starter.getEnchantmentBookSettings();

    private final Gson gson = new Gson();

    @Command("update-enchants")
    @Permission(value = "crazyenchantments.update-enchants", def = PermissionDefault.OP)
    public void updateEnchants(Player player) {
        NamespacedKey key = DataKeys.enchantments.getNamespacedKey();

        List<Integer> slots = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40);

        for (int value : slots) {
            ItemStack itemStack = player.getInventory().getItem(value);

            if (itemStack == null || !itemStack.hasItemMeta() || itemStack.lore() == null) return;

            ArrayList<Component> itemLore = new ArrayList<>();

            AtomicReference<Enchant> enchant = new AtomicReference<>(new Enchant(null));

            itemStack.editMeta(itemMeta -> {
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();

                if (container.has(key)) {
                    enchant.set(gson.fromJson(container.get(key, PersistentDataType.STRING), Enchant.class));
                }

                List<Component> lore = itemMeta.lore();

                if (lore != null) {
                    for (Component line : lore) {
                        String strippedName = ColorUtils.toPlainText(line);
                        boolean isAdded = false;

                        for (CEnchantment active : this.bookSettings.getRegisteredEnchantments()) {
                            String lowerCaseStrippedName = strippedName.toLowerCase();
                            String lowerCaseCustom = active.getCustomName().toLowerCase().replaceAll("([&§]?#[0-9a-f]{6}|[&§][1-9a-fk-or])", "");
                            String lowerCaseName = active.getName().toLowerCase();

                            if (!lowerCaseStrippedName.contains(lowerCaseCustom) && !lowerCaseStrippedName.contains(lowerCaseName)) continue;

                            Enchant object = enchant.get();

                            if (object.hasEnchantment(active.getName())) break;

                            object.addEnchantment(active.getName(), NumberUtils.convertLevelInteger(strippedName.split(" ")[strippedName.split(" ").length - 1]));

                            lore.add(ColorUtils.legacyTranslateColourCodes(active.getCustomName() + " " + NumberUtils.toRoman(object.getLevel(active.getName()))));

                            isAdded = true;

                            break;
                        }

                        if (!isAdded) itemLore.add(line);
                    }
                }

                itemMeta.lore(itemLore);

                container.set(key, PersistentDataType.STRING, gson.toJson(enchant.get()));
            });

            player.getInventory().setItem(value, itemStack);
        }

        ColorUtils.sendMessage(player, "%prefix%&aAll items in your inventory have been migrated.", true);
    }
}