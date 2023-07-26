package com.badbones69.crazyenchantments.enchantments;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.Methods;
import com.badbones69.crazyenchantments.Starter;
import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.api.support.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.utilities.misc.EventUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.ArrayList;
import java.util.List;

public class AxeEnchantments implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    // Plugin Support.
    private final PluginSupport pluginSupport = starter.getPluginSupport();

    private final SpartanSupport spartanSupport = starter.getSpartanSupport();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (EventUtils.isIgnoredEvent(event)) return;
        if (pluginSupport.isFriendly(event.getDamager(), event.getEntity())) return;

        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!(event.getDamager() instanceof Player damager)) return;

        ItemStack item = methods.getItemInHand(damager);

        if (entity.isDead()) return;

        List<CEnchantment> enchantments = enchantmentBookSettings.getEnchantmentsOnItem(item);

        if (CEnchantments.BERSERK.isActivated() && enchantments.contains(CEnchantments.BERSERK.getEnchantment()) && CEnchantments.BERSERK.chanceSuccessful(item)) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.BERSERK.getEnchantment(), item);
            plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) {
                damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (crazyManager.getLevel(item, CEnchantments.BERSERK) + 5) * 20, 1));
                damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (crazyManager.getLevel(item, CEnchantments.BERSERK) + 5) * 20, 0));
            }
        }

        if (CEnchantments.BLESSED.isActivated() && enchantments.contains(CEnchantments.BLESSED.getEnchantment()) && CEnchantments.BLESSED.chanceSuccessful(item)) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.BLESSED.getEnchantment(), item);
            plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) removeBadPotions(damager);
        }

        if (CEnchantments.FEEDME.isActivated() && enchantments.contains(CEnchantments.FEEDME.getEnchantment()) && CEnchantments.FEEDME.chanceSuccessful(item) && damager.getFoodLevel() < 20) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.FEEDME.getEnchantment(), item);
            plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) {
                int food = 2 * crazyManager.getLevel(item, CEnchantments.FEEDME);

                if (SupportedPlugins.SPARTAN.isPluginLoaded()) spartanSupport.cancelFastEat(damager);

                if (damager.getFoodLevel() + food < 20) damager.setFoodLevel((int) (damager.getSaturation() + food));

                if (damager.getFoodLevel() + food > 20) damager.setFoodLevel(20);
            }
        }

        if (CEnchantments.REKT.isActivated() && enchantments.contains(CEnchantments.REKT.getEnchantment())) {
            double damage = event.getDamage() * 2;

            if (CEnchantments.REKT.chanceSuccessful(item)) {
                EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.REKT.getEnchantment(), item);
                plugin.getServer().getPluginManager().callEvent(useEvent);

                if (!useEvent.isCancelled()) event.setDamage(damage);
            }
        }

        if (CEnchantments.CURSED.isActivated() && enchantments.contains(CEnchantments.CURSED.getEnchantment()) && CEnchantments.CURSED.chanceSuccessful(item)) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.CURSED.getEnchantment(), item);
            plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (crazyManager.getLevel(item, CEnchantments.CURSED) + 9) * 20, 1));
        }

        if (CEnchantments.DIZZY.isActivated() && enchantments.contains(CEnchantments.DIZZY.getEnchantment()) && CEnchantments.DIZZY.chanceSuccessful(item)) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.DIZZY.getEnchantment(), item);
            plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (crazyManager.getLevel(item, CEnchantments.DIZZY) + 9) * 20, 0));
        }

        if (CEnchantments.BATTLECRY.isActivated() && enchantments.contains(CEnchantments.BATTLECRY.getEnchantment()) && CEnchantments.BATTLECRY.chanceSuccessful(item)) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.BATTLECRY.getEnchantment(), item);
            plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) {
                for (Entity nearbyEntity : damager.getNearbyEntities(3, 3, 3)) {
                    if (!pluginSupport.isFriendly(damager, nearbyEntity)) nearbyEntity.setVelocity(nearbyEntity.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize().setY(.5));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (pluginSupport.allowCombat(player.getLocation()) && event.getEntity().getKiller() != null) {
            Player damager = event.getEntity().getKiller();
            ItemStack item = methods.getItemInHand(damager);

            if (crazyManager.hasEnchantment(item, CEnchantments.DECAPITATION) && CEnchantments.DECAPITATION.chanceSuccessful(item)) {
                EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.DECAPITATION.getEnchantment(), item);
                plugin.getServer().getPluginManager().callEvent(useEvent);

                if (!useEvent.isCancelled()) event.getDrops().add(new ItemBuilder().setMaterial(Material.PLAYER_HEAD).setPlayerName(player.getName()).build());
            }
        }
    }

    private void removeBadPotions(Player player) {
        ArrayList<PotionEffectType> bad = new ArrayList<>();

        bad.add(PotionEffectType.BLINDNESS);
        bad.add(PotionEffectType.CONFUSION);
        bad.add(PotionEffectType.HUNGER);
        bad.add(PotionEffectType.POISON);
        bad.add(PotionEffectType.SLOW);
        bad.add(PotionEffectType.SLOW_DIGGING);
        bad.add(PotionEffectType.WEAKNESS);
        bad.add(PotionEffectType.WITHER);

        for (PotionEffectType potionEffectType : bad) {
            if (player.hasPotionEffect(potionEffectType)) player.removePotionEffect(potionEffectType);
        }
    }
}