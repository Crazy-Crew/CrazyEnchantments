package com.badbones69.crazyenchantments.paper.api.support.misc;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.mechanics.provided.gameplay.durability.DurabilityMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.durability.DurabilityMechanicFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class OraxenSupport {

    public int getMaxDurability(ItemStack itemStack) {
        String itemId = OraxenItems.getIdByItem(itemStack);
        DurabilityMechanicFactory durabilityFactory = DurabilityMechanicFactory.get();

        if (!durabilityFactory.isNotImplementedIn(itemId)) {
            DurabilityMechanic durabilityMechanic = durabilityFactory.getMechanic(itemId);
            return durabilityMechanic.getItemMaxDurability();
        }

        return itemStack.getType().getMaxDurability();
    }

    public int getDamage(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (!(itemMeta instanceof Damageable damageable)) return 0;

        String itemId = OraxenItems.getIdByItem(itemStack);
        DurabilityMechanicFactory durabilityFactory = DurabilityMechanicFactory.get();

        if (!durabilityFactory.isNotImplementedIn(itemId)) {
            DurabilityMechanic durabilityMechanic = durabilityFactory.getMechanic(itemId);
            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            Integer currentDurability = persistentDataContainer.get(DurabilityMechanic.DURABILITY_KEY, PersistentDataType.INTEGER);

            if (currentDurability == null) return damageable.getDamage();

            int realMaxDurability = durabilityMechanic.getItemMaxDurability();
            return realMaxDurability - currentDurability;
        }

        return damageable.getDamage();
    }

    public void setDamage(ItemStack itemStack, int newDamage) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof Damageable damageable)) return;

        String itemId = OraxenItems.getIdByItem(itemStack);
        DurabilityMechanicFactory durabilityFactory = DurabilityMechanicFactory.get();

        if (durabilityFactory.isNotImplementedIn(itemId)) {
            damageable.setDamage(newDamage);
        } else {
            DurabilityMechanic durabilityMechanic = durabilityFactory.getMechanic(itemId);
            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            int realMaxDurability = durabilityMechanic.getItemMaxDurability();
            int newDurability = realMaxDurability - newDamage;
            persistentDataContainer.set(DurabilityMechanic.DURABILITY_KEY, PersistentDataType.INTEGER, newDurability);
            int typeMaxDurability = itemStack.getType().getMaxDurability();
            damageable.setDamage(typeMaxDurability - (int) (((double) newDurability / realMaxDurability) * typeMaxDurability));
        }

        itemStack.setItemMeta(damageable);
    }
}