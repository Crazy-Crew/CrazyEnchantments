package me.badbones69.crazyenchantments.multisupport;

import io.th0rgal.oraxen.items.OraxenItems;
import io.th0rgal.oraxen.mechanics.provided.gameplay.durability.DurabilityMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.durability.DurabilityMechanicFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class OraxenSupport {  // Oraxen is only supported for 1.16+, no need to support older versions here
    public static int getMaxDurability(ItemStack itemStack) {
        String itemId = OraxenItems.getIdByItem(itemStack);
        DurabilityMechanicFactory durabilityFactory = DurabilityMechanicFactory.get();

        if (!durabilityFactory.isNotImplementedIn(itemId)) {
            DurabilityMechanic durabilityMechanic = (DurabilityMechanic) durabilityFactory.getMechanic(itemId);
            return durabilityMechanic.getItemMaxDurability();
        }

        return itemStack.getType().getMaxDurability();
    }

    public static int getDamage(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof Damageable)) {
            return 0;
        }

        String itemId = OraxenItems.getIdByItem(itemStack);
        Damageable damageable = (Damageable) itemMeta;
        DurabilityMechanicFactory durabilityFactory = DurabilityMechanicFactory.get();

        if (!durabilityFactory.isNotImplementedIn(itemId)) {
            DurabilityMechanic durabilityMechanic = (DurabilityMechanic) durabilityFactory.getMechanic(itemId);
            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            Integer currentDurability = persistentDataContainer.get(DurabilityMechanic.NAMESPACED_KEY,
                                                                    PersistentDataType.INTEGER);
            if (currentDurability == null) {
                return damageable.getDamage();
            }
            int realMaxDurability = durabilityMechanic.getItemMaxDurability();
            return realMaxDurability - currentDurability;
        }

        return damageable.getDamage();
    }

    public static void setDamage(ItemStack itemStack, int newDamage) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof Damageable)) {
            return;
        }

        String itemId = OraxenItems.getIdByItem(itemStack);
        Damageable damageable = (Damageable) itemMeta;
        DurabilityMechanicFactory durabilityFactory = DurabilityMechanicFactory.get();

        if (durabilityFactory.isNotImplementedIn(itemId)) {
            damageable.setDamage(newDamage);
        } else {
            DurabilityMechanic durabilityMechanic = (DurabilityMechanic) durabilityFactory.getMechanic(itemId);
            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            int realMaxDurability = durabilityMechanic.getItemMaxDurability();
            persistentDataContainer.set(DurabilityMechanic.NAMESPACED_KEY,
                                        PersistentDataType.INTEGER,
                                     realMaxDurability - newDamage);
            damageable.setDamage(0);
        }

        itemStack.setItemMeta((ItemMeta) damageable);
    }
}
