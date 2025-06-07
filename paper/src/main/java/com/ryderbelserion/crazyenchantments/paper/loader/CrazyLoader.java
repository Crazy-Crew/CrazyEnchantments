package com.ryderbelserion.crazyenchantments.paper.loader;

import com.ryderbelserion.crazyenchantments.paper.CrazyEnchantments;
import com.ryderbelserion.crazyenchantments.paper.enchants.EnchantmentRegistry;
import com.ryderbelserion.crazyenchantments.paper.enchants.interfaces.CustomEnchantment;
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
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

public class CrazyLoader implements PluginBootstrap {

    private EnchantmentRegistry registry;
    private FusionPaper paper;

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {
        final ComponentLogger logger = context.getLogger();
        final Path path = context.getDataDirectory();

        this.paper = new FusionPaper(logger, path);

        this.registry = new EnchantmentRegistry(this.paper, logger, path);
        this.registry.init();

        final Collection<CustomEnchantment> enchants = this.registry.getEnchantments().values();

        final LifecycleEventManager<@NotNull BootstrapContext> lifeCycleManager = context.getLifecycleManager();

        lifeCycleManager.registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ITEM).newHandler((event) -> {
            final PreFlattenTagRegistrar<ItemType> registry = event.registrar();

            for (final CustomEnchantment enchant : enchants) {
                if (!enchant.isEnabled()) continue;

                logger.info("Registering item tag {}", enchant.getTagForSupportedItems().key());

                registry.addToTag(ItemTypeTagKeys.create(enchant.getTagForSupportedItems().key()), enchant.getSupportedItems());
            }
        }));

        lifeCycleManager.registerEventHandler(RegistryEvents.ENCHANTMENT.freeze().newHandler(event -> {
            final WritableRegistry<Enchantment, EnchantmentRegistryEntry.@NotNull Builder> registry = event.registry();

            for (final CustomEnchantment enchant : enchants) {
                if (!enchant.isEnabled()) continue;

                if (enchant.isCurse()) {
                    logger.info("Registering curse {}", enchant.getKey());
                } else {
                    logger.info("Registering enchantment {}", enchant.getKey());
                }

                registry.register(TypedKey.create(RegistryKey.ENCHANTMENT, enchant.getKey()), enchantment -> {
                    enchantment.description(enchant.getDescription());
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
            final PreFlattenTagRegistrar<Enchantment> registry = event.registrar();

            for (final CustomEnchantment enchant : enchants) {
                if (!enchant.isEnabled()) continue;

                enchant.getEnchantTagKeys().forEach(enchantmentTagKey -> {
                    logger.info("Registering the enchantment tag {}", enchantmentTagKey.key());

                    registry.addToTag(enchantmentTagKey, Set.of(enchant.getTagEntry()));
                });
            }
        }));
    }

    @Override
    public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return new CrazyEnchantments(this.registry, this.paper);
    }
}