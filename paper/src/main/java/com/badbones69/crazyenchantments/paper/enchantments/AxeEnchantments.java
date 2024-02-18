package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.support.PluginSupport;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.objects.other.ItemBuilder;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.api.utils.EnchantUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EntityUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EventUtils;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Map;

public class AxeEnchantments implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = CrazyEnchantments.get();

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    @NotNull
    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    // Plugin Support.
    @NotNull
    private final PluginSupport pluginSupport = this.starter.getPluginSupport();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (EventUtils.isIgnoredEvent(event)) return;
        if (this.pluginSupport.isFriendly(event.getDamager(), event.getEntity())) return;

        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!(event.getDamager() instanceof Player damager)) return;

        ItemStack item = this.methods.getItemInHand(damager);

        if (entity.isDead()) return;

        Map<CEnchantment, Integer> enchantments = this.enchantmentBookSettings.getEnchantments(item);

        if (EnchantUtils.isEventActive(CEnchantments.BERSERK, damager, item, enchantments)) {
                damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (crazyManager.getLevel(item, CEnchantments.BERSERK) + 5) * 20, 1));
                damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (crazyManager.getLevel(item, CEnchantments.BERSERK) + 5) * 20, 0));
        }

        if (EnchantUtils.isEventActive(CEnchantments.BLESSED, damager, item, enchantments)) removeBadPotions(damager);

        if (EnchantUtils.isEventActive(CEnchantments.FEEDME, damager, item, enchantments)&& damager.getFoodLevel() < 20) {
            int food = 2 * this.crazyManager.getLevel(item, CEnchantments.FEEDME);

            if (damager.getFoodLevel() + food < 20) damager.setFoodLevel((int) (damager.getSaturation() + food));

            if (damager.getFoodLevel() + food > 20) damager.setFoodLevel(20);
        }

        if (EnchantUtils.isEventActive(CEnchantments.REKT, damager, item, enchantments)) event.setDamage(event.getDamage() * 2);

        if (EnchantUtils.isEventActive(CEnchantments.CURSED, damager, item, enchantments))
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (this.crazyManager.getLevel(item, CEnchantments.CURSED) + 9) * 20, 1));

        if (EnchantUtils.isEventActive(CEnchantments.DIZZY, damager, item, enchantments))
            entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (this.crazyManager.getLevel(item, CEnchantments.DIZZY) + 9) * 20, 0));

        if (EnchantUtils.isEventActive(CEnchantments.BATTLECRY, damager, item, enchantments)) {
            damager.getNearbyEntities(3, 3, 3).stream().filter(nearbyEntity -> !this.pluginSupport.isFriendly(damager, nearbyEntity)).forEach(nearbyEntity ->
                    nearbyEntity.setVelocity(nearbyEntity.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize().setY(.5)));
        }

        if (EnchantUtils.isEventActive(CEnchantments.DEMONFORGED, damager, item, enchantments) && entity instanceof Player player) {

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

        if (player.getKiller() == null) return;

        if (!this.pluginSupport.allowCombat(player.getLocation())) return;

        Player damager = player.getKiller();
        ItemStack item = this.methods.getItemInHand(damager);

        if (EnchantUtils.isEventActive(CEnchantments.DECAPITATION, damager, item, this.enchantmentBookSettings.getEnchantments(item))) {
            event.getDrops().add(new ItemBuilder().setMaterial(Material.PLAYER_HEAD).setPlayerName(player.getName()).build());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();

        if (killer == null) return;

        ItemStack item = this.methods.getItemInHand(killer);
        Map<CEnchantment, Integer> enchantments = this.enchantmentBookSettings.getEnchantments(item);
        Material headMat = EntityUtils.getHeadMaterial(event.getEntity());

        if (headMat != null && !EventUtils.containsDrop(event, headMat)) {
            double multiplier = this.crazyManager.getDecapitationHeadMap().getOrDefault(headMat, 0.0);

            if (multiplier != 0.0 && EnchantUtils.isEventActive(CEnchantments.DECAPITATION, killer, item, enchantments, multiplier)) {
                ItemStack head = new ItemBuilder().setMaterial(headMat).build();
                event.getDrops().add(head);
            }
        }
    }

    private void removeBadPotions(Player player) {

        ArrayList<PotionEffectType> bad = new ArrayList<>() {{
            add(PotionEffectType.BLINDNESS);
            add(PotionEffectType.CONFUSION);
            add(PotionEffectType.HUNGER);
            add(PotionEffectType.POISON);
            add(PotionEffectType.SLOW);
            add(PotionEffectType.SLOW_DIGGING);
            add(PotionEffectType.WEAKNESS);
            add(PotionEffectType.WITHER);
        }};

        bad.forEach(player::removePotionEffect);
    }
}