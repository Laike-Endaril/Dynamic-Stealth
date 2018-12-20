package com.fantasticsource.dynamicstealth.ai;

import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.passive.EntityTameable;

public class AITargetNonTamedEdit extends AINearestAttackableTargetEdit
{
    private final EntityTameable tameable;

    public AITargetNonTamedEdit(EntityAITargetNonTamed oldAI) throws IllegalAccessException
    {
        super(oldAI);
        tameable = (EntityTameable) attacker;
        targetChance = 10;
    }

    @Override
    public boolean shouldExecute()
    {
        return !tameable.isTamed() && super.shouldExecute();
    }
}