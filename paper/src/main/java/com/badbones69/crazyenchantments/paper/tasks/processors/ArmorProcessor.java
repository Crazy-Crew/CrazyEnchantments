package com.badbones69.crazyenchantments.paper.tasks.processors;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.utils.EnchantUtils;
import com.badbones69.crazyenchantments.paper.support.PluginSupport;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import java.util.*;

public class ArmorProcessor extends PoolProcessor { //todo() what do I even fucking do with this lol

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final CrazyInstance instance = this.plugin.getInstance();

    private final Starter starter = this.plugin.getStarter();

    private final Methods methods = this.starter.getMethods();

    private final PluginSupport pluginSupport = this.starter.getPluginSupport();

    public ArmorProcessor() {}

    public void add(@NotNull final UUID id){
        add(() -> process(id));
    }

    public void process(@NotNull final UUID playerId) {
        Player player = this.plugin.getServer().getPlayer(playerId);

        if (player == null) return;

        for (final ItemStack armor : Objects.requireNonNull(player.getEquipment()).getArmorContents()) {
            if (armor == null || armor.isEmpty()) continue;

            Map<CEnchantment, Integer> enchantments = this.instance.getEnchantments(armor);

            if (enchantments.isEmpty()) continue;

            int heal = 1;
            // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
            double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getValue();

            if (maxHealth > player.getHealth() && player.getHealth() > 0) {
                checkNursery(armor, player, enchantments, heal, maxHealth);
            }

            if (player.getFoodLevel() < 20) {
                checkImplants(armor, player, enchantments);
            }

            checkCommander(armor, player, enchantments);

            if (PluginSupport.SupportedPlugins.FACTIONS_UUID.isPluginLoaded()) {
                final int radius = 4 + enchantments.get(CEnchantments.ANGEL.getEnchantment());
                checkAngel(armor, player, enchantments, radius);
            }

            useHellForge(player, armor, enchantments);
        }

        PlayerInventory inventory = player.getInventory();

        final ItemStack mainHand = inventory.getItemInMainHand();

        if (!mainHand.isEmpty()) {
            useHellForge(player, mainHand, this.instance.getEnchantments(mainHand));
        }

        final ItemStack offHand = inventory.getItemInOffHand();

        if (!offHand.isEmpty()) {
            useHellForge(player, offHand, this.instance.getEnchantments(offHand));
        }
    }

    private void checkCommander(@NotNull final ItemStack armor, @NotNull final Player player, @NotNull final Map<CEnchantment, Integer> enchantments) {
        if (!EnchantUtils.isMoveEventActive(CEnchantments.COMMANDER, player, enchantments)) return;

        int radius = 4 + enchantments.get(CEnchantments.COMMANDER.getEnchantment());

        new FoliaScheduler(this.plugin, null, player) {
            @Override
            public void run() {
                if (EnchantUtils.normalEnchantEvent(CEnchantments.COMMANDER, player, armor)) {
                    final PotionEffect fastDigging = new PotionEffect(PotionEffectType.HASTE, 3 * 20, 1);

                    for (final Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                        new FoliaScheduler(plugin, null, entity) {
                            @Override
                            public void run() {
                                if (entity instanceof Player otherPlayer && pluginSupport.isFriendly(player, otherPlayer)) {
                                    otherPlayer.addPotionEffect(fastDigging);
                                }
                            }
                        }.runNextTick();
                    }
                }
            }
        }.runNextTick();

    }

    private void checkAngel(@NotNull final ItemStack armor, @NotNull final Player player, @NotNull final Map<CEnchantment, Integer> enchantments, final int radius) {
        if (!EnchantUtils.isMoveEventActive(CEnchantments.ANGEL, player, enchantments)) return;

        new FoliaScheduler(this.plugin, null, player) {
            @Override
            public void run() {
                if (!EnchantUtils.normalEnchantEvent(CEnchantments.ANGEL, player, armor)) return;

                final List<Player> players = new ArrayList<>();

                for (final Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                    new FoliaScheduler(plugin, null, entity) {
                        @Override
                        public void run() {
                            if (entity instanceof Player otherPlayer && pluginSupport.isFriendly(player, otherPlayer)) {
                                players.add(otherPlayer);
                            }
                        }
                    }.runNextTick();
                }

                if (players.isEmpty()) return;

                final PotionEffect regeneration = new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 0);

                for (final Player target : players) {
                    new FoliaScheduler(plugin, null, target) {
                        @Override
                        public void run() {
                            target.addPotionEffect(regeneration);
                        }
                    }.runNextTick();
                }
            }
        }.runNextTick();
    }

    private void checkImplants(@NotNull final ItemStack armor, @NotNull final Player player, @NotNull final Map<CEnchantment, Integer> enchantments) {
        if (!EnchantUtils.isMoveEventActive(CEnchantments.IMPLANTS, player, enchantments)) return;

        new FoliaScheduler(this.plugin, null, player) {
            @Override
            public void run() {
                if (EnchantUtils.normalEnchantEvent(CEnchantments.IMPLANTS, player, armor)) {
                    player.setFoodLevel(Math.min(20, player.getFoodLevel() + enchantments.get(CEnchantments.IMPLANTS.getEnchantment())));
                }
            }
        }.runNextTick();
    }

    private void checkNursery(@NotNull final ItemStack armor, @NotNull final Player player, @NotNull final Map<CEnchantment, Integer> enchantments, final int heal, final double maxHealth) {
        if (!EnchantUtils.isMoveEventActive(CEnchantments.NURSERY, player, enchantments)) return;

        new FoliaScheduler(this.plugin, null, player) {
            @Override
            public void run() {
                if (EnchantUtils.normalEnchantEvent(CEnchantments.NURSERY, player, armor)) {
                    if (player.getHealth() + heal <= maxHealth) player.setHealth(player.getHealth() + heal);
                    if (player.getHealth() + heal >= maxHealth) player.setHealth(maxHealth);
                }
            }
        }.runNextTick();
    }

    private void useHellForge(@NotNull final Player player, @NotNull final ItemStack item, @NotNull final Map<CEnchantment, Integer> enchantments) {
        if (item.isEmpty()) return;

        if (!EnchantUtils.isMoveEventActive(CEnchantments.HELLFORGED, player, enchantments)) return;

        new FoliaScheduler(this.plugin, null, player) {
            @Override
            public void run() {
                if (!EnchantUtils.normalEnchantEvent(CEnchantments.HELLFORGED, player, item)) return;

                final int armorDurability = methods.getDurability(item);

                if (armorDurability <= 0) return;

                int finalArmorDurability = armorDurability;

                finalArmorDurability -= enchantments.get(CEnchantments.HELLFORGED.getEnchantment());

                methods.setDurability(item, finalArmorDurability);
            }
        }.runNextTick();
    }
}