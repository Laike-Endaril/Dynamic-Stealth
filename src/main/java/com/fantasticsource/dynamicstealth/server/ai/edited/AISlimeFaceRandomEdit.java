package com.fantasticsource.dynamicstealth.server.ai.edited;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntitySlime;

import java.util.Random;

public class AISlimeFaceRandomEdit extends EntityAIBase
{
    public final EntitySlime slime;
    public float dirPrevious, dirChange;
    public int dirChangeMin = 10, dirChangeMax = 40, dirChangeRange = dirChangeMax - dirChangeMin;
    public int timer, timeMin = 5, timeMax = 40, timeRange = timeMax - timeMin;
    public int timer2, turnTime = 60;


    public AISlimeFaceRandomEdit(EntitySlime slimeIn)
    {
        slime = slimeIn;
        timer = timeMin + slime.getRNG().nextInt(timeRange);

        setMutexBits(2);
    }

    @Override
    public boolean shouldExecute()
    {
        if (timer > 0) timer--;
        return (timer <= 0 && slime.getAttackTarget() == null && (slime.onGround || slime.isInWater() || slime.isInLava()));
    }

    @Override
    public void startExecuting()
    {
        Random random = slime.getRNG();
        dirPrevious = slime.rotationYaw;
        if (dirChangeRange == 0) dirChange = dirChangeMin;
        else
        {
            dirChange = dirChangeMin + random.nextInt(dirChangeRange);
            if (random.nextBoolean()) dirChange = -dirChange;
        }
        timer2 = 0;
    }

    @Override
    public void updateTask()
    {
        if (slime.onGround || slime.isInWater() || slime.isInLava())
        {
            if (++timer2 <= turnTime)
            {
                setDirection((int) calcDir(dirPrevious, dirChange, (float) timer2 / turnTime), false);
            }
            else resetTimer();
        }
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return timer2 <= turnTime;
    }

    public void setDirection(float direction, boolean resetTimer)
    {
        slime.rotationYaw = direction;
        slime.rotationYawHead = direction;
        slime.renderYawOffset = direction;

        ((EntitySlime.SlimeMoveHelper) slime.getMoveHelper()).setDirection(direction, false);

        if (resetTimer) resetTimer();
    }

    public double calcDir(float start, float goalChange, float progressNormalized)
    {
        return start + goalChange * progressNormalized;
//        return start + goalChange * (0.5 + 0.707 * DynamicStealth.TRIG_TABLE.sin(progressNormalized * 1.5 * Math.PI - 0.75 * Math.PI));
    }

    public void resetTimer()
    {
        timer = timeMin + slime.getRNG().nextInt(timeRange);
    }
}
