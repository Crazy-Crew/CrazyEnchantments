package com.badbones69.crazyenchantments.paper.utilities.misc;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Random;

public class ItemUtils {

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private static final Methods methods = plugin.getStarter().getMethods();

    public static void giveCropDrops(Player player, Block crop) {
        Random random = new Random();
        switch (crop.getType()) {
            case COCOA -> methods.addItemToInventory(player, new ItemStack(Material.COCOA_BEANS, random.nextInt(2) + 2)); // Coco drops 2-3 beans.

            case WHEAT -> {
                methods.addItemToInventory(player, new ItemStack(Material.WHEAT));
                int amount = random.nextInt(3);
                if (amount > 0) methods.addItemToInventory(player, new ItemStack(Material.WHEAT_SEEDS, amount)); // Wheat drops 0-3 seeds.
            }

            case BEETROOTS -> {
                methods.addItemToInventory(player, new ItemStack(Material.BEETROOT));
                int amount = random.nextInt(3);
                if (amount > 0) methods.addItemToInventory(player, new ItemStack(Material.BEETROOT_SEEDS, amount)); // BeetRoots drops 0-3 seeds.
            }

            case POTATO -> methods.addItemToInventory(player, new ItemStack(Material.POTATO, random.nextInt(4) + 1)); // Potatoes drop 1-4 of them self's.
            case CARROTS -> methods.addItemToInventory(player, new ItemStack(Material.CARROT, random.nextInt(4) + 1)); // Carrots drop 1-4 of them self's.
            case NETHER_WART -> methods.addItemToInventory(player, new ItemStack(Material.NETHER_WART, random.nextInt(3) + 2)); // Nether Warts drop 2-4 of them self's.
        }
    }
}