package com.fantasticsource.dynamicstealth.server.ai.edited;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.MathHelper;

public class AIAttackRangedEdit extends EntityAIBase
{
    private final EntityLiving attacker;
    private final IRangedAttackMob rangedAttacker;
    private final double entityMoveSpeed;
    private final int attackIntervalMin, attackIntervalRange;
    private final float attackRadius;
    private final float attackRadiusSquared;
    private EntityLivingBase target;
    private int timer;
    private int seeTime;
    private Path path = null;

    public AIAttackRangedEdit(EntityAIAttackRanged oldAI)
    {
        rangedAttacker = oldAI.rangedAttackEntityHost;
        entityMoveSpeed = oldAI.entityMoveSpeed;
        attackIntervalMin = oldAI.attackIntervalMin;
        attackRadius = oldAI.attackRadius;

        attackIntervalRange = oldAI.maxRangedAttackTime - attackIntervalMin;

        attackRadiusSquared = attackRadius * attackRadius;
        attacker = (EntityLiving) rangedAttacker;
        timer = -1;

        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        EntityLivingBase testTarget = attacker.getAttackTarget();
        if (AITargetEdit.isSuitableTarget(attacker, testTarget))
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
        seeTime = 0;
        timer = -1;

        if (path != null && path.equals(attacker.getNavigator().getPath())) attacker.getNavigator().clearPath();
        path = null;
    }

    @Override
    public void updateTask()
    {
        double distSquared = attacker.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);

        seeTime++;

        if (distSquared <= attackRadiusSquared && seeTime >= 20)
        {
            if (path != null && path.equals(attacker.getNavigator().getPath())) attacker.getNavigator().clearPath();
            path = null;
        }
        else
        {
            path = attacker.getNavigator().getPathToEntityLiving(target);
            attacker.getNavigator().setPath(path, entityMoveSpeed);
        }

        attacker.getLookHelper().setLookPositionWithEntity(target, 30, 30);

        if (--timer == 0)
        {
            float distanceFactor = MathHelper.sqrt(distSquared) / attackRadius;
            rangedAttacker.attackEntityWithRangedAttack(target, MathHelper.clamp(distanceFactor, 0.1F, 1));
            timer = MathHelper.floor(attackIntervalMin + distanceFactor * attackIntervalRange);
        }
        else if (timer < 0)
        {
            float distanceFactor = MathHelper.sqrt(distSquared) / attackRadius;
            timer = MathHelper.floor(attackIntervalMin + distanceFactor * attackIntervalRange);
        }
    }
}