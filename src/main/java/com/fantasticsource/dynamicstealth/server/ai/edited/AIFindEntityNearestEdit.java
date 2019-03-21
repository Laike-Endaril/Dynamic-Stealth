package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.server.senses.sight.EntitySightData;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class AIFindEntityNearestEdit extends EntityAIBase
{
    public static Logger LOGGER = LogManager.getLogger();

    public EntityLiving searcher;
    public Class<? extends EntityLivingBase> targetClass;
    public EntityLivingBase target;


    public AIFindEntityNearestEdit(EntityAIFindEntityNearest oldAI)
    {
        searcher = oldAI.mob;
        targetClass = oldAI.classToCheck;

        if (searcher instanceof EntityCreature) LOGGER.warn("Use NearestAttackableTargetGoal.class for PathfinderMob mobs!");
    }

    @Override
    public boolean shouldExecute()
    {
        double range = EntitySightData.distanceFar(searcher);
        List<EntityLivingBase> list = searcher.world.getEntitiesWithinAABB(targetClass, searcher.getEntityBoundingBox().grow(range, 4, range));
        if (list.isEmpty()) return false;

        ExplicitPriorityQueue<EntityLivingBase> queue = new ExplicitPriorityQueue<>(list.size());
        for (EntityLivingBase entity : list)
        {
            if (entity != searcher) queue.add(entity, entity.getDistanceSq(searcher));
        }

        target = queue.poll();
        while (target != null && !AITargetEdit.isSuitableTarget(searcher, target)) target = queue.poll();
        return target != null;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return AITargetEdit.isSuitableTarget(searcher, searcher.getAttackTarget());
    }

    @Override
    public void startExecuting()
    {
        searcher.setAttackTarget(target);
        super.startExecuting();
    }

    @Override
    public void resetTask()
    {
        searcher.setAttackTarget(null);
        super.startExecuting();
    }
}