package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.server.threat.Threat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

public class AISpiderTargetEdit<T extends EntityLivingBase> extends AINearestAttackableTargetEdit<T>
{
    public AISpiderTargetEdit(EntityAINearestAttackableTarget oldAI)
    {
        super(oldAI);
    }

    @Override
    public boolean shouldExecute()
    {
        return (Threat.getThreat(attacker) > 0 || attacker.getBrightness() < 0.5) && super.shouldExecute();
    }
}
