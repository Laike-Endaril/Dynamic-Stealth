package com.fantasticsource.dynamicstealth.server.ai.edited;

import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.passive.EntityTameable;

public class AITargetNonTamedEdit extends AINearestAttackableTargetEdit
{
    private final EntityTameable tameable;

    public AITargetNonTamedEdit(EntityAITargetNonTamed oldAI)
    {
        super(oldAI);
        tameable = (EntityTameable) attacker;
    }

    @Override
    public boolean shouldExecute()
    {
        return !tameable.isTamed() && super.shouldExecute();
    }
}