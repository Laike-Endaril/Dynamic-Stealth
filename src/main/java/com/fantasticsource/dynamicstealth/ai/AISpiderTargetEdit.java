package com.fantasticsource.dynamicstealth.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

public class AISpiderTargetEdit<T extends EntityLivingBase> extends AINearestAttackableTargetEdit<T>
{
    public AISpiderTargetEdit(EntityAINearestAttackableTarget oldAI) throws IllegalAccessException
    {
        super(oldAI);
    }

    @Override
    public boolean shouldExecute()
    {
        return attacker.getBrightness() < 0.5 && super.shouldExecute();
    }
}
