package com.fantasticsource.dynamicstealth.server.ai.edited;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;

public class AISpiderAttackEdit extends AIAttackMeleeEdit
{
    public AISpiderAttackEdit(EntityAIAttackMelee oldAI) throws IllegalAccessException
    {
        super(oldAI);
        speed = 1;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        float f = attacker.getBrightness();

        if (f >= 0.5 && attacker.getRNG().nextInt(100) == 0)
        {
            attacker.setAttackTarget(null);
            return false;
        }

        return super.shouldContinueExecuting();
    }

    @Override
    protected double getAttackReachSqr(EntityLivingBase attackTarget)
    {
        return 4 + attackTarget.width;
    }
}
