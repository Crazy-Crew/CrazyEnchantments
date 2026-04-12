package com.ryderbelserion.crazyenchantments.paper.enchants.pickaxes.furnace;

import com.ryderbelserion.crazyenchantments.paper.CrazyPlugin;
import com.ryderbelserion.crazyenchantments.paper.api.registry.enchants.EnchantmentRegistry;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import org.bukkit.Registry;
import org.bukkit.Server;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;

public class FurnaceListener implements Listener {

    private final Registry<@NotNull Enchantment> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
    private final Key key = FurnaceEnchant.furnace_key;
    private final Enchantment furnace = this.registry.get(this.key);

    private final EnchantmentRegistry enchantmentRegistry;
    private final Server server;

    public FurnaceListener(@NotNull final CrazyPlugin plugin, @NotNull final EnchantmentRegistry enchantmentRegistry) {
        this.enchantmentRegistry = enchantmentRegistry;

        this.server = plugin.getServer();
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDrop(@NotNull final BlockDropItemEvent event) {
        final List<Item> drops = event.getItems();

        if (drops.isEmpty()) return;

        final Player player = event.getPlayer();
        final ItemStack tool = player.getInventory().getItemInMainHand();

        if (this.furnace == null) {
            return;
        }

        if (!tool.containsEnchantment(this.furnace)) {
            return;
        }

        final FurnaceEnchant enchant = (FurnaceEnchant) this.enchantmentRegistry.getEnchantment(this.key);

        if (!enchant.isEnabled()) {
            return;
        }

        final int size = drops.size();

        for (int position = 0; position < size; position++) {
            final Item item = drops.get(position);

            ItemStack itemStack = item.getItemStack();

            if (itemStack.isEmpty()) {
                continue;
            }

            final Optional<RecipeHolder<SmeltingRecipe>> optional = recipe(itemStack);

            // check if it can be smelted, if not continue.
            if (optional.isEmpty()) {
                continue;
            }

            final RecipeHolder<SmeltingRecipe> holder = optional.get();

            final Recipe recipe = holder.toBukkitRecipe();

            final ItemStack result = recipe.getResult();

            item.setItemStack(result);

            event.getItems().set(position, item);
        }
    }

    public Optional<RecipeHolder<SmeltingRecipe>> recipe(@NotNull final ItemStack itemStack) {
        final ServerLevel world = ((org.bukkit.craftbukkit.CraftWorld) this.server.getWorlds().getFirst()).getHandle();

        return world.recipeAccess().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(CraftItemStack.asNMSCopy(itemStack)), world);
    }
}