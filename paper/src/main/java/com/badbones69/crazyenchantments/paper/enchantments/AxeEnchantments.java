package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.PluginSupport;
import com.badbones69.crazyenchantments.paper.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.utilities.misc.EventUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AxeEnchantments implements Listener {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = this.plugin.getStarter();

    private final Methods methods = this.starter.getMethods();

    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    // Plugin Support.
    private final PluginSupport pluginSupport = this.starter.getPluginSupport();

    private final SpartanSupport spartanSupport = this.starter.getSpartanSupport();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (EventUtils.isIgnoredEvent(event)) return;
        if (this.pluginSupport.isFriendly(event.getDamager(), event.getEntity())) return;

        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!(event.getDamager() instanceof Player damager)) return;

        ItemStack item = this.methods.getItemInHand(damager);

        if (entity.isDead()) return;

        List<CEnchantment> enchantments = enchantmentBookSettings.getEnchantmentsOnItem(item);

        if (CEnchantments.BERSERK.isActivated() && enchantments.contains(CEnchantments.BERSERK.getEnchantment()) && CEnchantments.BERSERK.chanceSuccessful(item)) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.BERSERK.getEnchantment(), item);
            this.plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) {
                damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (crazyManager.getLevel(item, CEnchantments.BERSERK) + 5) * 20, 1));
                damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (crazyManager.getLevel(item, CEnchantments.BERSERK) + 5) * 20, 0));
            }
        }

        if (CEnchantments.BLESSED.isActivated() && enchantments.contains(CEnchantments.BLESSED.getEnchantment()) && CEnchantments.BLESSED.chanceSuccessful(item)) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.BLESSED.getEnchantment(), item);
            this.plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) removeBadPotions(damager);
        }

        if (CEnchantments.FEEDME.isActivated() && enchantments.contains(CEnchantments.FEEDME.getEnchantment()) && CEnchantments.FEEDME.chanceSuccessful(item) && damager.getFoodLevel() < 20) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.FEEDME.getEnchantment(), item);
            this.plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) {
                int food = 2 * this.crazyManager.getLevel(item, CEnchantments.FEEDME);

                if (SupportedPlugins.SPARTAN.isPluginLoaded()) this.spartanSupport.cancelFastEat(damager);

                if (damager.getFoodLevel() + food < 20) damager.setFoodLevel((int) (damager.getSaturation() + food));

                if (damager.getFoodLevel() + food > 20) damager.setFoodLevel(20);
            }
        }

        if (CEnchantments.REKT.isActivated() && enchantments.contains(CEnchantments.REKT.getEnchantment())) {
            double damage = event.getDamage() * 2;

            if (CEnchantments.REKT.chanceSuccessful(item)) {
                EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.REKT.getEnchantment(), item);
                this.plugin.getServer().getPluginManager().callEvent(useEvent);

                if (!useEvent.isCancelled()) event.setDamage(damage);
            }
        }

        if (CEnchantments.CURSED.isActivated() && enchantments.contains(CEnchantments.CURSED.getEnchantment()) && CEnchantments.CURSED.chanceSuccessful(item)) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.CURSED.getEnchantment(), item);
            this.plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (this.crazyManager.getLevel(item, CEnchantments.CURSED) + 9) * 20, 1));
        }

        if (CEnchantments.DIZZY.isActivated() && enchantments.contains(CEnchantments.DIZZY.getEnchantment()) && CEnchantments.DIZZY.chanceSuccessful(item)) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.DIZZY.getEnchantment(), item);
            this.plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (this.crazyManager.getLevel(item, CEnchantments.DIZZY) + 9) * 20, 0));
        }

        if (CEnchantments.BATTLECRY.isActivated() && enchantments.contains(CEnchantments.BATTLECRY.getEnchantment()) && CEnchantments.BATTLECRY.chanceSuccessful(item)) {
            EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.BATTLECRY.getEnchantment(), item);
            this.plugin.getServer().getPluginManager().callEvent(useEvent);

            if (!useEvent.isCancelled()) {
                for (Entity nearbyEntity : damager.getNearbyEntities(3, 3, 3)) {
                    if (!pluginSupport.isFriendly(damager, nearbyEntity)) nearbyEntity.setVelocity(nearbyEntity.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize().setY(.5));
                }
            }
        }
        if (CEnchantments.DEMONFORGED.isActivated() && enchantments.contains(CEnchantments.DEMONFORGED.getEnchantment()) && CEnchantments.DEMONFORGED.chanceSuccessful(item) && entity instanceof Player player) {

            ItemStack armorItem = switch (this.methods.percentPick(4, 0)) {
                case 1 -> player.getEquipment().getHelmet();
                case 2 -> player.getEquipment().getChestplate();
                case 3 -> player.getEquipment().getLeggings();
                default -> player.getEquipment().getBoots();
            };

            this.methods.removeDurability(armorItem, player);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (this.pluginSupport.allowCombat(player.getLocation()) && event.getEntity().getKiller() != null) {
            Player damager = event.getEntity().getKiller();
            ItemStack item = this.methods.getItemInHand(damager);

            if (this.crazyManager.hasEnchantment(item, CEnchantments.DECAPITATION) && CEnchantments.DECAPITATION.chanceSuccessful(item)) {
                EnchantmentUseEvent useEvent = new EnchantmentUseEvent(damager, CEnchantments.DECAPITATION.getEnchantment(), item);
                this.plugin.getServer().getPluginManager().callEvent(useEvent);

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