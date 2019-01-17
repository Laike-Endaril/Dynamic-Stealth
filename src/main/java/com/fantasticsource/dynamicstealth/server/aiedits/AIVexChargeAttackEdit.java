package com.fantasticsource.dynamicstealth.server.aiedits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class AIVexChargeAttackEdit extends EntityAIBase
{
    private EntityVex vex;
    private EntityLivingBase target;

    public AIVexChargeAttackEdit(EntityVex vex)
    {
        this.vex = vex;
        setMutexBits(1);
    }

    public boolean shouldExecute()
    {
        target = vex.getAttackTarget();
        return !vex.getMoveHelper().isUpdating() && vex.getRNG().nextInt(7) == 0 && AITargetEdit.isSuitableTarget(vex, target) && vex.getDistanceSq(target) > 4;
    }

    public boolean shouldContinueExecuting()
    {
        return vex.getMoveHelper().isUpdating() && vex.isCharging() && AITargetEdit.isSuitableTarget(vex, target);
    }

    public void startExecuting()
    {
        Vec3d vec3d = target.getPositionEyes(1);
        vex.getMoveHelper().setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1);
        vex.setCharging(true);
        vex.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1, 1);
    }

    public void resetTask()
    {
        vex.setCharging(false);
    }

    public void updateTask()
    {
        if (vex.getEntityBoundingBox().intersects(target.getEntityBoundingBox()))
        {
            vex.attackEntityAsMob(target);
            vex.setCharging(false);
        }
        else if (vex.getDistanceSq(target) < 9)
        {
            Vec3d vec3d = target.getPositionEyes(1);
            vex.getMoveHelper().setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1);
        }
    }
}
