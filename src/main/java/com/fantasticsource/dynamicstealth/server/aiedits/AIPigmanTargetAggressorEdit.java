package com.fantasticsource.dynamicstealth.server.aiedits;

import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;

public class AIPigmanTargetAggressorEdit extends AINearestAttackableTargetEdit<EntityPlayer>
{
    public AIPigmanTargetAggressorEdit(EntityAINearestAttackableTarget oldAI) throws IllegalAccessException
    {
        super(oldAI);
    }

    @Override
    public boolean shouldExecute()
    {
        return ((EntityPigZombie) attacker).isAngry() && super.shouldExecute();
    }
}
