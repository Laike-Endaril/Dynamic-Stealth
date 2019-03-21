package com.fantasticsource.dynamicstealth.server.ai.edited;

import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.passive.EntityLlama;

public class AILlamaHurtByTargetEdit extends AIHurtByTargetEdit
{
    public EntityLlama llama;

    public AILlamaHurtByTargetEdit(EntityAIHurtByTarget oldAI)
    {
        super(oldAI);
        llama = (EntityLlama) attacker;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if (llama.didSpit)
        {
            llama.didSpit = false;
            return false;
        }

        return super.shouldContinueExecuting();
    }
}
