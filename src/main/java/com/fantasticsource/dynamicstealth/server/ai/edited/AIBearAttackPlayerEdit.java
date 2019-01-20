package com.fantasticsource.dynamicstealth.server.ai.edited;

import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.player.EntityPlayer;

public class AIBearAttackPlayerEdit extends AINearestAttackableTargetEdit<EntityPlayer>
{
    public AIBearAttackPlayerEdit(EntityAINearestAttackableTarget<EntityPlayer> oldAI) throws IllegalAccessException
    {
        super(oldAI);
    }

    @Override
    public boolean shouldExecute()
    {
        return !attacker.isChild() && cubNearby() && super.shouldExecute();
    }

    @Override
    protected double getFollowDistance()
    {
        return super.getFollowDistance() * 0.5;
    }

    private boolean cubNearby()
    {
        for (EntityPolarBear otherBear : attacker.world.getEntitiesWithinAABB(EntityPolarBear.class, attacker.getEntityBoundingBox().grow(8, 4, 8)))
        {
            if (otherBear.isChild()) return true;
        }
        return false;
    }
}
