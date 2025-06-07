package com.ryderbelserion.crazyenchantments.paper;

import com.ryderbelserion.crazyenchantments.paper.enchants.EnchantmentRegistry;
import com.ryderbelserion.crazyenchantments.paper.listeners.VeinMinerEnchant;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CrazyEnchantments extends JavaPlugin {

    private final EnchantmentRegistry registry;

    public CrazyEnchantments(@NotNull final EnchantmentRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new VeinMinerEnchant(), this);
    }

    @Override
    public void onDisable() {

    }

    public @NotNull final EnchantmentRegistry getRegistry() {
        return this.registry;
    }
}