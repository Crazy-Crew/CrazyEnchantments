package me.badbones69.crazyenchantments.api.objects;

import me.badbones69.crazyenchantments.api.CrazyEnchantments;
import me.badbones69.crazyenchantments.api.enums.Messages;
import me.badbones69.crazyenchantments.api.managers.AllyManager;
import me.badbones69.crazyenchantments.multisupport.Support;
import me.badbones69.crazyenchantments.multisupport.Version;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class AllyMob {
    
    private AllyType type;
    private Player owner;
    private LivingEntity ally;
    private long spawnTime;
    private BukkitTask runnable;
    private CrazyEnchantments ce = CrazyEnchantments.getInstance();
    private Support support = Support.getInstance();
    private AllyMob instance;
    private static AllyManager allyManager = AllyManager.getInstance();
    
    public AllyMob(Player owner, AllyType type) {
        this.type = type;
        this.owner = owner;
        instance = this;
    }
    
    public AllyType getType() {
        return type;
    }
    
    public Player getOwner() {
        return owner;
    }
    
    public LivingEntity getAlly() {
        return ally;
    }
    
    public void spawnAlly(long spawnTime) {
        spawnAlly(owner.getLocation(), spawnTime);
    }
    
    public void spawnAlly(Location location, long spawnTime) {
        this.spawnTime = spawnTime;
        ally = (LivingEntity) location.getWorld().spawnEntity(location, type.entityType);
        support.noStack(ally);
        if (ce.useHealthAttributes()) {
            ally.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(type.maxHealth);
        } else {
            ally.setMaxHealth(type.maxHealth);
        }
        ally.setHealth(type.maxHealth);
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%Player%", owner.getName());
        placeholders.put("%Mob%", type.entityType.getName());
        ally.setCustomName(Messages.replacePlaceholders(placeholders, type.getName()));
        ally.setCustomNameVisible(true);
        startSpawnTimer();
        allyManager.addAllyMob(instance);
    }
    
    public void forceRemoveAlly() {
        runnable.cancel();
        allyManager.removeAllyMob(instance);
        ally.remove();
    }
    
    public void attackEnemy(LivingEntity enemy) {
        switch (ally.getType()) {
            case WOLF:
                Wolf wolf = (Wolf) ally;
                wolf.setTarget(enemy);
                break;
            case IRON_GOLEM:
                IronGolem iron = (IronGolem) ally;
                iron.setTarget(enemy);
                break;
            case ZOMBIE:
                Zombie zom = (Zombie) ally;
                zom.setTarget(enemy);
                break;
            case ENDERMITE:
                Endermite mite = (Endermite) ally;
                mite.setTarget(enemy);
                break;
            case SILVERFISH:
                Silverfish sfish = (Silverfish) ally;
                sfish.setTarget(enemy);
                break;
            case BEE:
                Bee bee = (Bee) ally;
                bee.setTarget(enemy);
                break;
        }
    }
    
    private void startSpawnTimer() {
        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                allyManager.removeAllyMob(instance);
                ally.remove();
            }
        }.runTaskLater(ce.getPlugin(), spawnTime * 20);
    }
    
    public enum AllyType {
        
        WOLF("Wolf", "&b%player%'s Saberwolf", EntityType.WOLF, 16),
        IRON_GOLEM("Iron-Golem", "&6%player%'s Golem", EntityType.IRON_GOLEM, 200),
        ZOMBIE("Zombie", "&2%player%'s Undead", EntityType.ZOMBIE, 45),
        ENDERMITE("Endermite", "&5%player%'s Endermite", EntityType.ENDERMITE, 10),
        SILVERFISH("Silverfish", "&7%player%'s Silverfish", EntityType.SILVERFISH, 10),
        BEE("Bee", "&e%player%'s Bee", Version.isNewer(Version.v1_14_R1) ? EntityType.valueOf("BEE") : EntityType.WOLF, 10);
        
        private String configName;
        private String defaultName;
        private EntityType entityType;
        private int maxHealth;
        
        private AllyType(String configName, String defaultName, EntityType entityType, int maxHealth) {
            this.configName = configName;
            this.defaultName = defaultName;
            this.entityType = entityType;
            this.maxHealth = maxHealth;
        }
        
        public String getConfigName() {
            return configName;
        }
        
        public String getDefaultName() {
            return defaultName;
        }
        
        public String getName() {
            return allyManager.getAllyTypeNameCache().get(this);
        }
        
        public EntityType getEntityType() {
            return entityType;
        }
        
        public int getMaxHealth() {
            return maxHealth;
        }
    }
    
}