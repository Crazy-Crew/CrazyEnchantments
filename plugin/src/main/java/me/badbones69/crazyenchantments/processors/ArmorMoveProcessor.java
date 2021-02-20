package me.badbones69.crazyenchantments.processors;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.AngelUseEvent;
import me.badbones69.crazyenchantments.api.events.EnchantmentUseEvent;
import me.badbones69.crazyenchantments.api.events.HellForgedUseEvent;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Version;
import me.badbones69.premiumhooks.anticheat.SpartanSupport;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class ArmorMoveProcessor extends Processor<PlayerMoveEvent> {

    private final Processor<Runnable> syncProcessor;
    private final CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private final Support support = Support.getInstance();

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
                    double maxHealth = ce.useHealthAttributes() ? Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue() : player.getMaxHealth();
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
                        int foodIncress = 1;
                        if (Support.SupportedPlugins.SPARTAN.isPluginLoaded()) {
                            SpartanSupport.cancelFastEat(player);
                        }
                        if (player.getFoodLevel() + foodIncress <= 20) {
                            player.setFoodLevel(player.getFoodLevel() + foodIncress);
                        }
                        if (player.getFoodLevel() + foodIncress >= 20) {
                            player.setFoodLevel(20);
                        }
                    }
                });
            }

            if ((CEnchantments.ANGEL.isActivated() && ce.hasEnchantment(armor, CEnchantments.ANGEL) && Support.SupportedPlugins.FACTIONS_MASSIVE_CRAFT.isPluginLoaded()) || Support.SupportedPlugins.FACTIONS_UUID.isPluginLoaded()) {
                final int radius = 4 + ce.getLevel(armor, CEnchantments.ANGEL);
                syncProcessor.add(() -> {
                    for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                        if (entity instanceof Player) {
                            Player other = (Player) entity;
                            if (support.isFriendly(player, other)) {
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
            int armorDurability = Version.isNewer(Version.v1_12_R1) ? ((Damageable) item.getItemMeta()).getDamage() : item.getDurability();
            if (armorDurability > 0 && CEnchantments.HELLFORGED.chanceSuccessful(item)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int finalArmorDirability = armorDurability;
                        HellForgedUseEvent event = new HellForgedUseEvent(player, item);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            finalArmorDirability -= ce.getLevel(item, CEnchantments.HELLFORGED);
                            if (Version.isNewer(Version.v1_12_R1)) {
                                Damageable damageable = (Damageable) item.getItemMeta();
                                if (damageable != null) {
                                    damageable.setDamage(Math.max(finalArmorDirability, 0));
                                    item.setItemMeta((ItemMeta) damageable);
                                }
                            } else {
                                item.setDurability((short) Math.max(finalArmorDirability, 0));
                            }
                        }
                    }
                }.runTask(ce.getPlugin());
            }
        }
    }
}