package com.badbones69.crazyenchantments.paper;

import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.PluginSupport;
import com.badbones69.crazyenchantments.paper.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.api.support.anticheats.SpartanSupport;
import com.badbones69.crazyenchantments.paper.api.support.misc.OraxenSupport;
import com.badbones69.crazyenchantments.paper.utilities.misc.ColorUtils;
import com.badbones69.crazyenchantments.paper.utilities.misc.EventUtils;
import com.badbones69.crazyenchantments.paper.utilities.misc.NumberUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Methods {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    // Plugin Support.
    private final PluginSupport pluginSupport = starter.getPluginSupport();

    private final OraxenSupport oraxenSupport = starter.getOraxenSupport();

    private final SpartanSupport spartanSupport = starter.getSpartanSupport();

    public EnchantmentType getFromName(String name) {
        for (EnchantmentType enchantmentType : starter.getInfoMenuManager().getEnchantmentTypes()) {
            if (enchantmentType.getName().equalsIgnoreCase(name)) return enchantmentType;
        }

        return null;
    }

    public int getRandomNumber(String range) {
        int number = 1;
        String[] split = range.split("-");

        if (NumberUtils.isInt(split[0]) && NumberUtils.isInt(split[1])) {
            int max = Integer.parseInt(split[1]) + 1;
            int min = Integer.parseInt(split[0]);

            number = getRandomNumber(min, max);

        }

        return number;
    }

    public int getRandomNumber(int min, int max) {
        Random random = new Random();
        return min + random.nextInt(max - min);
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

    @NotNull
    public ItemStack getItemInHand(Player player) {
        return player.getInventory().getItemInMainHand();
    }

    public void setItemInHand(Player player, ItemStack item) {
        player.getInventory().setItemInMainHand(item);
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
        Objects.requireNonNull(item.lore()).add(ColorUtils.legacyTranslateColourCodes(i));
        return item;
    }

    public boolean hasArgument(String arg, List<String> message) {
        for (String line : message) {
            line = ColorUtils.color(line).toLowerCase();

            if (line.contains(arg.toLowerCase())) return true;
        }

        return false;
    }

    public boolean randomPicker(int min, int max) {
        if (max <= min || max <= 0) return true;

        Random random = new Random();

        int chance = 1 + random.nextInt(max);
        return chance <= min;
    }

    public Integer percentPick(int max, int min) {
        if (max == min) {
            return max;
        } else {
            Random random = new Random();

            return min + random.nextInt(max - min);
        }
    }

    /**
     *
     * @param player The {@link Player} who's inventory should be checked.
     * @return Returns if the player's inventory is full while letting them know.
     */
    public boolean isInventoryFull(Player player) {
        if (player.getInventory().firstEmpty() != -1) return false;
        player.sendMessage(Messages.INVENTORY_FULL.getMessage());
        return true;
    }

    /**
     *
     * @param player The {@link Player} to give items to.
     * @param item The {@link ItemStack} to give to the player.
     */
    public void addItemToInventory(Player player, ItemStack item) {
        player.getInventory().addItem(item).values().forEach(x -> player.getWorld().dropItem(player.getLocation(), x));
    }
    public void addItemToInventory(Player player, List<Item> itemList) {
        itemList.forEach(x -> addItemToInventory(player, x.getItemStack()));
    }

    public List<LivingEntity> getNearbyLivingEntities(double radius, Entity entity) {
        List<Entity> out = entity.getNearbyEntities(radius, radius, radius);
        List<LivingEntity> entities = new ArrayList<>();

        for (Entity en : out) {
            if (en instanceof LivingEntity) entities.add((LivingEntity) en);
        }

        return entities;
    }

    public List<Entity> getNearbyEntities(double radius, Entity entity) {
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

        plugin.getFireworkDamageListener().addFirework(firework);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, firework::detonate, 2);
    }

    public Enchantment getEnchantment(String enchantmentName) {
        try {
            // HashMap<String, String> enchantments = getEnchantments();
            enchantmentName = enchantmentName.replaceAll("-|_| ", "");

            for (Enchantment enchantment : Enchantment.values()) {
                // MC 1.13+ has the correct names.
                if (enchantment.getKey().getKey().replaceAll("-|_| ", "").equalsIgnoreCase(enchantmentName)) return enchantment;
            }
        } catch (Exception ignore) {}

        return null;
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

            if (nbtItem.hasNBTData() && nbtItem.hasTag("Unbreakable") && nbtItem.getBoolean("Unbreakable")) return;

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

    public void explode(Entity player) {
        spawnExplodeParticles(player, player.getWorld(), player.getLocation());

        for (Entity entity : getNearbyEntities(3D, player)) {
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
                    if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                        spartanSupport.cancelSpeed((Player) player);
                        spartanSupport.cancelNormalMovements((Player) player);
                        spartanSupport.cancelNoFall((Player) player);
                    }
                }

                en.setVelocity(en.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1).setY(.5));
            }
        }
    }

    private void spawnExplodeParticles(Entity player, World world, Location location) {
        if (player.getLocation().getWorld() != null) {
            player.getLocation().getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 200);
            player.getLocation().getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 30, .4F, .5F, .4F);
            player.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, player.getLocation(), 2);
        }

        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
    }

    public void explode(Entity player, Entity arrow) {
        spawnExplodeParticles(arrow, player.getWorld(), player.getLocation());

        for (Entity entity : getNearbyEntities(3D, arrow)) {
            if (pluginSupport.allowCombat(entity.getLocation())) {
                if (entity.getType() == EntityType.DROPPED_ITEM) {
                    entity.remove();
                    continue;
                }

                if (!(entity instanceof LivingEntity en)) continue;
                if (pluginSupport.isFriendly(player, en)) continue;
                if (player.getName().equalsIgnoreCase(entity.getName())) continue;

                EntityDamageByEntityEvent damageByEntityEvent = new EntityDamageByEntityEvent(player, en, EntityDamageEvent.DamageCause.CUSTOM, 5D);
                plugin.getServer().getPluginManager().callEvent(damageByEntityEvent);
                if (damageByEntityEvent.isCancelled()) continue;

                en.damage(5D);

                en.setVelocity(en.getLocation().toVector().subtract(arrow.getLocation().toVector()).normalize().multiply(1).setY(.5));
                if (!(en instanceof Player)) continue;

                if (SupportedPlugins.SPARTAN.isPluginLoaded()) {
                    spartanSupport.cancelSpeed((Player) player);
                    spartanSupport.cancelNormalMovements((Player) player);
                    spartanSupport.cancelNoFall((Player) player);
                }
            }
        }
    }

    public HashSet<Block> getEnchantBlocks(Location loc, Location loc2) {
        HashSet<Block> blockList = new HashSet<>();
        int topBlockX = (Math.max(loc.getBlockX(), loc2.getBlockX()));
        int bottomBlockX = (Math.min(loc.getBlockX(), loc2.getBlockX()));
        int topBlockY = (Math.max(loc.getBlockY(), loc2.getBlockY()));
        int bottomBlockY = (Math.min(loc.getBlockY(), loc2.getBlockY()));
        int topBlockZ = (Math.max(loc.getBlockZ(), loc2.getBlockZ()));
        int bottomBlockZ = (Math.min(loc.getBlockZ(), loc2.getBlockZ()));

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    if (loc.getWorld() != null) blockList.add(loc.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        return blockList;
    }

    public void entityEvent(Player damager, LivingEntity entity, EntityDamageByEntityEvent damageByEntityEvent) {
        EventUtils.addIgnoredEvent(damageByEntityEvent);
        EventUtils.addIgnoredUUID(damager.getUniqueId());
        plugin.getServer().getPluginManager().callEvent(damageByEntityEvent);

        if (!damageByEntityEvent.isCancelled() && pluginSupport.allowCombat(entity.getLocation()) && !pluginSupport.isFriendly(damager, entity)) entity.damage(5D);

        EventUtils.removeIgnoredEvent(damageByEntityEvent);
        EventUtils.removeIgnoredUUID(damager.getUniqueId());
    }

    public void lightning(LivingEntity en) {
        Location loc = en.getLocation();
        if (loc.getWorld() != null) loc.getWorld().strikeLightning(loc);
        int lightningSoundRange = Files.CONFIG.getFile().getInt("Settings.EnchantmentOptions.Lightning-Sound-Range", 160);

        try {
            loc.getWorld().playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, (float) lightningSoundRange / 16f, 1);
        } catch (Exception ignore) {}
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
        Random random = new Random();

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