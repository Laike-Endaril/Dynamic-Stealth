package com.fantasticsource.dynamicstealth.newai;

import com.fantasticsource.dynamicstealth.ai.AITargetEdit;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class AIStoreKnownPosition extends EntityAIBase
{
    public final EntityLiving searcher;
    public BlockPos lastKnownPosition = null;
    public EntityLivingBase target = null;

    public AIStoreKnownPosition(EntityLiving living)
    {
        searcher = living;

        setMutexBits(0);
    }

    @Override
    public boolean shouldExecute()
    {
        EntityLivingBase target = searcher.getAttackTarget();
        if (target == null) return false;

        BlockPos targetPos = target.getPosition();
        if (targetPos.equals(lastKnownPosition)) return false;

        if (AITargetEdit.isSuitableTarget(searcher, target))
        {
            this.target = target;
            lastKnownPosition = targetPos;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return target != null && lastKnownPosition != null;
    }

    @Override
    public void resetTask()
    {
        target = null;
        lastKnownPosition = null;
    }
}
