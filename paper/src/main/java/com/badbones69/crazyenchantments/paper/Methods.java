package com.badbones69.crazyenchantments.paper;

import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EventUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.badbones69.crazyenchantments.paper.support.PluginSupport;
import com.badbones69.crazyenchantments.paper.support.misc.OraxenSupport;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Methods {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    // Plugin Support.
    @NotNull
    private final PluginSupport pluginSupport = this.starter.getPluginSupport();

    @NotNull
    private final OraxenSupport oraxenSupport = this.starter.getOraxenSupport();

    public EnchantmentType getFromName(String name) {
        for (EnchantmentType enchantmentType : MenuManager.getEnchantmentTypes()) {
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
        if (sender instanceof Player player) {
            return hasPermission(player, perm, toggle);
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

    @Nullable
    public Player getPlayer(String name) {
        return this.plugin.getServer().getPlayer(name);
    }

    public boolean isPlayerOnline(String playerName, CommandSender sender) {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
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

        this.plugin.getFireworkDamageListener().addFirework(firework);

        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, firework::detonate, 2);
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

        return this.oraxenSupport.getMaxDurability(item);
    }

    public int getDurability(ItemStack item) {
        if (!PluginSupport.SupportedPlugins.ORAXEN.isPluginLoaded()) {
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof Damageable) return ((Damageable) item.getItemMeta()).getDamage();
            return 0;
        }

        return this.oraxenSupport.getDamage(item);
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

        this.oraxenSupport.setDamage(item, newDamage);
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
            if (this.pluginSupport.allowCombat(entity.getLocation())) {
                if (entity.getType() == EntityType.DROPPED_ITEM) {
                    entity.remove();
                    continue;
                }

                if (!(entity instanceof LivingEntity en)) continue;
                if (this.pluginSupport.isFriendly(player, en)) continue;
                if (player.getName().equalsIgnoreCase(entity.getName())) continue;
                en.damage(5D);

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

    public void explode(Entity entity, Entity arrow) {
        spawnExplodeParticles(arrow, entity.getWorld(), entity.getLocation());

        for (Entity value : getNearbyEntities(3D, arrow)) {
            if (this.pluginSupport.allowCombat(value.getLocation())) {
                if (value.getType() == EntityType.DROPPED_ITEM) {
                    value.remove();
                    continue;
                }

                if (!(value instanceof LivingEntity livingEntity)) continue;
                if (this.pluginSupport.isFriendly(entity, livingEntity)) continue;
                if (entity.getName().equalsIgnoreCase(value.getName())) continue;

                EntityDamageEvent event = new EntityDamageEvent(livingEntity, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, DamageSource.builder(DamageType.EXPLOSION).withCausingEntity(entity).withDirectEntity(arrow).build(), 5D);

                this.plugin.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled()) continue;

                livingEntity.damage(5D);

                livingEntity.setVelocity(livingEntity.getLocation().toVector().subtract(arrow.getLocation().toVector()).normalize().multiply(1).setY(.5));

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

    public void entityEvent(Player damager, LivingEntity entity, EntityDamageEvent damageByEntityEvent) {
        EventUtils.addIgnoredEvent(damageByEntityEvent);
        EventUtils.addIgnoredUUID(damager.getUniqueId());
        this.plugin.getServer().getPluginManager().callEvent(damageByEntityEvent);

        if (!damageByEntityEvent.isCancelled() && this.pluginSupport.allowCombat(entity.getLocation()) && !this.pluginSupport.isFriendly(damager, entity)) entity.damage(5D);

        EventUtils.removeIgnoredEvent(damageByEntityEvent);
        EventUtils.removeIgnoredUUID(damager.getUniqueId());
    }

    public Entity lightning(LivingEntity en) {
        Location loc = en.getLocation();
        Entity lightning = null;
        if (loc.getWorld() != null) lightning = loc.getWorld().strikeLightning(loc);
        int lightningSoundRange = Files.CONFIG.getFile().getInt("Settings.EnchantmentOptions.Lightning-Sound-Range", 160);

        try {
            loc.getWorld().playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, (float) lightningSoundRange / 16f, 1);
        } catch (Exception ignore) {}

        return lightning;
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

    /**
     * @see #playerBreakBlock(Player, Block, ItemStack, boolean)
     */
    public boolean playerBreakBlock(Player player, Block block, ItemStack tool) {
        return playerBreakBlock(player, block, tool, true);
    }

    /**
     * Imitates all the events called when a player breaks a block.
     * Only calls #BlockDropItemEvent if the event isn't cancelled,
     * and there are drops.
     * @param player The player that will "break" the block.
     * @param block The block that was broken.
     * @param tool ItemStack used to break the block.
     * @return If the event was cancelled.
     */
    public boolean playerBreakBlock(Player player, Block block, ItemStack tool, boolean hasDrops) {
        BlockBreakEvent blockBreak = new BlockBreakEvent(block, player);
        Collection<ItemStack> dropItems = tool != null ? block.getDrops(tool, player) : block.getDrops();
        if (dropItems.isEmpty()) blockBreak.setDropItems(false);

        EventUtils.addIgnoredEvent(blockBreak);
        this.plugin.getServer().getPluginManager().callEvent(blockBreak);
        EventUtils.removeIgnoredEvent(blockBreak);

        if (blockBreak.isCancelled()) return true;

        if (blockBreak.isDropItems() && hasDrops) blockDropItems(player, block, dropItems);

        block.setType(Material.AIR);
        return false;
    }

    /**
     * Imitates the blockDropItemEvent usage.
     * @param player The player that broke the block.
     * @param block The block that was broken.
     * @param items The items that will be dropped from the broken block.
     */
    public void blockDropItems(Player player, Block block, Collection<ItemStack> items) {
        List<Item> dropItems = new ArrayList<>();
        items.forEach(item -> dropItems.add(block.getWorld().dropItemNaturally(block.getLocation(), item)));

        BlockDropItemEvent event = new BlockDropItemEvent(block, block.getState(), player, dropItems);
        this.plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) dropItems.forEach(Entity::remove);
    }
}