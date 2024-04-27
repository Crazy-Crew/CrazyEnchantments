package com.badbones69.crazyenchantments.paper.tasks.processors;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.api.utils.EnchantUtils;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.support.PluginSupport;
import com.badbones69.crazyenchantments.paper.support.PluginSupport.SupportedPlugins;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ArmorMoveProcessor extends Processor<UUID> {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final Methods methods = this.starter.getMethods();

    @NotNull
    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    // Plugin Support.
    @NotNull
    private final PluginSupport pluginSupport = this.starter.getPluginSupport();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    private final Processor<Runnable> syncProcessor;

    public ArmorMoveProcessor() {
        this.syncProcessor = new RunnableSyncProcessor();
    }

    public void stop() {
        this.syncProcessor.stop();
        super.stop();
    }

    public void start() {
        this.syncProcessor.start();
        super.start();
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

            if (SupportedPlugins.FACTIONS_UUID.isPluginLoaded()) {
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

        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            if (EnchantUtils.normalEnchantEvent(CEnchantments.COMMANDER, player, armor)) {
                player.getNearbyEntities(radius, radius, radius).stream().filter(e ->
                        e instanceof Player && this.pluginSupport.isFriendly(player, e)).map(e -> (Player) e)
                        .forEach(otherPlayer -> otherPlayer.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 3 * 20, 1)));
            }
        });
    }

    private void checkAngel(ItemStack armor, Player player, Map<CEnchantment, Integer> enchantments, int radius) {
        if (!EnchantUtils.isMoveEventActive(CEnchantments.ANGEL, player, enchantments)) return;

        List<Player> players = player.getNearbyEntities(radius, radius, radius).stream().filter(entity ->
                this.pluginSupport.isFriendly(player, entity)).map(entity -> (Player) entity).toList();
        
        if (players.isEmpty()) return;

        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            if (EnchantUtils.normalEnchantEvent(CEnchantments.ANGEL, player, armor)) {
                players.forEach(p -> p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 3 * 20, 0)));
            }
        });
    }

    private void checkImplants(ItemStack armor, Player player, Map<CEnchantment, Integer> enchantments) {
        if (!EnchantUtils.isMoveEventActive(CEnchantments.IMPLANTS, player, enchantments)) return;

        plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            if (EnchantUtils.normalEnchantEvent(CEnchantments.IMPLANTS, player, armor)) {
                player.setFoodLevel(Math.min(20, player.getFoodLevel() + enchantments.get(CEnchantments.IMPLANTS.getEnchantment())));
            }
        });
    }

    private void checkNursery(ItemStack armor, Player player, Map<CEnchantment, Integer> enchantments, int heal, double maxHealth) {
        if (!EnchantUtils.isMoveEventActive(CEnchantments.NURSERY, player, enchantments)) return;

        plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            if (EnchantUtils.normalEnchantEvent(CEnchantments.NURSERY, player, armor)) {
                if (player.getHealth() + heal <= maxHealth) player.setHealth(player.getHealth() + heal);
                if (player.getHealth() + heal >= maxHealth) player.setHealth(maxHealth);
            }
        });
    }

    private void useHellForge(Player player, ItemStack item, Map<CEnchantment, Integer> enchantments) {
        if (!EnchantUtils.isMoveEventActive(CEnchantments.HELLFORGED, player, enchantments)) return;
        int armorDurability = this.methods.getDurability(item);
        if (armorDurability <= 0) return;

        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            if (EnchantUtils.normalEnchantEvent(CEnchantments.HELLFORGED, player, item)) {
                int finalArmorDurability = armorDurability;
                finalArmorDurability -= enchantments.get(CEnchantments.HELLFORGED.getEnchantment());
                this.methods.setDurability(item, finalArmorDurability);
            }
        });
    }
}