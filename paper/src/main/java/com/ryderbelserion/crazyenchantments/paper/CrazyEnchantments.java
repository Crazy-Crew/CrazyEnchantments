package com.ryderbelserion.crazyenchantments.paper;

import com.ryderbelserion.crazyenchantments.paper.commands.brigadier.BaseCommand;
import com.ryderbelserion.crazyenchantments.paper.enchants.EnchantmentRegistry;
import com.ryderbelserion.fusion.core.files.FileManager;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.api.commands.PaperCommandManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class CrazyEnchantments extends JavaPlugin {

    private final EnchantmentRegistry registry;
    private final FusionPaper paper;

    private final FileManager fileManager;

    public CrazyEnchantments(@NotNull final EnchantmentRegistry registry, @NotNull final FusionPaper paper) {
        this.registry = registry;
        this.paper = paper;

        this.fileManager = this.paper.getFileManager();
    }

    @Override
    public void onEnable() {
        this.paper.enable(this);
        
        this.registry.getEnchantments().forEach((key, customEnchantment) -> {
            customEnchantment.init(this); // registers listeners, or things not meant for the bootstrap loader
        });

        final PaperCommandManager commandManager = this.paper.getCommandManager();

        commandManager.enable(new BaseCommand(), "It's an enchanting plugin.", List.of("ce"));
    }

    @Override
    public void onDisable() {
        if (this.paper != null) {
            this.paper.disable();
        }
    }

    public @NotNull final EnchantmentRegistry getRegistry() {
        return this.registry;
    }

    public @NotNull final FusionPaper getPaper() {
        return this.paper;
    }
}