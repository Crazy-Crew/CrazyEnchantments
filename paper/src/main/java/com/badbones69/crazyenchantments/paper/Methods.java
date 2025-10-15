package com.badbones69.crazyenchantments.paper;

import com.badbones69.crazyenchantments.paper.api.CrazyInstance;
import com.badbones69.crazyenchantments.paper.api.enums.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.files.FileKeys;
import com.badbones69.crazyenchantments.paper.api.enums.files.MessageKeys;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantType;
import com.badbones69.crazyenchantments.paper.api.utils.EventUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.google.gson.Gson;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Methods {

    private static final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private static final CrazyInstance instance = plugin.getInstance();

    public static EnchantType getFromName(@NotNull final String name) {
        for (final EnchantType enchantmentType : instance.getRegisteredEnchantmentTypes()) {
            if (enchantmentType.getName().equalsIgnoreCase(name)) return enchantmentType;
        }

        return null;
    }

    public static int getRandomNumber(@NotNull final String range) {
        int number = 1;
        String[] split = range.split("-");

        if (NumberUtils.isInt(split[0]) && NumberUtils.isInt(split[1])) {
            int max = Integer.parseInt(split[1]) + 1;
            int min = Integer.parseInt(split[0]);

            number = getRandomNumber(min, max);

        }

        return number;
    }

    public static int getRandomNumber(final int min, final int max) {
        return min + ThreadLocalRandom.current().nextInt(max - min);
    }

    public static boolean hasPermission(@NotNull final CommandSender sender, @NotNull final String perm, final boolean toggle) {
        if (sender instanceof Player player) {
            return hasPermission(player, perm, toggle);
        } else {
            return true;
        }
    }

    public static boolean hasPermission(@NotNull final Player player, @NotNull final String perm, final boolean toggle) {
        if (player.hasPermission("crazyenchantments." + perm) || player.hasPermission("crazyenchantments.admin")) {
            return true;
        } else {
            if (toggle) MessageKeys.NO_PERMISSION.sendMessage(player);

            return false;
        }
    }

    @NotNull
    public static ItemStack getItemInHand(@NotNull final Player player) {
        return player.getInventory().getItemInMainHand();
    }

    public static void setItemInHand(@NotNull final Player player, @NotNull final ItemStack item) {
        if (item.isEmpty()) return;

        player.getInventory().setItemInMainHand(item);
    }

    @Nullable
    public static Player getPlayer(@NotNull final String name) {
        return plugin.getServer().getPlayer(name);
    }

    public static void removeItem(@NotNull final ItemStack item, @NotNull final Player player) {
        removeItem(item, player, 1);
    }

    public static void removeItem(@NotNull final ItemStack item, @NotNull final Player player, final int amount) {
        if (item.isEmpty()) return;

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

    @Nullable
    public static ItemStack removeItem(@NotNull final ItemStack item) {
        return removeItem(item, 1);
    }

    @Nullable
    public static ItemStack removeItem(@NotNull final ItemStack item, final int amount) {
        if (item.isEmpty()) return item;

        ItemStack itemStack = item.clone();

        if (item.getAmount() <= amount) {
            itemStack = null;
        } else {
            itemStack.setAmount(item.getAmount() - amount);
        }

        return itemStack;
    }

    @NotNull
    public static ItemStack addLore(@NotNull final ItemStack item, @NotNull final String loreString) {
        if (item.isEmpty()) return item;

        List<net.kyori.adventure.text.Component> lore = item.lore();
        
        if (lore == null) lore = new ArrayList<>();

        //lore.add(ColorUtils.legacyTranslateColourCodes(loreString));

        item.lore(lore);

        return item;
    }

    public static boolean hasArgument(@NotNull final String arg, @NotNull final List<String> message) {
        for (String line : message) {
            //line = ColorUtils.color(line).toLowerCase();

            if (line.contains(arg.toLowerCase())) return true;
        }

        return false;
    }

    public static boolean randomPicker(final int min, final int max) {
        if (max <= min || max <= 0) return true;

        int chance = 1 + ThreadLocalRandom.current().nextInt(max);

        return chance <= min;
    }

    public static int percentPick(final int max, final int min) {
        if (max == min) {
            return max;
        } else {
            return min + ThreadLocalRandom.current().nextInt(max - min);
        }
    }

    /**
     *
     * @param player The {@link Player} who's inventory should be checked.
     * @return Returns if the player's inventory is full while letting them know.
     */
    public static boolean isInventoryFull(@NotNull final Player player) {
        if (player.getInventory().firstEmpty() != -1) return false;

        MessageKeys.INVENTORY_FULL.sendMessage(player);

        return true;
    }

    /**
     *
     * @param player The {@link Player} to give items to.
     * @param item The {@link ItemStack} to give to the player.
     */
    public static void addItemToInventory(@NotNull final Player player, @NotNull final ItemStack item) {
        if (item.isEmpty()) return;

        player.getInventory().addItem(item).values().forEach(x -> player.getWorld().dropItem(player.getLocation(), x));
    }

    public static void addItemToInventory(@NotNull final Player player, @NotNull final List<Item> itemList) {
        itemList.forEach(x -> addItemToInventory(player, x.getItemStack()));
    }

    @NotNull
    public static List<LivingEntity> getNearbyLivingEntities(final double radius, @NotNull final Entity entity) {
        List<Entity> out = entity.getNearbyEntities(radius, radius, radius);
        List<LivingEntity> entities = new ArrayList<>();

        for (Entity en : out) {
            if (en instanceof LivingEntity) entities.add((LivingEntity) en);
        }

        return entities;
    }

    @NotNull
    public static List<Entity> getNearbyEntities(final double radius, @NotNull final Entity entity) {
        return entity.getNearbyEntities(radius, radius, radius);
    }

    public static void fireWork(@NotNull final Location loc, @NotNull final List<Color> colors) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        fireworkMeta.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE)
                .withColor(colors)
                .trail(false)
                .flicker(false)
                .build());

        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);

        addFirework(firework);

        new FoliaScheduler(plugin, loc) {
            @Override
            public void run() {
                firework.detonate();
            }
        }.runDelayed(2);
    }

    public static void addFirework(@NotNull final Entity firework) {
        firework.getPersistentDataContainer().set(DataKeys.no_firework_damage.getNamespacedKey(), PersistentDataType.BOOLEAN, true);
    }

    public static int getMaxDurability(@NotNull final ItemStack item) {
        int durability = item.getType().getMaxDurability();

        if (item.hasData(DataComponentTypes.MAX_DAMAGE)) {
            @Nullable final Integer damage = item.getData(DataComponentTypes.MAX_DAMAGE);

            if (damage != null) {
                durability = damage;
            }
        }

        return durability;
    }

    public static int getDurability(@NotNull ItemStack item) {
        int durability = 0;

        if (item.isEmpty()) return durability;

        if (item.hasData(DataComponentTypes.DAMAGE)) {
            @Nullable final Integer damage = item.getData(DataComponentTypes.DAMAGE);

            if (damage != null) {
                durability = damage;
            }
        }

        return durability;
    }

    public static void setDurability(@NotNull final ItemStack item, int newDamage) {
        newDamage = Math.max(newDamage, 0);

        item.setData(DataComponentTypes.DAMAGE, newDamage);
    }

    public static void removeDurability(@NotNull final ItemStack item, @NotNull final Player player) {
        final int maxDurability = getMaxDurability(item);
        final int durability = getDurability(item);

        if (maxDurability == 0 || item.hasData(DataComponentTypes.UNBREAKABLE)) return;

        if (item.hasData(DataComponentTypes.ENCHANTMENTS)) {
            final boolean hasUnbreaking = item.getEnchantments().containsKey(Enchantment.UNBREAKING);

            if (hasUnbreaking) {
                final int level = item.getEnchantmentLevel(Enchantment.UNBREAKING);

                if (randomPicker(1, 1 + level)) {
                    if (durability > maxDurability) {
                        player.getInventory().remove(item);
                    } else {
                        setDurability(item, durability + 1);
                    }
                }

                return;
            }
        }

        if (durability > maxDurability) {
            player.getInventory().remove(item);
        } else {
            setDurability(item, durability + 1);
        }
    }

    public static void explode(@NotNull final Entity player) {
        spawnExplodeParticles(player.getWorld(), player.getLocation());

        for (Entity entity : getNearbyEntities(3D, player)) {
            //if (this.pluginSupport.allowCombat(entity.getLocation())) {
                if (entity.getType() == EntityType.ITEM) {
                    entity.remove();

                    continue;
                }

                if (!(entity instanceof LivingEntity en)) continue;
                //if (this.pluginSupport.isFriendly(player, en)) continue;
                if (player.getName().equalsIgnoreCase(entity.getName())) continue;

                en.damage(5D);

                en.setVelocity(en.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1).setY(.5));
            //}
        }
    }

    private static final Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }

    private static void spawnExplodeParticles(@NotNull final World world, @NotNull final Location location) {
        world.spawnParticle(Particle.FLAME, location, 200);
        world.spawnParticle(Particle.CLOUD, location, 30, .4F, .5F, .4F);
        world.spawnParticle(Particle.EXPLOSION, location, 2);

        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
    }

    public static void explode(@NotNull final Entity shooter, @NotNull final Entity arrow) {
        spawnExplodeParticles(shooter.getWorld(), arrow.getLocation());

        for (Entity value : getNearbyEntities(3D, arrow)) {
            //if (this.pluginSupport.allowCombat(value.getLocation())) {
                if (value.getType() == EntityType.ITEM) {
                    value.remove();

                    continue;
                }

                if (!(value instanceof LivingEntity livingEntity)) continue;
                //if (this.pluginSupport.isFriendly(shooter, livingEntity)) continue;
                if (shooter.getName().equalsIgnoreCase(value.getName())) continue;

                EntityDamageEvent event = new EntityDamageEvent(livingEntity, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, DamageSource.builder(DamageType.EXPLOSION).withCausingEntity(shooter).withDirectEntity(arrow).build(), 5D);

                plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) continue;

                livingEntity.damage(5D);

                livingEntity.setVelocity(livingEntity.getLocation().toVector().subtract(arrow.getLocation().toVector()).normalize().multiply(1).setY(.5));

            //}
        }
    }

    @NotNull
    public static Set<Block> getEnchantBlocks(@NotNull final Location loc, @NotNull final Location loc2) {
        Set<Block> blockList = new HashSet<>();

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

    public static void entityEvent(@NotNull final Player damager, @NotNull final LivingEntity entity, @NotNull final EntityDamageEvent damageByEntityEvent) {
        EventUtils.addIgnoredEvent(damageByEntityEvent);
        EventUtils.addIgnoredUUID(damager.getUniqueId());

        plugin.getServer().getPluginManager().callEvent(damageByEntityEvent);

        //if (!damageByEntityEvent.isCancelled() && this.pluginSupport.allowCombat(entity.getLocation()) && !this.pluginSupport.isFriendly(damager, entity)) entity.damage(5D);

        EventUtils.removeIgnoredEvent(damageByEntityEvent);
        EventUtils.removeIgnoredUUID(damager.getUniqueId());
    }

    public static Entity lightning(@NotNull final LivingEntity entity) {
        Location loc = entity.getLocation();

        Entity lightning = null;

        if (loc.getWorld() != null) lightning = loc.getWorld().strikeLightning(loc);

        int lightningSoundRange = FileKeys.config.getPaperConfiguration().getInt("Settings.EnchantmentOptions.Lightning-Sound-Range", 160);

        try {
            loc.getWorld().playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, (float) lightningSoundRange / 16f, 1);
        } catch (Exception ignore) {}

        return lightning;
    }

    /**
     *
     * @param event The event to check.
     * @return True if the event is cancelled.
     */
    public static boolean isEventCancelled(@NotNull final Event event) {
        return !event.callEvent();
    }

    /**
     * Checks if the player is in creative mode and lets them know that they should not be.
     * @param player The {@link Player} whom to check.
     * @return True if the player is in creative mode.
     */
    public static boolean inCreativeMode(@NotNull final Player player) {
        if (player.getGameMode() != GameMode.CREATIVE) return false;

        MessageKeys.PLAYER_IS_IN_CREATIVE_MODE.sendMessage(player);

        return true;
    }

    /**
     * Plays item break sound and effect.
     * @param player The {@link Player} who's item broke.
     */
    public static void playItemBreak(@NotNull final Player player, @NotNull final ItemStack item) {
        if (item.isEmpty()) return;

        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);

        player.getWorld().spawnParticle(Particle.ITEM, player.getEyeLocation(), 10, 0.3, 0.5, 0.3, 0, item);
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
    public static boolean playerBreakBlock(@NotNull final Player player, @NotNull final Block block, @NotNull final ItemStack tool, final boolean hasDrops) {
        // My favorite chain of methods I created. Feel free to ask if there are problems. -TDL
        BlockBreakEvent blockBreak = new BlockBreakEvent(block, player);

        Collection<ItemStack> dropItems = !tool.isEmpty() ? block.getDrops(tool, player) : block.getDrops();

        if (dropItems.isEmpty()) blockBreak.setDropItems(false);

        blockBreak.setExpToDrop(getXPThroughNMS(block, tool));

        EventUtils.addIgnoredEvent(blockBreak);

        plugin.getServer().getPluginManager().callEvent(blockBreak);

        EventUtils.removeIgnoredEvent(blockBreak);

        if (blockBreak.isCancelled()) return true;

        if (blockBreak.isDropItems() && hasDrops) blockDropItems(player, block, dropItems);

        dropXP(block, blockBreak.getExpToDrop()); //This will always try to drop xp when the event is not cancelled, as apposed to relying on isDropItems().

        block.setType(Material.AIR);

        return false;
    }


    /**
     * Used to drop XP.
     * @param block The block that is dropping xp.
     * @param expToDrop The amount of xp it should drop.
     */
    private static void dropXP(@NotNull final Block block, final int expToDrop) {
        if (expToDrop < 1) return;

        ExperienceOrb exp = block.getWorld().spawn(block.getLocation(), ExperienceOrb.class);

        exp.setExperience(expToDrop);
    }

    /**
     * Imitates the blockDropItemEvent usage.
     * @param player The player that broke the block.
     * @param block The block that was broken.
     * @param items The items that will be dropped from the broken block.
     */
    private static void blockDropItems(@NotNull final Player player, @NotNull final Block block, @NotNull final Collection<ItemStack> items) {
        List<Item> dropItems = new ArrayList<>();

        items.forEach(item -> dropItems.add(block.getWorld().dropItemNaturally(block.getLocation(), item)));

        BlockDropItemEvent event = new BlockDropItemEvent(block, block.getState(), player, dropItems);

        plugin.getServer().getPluginManager().callEvent(event);

        // If cancelled, removes the blocks as they should have never been there.
        // This mimics the method that the base server uses.
        if (event.isCancelled()) dropItems.forEach(Entity::remove);
    }

    /**
     *
     * @param block The {@link Block} to get xp of when broken.
     * @param item The {@link ItemStack} used to break the block.
     * @return The amount of xp the block would drop when broken by that item.
     */
    private static int getXPThroughNMS(@NotNull final Block block, @NotNull final ItemStack item) { // When it breaks, you can not blame me as I was left unsupervised. -TDL
        CraftBlock cb = (CraftBlock) block;

        net.minecraft.world.level.block.state.BlockState iWorldblockdata = cb.getNMS();
        net.minecraft.world.level.block.Block worldBlock = iWorldblockdata.getBlock();
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return worldBlock.getExpDrop(iWorldblockdata, cb.getHandle().getMinecraftWorld(), cb.getPosition(), nmsItem, true);
    }
}