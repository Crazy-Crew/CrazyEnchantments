package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.objects.User;
import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.managers.PlayerManager;
import com.badbones69.crazyenchantments.paper.managers.currency.enums.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.RageBreakEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.utils.EnchantUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EntityUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EventUtils;
import com.badbones69.crazyenchantments.paper.managers.configs.ConfigManager;
import com.badbones69.crazyenchantments.paper.controllers.BossBarController;
import com.badbones69.crazyenchantments.paper.managers.currency.CurrencyManager;
import com.badbones69.crazyenchantments.paper.support.PluginSupport;
import com.badbones69.crazyenchantments.registry.UserRegistry;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyenchantments.constants.MessageKeys;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SwordEnchantments implements Listener {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Server server = this.plugin.getServer();

    private final PluginManager pluginManager = this.server.getPluginManager();

    private final CrazyInstance instance = this.plugin.getInstance();

    private final UserRegistry userRegistry = this.instance.getUserRegistry();

    private final PlayerManager playerManager = this.instance.getPlayerManager();

    private final ConfigManager options = this.plugin.getConfigManager();

    @NotNull
    private final CrazyManager crazyManager = null;

    // Plugin Support.
    @NotNull
    private final PluginSupport pluginSupport = null;

    @NotNull
    private final BossBarController bossBarController = this.plugin.getBossBarController();

    private final CurrencyManager currencyManager = this.instance.getCurrencyManager();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        final Entity damager = event.getDamager();
        final Entity entity = event.getEntity();

        if (EventUtils.isIgnoredEvent(event) || EventUtils.isIgnoredUUID(damager.getUniqueId())) return;

        if (this.pluginSupport.isFriendly(damager, entity)) return;

        if (this.options.isBreakRageOnDamage() && entity instanceof Player player) {
            final ItemStack itemStack = Methods.getItemInHand(player);

            if (!itemStack.isEmpty()) {
                this.playerManager.getPlayer(player).ifPresent(cePlayer -> {
                    final RageBreakEvent rageBreakEvent = new RageBreakEvent(player, damager, itemStack);

                    this.pluginManager.callEvent(rageBreakEvent);

                    if (!rageBreakEvent.isCancelled() && cePlayer.hasRage()) {
                        cePlayer.getRageTask().cancel();
                        cePlayer.setRageMultiplier(0.0);
                        cePlayer.setRageLevel(0);
                        cePlayer.setRage(false);

                        rageInformPlayer(player, MessageKeys.rage_damaged, 0f);
                    }
                });
            }
        }

        if (!(entity instanceof LivingEntity livingEntity)) return;
        if (!(damager instanceof final Player damagerEntity)) return;

        final ItemStack item = Methods.getItemInHand(damagerEntity);

        if (item.isEmpty()) return;

        if (entity.isDead()) return;

        final Map<CEnchantment, Integer> enchantments = this.instance.getEnchantments(item);

        if (enchantments.isEmpty()) return;

        this.playerManager.getPlayer(damagerEntity).ifPresent(cePlayer -> {
            // Check if CEPlayer is null as plugins like citizen use Player objects.
            if (EnchantUtils.isEventActive(CEnchantments.RAGE, damager, item, enchantments)) {
                if (cePlayer.hasRage()) {
                    cePlayer.getRageTask().cancel();

                    if (cePlayer.getRageMultiplier() <= this.options.getRageMaxLevel())
                        cePlayer.setRageMultiplier(cePlayer.getRageMultiplier() + (enchantments.get(CEnchantments.RAGE.getEnchantment()) * this.options.getRageIncrement()));

                    final int rageUp = cePlayer.getRageLevel() + 1;

                    if (cePlayer.getRageMultiplier().intValue() >= rageUp) {
                        rageInformPlayer(damagerEntity, Map.of("{level}", String.valueOf(rageUp)), ((float) rageUp / (float) (this.options.getRageMaxLevel() + 1)));

                        cePlayer.setRageLevel(rageUp);
                    }

                    event.setDamage(event.getDamage() * cePlayer.getRageMultiplier());
                } else {
                    cePlayer.setRageMultiplier(1.0);
                    cePlayer.setRage(true);
                    cePlayer.setRageLevel(1);

                    rageInformPlayer(damagerEntity, MessageKeys.rage_building, ((float) cePlayer.getRageLevel() / (float) this.options.getRageMaxLevel()));
                }

                cePlayer.setRageTask(new FoliaScheduler(this.plugin, null, damager) {
                    @Override
                    public void run() {
                        cePlayer.setRageMultiplier(0.0);
                        cePlayer.setRage(false);
                        cePlayer.setRageLevel(0);

                        rageInformPlayer(damagerEntity, MessageKeys.rage_cooled_down, 0f);
                    }
                }.runDelayed(80));
            }
        });

        if (entity instanceof Player player && EnchantUtils.isEventActive(CEnchantments.DISARMER, damagerEntity, item, enchantments)) {
            final EquipmentSlot equipmentSlot = getSlot(Methods.percentPick(4, 0));

            final EntityEquipment equipment = player.getEquipment();

            final ItemStack armor = equipment.getItem(equipmentSlot);

            if (armor.isEmpty()) {
                equipment.setItem(equipmentSlot, null);

                Methods.addItemToInventory(player, armor);
            }
        }

        if (entity instanceof Player player && EnchantUtils.isEventActive(CEnchantments.DISORDER, damagerEntity, item, enchantments)) {
            final Inventory inventory = player.getInventory();

            final List<ItemStack> items = new ArrayList<>();
            final List<Integer> slots = new ArrayList<>();

            for (int i = 0; i < 9; i++) {
                final ItemStack inventoryItem = inventory.getItem(i);

                if (inventoryItem != null) {
                    items.add(inventoryItem);

                    inventory.setItem(i, null);
                }

                slots.add(i);
            }

            Collections.shuffle(items);
            Collections.shuffle(slots);

            for (int i = 0; i < items.size(); i++) {
                inventory.setItem(slots.get(i), items.get(i));
            }

            this.userRegistry.getUser(damagerEntity).sendMessage(MessageKeys.disordered_enemy_hot_bar);
        }

        if (livingEntity instanceof Player player && EnchantUtils.isEventActive(CEnchantments.SKILLSWIPE, damagerEntity, item, enchantments)) {
            final int amount = 4 + enchantments.get(CEnchantments.SKILLSWIPE.getEnchantment());

            if (player.getTotalExperience() > 0) { //todo() allow changing this to give money by adding per enchant currency support.
                if (this.currencyManager.hasAmount(Currency.XP_TOTAL, player, amount)) {
                    this.currencyManager.takeAmount(Currency.XP_TOTAL, player, amount);
                } else {
                    player.setTotalExperience(0);
                }

                this.currencyManager.addAmount(Currency.XP_TOTAL, damagerEntity, amount);
            }
        }

        if (damagerEntity.getHealth() > 0 && EnchantUtils.isEventActive(CEnchantments.LIFESTEAL, damagerEntity, item, enchantments)) {
            final int steal = enchantments.get(CEnchantments.LIFESTEAL.getEnchantment());
            // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
            final double maxHealth = damagerEntity.getAttribute(Attribute.MAX_HEALTH).getValue(); //todo() npe

            if (damagerEntity.getHealth() + steal < maxHealth) damagerEntity.setHealth(damagerEntity.getHealth() + steal);

            if (damagerEntity.getHealth() + steal >= maxHealth) damagerEntity.setHealth(maxHealth);
        }

        if (EnchantUtils.isEventActive(CEnchantments.NUTRITION, damagerEntity, item, enchantments)) {
            if (damagerEntity.getSaturation() + (2 * enchantments.get(CEnchantments.NUTRITION.getEnchantment())) <= 20) {
                damagerEntity.setSaturation(damagerEntity.getSaturation() + (2 * enchantments.get(CEnchantments.NUTRITION.getEnchantment())));
            }

            if (damagerEntity.getSaturation() + (2 * enchantments.get(CEnchantments.NUTRITION.getEnchantment())) >= 20) {
                damagerEntity.setSaturation(20);
            }
        }

        if (damagerEntity.getHealth() > 0 && EnchantUtils.isEventActive(CEnchantments.VAMPIRE, damagerEntity, item, enchantments)) {
            // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
            final double maxHealth = damagerEntity.getAttribute(Attribute.MAX_HEALTH).getValue(); //todo() npe

            if (damagerEntity.getHealth() + event.getDamage() / 2 < maxHealth) damagerEntity.setHealth(damagerEntity.getHealth() + event.getDamage() / 2);

            if (damagerEntity.getHealth() + event.getDamage() / 2 >= maxHealth) damagerEntity.setHealth(maxHealth);
        }

        if (EnchantUtils.isEventActive(CEnchantments.BLINDNESS, damagerEntity, item, enchantments)) {
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, enchantments.get(CEnchantments.BLINDNESS.getEnchantment()) - 1));
        }

        if (EnchantUtils.isEventActive(CEnchantments.CONFUSION, damagerEntity, item, enchantments)) {
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 5 + (enchantments.get(CEnchantments.CONFUSION.getEnchantment())) * 20, 0));
        }

        if (EnchantUtils.isEventActive(CEnchantments.DOUBLEDAMAGE, damagerEntity, item, enchantments)) {
            event.setDamage((event.getDamage() * 2));
        }

        if (EnchantUtils.isEventActive(CEnchantments.EXECUTE, damagerEntity, item, enchantments)) {
            damagerEntity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 3 + (enchantments.get(CEnchantments.EXECUTE.getEnchantment())) * 20, 3));
        }

        if (EnchantUtils.isEventActive(CEnchantments.FASTTURN, damagerEntity, item, enchantments)) {
            event.setDamage(event.getDamage() + (event.getDamage() / 3));
        }

        if (EnchantUtils.isEventActive(CEnchantments.LIGHTWEIGHT, damagerEntity, item, enchantments)) {
            damagerEntity.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 5 * 20, enchantments.get(CEnchantments.LIGHTWEIGHT.getEnchantment()) - 1));
        }

        if (EnchantUtils.isEventActive(CEnchantments.OBLITERATE, damagerEntity, item, enchantments)) {
            event.getEntity().setVelocity(damagerEntity.getLocation().getDirection().multiply(2).setY(1.25));
        }

        if (EnchantUtils.isEventActive(CEnchantments.PARALYZE, damagerEntity, item, enchantments)) {
            for (final LivingEntity target : Methods.getNearbyLivingEntities(2D, damagerEntity)) {
                EntityDamageEvent damageByEntityEvent = new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.MAGIC,
                        DamageSource.builder(DamageType.INDIRECT_MAGIC).withDirectEntity(damagerEntity).build(), 5D);
                Methods.entityEvent(damagerEntity, target, damageByEntityEvent);
            }

            livingEntity.getWorld().strikeLightningEffect(livingEntity.getLocation());
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 3 * 20, 2));
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 3 * 20, 2));
        }

        if (EnchantUtils.isEventActive(CEnchantments.SLOWMO, damagerEntity, item, enchantments)) {
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 3 * 20, enchantments.get(CEnchantments.SLOWMO.getEnchantment())));
        }

        if (EnchantUtils.isEventActive(CEnchantments.SNARE, damagerEntity, item, enchantments)) {
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 3 * 20, 0));
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 3 * 20, 0));
        }

        if (EnchantUtils.isEventActive(CEnchantments.TRAP, damagerEntity, item, enchantments)) {
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 3 * 20, 2));
        }

        if (EnchantUtils.isEventActive(CEnchantments.VIPER, damagerEntity, item, enchantments)) {
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5 * 20, enchantments.get(CEnchantments.VIPER.getEnchantment())));
        }

        if (EnchantUtils.isEventActive(CEnchantments.WITHER, damagerEntity, item, enchantments)) {
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 2 * 20, 2));
        }

        if (EnchantUtils.isEventActive(CEnchantments.FAMISHED, damagerEntity, item, enchantments)) {
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 10 * 20, 1));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player damager = player.getKiller();

        if (damager == null) return;

        final ItemStack item = Methods.getItemInHand(damager);

        if (item.isEmpty()) return;

        final Map<CEnchantment, Integer> enchantments = this.instance.getEnchantments(item);

        if (enchantments.isEmpty()) return;

        if (EnchantUtils.isEventActive(CEnchantments.HEADLESS, damager, item, enchantments)) {
            final ItemStack itemStack = ItemType.PLAYER_HEAD.createItemStack();

            itemStack.setData(DataComponentTypes.ITEM_NAME, player.displayName());

            event.getDrops().add(itemStack);
        }

        if (EnchantUtils.isEventActive(CEnchantments.REVENGE, damager, item, enchantments)) {
            for (final Entity entity : player.getNearbyEntities(10, 10, 10).stream().filter(entity -> this.pluginSupport.isFriendly(entity, player)).toList()) {
                if (!(entity instanceof Player ally)) continue;

                ally.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 1));
                ally.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0));
                ally.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 5 * 20, 1));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        final LivingEntity livingEntity = event.getEntity();
        final Player killer = livingEntity.getKiller();

        if (killer == null) return;

        final ItemStack itemStack = Methods.getItemInHand(killer);

        if (itemStack.isEmpty()) return;

        final Map<CEnchantment, Integer> enchantments = this.instance.getEnchantments(itemStack);

        if (enchantments.isEmpty()) return;

        if (EnchantUtils.isEventActive(CEnchantments.INQUISITIVE, killer, itemStack, enchantments)) {
            event.setDroppedExp(event.getDroppedExp() * (enchantments.get(CEnchantments.INQUISITIVE.getEnchantment()) + 1));
        }

//        final Material material = EntityUtils.getHeadMaterial(livingEntity);
//
//        if (material != null && !EventUtils.containsDrop(event, material)) {
//            final double multiplier = this.crazyManager.getDecapitationHeadMap().getOrDefault(material, 0.0);
//
//            if (multiplier > 0.0 && EnchantUtils.isEventActive(CEnchantments.HEADLESS, killer, itemStack, enchantments, multiplier)) {
//                event.getDrops().add(ItemStack.of(material));
//            }
//        }

        if (livingEntity instanceof Player livingPlayer && EnchantUtils.isEventActive(CEnchantments.CHARGE, killer, itemStack, enchantments)) {
            final int radius = 4 + enchantments.get(CEnchantments.CHARGE.getEnchantment());

            killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1));

            for (final Entity target : killer.getNearbyEntities(radius, radius, radius).stream().filter(entity -> this.pluginSupport.isFriendly(livingPlayer, killer)).toList()) {
                if (target instanceof Player targetPlayer) {
                    targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1));
                }
            }
        }
    }

    private EquipmentSlot getSlot(final int slot) {
        return switch (slot) {
            case 1 -> EquipmentSlot.CHEST;
            case 2 -> EquipmentSlot.LEGS;
            case 3 -> EquipmentSlot.FEET;
            default -> EquipmentSlot.HEAD;
        };
    }

    private void rageInformPlayer(@NotNull final Player player, @NotNull final Map<String, String> placeholders, final float progress) {
        final User user = this.userRegistry.getUser(player);

        if (this.options.isUseRageBossBar()) {
            final Component component = user.getComponent(MessageKeys.rage_rage_up);

            if (!component.equals(Component.empty())) {
                this.bossBarController.updateBossBar(player, component, progress);
            }
        } else {
            user.sendMessage(MessageKeys.rage_rage_up, placeholders);
        }
    }

    private void rageInformPlayer(@NotNull final Player player, @NotNull final Key message, final float progress) {
        final User user = this.userRegistry.getUser(player);

        if (this.options.isUseRageBossBar()) {
            final Component component = user.getComponent(message);

            if (!component.equals(Component.empty())) {
                this.bossBarController.updateBossBar(player, component, progress);
            }
        } else {
            user.sendMessage(message);
        }
    }
}