package com.fantasticsource.dynamicstealth.server.ai.edited;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityVindicator;

public class AIJohnnyAttackEdit extends AINearestAttackableTargetEdit<EntityLivingBase>
{
    public AIJohnnyAttackEdit(EntityAINearestAttackableTarget oldAI)
    {
        super(oldAI);
    }

    @Override
    public boolean shouldExecute()
    {
        return ((EntityVindicator) attacker).johnny && super.shouldExecute();
    }
}
