package com.fantasticsource.dynamicstealth.server.ai.edited;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.monster.EntityPigZombie;

public class AIPigmanHurtByAggressorEdit extends AIHurtByTargetEdit
{
    public AIPigmanHurtByAggressorEdit(EntityAIHurtByTarget oldAI)
    {
        super(oldAI);
    }

    @Override
    protected void setEntityAttackTarget(EntityCreature attacker, EntityLivingBase target)
    {
        super.setEntityAttackTarget(attacker, target);

        if (attacker instanceof EntityPigZombie)
        {
            ((EntityPigZombie) attacker).becomeAngryAt(target);
        }
    }
}
