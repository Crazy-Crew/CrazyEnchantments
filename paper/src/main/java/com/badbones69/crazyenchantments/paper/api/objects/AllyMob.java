package com.badbones69.crazyenchantments.paper.api.objects;

import ch.jalu.configme.properties.Property;
import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.managers.AllyManager;
import com.badbones69.crazyenchantments.platform.impl.Config;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class AllyMob {

    private final @NotNull CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    private final @NotNull AllyManager allyManager = this.plugin.getStarter().getAllyManager();

    private final AllyType type;
    private final Player owner;
    private final AllyMob instance;

    private LivingEntity ally;
    private long spawnTime;
    private BukkitTask runnable;

    public AllyMob(Player owner, AllyType type) {
        this.type = type;
        this.owner = owner;
        this.instance = this;
    }

    public AllyType getType() {
        return this.type;
    }

    public Player getOwner() {
        return this.owner;
    }

    public LivingEntity getAlly() {
        return this.ally;
    }

    public void spawnAlly(long spawnTime) {
        spawnAlly(this.owner.getLocation(), spawnTime);
    }

    public void spawnAlly(Location location, long spawnTime) {
        this.spawnTime = spawnTime;

        this.ally = (LivingEntity) location.getWorld().spawnEntity(location, this.type.entityType);

        this.ally.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(this.type.maxHealth);

        this.ally.setHealth(this.type.maxHealth);

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%Player%", this.owner.getName());
        placeholders.put("%Mob%", this.type.entityType.getName());

        this.ally.setCustomName(Messages.replacePlaceholders(placeholders, this.type.getName()));
        this.ally.setCustomNameVisible(true);

        startSpawnTimer();

        this.allyManager.addAllyMob(this.instance);
    }

    public void forceRemoveAlly() {
        this.runnable.cancel();
        this.allyManager.removeAllyMob(this.instance);
        this.ally.remove();
    }

    public void attackEnemy(LivingEntity enemy) {
        switch (this.ally.getType()) {
            case WOLF -> {
                Wolf wolf = (Wolf) this.ally;
                wolf.setTarget(enemy);
            }

            case IRON_GOLEM -> {
                IronGolem iron = (IronGolem) this.ally;
                iron.setTarget(enemy);
            }

            case ZOMBIE -> {
                Zombie zom = (Zombie) this.ally;
                zom.setTarget(enemy);
            }

            case ENDERMITE -> {
                Endermite mite = (Endermite) this.ally;
                mite.setTarget(enemy);
            }

            case SILVERFISH -> {
                Silverfish sfish = (Silverfish) this.ally;
                sfish.setTarget(enemy);
            }

            case BEE -> {
                Bee bee = (Bee) this.ally;
                bee.setTarget(enemy);
            }
        }
    }

    private void startSpawnTimer() {
        if (this.ally != null) {
            this.runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    allyManager.removeAllyMob(instance);
                    ally.remove();
                }
            }.runTaskLater(this.plugin, this.spawnTime * 20);
        }
    }

    public enum AllyType {

        WOLF(Config.ally_mobs_wolf, EntityType.WOLF, 16),
        IRON_GOLEM(Config.ally_mobs_golem, EntityType.IRON_GOLEM, 200),
        ZOMBIE(Config.ally_mobs_zombie, EntityType.ZOMBIE, 45),
        ENDERMITE(Config.ally_mobs_endermite, EntityType.ENDERMITE, 10),
        SILVERFISH(Config.ally_mobs_silverfish, EntityType.SILVERFISH, 10),
        BEE(Config.ally_mobs_bee, EntityType.BEE, 10);

        private final Property<String> configName;
        private final EntityType entityType;
        private final int maxHealth;

        @NotNull
        private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

        @NotNull
        private final AllyManager allyManager = this.plugin.getStarter().getAllyManager();

        AllyType(Property<String> configName, EntityType entityType, int maxHealth) {
            this.configName = configName;
            this.entityType = entityType;
            this.maxHealth = maxHealth;
        }

        public Property<String> getConfigName() {
            return this.configName;
        }

        public String getName() {
            return this.allyManager.getAllyTypeNameCache().get(this);
        }

        public EntityType getEntityType() {
            return this.entityType;
        }

        public int getMaxHealth() {
            return this.maxHealth;
        }
    }
}