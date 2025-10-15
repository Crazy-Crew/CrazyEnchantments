package com.badbones69.crazyenchantments.paper.api.managers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.api.enums.FileKeys;
import com.badbones69.crazyenchantments.paper.api.objects.AllyMob;
import com.badbones69.crazyenchantments.paper.api.objects.AllyMob.AllyType;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AllyManager {

    @NotNull
    private final CrazyEnchantments plugin = JavaPlugin.getPlugin(CrazyEnchantments.class);
    private final List<AllyMob> allyMobs = new ArrayList<>();
    private final Map<UUID, List<AllyMob>> allyOwners = new HashMap<>();
    private final Map<AllyType, String> allyTypeNameCache = new HashMap<>();
    
    public void load() {
        YamlConfiguration configuration = FileKeys.config.getPaperConfiguration();

        String allyTypePath = "Settings.EnchantmentOptions.Ally-Mobs.";

        for (AllyType type : AllyType.values()) {
            //this.allyTypeNameCache.put(type, ColorUtils.color(configuration.getString(allyTypePath + type.getConfigName(), type.getDefaultName()))); //todo() legacy trash
        }
    }
    
    public List<AllyMob> getAllyMobs() {
        return this.allyMobs;
    }
    
    public void addAllyMob(@Nullable final AllyMob allyMob) {
        if (allyMob != null) {
            this.allyMobs.add(allyMob);

            UUID owner = allyMob.getOwner().getUniqueId();

            if (this.allyOwners.containsKey(owner)) {
                this.allyOwners.get(owner).add(allyMob);
            } else {
                List<AllyMob> allies = new ArrayList<>();
                allies.add(allyMob);
                this.allyOwners.put(owner, allies);
            }
        }
    }
    
    public void removeAllyMob(@Nullable final AllyMob allyMob) {
        if (allyMob != null) {
            this.allyMobs.remove(allyMob);
            UUID owner = allyMob.getOwner().getUniqueId();

            if (this.allyOwners.containsKey(owner)) {
                this.allyOwners.get(owner).add(allyMob);

                if (this.allyOwners.get(owner).isEmpty()) this.allyOwners.remove(owner);
            }
        }
    }

    public void forceRemoveAllies() {
        if (!this.allyMobs.isEmpty()) {
            for (final AllyMob ally : this.allyMobs) {
                final LivingEntity entity = ally.getAlly();

                new FoliaScheduler(this.plugin, null, entity) {
                    @Override
                    public void run() {
                        entity.remove();
                    }
                }.runNextTick();
            }

            this.allyMobs.clear();
            this.allyOwners.clear();
        }
    }

    public void forceRemoveAllies(@NotNull final Player owner) {
        for (final AllyMob ally : this.allyOwners.getOrDefault(owner.getUniqueId(), new ArrayList<>())) {
            final LivingEntity entity = ally.getAlly();

            new FoliaScheduler(this.plugin, null, entity) {
                @Override
                public void run() {
                    entity.remove();

                    allyMobs.remove(ally);
                }
            }.runNextTick();
        }

        this.allyOwners.remove(owner.getUniqueId());
    }

    public void setEnemy(@NotNull final Player owner, @NotNull final Entity enemy) {
        this.allyOwners.getOrDefault(owner.getUniqueId(), new ArrayList<>()).forEach(ally -> {
            new FoliaScheduler(this.plugin, null, ally.getAlly()) {
                @Override
                public void run() {
                    ally.attackEnemy((LivingEntity) enemy);
                }
            }.runNextTick();
        });
    }
    
    public Map<AllyType, String> getAllyTypeNameCache() {
        return this.allyTypeNameCache;
    }
    
    public boolean isAlly(@NotNull final Player player, @NotNull final Entity livingEntity) {
        if (isAllyMob(livingEntity)) return isAlly(player, getAllyMob(livingEntity));

        return false;
    }
    
    public boolean isAlly(@NotNull final Player player, @NotNull final AllyMob ally) {
        return ally.getOwner().getUniqueId() == player.getUniqueId();
    }
    
    public boolean isAllyMob(@NotNull final Entity livingEntity) {
        for (AllyMob ally : this.allyMobs) {
            if (ally.getAlly().getUniqueId() == livingEntity.getUniqueId()) return true;
        }

        return false;
    }
    
    public AllyMob getAllyMob(@NotNull final Entity livingEntity) {
        for (AllyMob ally : this.allyMobs) {
            if (ally.getAlly().getUniqueId() == livingEntity.getUniqueId()) return ally;
        }

        return null;
    }
}