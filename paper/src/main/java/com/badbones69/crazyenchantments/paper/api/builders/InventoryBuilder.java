package com.badbones69.crazyenchantments.paper.api.builders;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("ALL")
public abstract class InventoryBuilder implements InventoryHolder {

    @NotNull
    protected final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    protected final Server server = this.plugin.getServer();

    private final Inventory inventory;
    private final Player player;
    private String title;
    private int size;
    private int page;

    private EnchantmentType enchantmentType;

    private GKitz kit;

    public InventoryBuilder(@NotNull final Player player, int size, @NotNull final String title) {
        this.title = title;
        this.size = size;

        this.player = player;

        this.kit = null;

        this.inventory = this.server.createInventory(this, this.size, ColorUtils.legacyTranslateColourCodes(title));
    }

    public InventoryBuilder(@NotNull final Player player, final int size, @NotNull final String title, @NotNull final GKitz kit) {
        this.title = title;
        this.size = size;

        this.player = player;

        this.kit = kit;

        this.inventory = this.server.createInventory(this, this.size, ColorUtils.legacyTranslateColourCodes(title));
    }

    public abstract InventoryBuilder build();

    public InventoryBuilder setEnchantmentType(@NotNull final EnchantmentType enchantmentType) {
        this.enchantmentType = enchantmentType;

        return this;
    }

    public EnchantmentType getEnchantmentType() {
        return this.enchantmentType;
    }

    public GKitz getKit() {
        return this.kit;
    }

    public void size(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return this.page;
    }

    public void title(@NotNull final String title) {
        this.title = title;
    }

    public boolean contains(@NotNull final String message) {
        return this.title.contains(message);
    }

    public Player getPlayer() {
        return this.player;
    }

    public InventoryView getInventoryView() {
        return getPlayer().getOpenInventory();
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return this.inventory;
    }
}