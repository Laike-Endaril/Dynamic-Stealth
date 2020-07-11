package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.compat.Compat;
import com.fantasticsource.dynamicstealth.server.threat.Threat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;

public class AISpiderAttackEdit extends AIAttackMeleeEdit
{
    public AISpiderAttackEdit(EntityAIAttackMelee oldAI)
    {
        super(oldAI);
        speedTowardsTarget = 1;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        Threat.ThreatData data = Threat.get(attacker);
        if (data.threatPercentage <= 0)
        {
            Compat.clearAttackTargetAndCancelBadTasks(attacker);
            return false;
        }

        if (attacker.getBrightness() >= 0.5) Threat.setThreat(attacker, --data.threatPercentage);
        return super.shouldContinueExecuting();
    }

    @Override
    protected double getAttackReachSqr(EntityLivingBase attackTarget)
    {
        return 4 + attackTarget.width;
    }
}
