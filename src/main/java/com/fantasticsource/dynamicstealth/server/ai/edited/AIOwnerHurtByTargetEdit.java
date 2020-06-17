package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.compat.Compat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.passive.EntityTameable;

public class AIOwnerHurtByTargetEdit extends AITargetEdit
{
    EntityTameable tameable;
    EntityLivingBase ownerEnemy;
    private int timestamp;

    public AIOwnerHurtByTargetEdit(EntityAIOwnerHurtByTarget oldAI)
    {
        super(oldAI);
        tameable = (EntityTameable) attacker;
        setMutexBits(1);
    }

    @Override
    public boolean shouldExecute()
    {
        if (!tameable.isTamed()) return false;

        EntityLivingBase owner = tameable.getOwner();
        if (owner == null) return false;

        ownerEnemy = owner.getRevengeTarget();
        return owner.getRevengeTimer() != timestamp && isSuitableTarget(ownerEnemy) && tameable.shouldAttackEntity(ownerEnemy, owner);
    }

    @Override
    public void startExecuting()
    {
        if (ownerEnemy == null) Compat.clearAttackTargetAndReplaceAITasks(attacker);
        else attacker.setAttackTarget(ownerEnemy);

        EntityLivingBase owner = tameable.getOwner();
        if (owner != null) timestamp = owner.getRevengeTimer();

        super.startExecuting();
    }
}