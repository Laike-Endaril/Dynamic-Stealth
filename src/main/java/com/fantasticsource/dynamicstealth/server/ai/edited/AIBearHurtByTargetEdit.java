package com.fantasticsource.dynamicstealth.server.ai.edited;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.monster.EntityPolarBear;

public class AIBearHurtByTargetEdit extends AIHurtByTargetEdit
{
    public AIBearHurtByTargetEdit(EntityAIHurtByTarget oldAI) throws IllegalAccessException
    {
        super(oldAI);
    }

    @Override
    public void startExecuting()
    {
        super.startExecuting();

        if (attacker.isChild())
        {
            alertOthers();
            resetTask();
        }
    }

    @Override
    protected void setEntityAttackTarget(EntityCreature creatureIn, EntityLivingBase entityLivingBaseIn)
    {
        if (creatureIn instanceof EntityPolarBear && !creatureIn.isChild())
        {
            super.setEntityAttackTarget(creatureIn, entityLivingBaseIn);
        }
    }
}
