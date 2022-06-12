package com.badbones69.crazyenchantments.multisupport.misc.mobstacker

import org.bukkit.entity.LivingEntity
import uk.antiperson.stackmob.StackMob
import uk.antiperson.stackmob.entity.EntityManager

/**
 * @author https://www.spigotmc.org/resources/stackmob-enhance-your-servers-performance-without-the-sacrifice.29999/
 */
class StackMobAntiSupport {

    fun preventStacking(entity: LivingEntity) {

        val entityManager = EntityManager(StackMob())

        entityManager.unregisterStackedEntity(entity)

    }
}