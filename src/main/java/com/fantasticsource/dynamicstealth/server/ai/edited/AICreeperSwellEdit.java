package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.compat.Compat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.math.Vec3d;

public class AICreeperSwellEdit extends EntityAIBase
{
    EntityCreeper creeper;
    EntityLivingBase target;
    Vec3d lastPos = null;

    public AICreeperSwellEdit(EntityCreeper creeper)
    {
        this.creeper = creeper;

        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute()
    {
        target = creeper.getAttackTarget();
        if (AITargetEdit.isSuitableTarget(creeper, target) && creeper.getDistanceSq(target) < 9) return true;
        else
        {
            target = null;
            return false;
        }
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if (target == null || !target.isEntityAlive()) return false;

        if (AITargetEdit.isSuitableTarget(creeper, target))
        {
            creeper.getMoveHelper().setMoveTo(target.posX, target.posY, target.posZ, 0);
            creeper.getLookHelper().setLookPositionWithEntity(target, 30, 30);

            lastPos = target.getPositionVector();
            return creeper.getDistanceSq(target) <= 49;
        }

        return (lastPos != null && lastPos.squareDistanceTo(creeper.getPositionVector()) < 9); //Creeper will keep exploding if you were within 3 blocks last it saw you!
    }

    @Override
    public void startExecuting()
    {
        creeper.getNavigator().clearPath();
        creeper.setCreeperState(1);
    }

    @Override
    public void resetTask()
    {
        if (!AITargetEdit.isSuitableTarget(creeper, target)) Compat.clearAttackTargetAndReplaceAITasks(creeper);


        target = null;
        creeper.setCreeperState(-1);
    }
}
