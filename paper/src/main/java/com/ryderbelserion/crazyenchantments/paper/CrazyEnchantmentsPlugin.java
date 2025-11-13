package com.ryderbelserion.crazyenchantments.paper;

import com.ryderbelserion.crazyenchantments.paper.api.registry.EnchantmentRegistry;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CrazyEnchantmentsPlugin extends JavaPlugin {

    private final EnchantmentRegistry enchantmentRegistry;
    private final FusionPaper fusion;

    public CrazyEnchantmentsPlugin(@NotNull final EnchantmentRegistry enchantmentRegistry, @NotNull final FusionPaper fusion) {
        this.enchantmentRegistry = enchantmentRegistry;
        this.fusion = fusion;
    }

    private CrazyEnchantmentsPlatform platform;

    @Override
    public void onEnable() {
        this.platform = new CrazyEnchantmentsPlatform(this, this.fusion.setPlugin(this).init());
        this.platform.start(getServer().getConsoleSender());
    }

    @Override
    public void onDisable() {
        if (this.platform != null) {
            this.platform.stop();
        }
    }

    public @NotNull final EnchantmentRegistry getEnchantmentRegistry() {
        return this.enchantmentRegistry;
    }

    public @NotNull final CrazyEnchantmentsPlatform getPlatform() {
        return this.platform;
    }

    public @NotNull final FusionPaper getPaper() {
        return this.fusion;
    }
}