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
import com.badbones69.crazyenchantments.paper.utilities.misc.EnchantUtils;
import com.badbones69.crazyenchantments.paper.utilities.misc.EventUtils;
import org.bukkit.Material;
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
import java.util.Map;

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

        Map<CEnchantment, Integer> enchantments = enchantmentBookSettings.getEnchantments(item);

        if (EnchantUtils.isEventActive(CEnchantments.BERSERK, damager, item, enchantments)) {
                damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (crazyManager.getLevel(item, CEnchantments.BERSERK) + 5) * 20, 1));
                damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (crazyManager.getLevel(item, CEnchantments.BERSERK) + 5) * 20, 0));
        }

        if (EnchantUtils.isEventActive(CEnchantments.BLESSED, damager, item, enchantments)) removeBadPotions(damager);

        if (EnchantUtils.isEventActive(CEnchantments.FEEDME, damager, item, enchantments)&& damager.getFoodLevel() < 20) {

            int food = 2 * crazyManager.getLevel(item, CEnchantments.FEEDME);

            if (SupportedPlugins.SPARTAN.isPluginLoaded()) spartanSupport.cancelFastEat(damager);

            if (damager.getFoodLevel() + food < 20) damager.setFoodLevel((int) (damager.getSaturation() + food));

            if (damager.getFoodLevel() + food > 20) damager.setFoodLevel(20);
        }

        if (EnchantUtils.isEventActive(CEnchantments.REKT, damager, item, enchantments)) event.setDamage(event.getDamage() * 2);

        if (EnchantUtils.isEventActive(CEnchantments.CURSED, damager, item, enchantments))
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (crazyManager.getLevel(item, CEnchantments.CURSED) + 9) * 20, 1));

        if (EnchantUtils.isEventActive(CEnchantments.DIZZY, damager, item, enchantments))
            entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (crazyManager.getLevel(item, CEnchantments.DIZZY) + 9) * 20, 0));

        if (EnchantUtils.isEventActive(CEnchantments.BATTLECRY, damager, item, enchantments)) {
            damager.getNearbyEntities(3, 3, 3).stream().filter(nearbyEntity -> !pluginSupport.isFriendly(damager, nearbyEntity)).forEach(nearbyEntity ->
                    nearbyEntity.setVelocity(nearbyEntity.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize().setY(.5)));
        }
        if (EnchantUtils.isEventActive(CEnchantments.DEMONFORGED, damager, item, enchantments) && entity instanceof Player player) {

            ItemStack armorItem = switch (methods.percentPick(4, 0)) {
                case 1 -> player.getEquipment().getHelmet();
                case 2 -> player.getEquipment().getChestplate();
                case 3 -> player.getEquipment().getLeggings();
                default -> player.getEquipment().getBoots();
            };

            methods.removeDurability(armorItem, player);

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