package com.badbones69.crazyenchantments.paper.api.objects;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.managers.AllyManager;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
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
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;

public class AllyMob {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

    @NotNull
    private final AllyManager allyManager = null;

    private final AllyType type;
    private final Player owner;
    private final AllyMob instance;

    private LivingEntity ally;
    private long spawnTime;
    private ScheduledTask runnable;

    public AllyMob(@NotNull final Player owner, @NotNull final AllyType type) {
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

    public void spawnAlly(final long spawnTime) {
        spawnAlly(this.owner.getLocation(), spawnTime);
    }

    public void spawnAlly(@NotNull final Location location, final long spawnTime) {
        this.spawnTime = spawnTime;

        this.ally = (LivingEntity) location.getWorld().spawnEntity(location, this.type.entityType);

        this.ally.getAttribute(Attribute.MAX_HEALTH).setBaseValue(this.type.maxHealth); //todo() npe check

        this.ally.setHealth(this.type.maxHealth);

        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%Player%", this.owner.getName());
        placeholders.put("%Mob%", this.type.entityType.getName()); //todo() npe check

        //this.ally.setCustomName(Messages.replacePlaceholders(placeholders, this.type.getName())); //todo() legacy trash
        this.ally.setCustomNameVisible(true);

        startSpawnTimer();

        this.allyManager.addAllyMob(this.instance);
    }

    public void forceRemoveAlly() {
        this.runnable.cancel();
        this.allyManager.removeAllyMob(this.instance);
        this.ally.remove();
    }

    public void attackEnemy(@NotNull final LivingEntity enemy) {
        new FoliaScheduler(this.plugin, null, this.ally) {
            @Override
            public void run() {
                switch (ally.getType()) {
                    case WOLF -> {
                        Wolf wolf = (Wolf) ally;
                        wolf.setTarget(enemy);
                    }

                    case IRON_GOLEM -> {
                        IronGolem iron = (IronGolem) ally;
                        iron.setTarget(enemy);
                    }

                    case ZOMBIE -> {
                        Zombie zom = (Zombie) ally;
                        zom.setTarget(enemy);
                    }

                    case ENDERMITE -> {
                        Endermite mite = (Endermite) ally;
                        mite.setTarget(enemy);
                    }

                    case SILVERFISH -> {
                        Silverfish sfish = (Silverfish) ally;
                        sfish.setTarget(enemy);
                    }

                    case BEE -> {
                        Bee bee = (Bee) ally;
                        bee.setCannotEnterHiveTicks(Integer.MAX_VALUE);
                        bee.setTarget(enemy);
                    }
                }
            }
        }.runNextTick();
    }

    private void startSpawnTimer() {
        if (this.ally != null) {
            this.runnable = new FoliaScheduler(this.plugin, null, this.ally) {
                @Override
                public void run() {
                    allyManager.removeAllyMob(instance);
                    ally.remove();
                }
            }.runDelayed(this.spawnTime * 20);
        }
    }
    
    public enum AllyType {
        WOLF("Wolf", "<aqua>%player%'s Saberwolf", EntityType.WOLF, 16),
        IRON_GOLEM("Iron-Golem", "<gold>%player%'s Golem", EntityType.IRON_GOLEM, 200),
        ZOMBIE("Zombie", "<dark_green>%player%'s Undead", EntityType.ZOMBIE, 45),
        ENDERMITE("Endermite", "<dark_purple>%player%'s Endermite", EntityType.ENDERMITE, 10),
        SILVERFISH("Silverfish", "<gray>%player%'s Silverfish", EntityType.SILVERFISH, 10),
        BEE("Bee", "<yellow>%player%'s Bee", EntityType.BEE, 10);
        
        private final String configName;
        private final String defaultName;
        private final EntityType entityType;
        private final int maxHealth;  // TODO Make this into a config option.

        @NotNull
        private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);

        @NotNull
        private final AllyManager allyManager = null;
        
        AllyType(@NotNull final String configName, @NotNull final String defaultName, @NotNull final EntityType entityType, final int maxHealth) {
            this.configName = configName;
            this.defaultName = defaultName;
            this.entityType = entityType;
            this.maxHealth = maxHealth;
        }
        
        public String getConfigName() {
            return this.configName;
        }
        
        public String getDefaultName() {
            return this.defaultName;
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