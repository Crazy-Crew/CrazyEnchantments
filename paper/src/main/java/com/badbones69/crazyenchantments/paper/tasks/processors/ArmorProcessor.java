package com.badbones69.crazyenchantments.paper.tasks.processors;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.utils.EnchantUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.support.PluginSupport;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ArmorProcessor extends PoolProcessor {

    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final Starter starter = this.plugin.getStarter();

    private final Methods methods = this.starter.getMethods();

    private final PluginSupport pluginSupport = this.starter.getPluginSupport();

    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    public ArmorProcessor() {}

    public void add(UUID id){
        add(() -> process(id));
    }

    public void process(UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);

        if (player == null) return;

        for (final ItemStack armor : Objects.requireNonNull(player.getEquipment()).getArmorContents()) {
            Map<CEnchantment, Integer> enchantments = this.enchantmentBookSettings.getEnchantments(armor);
            if (enchantments.isEmpty()) continue;

            int heal = 1;
            // Uses getValue as if the player has health boost it is modifying the base so the value after the modifier is needed.
            double maxHealth = Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();

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

        PlayerInventory inv = player.getInventory();

        useHellForge(player, inv.getItemInMainHand(), this.enchantmentBookSettings.getEnchantments(inv.getItemInMainHand()));
        useHellForge(player, inv.getItemInOffHand(), this.enchantmentBookSettings.getEnchantments(inv.getItemInOffHand()));
    }

    private void checkCommander(ItemStack armor, Player player, Map<CEnchantment, Integer> enchantments) {

        if (!EnchantUtils.isMoveEventActive(CEnchantments.COMMANDER, player, enchantments)) return;

        int radius = 4 + enchantments.get(CEnchantments.COMMANDER.getEnchantment());

        player.getScheduler().run(this.plugin, playerTask -> {
            if (EnchantUtils.normalEnchantEvent(CEnchantments.COMMANDER, player, armor)) {
                PotionEffect fastDigging = new PotionEffect(PotionEffectType.HASTE, 3 * 20, 1);
                for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
                    e.getScheduler().run(plugin, task -> {
                        if (e instanceof Player otherPlayer && this.pluginSupport.isFriendly(player, otherPlayer)) {
                            otherPlayer.addPotionEffect(fastDigging);
                        }
                    }, null);
                }
            }
        }, null);

    }

    private void checkAngel(ItemStack armor, Player player, Map<CEnchantment, Integer> enchantments, int radius) {

        if (!EnchantUtils.isMoveEventActive(CEnchantments.ANGEL, player, enchantments)) return;

        player.getScheduler().run(this.plugin, playerTask -> {

            if (!EnchantUtils.normalEnchantEvent(CEnchantments.ANGEL, player, armor)) return;

            List<Player> players = new ArrayList<>();
            for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {
                entity.getScheduler().run(plugin, (task) -> {
                    if (entity instanceof Player otherPlayer && this.pluginSupport.isFriendly(player, otherPlayer)) {
                        players.add(otherPlayer);
                    }
                }, null);
            }


            if (players.isEmpty()) return;

            PotionEffect regeneration = new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 0);
            for (Player p : players) {
                p.getScheduler().run(plugin, task -> p.addPotionEffect(regeneration), null);
            }
        }, null);
    }

    private void checkImplants(ItemStack armor, Player player, Map<CEnchantment, Integer> enchantments) {

        if (!EnchantUtils.isMoveEventActive(CEnchantments.IMPLANTS, player, enchantments)) return;

        player.getScheduler().run(this.plugin, playerTask -> {
            if (EnchantUtils.normalEnchantEvent(CEnchantments.IMPLANTS, player, armor)) {
                player.setFoodLevel(Math.min(20, player.getFoodLevel() + enchantments.get(CEnchantments.IMPLANTS.getEnchantment())));
            }
        }, null);
    }

    private void checkNursery(ItemStack armor, Player player, Map<CEnchantment, Integer> enchantments, int heal, double maxHealth) {

        if (!EnchantUtils.isMoveEventActive(CEnchantments.NURSERY, player, enchantments)) return;

        player.getScheduler().run(this.plugin, playerTask -> {
            if (EnchantUtils.normalEnchantEvent(CEnchantments.NURSERY, player, armor)) {
                if (player.getHealth() + heal <= maxHealth) player.setHealth(player.getHealth() + heal);
                if (player.getHealth() + heal >= maxHealth) player.setHealth(maxHealth);
            }
        }, null);
    }

    private void useHellForge(Player player, ItemStack item, Map<CEnchantment, Integer> enchantments) {

        if (!EnchantUtils.isMoveEventActive(CEnchantments.HELLFORGED, player, enchantments)) return;

        player.getScheduler().run(this.plugin, playerTask -> {
            if (!EnchantUtils.normalEnchantEvent(CEnchantments.HELLFORGED, player, item)) return;
            int armorDurability = this.methods.getDurability(item);
            if (armorDurability <= 0) return;

            int finalArmorDurability = armorDurability;
            finalArmorDurability -= enchantments.get(CEnchantments.HELLFORGED.getEnchantment());
            this.methods.setDurability(item, finalArmorDurability);
        }, null);
    }
}