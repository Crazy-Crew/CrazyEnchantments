package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.api.objects.CEnchantment;
import me.badbones69.crazyenchantments.api.objects.ItemBuilder;
import me.badbones69.crazyenchantments.multisupport.SpartanSupport;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import org.bukkit.Bukkit;
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

public class Axes implements Listener {
    
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled() || ce.isIgnoredEvent(e)) return;
        if (Support.isFriendly(e.getDamager(), e.getEntity())) return;
        if (e.getEntity() instanceof LivingEntity) {
            LivingEntity en = (LivingEntity) e.getEntity();
            if (e.getDamager() instanceof Player) {
                Player damager = (Player) e.getDamager();
                ItemStack item = Methods.getItemInHand(damager);
                if (!e.getEntity().isDead()) {
                    List<CEnchantment> enchantments = ce.getEnchantmentsOnItem(item);
                    if (CEnchantments.BERSERK.isActivated()) {
                        if (enchantments.contains(CEnchantments.BERSERK.getEnchantment())) {
                            if (CEnchantments.BERSERK.chanceSuccessful(item)) {
                                EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.BERSERK.getEnchantment(), item);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (ce.getLevel(item, CEnchantments.BERSERK) + 5) * 20, 1));
                                    damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (ce.getLevel(item, CEnchantments.BERSERK) + 5) * 20, 0));
                                }
                            }
                        }
                    }
                    if (CEnchantments.BLESSED.isActivated()) {
                        if (enchantments.contains(CEnchantments.BLESSED.getEnchantment())) {
                            if (CEnchantments.BLESSED.chanceSuccessful(item)) {
                                EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.BLESSED.getEnchantment(), item);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    removeBadPotions(damager);
                                }
                            }
                        }
                    }
                    if (CEnchantments.FEEDME.isActivated()) {
                        if (enchantments.contains(CEnchantments.FEEDME.getEnchantment())) {
                            int food = 2 * ce.getLevel(item, CEnchantments.FEEDME);
                            if (CEnchantments.FEEDME.chanceSuccessful(item)) {
                                if (damager.getFoodLevel() < 20) {
                                    EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.FEEDME.getEnchantment(), item);
                                    Bukkit.getPluginManager().callEvent(event);
                                    if (!event.isCancelled()) {
                                        if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                                            SpartanSupport.cancelFastEat(damager);
                                        }
                                        if (damager.getFoodLevel() + food < 20) {
                                            damager.setFoodLevel((int) (damager.getSaturation() + food));
                                        }
                                        if (damager.getFoodLevel() + food > 20) {
                                            damager.setFoodLevel(20);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (CEnchantments.REKT.isActivated()) {
                        if (enchantments.contains(CEnchantments.REKT.getEnchantment())) {
                            double damage = e.getDamage() * 2;
                            if (CEnchantments.REKT.chanceSuccessful(item)) {
                                EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.REKT.getEnchantment(), item);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    e.setDamage(damage);
                                }
                            }
                        }
                    }
                    if (CEnchantments.CURSED.isActivated()) {
                        if (enchantments.contains(CEnchantments.CURSED.getEnchantment())) {
                            if (CEnchantments.CURSED.chanceSuccessful(item)) {
                                EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.CURSED.getEnchantment(), item);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    en.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, (ce.getLevel(item, CEnchantments.CURSED) + 9) * 20, 1));
                                }
                            }
                        }
                    }
                    if (CEnchantments.DIZZY.isActivated()) {
                        if (enchantments.contains(CEnchantments.DIZZY.getEnchantment())) {
                            if (CEnchantments.DIZZY.chanceSuccessful(item)) {
                                EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.DIZZY.getEnchantment(), item);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    en.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, (ce.getLevel(item, CEnchantments.DIZZY) + 9) * 20, 0));
                                }
                            }
                        }
                    }
                    if (CEnchantments.BATTLECRY.isActivated()) {
                        if (enchantments.contains(CEnchantments.BATTLECRY.getEnchantment())) {
                            if (CEnchantments.BATTLECRY.chanceSuccessful(item)) {
                                EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.BATTLECRY.getEnchantment(), item);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    for (Entity entity : damager.getNearbyEntities(3, 3, 3)) {
                                        if (!Support.isFriendly(damager, entity)) {
                                            entity.setVelocity(entity.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize().setY(.5));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!Support.allowsPVP(e.getEntity().getLocation())) return;
        if (e.getEntity().getKiller() instanceof Player) {
            Player damager = e.getEntity().getKiller();
            Player player = e.getEntity();
            ItemStack item = Methods.getItemInHand(damager);
            if (CEnchantments.DECAPITATION.isActivated()) {
                if (ce.hasEnchantment(item, CEnchantments.DECAPITATION)) {
                    int power = ce.getLevel(item, CEnchantments.DECAPITATION);
                    if (CEnchantments.DECAPITATION.chanceSuccessful(item)) {
                        EnchantmentUseEvent event = new EnchantmentUseEvent(damager, CEnchantments.DECAPITATION.getEnchantment(), item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            e.getDrops().add(new ItemBuilder().setMaterial("PLAYER_HEAD", "397:3").setPlayer(player.getName()).build());
                        }
                    }
                }
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
        for (PotionEffectType p : bad) {
            if (player.hasPotionEffect(p)) {
                player.removePotionEffect(p);
            }
        }
    }
    
}