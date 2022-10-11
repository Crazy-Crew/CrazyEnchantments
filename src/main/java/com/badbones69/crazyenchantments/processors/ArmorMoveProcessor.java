package com.badbones69.crazyenchantments.processors;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.events.AngelUseEvent;
import com.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.api.events.HellForgedUseEvent;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class ArmorMoveProcessor extends Processor<PlayerMoveEvent> {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private final PluginSupport pluginSupport = plugin.getStarter().getPluginSupport();

    private final Methods methods = plugin.getStarter().getMethods();

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
            if (!crazyManager.hasEnchantments(armor)) continue;

            if (CEnchantments.NURSERY.isActivated() && crazyManager.hasEnchantment(armor, CEnchantments.NURSERY)) {
                int heal = 1;

                if (CEnchantments.NURSERY.chanceSuccessful(armor)) {
                    // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
                    double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
                    if (maxHealth > player.getHealth() && player.getHealth() > 0) {
                        syncProcessor.add(() -> {
                            EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.NURSERY.getEnchantment(), armor);
                            plugin.getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled() && player.getHealth() > 0) {
                                if (player.getHealth() + heal <= maxHealth) player.setHealth(player.getHealth() + heal);

                                if (player.getHealth() + heal >= maxHealth) player.setHealth(maxHealth);
                            }
                        });
                    }
                }
            }

            if (CEnchantments.IMPLANTS.isActivated() && crazyManager.hasEnchantment(armor, CEnchantments.IMPLANTS) && CEnchantments.IMPLANTS.chanceSuccessful(armor) && player.getFoodLevel() < 20) {
                syncProcessor.add(() -> {
                    EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.IMPLANTS.getEnchantment(), armor);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        int foodIncrease = 1;

                        //if (PluginSupport.SupportedPlugins.SPARTAN.isPluginLoaded()) {
                            // SpartanSupport.cancelFastEat(player);
                        //}

                        if (player.getFoodLevel() + foodIncrease <= 20) player.setFoodLevel(player.getFoodLevel() + foodIncrease);

                        if (player.getFoodLevel() + foodIncrease >= 20) player.setFoodLevel(20);
                    }
                });
            }

            if ((CEnchantments.ANGEL.isActivated() && crazyManager.hasEnchantment(armor, CEnchantments.ANGEL) && PluginSupport.SupportedPlugins.FACTIONS_UUID.isPluginLoaded())) {
                final int radius = 4 + crazyManager.getLevel(armor, CEnchantments.ANGEL);

                syncProcessor.add(() -> {
                    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                        if (!(entity instanceof Player other)) continue;
                        if (!pluginSupport.isFriendly(player, other)) continue;
                        AngelUseEvent event = new AngelUseEvent(player, armor);

                        plugin.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled()) other.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 0));
                    }
                });
            }
            useHellForge(player, armor);
        }

        for (final ItemStack item : player.getInventory().getContents()) {
            useHellForge(player, item);
        }
    }

    private void useHellForge(Player player, ItemStack item) {
        if (crazyManager.hasEnchantment(item, CEnchantments.HELLFORGED)) {
            int armorDurability = methods.getDurability(item);

            if (armorDurability > 0 && CEnchantments.HELLFORGED.chanceSuccessful(item)) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    int finalArmorDurability = armorDurability;
                    HellForgedUseEvent event = new HellForgedUseEvent(player, item);
                    plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        finalArmorDurability -= crazyManager.getLevel(item, CEnchantments.HELLFORGED);
                        methods.setDurability(item, finalArmorDurability);
                    }
                });
            }
        }
    }
}