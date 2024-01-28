package com.badbones69.crazyenchantments.paper.processors;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.PluginSupport;
import com.badbones69.crazyenchantments.paper.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.HellForgedUseEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.utilities.misc.EnchantUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.Objects;

public class ArmorMoveProcessor extends Processor<PlayerMoveEvent> {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    // Plugin Support.
    private final PluginSupport pluginSupport = starter.getPluginSupport();

    private final SpartanSupport spartanSupport = starter.getSpartanSupport();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    private final Processor<Runnable> syncProcessor;

    public ArmorMoveProcessor() {
        this.syncProcessor = new RunnableSyncProcessor();
    }

    public void stop() {
        syncProcessor.stop();
        super.stop();
    }

    public void start() {
        syncProcessor.start();
        super.start();
    }

    public void process(PlayerMoveEvent process) {
        Player player = process.getPlayer();

        for (final ItemStack armor : Objects.requireNonNull(player.getEquipment()).getArmorContents()) {
            Map<CEnchantment, Integer> enchantments = enchantmentBookSettings.getEnchantments(armor);
            if (enchantments.isEmpty()) continue;

            int heal = 1;
            // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
            double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
            if (maxHealth > player.getHealth() && player.getHealth() > 0) {
                syncProcessor.add(() -> {
                    if (EnchantUtils.isEventActive(CEnchantments.NURSERY, player, armor, enchantments)) {
                        if (player.getHealth() + heal <= maxHealth) player.setHealth(player.getHealth() + heal);
                        if (player.getHealth() + heal >= maxHealth) player.setHealth(maxHealth);
                    }
                });
            }

            if (player.getFoodLevel() < 20) {
                syncProcessor.add(() -> {

                    if (EnchantUtils.isEventActive(CEnchantments.IMPLANTS, player, armor, enchantments)) {

                        int foodIncrease = 1;

                        if (SupportedPlugins.SPARTAN.isPluginLoaded()) spartanSupport.cancelFastEat(player);

                        if (player.getFoodLevel() + foodIncrease <= 20)
                            player.setFoodLevel(player.getFoodLevel() + foodIncrease);

                        if (player.getFoodLevel() + foodIncrease >= 20) player.setFoodLevel(20);
                    }
                });
            }

            if (SupportedPlugins.FACTIONS_UUID.isPluginLoaded()) {
                final int radius = 4 + crazyManager.getLevel(armor, CEnchantments.ANGEL);

                syncProcessor.add(() -> {
                    if (EnchantUtils.isEventActive(CEnchantments.ANGEL, player, armor, enchantments)) {
                        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                            if (!(entity instanceof Player other)) continue;
                            if (!pluginSupport.isFriendly(player, other)) continue;

                            other.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 0));
                        }
                    }
                });
            }
            useHellForge(player, armor, enchantments);
        }

        for (final ItemStack item : player.getInventory().getContents()) {
            useHellForge(player, item, enchantmentBookSettings.getEnchantments(item));
        }
    }

    private void useHellForge(Player player, ItemStack item, Map<CEnchantment, Integer> enchantments) {
        if (!EnchantUtils.isEventActive(CEnchantments.HELLFORGED, player, item, enchantments)) return;
        int armorDurability = methods.getDurability(item);
        if (armorDurability <= 0) return;

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            int finalArmorDurability = armorDurability;
            finalArmorDurability -= crazyManager.getLevel(item, CEnchantments.HELLFORGED);
            methods.setDurability(item, finalArmorDurability);
        });
    }
}