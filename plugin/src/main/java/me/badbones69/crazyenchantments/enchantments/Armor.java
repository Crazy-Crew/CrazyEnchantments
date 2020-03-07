package me.badbones69.crazyenchantments.enchantments;

import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.FileManager.Files;
import me.badbones69.crazyenchantments.api.enums.CEnchantments;
import me.badbones69.crazyenchantments.api.events.*;
import me.badbones69.crazyenchantments.api.objects.ArmorEnchantment;
import me.badbones69.crazyenchantments.api.objects.PotionEffects;
import me.badbones69.crazyenchantments.controllers.ProtectionCrystal;
import me.badbones69.crazyenchantments.multisupport.*;
import me.badbones69.crazyenchantments.multisupport.Support.SupportedPlugins;
import me.badbones69.crazyenchantments.multisupport.particles.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;

public class Armor implements Listener {
    
    private static HashMap<Player, List<LivingEntity>> mobs = new HashMap<>();
    private List<Player> fall = new ArrayList<>();
    private HashMap<Player, HashMap<CEnchantments, Calendar>> timer = new HashMap<>();
    private HashMap<Player, Calendar> mobTimer = new HashMap<>();
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    
    public static Map<Player, List<LivingEntity>> getAllies() {
        return mobs;
    }
    
    public static void removeAllies() {
        for (Entry<Player, List<LivingEntity>> player : mobs.entrySet()) {
            player.getValue().forEach(Entity :: remove);
        }
    }
    
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
                if (Support.isFriendly(e.getDamager(), e.getEntity())) return;
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
                                            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
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
                                                if (!damageByEntityEvent.isCancelled() && Support.allowsPVP(en.getLocation()) && !Support.isFriendly(player, en)) {
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
                        for (ItemStack armor : damager.getEquipment().getArmorContents()) {
                            if (ce.hasEnchantment(armor, CEnchantments.LEADERSHIP) && CEnchantments.LEADERSHIP.chanceSuccessful(armor) && (SupportedPlugins.FACTIONS_MASSIVE_CRAFT.isPluginLoaded() || SupportedPlugins.FACTIONS_UUID.isPluginLoaded())) {
                                int radius = 4 + ce.getLevel(armor, CEnchantments.LEADERSHIP);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        int players = 0;
                                        for (Entity entity : damager.getNearbyEntities(radius, radius, radius)) {
                                            if (entity instanceof Player) {
                                                Player other = (Player) entity;
                                                if (Support.isFriendly(damager, other)) {
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
                if (Support.isVanished(player) || Support.isVanished(other)) return;
                CEnchantments enchant = e.getEnchantment();
                int level = e.getLevel();
                if (Support.allowsPVP(other.getLocation()) && !Support.isFriendly(player, other) && !Methods.hasPermission(other, "bypass.aura", false)) {
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
        new BukkitRunnable() {
            @Override
            public void run() {
                if (to.getBlockX() != from.getBlockX() || to.getBlockY() != from.getBlockY() || to.getBlockZ() != from.getBlockZ()) {
                    for (ItemStack armor : player.getEquipment().getArmorContents()) {
                        if (ce.hasEnchantments(armor)) {
                            if (CEnchantments.NURSERY.isActivated() && ce.hasEnchantment(armor, CEnchantments.NURSERY)) {
                                int heal = 1;
                                if (CEnchantments.NURSERY.chanceSuccessful(armor)) {
                                    double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
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
                                                if (Support.isFriendly(player, other)) {
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
                if (!Support.allowsPVP(player.getLocation())) return;
                if (CEnchantments.SELFDESTRUCT.isActivated()) {
                    for (ItemStack item : player.getEquipment().getArmorContents()) {
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
                    for (ItemStack item : killer.getEquipment().getArmorContents()) {
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
    
    @EventHandler
    public void onAllyTarget(EntityTargetEvent e) {
        if (e.getEntity() instanceof LivingEntity) {
            for (Entry<Player, List<LivingEntity>> mobList : mobs.entrySet()) {
                if (mobList.getValue().contains(e.getEntity()) && e.getTarget() != null &&
                mobList.getKey().getName().equals(e.getTarget().getName())) {
                    e.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAllySpawn(EntityDamageByEntityEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!e.isCancelled() && !ce.isIgnoredEvent(e)) {
                    Calendar rightNow = Calendar.getInstance();
                    if (e.getEntity() instanceof Player && e.getDamager() instanceof LivingEntity) {// Player gets attacked
                        Player player = (Player) e.getEntity();
                        LivingEntity en = (LivingEntity) e.getDamager();
                        if (!mobs.containsKey(player)) {
                            for (ItemStack item : player.getEquipment().getArmorContents()) {
                                // Spawn allies when getting attacked
                                if (ce.hasEnchantments(item) && !mobs.containsKey(player) && (!mobTimer.containsKey(player) || (mobTimer.containsKey(player) && rightNow.after(mobTimer.get(player))))) {
                                    if (CEnchantments.TAMER.isActivated() && ce.hasEnchantment(item, CEnchantments.TAMER)) {
                                        int power = ce.getLevel(item, CEnchantments.TAMER);
                                        spawnAllies(player, en, EntityType.WOLF, power);
                                    }
                                    if (CEnchantments.GUARDS.isActivated() && ce.hasEnchantment(item, CEnchantments.GUARDS)) {
                                        int power = ce.getLevel(item, CEnchantments.GUARDS);
                                        spawnAllies(player, en, EntityType.IRON_GOLEM, power);
                                    }
                                    if (en instanceof Player) {
                                        if (CEnchantments.NECROMANCER.isActivated() && ce.hasEnchantment(item, CEnchantments.NECROMANCER)) {
                                            int power = ce.getLevel(item, CEnchantments.NECROMANCER);
                                            spawnAllies(player, en, EntityType.ZOMBIE, power * 2);
                                        }
                                        if (CEnchantments.INFESTATION.isActivated() && ce.hasEnchantment(item, CEnchantments.INFESTATION)) {
                                            int power = ce.getLevel(item, CEnchantments.INFESTATION);
                                            spawnAllies(player, en, EntityType.ENDERMITE, power * 3);
                                            spawnAllies(player, en, EntityType.SILVERFISH, power * 3);
                                        }
                                    }
                                }
                            }
                        } else {
                            attackEnemy(player, en);
                        }
                    }
                    if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof Player) {// Player attacks
                        Player player = (Player) e.getDamager();
                        LivingEntity en = (LivingEntity) e.getEntity();
                        if (mobs.containsKey(player) && mobs.get(player).contains(en)) {// If player hurts ally
                            e.setCancelled(true);
                            return;
                        }
                        if (!mobs.containsKey(player)) {
                            for (ItemStack item : player.getEquipment().getArmorContents()) {
                                // Spawn allies when attacking
                                if (ce.hasEnchantments(item) && !mobs.containsKey(player) && (!mobTimer.containsKey(player) || (mobTimer.containsKey(player) && rightNow.after(mobTimer.get(player))))) {
                                    if (CEnchantments.TAMER.isActivated() && ce.hasEnchantment(item, CEnchantments.TAMER)) {
                                        int power = ce.getLevel(item, CEnchantments.TAMER);
                                        spawnAllies(player, en, EntityType.WOLF, power);
                                    }
                                    if (CEnchantments.GUARDS.isActivated() && ce.hasEnchantment(item, CEnchantments.GUARDS)) {
                                        int power = ce.getLevel(item, CEnchantments.GUARDS);
                                        spawnAllies(player, en, EntityType.IRON_GOLEM, power);
                                    }
                                    if (en instanceof Player) {
                                        if (CEnchantments.NECROMANCER.isActivated() && ce.hasEnchantment(item, CEnchantments.NECROMANCER)) {
                                            int power = ce.getLevel(item, CEnchantments.NECROMANCER);
                                            spawnAllies(player, en, EntityType.ZOMBIE, power * 2);
                                        }
                                        if (CEnchantments.INFESTATION.isActivated() && ce.hasEnchantment(item, CEnchantments.INFESTATION)) {
                                            int power = ce.getLevel(item, CEnchantments.INFESTATION);
                                            spawnAllies(player, en, EntityType.ENDERMITE, power * 3);
                                            spawnAllies(player, en, EntityType.SILVERFISH, power * 3);
                                        }
                                    }
                                }
                            }
                        } else {
                            attackEnemy(player, en);
                        }
                    }
                }
            }
        }.runTaskAsynchronously(ce.getPlugin());
    }
    
    @EventHandler
    public void onAllyDeath(EntityDeathEvent e) {
        for (Entry<Player, List<LivingEntity>> mobList : mobs.entrySet()) {
            if (mobList.getValue().contains(e.getEntity())) {
                e.setDroppedExp(0);
                e.getDrops().clear();
            }
        }
    }
    
    @EventHandler
    public void onAllyDespawn(ChunkUnloadEvent e) {
        if (e.getChunk().getEntities().length > 0) {
            for (Entity entity : e.getChunk().getEntities()) {
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    for (Entry<Player, List<LivingEntity>> mobList : mobs.entrySet()) {
                        if (mobList.getValue().contains(livingEntity)) {
                            mobList.getValue().remove(livingEntity);
                            livingEntity.remove();
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (mobs.containsKey(player)) {
            for (LivingEntity en : mobs.get(player)) {
                en.remove();
            }
            mobs.remove(player);
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
                                damageable.setDamage(Math.max(finalArmorDirability, 0));
                                item.setItemMeta((ItemMeta) damageable);
                            } else {
                                item.setDurability((short) Math.max(finalArmorDirability, 0));
                            }
                        }
                    }
                }.runTask(ce.getPlugin());
            }
        }
    }
    
    private void spawnAllies(final Player player, LivingEntity enemy, EntityType mob, Integer amount) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 2);
        mobTimer.put(player, cal);
        for (int i = 0; i < amount; i++) {
            LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), mob);
            switch (mob) {
                case WOLF:
                    entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(16);
                    entity.setHealth(16);
                    Support.noStack(entity);
                    break;
                case IRON_GOLEM:
                    entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
                    entity.setHealth(200);
                    Support.noStack(entity);
                    entity.setCanPickupItems(false);
                    break;
                case ZOMBIE:
                    entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(45);
                    entity.setHealth(45);
                    Support.noStack(entity);
                    entity.setCanPickupItems(false);
                    break;
                case ENDERMITE:
                case SILVERFISH:
                    entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
                    entity.setHealth(10);
                    Support.noStack(entity);
                    break;
                default:
                    break;
            }
            entity.setCustomName(Methods.color("&6" + player.getName() + "'s " + entity.getName()));
            entity.setCustomNameVisible(true);
            if (!mobs.containsKey(player)) {
                mobs.put(player, new ArrayList<>(Arrays.asList(entity)));
            } else {
                mobs.get(player).add(entity);
            }
        }
        attackEnemy(player, enemy);
        Bukkit.getScheduler().runTaskLater(ce.getPlugin(), () -> {
            if (mobs.containsKey(player)) {
                for (LivingEntity entity : mobs.get(player)) {
                    entity.remove();
                }
                mobs.remove(player);
            }
        }, 60 * 20);
    }
    
    private void attackEnemy(Player player, LivingEntity enemy) {
        if (mobs.containsKey(player)) {
            for (LivingEntity ally : mobs.get(player)) {
                switch (ally.getType()) {
                    case IRON_GOLEM:
                        IronGolem iron = (IronGolem) ally;
                        iron.setTarget(enemy);
                        break;
                    case WOLF:
                        Wolf wolf = (Wolf) ally;
                        wolf.setTarget(enemy);
                        break;
                    case ZOMBIE:
                        Zombie zom = (Zombie) ally;
                        zom.setTarget(enemy);
                        break;
                    case ENDERMITE:
                        Endermite mite = (Endermite) ally;
                        mite.setTarget(enemy);
                        break;
                    case SILVERFISH:
                        Silverfish sfish = (Silverfish) ally;
                        sfish.setTarget(enemy);
                        break;
                    default:
                        break;
                }
            }
        }
    }
    
}