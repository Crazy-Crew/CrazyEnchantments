package me.badbones69.crazyenchantments.controlers;

import me.badbones69.crazyenchantments.Main;
import me.badbones69.crazyenchantments.Methods;
import me.badbones69.crazyenchantments.api.CEnchantments;
import me.badbones69.crazyenchantments.api.CustomEBook;
import me.badbones69.crazyenchantments.api.EnchantmentType;
import me.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CustomEnchantments implements Listener {

	private List<String> CustomEnchants = new ArrayList<String>();
	private HashMap<String, List<String>> Discription = new HashMap<String, List<String>>();
	private HashMap<String, String> Name = new HashMap<String, String>();
	private HashMap<String, String> BookColor = new HashMap<String, String>();
	private HashMap<String, String> EnchantmentColor = new HashMap<String, String>();
	private HashMap<String, Boolean> Toggle = new HashMap<String, Boolean>();
	private HashMap<String, EnchantmentType> Type = new HashMap<String, EnchantmentType>();
	private HashMap<Projectile, Integer> Power = new HashMap<Projectile, Integer>();
	private HashMap<Projectile, String> Enchant = new HashMap<Projectile, String>();

	private static CustomEnchantments instance = new CustomEnchantments();

	public static CustomEnchantments getInstance() {
		return instance;
	}

	@EventHandler
	public void onEquip(ArmorEquipEvent e) {
		if(Main.settings.getCustomEnchs().contains("Enchantments")) {
			Player player = e.getPlayer();
			ItemStack NewItem = e.getNewArmorPiece();
			ItemStack OldItem = e.getOldArmorPiece();
			if(hasEnchantments(NewItem)) {
				for(String ench : getEnchantments()) {
					if(hasEnchantment(NewItem, ench)) {
						if(isEnabled(ench)) {
							int power = getPower(NewItem, ench);
							int add = Main.settings.getCustomEnchs().getInt("Enchantments." + ench + ".EnchantOptions.ArmorOptions.PowerIncrease");
							for(String po : Main.settings.getCustomEnchs().getStringList("Enchantments." + ench + ".EnchantOptions.ArmorOptions.PotionEffects")) {
								PotionEffectType potion = PotionEffectType.NIGHT_VISION;
								int amp = 0;
								int time = 55555;
								String[] b = po.split(", ");
								for(String B : b) {
									for(String P : Methods.getPotions()) {
										if(B.toLowerCase().startsWith(P.toLowerCase() + ":")) {
											potion = PotionEffectType.getByName(P);
											amp = Integer.parseInt(B.replaceAll(P + ":", ""));
										}
									}
									if(B.toUpperCase().contains("Time:".toUpperCase())) {
										time = Integer.parseInt(B.replaceAll("Time:", ""));
									}
								}
								if(power == 1) {
									player.addPotionEffect(new PotionEffect(potion, time * 20, amp - 1));
								}else {
									player.addPotionEffect(new PotionEffect(potion, time * 20, amp + ((power - 1) * add) - 1));
								}
							}
						}
					}
				}
			}
			if(hasEnchantments(OldItem)) {
				for(String ench : getEnchantments()) {
					if(hasEnchantment(OldItem, ench)) {
						if(isEnabled(ench)) {
							for(String po : Main.settings.getCustomEnchs().getStringList("Enchantments." + ench + ".EnchantOptions.ArmorOptions.PotionEffects")) {
								PotionEffectType potion = PotionEffectType.NIGHT_VISION;
								String[] b = po.split(", ");
								for(String B : b) {
									for(String P : Methods.getPotions()) {
										if(B.toLowerCase().startsWith(P.toLowerCase() + ":")) {
											potion = PotionEffectType.getByName(P);
										}
									}
								}
								if(player.hasPotionEffect(potion)) {
									player.removePotionEffect(potion);
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(Main.settings.getCustomEnchs().contains("Enchantments")) {
			if(e.isCancelled()) return;
			if(e.getEntity() instanceof LivingEntity) {
				if(e.getDamager() instanceof Player) {
					Player damager = (Player) e.getDamager();
					LivingEntity damaged = (LivingEntity) e.getEntity();
					ItemStack item = Methods.getItemInHand(damager);
					if(!e.getEntity().isDead()) {
						if(!Support.allowsPVP(e.getEntity().getLocation())) return;
						if(hasEnchantments(item)) {
							for(String ench : getEnchantments()) {
								if(hasEnchantment(item, ench)) {
									if(isEnabled(ench)) {
										//Damager Potion Control
										if(Main.settings.getCustomEnchs().contains("Enchantments." + ench + ".EnchantOptions.WeaponOptions.Damager.PowerIncrease")) {
											if(Main.settings.getCustomEnchs().contains("Enchantments." + ench + ".EnchantOptions.WeaponOptions.Damager.PotionEffects")) {
												int power = getPower(Methods.getItemInHand(damager), ench);
												int add = Main.settings.getCustomEnchs().getInt("Enchantments." + ench + ".EnchantOptions.WeaponOptions.Damager.PowerIncrease");
												for(String po : Main.settings.getCustomEnchs().getStringList("Enchantments." + ench + ".EnchantOptions.WeaponOptions.Damager.PotionEffects")) {
													PotionEffectType potion = PotionEffectType.NIGHT_VISION;
													int amp = 0;
													int time = 55555;
													int cha = 100;
													String[] b = po.split(", ");
													for(String B : b) {
														for(String P : Methods.getPotions()) {
															if(B.toLowerCase().startsWith(P.toLowerCase() + ":")) {
																potion = PotionEffectType.getByName(P);
																amp = Integer.parseInt(B.replaceAll(P + ":", ""));
															}
														}
														if(B.toUpperCase().contains("Time:".toUpperCase())) {
															time = Integer.parseInt(B.replaceAll("Time:", ""));
														}
														if(B.toUpperCase().contains("Chance:".toUpperCase())) {
															cha = Integer.parseInt(B.replaceAll("Chance:", ""));
														}
													}
													Random number = new Random();
													int chance;
													for(int counter = 1; counter <= 1; counter++) {
														chance = 1 + number.nextInt(99);
														if(chance <= cha) {
															damager.addPotionEffect(new PotionEffect(potion, time * 20, amp + (power * add)));
														}
													}
												}
											}
										}
										//Damaged Potion Control
										if(Main.settings.getCustomEnchs().contains("Enchantments." + ench + ".EnchantOptions.WeaponOptions.Damaged.PowerIncrease")) {
											if(Main.settings.getCustomEnchs().contains("Enchantments." + ench + ".EnchantOptions.WeaponOptions.Damaged.PotionEffects")) {
												int power = getPower(Methods.getItemInHand(damager), ench);
												int add = Main.settings.getCustomEnchs().getInt("Enchantments." + ench + ".EnchantOptions.WeaponOptions.Damaged.PowerIncrease");
												for(String po : Main.settings.getCustomEnchs().getStringList("Enchantments." + ench + ".EnchantOptions.WeaponOptions.Damaged.PotionEffects")) {
													PotionEffectType potion = PotionEffectType.NIGHT_VISION;
													int amp = 0;
													int time = 55555;
													int cha = 100;
													String[] b = po.split(", ");
													for(String B : b) {
														for(String P : Methods.getPotions()) {
															if(B.toLowerCase().startsWith(P.toLowerCase() + ":")) {
																potion = PotionEffectType.getByName(P);
																amp = Integer.parseInt(B.replaceAll(P + ":", ""));
															}
														}
														if(B.toUpperCase().contains("Time:".toUpperCase())) {
															time = Integer.parseInt(B.replaceAll("Time:", ""));
														}
														if(B.toUpperCase().contains("Chance:".toUpperCase())) {
															cha = Integer.parseInt(B.replaceAll("Chance:", ""));
														}
													}
													Random number = new Random();
													int chance;
													for(int counter = 1; counter <= 1; counter++) {
														chance = 1 + number.nextInt(99);
														if(chance <= cha) {
															damaged.addPotionEffect(new PotionEffect(potion, time * 20, amp + (power * add)));
														}
													}
												}
											}
										}
										//Damaged Damage Multiplyer Control.
										if(Main.settings.getCustomEnchs().contains("Enchantments." + ench + ".EnchantOptions.WeaponOptions.Damaged.DamageMultiplyer")) {
											Random number = new Random();
											int chance;
											int cha = Main.settings.getCustomEnchs().getInt("Enchantments." + ench + ".EnchantOptions.WeaponOptions.Damaged.DamageMultiplyer.Chance");
											int power = Main.settings.getCustomEnchs().getInt("Enchantments." + ench + ".EnchantOptions.WeaponOptions.Damaged.DamageMultiplyer.PowerIncrease");
											int multi = Main.settings.getCustomEnchs().getInt("Enchantments." + ench + ".EnchantOptions.WeaponOptions.Damaged.DamageMultiplyer.Multiplyer");
											double damage = e.getDamage() * (multi + (getPower(Methods.getItemInHand(damager), ench) + power));
											for(int counter = 1; counter <= 1; counter++) {
												chance = 1 + number.nextInt(99);
												if(chance <= cha) {
													e.setDamage(damage);
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
		}
	}

	@EventHandler
	public void onBowShoot(EntityShootBowEvent e) {
		if(!Support.allowsPVP(e.getEntity().getLocation())) return;
		ItemStack item = e.getBow();
		if(hasEnchantments(item)) {
			for(String ench : CustomEnchants) {
				if(hasEnchantment(item, ench)) {
					if(isEnabled(ench)) {
						Power.put((Projectile) e.getProjectile(), getPower(e.getBow(), ench));
						Enchant.put((Projectile) e.getProjectile(), ench);
					}
				}
			}
		}
	}

	@EventHandler
	public void onland(ProjectileHitEvent e) {
		if(!Main.settings.getCustomEnchs().contains("Enchantments")) return;
		if(!Support.allowsPVP(e.getEntity().getLocation())) return;
		if(!Support.allowsExplotions(e.getEntity().getLocation())) return;
		if(Power.containsKey(e.getEntity())) {
			String ench = Enchant.get(e.getEntity());
			if(Main.settings.getCustomEnchs().contains("Enchantments." + ench + ".EnchantOptions.BowOptions.OnHit.Ground.Explode")) {
				int cha = Main.settings.getCustomEnchs().getInt("Enchantments." + ench + ".EnchantOptions.BowOptions.OnHit.Ground.Explode.Chance");
				int power = Main.settings.getCustomEnchs().getInt("Enchantments." + ench + ".EnchantOptions.BowOptions.OnHit.Ground.Explode.Power");
				int incr = Main.settings.getCustomEnchs().getInt("Enchantments." + ench + ".EnchantOptions.BowOptions.OnHit.Ground.Explode.PowerIncrease");
				Random number = new Random();
				int chance;
				for(int counter = 1; counter <= 1; counter++) {
					chance = 1 + number.nextInt(99);
					if(chance <= cha) {
						if(Power.get(e.getEntity()) == 1) {
							e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), power);
						}else {
							e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), power + ((Power.get(e.getEntity()) - 1) * incr));
						}
						e.getEntity().remove();
						Power.remove(e.getEntity());
						Enchant.remove(e.getEntity());
					}
				}
			}
		}
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if(!Main.settings.getCustomEnchs().contains("Enchantments")) return;
		if(!Support.allowsPVP(e.getDamager().getLocation())) return;
		if(e.getDamager() instanceof Arrow) {
			if(!(e.getEntity() instanceof LivingEntity)) return;
			Projectile arrow = (Projectile) e.getDamager();
			String ench = Enchant.get(arrow);
			if(Power.containsKey(arrow)) {
				if(Main.settings.getCustomEnchs().contains("Enchantments." + ench + ".EnchantOptions.BowOptions.OnHit.Damaged")) {
					int power = Power.get(arrow);
					int add = Main.settings.getCustomEnchs().getInt("Enchantments." + ench + ".EnchantOptions.BowOptions.OnHit.Damaged.PowerIncrease");
					for(String po : Main.settings.getCustomEnchs().getStringList("Enchantments." + ench + ".EnchantOptions.BowOptions.OnHit.Damaged.PotionEffects")) {
						PotionEffectType potion = PotionEffectType.NIGHT_VISION;
						int amp = 0;
						int time = 55555;
						int cha = 100;
						String[] b = po.split(", ");
						for(String B : b) {
							for(String P : Methods.getPotions()) {
								if(B.contains(P)) {
									potion = PotionEffectType.getByName(P);
									amp = Integer.parseInt(B.replaceAll(P.toString() + ":", ""));
								}
							}
							if(B.toUpperCase().contains("Time:".toUpperCase())) {
								time = Integer.parseInt(B.replaceAll("Time:", ""));
							}
							if(B.toUpperCase().contains("Chance:".toUpperCase())) {
								cha = Integer.parseInt(B.replaceAll("Chance:", ""));
							}
						}
						Random number = new Random();
						int chance;
						for(int counter = 1; counter <= 1; counter++) {
							chance = 1 + number.nextInt(99);
							if(chance <= cha) {
								if(power == 1) {
									((LivingEntity) e.getEntity()).addPotionEffect(new PotionEffect(potion, time * 20, amp - 1));
								}else {
									((LivingEntity) e.getEntity()).addPotionEffect(new PotionEffect(potion, time * 20, amp + ((power - 1) * add) - 1));
								}
							}
						}
						Power.remove(arrow);
						Enchant.remove(arrow);
					}
				}
			}
		}
	}

	public List<String> getEnchantments() {
		return instance.CustomEnchants;
	}

	public Boolean isEnchantment(String enchantment) {
		for(String en : getEnchantments()) {
			if(enchantment.equalsIgnoreCase(en) || enchantment.equalsIgnoreCase(getCustomName(en))) {
				return true;
			}
		}
		return false;
	}

	public String getCustomName(String enchantment) {
		return instance.Name.get(enchantment);
	}

	public String getBookColor(String enchantment) {
		return Methods.color(instance.BookColor.get(enchantment));
	}

	public String getEnchantmentColor(String enchantment) {
		return Methods.color(instance.EnchantmentColor.get(enchantment));
	}

	public EnchantmentType getType(String enchantment) {
		return instance.Type.get(enchantment);
	}

	public Boolean isEnabled(String enchantment) {
		return instance.Toggle.get(enchantment);
	}

	public List<String> getDiscription(String enchantment) {
		return instance.Discription.get(enchantment);
	}

	public String getFromName(String name) {
		for(String en : getEnchantments()) {
			if(en.equalsIgnoreCase(name)) {
				return en;
			}
			if(en.equalsIgnoreCase(name)) {
				return en;
			}
		}
		return null;
	}

	public boolean hasEnchantments(ItemStack item) {
		if(item != null) {
			if(item.hasItemMeta()) {
				if(item.getItemMeta().hasLore()) {
					for(String lore : item.getItemMeta().getLore()) {
						for(String enchantment : getEnchantments()) {
							if(lore.contains(getCustomName(enchantment))) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public boolean hasEnchantment(ItemStack item, String enchantment) {
		if(item != null) {
			if(item.hasItemMeta()) {
				if(item.getItemMeta().hasLore()) {
					for(String lore : item.getItemMeta().getLore()) {
						if(lore.contains(getCustomName(enchantment))) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Get the highest category rarity the enchantment is in.
	 * @param enchantment The enchantment you are checking.
	 * @return The highest category based on the rarities.
	 */
	public String getHighestEnchantmentCategory(String enchantment) {
		String top = "";
		int rarity = 0;
		for(String cat : getEnchantmentCategories(enchantment)) {
			if(getCategoryRarity(cat) >= rarity) {
				rarity = getCategoryRarity(cat);
				top = cat;
			}
		}
		return top;
	}

	/**
	 *
	 * @param enchantment The enchantment you want to check.
	 * @return All the categories the enchantment is in.
	 */
	public ArrayList<String> getEnchantmentCategories(String enchantment) {
		ArrayList<String> cats = new ArrayList<String>();
		for(String c : Main.settings.getCustomEnchs().getStringList("Enchantments." + enchantment + ".Categories")) {
			for(String C : Main.settings.getConfig().getConfigurationSection("Categories").getKeys(false)) {
				if(c.equalsIgnoreCase(C)) {
					cats.add(C);
				}
			}
		}
		return cats;
	}

	/**
	 *
	 * @param category The category you want the rarity from.
	 * @return The level of the category's rarity.
	 */
	public Integer getCategoryRarity(String category) {
		int rarity = 0;
		FileConfiguration config = Main.settings.getConfig();
		if(config.contains("Categories." + category)) {
			rarity = config.getInt("Categories." + category + ".Rarity");
		}
		return rarity;
	}

	public ItemStack addEnchantment(ItemStack item, String enchant, Integer level) {
		if(hasEnchantment(item, enchant)) {
			removeEnchantment(item, enchant);
		}
		List<String> newLore = new ArrayList<String>();
		List<String> lores = new ArrayList<String>();
		HashMap<String, String> enchantments = new HashMap<String, String>();
		for(CEnchantments en : Main.CE.getItemEnchantments(item)) {
			enchantments.put(en.getName(), Methods.color(en.getEnchantmentColor() + en.getCustomName() + " " + convertPower(Main.CE.getPower(item, en))));
			Main.CE.removeEnchantment(item, en);
		}
		for(String en : getItemEnchantments(item)) {
			enchantments.put(en, Methods.color(getEnchantmentColor(en) + getCustomName(en) + " " + convertPower(getPower(item, en))));
			removeEnchantment(item, en);
		}
		ItemMeta meta = item.getItemMeta();
		if(meta != null) {
			if(meta.hasLore()) {
				lores.addAll(item.getItemMeta().getLore());
			}
		}
		enchantments.put(enchant, Methods.color(getEnchantmentColor(enchant) + getCustomName(enchant) + " " + convertPower(level)));
		for(String en : enchantments.keySet()) {
			newLore.add(enchantments.get(en));
		}
		newLore.addAll(lores);
		meta.setLore(newLore);
		item.setItemMeta(meta);
		if(Version.getCurrentVersion().isNewer(Version.v1_10_R1)) {
			return item;
		}else {
			return Methods.addGlow(item);
		}
	}

	public ItemStack removeEnchantment(ItemStack item, String enchant) {
		List<String> newLore = new ArrayList<String>();
		ItemMeta meta = item.getItemMeta();
		for(String lore : item.getItemMeta().getLore()) {
			if(!lore.contains(getCustomName(enchant))) {
				newLore.add(lore);
			}
		}
		meta.setLore(newLore);
		item.setItemMeta(meta);
		return item;
	}

	public ArrayList<String> getItemEnchantments(ItemStack item) {
		ArrayList<String> enchantments = new ArrayList<String>();
		if(item != null) {
			if(item.hasItemMeta()) {
				if(item.getItemMeta().hasLore()) {
					for(String lore : item.getItemMeta().getLore()) {
						for(String enchantment : getEnchantments()) {
							if(lore.contains(getCustomName(enchantment))) {
								enchantments.add(enchantment);
							}
						}
					}
				}
			}
		}
		return enchantments;
	}

	/**
	 * Check if an itemstack is a enchantment book.
	 * @param book The item you are checking.
	 * @return True if it is and false if not.
	 */
	public Boolean isEnchantmentBook(ItemStack book) {
		if(book != null) {
			if(book.hasItemMeta()) {
				if(book.getItemMeta().hasDisplayName()) {
					if(book.getType() == Main.CE.getEnchantmentBookItem().getType()) {
						for(String en : getEnchantments()) {
							if(book.getItemMeta().getDisplayName().startsWith(getBookColor(en) + getCustomName(en))) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * This method converts an ItemStack into a CustomEBook.
	 * @param book The ItemStack you are converting.
	 * @return The ItemStack but as a CustomEBook.
	 */
	public CustomEBook convertToCEBook(ItemStack book) {
		CustomEBook ceBook = new CustomEBook(getEnchantmentBookEnchantmnet(book), getBookPower(book, getEnchantmentBookEnchantmnet(book)), book.getAmount());
		ceBook.setSuccessRate(Methods.getPercent("%Success_Rate%", book, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore")));
		ceBook.setDestoryRate(Methods.getPercent("%Destroy_Rate%", book, Main.settings.getConfig().getStringList("Settings.EnchantmentBookLore")));
		ceBook.setGlowing(Main.settings.getConfig().getBoolean("Settings.Enchantment-Book-Glowing"));
		return ceBook;
	}

	/**
	 * Get the enchantment from an enchantment book.
	 * @param book The book you want the enchantment from.
	 * @return The enchantment the book is.
	 */
	public String getEnchantmentBookEnchantmnet(ItemStack book) {
		if(book != null) {
			if(book.hasItemMeta()) {
				if(book.getItemMeta().hasDisplayName()) {
					if(book.getType() == Main.CE.getEnchantmentBookItem().getType()) {
						for(String en : getEnchantments()) {
							if(book.getItemMeta().getDisplayName().startsWith(getBookColor(en) + getCustomName(en))) {
								return en;
							}
						}
					}
				}
			}
		}
		return null;
	}

	public Integer getMaxPower(String enchant) {
		return Main.settings.getCustomEnchs().getInt("Enchantments." + enchant + ".MaxPower");
	}

	public Integer getBookPower(ItemStack book, String enchant) {
		String line = book.getItemMeta().getDisplayName().replace(getCustomName(enchant) + " ", "");
		line = Methods.removeColor(line);
		if(Methods.isInt(line)) return Integer.parseInt(line);
		if(line.equalsIgnoreCase("I")) return 1;
		if(line.equalsIgnoreCase("II")) return 2;
		if(line.equalsIgnoreCase("III")) return 3;
		if(line.equalsIgnoreCase("IV")) return 4;
		if(line.equalsIgnoreCase("V")) return 5;
		if(line.equalsIgnoreCase("VI")) return 6;
		if(line.equalsIgnoreCase("VII")) return 7;
		if(line.equalsIgnoreCase("VIII")) return 8;
		if(line.equalsIgnoreCase("IX")) return 9;
		if(line.equalsIgnoreCase("X")) return 10;
		return 1;
	}

	public Integer getPower(ItemStack item, String enchant) {
		String line = "";
		for(String lore : item.getItemMeta().getLore()) {
			if(lore.contains(getCustomName(enchant))) {
				line = lore;
				break;
			}
		}
		line = line.replace(getCustomName(enchant) + " ", "");
		line = Methods.removeColor(line);
		if(Methods.isInt(line)) return Integer.parseInt(line);
		if(line.equalsIgnoreCase("I")) return 1;
		if(line.equalsIgnoreCase("II")) return 2;
		if(line.equalsIgnoreCase("III")) return 3;
		if(line.equalsIgnoreCase("IV")) return 4;
		if(line.equalsIgnoreCase("V")) return 5;
		if(line.equalsIgnoreCase("VI")) return 6;
		if(line.equalsIgnoreCase("VII")) return 7;
		if(line.equalsIgnoreCase("VIII")) return 8;
		if(line.equalsIgnoreCase("IX")) return 9;
		if(line.equalsIgnoreCase("X")) return 10;
		return 1;
	}

	public String convertPower(Integer i) {
		if(i <= 0) return "I";
		if(i == 1) return "I";
		if(i == 2) return "II";
		if(i == 3) return "III";
		if(i == 4) return "IV";
		if(i == 5) return "V";
		if(i == 6) return "VI";
		if(i == 7) return "VII";
		if(i == 8) return "VIII";
		if(i == 9) return "IX";
		if(i == 10) return "X";
		return i + "";
	}

	public void update() {
		CustomEnchants.clear();
		Name.clear();
		BookColor.clear();
		EnchantmentColor.clear();
		Toggle.clear();
		Discription.clear();
		Type.clear();
		for(String ench : Main.settings.getCustomEnchs().getConfigurationSection("Enchantments").getKeys(false)) {
			CustomEnchants.add(ench);
			Name.put(ench, Main.settings.getCustomEnchs().getString("Enchantments." + ench + ".Name"));
			BookColor.put(ench, Main.settings.getCustomEnchs().getString("Enchantments." + ench + ".BookColor"));
			EnchantmentColor.put(ench, Main.settings.getCustomEnchs().getString("Enchantments." + ench + ".Color"));
			Toggle.put(ench, Main.settings.getCustomEnchs().getBoolean("Enchantments." + ench + ".Enabled"));
			Discription.put(ench, Main.settings.getCustomEnchs().getStringList("Enchantments." + ench + ".Info.Description"));
			Type.put(ench, EnchantmentType.getFromName(Main.settings.getCustomEnchs().getString("Enchantments." + ench + ".EnchantOptions.ItemsEnchantable")));
		}
	}

}