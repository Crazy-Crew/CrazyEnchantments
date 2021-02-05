package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.*;
import me.badbones69.crazyenchantments.api.objects.ArmorEnchantment;
import me.badbones69.crazyenchantments.api.objects.PotionEffects;
import me.badbones69.crazyenchantments.controllers.ProtectionCrystal;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.Version;
import me.badbones69.crazyenchantments.multisupport.anticheats.AACSupport;
import me.badbones69.crazyenchantments.multisupport.anticheats.NoCheatPlusSupport;
import me.badbones69.crazyenchantments.multisupport.particles.ParticleEffect;
import me.badbones69.premiumhooks.anticheat.SpartanSupport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;

public class Armor implements Listener {
    
    private List<Player> fall = new ArrayList<>();
    private HashMap<Player, HashMap<CEnchantments, Calendar>> timer = new HashMap<>();
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private Support support = Support.getInstance();
    
    @EventHandler
    public void onEquip(ArmorEquipEvent e) {
        Player player = e.getPlayer();
        ItemStack newItem = e.getNewArmorPiece();
        ItemStack oldItem = e.getOldArmorPiece();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (ce.hasEnchantments(oldItem)) {// Removing the potion effects.
                    for (CEnchantments enchantment : ce.getEnchantmentPotions().keySet()) {
                        if (enchantment.isActivated() && ce.hasEnchantment(oldItem, enchantment.getEnchantment())) {
                            Map<PotionEffectType, Integer> effects = ce.getUpdatedEffects(player, new ItemStack(Material.AIR), oldItem, enchantment);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    for (Entry<PotionEffectType, Integer> type : effects.entrySet()) {
                                        if (type.getValue() < 0) {
                                            player.removePotionEffect(type.getKey());
                                        } else {
                                            player.removePotionEffect(type.getKey());
                                            player.addPotionEffect(new PotionEffect(type.getKey(), Integer.MAX_VALUE, type.getValue()));
                                        }
                                    }
                                }
                            }.runTask(ce.getPlugin());
                        }
                    }
                }
                if (ce.hasEnchantments(newItem)) {// Adding the potion effects.
                    for (CEnchantments enchantment : ce.getEnchantmentPotions().keySet()) {
                        if (enchantment.isActivated() && ce.hasEnchantment(newItem, enchantment.getEnchantment())) {
                            Map<PotionEffectType, Integer> effects = ce.getUpdatedEffects(player, newItem, oldItem, enchantment);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    EnchantmentUseEvent event = new EnchantmentUseEvent(player, enchantment.getEnchantment(), newItem);
                                    Bukkit.getPluginManager().callEvent(event);
                                    if (!event.isCancelled()) {
                                        for (Entry<PotionEffectType, Integer> type : effects.entrySet()) {
                                            if (type.getValue() < 0) {
                                                player.removePotionEffect(type.getKey());
                                            } else {
                                                player.removePotionEffect(type.getKey());
                                                player.addPotionEffect(new PotionEffect(type.getKey(), Integer.MAX_VALUE, type.getValue()));
                                            }
                                        }
                                    }
                                }
                            }.runTask(ce.getPlugin());
                        }
                    }
                }
            }
        }.runTaskAsynchronously(ce.getPlugin());
    }
    
    //todo Make INSOMNIA work correctly. It should double the damage a player with the armor enchantment on does.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled() || ce.isIgnoredEvent(e) || ce.isIgnoredUUID(e.getDamager().getUniqueId())) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (support.isFriendly(e.getDamager(), e.getEntity())) return;
                if (e.getDamager() instanceof LivingEntity && e.getEntity() instanceof Player) {
                    final Player player = (Player) e.getEntity();
                    final LivingEntity damager = (LivingEntity) e.getDamager();
                    for (ItemStack armor : player.getEquipment().getArmorContents()) {
                        if (ce.hasEnchantments(armor)) {
                            for (ArmorEnchantment armorEnchantment : ce.getArmorManager().getArmorEnchantments()) {
                                CEnchantments enchantment = armorEnchantment.getEnchantment();
                                if (ce.hasEnchantment(armor, enchantment) && enchantment.chanceSuccessful(armor)) {
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            EnchantmentUseEvent event = new EnchantmentUseEvent(player, enchantment, armor);
                                            Bukkit.getPluginManager().callEvent(event);
                                            if (!event.isCancelled()) {
                                                if (armorEnchantment.isPotionEnchantment()) {
                                                    for (PotionEffects effect : armorEnchantment.getPotionEffects()) {
                                                        //Debug for the enchantment info.
//                                                        System.out.println(
//                                                        "===========================" + "\n"
//                                                        + "Enchantment: " + enchantment.getName() + "\n"
//                                                        + "Level: " + ce.getLevel(armor, enchantment) + "\n"
//                                                        + "Effect: " + effect.getPotionEffect().getName() + "\n"
//                                                        + "Duration: " + (effect.getDuration() / 20) + "s \n"
//                                                        + "Amplifier: " + effect.getAmplifier() + "\n"
//                                                        + "Amp + Lvl: " + ((armorEnchantment.isLevelAddedToAmplifier() ? ce.getLevel(armor, enchantment) : 0) + effect.getAmplifier()) + "\n" +
//                                                        "===========================");
                                                        damager.addPotionEffect(new PotionEffect(effect.getPotionEffect(), effect.getDuration(), (armorEnchantment.isLevelAddedToAmplifier() ? ce.getLevel(armor, enchantment) : 0) + effect.getAmplifier()));
                                                    }
                                                } else {
                                                    e.setDamage(e.getDamage() * ((armorEnchantment.isLevelAddedToAmplifier() ? ce.getLevel(armor, enchantment) : 0) + armorEnchantment.getDamageAmplifier()));
                                                }
                                            }
                                        }
                                    }.runTask(ce.getPlugin());
                                }
                            }
                            if (player.getHealth() <= 8 && ce.hasEnchantment(armor, CEnchantments.ROCKET.getEnchantment()) && CEnchantments.ROCKET.chanceSuccessful(armor)) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.ROCKET.getEnchantment(), armor);
                                        Bukkit.getPluginManager().callEvent(event);
                                        if (SupportedPlugins.AAC.isPluginLoaded()) {
                                            AACSupport.exemptPlayerTime(player);
                                        }
                                        if (!event.isCancelled()) {
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    player.setVelocity(player.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize().setY(1));
                                                }
                                            }.runTaskLater(ce.getPlugin(), 1);
                                            fall.add(player);
                                            if (Version.isNewer(Version.v1_8_R3)) {
                                                player.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 1);
                                            } else {
                                                ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 1, 1, player.getLocation(), 100);
                                            }
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    fall.remove(player);
                                                }
                                            }.runTaskLater(ce.getPlugin(), 8 * 20);
                                        }
                                    }
                                }.runTask(ce.getPlugin());
                            }
                            if (ce.hasEnchantment(armor, CEnchantments.ENLIGHTENED) && CEnchantments.ENLIGHTENED.chanceSuccessful(armor)) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.ENLIGHTENED.getEnchantment(), armor);
                                        Bukkit.getPluginManager().callEvent(event);
                                        if (!event.isCancelled()) {
                                            double heal = ce.getLevel(armor, CEnchantments.ENLIGHTENED);
                                            //Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
                                            double maxHealth = ce.useHealthAttributes() ? player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() : player.getMaxHealth();
                                            if (player.getHealth() + heal < maxHealth) {
                                                player.setHealth(player.getHealth() + heal);
                                            }
                                            if (player.getHealth() + heal >= maxHealth) {
                                                player.setHealth(maxHealth);
                                            }
                                        }
                                    }
                                }.runTask(ce.getPlugin());
                            }
                            if (ce.hasEnchantment(armor, CEnchantments.MOLTEN) && CEnchantments.MOLTEN.chanceSuccessful(armor)) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.MOLTEN.getEnchantment(), armor);
                                        Bukkit.getPluginManager().callEvent(event);
                                        if (!event.isCancelled()) {
                                            damager.setFireTicks((ce.getLevel(armor, CEnchantments.MOLTEN) * 2) * 20);
                                        }
                                    }
                                }.runTask(ce.getPlugin());
                            }
                            if (ce.hasEnchantment(armor, CEnchantments.SAVIOR) && CEnchantments.SAVIOR.chanceSuccessful(armor)) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.SAVIOR.getEnchantment(), armor);
                                        Bukkit.getPluginManager().callEvent(event);
                                        if (!event.isCancelled()) {
                                            e.setDamage(e.getDamage() / 2);
                                        }
                                    }
                                }.runTask(ce.getPlugin());
                            }
                            if (ce.hasEnchantment(armor, CEnchantments.CACTUS) && CEnchantments.CACTUS.chanceSuccessful(armor)) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.CACTUS.getEnchantment(), armor);
                                        Bukkit.getPluginManager().callEvent(event);
                                        if (!event.isCancelled()) {
                                            damager.damage(ce.getLevel(armor, CEnchantments.CACTUS));
                                        }
                                    }
                                }.runTask(ce.getPlugin());
                            }
                            if (ce.hasEnchantment(armor, CEnchantments.STORMCALLER) && CEnchantments.STORMCALLER.chanceSuccessful(armor)) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.STORMCALLER.getEnchantment(), armor);
                                        Bukkit.getPluginManager().callEvent(event);
                                        if (!event.isCancelled()) {
                                            Location loc = damager.getLocation();
                                            loc.getWorld().spigot().strikeLightningEffect(loc, true);
                                            int lightningSoundRange = Files.CONFIG.getFile().getInt("Settings.EnchantmentOptions.Lightning-Sound-Range", 160);
                                            try {
                                                loc.getWorld().playSound(loc, ce.getSound("ENTITY_LIGHTNING_BOLT_IMPACT", "ENTITY_LIGHTNING_IMPACT"), (float) lightningSoundRange / 16f, 1);
                                            } catch (Exception ignore) {
                                            }
                                            if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) {
                                                NoCheatPlusSupport.exemptPlayer(player);
                                            }
                                            if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                                                SpartanSupport.cancelNoSwing(player);
                                            }
                                            if (SupportedPlugins.AAC.isPluginLoaded()) {
                                                AACSupport.exemptPlayer(player);
                                            }
                                            for (LivingEntity en : Methods.getNearbyLivingEntities(loc, 2D, player)) {
                                                EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(player, en, DamageCause.CUSTOM, 5D);
                                                ce.addIgnoredEvent(damageByEntityEvent);
                                                ce.addIgnoredUUID(player.getUniqueId());
                                                Bukkit.getPluginManager().callEvent(damageByEntityEvent);
                                                if (!damageByEntityEvent.isCancelled() && support.allowsPVP(en.getLocation()) && !support.isFriendly(player, en)) {
                                                    en.damage(5D);
                                                }
                                                ce.removeIgnoredEvent(damageByEntityEvent);
                                                ce.removeIgnoredUUID(player.getUniqueId());
                                            }
                                            damager.damage(5D);
                                            if (SupportedPlugins.NO_CHEAT_PLUS.isPluginLoaded()) {
                                                NoCheatPlusSupport.unexemptPlayer(player);
                                            }
                                            if (SupportedPlugins.AAC.isPluginLoaded()) {
                                                AACSupport.unexemptPlayer(player);
                                            }
                                        }
                                    }
                                }.runTask(ce.getPlugin());
                            }
                        }
                    }
                    if (damager instanceof Player) {
                        for (ItemStack armor : Objects.requireNonNull(damager.getEquipment()).getArmorContents()) {
                            if (ce.hasEnchantment(armor, CEnchantments.LEADERSHIP) && CEnchantments.LEADERSHIP.chanceSuccessful(armor) && (SupportedPlugins.FACTIONS_MASSIVE_CRAFT.isPluginLoaded() || SupportedPlugins.FACTIONS_UUID.isPluginLoaded())) {
                                int radius = 4 + ce.getLevel(armor, CEnchantments.LEADERSHIP);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        int players = 0;
                                        for (Entity entity : damager.getNearbyEntities(radius, radius, radius)) {
                                            if (entity instanceof Player) {
                                                Player other = (Player) entity;
                                                if (support.isFriendly(damager, other)) {
                                                    players++;
                                                }
                                            }
                                        }
                                        if (players > 0) {
                                            EnchantmentUseEvent event = new EnchantmentUseEvent((Player) damager, CEnchantments.LEADERSHIP.getEnchantment(), armor);
                                            Bukkit.getPluginManager().callEvent(event);
                                            if (!event.isCancelled()) {
                                                e.setDamage(e.getDamage() + (players / 2));
                                            }
                                        }
                                    }
                                }.runTask(ce.getPlugin());
                            }
                        }
                    }
                }
            }
        }.runTaskAsynchronously(ce.getPlugin());
    }
    
    @EventHandler
    public void onAura(AuraActiveEvent e) {
        Player player = e.getPlayer();
        Player other = e.getOther();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.canSee(other) || !other.canSee(player)) return;
                if (support.isVanished(player) || support.isVanished(other)) return;
                CEnchantments enchant = e.getEnchantment();
                int level = e.getLevel();
                //Debug code for checking.
//                System.out.println("PvP: " + support.allowsPVP(other.getLocation()));
//                System.out.println("Enemy: " + !support.isFriendly(player, other));
//                System.out.println("NoBypass: " + !Methods.hasPermission(other, "bypass.aura", false));
                if (support.allowsPVP(other.getLocation()) && !support.isFriendly(player, other) && !Methods.hasPermission(other, "bypass.aura", false)) {
                    Calendar cal = Calendar.getInstance();
                    HashMap<CEnchantments, Calendar> effect = new HashMap<>();
                    if (timer.containsKey(other)) {
                        effect = timer.get(other);
                    }
                    HashMap<CEnchantments, Calendar> finalEffect = effect;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            switch (enchant) {
                                case BLIZZARD:
                                    if (CEnchantments.BLIZZARD.isActivated()) {
                                        other.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, level - 1));
                                    }
                                    break;
                                case INTIMIDATE:
                                    if (CEnchantments.INTIMIDATE.isActivated()) {
                                        other.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3 * 20, level - 1));
                                    }
                                    break;
                                case ACIDRAIN:
                                    if (CEnchantments.ACIDRAIN.isActivated() && (!timer.containsKey(other) ||
                                    (timer.containsKey(other) && !timer.get(other).containsKey(enchant)) ||
                                    (timer.containsKey(other) && timer.get(other).containsKey(enchant) &&
                                    cal.after(timer.get(other).get(enchant))
                                    && CEnchantments.ACIDRAIN.chanceSuccessful()))) {
                                        other.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 4 * 20, 1));
                                        int time = 35 - (level * 5);
                                        cal.add(Calendar.SECOND, time > 0 ? time : 5);
                                        finalEffect.put(enchant, cal);
                                    }
                                    break;
                                case SANDSTORM:
                                    if (CEnchantments.SANDSTORM.isActivated() && (!timer.containsKey(other) ||
                                    (timer.containsKey(other) && !timer.get(other).containsKey(enchant)) ||
                                    (timer.containsKey(other) && timer.get(other).containsKey(enchant) &&
                                    cal.after(timer.get(other).get(enchant))
                                    && CEnchantments.SANDSTORM.chanceSuccessful()))) {
                                        other.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 0));
                                        int time = 35 - (level * 5);
                                        cal.add(Calendar.SECOND, time > 0 ? time : 5);
                                        finalEffect.put(enchant, cal);
                                    }
                                    break;
                                case RADIANT:
                                    if (CEnchantments.RADIANT.isActivated() && (!timer.containsKey(other) ||
                                    (timer.containsKey(other) && !timer.get(other).containsKey(enchant)) ||
                                    (timer.containsKey(other) && timer.get(other).containsKey(enchant) &&
                                    cal.after(timer.get(other).get(enchant))
                                    && CEnchantments.RADIANT.chanceSuccessful()))) {
                                        other.setFireTicks(5 * 20);
                                        int time = 20 - (level * 5);
                                        cal.add(Calendar.SECOND, Math.max(time, 0));
                                        finalEffect.put(enchant, cal);
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }.runTask(ce.getPlugin());
                    timer.put(other, effect);
                }
            }
        }.runTaskAsynchronously(ce.getPlugin());
    }
    
    @SuppressWarnings({"deprecation", "squid:CallToDeprecatedMethod"})
    @EventHandler
    public void onMovement(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();
        if (Objects.requireNonNull(to).getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                    for (ItemStack armor : Objects.requireNonNull(player.getEquipment()).getArmorContents()) {
                        if (ce.hasEnchantments(armor)) {
                            if (CEnchantments.NURSERY.isActivated() && ce.hasEnchantment(armor, CEnchantments.NURSERY)) {
                                int heal = 1;
                                if (CEnchantments.NURSERY.chanceSuccessful(armor)) {
                                    //Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
                                    double maxHealth = ce.useHealthAttributes() ? Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue() : player.getMaxHealth();
                                    if (maxHealth > player.getHealth()) {
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.NURSERY.getEnchantment(), armor);
                                                Bukkit.getPluginManager().callEvent(event);
                                                if (!event.isCancelled()) {
                                                    if (player.getHealth() + heal <= maxHealth) {
                                                        player.setHealth(player.getHealth() + heal);
                                                    }
                                                    if (player.getHealth() + heal >= maxHealth) {
                                                        player.setHealth(maxHealth);
                                                    }
                                                }
                                            }
                                        }.runTask(ce.getPlugin());
                                    }
                                }
                            }
                            if (CEnchantments.IMPLANTS.isActivated() && ce.hasEnchantment(armor, CEnchantments.IMPLANTS) &&
                            CEnchantments.IMPLANTS.chanceSuccessful(armor) && player.getFoodLevel() < 20) {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.IMPLANTS.getEnchantment(), armor);
                                        Bukkit.getPluginManager().callEvent(event);
                                        if (!event.isCancelled()) {
                                            int foodIncress = 1;
                                            if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                                                SpartanSupport.cancelFastEat(player);
                                            }
                                            if (player.getFoodLevel() + foodIncress <= 20) {
                                                player.setFoodLevel(player.getFoodLevel() + foodIncress);
                                            }
                                            if (player.getFoodLevel() + foodIncress >= 20) {
                                                player.setFoodLevel(20);
                                            }
                                        }
                                    }
                                }.runTask(ce.getPlugin());
                            }
                            if (CEnchantments.ANGEL.isActivated() && ce.hasEnchantment(armor, CEnchantments.ANGEL)
                            && SupportedPlugins.FACTIONS_MASSIVE_CRAFT.isPluginLoaded() || SupportedPlugins.FACTIONS_UUID.isPluginLoaded()) {
                                int radius = 4 + ce.getLevel(armor, CEnchantments.ANGEL);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
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
                                    }
                                }.runTask(ce.getPlugin());
                            }
                            //This is ran to check the player's armor slots.
                            useHellForge(player, armor);
                        }
                    }
                    //This is ran to check the player's inventory items.
                    for (ItemStack item : player.getInventory().getContents()) {
                        useHellForge(player, item);
                    }
            }
        }.runTaskAsynchronously(ce.getPlugin());
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!(player.getKiller() instanceof Player)) return;
                Player killer = player.getKiller();
                if (!support.allowsPVP(player.getLocation())) return;
                if (CEnchantments.SELFDESTRUCT.isActivated()) {
                    for (ItemStack item : Objects.requireNonNull(player.getEquipment()).getArmorContents()) {
                        if (ce.hasEnchantments(item) && ce.hasEnchantment(item, CEnchantments.SELFDESTRUCT.getEnchantment())) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.SELFDESTRUCT.getEnchantment(), item);
                                    Bukkit.getPluginManager().callEvent(event);
                                    if (!event.isCancelled()) {
                                        Methods.explode(player);
                                        List<ItemStack> items = new ArrayList<>();
                                        for (ItemStack drop : e.getDrops()) {
                                            if (drop != null && ProtectionCrystal.isProtected(drop) && ProtectionCrystal.isProtectionSuccessful(player)) {
                                                items.add(drop);
                                            }
                                        }
                                        e.getDrops().clear();
                                        e.getDrops().addAll(items);
                                    }
                                }
                            }.runTask(ce.getPlugin());
                        }
                    }
                }
                if (CEnchantments.RECOVER.isActivated()) {
                    for (ItemStack item : Objects.requireNonNull(killer.getEquipment()).getArmorContents()) {
                        if (ce.hasEnchantments(item) && ce.hasEnchantment(item, CEnchantments.RECOVER)) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    EnchantmentUseEvent event = new EnchantmentUseEvent(player, CEnchantments.RECOVER.getEnchantment(), item);
                                    Bukkit.getPluginManager().callEvent(event);
                                    if (!event.isCancelled()) {
                                        killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 8 * 20, 2));
                                        killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 1));
                                    }
                                }
                            }.runTask(ce.getPlugin());
                        }
                    }
                }
            }
        }.runTaskAsynchronously(ce.getPlugin());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerFallDamage(EntityDamageEvent e) {
        if (fall.contains(e.getEntity()) && e.getCause() == DamageCause.FALL) {
            e.setCancelled(true);
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