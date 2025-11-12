package com.ryderbelserion.crazyenchantments.paper;

import com.ryderbelserion.crazyenchantments.paper.api.registry.EnchantmentRegistry;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CrazyEnchantmentsPlugin extends JavaPlugin {

    private final EnchantmentRegistry enchantmentRegistry;
    private final FusionPaper paper;

    public CrazyEnchantmentsPlugin(@NotNull final EnchantmentRegistry enchantmentRegistry, @NotNull final FusionPaper paper) {
        this.enchantmentRegistry = enchantmentRegistry;

        this.paper = paper;
    }

    private CrazyEnchantmentsPlatform platform;

    @Override
    public void onEnable() {
        this.platform = new CrazyEnchantmentsPlatform(this, this.paper);
        this.platform.start(getServer().getConsoleSender());
    }

    @Override
    public void onDisable() {
        this.platform.stop();
    }

    public @NotNull final EnchantmentRegistry getEnchantmentRegistry() {
        return this.enchantmentRegistry;
    }

    public @NotNull final CrazyEnchantmentsPlatform getPlatform() {
        return this.platform;
    }

    public @NotNull final FusionPaper getPaper() {
        return this.paper;
    }
}