package com.fantasticsource.dynamicstealth.server.aiedits;

import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.monster.EntityZombie;

public class AIZombieAttackEdit extends AIAttackMeleeEdit
{
    private final EntityZombie zombie;
    private int raiseArmTicks;

    public AIZombieAttackEdit(EntityAIAttackMelee oldAI) throws IllegalAccessException
    {
        super(oldAI);
        zombie = (EntityZombie) attacker;
    }

    @Override
    public void startExecuting()
    {
        super.startExecuting();
        this.raiseArmTicks = 0;
    }

    @Override
    public void resetTask()
    {
        super.resetTask();
        this.zombie.setArmsRaised(false);
    }

    @Override
    public void updateTask()
    {
        super.updateTask();
        ++this.raiseArmTicks;

        if (this.raiseArmTicks >= 5 && this.attackTick < 10)
        {
            this.zombie.setArmsRaised(true);
        }
        else
        {
            this.zombie.setArmsRaised(false);
        }
    }
}