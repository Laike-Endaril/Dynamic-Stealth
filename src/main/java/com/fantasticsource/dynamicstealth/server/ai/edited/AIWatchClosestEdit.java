package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.compat.Compat;
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
    public EntityLiving living;
    public Entity target;
    public int lookTime;
    public Class<? extends Entity> watchedClass;

    public AIWatchClosestEdit(EntityAIWatchClosest oldAI)
    {
        living = oldAI.entity;
        watchedClass = oldAI.watchedClass;
        chance = oldAI.chance;
        setMutexBits(2);
    }

    public AIWatchClosestEdit(EntityAIWatchClosest oldAI, boolean isEntityAIWatchClosest2)
    {
        this(oldAI);
        if (isEntityAIWatchClosest2) setMutexBits(3);
    }

    public AIWatchClosestEdit(EntityLiving living, Class<? extends Entity> watchedClass, float chance)
    {
        this.living = living;
        this.watchedClass = watchedClass;
        this.chance = chance;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        if (living.getRNG().nextFloat() >= chance) return false;

        if (living.getAttackTarget() != null)
        {
            target = living.getAttackTarget();
            if (AITargetEdit.isSuitableTarget(living, (EntityLivingBase) target)) return true;

            target = null;
            Compat.clearAttackTargetAndCancelBadTasks(living);
            return false;
        }

        List<Entity> list;
        ExplicitPriorityQueue<Entity> queue;
        double range = EntitySightData.distanceFar(living);
        if (watchedClass == EntityPlayer.class)
        {
            list = living.world.getEntitiesWithinAABB(EntityPlayer.class, living.getEntityBoundingBox().grow(range, 4D, range));
        }
        else
        {
            list = living.world.getEntitiesWithinAABB(watchedClass, living.getEntityBoundingBox().grow(range, 3, range), Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.notRiding(living)));
        }

        if (list.isEmpty()) return false;

        queue = new ExplicitPriorityQueue<>(list.size());
        for (Entity e : list)
        {
            if (living != e) queue.add(e, living.getDistanceSq(e));
        }

        target = queue.poll();
        while (target != null && !Sight.canSee(living, target, false)) //Doesn't need isSuitableTarget because it's not always used for attacking
        {
            target = queue.poll();
        }

        return target != null;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return (target.isEntityAlive() && Sight.canSee(living, target, false) && lookTime > 0);
    }

    @Override
    public void startExecuting()
    {
        lookTime = 40 + living.getRNG().nextInt(40);
    }

    @Override
    public void resetTask()
    {
        target = null;
    }

    @Override
    public void updateTask()
    {
        living.getLookHelper().setLookPosition(target.posX, target.posY + target.getEyeHeight(), target.posZ, living.getHorizontalFaceSpeed(), living.getVerticalFaceSpeed());
        --lookTime;
    }
}