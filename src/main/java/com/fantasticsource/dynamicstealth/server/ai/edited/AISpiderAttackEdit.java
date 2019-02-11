package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.server.threat.Threat;
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
        Threat.ThreatData data = Threat.get(attacker);
        if (data.threatLevel <= 0)
        {
            attacker.setAttackTarget(null);
            return false;
        }

        if (attacker.getBrightness() >= 0.5) Threat.setThreat(attacker, --data.threatLevel);
        return super.shouldContinueExecuting();
    }

    @Override
    protected double getAttackReachSqr(EntityLivingBase attackTarget)
    {
        return 4 + attackTarget.width;
    }
}
