package me.BadBones69.CrazyEnchantments.MultiSupport;

import org.bukkit.inventory.ItemStack;

public class Nine {
	public static ItemStack addGlow(ItemStack item){
		if(item.hasItemMeta()){
			if(item.getItemMeta().hasEnchants())return item;
		}
		net.minecraft.server.v1_9_R1.ItemStack nmsStack = org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack.asNMSCopy(item);
        net.minecraft.server.v1_9_R1.NBTTagCompound tag = null;
        if (!nmsStack.hasTag()) {
            tag = new net.minecraft.server.v1_9_R1.NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null){
        	tag = nmsStack.getTag();
        }
        net.minecraft.server.v1_9_R1.NBTTagList ench = new net.minecraft.server.v1_9_R1.NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack.asCraftMirror(nmsStack);
	}
}