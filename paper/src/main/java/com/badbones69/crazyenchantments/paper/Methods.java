package com.badbones69.crazyenchantments.paper;

import com.badbones69.crazyenchantments.paper.api.builders.types.MenuManager;
import com.badbones69.crazyenchantments.paper.api.economy.Currency;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.enums.pdc.DataKeys;
import com.badbones69.crazyenchantments.paper.api.enums.v2.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.enchants.EnchantmentType;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import com.badbones69.crazyenchantments.paper.api.utils.EventUtils;
import com.badbones69.crazyenchantments.paper.api.utils.NumberUtils;
import com.badbones69.crazyenchantments.paper.support.PluginSupport;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;

public class Methods {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final Starter starter = this.plugin.getStarter();

    // Plugin Support.
    @NotNull
    private final PluginSupport pluginSupport = this.starter.getPluginSupport();

    public EnchantmentType getFromName(@NotNull final String name) {
        for (EnchantmentType enchantmentType : MenuManager.getEnchantmentTypes()) {
            if (enchantmentType.getName().equalsIgnoreCase(name)) return enchantmentType;
        }

        return null;
    }

    public int getRandomNumber(@NotNull final String range) {
        int number = 1;
        String[] split = range.split("-");

        if (NumberUtils.isInt(split[0]) && NumberUtils.isInt(split[1])) {
            int max = Integer.parseInt(split[1]) + 1;
            int min = Integer.parseInt(split[0]);

            number = getRandomNumber(min, max);

        }

        return number;
    }

    public int getRandomNumber(final int min, final int max) {
        Random random = new Random();
        return min + random.nextInt(max - min);
    }

    public boolean hasPermission(@NotNull final CommandSender sender, @NotNull final String perm, final boolean toggle) {
        if (sender instanceof Player player) {
            return hasPermission(player, perm, toggle);
        } else {
            return true;
        }
    }

    public boolean hasPermission(@NotNull final Player player, @NotNull final String perm, final boolean toggle) {
        if (player.hasPermission("crazyenchantments." + perm) || player.hasPermission("crazyenchantments.admin")) {
            return true;
        } else {
            if (toggle) player.sendMessage(Messages.NO_PERMISSION.getMessage());

            return false;
        }
    }

    @NotNull
    public ItemStack getItemInHand(@NotNull final Player player) {
        return player.getInventory().getItemInMainHand();
    }

    public void setItemInHand(@NotNull final Player player, @NotNull final ItemStack item) {
        if (item.isEmpty()) return;

        player.getInventory().setItemInMainHand(item);
    }

    @Nullable
    public Player getPlayer(@NotNull final String name) {
        return this.plugin.getServer().getPlayer(name);
    }

    public void removeItem(@NotNull final ItemStack item, @NotNull final Player player) {
        removeItem(item, player, 1);
    }

    public void removeItem(@NotNull final ItemStack item, @NotNull final Player player, final int amount) {
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
    public ItemStack removeItem(@NotNull final ItemStack item) {
        return removeItem(item, 1);
    }

    @Nullable
    public ItemStack removeItem(@NotNull final ItemStack item, final int amount) {
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
    public ItemStack addLore(@NotNull final ItemStack item, @NotNull final String loreString) {
        if (item.isEmpty()) return item;

        List<net.kyori.adventure.text.Component> lore = item.lore();
        
        if (lore == null) lore = new ArrayList<>();

        lore.add(ColorUtils.legacyTranslateColourCodes(loreString));

        item.lore(lore);

        return item;
    }

    public boolean hasArgument(@NotNull final String arg, @NotNull final List<String> message) {
        for (String line : message) {
            line = ColorUtils.color(line).toLowerCase();

            if (line.contains(arg.toLowerCase())) return true;
        }

        return false;
    }

    public boolean randomPicker(final int min, final int max) {
        if (max <= min || max <= 0) return true;

        Random random = new Random();

        int chance = 1 + random.nextInt(max);

        return chance <= min;
    }

    public Integer percentPick(final int max, final int min) {
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
    public boolean isInventoryFull(@NotNull final Player player) {
        if (player.getInventory().firstEmpty() != -1) return false;

        player.sendMessage(Messages.INVENTORY_FULL.getMessage());

        return true;
    }

    /**
     *
     * @param player The {@link Player} to give items to.
     * @param item The {@link ItemStack} to give to the player.
     */
    public void addItemToInventory(@NotNull final Player player, @NotNull final ItemStack item) {
        if (item.isEmpty()) return;

        player.getInventory().addItem(item).values().forEach(x -> player.getWorld().dropItem(player.getLocation(), x));
    }

    public void addItemToInventory(@NotNull final Player player, @NotNull final List<Item> itemList) {
        itemList.forEach(x -> addItemToInventory(player, x.getItemStack()));
    }

    @NotNull
    public List<LivingEntity> getNearbyLivingEntities(final double radius, @NotNull final Entity entity) {
        List<Entity> out = entity.getNearbyEntities(radius, radius, radius);
        List<LivingEntity> entities = new ArrayList<>();

        for (Entity en : out) {
            if (en instanceof LivingEntity) entities.add((LivingEntity) en);
        }

        return entities;
    }

    @NotNull
    public List<Entity> getNearbyEntities(final double radius, @NotNull final Entity entity) {
        return entity.getNearbyEntities(radius, radius, radius);
    }

    public void fireWork(@NotNull final Location loc, @NotNull final List<Color> colors) {
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

        new FoliaScheduler(this.plugin, loc) {
            @Override
            public void run() {
                firework.detonate();
            }
        }.runDelayed(2);
    }

    public void addFirework(@NotNull final Entity firework) {
        firework.getPersistentDataContainer().set(DataKeys.no_firework_damage.getNamespacedKey(), PersistentDataType.BOOLEAN, true);
    }

    public Enchantment getEnchantment(@NotNull String enchantmentName) {
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

    public int getMaxDurability(@NotNull final ItemStack item) {
        int durability = item.getType().getMaxDurability();

        if (item.hasData(DataComponentTypes.MAX_DAMAGE)) {
            @Nullable final Integer damage = item.getData(DataComponentTypes.MAX_DAMAGE);

            if (damage != null) {
                durability = damage;
            }
        }

        return durability;
    }

    public int getDurability(@NotNull ItemStack item) {
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

    public void setDurability(@NotNull final ItemStack item, int newDamage) {
        newDamage = Math.max(newDamage, 0);

        item.setData(DataComponentTypes.DAMAGE, newDamage);
    }

    public void removeDurability(@NotNull final ItemStack item, @NotNull final Player player) {
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

    public void explode(@NotNull final Entity player) {
        spawnExplodeParticles(player.getWorld(), player.getLocation());

        for (Entity entity : getNearbyEntities(3D, player)) {
            if (this.pluginSupport.allowCombat(entity.getLocation())) {
                if (entity.getType() == EntityType.ITEM) {
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

    private static final Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }

    private void spawnExplodeParticles(@NotNull final World world, @NotNull final Location location) {
        world.spawnParticle(Particle.FLAME, location, 200);
        world.spawnParticle(Particle.CLOUD, location, 30, .4F, .5F, .4F);
        world.spawnParticle(Particle.EXPLOSION, location, 2);

        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
    }

    public void explode(@NotNull final Entity shooter, @NotNull final Entity arrow) {
        spawnExplodeParticles(shooter.getWorld(), arrow.getLocation());

        for (Entity value : getNearbyEntities(3D, arrow)) {
            if (this.pluginSupport.allowCombat(value.getLocation())) {
                if (value.getType() == EntityType.ITEM) {
                    value.remove();

                    continue;
                }

                if (!(value instanceof LivingEntity livingEntity)) continue;
                if (this.pluginSupport.isFriendly(shooter, livingEntity)) continue;
                if (shooter.getName().equalsIgnoreCase(value.getName())) continue;

                EntityDamageEvent event = new EntityDamageEvent(livingEntity, EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, DamageSource.builder(DamageType.EXPLOSION).withCausingEntity(shooter).withDirectEntity(arrow).build(), 5D);

                this.plugin.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) continue;

                livingEntity.damage(5D);

                livingEntity.setVelocity(livingEntity.getLocation().toVector().subtract(arrow.getLocation().toVector()).normalize().multiply(1).setY(.5));

            }
        }
    }

    @NotNull
    public Set<Block> getEnchantBlocks(@NotNull final Location loc, @NotNull final Location loc2) {
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

    public void entityEvent(@NotNull final Player damager, @NotNull final LivingEntity entity, @NotNull final EntityDamageEvent damageByEntityEvent) {
        EventUtils.addIgnoredEvent(damageByEntityEvent);
        EventUtils.addIgnoredUUID(damager.getUniqueId());

        this.plugin.getServer().getPluginManager().callEvent(damageByEntityEvent);

        if (!damageByEntityEvent.isCancelled() && this.pluginSupport.allowCombat(entity.getLocation()) && !this.pluginSupport.isFriendly(damager, entity)) entity.damage(5D);

        EventUtils.removeIgnoredEvent(damageByEntityEvent);
        EventUtils.removeIgnoredUUID(damager.getUniqueId());
    }

    public Entity lightning(@NotNull final LivingEntity entity) {
        Location loc = entity.getLocation();

        Entity lightning = null;

        if (loc.getWorld() != null) lightning = loc.getWorld().strikeLightning(loc);

        int lightningSoundRange = FileKeys.config.getConfiguration().getInt("Settings.EnchantmentOptions.Lightning-Sound-Range", 160);

        try {
            loc.getWorld().playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, (float) lightningSoundRange / 16f, 1);
        } catch (Exception ignore) {}

        return lightning;
    }

    public void switchCurrency(@NotNull final Player player, @NotNull final Currency option, @NotNull final String one, @NotNull final String two, @NotNull final String cost) {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put(one, cost);
        placeholders.put(two, cost);

        switch (option) {
            case VAULT -> player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(placeholders));
            case XP_LEVEL -> player.sendMessage(Messages.NEED_MORE_XP_LEVELS.getMessage(placeholders));
            case XP_TOTAL -> player.sendMessage(Messages.NEED_MORE_TOTAL_XP.getMessage(placeholders));
        }
    }

    /**
     *
     * @param event The event to check.
     * @return True if the event is cancelled.
     */
    public boolean isEventCancelled(@NotNull final Event event) {
        return !event.callEvent();
    }

    /**
     * Checks if the player is in creative mode and lets them know that they should not be.
     * @param player The {@link Player} whom to check.
     * @return True if the player is in creative mode.
     */
    public boolean inCreativeMode(@NotNull final Player player) {
        if (player.getGameMode() != GameMode.CREATIVE) return false;

        player.sendMessage(Messages.PLAYER_IS_IN_CREATIVE_MODE.getMessage());

        return true;
    }

    /**
     * Plays item break sound and effect.
     * @param player The {@link Player} who's item broke.
     */
    public void playItemBreak(@NotNull final Player player, @NotNull final ItemStack item) {
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
    public boolean playerBreakBlock(@NotNull final Player player, @NotNull final Block block, @NotNull final ItemStack tool, final boolean hasDrops) {
        // My favorite chain of methods I created. Feel free to ask if there are problems. -TDL
        BlockBreakEvent blockBreak = new BlockBreakEvent(block, player);

        Collection<ItemStack> dropItems = !tool.isEmpty() ? block.getDrops(tool, player) : block.getDrops();

        if (dropItems.isEmpty()) blockBreak.setDropItems(false);

        blockBreak.setExpToDrop(getXPThroughNMS(block, tool));

        EventUtils.addIgnoredEvent(blockBreak);

        this.plugin.getServer().getPluginManager().callEvent(blockBreak);

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
    private void dropXP(@NotNull final Block block, final int expToDrop) {
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
    private void blockDropItems(@NotNull final Player player, @NotNull final Block block, @NotNull final Collection<ItemStack> items) {
        List<Item> dropItems = new ArrayList<>();

        items.forEach(item -> dropItems.add(block.getWorld().dropItemNaturally(block.getLocation(), item)));

        BlockDropItemEvent event = new BlockDropItemEvent(block, block.getState(), player, dropItems);

        this.plugin.getServer().getPluginManager().callEvent(event);

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
    private int getXPThroughNMS(@NotNull final Block block, @NotNull final ItemStack item) { // When it breaks, you can not blame me as I was left unsupervised. -TDL
        CraftBlock cb = (CraftBlock) block;

        net.minecraft.world.level.block.state.BlockState iWorldblockdata = cb.getNMS();
        net.minecraft.world.level.block.Block worldBlock = iWorldblockdata.getBlock();
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return worldBlock.getExpDrop(iWorldblockdata, cb.getHandle().getMinecraftWorld(), cb.getPosition(), nmsItem, true);
    }
}