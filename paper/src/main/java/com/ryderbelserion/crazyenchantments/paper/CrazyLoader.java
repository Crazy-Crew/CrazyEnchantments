package com.ryderbelserion.crazyenchantments.paper;

import com.ryderbelserion.crazyenchantments.paper.api.registry.enchants.EnchantmentRegistry;
import com.ryderbelserion.crazyenchantments.paper.api.interfaces.ICustomEnchantment;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.paper.FusionPaper;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.event.WritableRegistry;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.tag.PreFlattenTagRegistrar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.Set;

public class CrazyLoader implements PluginBootstrap {

    private EnchantmentRegistry enchantmentRegistry;
    private FusionPaper fusion;

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        this.fusion = new FusionPaper(context);

        this.enchantmentRegistry = new EnchantmentRegistry(this.fusion);
        this.enchantmentRegistry.init();

        final Collection<ICustomEnchantment> enchants = this.enchantmentRegistry.getEnchantments().values();

        final LifecycleEventManager<@NotNull BootstrapContext> lifeCycleManager = context.getLifecycleManager();

        lifeCycleManager.registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ITEM).newHandler((event) -> {
            final PreFlattenTagRegistrar<@NotNull ItemType> registry = event.registrar();

            for (final ICustomEnchantment enchant : enchants) {
                if (!enchant.isEnabled()) continue;

                this.fusion.log(Level.INFO, "Registering item tag %s for %s", enchant.getTagForSupportedItems().key(), enchant.getKey());

                registry.addToTag(ItemTypeTagKeys.create(enchant.getTagForSupportedItems().key()), enchant.getSupportedItems());
            }
        }));

        lifeCycleManager.registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
            final WritableRegistry<@NotNull Enchantment, EnchantmentRegistryEntry.@NotNull Builder> registry = event.registry();

            for (final ICustomEnchantment enchant : enchants) {
                if (!enchant.isEnabled()) continue;

                if (enchant.isCurse()) {
                    this.fusion.log(Level.INFO, "Registering curse %s", enchant.getKey());
                } else {
                    this.fusion.log(Level.INFO, "Registering enchantment %s", enchant.getKey());
                }

                registry.register(TypedKey.create(RegistryKey.ENCHANTMENT, enchant.getKey()), enchantment -> {
                    enchantment.description(enchant.getName());
                    enchantment.anvilCost(enchant.getAnvilCost());
                    enchantment.maxLevel(enchant.getMaxLevel());
                    enchantment.weight(enchant.getWeight());
                    enchantment.minimumCost(enchant.getMinimumCost());
                    enchantment.maximumCost(enchant.getMaximumCost());
                    enchantment.activeSlots(enchant.getActiveSlots());
                    enchantment.supportedItems(event.getOrCreateTag(enchant.getTagForSupportedItems()));
                });
            }
        }));

        lifeCycleManager.registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ENCHANTMENT).newHandler((event) -> {
            final PreFlattenTagRegistrar<@NotNull Enchantment> registry = event.registrar();

            for (final ICustomEnchantment enchant : enchants) {
                if (!enchant.isEnabled()) continue;

                enchant.getEnchantTagKeys().forEach(enchantmentTagKey -> {
                    this.fusion.log(Level.INFO, "Registering the enchantment tag %s for %s", enchantmentTagKey.key(), enchant.getKey());

                    registry.addToTag(enchantmentTagKey, Set.of(enchant.getTagEntry()));
                });
            }
        }));
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new CrazyPlugin(this.enchantmentRegistry, this.fusion);
    }
}