package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.server.ai.EntityAIData;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class AIEntityLookIdleEdit extends EntityAIBase
{
    protected EntityLiving idleEntity;
    protected double lookX, lookZ;
    protected int idleTime, headTurnSpeed;


    public AIEntityLookIdleEdit(EntityLiving idleEntity)
    {
        this.setMutexBits(3);

        this.idleEntity = idleEntity;
        headTurnSpeed = EntityAIData.headTurnSpeed(idleEntity);
    }

    public boolean shouldExecute()
    {
        return idleEntity.getAttackTarget() == null && idleEntity.getRNG().nextFloat() < 0.02F;
    }

    public boolean shouldContinueExecuting()
    {
        return idleEntity.getAttackTarget() == null && idleTime >= 0;
    }

    public void startExecuting()
    {
        double yaw = Math.PI * 2 * idleEntity.getRNG().nextDouble();
        lookX = Math.cos(yaw);
        lookZ = Math.sin(yaw);
        idleTime = 20 + idleEntity.getRNG().nextInt(20);
    }

    public void updateTask()
    {
        idleTime--;
        idleEntity.getLookHelper().setLookPosition(idleEntity.posX + lookX, idleEntity.posY + idleEntity.getEyeHeight(), idleEntity.posZ + lookZ, headTurnSpeed, headTurnSpeed);
    }
}
