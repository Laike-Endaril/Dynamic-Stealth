package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.server.senses.sight.EntitySightData;
import com.fantasticsource.dynamicstealth.server.senses.sight.Sight;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;

import java.util.List;

public class AIWatchClosestEdit extends EntityAIBase
{
    public final float chance;
    public EntityLiving entity;
    public Entity target;
    public int lookTime;
    public Class<? extends Entity> watchedClass;

    public AIWatchClosestEdit(EntityAIWatchClosest oldAI)
    {
        entity = oldAI.entity;
        watchedClass = oldAI.watchedClass;
        chance = oldAI.chance;
        setMutexBits(2);
    }

    public AIWatchClosestEdit(EntityAIWatchClosest oldAI, boolean isEntityAIWatchClosest2)
    {
        this(oldAI);
        if (isEntityAIWatchClosest2) setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        if (entity.getRNG().nextFloat() >= chance) return false;

        if (entity.getAttackTarget() != null)
        {
            target = entity.getAttackTarget();
            if (AITargetEdit.isSuitableTarget(entity, (EntityLivingBase) target)) return true;

            target = null;
            entity.setAttackTarget(null);
            return false;
        }

        List<Entity> list;
        ExplicitPriorityQueue<Entity> queue;
        double range = EntitySightData.distanceFar(entity);
        if (watchedClass == EntityPlayer.class)
        {
            list = entity.world.getEntitiesWithinAABB(EntityPlayer.class, entity.getEntityBoundingBox().grow(range, 4D, range));
        }
        else
        {
            list = entity.world.getEntitiesWithinAABB(watchedClass, entity.getEntityBoundingBox().grow(range, 3, range), Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.notRiding(entity)));
        }

        if (list.isEmpty()) return false;

        queue = new ExplicitPriorityQueue<>(list.size());
        for (Entity e : list)
        {
            if (entity != e) queue.add(e, entity.getDistanceSq(e));
        }

        target = queue.poll();
        while (target != null && !Sight.canSee(entity, target, false)) //Doesn't need isSuitableTarget because it's not always used for attacking
        {
            target = queue.poll();
        }

        return target != null;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return (target.isEntityAlive() && Sight.canSee(entity, target, false) && lookTime > 0);
    }

    @Override
    public void startExecuting()
    {
        lookTime = 40 + entity.getRNG().nextInt(40);
    }

    @Override
    public void resetTask()
    {
        target = null;
    }

    @Override
    public void updateTask()
    {
        entity.getLookHelper().setLookPosition(target.posX, target.posY + target.getEyeHeight(), target.posZ, entity.getHorizontalFaceSpeed(), entity.getVerticalFaceSpeed());
        --lookTime;
    }
}