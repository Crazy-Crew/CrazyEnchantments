package com.badbones69.crazyenchantments;

import us.crazycrew.crazyenchantments.objects.ICrazyEnchantments;
import com.ryderbelserion.fusion.core.FusionCore;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;

public class CrazyPlugin extends ICrazyEnchantments {

    private final FusionCore fusion;
    private final Path dataPath;

    public CrazyPlugin(@NotNull final FusionCore fusion) {
        this.dataPath = fusion.getDataPath();
        this.fusion = fusion;
    }

    @Override
    public @NotNull final FusionCore getFusion() {
        return this.fusion;
    }

    @Override
    public @NotNull final Path getDataPath() {
        return this.dataPath;
    }
}