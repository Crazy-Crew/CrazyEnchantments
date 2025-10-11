package com.badbones69.crazyenchantments.paper.api.builders.gui.types;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.builders.gui.InventoryBuilder;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.managers.KitsManager;
import com.badbones69.crazyenchantments.paper.managers.items.ItemManager;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.gui.interfaces.Gui;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class StaticInventory extends InventoryBuilder {

    protected final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    protected final ItemManager itemManager = this.plugin.getItemManager();

    protected final KitsManager kitsManager = this.plugin.getKitsManager();

    protected final CrazyInstance instance = this.plugin.getInstance();

    protected final FusionPaper fusion = this.plugin.getFusion();

    protected final Server server = this.plugin.getServer();

    private final Player player;
    private final int size;
    private final Gui gui;

    public StaticInventory(@NotNull final Player player, @NotNull final String title, final int size) {
        super(player);

        this.gui = Gui.gui(this.plugin).setTitle(this.fusion.papi(this.player = player, title)).setRows(size / 9).disableInteractions().create();
        this.size = size;
    }

    protected EnchantmentType enchantmentType;

    public abstract void open();

    public StaticInventory setEnchantmentType(@NotNull final EnchantmentType enchantmentType) {
        this.enchantmentType = enchantmentType;

        return this;
    }

    public final boolean contains(@NotNull final String message) {
        return getTitle().contains(message);
    }

    public @NotNull final Player getPlayer() {
        return this.player;
    }

    public @NotNull final String getTitle() {
        return this.gui.getTitle();
    }

    public @NotNull final Gui getGui() {
        return this.gui;
    }

    public final int getSize() {
        return this.size;
    }
}