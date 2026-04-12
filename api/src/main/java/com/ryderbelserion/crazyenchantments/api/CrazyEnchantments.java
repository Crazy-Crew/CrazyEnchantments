package com.ryderbelserion.crazyenchantments.api;

import com.ryderbelserion.crazyenchantments.api.adapters.IPlayerAdapter;
import com.ryderbelserion.crazyenchantments.api.interfaces.registry.IContextRegistry;
import com.ryderbelserion.crazyenchantments.api.interfaces.registry.IMessageRegistry;
import com.ryderbelserion.crazyenchantments.api.interfaces.registry.IUserRegistry;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;

public abstract class CrazyEnchantments<S> {

    public static final String namespace = "corecraft";

    protected final FusionKyori<S> fusion;
    protected final FileManager fileManager;

    protected final Path dataPath;

    public CrazyEnchantments(@NotNull final FusionKyori<S> fusion) {
        this.fusion = fusion;

        this.dataPath = this.fusion.getDataPath();

        this.fileManager = this.fusion.getFileManager();
    }

    public abstract <C> @NotNull IPlayerAdapter<C> getPlayerAdapter(@NotNull final Class<C> object);

    public abstract IMessageRegistry getMessageRegistry();

    public abstract IContextRegistry getContextRegistry();

    public abstract IUserRegistry getUserRegistry();

    public abstract void registerCommands();

    public abstract void init();

    public abstract void reload();

    public abstract void post();

    public void shutdown() {

    }

    public final FileManager getFileManager() {
        return this.fileManager;
    }

    public final FusionKyori getFusion() {
        return this.fusion;
    }

    public final Path getDataPath() {
        return this.dataPath;
    }

    public static class Provider {

        private static CrazyEnchantments instance;

        @ApiStatus.Internal
        private Provider() {
            throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
        }

        public static CrazyEnchantments getInstance() {
            return instance;
        }

        @ApiStatus.Internal
        public static void register(@NotNull final CrazyEnchantments instance) {
            Provider.instance = instance;
        }

        @ApiStatus.Internal
        public static void unregister() {
            Provider.instance = null;
        }
    }
}