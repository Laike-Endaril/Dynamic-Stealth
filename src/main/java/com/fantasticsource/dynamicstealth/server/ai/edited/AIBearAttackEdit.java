package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.monster.EntityPolarBear;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AIBearAttackEdit extends AIAttackMeleeEdit
{
    static Method playWarningSoundMethod;

    static
    {
        playWarningSoundMethod = ReflectionTool.getMethod(EntityPolarBear.class, "func_189796_de", "playWarningSound");
    }


    EntityPolarBear bear;

    public AIBearAttackEdit(EntityAIAttackMelee oldAI) throws IllegalAccessException
    {
        super(oldAI);
        bear = (EntityPolarBear) attacker;
    }

    @Override
    protected void checkAndPerformAttack(EntityLivingBase target, double distSquared)
    {
        double reachSquared = getAttackReachSqr(target);

        if (distSquared <= reachSquared && attackTick <= 0)
        {
            attackTick = attackInterval;
            bear.attackEntityAsMob(target);
            bear.setStanding(false);
        }
        else if (distSquared <= reachSquared * 2.0D)
        {
            if (attackTick <= 0)
            {
                bear.setStanding(false);
                attackTick = attackInterval;
            }

            if (attackTick <= 10)
            {
                bear.setStanding(true);
                try
                {
                    playWarningSoundMethod.invoke(bear);
                }
                catch (IllegalAccessException | InvocationTargetException e)
                {
                    MCTools.crash(e, 127, false);
                }
            }
        }
        else
        {
            attackTick = attackInterval;
            bear.setStanding(false);
        }
    }

    @Override
    public void resetTask()
    {
        bear.setStanding(false);
        super.resetTask();
    }

    @Override
    protected double getAttackReachSqr(EntityLivingBase attackTarget)
    {
        return 4 + attackTarget.width;
    }
}
