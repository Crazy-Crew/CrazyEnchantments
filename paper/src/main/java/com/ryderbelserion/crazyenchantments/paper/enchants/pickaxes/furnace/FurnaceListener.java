package com.ryderbelserion.crazyenchantments.paper.enchants.pickaxes.furnace;

import com.ryderbelserion.crazyenchantments.paper.CrazyPlugin;
import com.ryderbelserion.crazyenchantments.paper.api.CrazyEnchantmentsPaper;
import com.ryderbelserion.crazyenchantments.paper.api.registry.enchants.EnchantmentRegistry;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.minecraft.world.item.crafting.RecipePropertySet;
import org.bukkit.Registry;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class FurnaceListener implements Listener {

    private final Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    private final Key key = FurnaceEnchant.furnace_key;
    private final Enchantment autosmelt = this.registry.get(this.key);

    private final EnchantmentRegistry enchantmentRegistry;
    private final boolean isYardWatchEnabled;
    private final CrazyPlugin plugin;
    private final Server server;

    public FurnaceListener(@NotNull final CrazyPlugin plugin, @NotNull final EnchantmentRegistry enchantmentRegistry) {
        this.enchantmentRegistry = enchantmentRegistry;

        final CrazyEnchantmentsPaper platform = plugin.getPlatform();

        this.isYardWatchEnabled = platform.isYardWatchEnabled();
        this.server = plugin.getServer();
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(@NotNull final BlockDropItemEvent event) {
        final List<Item> drops = event.getItems();

        if (drops.isEmpty()) return;

        final int size = drops.size();

        for (int position = 0; position < size; position++) {
            final Item item = drops.get(position);

            final ItemStack itemStack = item.getItemStack();

            // check if it can be smelted, if not continue.
            if (!canSmelt(itemStack)) {
                continue;
            }

            item.setItemStack(itemStack);

            event.getItems().set(position, item);
        }
    }

    public boolean canSmelt(@NotNull final ItemStack itemStack) {
        boolean canSmelt = false;

        final Iterator<Recipe> recipes = this.server.recipeIterator();

        while (recipes.hasNext()) {
            final Recipe recipe = recipes.next();

            if (!(recipe instanceof FurnaceRecipe furnace)) {
                continue;
            }

            final RecipeChoice choice = furnace.getInputChoice();

            canSmelt = choice.test(itemStack);

            if (canSmelt) {
                break;
            }
        }

        return canSmelt;
    }
}