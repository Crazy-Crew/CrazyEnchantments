package com.ryderbelserion.crazyenchantments.paper;

import com.ryderbelserion.crazyenchantments.paper.api.MessageRegistry;
import com.ryderbelserion.crazyenchantments.paper.commands.brigadier.BaseCommand;
import com.ryderbelserion.crazyenchantments.paper.enchants.EnchantmentRegistry;
import com.ryderbelserion.fusion.core.files.FileManager;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.api.commands.PaperCommandManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CrazyEnchantments extends JavaPlugin {

    private final MessageRegistry messageRegistry;
    private final EnchantmentRegistry enchantmentRegistry;
    private final FusionPaper paper;

    private final FileManager fileManager;

    public CrazyEnchantments(@NotNull final EnchantmentRegistry enchantmentRegistry, @NotNull final FusionPaper paper) {
        this.messageRegistry = new MessageRegistry();
        this.enchantmentRegistry = enchantmentRegistry;
        this.paper = paper;

        this.fileManager = this.paper.getFileManager();
    }

    @Override
    public void onEnable() {
        this.paper.enable(this);

        final Path path = getDataPath();

        this.fileManager.addFile(path.resolve("config.yml"), new ArrayList<>(), null);
        this.fileManager.addFile(path.resolve("messages.yml"), new ArrayList<>(), null);

        this.messageRegistry.populateMessages();
        
        this.enchantmentRegistry.getEnchantments().forEach((key, customEnchantment) -> {
            customEnchantment.init(this); // registers listeners, or things not meant for the bootstrap loader
        });

        final PaperCommandManager commandManager = this.paper.getCommandManager();

        commandManager.enable(new BaseCommand(), "You like my crazy enchants?", List.of("ce"));
    }

    @Override
    public void onDisable() {
        if (this.paper != null) {
            this.paper.disable();
        }
    }

    public @NotNull final MessageRegistry getMessageRegistry() {
        return this.messageRegistry;
    }

    public @NotNull final EnchantmentRegistry getEnchantmentRegistry() {
        return this.enchantmentRegistry;
    }

    public @NotNull final FileManager getFileManager() {
        return this.fileManager;
    }

    public @NotNull final FusionPaper getPaper() {
        return this.paper;
    }
}