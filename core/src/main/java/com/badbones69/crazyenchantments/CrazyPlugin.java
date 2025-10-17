package com.badbones69.crazyenchantments;

import com.badbones69.crazyenchantments.registry.MessageRegistry;
import com.badbones69.crazyenchantments.registry.UserRegistry;
import us.crazycrew.crazyenchantments.ICrazyEnchantments;
import com.ryderbelserion.fusion.core.FusionCore;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;

public abstract class CrazyPlugin extends ICrazyEnchantments {

    private final FusionCore fusion;
    private final Path dataPath;

    public CrazyPlugin(@NotNull final FusionCore fusion) {
        this.dataPath = fusion.getDataPath();
        this.fusion = fusion;
    }

    private MessageRegistry messageRegistry;
    private UserRegistry userRegistry;

    public void init() {
        this.messageRegistry = new MessageRegistry(this.userRegistry = new UserRegistry());
        this.messageRegistry.init();
    }

    @Override
    public @NotNull final MessageRegistry getMessageRegistry() {
        return this.messageRegistry;
    }

    @Override
    public @NotNull final UserRegistry getUserRegistry() {
        return this.userRegistry;
    }

    public @NotNull final FusionCore getFusion() {
        return this.fusion;
    }

    @Override
    public @NotNull final Path getDataPath() {
        return this.dataPath;
    }
}