package com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.interfaces;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import java.nio.file.Path;

public abstract class IMigrator {

    protected final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    protected final CrazyPlatform platform = this.plugin.getPlatform();

    protected final FusionPaper fusion = this.platform.getFusion();

    protected final FileManager fileManager = this.platform.getFileManager();

    protected final MessageRegistry messageRegistry = this.fusion.getMessageRegistry();

    protected final Path path = this.platform.getDataPath();

    protected final Starter starter = this.plugin.getStarter();

    protected final EnchantmentBookSettings bookSettings = this.starter.getEnchantmentBookSettings();

    protected final CommandSender sender;
    protected final MigrationType type;

    public IMigrator(@NonNull final CommandSender sender, @NonNull final MigrationType type) {
        this.sender = sender;
        this.type = type;
    }

    public abstract void init();
}