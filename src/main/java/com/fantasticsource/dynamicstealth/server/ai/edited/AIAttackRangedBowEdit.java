package com.fantasticsource.dynamicstealth.server.ai.edited;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;

public class AIAttackRangedBowEdit<T extends EntityMob & IRangedAttackMob> extends EntityAIAttackRangedBow
{
    private int timer = -1;
    private int seeTime = 0;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    private Path path = null;

    public AIAttackRangedBowEdit(EntityAIAttackRangedBow oldAI)
    {
        super(oldAI.entity, oldAI.moveSpeedAmp, oldAI.attackCooldown, oldAI.maxAttackDistance);

        setMutexBits(3);
    }

    @Override
    public void setAttackCooldown(int ticks)
    {
        attackCooldown = ticks;
    }

    @Override
    public boolean shouldExecute()
    {
        return isBowInMainhand() && AITargetEdit.isSuitableTarget(entity, entity.getAttackTarget());
    }

    @Override
    protected boolean isBowInMainhand()
    {
        return !entity.getHeldItemMainhand().isEmpty() && entity.getHeldItemMainhand().getItem() == Items.BOW;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return shouldExecute();
    }

    @Override
    public void startExecuting()
    {
        ((T) entity).setSwingingArms(true);
    }

    @Override
    public void resetTask()
    {
        ((T) entity).setSwingingArms(false);
        seeTime = 0;
        timer = -1;
        entity.resetActiveHand();

        if (path != null && path.equals(entity.getNavigator().getPath())) entity.getNavigator().clearPath();
        path = null;
    }

    @Override
    public void updateTask()
    {
        EntityLivingBase target = entity.getAttackTarget();
        if (target == null) return;

        double distSquared = entity.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);

        seeTime++;

        if (distSquared <= maxAttackDistance && seeTime >= 20)
        {
            if (path != null && path.equals(entity.getNavigator().getPath())) entity.getNavigator().clearPath();
            path = null;
            ++strafingTime;
        }
        else
        {
            path = entity.getNavigator().getPathToEntityLiving(target);
            entity.getNavigator().setPath(path, moveSpeedAmp);
            strafingTime = -1;
        }

        if (strafingTime > -1)
        {
            if (strafingTime >= 20)
            {
                if (entity.getRNG().nextFloat() < 0.3) strafingClockwise = !strafingClockwise;
                if (entity.getRNG().nextFloat() < 0.3) strafingBackwards = !strafingBackwards;
                strafingTime = 0;
            }

            if (distSquared > maxAttackDistance * 0.75)
            {
                strafingBackwards = false;
            }
            else if (distSquared < maxAttackDistance * 0.25)
            {
                strafingBackwards = true;
            }

            entity.getMoveHelper().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
            entity.faceEntity(target, 30, 30);
        }
        else
        {
            entity.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        }

        if (entity.isHandActive())
        {
            int itemMaxUseDuration = entity.getItemInUseMaxCount();

            if (itemMaxUseDuration >= 20)
            {
                entity.resetActiveHand();
                ((T) entity).attackEntityWithRangedAttack(target, ItemBow.getArrowVelocity(itemMaxUseDuration));
                timer = attackCooldown;
            }
        }
        else if (--timer <= 0)
        {
            entity.setActiveHand(EnumHand.MAIN_HAND);
        }
    }
}