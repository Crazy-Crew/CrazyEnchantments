package com.badbones69.crazyenchantments;

import com.badbones69.crazyenchantments.api.CrazyManager;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.PluginSupport;
import com.badbones69.crazyenchantments.api.economy.Currency;
import com.badbones69.crazyenchantments.api.enums.Messages;
import com.badbones69.crazyenchantments.api.support.misc.OraxenSupport;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Methods {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final OraxenSupport oraxenSupport = plugin.getStarter().getOraxenSupport();

    private final PluginSupport pluginSupport = plugin.getStarter().getPluginSupport();

    private final Random random = new Random();

    public final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F\\d]{6}");

    public String color(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }

    public int getRandomNumber(String range) {
        int number = 1;
        String[] split = range.split("-");

        if (isInt(split[0]) && isInt(split[1])) {
            int max = Integer.parseInt(split[1]) + 1;
            int min = Integer.parseInt(split[0]);
            number = min + random.nextInt(max - min);
        }

        return number;
    }

    public boolean hasPermission(CommandSender sender, String perm, boolean toggle) {
        if (sender instanceof Player) {
            return hasPermission((Player) sender, perm, toggle);
        } else {
            return true;
        }
    }

    public boolean hasPermission(Player player, String perm, boolean toggle) {
        if (player.hasPermission("crazyenchantments." + perm) || player.hasPermission("crazyenchantments.admin")) {
            return true;
        } else {
            if (toggle) player.sendMessage(Messages.NO_PERMISSION.getMessage());

            return false;
        }
    }

    public ItemStack addGlow(ItemStack item) {
        return addGlow(item, true);
    }

    public ItemStack addGlow(ItemStack item, boolean toggle) {
        ItemStack it = item.clone();
        try {
            if (toggle) {
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().hasEnchants()) return item;
                }

                item.addUnsafeEnchantment(Enchantment.LUCK, 1);
                ItemMeta meta = item.getItemMeta();
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }

            return item;
        } catch (NoClassDefFoundError e) {
            return it;
        }
    }

    public ItemStack getItemInHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    public void setItemInHand(Player player, ItemStack item) {
        player.getInventory().setItemInMainHand(item);
    }

    public String getPrefix() {
        return getPrefix("");
    }

    public String getPrefix(String string) {
        return color(Files.CONFIG.getFile().getString("Settings.Prefix") + string);
    }

    public boolean isInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    public Player getPlayer(String name) {
        return plugin.getServer().getPlayer(name);
    }

    public boolean isPlayerOnline(String playerName, CommandSender sender) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(playerName)) return true;
        }

        sender.sendMessage(Messages.NOT_ONLINE.getMessage());
        return false;
    }

    public void removeItem(ItemStack item, Player player) {
        removeItem(item, player, 1);
    }

    public void removeItem(ItemStack item, Player player, int amount) {
        try {
            boolean found = false;

            if (player.getInventory().contains(item)) {
                if (item.getAmount() <= amount) {
                    player.getInventory().removeItem(item);
                    found = true;
                } else {
                    found = true;
                    item.setAmount(item.getAmount() - amount);
                }
            }

            if (!found) {
                ItemStack offHand = player.getEquipment().getItemInOffHand();

                if (offHand.isSimilar(item)) {
                    if ((amount - offHand.getAmount()) >= 0) {
                        player.getEquipment().setItemInOffHand(new ItemStack(Material.AIR, 1));
                    } else {
                        item.setAmount(offHand.getAmount() - amount);
                    }
                }
            }
        } catch (Exception ignored) {}

        player.updateInventory();
    }

    public ItemStack removeItem(ItemStack item) {
        return removeItem(item, 1);
    }

    public ItemStack removeItem(ItemStack item, int amount) {
        ItemStack itemStack = item.clone();

        if (item.getAmount() <= amount) {
            itemStack = new ItemStack(Material.AIR);
        } else {
            itemStack.setAmount(item.getAmount() - amount);
        }

        return itemStack;
    }

    public ItemStack addLore(ItemStack item, String i) {
        ArrayList<String> lore = new ArrayList<>();
        ItemMeta m = item.getItemMeta();

        if (item.getItemMeta().hasLore()) lore.addAll(item.getItemMeta().getLore());

        lore.add(color(i));

        if (lore.contains(color(Files.CONFIG.getFile().getString("Settings.WhiteScroll.ProtectedName")))) {
            lore.remove(color(Files.CONFIG.getFile().getString("Settings.WhiteScroll.ProtectedName")));
            lore.add(color(Files.CONFIG.getFile().getString("Settings.WhiteScroll.ProtectedName")));
        }

        if (lore.contains(color(Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected")))) {
            lore.remove(color(Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected")));
            lore.add(color(Files.CONFIG.getFile().getString("Settings.ProtectionCrystal.Protected")));
        }

        m.setLore(lore);
        item.setItemMeta(m);

        return item;
    }

    public int getPercent(String argument, ItemStack item, List<String> originalLore, int defaultValue) {
        String arg = defaultValue + "";

        for (String originalLine : originalLore) {
            originalLine = color(originalLine).toLowerCase();

            if (originalLine.contains(argument.toLowerCase())) {
                String[] b = originalLine.split(argument.toLowerCase());

                for (String itemLine : item.getItemMeta().getLore()) {
                    boolean toggle = false; // Checks to make sure the lore is the same.

                    if (b.length >= 1) {
                        if (itemLine.toLowerCase().startsWith(b[0])) {
                            arg = itemLine.toLowerCase().replace(b[0], "");
                            toggle = true;
                        }
                    }

                    if (b.length >= 2) {
                        if (itemLine.toLowerCase().endsWith(b[1])) {
                            arg = arg.toLowerCase().replace(b[1], "");
                        } else {
                            toggle = false;
                        }
                    }

                    if (toggle) break;
                }

                if (isInt(arg)) break;
            }
        }

        int percent = defaultValue;

        if (isInt(arg)) percent = Integer.parseInt(arg);

        return percent;
    }

    public boolean hasArgument(String arg, List<String> message) {
        for (String line : message) {
            line = color(line).toLowerCase();

            if (line.contains(arg.toLowerCase())) return true;
        }

        return false;
    }

    public boolean randomPicker(int max) {
        if (max <= 0) return true;

        int chance = 1 + random.nextInt(max);
        return chance == 1;
    }

    public boolean randomPicker(int min, int max) {
        if (max <= min || max <= 0) return true;

        int chance = 1 + random.nextInt(max);
        return chance <= min;
    }

    public Integer percentPick(int max, int min) {
        if (max == min) {
            return max;
        } else {
            return min + random.nextInt(max - min);
        }
    }

    public boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public List<LivingEntity> getNearbyLivingEntities(Location loc, double radius, Entity entity) {
        List<Entity> out = entity.getNearbyEntities(radius, radius, radius);
        List<LivingEntity> entities = new ArrayList<>();

        for (Entity en : out) {
            if (en instanceof LivingEntity) entities.add((LivingEntity) en);
        }

        return entities;
    }

    public List<Entity> getNearbyEntities(Location loc, double radius, Entity entity) {
        return entity.getNearbyEntities(radius, radius, radius);
    }

    public void fireWork(Location loc, List<Color> colors) {
        fireWork(loc, new ArrayList<>(colors));
    }

    public void fireWork(Location loc, ArrayList<Color> colors) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE)
                .withColor(colors)
                .trail(false)
                .flicker(false)
                .build());

        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);

        // FireworkDamage.addFirework(fw);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, firework::detonate, 2);
    }

    public Color getColor(String color) {
        return switch (color.toUpperCase()) {
            case "AQUA" -> Color.AQUA;
            case "BLACK" -> Color.BLACK;
            case "BLUE" -> Color.BLUE;
            case "FUCHSIA" -> Color.FUCHSIA;
            case "GRAY" -> Color.GRAY;
            case "GREEN" -> Color.GREEN;
            case "LIME" -> Color.LIME;
            case "MAROON" -> Color.MAROON;
            case "NAVY" -> Color.NAVY;
            case "OLIVE" -> Color.OLIVE;
            case "ORANGE" -> Color.ORANGE;
            case "PURPLE" -> Color.PURPLE;
            case "RED" -> Color.RED;
            case "SILVER" -> Color.SILVER;
            case "TEAL" -> Color.TEAL;
            case "YELLOW" -> Color.YELLOW;
            default -> Color.WHITE;
        };
    }

    public String stripString(String string) {
        return string != null ? string.replace("-", "").replace("_", "").replace(" ", "") : "";
    }

    public Enchantment getEnchantment(String enchantmentName) {
        try {
            // HashMap<String, String> enchantments = getEnchantments();
            enchantmentName = stripString(enchantmentName);

            for (Enchantment enchantment : Enchantment.values()) {
                // MC 1.13+ has the correct names.
                if (stripString(enchantment.getKey().getKey()).equalsIgnoreCase(enchantmentName)) return enchantment;
            }
        } catch (Exception ignore) {}

        return null;
    }

    /**
     * Verify the ItemStack has a lore. This checks to make sure everything isn't null because recent minecraft updates cause NPEs.
     *
     * @param item Itemstack you are checking.
     * @return True if the item has a lore and no null issues.
     */
    public boolean verifyItemLore(ItemStack item) {
        return item != null && item.getItemMeta() != null && item.hasItemMeta() && item.getItemMeta().getLore() != null && item.getItemMeta().hasLore();
    }

    public HashMap<String, String> getEnchantments() {
        HashMap<String, String> enchantments = new HashMap<>();
        enchantments.put("ARROW_DAMAGE", "Power");
        enchantments.put("ARROW_FIRE", "Flame");
        enchantments.put("ARROW_INFINITE", "Infinity");
        enchantments.put("ARROW_KNOCKBACK", "Punch");
        enchantments.put("DAMAGE_ALL", "Sharpness");
        enchantments.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
        enchantments.put("DAMAGE_UNDEAD", "Smite");
        enchantments.put("DEPTH_STRIDER", "Depth_Strider");
        enchantments.put("DIG_SPEED", "Efficiency");
        enchantments.put("DURABILITY", "Unbreaking");
        enchantments.put("FIRE_ASPECT", "Fire_Aspect");
        enchantments.put("KNOCKBACK", "KnockBack");
        enchantments.put("LOOT_BONUS_BLOCKS", "Fortune");
        enchantments.put("LOOT_BONUS_MOBS", "Looting");
        enchantments.put("LUCK", "Luck_Of_The_Sea");
        enchantments.put("LURE", "Lure");
        enchantments.put("OXYGEN", "Respiration");
        enchantments.put("PROTECTION_ENVIRONMENTAL", "Protection");
        enchantments.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
        enchantments.put("PROTECTION_FALL", "Feather_Falling");
        enchantments.put("PROTECTION_FIRE", "Fire_Protection");
        enchantments.put("PROTECTION_PROJECTILE", "Projectile_Protection");
        enchantments.put("SILK_TOUCH", "Silk_Touch");
        enchantments.put("THORNS", "Thorns");
        enchantments.put("WATER_WORKER", "Aqua_Affinity");
        enchantments.put("BINDING_CURSE", "Curse_Of_Binding");
        enchantments.put("MENDING", "Mending");
        enchantments.put("FROST_WALKER", "Frost_Walker");
        enchantments.put("VANISHING_CURSE", "Curse_Of_Vanishing");
        return enchantments;
    }

    public int getMaxDurability(ItemStack item) {
        if (!PluginSupport.SupportedPlugins.ORAXEN.isPluginLoaded()) return item.getType().getMaxDurability();

        return oraxenSupport.getMaxDurability(item);
    }

    public int getDurability(ItemStack item) {
        if (!PluginSupport.SupportedPlugins.ORAXEN.isPluginLoaded()) {
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof Damageable) return ((Damageable) item.getItemMeta()).getDamage();
            return 0;
        }

        return oraxenSupport.getDamage(item);
    }

    public void setDurability(ItemStack item, int newDamage) {
        newDamage = Math.max(newDamage, 0);

        if (!PluginSupport.SupportedPlugins.ORAXEN.isPluginLoaded()) {
            ItemMeta meta = item.getItemMeta();

            if (meta instanceof Damageable damageable) {
                damageable.setDamage(newDamage);
                item.setItemMeta(damageable);
            }

            return;
        }

        oraxenSupport.setDamage(item, newDamage);
    }

    public void removeDurability(ItemStack item, Player player) {
        if (getMaxDurability(item) == 0) return;

        if (item.hasItemMeta()) {
            try {
                if (item.getItemMeta().isUnbreakable()) return;
            } catch (NoSuchMethodError ignored) {}

            NBTItem nbtItem = new NBTItem(item);

            if (nbtItem.hasNBTData() && nbtItem.hasKey("Unbreakable") && nbtItem.getBoolean("Unbreakable")) return;

            if (item.getItemMeta().hasEnchants()) {
                if (item.getItemMeta().hasEnchant(Enchantment.DURABILITY)) {
                    if (randomPicker(1, 1 + item.getEnchantmentLevel(Enchantment.DURABILITY))) {
                        if (getDurability(item) > getMaxDurability(item)) {
                            player.getInventory().remove(item);
                        } else {
                            setDurability(item, getDurability(item) + 1);
                        }
                    }

                    return;
                }
            }
        }

        if (getDurability(item) > getMaxDurability(item)) {
            player.getInventory().remove(item);
        } else {
            setDurability(item, getDurability(item) + 1);
        }
    }

    public boolean isSimilar(ItemStack one, ItemStack two) {
        if (one.getType().equals(two.getType())) return false;
        if (!one.hasItemMeta() || !two.hasItemMeta()) return false;
        if (!one.getItemMeta().hasDisplayName() || !two.getItemMeta().hasDisplayName()) return false;
        if (!one.getItemMeta().getDisplayName().equalsIgnoreCase(two.getItemMeta().getDisplayName())) return false;
        if (!one.getItemMeta().hasLore() || !two.getItemMeta().hasLore()) return false;
        int i = 0;

        for (String lore : one.getItemMeta().getLore()) {
            if (!lore.equals(two.getItemMeta().getLore().get(i++))) return false;
        }

        return true;
    }

    public void explode(Entity player) {
        spawnParticles(player, player.getWorld(), player.getLocation());

        for (Entity entity : getNearbyEntities(player.getLocation(), 3D, player)) {
            if (pluginSupport.allowCombat(entity.getLocation())) {
                if (entity.getType() == EntityType.DROPPED_ITEM) {
                    entity.remove();
                    continue;
                }

                if (!(entity instanceof LivingEntity en)) continue;
                if (pluginSupport.isFriendly(player, en)) continue;
                if (player.getName().equalsIgnoreCase(entity.getName())) continue;
                en.damage(5D);

                if (en instanceof Player) {
                    //if (PluginSupport.SupportedPlugins.SPARTAN.isPluginLoaded()) {
                        //SpartanSupport.cancelSpeed((Player) player);
                        //SpartanSupport.cancelNormalMovements((Player) player);
                        //SpartanSupport.cancelNoFall((Player) player);
                    //}
                }

                en.setVelocity(en.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1).setY(.5));
            }
        }
    }

    private void spawnParticles(Entity player, World world, Location location) {
        if (player.getLocation().getWorld() != null) {
            player.getLocation().getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 200);
            player.getLocation().getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 30, .4F, .5F, .4F);
            player.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 2);
        }

        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
    }

    public void explode(Entity player, Entity arrow) {
        spawnParticles(arrow, player.getWorld(), player.getLocation());

        for (Entity entity : getNearbyEntities(arrow.getLocation(), 3D, arrow)) {
            if (pluginSupport.allowCombat(entity.getLocation())) {
                if (entity.getType() == EntityType.DROPPED_ITEM) {
                    entity.remove();
                    continue;
                }

                if (!(entity instanceof LivingEntity en)) continue;
                if (pluginSupport.isFriendly(player, en)) continue;
                if (player.getName().equalsIgnoreCase(entity.getName())) continue;
                en.damage(5D);

                en.setVelocity(en.getLocation().toVector().subtract(arrow.getLocation().toVector()).normalize().multiply(1).setY(.5));
                if (!(en instanceof Player)) continue;

                //if (PluginSupport.SupportedPlugins.SPARTAN.isPluginLoaded()) {
                    // SpartanSupport.cancelSpeed((Player) player);
                    // SpartanSupport.cancelNormalMovements((Player) player);
                    // SpartanSupport.cancelNoFall((Player) player);
                //}
            }
        }
    }

    public void loopEffectsMap(Map<PotionEffectType, Integer> effects, Player player) {
        for (Map.Entry<PotionEffectType, Integer> type : effects.entrySet()) {
            if (type.getValue() < 0) {
                player.removePotionEffect(type.getKey());
            } else {
                player.removePotionEffect(type.getKey());
                player.addPotionEffect(new PotionEffect(type.getKey(), Integer.MAX_VALUE, type.getValue()));
            }
        }
    }

    public List<Block> getEnchantBlocks(Location loc, Location loc2) {
        List<Block> blockList = new ArrayList<>();
        int topBlockX = (Math.max(loc.getBlockX(), loc2.getBlockX()));
        int bottomBlockX = (Math.min(loc.getBlockX(), loc2.getBlockX()));
        int topBlockY = (Math.max(loc.getBlockY(), loc2.getBlockY()));
        int bottomBlockY = (Math.min(loc.getBlockY(), loc2.getBlockY()));
        int topBlockZ = (Math.max(loc.getBlockZ(), loc2.getBlockZ()));
        int bottomBlockZ = (Math.min(loc.getBlockZ(), loc2.getBlockZ()));

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    blockList.add(Objects.requireNonNull(loc.getWorld()).getBlockAt(x, y, z));
                }
            }
        }

        return blockList;
    }

    public void entityEvent(Player damager, LivingEntity entity, EntityDamageByEntityEvent damageByEntityEvent, CrazyManager crazyManager, CrazyEnchantments plugin, PluginSupport pluginSupport) {
        crazyManager.addIgnoredEvent(damageByEntityEvent);
        crazyManager.addIgnoredUUID(damager.getUniqueId());
        plugin.getServer().getPluginManager().callEvent(damageByEntityEvent);

        if (!damageByEntityEvent.isCancelled() && pluginSupport.allowCombat(entity.getLocation()) && !pluginSupport.isFriendly(damager, entity)) entity.damage(5D);

        crazyManager.removeIgnoredEvent(damageByEntityEvent);
        crazyManager.removeIgnoredUUID(damager.getUniqueId());
    }

    public Location checkEntity(LivingEntity en) {
        Location loc = en.getLocation();
        Objects.requireNonNull(loc.getWorld()).spigot().strikeLightningEffect(loc, true);
        int lightningSoundRange = Files.CONFIG.getFile().getInt("Settings.EnchantmentOptions.Lightning-Sound-Range", 160);

        try {
            loc.getWorld().playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, (float) lightningSoundRange / 16f, 1);
        } catch (Exception ignore) {}

        return loc;
    }

    public void switchCurrency(Player player, Currency option, String one, String two, String cost) {
        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put(one, cost);
        placeholders.put(two, cost);

        switch (option) {
            case VAULT -> player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(placeholders));
            case XP_LEVEL -> player.sendMessage(Messages.NEED_MORE_XP_LEVELS.getMessage(placeholders));
            case XP_TOTAL -> player.sendMessage(Messages.NEED_MORE_TOTAL_XP.getMessage(placeholders));
        }
    }

    public ItemBuilder getRandomPaneColor() {
        List<String> colors = Arrays.asList(
                "WHITE_STAINED_GLASS_PANE",
                "ORANGE_STAINED_GLASS_PANE",
                "MAGENTA_STAINED_GLASS_PANE",
                "LIGHT_BLUE_STAINED_GLASS_PANE",
                "YELLOW_STAINED_GLASS_PANE",
                "LIME_STAINED_GLASS_PANE",
                "PINK_STAINED_GLASS_PANE",
                "GRAY_STAINED_GLASS_PANE",
                // Skipped 8 due to it being basically invisible in a GUI.
                "CYAN_STAINED_GLASS_PANE",
                "PURPLE_STAINED_GLASS_PANE",
                "BLUE_STAINED_GLASS_PANE",
                "BROWN_STAINED_GLASS_PANE",
                "GREEN_STAINED_GLASS_PANE",
                "RED_STAINED_GLASS_PANE",
                "BLACK_STAINED_GLASS_PANE");
        return new ItemBuilder().setMaterial(colors.get(random.nextInt(colors.size())));
    }
}