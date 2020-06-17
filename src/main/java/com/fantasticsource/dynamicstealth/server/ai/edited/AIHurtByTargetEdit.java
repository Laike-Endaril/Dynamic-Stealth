package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.compat.Compat;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.AxisAlignedBB;

public class AIHurtByTargetEdit extends AITargetEdit
{
    private final boolean entityCallsForHelp;
    private final Class<?>[] excludedReinforcementTypes;
    private int revengeTimerOld;

    public AIHurtByTargetEdit(EntityAIHurtByTarget oldAI)
    {
        super(oldAI);
        entityCallsForHelp = oldAI.entityCallsForHelp;
        excludedReinforcementTypes = oldAI.excludedReinforcementTypes;
        setMutexBits(1);
    }

    @Override
    public boolean shouldExecute()
    {
        int i = attacker.getRevengeTimer();
        EntityLivingBase revengeTarget = attacker.getRevengeTarget();
        return i != revengeTimerOld && revengeTarget != null && isSuitableTarget(revengeTarget);
    }

    @Override
    public void startExecuting()
    {
        target = attacker.getRevengeTarget();
        if (target == null) Compat.clearAttackTargetAndReplaceAITasks(attacker);
        else attacker.setAttackTarget(target);
        revengeTimerOld = attacker.getRevengeTimer();

        if (entityCallsForHelp) alertOthers();

        super.startExecuting();
    }

    protected void alertOthers()
    {
        double followDistance = getFollowDistance();

        for (EntityCreature entitycreature : attacker.world.getEntitiesWithinAABB(attacker.getClass(), (new AxisAlignedBB(attacker.posX, attacker.posY, attacker.posZ, attacker.posX + 1, attacker.posY + 1, attacker.posZ + 1)).grow(followDistance, 10, followDistance)))
        {
            EntityLivingBase revengeTarget = attacker.getRevengeTarget();
            if (revengeTarget != null && attacker != entitycreature && entitycreature.getAttackTarget() == null && (!(attacker instanceof EntityTameable) || ((EntityTameable) attacker).getOwner() == ((EntityTameable) entitycreature).getOwner()) && !entitycreature.isOnSameTeam(attacker.getRevengeTarget()))
            {
                boolean shouldAttack = true;
                for (Class<?> excludedClass : excludedReinforcementTypes)
                {
                    if (entitycreature.getClass() == excludedClass)
                    {
                        shouldAttack = false;
                        break;
                    }
                }

                if (shouldAttack) setEntityAttackTarget(entitycreature, attacker.getRevengeTarget());
            }
        }
    }

    protected void setEntityAttackTarget(EntityCreature attacker, EntityLivingBase target)
    {
        if (target == null) Compat.clearAttackTargetAndReplaceAITasks(attacker);
        else attacker.setAttackTarget(target);
        attacker.getMoveHelper().setMoveTo(target.posX, target.posY, target.posZ, attacker.getMoveHelper().getSpeed());
        attacker.getLookHelper().setLookPositionWithEntity(target, 180, 180);
    }
}