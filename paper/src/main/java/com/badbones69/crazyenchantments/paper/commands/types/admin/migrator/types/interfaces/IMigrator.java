package com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.types.interfaces;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyPlatform;
import com.badbones69.crazyenchantments.paper.commands.types.admin.migrator.enums.MigrationType;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.ryderbelserion.common.api.enums.messages.Messages;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    protected final long startTime;

    public IMigrator(@NonNull final CommandSender sender, @NonNull final MigrationType type) {
        this.startTime = System.nanoTime();
        this.sender = sender;
        this.type = type;
    }

    public abstract void init();

    public void sendMessage(List<String> files, final int success, final int failed, final boolean isDetailed) {
        final Map<String, String> placeholders = Map.of(
                "{files}", files.size() > 1 ? StringUtils.toString(files) : files.isEmpty() ? "N/A" : files.getFirst(),
                "{succeeded_amount}", String.valueOf(success),
                "{failed_amount}", String.valueOf(failed),
                "{type}", this.type.getName(),
                "{time}", time()
        );

        if (isDetailed) {
            Messages.command_migrate_detailed_success.sendMessage(this.sender, placeholders);

            return;
        }

        Messages.command_migrate_simple_success.sendMessage(this.sender, placeholders);
    }

    public final String time() {
        final double time = (double) (System.nanoTime() - this.startTime) / 1.0E9D;

        return String.format(Locale.ROOT, "%.3fs", time);
    }
}