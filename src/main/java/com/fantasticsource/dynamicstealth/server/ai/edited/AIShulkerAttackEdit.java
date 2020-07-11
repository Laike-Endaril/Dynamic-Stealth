package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.dynamicstealth.compat.Compat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.EnumDifficulty;

import java.util.Random;

public class AIShulkerAttackEdit extends EntityAIBase
{
    private int attackTime;
    private EntityShulker shulker;
    private EntityLivingBase target;

    public AIShulkerAttackEdit(EntityShulker shulker)
    {
        this.shulker = shulker;

        setMutexBits(3);
    }

    public boolean shouldExecute()
    {
        target = shulker.getAttackTarget();

        if (target != null && target.isEntityAlive())
        {
            return shulker.world.getDifficulty() != EnumDifficulty.PEACEFUL;
        }
        else return false;
    }

    public void startExecuting()
    {
        attackTime = 20;
        shulker.updateArmorModifier(100);
    }

    public void resetTask()
    {
        shulker.updateArmorModifier(0);
    }

    public void updateTask()
    {
        if (shulker.world.getDifficulty() != EnumDifficulty.PEACEFUL)
        {
            --attackTime;
            shulker.getLookHelper().setLookPositionWithEntity(target, 180, 180);
            double d0 = shulker.getDistanceSq(target);

            if (d0 < 400)
            {
                if (attackTime <= 0)
                {
                    Random rand = shulker.getRNG();
                    attackTime = 20 + rand.nextInt(10) * 10;
                    EntityShulkerBullet entityshulkerbullet = new EntityShulkerBullet(shulker.world, shulker, target, shulker.getAttachmentFacing().getAxis());
                    shulker.world.spawnEntity(entityshulkerbullet);
                    shulker.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1);
                }
            }
            else Compat.clearAttackTargetAndCancelBadTasks(shulker);

            super.updateTask();
        }
    }
}
