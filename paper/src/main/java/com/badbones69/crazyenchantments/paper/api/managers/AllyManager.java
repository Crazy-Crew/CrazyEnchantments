package com.badbones69.crazyenchantments.paper.api.managers;

import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.objects.AllyMob;
import com.badbones69.crazyenchantments.paper.api.objects.AllyMob.AllyType;
import com.badbones69.crazyenchantments.paper.api.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AllyManager {

    private final List<AllyMob> allyMobs = new ArrayList<>();
    private final Map<UUID, List<AllyMob>> allyOwners = new HashMap<>();
    private final Map<AllyType, String> allyTypeNameCache = new HashMap<>();
    
    public void load() {
        FileConfiguration config = Files.CONFIG.getFile();
        String allyTypePath = "Settings.EnchantmentOptions.Ally-Mobs.";

        for (AllyType type : AllyType.values()) {
            allyTypeNameCache.put(type, ColorUtils.color(config.getString(allyTypePath + type.getConfigName(), type.getDefaultName())));
        }
    }
    
    public List<AllyMob> getAllyMobs() {
        return allyMobs;
    }
    
    public void addAllyMob(AllyMob allyMob) {
        if (allyMob != null) {
            allyMobs.add(allyMob);
            UUID owner = allyMob.getOwner().getUniqueId();

            if (allyOwners.containsKey(owner)) {
                allyOwners.get(owner).add(allyMob);
            } else {
                List<AllyMob> allies = new ArrayList<>();
                allies.add(allyMob);
                allyOwners.put(owner, allies);
            }
        }
    }
    
    public void removeAllyMob(AllyMob allyMob) {
        if (allyMob != null) {
            allyMobs.remove(allyMob);
            UUID owner = allyMob.getOwner().getUniqueId();

            if (allyOwners.containsKey(owner)) {
                allyOwners.get(owner).add(allyMob);

                if (allyOwners.get(owner).isEmpty()) allyOwners.remove(owner);
            }
        }
    }
    
    public void forceRemoveAllies() {
        if (!allyMobs.isEmpty()) {
            allyMobs.forEach(ally -> ally.getAlly().remove());
            allyMobs.clear();
            allyOwners.clear();
        }
    }
    
    public void forceRemoveAllies(Player owner) {
        for (AllyMob ally : allyOwners.getOrDefault(owner.getUniqueId(), new ArrayList<>())) {
            ally.getAlly().remove();
            allyMobs.remove(ally);
        }

        allyOwners.remove(owner.getUniqueId());
    }
    
    public void setEnemy(Player owner, Entity enemy) {
        allyOwners.getOrDefault(owner.getUniqueId(), new ArrayList<>()).forEach(ally -> ally.attackEnemy((LivingEntity) enemy));
    }
    
    public Map<AllyType, String> getAllyTypeNameCache() {
        return allyTypeNameCache;
    }
    
    public boolean isAlly(Player player, Entity livingEntity) {
        if (isAllyMob(livingEntity)) return isAlly(player, getAllyMob(livingEntity));

        return false;
    }
    
    public boolean isAlly(Player player, AllyMob ally) {
        return ally.getOwner().getUniqueId() == player.getUniqueId();
    }
    
    public boolean isAllyMob(Entity livingEntity) {
        for (AllyMob ally : allyMobs) {
            if (ally.getAlly().getUniqueId() == livingEntity.getUniqueId()) return true;
        }

        return false;
    }
    
    public AllyMob getAllyMob(Entity livingEntity) {
        for (AllyMob ally : allyMobs) {
            if (ally.getAlly().getUniqueId() == livingEntity.getUniqueId()) return ally;
        }

        return null;
    }
}