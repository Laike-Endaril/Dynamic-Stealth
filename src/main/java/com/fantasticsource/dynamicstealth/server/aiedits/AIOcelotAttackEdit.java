package com.fantasticsource.dynamicstealth.server.aiedits;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;

public class AIOcelotAttackEdit extends EntityAIBase
{
    EntityLiving entity;
    EntityLivingBase target;
    int attackCountdown;
    private Path path = null;

    public AIOcelotAttackEdit(EntityLiving entityIn)
    {
        entity = entityIn;

        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        EntityLivingBase testTarget = entity.getAttackTarget();
        if (AITargetEdit.isSuitableTarget(entity, testTarget))
        {
            target = testTarget;
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return shouldExecute();
    }

    @Override
    public void resetTask()
    {
        target = null;
        if (path != null && path.equals(entity.getNavigator().getPath())) entity.getNavigator().clearPath();
        path = null;
    }

    @Override
    public void updateTask()
    {
        entity.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        double reachSquared = entity.width * entity.width * 4;
        double distSquared = entity.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
        double speed = 0.8D;

        if (distSquared > reachSquared && distSquared < 16)
        {
            speed = 1.33D;
        }
        else if (distSquared < 225)
        {
            speed = 0.6D;
        }

        path = entity.getNavigator().getPathToEntityLiving(target);
        entity.getNavigator().setPath(path, speed);

        if (attackCountdown > 0) attackCountdown--;

        if (distSquared <= reachSquared)
        {
            if (attackCountdown <= 0)
            {
                attackCountdown = 20;
                entity.attackEntityAsMob(target);
            }
        }
    }
}