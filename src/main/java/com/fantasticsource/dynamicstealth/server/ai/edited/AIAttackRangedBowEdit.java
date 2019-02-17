package com.fantasticsource.dynamicstealth.server.ai.edited;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;

public class AIAttackRangedBowEdit<T extends EntityMob & IRangedAttackMob> extends EntityAIAttackRangedBow
{
    private static Field entityField, moveSpeedAmpField, attackCooldownField, maxAttackDistanceField;

    static
    {
        try
        {
            entityField = ReflectionTool.getField(EntityAIAttackRangedBow.class, "field_188499_a", "entity");
            moveSpeedAmpField = ReflectionTool.getField(EntityAIAttackRangedBow.class, "field_188500_b", "moveSpeedAmp");
            attackCooldownField = ReflectionTool.getField(EntityAIAttackRangedBow.class, "field_188501_c", "attackCooldown");
            maxAttackDistanceField = ReflectionTool.getField(EntityAIAttackRangedBow.class, "field_188502_d", "maxAttackDistance");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(139, false);
        }
    }


    private T attacker;
    private double moveSpeed;
    private int cooldown;
    private float maxAttackDistanceSquared;
    private int timer = -1;
    private int seeTime = 0;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;
    private Path path = null;

    public AIAttackRangedBowEdit(EntityAIAttackRangedBow oldAI) throws IllegalAccessException
    {
        super(null, 0, 0, 0);

        attacker = (T) entityField.get(oldAI);
        moveSpeed = (double) moveSpeedAmpField.get(oldAI);
        cooldown = (int) attackCooldownField.get(oldAI);
        maxAttackDistanceSquared = (float) maxAttackDistanceField.get(oldAI);

        setMutexBits(3);
    }

    @Override
    public void setAttackCooldown(int ticks)
    {
        cooldown = ticks;
    }

    @Override
    public boolean shouldExecute()
    {
        return isBowInMainhand() && AITargetEdit.isSuitableTarget(attacker, attacker.getAttackTarget());
    }

    @Override
    protected boolean isBowInMainhand()
    {
        return !attacker.getHeldItemMainhand().isEmpty() && attacker.getHeldItemMainhand().getItem() == Items.BOW;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return shouldExecute();
    }

    @Override
    public void startExecuting()
    {
        attacker.setSwingingArms(true);
    }

    @Override
    public void resetTask()
    {
        attacker.setSwingingArms(false);
        seeTime = 0;
        timer = -1;
        attacker.resetActiveHand();

        if (path != null && path.equals(attacker.getNavigator().getPath())) attacker.getNavigator().clearPath();
        path = null;
    }

    @Override
    public void updateTask()
    {
        EntityLivingBase target = attacker.getAttackTarget();
        if (target == null) return;

        double distSquared = attacker.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);

        seeTime++;

        if (distSquared <= (double) maxAttackDistanceSquared && seeTime >= 20)
        {
            if (path != null && path.equals(attacker.getNavigator().getPath())) attacker.getNavigator().clearPath();
            path = null;
            ++strafingTime;
        }
        else
        {
            path = attacker.getNavigator().getPathToEntityLiving(target);
            attacker.getNavigator().setPath(path, moveSpeed);
            strafingTime = -1;
        }

        if (strafingTime > -1)
        {
            if (strafingTime >= 20)
            {
                if (attacker.getRNG().nextFloat() < 0.3) strafingClockwise = !strafingClockwise;
                if (attacker.getRNG().nextFloat() < 0.3) strafingBackwards = !strafingBackwards;
                strafingTime = 0;
            }

            if (distSquared > maxAttackDistanceSquared * 0.75)
            {
                strafingBackwards = false;
            }
            else if (distSquared < maxAttackDistanceSquared * 0.25)
            {
                strafingBackwards = true;
            }

            attacker.getMoveHelper().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
            attacker.faceEntity(target, 30, 30);
        }
        else
        {
            attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
        }

        if (attacker.isHandActive())
        {
            int itemMaxUseDuration = attacker.getItemInUseMaxCount();

            if (itemMaxUseDuration >= 20)
            {
                attacker.resetActiveHand();
                attacker.attackEntityWithRangedAttack(target, ItemBow.getArrowVelocity(itemMaxUseDuration));
                timer = cooldown;
            }
        }
        else if (--timer <= 0)
        {
            attacker.setActiveHand(EnumHand.MAIN_HAND);
        }
    }
}