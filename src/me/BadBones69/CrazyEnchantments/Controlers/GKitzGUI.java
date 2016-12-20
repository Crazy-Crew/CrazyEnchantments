package me.BadBones69.CrazyEnchantments.Controlers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.BadBones69.CrazyEnchantments.Main;
import me.BadBones69.CrazyEnchantments.Methods;
import me.BadBones69.CrazyEnchantments.API.GKitz;
import me.BadBones69.CrazyEnchantments.API.Version;

public class GKitzGUI implements Listener{
	
	public static void openGUI(Player player){
		FileConfiguration gkitz = Main.settings.getGKitz();
		Inventory inv = Bukkit.createInventory(null, gkitz.getInt("Settings.GUI-Size"), Methods.color(gkitz.getString("Settings.Inventory-Name")));
		if(gkitz.contains("Settings.GUI-Customization")){
			for(String custom : gkitz.getStringList("Settings.GUI-Customization")){
				String name = "";
				String item = "1";
				int slot = 0;
				ArrayList<String> lore = new ArrayList<String>();
				String[] b = custom.split(", ");
				for(String i : b){
					if(i.contains("Item:")){
						i=i.replace("Item:", "");
						item=i;
					}
					if(i.contains("Name:")){
						i=i.replace("Name:", "");
						name=i;
					}
					if(i.contains("Slot:")){
						i=i.replace("Slot:", "");
						slot=Integer.parseInt(i);
					}
					if(i.contains("Lore:")){
						i=i.replace("Lore:", "");
						String[] d = i.split(",");
						for(String l : d){
							lore.add(l);
						}
					}
				}
				slot--;
				inv.setItem(slot, Methods.makeItem(item, 1, name, lore));
			}
		}
		for(String kit : GKitz.getGKitz()){
			int slot = gkitz.getInt("GKitz." + kit + ".Display.Slot") - 1;
			String id = gkitz.getString("GKitz." + kit + ".Display.Item");
			String name = gkitz.getString("GKitz." + kit + ".Display.Name");
			List<String> lore = new ArrayList<String>();
			for(String l : gkitz.getStringList("GKitz." + kit + ".Display.Lore")){
				if(GKitz.canGetGKit(player, kit)){
					lore.add(GKitz.getCooldownLeft(Calendar.getInstance(), l));
				}else{
					lore.add(GKitz.getCooldownLeft(GKitz.getCooldown(player, kit), l));
				}
			}
			inv.setItem(slot, Methods.makeItem(id, 1, name, lore));
		}
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		if(inv != null){
			FileConfiguration gkitz = Main.settings.getGKitz();
			FileConfiguration msg = Main.settings.getMsg();
			Player player = (Player) e.getWhoClicked();
			ItemStack item = e.getCurrentItem();
			if(inv.getName().equals(Methods.color(gkitz.getString("Settings.Inventory-Name")))){
				e.setCancelled(true);
				if(e.getRawSlot() < inv.getSize()){
					if(item != null){
						if(item.hasItemMeta()){
							if(item.getItemMeta().hasDisplayName()){
								String name = item.getItemMeta().getDisplayName();
								for(String kit : GKitz.getGKitz()){
									if(name.equals(GKitz.getGKitDisplayName(kit))){
										if(e.getAction() == InventoryAction.PICKUP_HALF){
											ArrayList<ItemStack> items = GKitz.getInfoGKit(kit);
											int slots = 9;
											for(int size = items.size();size >= 9;size -= 9){
												slots+=9;
											}
											Inventory in = Bukkit.createInventory(null, slots, GKitz.getGKitDisplayName(kit));
											for(ItemStack it : items){
												in.addItem(it);
											}
											if(Version.getVersion().getVersionInteger()<181){
												in.setItem(slots-1, Methods.makeItem(Material.FEATHER, 1, 0, msg.getString("Messages.InfoGUI.Categories-Info.Back.Right")));
											}else{
												in.setItem(slots-1, Methods.makeItem(Material.PRISMARINE_CRYSTALS, 1, 0, msg.getString("Messages.InfoGUI.Categories-Info.Back.Right")));
											}
											player.openInventory(in);
										}else{
											if(GKitz.hasGKitPermission(player, kit)){
												if(GKitz.canGetGKit(player, kit)){
													GKitz.giveKit(player, kit);
													GKitz.addCooldown(player, kit);
													GKitz.runKitCommands(player, kit);
													player.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.Received-GKit")
															.replaceAll("%Kit%", GKitz.getGKitDisplayName(kit)).replaceAll("%kit%", GKitz.getGKitDisplayName(kit))));
													return;
												}else{
													player.sendMessage(Methods.getPrefix() + GKitz.getCooldownLeft(GKitz.getCooldown(player, kit), msg.getString("Messages.Still-In-Cooldown")
															.replaceAll("%Kit%", GKitz.getGKitDisplayName(kit)).replaceAll("%kit%", GKitz.getGKitDisplayName(kit))));
													return;
												}
											}else{
												player.sendMessage(Methods.getPrefix() + Methods.color(msg.getString("Messages.No-GKit-Permission")
														.replaceAll("%Kit%", kit).replaceAll("%kit%", kit)));
												return;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			for(String kit : GKitz.getGKitz()){
				if(inv.getName().equals(Methods.color(GKitz.getGKitDisplayName(kit)))){
					e.setCancelled(true);
					if(e.getRawSlot() < inv.getSize()){
						if(item != null){
							if(item.hasItemMeta()){
								if(item.getItemMeta().hasDisplayName()){
									String name = item.getItemMeta().getDisplayName();
									if(name.equals(Methods.color(msg.getString("Messages.InfoGUI.Categories-Info.Back.Right")))){
										openGUI(player);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
}