package com.badbones69.crazyenchantments.paper.api.objects.enchants;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.api.builders.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.ArrayList;
import java.util.List;

public class EnchantmentType {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Methods methods = this.plugin.getStarter().getMethods();

    private final String displayName;
    private final int slot;
    private final ItemStack displayItem;
    private final List<CEnchantment> enchantments = new ArrayList<>();
    private final List<Material> enchantableMaterials = new ArrayList<>();

    public EnchantmentType(String name, CommentedConfigurationNode configuration) {
        this.displayName = name;
        this.slot = configuration.node("Display-Item", "Slot").getInt(1)-1;

        this.displayItem = new ItemBuilder()
                .setMaterial(configuration.node("Display-Item", "Item").getString("Stone"))
                .setItemModel(configuration.node("Display-Item", "Model", "Namespace").getString(""), configuration.node("Display-Item", "Model", "Key").getString(""))
                .setName(configuration.node("Display-Item", "Name").getString("&cError getting name for %s".formatted(name)))
                .setLore(StringUtils.getStringList(configuration.node("Display-Item", "Lore")))
                .build();

        for (final String type : StringUtils.getStringList(configuration.node("Enchantable-Items"))) {
            final Material material = new ItemBuilder().setMaterial(type).getMaterial();

            if (material != null) {
                this.enchantableMaterials.add(material);
            }
        }
    }

    public EnchantmentType getFromName(String name) {
        return this.methods.getFromName(name);
    }

    public String getName() {
        return this.displayName;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    public List<Material> getEnchantableMaterials() {
        return this.enchantableMaterials;
    }

    /**
     * Checks if this cEnchantment may be applied to the given {@link
     * ItemStack}.
     *
     * @param item Item to test
     * @return True if the cEnchantment may be applied, otherwise False
     */
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return this.enchantableMaterials.contains(item.getType());
    }

    public List<CEnchantment> getEnchantments() {
        return this.enchantments;
    }

    public void addEnchantment(CEnchantment enchantment) {
        this.enchantments.add(enchantment);
    }

    public void removeEnchantment(CEnchantment enchantment) {
        this.enchantments.remove(enchantment);
    }
}