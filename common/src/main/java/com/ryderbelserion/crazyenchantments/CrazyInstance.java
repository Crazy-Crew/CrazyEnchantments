package com.ryderbelserion.crazyenchantments;

import com.ryderbelserion.crazyenchantments.interfaces.ICrazyEnchantments;
import com.ryderbelserion.crazyenchantments.objects.ConfigOptions;
import com.ryderbelserion.fusion.core.api.enums.FileAction;
import com.ryderbelserion.fusion.core.api.utils.FileUtils;
import com.ryderbelserion.fusion.core.files.FileManager;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CrazyInstance implements ICrazyEnchantments {

    private final FileManager fileManager;
    private final Path path;

    public CrazyInstance(@NotNull final FileManager fileManager, @NotNull final Path path) {
        this.fileManager = fileManager;
        this.path = path;
    }

    private ConfigOptions options;

    @Override
    public void init() {
        this.fileManager.addFile(this.path.resolve("config.yml"), new ArrayList<>(), options -> options.shouldCopyDefaults(true));

        List.of(
                "BlockList.yml",
                "Data.yml",
                "Enchantment-Types.yml",
                "Enchantments.yml",
                "GKitz.yml",
                "HeadMap.yml",
                "Messages.yml",
                "Tinker.yml"
        ).forEach(name -> FileUtils.extract(name, this.path.resolve("examples"), new ArrayList<>() {{
            add(FileAction.EXTRACT_FOLDER);
            add(FileAction.DELETE_FILE);
        }}));

        this.options = new ConfigOptions();
        this.options.init();
    }

    @Override
    public void reload() {
        List.of(
                "BlockList.yml",
                "Data.yml",
                "Enchantment-Types.yml",
                "Enchantments.yml",
                "GKitz.yml",
                "HeadMap.yml",
                "Messages.yml",
                "Tinker.yml"
        ).forEach(name -> FileUtils.extract(name, this.path.resolve("examples"), new ArrayList<>() {{
            add(FileAction.EXTRACT_FOLDER);
            add(FileAction.DELETE_FILE);
        }}));

        this.options = new ConfigOptions();
        this.options.init();
    }

    @Override
    public final FileManager getFileManager() {
        return this.fileManager;
    }

    @Override
    public final ConfigOptions getOptions() {
        return this.options;
    }

    @Override
    public final Path getPath() {
        return this.path;
    }
}