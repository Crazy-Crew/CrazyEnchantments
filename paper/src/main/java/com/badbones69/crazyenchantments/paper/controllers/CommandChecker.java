package com.badbones69.crazyenchantments.paper.controllers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandChecker implements Listener {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    @NotNull
    private final CrazyManager crazyManager = this.starter.getCrazyManager();

    @NotNull
    private final EnchantmentBookSettings enchantmentBookSettings = this.starter.getEnchantmentBookSettings();

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClear(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Map<CEnchantments, HashMap<PotionEffectType, Integer>> enchantmentPotions = this.crazyManager.getEnchantmentPotions();

        if (Arrays.asList("/ci", "/clear", "/clearinventory").contains(event.getMessage().toLowerCase())) {
            Arrays.stream(player.getEquipment().getArmorContents())
                    .map(this.enchantmentBookSettings::getEnchantments).forEach((enchant) -> enchantmentPotions.entrySet()
                            .stream().filter(enchantedPotion -> enchantedPotion.getKey().getEnchantment().equals(enchant))
                            .forEach(enchantedPotion -> enchantedPotion.getValue().keySet().forEach(player::removePotionEffect)));

        } else if (event.getMessage().equalsIgnoreCase("/heal")) {
            updateEffects(player);
        }
    }

    private void updateEffects(Player player) {
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.crazyManager.updatePlayerEffects(player), 5);
    }
}