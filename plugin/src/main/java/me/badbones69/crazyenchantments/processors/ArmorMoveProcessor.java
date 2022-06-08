package me.badbones69.crazyenchantments.processors;

import me.badbones69.crazyenchantments.api.CrazyManager;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.AngelUseEvent;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.api.events.HellForgedUseEvent;
import me.badbones69.crazyenchantments.api.PluginSupport;
import me.badbones69.crazyenchantments.api.PluginSupport.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.anticheats.SpartanSupport;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Objects;

public class ArmorMoveProcessor extends Processor<PlayerMoveEvent> {

    private final Processor<Runnable> syncProcessor;
    private final CrazyManager ce = CrazyManager.getInstance();
    private final PluginSupport pluginSupport = PluginSupport.getInstance();

    public ArmorMoveProcessor() {
        this.syncProcessor = new RunnableSyncProcessor(ce.getPlugin());
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
            if (!ce.hasEnchantments(armor)) continue;
            if (CEnchantments.NURSERY.isActivated() && ce.hasEnchantment(armor, CEnchantments.NURSERY)) {
                int heal = 1;
                if (CEnchantments.NURSERY.chanceSuccessful(armor)) {
                    //Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
                    double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
                    if (maxHealth > player.getHealth() && player.getHealth() > 0) {
                        syncProcessor.add(() -> {
                            EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.NURSERY.getEnchantment(), armor);
                            Bukkit.getPluginManager().callEvent(event);
                            if (!event.isCancelled() && player.getHealth() > 0) {
                                if (player.getHealth() + heal <= maxHealth) {
                                    player.setHealth(player.getHealth() + heal);
                                }
                                if (player.getHealth() + heal >= maxHealth) {
                                    player.setHealth(maxHealth);
                                }
                            }
                        });
                    }
                }
            }

            if (CEnchantments.IMPLANTS.isActivated() && ce.hasEnchantment(armor, CEnchantments.IMPLANTS) && CEnchantments.IMPLANTS.chanceSuccessful(armor) && player.getFoodLevel() < 20) {
                syncProcessor.add(() -> {
                    EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.IMPLANTS.getEnchantment(), armor);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        int foodIncrease = 1;

                        if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                            SpartanSupport.cancelFastEat(player);
                        }

                        if (player.getFoodLevel() + foodIncrease <= 20) {
                            player.setFoodLevel(player.getFoodLevel() + foodIncrease);
                        }
                        if (player.getFoodLevel() + foodIncrease >= 20) {
                            player.setFoodLevel(20);
                        }

                    }
                });
            }

            if ((CEnchantments.ANGEL.isActivated() && ce.hasEnchantment(armor, CEnchantments.ANGEL) && SupportedPlugins.FACTIONS_UUID.isPluginLoaded())) {
                final int radius = 4 + ce.getLevel(armor, CEnchantments.ANGEL);
                syncProcessor.add(() -> {
                    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                        if (entity instanceof Player) {
                            Player other = (Player) entity;
                            if (pluginSupport.isFriendly(player, other)) {
                                AngelUseEvent event = new AngelUseEvent(player, armor);
                                Bukkit.getPluginManager().callEvent(event);
                                if (!event.isCancelled()) {
                                    other.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 0));
                                }
                            }
                        }
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
        if (ce.hasEnchantment(item, CEnchantments.HELLFORGED)) {
            int armorDurability = ((Damageable) item.getItemMeta()).getDamage();
            if (armorDurability > 0 && CEnchantments.HELLFORGED.chanceSuccessful(item)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int finalArmorDurability = armorDurability;
                        HellForgedUseEvent event = new HellForgedUseEvent(player, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            finalArmorDurability -= ce.getLevel(item, CEnchantments.HELLFORGED);
                            Damageable damageable = (Damageable) item.getItemMeta();
                            if (damageable != null) {
                                damageable.setDamage(Math.max(finalArmorDurability, 0));
                                item.setItemMeta(damageable);
                            }
                        }
                    }
                }.runTask(ce.getPlugin());
            }
        }
    }
}