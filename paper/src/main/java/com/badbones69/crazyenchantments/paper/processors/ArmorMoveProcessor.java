package com.badbones69.crazyenchantments.paper.processors;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.PluginSupport;
import com.badbones69.crazyenchantments.paper.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.AngelUseEvent;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.paper.api.events.HellForgedUseEvent;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class ArmorMoveProcessor extends Processor<PlayerMoveEvent> {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = this.plugin.getStarter();

    private final Methods methods = this.starter.getMethods();

    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    // Plugin Support.
    private final PluginSupport pluginSupport = this.starter.getPluginSupport();

    private final SpartanSupport spartanSupport = this.starter.getSpartanSupport();

    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private final Processor<Runnable> syncProcessor;

    public ArmorMoveProcessor() {
        this.syncProcessor = new RunnableSyncProcessor();
    }

    public void stop() {
        this.syncProcessor.stop();
        super.stop();
    }

    public void start() {
        this.syncProcessor.start();
        super.start();
    }

    public void process(PlayerMoveEvent process) {
        Player player = process.getPlayer();

        for (final ItemStack armor : Objects.requireNonNull(player.getEquipment()).getArmorContents()) {
            if (!this.enchantmentBookSettings.hasEnchantments(armor)) continue;

            if (CEnchantments.NURSERY.isActivated() && crazyManager.hasEnchantment(armor, CEnchantments.NURSERY)) {
                int heal = 1;

                if (CEnchantments.NURSERY.chanceSuccessful(armor)) {
                    // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
                    double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
                    if (maxHealth > player.getHealth() && player.getHealth() > 0) {
                        this.syncProcessor.add(() -> {
                            EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.NURSERY.getEnchantment(), armor);
                            this.plugin.getServer().getPluginManager().callEvent(event);

                            if (!event.isCancelled() && player.getHealth() > 0) {
                                if (player.getHealth() + heal <= maxHealth) player.setHealth(player.getHealth() + heal);

                                if (player.getHealth() + heal >= maxHealth) player.setHealth(maxHealth);
                            }
                        });
                    }
                }
            }

            if (CEnchantments.IMPLANTS.isActivated() && crazyManager.hasEnchantment(armor, CEnchantments.IMPLANTS) && CEnchantments.IMPLANTS.chanceSuccessful(armor) && player.getFoodLevel() < 20) {
                this.syncProcessor.add(() -> {
                    EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.IMPLANTS.getEnchantment(), armor);
                    this.plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        int foodIncrease = 1;

                        if (SupportedPlugins.SPARTAN.isPluginLoaded()) spartanSupport.cancelFastEat(player);

                        if (player.getFoodLevel() + foodIncrease <= 20) player.setFoodLevel(player.getFoodLevel() + foodIncrease);

                        if (player.getFoodLevel() + foodIncrease >= 20) player.setFoodLevel(20);
                    }
                });
            }

            if ((CEnchantments.ANGEL.isActivated() && crazyManager.hasEnchantment(armor, CEnchantments.ANGEL) && SupportedPlugins.FACTIONS_UUID.isPluginLoaded())) {
                final int radius = 4 + crazyManager.getLevel(armor, CEnchantments.ANGEL);

                this.syncProcessor.add(() -> {
                    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                        if (!(entity instanceof Player other)) continue;
                        if (!pluginSupport.isFriendly(player, other)) continue;

                        AngelUseEvent event = new AngelUseEvent(player, armor);
                        this.plugin.getServer().getPluginManager().callEvent(event);

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
            int armorDurability = this.methods.getDurability(item);

            if (armorDurability > 0 && CEnchantments.HELLFORGED.chanceSuccessful(item)) {
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                    int finalArmorDurability = armorDurability;

                    HellForgedUseEvent event = new HellForgedUseEvent(player, item);
                    this.plugin.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        finalArmorDurability -= this.crazyManager.getLevel(item, CEnchantments.HELLFORGED);
                        this.methods.setDurability(item, finalArmorDurability);
                    }
                });
            }
        }
    }
}