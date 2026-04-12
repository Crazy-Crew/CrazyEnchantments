package com.ryderbelserion.crazyenchantments.paper;

import com.ryderbelserion.crazyenchantments.paper.api.CrazyEnchantmentsPaper;
import com.ryderbelserion.crazyenchantments.paper.api.registry.enchants.EnchantmentRegistry;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CrazyPlugin extends JavaPlugin {

    private final EnchantmentRegistry enchantmentRegistry;
    private final FusionPaper fusion;

    public CrazyPlugin(@NotNull final EnchantmentRegistry enchantmentRegistry, @NotNull final FusionPaper fusion) {
        this.enchantmentRegistry = enchantmentRegistry;
        this.fusion = fusion;
    }

    private CrazyEnchantmentsPaper platform;

    @Override
    public void onEnable() {
        this.platform = new CrazyEnchantmentsPaper(this, this.fusion);
        this.platform.init();
    }

    @Override
    public void onDisable() {
        if (this.platform != null) {
            this.platform.shutdown();
        }
    }

    public @NotNull final EnchantmentRegistry getEnchantmentRegistry() {
        return this.enchantmentRegistry;
    }

    public @NotNull final CrazyEnchantmentsPaper getPlatform() {
        return this.platform;
    }

    public @NotNull final FusionPaper getPaper() {
        return this.fusion;
    }
}