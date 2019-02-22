package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.AxisAlignedBB;

import java.lang.reflect.Field;

public class AIHurtByTargetEdit extends AITargetEdit
{
    private static Field entityCallsForHelpField, excludedReinforcementTypesField;

    static
    {
        try
        {
            entityCallsForHelpField = ReflectionTool.getField(EntityAIHurtByTarget.class, "field_75312_a", "entityCallsForHelp");
            excludedReinforcementTypesField = ReflectionTool.getField(EntityAIHurtByTarget.class, "field_179447_c", "excludedReinforcementTypes");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            MCTools.crash(e, 129, false);
        }
    }


    private final boolean entityCallsForHelp;
    private final Class<?>[] excludedReinforcementTypes;
    private int revengeTimerOld;

    public AIHurtByTargetEdit(EntityAIHurtByTarget oldAI) throws IllegalAccessException
    {
        super(oldAI);
        entityCallsForHelp = (boolean) entityCallsForHelpField.get(oldAI);
        excludedReinforcementTypes = (Class<?>[]) excludedReinforcementTypesField.get(oldAI);
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
        attacker.setAttackTarget(attacker.getRevengeTarget());
        target = attacker.getAttackTarget();
        revengeTimerOld = attacker.getRevengeTimer();

        if (entityCallsForHelp) alertOthers();

        super.startExecuting();
    }

    protected void alertOthers()
    {
        double followDistance = getFollowDistance();

        for (EntityCreature entitycreature : attacker.world.getEntitiesWithinAABB(attacker.getClass(), (new AxisAlignedBB(attacker.posX, attacker.posY, attacker.posZ, attacker.posX + 1, attacker.posY + 1, attacker.posZ + 1)).grow(followDistance, 10, followDistance)))
        {
            if (attacker != entitycreature && entitycreature.getAttackTarget() == null && (!(attacker instanceof EntityTameable) || ((EntityTameable) attacker).getOwner() == ((EntityTameable) entitycreature).getOwner()) && !entitycreature.isOnSameTeam(attacker.getRevengeTarget()))
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
        attacker.setAttackTarget(target);
        attacker.getMoveHelper().setMoveTo(target.posX, target.posY, target.posZ, attacker.getMoveHelper().getSpeed());
        attacker.getLookHelper().setLookPositionWithEntity(target, 180, 180);
    }
}