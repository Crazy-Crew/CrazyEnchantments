package me.BadBones69.CrazyEnchantments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DustControl implements Listener{
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		if(inv!=null){
			if(e.getCurrentItem()!=null){
				if(e.getCursor()!=null){
					ItemStack book = e.getCurrentItem();
					ItemStack dust = e.getCursor();
					if(book.getAmount()!=1||dust.getAmount()!=1)return;
					if(book.hasItemMeta()&&dust.hasItemMeta()){
						if(book.getItemMeta().hasLore()&&dust.getItemMeta().hasLore()){
							if(book.getItemMeta().hasDisplayName()&&dust.getItemMeta().hasDisplayName()){
								if(book.getType()==Material.BOOK){
									if(dust.getItemMeta().getDisplayName().equals(Api.color(Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Name")))){
										if(dust.getType()==Api.makeItem(Main.settings.getConfig().getString("Settings.Dust.SuccessDust.Item"), 1, "", Arrays.asList("")).getType()){
											int per = getPercent("SuccessDust", dust);
											List<String> newlore = new ArrayList<String>();
											for(String lore : book.getItemMeta().getLore()){
												if(lore.contains("% Success Chance")){
													lore=lore.replaceAll("% Success Chance", "");
													lore=Api.removeColor(lore);
													int total = Integer.parseInt(lore);
													if(total>=100)return;
													per = per+total;
													if(per<0)per=0;
													if(per>100)per=100;
													lore=Api.color("&a"+per+"% Success Chance");
												}
												newlore.add(lore);
											}
											e.setCancelled(true);
											e.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
											ItemMeta m = book.getItemMeta();
											m.setLore(newlore);
											book.setItemMeta(m);
											e.setCurrentItem(book);
											return;
										}
									}
									if(dust.getItemMeta().getDisplayName().equals(Api.color(Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Name")))){
										if(dust.getType()==Api.makeItem(Main.settings.getConfig().getString("Settings.Dust.DestroyDust.Item"), 1, "", Arrays.asList("")).getType()){
											int per = getPercent("DestroyDust", dust);
											List<String> newlore = new ArrayList<String>();
											for(String lore : book.getItemMeta().getLore()){
												if(lore.contains("% Destroy Chance")){
													lore=lore.replaceAll("% Destroy Chance", "");
													lore=Api.removeColor(lore);
													int total = Integer.parseInt(lore);
													if(total<=0)return;
													per = total-per;
													if(per<0)per=0;
													if(per>100)per=100;
													lore=Api.color("&4"+per+"% Destroy Chance");
												}
												newlore.add(lore);
											}
											e.setCancelled(true);
											e.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
											ItemMeta m = book.getItemMeta();
											m.setLore(newlore);
											book.setItemMeta(m);
											e.setCurrentItem(book);
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
	}
	@EventHandler
	public void openDust(PlayerInteractEvent e){
		Player player = e.getPlayer();
		if(e.getAction()==Action.RIGHT_CLICK_AIR||e.getAction()==Action.RIGHT_CLICK_BLOCK){
			if(Api.getItemInHand(player)!=null){
				ItemStack item = Api.getItemInHand(player);
				if(item.hasItemMeta()){
					if(item.getItemMeta().hasDisplayName()&&item.getItemMeta().hasLore()){
						if(item.getType()==Api.makeItem(Main.settings.getConfig().getString("Settings.Dust.MysteryDust.Item"), 1, "", Arrays.asList("")).getType()){
							if(item.getItemMeta().getDisplayName().equals(Api.color(Main.settings.getConfig().getString("Settings.Dust.MysteryDust.Name")))){
								e.setCancelled(true);
								Api.setItemInHand(player, Api.removeItem(item));
								player.getInventory().addItem(getDust(pickDust(), 1, Api.percentPick(getPercent("MysteryDust", item)+1, 1)));
								if(Api.getVersion()>=191){
									player.playSound(player.getLocation(), Sound.valueOf("BLOCK_LAVA_POP"), 1, 1);
								}else{
									player.playSound(player.getLocation(), Sound.valueOf("LAVA_POP"), 1, 1);
								}
								return;
							}
						}
					}
				}
			}
		}
	}
	public static ItemStack getDust(String Dust,int i){
		String id = Main.settings.getConfig().getString("Settings.Dust."+Dust+".Item");
		String name = Main.settings.getConfig().getString("Settings.Dust."+Dust+".Name");
		List<String> lore = new ArrayList<String>();
		int max = Main.settings.getConfig().getInt("Settings.Dust."+Dust+".PercentRange.Max");
		int min = Main.settings.getConfig().getInt("Settings.Dust."+Dust+".PercentRange.Min");
		int percent = Api.percentPick(max, min);
		for(String l : Main.settings.getConfig().getStringList("Settings.Dust."+Dust+".Lore")){
			lore.add(l.replaceAll("%Percent%", percent+"").replaceAll("%percent%", percent+""));
		}
		return Api.makeItem(id, i, name, lore);
	}
	public static ItemStack getDust(String Dust,int i, int percent){
		String id = Main.settings.getConfig().getString("Settings.Dust."+Dust+".Item");
		String name = Main.settings.getConfig().getString("Settings.Dust."+Dust+".Name");
		List<String> lore = new ArrayList<String>();
		for(String l : Main.settings.getConfig().getStringList("Settings.Dust."+Dust+".Lore")){
			lore.add(l.replaceAll("%Percent%", percent+"").replaceAll("%percent%", percent+""));
		}
		return Api.makeItem(id, i, name, lore);
	}
	String pickDust(){
		Random r = new Random();
		int i = r.nextInt(2);
		if(i==0)return "SuccessDust";
		return "DestroyDust";
	}
	public static Integer getPercent(String dust, ItemStack item){
		List<String> lore = item.getItemMeta().getLore();
		List<String> L = Main.settings.getConfig().getStringList("Settings.Dust."+dust+".Lore");
		String arg = "";
		int i = 0;
		for(String l : L){
			l = Api.color(l);
			String lo = lore.get(i);
			if(l.contains("%Percent%")){
				String[] b = l.split("%Percent%");
				if(b.length>=1)arg = lo.replace(b[0], "");
				if(b.length>=2)arg = arg.replace(b[1], "");
			}
			if(l.contains("%percent%")){
				String[] b = l.split("%percent%");
				if(b.length>=1)arg = lo.replace(b[0], "");
				if(b.length>=2)arg = arg.replace(b[1], "");
			}
			i++;
		}
		return Integer.parseInt(arg);
	}
}