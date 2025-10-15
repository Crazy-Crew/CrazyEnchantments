package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.managers.currency.enums.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.v2.Messages;
import com.badbones69.crazyenchantments.paper.api.events.RageBreakEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEPlayer;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.utils.EnchantUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EntityUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EventUtils;
import com.badbones69.crazyenchantments.paper.managers.configs.ConfigManager;
import com.badbones69.crazyenchantments.paper.controllers.BossBarController;
import com.badbones69.crazyenchantments.paper.managers.currency.CurrencyManager;
import com.badbones69.crazyenchantments.paper.support.PluginSupport;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SwordEnchantments implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyInstance instance = this.plugin.getInstance();

    private final ConfigManager options = this.plugin.getConfigManager();

    @NotNull
    private final CrazyManager crazyManager = null;

    @NotNull
    private final Methods methods = null;

    // Plugin Support.
    @NotNull
    private final PluginSupport pluginSupport = null;

    @NotNull
    private final BossBarController bossBarController = this.plugin.getBossBarController();

    private final CurrencyManager currencyManager = this.instance.getCurrencyManager();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (EventUtils.isIgnoredEvent(event) || EventUtils.isIgnoredUUID(event.getDamager().getUniqueId())) return;

        if (this.pluginSupport.isFriendly(event.getDamager(), event.getEntity())) return;

        if (this.options.isBreakRageOnDamage() && event.getEntity() instanceof Player player) {
            final ItemStack itemStack = this.methods.getItemInHand(player);

            if (!itemStack.isEmpty()) {
                CEPlayer cePlayer = this.crazyManager.getCEPlayer(player);

                if (cePlayer != null) {
                    RageBreakEvent rageBreakEvent = new RageBreakEvent(player, event.getDamager(), itemStack);

                    this.plugin.getServer().getPluginManager().callEvent(rageBreakEvent);

                    if (!rageBreakEvent.isCancelled() && cePlayer.hasRage()) {
                        cePlayer.getRageTask().cancel();
                        cePlayer.setRageMultiplier(0.0);
                        cePlayer.setRageLevel(0);
                        cePlayer.setRage(false);

                        rageInformPlayer(player, Messages.RAGE_DAMAGED, 0f);
                    }
                }
            }
        }

        if (!(event.getEntity() instanceof LivingEntity en)) return;
        if (!(event.getDamager() instanceof final Player damager)) return;

        CEPlayer cePlayer = this.crazyManager.getCEPlayer(damager);
        ItemStack item = this.methods.getItemInHand(damager);

        if (item.isEmpty()) return;

        if (event.getEntity().isDead()) return;

        Map<CEnchantment, Integer> enchantments = this.instance.getEnchantments(item);
        boolean isEntityPlayer = event.getEntity() instanceof Player;

        if (isEntityPlayer && EnchantUtils.isEventActive(CEnchantments.DISARMER, damager, item, enchantments)) {
            Player player = (Player) event.getEntity();

            EquipmentSlot equipmentSlot = getSlot(this.methods.percentPick(4, 0));

            ItemStack armor = switch (equipmentSlot) {
                case HEAD -> player.getEquipment().getHelmet();
                case CHEST -> player.getEquipment().getChestplate();
                case LEGS -> player.getEquipment().getLeggings();
                case FEET -> player.getEquipment().getBoots();
                default -> null;
            };

            if (armor != null) {
                switch (equipmentSlot) {
                    case HEAD -> player.getEquipment().setHelmet(null);
                    case CHEST -> player.getEquipment().setChestplate(null);
                    case LEGS -> player.getEquipment().setLeggings(null);
                    case FEET -> player.getEquipment().setBoots(null);
                }

                this.methods.addItemToInventory(player, armor);
            }
        }

        if (isEntityPlayer && EnchantUtils.isEventActive(CEnchantments.DISORDER, damager, item, enchantments)) {
            Player player = (Player) event.getEntity();
            Inventory inventory = player.getInventory();
            List<ItemStack> items = new ArrayList<>();
            List<Integer> slots = new ArrayList<>();

            for (int i = 0; i < 9; i++) {
                ItemStack inventoryItem = inventory.getItem(i);

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

            Messages.DISORDERED_ENEMY_HOT_BAR.sendMessage(damager);
        }

        // Check if CEPlayer is null as plugins like citizen use Player objects.
        if (cePlayer != null && EnchantUtils.isEventActive(CEnchantments.RAGE, damager, item, enchantments)) {

            if (cePlayer.hasRage()) {
                cePlayer.getRageTask().cancel();

                if (cePlayer.getRageMultiplier() <= this.options.getRageMaxLevel())
                    cePlayer.setRageMultiplier(cePlayer.getRageMultiplier() + (enchantments.get(CEnchantments.RAGE.getEnchantment()) * this.options.getRageIncrement()));

                int rageUp = cePlayer.getRageLevel() + 1;

                if (cePlayer.getRageMultiplier().intValue() >= rageUp) {
                    rageInformPlayer(damager, Map.of("%Level%", String.valueOf(rageUp)), ((float) rageUp / (float) (this.options.getRageMaxLevel() + 1)));
                    cePlayer.setRageLevel(rageUp);
                }

                event.setDamage(event.getDamage() * cePlayer.getRageMultiplier());
            } else {
                cePlayer.setRageMultiplier(1.0);
                cePlayer.setRage(true);
                cePlayer.setRageLevel(1);

                rageInformPlayer(damager, Messages.RAGE_BUILDING, ((float) cePlayer.getRageLevel() / (float) this.options.getRageMaxLevel()));
            }

            cePlayer.setRageTask(new FoliaScheduler(this.plugin, null, cePlayer.getPlayer()) {
                @Override
                public void run() {
                    cePlayer.setRageMultiplier(0.0);
                    cePlayer.setRage(false);
                    cePlayer.setRageLevel(0);

                    rageInformPlayer(damager, Messages.RAGE_COOLED_DOWN, 0f);
                }
            }.runDelayed(80));
        }

        if (en instanceof Player player && EnchantUtils.isEventActive(CEnchantments.SKILLSWIPE, damager, item, enchantments)) {
            int amount = 4 + enchantments.get(CEnchantments.SKILLSWIPE.getEnchantment());

            if (player.getTotalExperience() > 0) { //todo() allow changing this to give money by adding per enchant currency support.
                if (this.currencyManager.hasAmount(Currency.XP_TOTAL, player, amount)) {
                    this.currencyManager.takeAmount(Currency.XP_TOTAL, player, amount);
                } else {
                    player.setTotalExperience(0);
                }

                this.currencyManager.addAmount(Currency.XP_TOTAL, damager, amount);
            }
        }

        if (damager.getHealth() > 0 && EnchantUtils.isEventActive(CEnchantments.LIFESTEAL, damager, item, enchantments)) {
            int steal = enchantments.get(CEnchantments.LIFESTEAL.getEnchantment());
            // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
            double maxHealth = damager.getAttribute(Attribute.MAX_HEALTH).getValue(); //todo() npe

            if (damager.getHealth() + steal < maxHealth) damager.setHealth(damager.getHealth() + steal);

            if (damager.getHealth() + steal >= maxHealth) damager.setHealth(maxHealth);
        }

        if (EnchantUtils.isEventActive(CEnchantments.NUTRITION, damager, item, enchantments)) {
            if (damager.getSaturation() + (2 * enchantments.get(CEnchantments.NUTRITION.getEnchantment())) <= 20) damager.setSaturation(damager.getSaturation() + (2 * enchantments.get(CEnchantments.NUTRITION.getEnchantment())));

            if (damager.getSaturation() + (2 * enchantments.get(CEnchantments.NUTRITION.getEnchantment())) >= 20) damager.setSaturation(20);
        }

        if (damager.getHealth() > 0 && EnchantUtils.isEventActive(CEnchantments.VAMPIRE, damager, item, enchantments)) {
            // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
            double maxHealth = damager.getAttribute(Attribute.MAX_HEALTH).getValue(); //todo() npe

            if (damager.getHealth() + event.getDamage() / 2 < maxHealth) damager.setHealth(damager.getHealth() + event.getDamage() / 2);

            if (damager.getHealth() + event.getDamage() / 2 >= maxHealth) damager.setHealth(maxHealth);
        }

        if (EnchantUtils.isEventActive(CEnchantments.BLINDNESS, damager, item, enchantments)) {
            en.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, enchantments.get(CEnchantments.BLINDNESS.getEnchantment()) - 1));
        }

        if (EnchantUtils.isEventActive(CEnchantments.CONFUSION, damager, item, enchantments)) {
            en.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 5 + (enchantments.get(CEnchantments.CONFUSION.getEnchantment())) * 20, 0));
        }

        if (EnchantUtils.isEventActive(CEnchantments.DOUBLEDAMAGE, damager, item, enchantments)) {
            event.setDamage((event.getDamage() * 2));
        }

        if (EnchantUtils.isEventActive(CEnchantments.EXECUTE, damager, item, enchantments)) {
            damager.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 3 + (enchantments.get(CEnchantments.EXECUTE.getEnchantment())) * 20, 3));
        }

        if (EnchantUtils.isEventActive(CEnchantments.FASTTURN, damager, item, enchantments)) {
            event.setDamage(event.getDamage() + (event.getDamage() / 3));
        }

        if (EnchantUtils.isEventActive(CEnchantments.LIGHTWEIGHT, damager, item, enchantments)) {
            damager.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 5 * 20, enchantments.get(CEnchantments.LIGHTWEIGHT.getEnchantment()) - 1));
        }

        if (EnchantUtils.isEventActive(CEnchantments.OBLITERATE, damager, item, enchantments)) {
            event.getEntity().setVelocity(damager.getLocation().getDirection().multiply(2).setY(1.25));
        }

        if (EnchantUtils.isEventActive(CEnchantments.PARALYZE, damager, item, enchantments)) {

            for (LivingEntity entity : this.methods.getNearbyLivingEntities(2D, damager)) {
                EntityDamageEvent damageByEntityEvent = new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.MAGIC, DamageSource.builder(DamageType.INDIRECT_MAGIC).withDirectEntity(damager).build(), 5D);
                this.methods.entityEvent(damager, entity, damageByEntityEvent);
            }

            en.getWorld().strikeLightningEffect(en.getLocation());
            en.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 3 * 20, 2));
            en.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 3 * 20, 2));
        }

        if (EnchantUtils.isEventActive(CEnchantments.SLOWMO, damager, item, enchantments)) {
            en.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 3 * 20, enchantments.get(CEnchantments.SLOWMO.getEnchantment())));
        }

        if (EnchantUtils.isEventActive(CEnchantments.SNARE, damager, item, enchantments)) {
            en.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 3 * 20, 0));
            en.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 3 * 20, 0));
        }

        if (EnchantUtils.isEventActive(CEnchantments.TRAP, damager, item, enchantments)) {
            en.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 3 * 20, 2));
        }

        if (EnchantUtils.isEventActive(CEnchantments.VIPER, damager, item, enchantments)) {
            en.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 5 * 20, enchantments.get(CEnchantments.VIPER.getEnchantment())));
        }

        if (EnchantUtils.isEventActive(CEnchantments.WITHER, damager, item, enchantments)) {
            en.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 2 * 20, 2));
        }

        if (EnchantUtils.isEventActive(CEnchantments.FAMISHED, damager, item, enchantments)) {
            en.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 10 * 20, 1));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;

        Player damager = event.getEntity().getKiller();
        Player player = event.getEntity();
        ItemStack item = this.methods.getItemInHand(damager);
        Map<CEnchantment, Integer> enchantments = this.instance.getEnchantments(item);

        if (EnchantUtils.isEventActive(CEnchantments.HEADLESS, damager, item, enchantments)) {
            ItemStack head = new ItemBuilder().setMaterial("PLAYER_HEAD").setPlayerName(player.getName()).build();
            event.getDrops().add(head);
        }

        if (EnchantUtils.isEventActive(CEnchantments.REVENGE, damager, item, enchantments)) {
            for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                if (!pluginSupport.isFriendly(entity, player)) continue;
                Player ally = (Player) entity;

                ally.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 1));
                ally.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 0));
                ally.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 5 * 20, 1));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player damager = event.getEntity().getKiller();
            ItemStack item = this.methods.getItemInHand(damager);

            if (item.isEmpty()) return;

            Map<CEnchantment, Integer> enchantments = this.instance.getEnchantments(item);

            if (EnchantUtils.isEventActive(CEnchantments.INQUISITIVE, damager, item, enchantments)) {
                event.setDroppedExp(event.getDroppedExp() * (enchantments.get(CEnchantments.INQUISITIVE.getEnchantment()) + 1));
            }

            Material headMat = EntityUtils.getHeadMaterial(event.getEntity());
            if (headMat != null && !EventUtils.containsDrop(event, headMat)) {
                double multiplier = this.crazyManager.getDecapitationHeadMap().getOrDefault(headMat, 0.0);
                if (multiplier != 0.0 && EnchantUtils.isEventActive(CEnchantments.HEADLESS, damager, item, enchantments, multiplier)) {
                    ItemStack head = new ItemBuilder().setMaterial(headMat).build();
                    event.getDrops().add(head);
                }
			}

            // The entity that is killed is a player.
            if (event.getEntity() instanceof Player && EnchantUtils.isEventActive(CEnchantments.CHARGE, damager, item, enchantments)) {
                int radius = 4 + enchantments.get(CEnchantments.CHARGE.getEnchantment());
                damager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1));

                damager.getNearbyEntities(radius, radius, radius).stream().filter(entity ->
                        this.pluginSupport.isFriendly(entity, damager)).forEach(entity ->
                        ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10 * 20, 1)));
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
        if (this.options.isUseRageBossBar()) {
            final Component component = Messages.RAGE_RAGE_UP.getMessage(player);

            if (!component.equals(Component.empty())) {
                this.bossBarController.updateBossBar(player, component, progress);
            }
        } else {
            Messages.RAGE_RAGE_UP.sendMessage(player, placeholders);
        }
    }

    private void rageInformPlayer(@NotNull final Player player, @NotNull final Messages message, final float progress) {
        if (this.options.isUseRageBossBar()) {
            final Component component = message.getMessage(player);

            if (!component.equals(Component.empty())) {
                this.bossBarController.updateBossBar(player, component, progress);
            }
        } else {
            message.sendMessage(player);
        }
    }
}