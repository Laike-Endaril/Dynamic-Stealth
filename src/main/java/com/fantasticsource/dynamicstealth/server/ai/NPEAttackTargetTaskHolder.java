package com.fantasticsource.dynamicstealth.server.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class NPEAttackTargetTaskHolder extends EntityAIBase
{
    private EntityLiving entity;
    private EntityAIBase badAI;

    public NPEAttackTargetTaskHolder(EntityLiving entity, EntityAIBase badAI)
    {
        this.entity = entity;
        this.badAI = badAI;

        setMutexBits(badAI.getMutexBits());
    }

    @Override
    public boolean shouldExecute()
    {
        return entity.getAttackTarget() != null && badAI.shouldExecute();
    }

    @Override
    public void startExecuting()
    {
        if (entity.getAttackTarget() != null) badAI.startExecuting();
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return entity.getAttackTarget() != null && badAI.shouldContinueExecuting();
    }

    @Override
    public void updateTask()
    {
        if (entity.getAttackTarget() != null) badAI.updateTask();
    }

    @Override
    public boolean isInterruptible()
    {
        return badAI.isInterruptible();
    }

    @Override
    public int getMutexBits()
    {
        return badAI.getMutexBits();
    }

    @Override
    public void resetTask()
    {
        badAI.resetTask();
    }
}
