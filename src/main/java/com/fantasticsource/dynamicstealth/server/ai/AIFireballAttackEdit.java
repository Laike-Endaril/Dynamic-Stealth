package com.fantasticsource.dynamicstealth.server.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class AIFireballAttackEdit extends EntityAIBase
{
    private final EntityBlaze blaze;
    private int attackStep;
    private int attackTime;
    private EntityLivingBase target;

    public AIFireballAttackEdit(EntityBlaze blazeIn)
    {
        blaze = blazeIn;
        setMutexBits(3);
    }

    public boolean shouldExecute()
    {
        target = blaze.getAttackTarget();
        return target != null && target.isEntityAlive();
    }

    public boolean shouldContinueExecuting()
    {
        return shouldExecute();
    }

    public void startExecuting()
    {
        attackStep = 0;
    }

    public void resetTask()
    {
        blaze.setOnFire(false);
    }

    public void updateTask()
    {
        --attackTime;
        double d0 = blaze.getDistanceSq(target);

        if (d0 < 4.0D)
        {
            if (attackTime <= 0)
            {
                attackTime = 20;
                blaze.attackEntityAsMob(target);
            }

            blaze.getMoveHelper().setMoveTo(target.posX, target.posY, target.posZ, 1.0D);
        }
        else if (d0 < getFollowDistance() * getFollowDistance())
        {
            double d1 = target.posX - blaze.posX;
            double d2 = target.getEntityBoundingBox().minY + (double)(target.height / 2.0F) - (blaze.posY + (double)(blaze.height / 2.0F));
            double d3 = target.posZ - blaze.posZ;

            if (attackTime <= 0)
            {
                ++attackStep;

                if (attackStep == 1)
                {
                    attackTime = 60;
                    blaze.setOnFire(true);
                }
                else if (attackStep <= 4)
                {
                    attackTime = 6;
                }
                else
                {
                    attackTime = 100;
                    attackStep = 0;
                    blaze.setOnFire(false);
                }

                if (attackStep > 1)
                {
                    float f = MathHelper.sqrt(MathHelper.sqrt(d0)) * 0.5F;
                    blaze.world.playEvent(null, 1018, new BlockPos((int)blaze.posX, (int)blaze.posY, (int)blaze.posZ), 0);

                    for (int i = 0; i < 1; ++i)
                    {
                        EntitySmallFireball entitysmallfireball = new EntitySmallFireball(blaze.world, blaze, d1 + blaze.getRNG().nextGaussian() * (double)f, d2, d3 + blaze.getRNG().nextGaussian() * (double)f);
                        entitysmallfireball.posY = blaze.posY + (double)(blaze.height / 2.0F) + 0.5D;
                        blaze.world.spawnEntity(entitysmallfireball);
                    }
                }
            }

            blaze.getLookHelper().setLookPositionWithEntity(target, 10.0F, 10.0F);
        }
        else
        {
            blaze.getNavigator().clearPath();
            blaze.getMoveHelper().setMoveTo(target.posX, target.posY, target.posZ, 1.0D);
        }

        super.updateTask();
    }

    private double getFollowDistance()
    {
        return blaze.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
    }
}
